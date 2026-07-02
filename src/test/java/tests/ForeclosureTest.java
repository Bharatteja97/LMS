package tests;

import base.BaseClass;
import accounts.AccountDetailsPage;
import accounts.ForeclosurePage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForeclosureTest extends BaseClass {
    private static final Logger logger = LogManager.getLogger(ForeclosureTest.class);
    private static final String DETAIL_PAGE_URL = "https://lms.alfinnext.com/lms/accounts/209?product=VEHICLE_LOAN";

    private AccountDetailsPage detailPage;

    @BeforeClass(alwaysRun = true)
    public void navigateToAccount() {
        driver.get(DETAIL_PAGE_URL);
        detailPage = new AccountDetailsPage(driver);
        detailPage.isPageLoaded();
        logger.info("Navigated to account detail page: " + DETAIL_PAGE_URL);
    }

    @BeforeMethod(alwaysRun = true)
    public void resetToDetailPage() {
        try {
            driver.get(DETAIL_PAGE_URL);
            detailPage = new AccountDetailsPage(driver);
            detailPage.isPageLoaded();
        } catch (org.openqa.selenium.NoSuchSessionException e) {
            logger.warn("Browser session lost — skipping reset, test will likely be skipped");
            throw e;
        }
    }

    // ── Test 1: Modal opens and title is correct ─────────────────────────────

    @Test(priority = 1)
    public void testOpenForeclosureModal() {
        logger.info("Verifying Foreclosure modal opens from action menu");
        ForeclosurePage foreclosurePage = detailPage.clickForeclosureOption();

        Assert.assertTrue(foreclosurePage.isModalOpened(), "Foreclosure modal should be visible");

        String title = foreclosurePage.getModalTitle();
        logger.info("Foreclosure modal title: " + title);
        Assert.assertTrue(title.contains("Foreclosure"), "Modal title should contain 'Foreclosure'");

        // Log which view the modal opened on
        if (foreclosurePage.isOnPaymentFormView()) {
            logger.info("Modal opened on: PAYMENT FORM view");
        } else if (foreclosurePage.isOnHistoryView()) {
            logger.info("Modal opened on: HISTORY view");
        } else {
            logger.info("Modal opened on: SUMMARY view");
        }

        foreclosurePage.clickClose();
    }

    // ── Test 2: Loan Payoff Summary fields (only if summary view is shown) ───

    @Test(priority = 2)
    public void testLoanPayoffSummaryOrHistoryFields() {
        logger.info("Verifying modal content fields");
        ForeclosurePage foreclosurePage = detailPage.clickForeclosureOption();
        Assert.assertTrue(foreclosurePage.isModalOpened(), "Foreclosure modal should be open");

        if (foreclosurePage.isOnSummaryView()) {
            logger.info("Summary view — verifying Loan Payoff Summary fields");

            String lan = foreclosurePage.getLoanAccountNumber();
            logger.info("Loan Account Number: " + lan);
            Assert.assertFalse(lan.isEmpty(), "Loan Account Number should not be empty");

            String status = foreclosurePage.getCurrentLoanStatus();
            logger.info("Current Loan Status: " + status);
            Assert.assertFalse(status.isEmpty(), "Current Loan Status should not be empty");

            String principal = foreclosurePage.getOutstandingPrincipal();
            logger.info("Outstanding Principal: " + principal);
            Assert.assertTrue(principal.matches(".*\\d.*"), "Outstanding Principal should contain a number");

            String netDues = foreclosurePage.getTotalNetDues();
            logger.info("Total Net Dues: " + netDues);
            Assert.assertTrue(netDues.matches(".*\\d.*"), "Total Net Dues should contain a number");

        } else if (foreclosurePage.isOnPaymentFormView()) {
            logger.info("Payment form view — verifying quote reference and total payoff");
            String ref = foreclosurePage.getQuoteReferenceOnPaymentForm();
            logger.info("Quote Reference: " + ref);
            Assert.assertTrue(ref.startsWith("FCL-"), "Quote reference should start with 'FCL-'");

            String total = foreclosurePage.getTotalPayoffAmount();
            logger.info("Total Payoff Amount: " + total);
            Assert.assertTrue(total.matches(".*\\d.*"), "Total payoff amount should contain a number");

        } else {
            // History view (or transitioning) — verify by retrieving first quote ID directly
            logger.info("History view — verifying first quote ID is present");
            String quoteId = foreclosurePage.getFirstQuoteId();
            logger.info("First quote ID: " + quoteId);
            Assert.assertTrue(quoteId.startsWith("FCL-"), "Quote ID should start with 'FCL-'");
        }

        foreclosurePage.clickClose();
    }

    // ── Test 3: Generate a new quote ─────────────────────────────────────────

    @Test(priority = 3)
    public void testGenerateForeclosureQuote() {
        logger.info("Verifying Generate Quote flow");
        ForeclosurePage foreclosurePage = detailPage.clickForeclosureOption();
        Assert.assertTrue(foreclosurePage.isModalOpened(), "Foreclosure modal should be open");

        // clickGenerateQuote handles all views (payment form → back → history → generate)
        foreclosurePage.clickGenerateQuote();
        logger.info("Clicked Generate Quote");

        Assert.assertTrue(foreclosurePage.isQuotesHistoryVisible(),
            "Foreclosure Quotes History section should appear after generating a quote");

        String quoteId = foreclosurePage.getFirstQuoteId();
        logger.info("Quote ID: " + quoteId);
        Assert.assertTrue(quoteId.startsWith("FCL-"), "Quote ID should start with 'FCL-'");

        Assert.assertTrue(foreclosurePage.isFirstQuoteActive(), "Generated quote should have Active status");

        String payoffAmount = foreclosurePage.getFirstQuotePayoffAmount();
        logger.info("Payoff amount: " + payoffAmount);
        Assert.assertFalse(payoffAmount.isEmpty(), "Payoff amount should not be empty");

        foreclosurePage.clickClose();
    }

    // ── Test 4: Foreclose → opens payment form ───────────────────────────────

    @Test(priority = 4)
    public void testForecloseButtonOpensPaymentForm() {
        logger.info("Verifying Foreclose button opens payment form");
        ForeclosurePage foreclosurePage = detailPage.clickForeclosureOption();
        Assert.assertTrue(foreclosurePage.isModalOpened(), "Foreclosure modal should be open");

        // Ensure we are on history view with an active quote
        if (!foreclosurePage.isOnHistoryView()) {
            foreclosurePage.clickGenerateQuote();
        }
        Assert.assertTrue(foreclosurePage.isQuotesHistoryVisible(), "Should be on history view");
        Assert.assertTrue(foreclosurePage.isFirstQuoteActive(), "Quote must be Active to foreclose");

        foreclosurePage.clickForecloseOnFirstQuote();
        logger.info("Clicked Foreclose → on first active quote");

        Assert.assertTrue(foreclosurePage.isOnPaymentFormView(),
            "Payment form view should appear after clicking Foreclose");

        String ref = foreclosurePage.getQuoteReferenceOnPaymentForm();
        logger.info("Quote reference on payment form: " + ref);
        Assert.assertTrue(ref.startsWith("FCL-"), "Quote reference should start with 'FCL-'");

        String total = foreclosurePage.getTotalPayoffAmount();
        logger.info("Total payoff / Foreclosure Amount: " + total);
        Assert.assertTrue(total.matches(".*\\d.*"), "Total payoff should contain a number");

        foreclosurePage.clickClose();
    }

    // ── Test 5: Full flow — Foreclose → fill form → Confirm & Foreclose ──────

    @Test(priority = 5)
    public void testForeclosureFullFlow() {
        logger.info("Verifying full Foreclosure flow: generate quote → foreclose → fill form → confirm");
        ForeclosurePage foreclosurePage = detailPage.clickForeclosureOption();
        Assert.assertTrue(foreclosurePage.isModalOpened(), "Foreclosure modal should be open");

        // Step 1: Get to history view with an active quote
        if (foreclosurePage.isOnPaymentFormView()) {
            logger.info("Modal opened on payment form — going back to history");
            foreclosurePage.backToHistoryIfOnPaymentForm();
        }
        if (!foreclosurePage.isOnHistoryView()) {
            foreclosurePage.clickGenerateQuote();
        }

        Assert.assertTrue(foreclosurePage.isQuotesHistoryVisible(), "Should be on history view");
        String quoteId = foreclosurePage.getFirstQuoteId();
        logger.info("Proceeding to foreclose quote: " + quoteId);
        Assert.assertTrue(foreclosurePage.isFirstQuoteActive(), "Quote must be Active");

        // Step 2: Click Foreclose →
        foreclosurePage.clickForecloseOnFirstQuote();
        Assert.assertTrue(foreclosurePage.isOnPaymentFormView(), "Should now be on payment form view");
        logger.info("Payment form opened. Quote ref: " + foreclosurePage.getQuoteReferenceOnPaymentForm());
        logger.info("Total payoff amount: " + foreclosurePage.getTotalPayoffAmount());

        // Step 3: Fill in the Execute Foreclosure Payment form
        foreclosurePage.selectRepaymentMode("RTGS");
        logger.info("Selected Repayment Mode: RTGS");

        String testUtr = "UTR" + System.currentTimeMillis();
        foreclosurePage.enterBankUtr(testUtr);
        logger.info("Entered Bank UTR / Ref Number: " + testUtr);

        foreclosurePage.enterRemarks("Automated test foreclosure — " + quoteId);
        logger.info("Entered remarks");

        // Step 4: Confirm
        Assert.assertTrue(foreclosurePage.isConfirmAndForecloseEnabled(),
            "Confirm & Foreclose button should be enabled after filling required fields");

        foreclosurePage.clickConfirmAndForeclose();
        logger.info("Clicked Confirm & Foreclose");

        pause(3000);
        Assert.assertTrue(
            driver.getCurrentUrl().contains("/lms/accounts/"),
            "Should remain within account details context after confirming foreclosure"
        );
        logger.info("Post-foreclosure URL: " + driver.getCurrentUrl());
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
