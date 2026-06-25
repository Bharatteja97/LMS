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

    // ─── LMS application details ─────────────────────────────────────────
    private static final String APPLICATION_ID = "465";

    // =========================================================================
    // APPROACH 2 – Copy link directly from the LMS page (no Gmail needed)
    // =========================================================================

    /**
     * LMS-Direct approach:
     *  - Goes to LMS Operations → application 465 → Processing Fee tab.
     *  - Reads the rzp.io link from the "Payment Link" input field.
     *  - Clicks the external-link icon to open the link in a new tab
     *    (fallback: opens it via JS window.open if icon click doesn't work).
     *  - Completes the Razorpay test checkout: Netbanking → Bank of Baroda → Success.
     */
    @Test(priority = 3)
    public void testPayFromLmsPageDirect() throws Exception {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] LMS-Direct: Reading payment link from LMS tab...");
        System.out.println("[INFO] Application ID: " + APPLICATION_ID);
        System.out.println("═══════════════════════════════════════════════════════════");

        ProcessingFeePage feePage = new ProcessingFeePage(driver);
        feePage.payFromLmsPage(APPLICATION_ID, TEST_MOBILE, TEST_EMAIL);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Processing fee payment completed (Approach 2 - LMS Direct).");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}
