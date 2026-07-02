package tests;

import base.BaseClass;
import accounts.AccountDetailsPage;
import accounts.BalanceTransferPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BalanceTransferTest extends BaseClass {
    private static final Logger logger = LogManager.getLogger(BalanceTransferTest.class);

    // Direct URL to the Balance Transfer tab for account 210
    private static final String BT_URL =
        "https://lms.alfinnext.com/lms/accounts/210?tab=balance_transfer&product=VEHICLE_LOAN";

    private BalanceTransferPage btPage;

    @BeforeClass(alwaysRun = true)
    public void openBalanceTransferTab() {
        driver.get(BT_URL);
        btPage = new BalanceTransferPage(driver);
        btPage.isPageLoaded();
        logger.info("Navigated to Balance Transfer tab: " + BT_URL);
    }

    @BeforeMethod(alwaysRun = true)
    public void resetTab() {
        try {
            driver.get(BT_URL);
            btPage = new BalanceTransferPage(driver);
            btPage.isPageLoaded();
        } catch (org.openqa.selenium.NoSuchSessionException e) {
            logger.warn("Browser session lost before test — skipping");
            throw e;
        }
    }

    // ── Test 1: Section loads with correct heading ────────────────────────────

    @Test(priority = 1)
    public void testBalanceTransferTabLoads() {
        logger.info("Verifying Balance Transfer Settlement section loads");

        Assert.assertTrue(btPage.isPageLoaded(),
            "Balance Transfer Settlement section should be visible");

        String heading = btPage.getSectionHeading();
        logger.info("Section heading: " + heading);
        Assert.assertTrue(heading.contains("Balance Transfer"),
            "Heading should contain 'Balance Transfer'");
    }

    // ── Test 2: Info notice mentions outstanding liability ────────────────────

    @Test(priority = 2)
    public void testInfoNoticeText() {
        logger.info("Verifying info notice text");

        String notice = btPage.getInfoNoticeText();
        logger.info("Info notice: " + notice);

        Assert.assertTrue(notice.contains("outstanding liability") || notice.contains("outstanding"),
            "Notice should mention outstanding liability");
        Assert.assertTrue(notice.contains("BALANCE_TRANSFERRED"),
            "Notice should mention BALANCE_TRANSFERRED status");
    }

    // ── Test 3: Form fields are present and accept input ─────────────────────

    @Test(priority = 3)
    public void testFormFieldsAcceptInput() {
        logger.info("Verifying form fields accept input");

        btPage.enterNewLenderName("HDFC Bank");
        logger.info("Entered New Lender Name: HDFC Bank");
        Assert.assertEquals(btPage.getNewLenderNameValue(), "HDFC Bank",
            "New Lender Name field should hold entered value");

        btPage.enterNocReference("NOC-2026-001");
        logger.info("Entered NOC Reference: NOC-2026-001");

        btPage.enterReasonForTransfer("Lower interest rate offered by new lender");
        logger.info("Entered Reason for Transfer");
        Assert.assertFalse(btPage.getReasonForTransferValue().isEmpty(),
            "Reason for Transfer should not be empty after input");
    }

    // ── Test 4: Reset clears the form ────────────────────────────────────────

    @Test(priority = 4)
    public void testResetClearsForm() {
        logger.info("Verifying Reset button clears the form");

        btPage.enterNewLenderName("SBI");
        btPage.enterReasonForTransfer("Test reason");

        btPage.clickReset();
        logger.info("Clicked Reset");

        pause(1000);
        String lenderValue = btPage.getNewLenderNameValue();
        logger.info("New Lender Name after reset: '" + lenderValue + "'");
        Assert.assertTrue(lenderValue == null || lenderValue.isEmpty(),
            "New Lender Name should be cleared after Reset");
    }

    // ── Test 5: Full flow — fill form and click Confirm Transfer ─────────────

    @Test(priority = 5)
    public void testConfirmTransferFullFlow() {
        logger.info("Verifying full Balance Transfer flow: fill form → Confirm Transfer");

        // Step 1: Fill required field — New Lender Name
        String lenderName = "HDFC Bank";
        btPage.enterNewLenderName(lenderName);
        logger.info("New Lender Name: " + lenderName);

        // Step 2: Fill optional NOC Reference
        btPage.enterNocReference("NOC-20260630-001");
        logger.info("NOC Reference: NOC-20260630-001");

        // Step 3: Fill Reason for Transfer
        btPage.enterReasonForTransfer("Lower interest rate, better repayment terms");
        logger.info("Reason for Transfer entered");

        // Step 4: Confirm Transfer button should be enabled
        Assert.assertTrue(btPage.isConfirmTransferEnabled(),
            "Confirm Transfer button should be enabled after filling required fields");

        // Step 5: Click Confirm Transfer
        btPage.clickConfirmTransfer();
        logger.info("Clicked Confirm Transfer");

        pause(3000);

        // Step 6: Verify post-transfer state — page stays on account context
        String currentUrl = driver.getCurrentUrl();
        logger.info("Post-transfer URL: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("/lms/accounts/"),
            "Should remain within account details context after transfer");
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
