package tests;

import base.BaseClass;
import org.testng.annotations.Test;
import operations.OperationsPage;
import pages.DealerLetterPage;

public class DealerLetterTest extends BaseClass {

    private String getApplicationId() {
        String appId = System.getProperty("APPLICATION_ID");
        if (appId == null) {
            throw new IllegalStateException("APPLICATION_ID is not set! Run UnderWritingTest first.");
        }
        return appId;
    }

    private static final String DEALER_EMAIL = "bharatteja09@gmail.com";

    @Test
    public void testDispatchMailToDealer() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating to Operations application to dispatch dealer mail...");
        System.out.println("═══════════════════════════════════════════════════════════");

        OperationsPage opPage = new OperationsPage(driver);
        opPage.navigateToDetail(getApplicationId());
        
        DealerLetterPage dealerPage = new DealerLetterPage(driver);
        dealerPage.clickDisbursementDetailsTab();
        dealerPage.clickDispatchMailToDealer();
        
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[INFO] Filling email in the modal...");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        dealerPage.enterEmailAndDispatch(DEALER_EMAIL);
        
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Dealer mail dispatch completed successfully.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}
