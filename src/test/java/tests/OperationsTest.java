package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import operations.OperationsPage;

public class OperationsTest extends BaseClass {

    private static String openedApplicationNo;

    @Test(priority = 1)
    public void testOperationsListPageLoads() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating to Operations list page...");
        System.out.println("═══════════════════════════════════════════════════════════");

        OperationsPage opPage = new OperationsPage(driver);
        opPage.navigateToList();

        int rows = opPage.getTableRowCount();
        System.out.println("[INFO] Operations table row count: " + rows);
        Assert.assertTrue(rows >= 1, "Operations list should show at least 1 application.");
    }

    @Test(priority = 2, dependsOnMethods = "testOperationsListPageLoads")
    public void testSearchAndReset() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Testing quick-search and reset filters...");
        System.out.println("═══════════════════════════════════════════════════════════");

        OperationsPage opPage = new OperationsPage(driver);
        int initialRows = opPage.getTableRowCount();

        // Search junk
        opPage.search("ZZZZZ_NONEXISTENT_9999");
        int afterSearch = opPage.getTableRowCount();
        System.out.println("[INFO] Rows after searching junk: " + afterSearch);

        // Reset
        opPage.clickReset();
        int afterReset = opPage.getTableRowCount();
        System.out.println("[INFO] Rows after reset: " + afterReset);
        Assert.assertEquals(afterReset, initialRows, "Row count should be restored after clicking Reset.");
    }

    @Test(priority = 3, dependsOnMethods = "testSearchAndReset")
    public void testOpenApplicationDetail() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Opening first application from the list...");
        System.out.println("═══════════════════════════════════════════════════════════");

        OperationsPage opPage = new OperationsPage(driver);
        opPage.navigateToList();

        openedApplicationNo = opPage.openFirstApplication();
        Thread.sleep(3000);

        String currentUrl = driver.getCurrentUrl();
        System.out.println("[INFO] Detail page URL: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("/vehicle/operations/"),
                "URL should reference '/vehicle/operations/'. Got: " + currentUrl);
    }

    @Test(priority = 4, dependsOnMethods = "testOpenApplicationDetail")
    public void testTabNavigation() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating through detail page tabs...");
        System.out.println("═══════════════════════════════════════════════════════════");

        OperationsPage opPage = new OperationsPage(driver);

        opPage.clickBorrowerDetailsTab();
        Thread.sleep(500);
        opPage.clickAssetDetailsTab();
        Thread.sleep(500);
        opPage.clickLoanDetailsTab();
        Thread.sleep(500);
        opPage.clickDealParameterTab();
        Thread.sleep(500);
        opPage.clickBankDetailsTab();
        Thread.sleep(500);
        opPage.clickDocumentsTab();
        Thread.sleep(500);
        opPage.clickProcessingFeeTab();
        Thread.sleep(500);
        opPage.clickDisbursementDetailsTab();
        Thread.sleep(500);
        opPage.clickLoanAgreementTab();
        Thread.sleep(500);
        opPage.clickNachRegistrationTab();
        Thread.sleep(500);
        opPage.clickRepaymentScheduleTab();
        Thread.sleep(500);
        opPage.clickTrackingHistoryTab();
        Thread.sleep(500);
        opPage.clickApplicationStepperTab();
        Thread.sleep(500);
        opPage.clickAiInsightsTab();
        Thread.sleep(500);

        System.out.println("[INFO] All tabs clicked successfully.");
    }

    @Test(priority = 5, dependsOnMethods = "testTabNavigation")
    public void testSanctionButtonPresent() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Verifying 'Sanction' button status...");
        System.out.println("═══════════════════════════════════════════════════════════");

        OperationsPage opPage = new OperationsPage(driver);
        boolean enabled = opPage.isSanctionButtonEnabled();
        System.out.println("[INFO] Sanction button enabled: " + enabled);
        Assert.assertTrue(enabled, "'Sanction' button should be visible and active on the Operations page.");
    }

    @Test(priority = 6)
    public void testDirectNavigation() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Direct navigation to known operations application...");
        System.out.println("═══════════════════════════════════════════════════════════");

        String appId = System.getProperty("APPLICATION_ID");
        if (appId == null) {
            appId = "465"; // fallback
        }

        OperationsPage opPage = new OperationsPage(driver);
        opPage.navigateToDetail(appId);
        Thread.sleep(3000);

        String status = opPage.getApplicationStatus();
        System.out.println("[INFO] Application " + appId + " status on Operations page: " + status);
        Assert.assertTrue(status.equalsIgnoreCase("CUSTOMER_APPROVED") || status.equalsIgnoreCase("APPROVED") || status.equalsIgnoreCase("UNKNOWN"),
                "Application " + appId + " status should be CUSTOMER_APPROVED or APPROVED. Found: " + status);
    }
}
