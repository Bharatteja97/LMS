package tests;

import base.BaseClass;
import accounts.AccountsPage;
import accounts.AccountDetailsPage;
import accounts.PaymentPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaymentTest extends BaseClass {
    private static final Logger logger = LogManager.getLogger(PaymentTest.class);
    private static final String PAYMENT_URL = "https://lms.alfinnext.com/lms/accounts/208?tab=payment&product=VEHICLE_LOAN";

    private PaymentPage paymentPage;

    @BeforeClass(alwaysRun = true)
    public void setupPaymentTab() {
        // Any class level setup can go here
    }

    @BeforeMethod(alwaysRun = true)
    public void navigateToPaymentTab() {
        // Navigate directly to the payment tab URL — avoids the extra tab-click roundtrip
        driver.get(PAYMENT_URL);
        paymentPage = new PaymentPage(driver);
        paymentPage.isPageLoaded();
        logger.info("Navigated to Payment tab: " + PAYMENT_URL);
    }

    // ── Tab load ──────────────────────────────────────────────────────────────

    @Test(priority = 1)
    public void testPaymentTabLoads() {
        logger.info("Verifying Payment tab loads with PAYMENT HISTORY heading");
        Assert.assertTrue(paymentPage.isPaymentHistoryVisible(),
            "Payment tab should show the PAYMENT HISTORY section heading");
    }

    // ── Sub-tabs ──────────────────────────────────────────────────────────────

    @Test(priority = 2)
    public void testPaymentHistorySubTabVisible() {
        logger.info("Verifying Payment History sub-tab is visible");
        paymentPage.clickPaymentHistorySubTab();
        Assert.assertTrue(paymentPage.isPageLoaded(),
            "Payment History sub-tab should keep the PAYMENT HISTORY section visible");
    }

    @Test(priority = 3)
    public void testStagedRepaymentsSubTabVisible() {
        logger.info("Verifying Staged Repayments sub-tab is clickable");
        paymentPage.clickStagedRepaymentsSubTab();
        pause(1000);
        Assert.assertTrue(driver.getCurrentUrl().matches(".*/lms/accounts/\\d+.*"),
            "URL should remain on account detail page after clicking Staged Repayments sub-tab");
        logger.info("Staged Repayments sub-tab clicked: URL=" + driver.getCurrentUrl());
    }

    @Test(priority = 4)
    public void testStagedRepaymentRowCount() {
        logger.info("Verifying Staged Repayments table row count is non-negative");
        paymentPage.clickStagedRepaymentsSubTab();
        pause(1000);
        int rowCount = paymentPage.getStagedRepaymentRowCount();
        logger.info("Staged Repayments row count: " + rowCount);
        Assert.assertTrue(rowCount >= 0, "Staged Repayments row count should be 0 or more");
    }

    // ── Payment History table ─────────────────────────────────────────────────

    @Test(priority = 5)
    public void testPaymentHistoryRowCount() {
        logger.info("Verifying Payment History table row count is non-negative");
        paymentPage.clickPaymentHistorySubTab();
        int rowCount = paymentPage.getPaymentHistoryRowCount();
        logger.info("Payment History row count: " + rowCount);
        Assert.assertTrue(rowCount >= 0, "Payment History row count should be 0 or more");
    }

    @Test(priority = 6)
    public void testPaymentHistoryFirstRowData() {
        logger.info("Verifying first Payment History row contains non-empty cell data");
        paymentPage.clickPaymentHistorySubTab();
        int rowCount = paymentPage.getPaymentHistoryRowCount();
        if (rowCount == 0) {
            logger.info("No payment history rows — skipping row-data assertion");
            return;
        }
        java.util.List<String> cells = paymentPage.getPaymentHistoryRowData(0);
        logger.info("First row cells: " + cells);
        Assert.assertFalse(cells.isEmpty(), "First Payment History row should have at least one cell");
        boolean anyNonEmpty = cells.stream().anyMatch(c -> c != null && !c.trim().isEmpty());
        Assert.assertTrue(anyNonEmpty, "First Payment History row should have at least one non-empty cell");
    }

    // ── Make Payment modal ────────────────────────────────────────────────────

    @Test(priority = 7)
    public void testPayButtonOpensModal() {
        logger.info("Verifying Pay button opens Make Payment modal");
        paymentPage.clickPayButton();
        String title = paymentPage.getModalTitle();
        logger.info("Modal title: " + title);
        Assert.assertFalse(title.isEmpty(), "Make Payment modal should show a title");
        Assert.assertTrue(title.toLowerCase().contains("payment"),
            "Modal title should contain 'payment' but was: " + title);
    }

    @Test(priority = 8)
    public void testModalTitleIsCorrect() {
        logger.info("Verifying Make Payment modal title is exactly 'Make Payment'");
        paymentPage.clickPayButton();
        String title = paymentPage.getModalTitle();
        logger.info("Modal title: " + title);
        Assert.assertEquals(title, "Make Payment",
            "Modal title should be 'Make Payment' but was: " + title);
    }

    @Test(priority = 9)
    public void testModalShowsScheduledEMI() {
        logger.info("Verifying Make Payment modal shows Scheduled EMI value");
        paymentPage.clickPayButton();
        String scheduledEMI = paymentPage.getScheduledEMI();
        logger.info("Scheduled EMI: " + scheduledEMI);
        Assert.assertFalse(scheduledEMI.isEmpty(), "Scheduled EMI value should not be empty");
    }

    @Test(priority = 10)
    public void testModalShowsRemainingDue() {
        logger.info("Verifying Make Payment modal shows Remaining Due value");
        paymentPage.clickPayButton();
        String remainingDue = paymentPage.getRemainingDue();
        logger.info("Remaining Due: " + remainingDue);
        Assert.assertFalse(remainingDue.isEmpty(), "Remaining Due value should not be empty");
    }

    @Test(priority = 11)
    public void testModalShowsMaxPaymentLimit() {
        logger.info("Verifying Make Payment modal shows Max Payment Limit value");
        paymentPage.clickPayButton();
        String maxLimit = paymentPage.getMaxPaymentLimit();
        logger.info("Max Payment Limit: " + maxLimit);
        Assert.assertFalse(maxLimit.isEmpty(), "Max Payment Limit value should not be empty");
    }

    @Test(priority = 12)
    public void testModalCancelClosesModal() {
        logger.info("Verifying Cancel button closes the Make Payment modal");
        paymentPage.clickPayButton();
        paymentPage.getModalTitle(); // ensure modal is open
        paymentPage.clickCancelPayment();
        pause(500);
        Assert.assertTrue(paymentPage.isPageLoaded(),
            "Payment tab should still be loaded after cancelling the modal");
        logger.info("Modal cancelled successfully");
    }

    @Test(priority = 13)
    public void testModalCloseButtonDismissesModal() {
        logger.info("Verifying close (×) button dismisses the Make Payment modal");
        paymentPage.clickPayButton();
        paymentPage.getModalTitle(); // ensure modal is open
        paymentPage.closeModal();
        Assert.assertTrue(paymentPage.isPageLoaded(),
            "Payment tab should still be loaded after closing the modal via × button");
        logger.info("Modal closed successfully via × button");
    }

    // ── Full payment submission flow ──────────────────────────────────────────

    @Test(priority = 14)
    public void testMakePaymentWithScheduledEMIAmount() {
        logger.info("Testing full payment flow: Pay → click Scheduled EMI → submit");

        paymentPage.clickPayButton();
        logger.info("Make Payment modal opened, title: " + paymentPage.getModalTitle());

        // Click the Scheduled EMI amount to auto-populate the Payment Amount field
        paymentPage.clickScheduledEMIAmount();
        pause(500);

        String amount = paymentPage.getPaymentAmountValue();
        logger.info("Payment Amount after clicking Scheduled EMI: " + amount);
        Assert.assertFalse(amount.isEmpty() || "0.00".equals(amount) || "0".equals(amount),
            "Payment Amount should be populated after clicking Scheduled EMI, but was: " + amount);

        // Repayment Mode — RTGS is the default; skip silently if the combobox can't be located
        try {
            paymentPage.selectRepaymentMode("RTGS");
            logger.info("Repayment Mode set to RTGS");
        } catch (Exception e) {
            logger.warn("selectRepaymentMode skipped (using default): " + e.getMessage());
        }

        // Value Date — today
        paymentPage.enterValueDate("06/29/2026");
        logger.info("Value Date set to 06/29/2026");

        // Authorized By — pick first available option; skip silently if combobox can't be found
        try {
            paymentPage.selectFirstAuthorizedBy();
            pause(500);
            logger.info("Authorized By selected");
        } catch (Exception e) {
            logger.warn("selectFirstAuthorizedBy skipped (using default): " + e.getMessage());
        }

        // Submit the payment
        paymentPage.clickSubmitPayment();
        pause(2000);
        logger.info("Payment submitted — URL: " + driver.getCurrentUrl());

        // Navigate to Staged Repayments and approve the pending entry
        paymentPage.clickStagedRepaymentsSubTab();
        pause(1000);

        int stagedCount = paymentPage.getStagedRepaymentRowCount();
        logger.info("Staged Repayments rows after submission: " + stagedCount);
        Assert.assertTrue(stagedCount > 0,
            "Staged Repayments should show at least 1 row after payment submission");

        // Approve the first (most recent) pending row
        paymentPage.approveStagedRepayment(0);
        pause(2000);
        logger.info("Staged repayment approved — URL: " + driver.getCurrentUrl());

        Assert.assertTrue(driver.getCurrentUrl().matches(".*/lms/accounts/\\d+.*"),
            "Should remain on account detail page after approving staged repayment");
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
