package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProcessingFeePage;
/**
 * TestNG test for the Processing Fee payment flow.
 *
 *  APPROACH (LMS-direct):
 *    1. Go directly to the LMS Processing Fee tab (application 465).
 *    2. Read the rzp.io link from the "Payment Link" input field.
 *    3. Clicks the "open in new tab" icon — OR — open via JS window.open().
 *    4. Complete the Razorpay checkout in the new tab.
 *
 * Run:
 *   mvn test -Dtest=ProcessingFeeTest
 */
public class ProcessingFeeTest extends BaseClass {

    // ─── Razorpay test contact details ────────────────────────────────────
    private static final String TEST_MOBILE = "9704283625";
    private static final String TEST_EMAIL  = "bharatteja09@gmail.com";

    private String getApplicationId() {
        String appId = System.getProperty("APPLICATION_ID");
        if (appId == null) {
            throw new IllegalStateException("APPLICATION_ID is not set! Run UnderWritingTest first.");
        }
        return appId;
    }

    @Test
    public void testProcessingFeePayment() throws Exception {
        String appId = getApplicationId();
        String opsDetailUrl = "https://lms.alfinnext.com/vehicle/operations/" + appId + "?product=VEHICLE_LOAN";
        
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating to Operations Detail page: " + opsDetailUrl);
        System.out.println("═══════════════════════════════════════════════════════════");
        driver.get(opsDetailUrl);

        ProcessingFeePage feePage = new ProcessingFeePage(driver);
        feePage.payFromLmsPage(appId, TEST_MOBILE, TEST_EMAIL);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Processing fee payment completed (Approach 2 - LMS Direct).");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}
