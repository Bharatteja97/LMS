package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import operations.OperationsPage;
import pages.NachRegistrationPage;
import utils.ApiUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.restassured.response.Response;

public class NachRegistrationTest extends BaseClass {

    private String getApplicationId() {
        String appId = System.getProperty("APPLICATION_ID");
        if (appId == null) {
            throw new IllegalStateException("APPLICATION_ID is not set! Run previous tests first.");
        }
        return appId;
    }

    @Test
    public void testNachRegistration() {
        String appId = getApplicationId();

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating to Operations application to perform NACH Registration...");
        System.out.println("═══════════════════════════════════════════════════════════");

        OperationsPage opPage = new OperationsPage(driver);
        opPage.navigateToDetail(appId);

        NachRegistrationPage nachPage = new NachRegistrationPage(driver);
        nachPage.clickNachRegistrationTab();
        nachPage.clickCreateEMandate();
        nachPage.confirmCreateModal();

        System.out.println("[INFO] Getting Token from browser cookies...");
        org.openqa.selenium.Cookie tokenCookie = driver.manage().getCookieNamed("token");
        String token = tokenCookie != null ? tokenCookie.getValue() : null;
        
        if (token == null || token.isEmpty()) {
             System.out.println("[WARNING] Could not extract token from cookies. Using dummy token.");
             token = "YOUR_BEARER_TOKEN";
        }

        System.out.println("[INFO] Calling ApiUtils.getMandateDetails...");
        String responseBody = ApiUtils.getMandateDetails(Integer.parseInt(appId), token);
        System.out.println("[DEBUG] Mandate Details API Response: " + responseBody);
        
        String mandateId = null;
        Matcher m = Pattern.compile("\"mandateId\"\\s*:\\s*\"([A-Z0-9]{20,})\"").matcher(responseBody);
        if (m.find()) {
            mandateId = m.group(1);
            System.out.println("[INFO] Successfully extracted Mandate ID using ApiUtils: " + mandateId);
        } else {
            System.out.println("[WARNING] Falling back to mock mandate ID.");
            mandateId = "ENA_MOCK_" + appId;
        }
        
        System.out.println("[INFO] Using Mandate ID for webhook: " + mandateId);
        System.out.println("[INFO] Calling webhook callback API natively via Java...");
        
        String body = "{"
            + "\"id\":\"" + appId + "\","
            + "\"event\":\"apimndt.destaccept\","
            + "\"created_at\":1700000000000,"
            + "\"payload\":{"
            + "\"api_mandate\":{"
            + "\"id\":\"" + mandateId + "\","
            + "\"current_status\":\"REGISTERED\","
            + "\"umrn\":\"TEST_UMRN_001\","
            + "\"scheme_ref_number\":\"824587565245069\""
            + "}"
            + "}"
            + "}";

        Response response = ApiUtils.postMandateCallback(token, body);
        int statusCode = response.getStatusCode();
        
        System.out.println("[INFO] Webhook POST Response Status: " + statusCode);
        System.out.println("[INFO] Response Body:\n" + response.getBody().asPrettyString());
        
        Assert.assertEquals(statusCode, 200, "Webhook callback failed");
    }
}
