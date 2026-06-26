package tests;

import base.BaseClass;
import org.testng.annotations.Test;
import operations.OperationsPage;
import pages.DisbursementPage;

public class DisbursementTest extends BaseClass {

    private String getApplicationId() {
        String appId = System.getProperty("APPLICATION_ID");
        if (appId == null) {
            throw new IllegalStateException("APPLICATION_ID is not set! Run previous tests first.");
        }
        return appId;
    }

    @Test
    public void testSendToDisbursal() {
        String appId = getApplicationId();

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating to Operations application to Send to Disbursal...");
        System.out.println("═══════════════════════════════════════════════════════════");

        OperationsPage opPage = new OperationsPage(driver);
        opPage.navigateToDetail(appId);

        opPage.clickDisbursementDetailsTab();

        DisbursementPage disbPage = new DisbursementPage(driver);
        
        if (disbPage.isSendToDisbursalPresent()) {
            System.out.println("[INFO] 'Send to Disbursal' button found. Clicking it...");
            disbPage.clickSendToDisbursal();
            disbPage.confirmSendToDisbursalModal();
            System.out.println("[INFO] Successfully sent application " + appId + " to Disbursal.");
        } else {
            System.out.println("[INFO] 'Send to Disbursal' button not found. Assuming already sent.");
        }

        System.out.println("[INFO] Now clicking Disburse to complete the flow...");
        disbPage.clickDisburse();
        disbPage.fillDisburseModalAndConfirm("UTR987654321", "NEFT");

        System.out.println("[INFO] Successfully completed Disbursement for application " + appId + ".");
    }
}
