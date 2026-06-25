package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import origination.OriginationPage;
import origination.LeadDetailsPage;

public class LeadDetailsTest extends BaseClass {

    @Test(priority = 1)
    public void testVerifyLeadDetails() {
        // Navigate to the Origination table page
        driver.get("https://lms.alfinnext.com/vehicle/origination?product=VEHICLE_LOAN");

        OriginationPage originationPage = new OriginationPage(driver);
        
        // Search and open the newly created lead using its unique PAN
        String searchKey = System.getProperty("LEAD_PAN");
        if (searchKey == null) {
            searchKey = "ABCDE"; // Fallback for standalone run
        }
        System.out.println("[INFO] Searching for lead by PAN: " + searchKey);
        originationPage.searchAndOpenLead(searchKey);

        // Open LeadDetailsPage and verify details
        LeadDetailsPage leadDetailsPage = new LeadDetailsPage(driver);
        
        String leadId = leadDetailsPage.getLeadId();
        String leadStatus = leadDetailsPage.getLeadStatus();
        String loanType = leadDetailsPage.getLoanType();

        System.out.println("[INFO] Successfully opened Lead Details page.");
        System.out.println("  Fetched Lead ID: " + leadId);
        System.out.println("  Fetched Lead Status: " + leadStatus);
        System.out.println("  Fetched Loan Type: " + loanType);

        // Extract numeric ID from URL and save for subsequent tests
        String currentUrl = driver.getCurrentUrl();
        String numericId = currentUrl.split("/origination/")[1].split("\\?")[0];
        System.setProperty("LEAD_ID", numericId);
        System.out.println("[INFO] Saved LEAD_ID to System properties: " + numericId);

        // Assertions to verify correctness
        Assert.assertNotNull(leadId, "Lead ID should not be null");
        Assert.assertTrue(leadId.contains("LD"), "Lead ID should contain 'LD' identifier prefix");
        Assert.assertTrue(leadStatus.toLowerCase().contains("new"), "Lead Status should be 'NEW'");
        Assert.assertTrue(loanType.toLowerCase().contains("two wheeler"), "Loan Type should contain 'TWO WHEELER'");

        // 5. Click the "Re-Dispatch KYC Link" button to trigger sending the link to customer email
        leadDetailsPage.clickReDispatchKycLink();
        leadDetailsPage.clickConfirmDispatch();

        // 6. Wait briefly for the dispatch confirmation
        System.out.println("[INFO] KYC link dispatched. Waiting for confirmation...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[INFO] KYC dispatch completed. Email verification will be done in KycPageTest.");
    }
}
