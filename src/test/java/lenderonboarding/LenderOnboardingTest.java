package lenderonboarding;

import base.BaseClass;
import org.testng.annotations.Test;
import pages.DashboardPage;

public class LenderOnboardingTest extends BaseClass {

    @Test
    public void testLenderOnboardingFlow() {
        // Navigate directly to the given URL
        driver.get("https://lms.alfinnext.com/settings/client-onboard?product=VEHICLE_LOAN");
        
        // Initialize the page object after navigation
        LenderOnboardingPage onboardingPage = new LenderOnboardingPage(driver);
        
        // NOTE: Since login might be required before accessing this URL,
        // you may need to add login steps here first or handle session cookies.

        // -----------------------------------------
        // Step 1: Fill Company Information
        // -----------------------------------------
        // Client Code must be unique, so generate a fresh one for every run
        // to avoid "already exists" failures on repeated executions.
        String uniqueClientCode = "TC-" + System.currentTimeMillis();

        onboardingPage.fillCompanyInformation(
                "Test Company Ltd",
                "Test Company Private Limited",
                "TestBrand",
                uniqueClientCode);

        onboardingPage.fillRegistrationDetails(
                "01/01/2020",
                "U72900KA2020PTC123456",
                "ABCDE1234F",
                "29ABCDE1234F1Z5");

        onboardingPage.fillContactInformation(
                "https://testcompany.com",
                "official@testcompany.com",
                "support@testcompany.com",
                "+91-9876543210");

        onboardingPage.clickContinue(); // Navigate to next step

        // -----------------------------------------
        // Step 2: Office Address
        // -----------------------------------------
        onboardingPage.clickAddAddress(); // Opens the form/modal
        onboardingPage.fillOfficeAddress("123 Tech Park", "Floor 5", "560001", "Telangana", "Hyderabad");
        onboardingPage.clickSaveAddress(); // Saves the form
        onboardingPage.clickContinue(); // Navigate to next step

        // -----------------------------------------
        // Step 3: Branding
        // -----------------------------------------
        onboardingPage.uploadLogo("c:\\Users\\i-tech\\eclipse-workspace\\LMS\\src\\test\\resources\\q_logo.png");
        onboardingPage.uploadFavicon("c:\\Users\\i-tech\\eclipse-workspace\\LMS\\src\\test\\resources\\q_logo.png");
        
        onboardingPage.clickSubmit();
        
        // Optional: Wait a bit to ensure submission completes
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {}
    }
}
