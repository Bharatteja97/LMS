package tests;

import base.BaseClass;
import accounts.AccountsPage;
import accounts.AccountDetailsPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccountsDetailsTest extends BaseClass {
    private static final Logger logger = LogManager.getLogger(AccountsDetailsTest.class);
    private static final String ACCOUNTS_URL = "https://lms.alfinnext.com/lms/accounts?product=VEHICLE_LOAN";

    private String detailPageUrl;
    private AccountDetailsPage detailPage;

    /**
     * Navigate to the accounts list once, click the first LAN, and capture the
     * detail-page URL.  Every @BeforeMethod then drives directly to that URL,
     * avoiding a full list-page round-trip for each test.
     */
    @BeforeClass(alwaysRun = true)
    public void captureDetailPageUrl() {
        driver.get(ACCOUNTS_URL);
        AccountsPage accountsPage = new AccountsPage(driver);
        accountsPage.waitForTableToLoad();
        detailPage = accountsPage.clickLANByRow(0);
        detailPage.isPageLoaded();
        detailPageUrl = driver.getCurrentUrl();
        logger.info("Detail page URL captured: " + detailPageUrl);
    }

    @BeforeMethod(alwaysRun = true)
    public void navigateToDetailPage() {
        driver.get(detailPageUrl);
        detailPage = new AccountDetailsPage(driver);
        detailPage.isPageLoaded();
    }

    // ── Page load validation ──────────────────────────────────────────────────

    @Test(priority = 1)
    public void testPageLoadsSuccessfully() {
        logger.info("Verifying detail page loads");
        Assert.assertTrue(detailPage.isPageLoaded(),
            "Account detail page should be loaded (URL matches /accounts/{id} and borrower name visible)");
    }

    @Test(priority = 2)
    public void testUrlContainsAccountId() {
        logger.info("Verifying URL contains numeric account ID");
        String url = driver.getCurrentUrl();
        logger.info("Current URL: " + url);
        Assert.assertTrue(url.matches(".*/lms/accounts/\\d+.*"),
            "URL should match /lms/accounts/{numericId} but was: " + url);
    }

    // ── Header fields ─────────────────────────────────────────────────────────

    @Test(priority = 3)
    public void testBorrowerNameIsVisible() {
        logger.info("Verifying borrower name is visible in header");
        String name = detailPage.getBorrowerName();
        logger.info("Borrower name: " + name);
        Assert.assertFalse(name.isEmpty(), "Borrower name should not be empty");
    }

    @Test(priority = 4)
    public void testLANNumberIsVisible() {
        logger.info("Verifying LAN number is shown in header");
        String lan = detailPage.getLANNumber();
        logger.info("LAN number: " + lan);
        Assert.assertFalse(lan.isEmpty(), "LAN number should not be empty");
        Assert.assertTrue(lan.toUpperCase().startsWith("VEHICLE"),
            "LAN should start with VEHICLE but was: " + lan);
    }

    @Test(priority = 5)
    public void testLANTextContainsPrefix() {
        logger.info("Verifying full LAN label contains 'LAN:' prefix");
        String lanText = detailPage.getLANText();
        logger.info("Full LAN text: " + lanText);
        Assert.assertTrue(lanText.contains("LAN:"),
            "LAN label should contain 'LAN:' prefix but was: " + lanText);
    }

    @Test(priority = 6)
    public void testStatusBadgeIsVisible() {
        logger.info("Verifying status badge is visible");
        String status = detailPage.getStatusBadge();
        logger.info("Status badge: " + status);
        Assert.assertFalse(status.isEmpty(), "Status badge should not be empty");
    }

    @Test(priority = 7)
    public void testANTextIsVisible() {
        logger.info("Verifying AN text is visible in header");
        String anText = detailPage.getANText();
        logger.info("AN text: " + anText);
        Assert.assertFalse(anText.isEmpty(), "AN text should not be empty");
    }

    @Test(priority = 8)
    public void testProductTypeIsVisible() {
        logger.info("Verifying Product Type is visible in header");
        String productType = detailPage.getProductType();
        logger.info("Product type: " + productType);
        Assert.assertFalse(productType.isEmpty(), "Product type should not be empty");
    }

    @Test(priority = 9)
    public void testDisbursedOnDateIsVisible() {
        logger.info("Verifying Disbursed On date is visible in header");
        String disbursedOn = detailPage.getDisbursedOn();
        logger.info("Disbursed on: " + disbursedOn);
        Assert.assertFalse(disbursedOn.isEmpty(), "Disbursed On date should not be empty");
    }

    // ── Info grid fields ──────────────────────────────────────────────────────

    @Test(priority = 10)
    public void testLoanAmountIsDisplayed() {
        logger.info("Verifying Loan Amount is displayed in info grid");
        String amount = detailPage.getLoanAmount();
        logger.info("Loan Amount: " + amount);
        Assert.assertFalse(amount.isEmpty(), "Loan Amount should not be empty");
    }

    @Test(priority = 11)
    public void testOutstandingIsDisplayed() {
        logger.info("Verifying Outstanding is displayed in info grid");
        String outstanding = detailPage.getOutstanding();
        logger.info("Outstanding: " + outstanding);
        Assert.assertFalse(outstanding.isEmpty(), "Outstanding should not be empty");
    }

    @Test(priority = 12)
    public void testCurrentDPDIsDisplayed() {
        logger.info("Verifying Current DPD is displayed in info grid");
        String dpd = detailPage.getCurrentDPD();
        logger.info("Current DPD: " + dpd);
        Assert.assertFalse(dpd.isEmpty(), "Current DPD should not be empty (may be 0)");
    }

    @Test(priority = 13)
    public void testNextEMIDueIsDisplayed() {
        logger.info("Verifying Next EMI Due is displayed in info grid");
        String nextEMI = detailPage.getNextEMIDue();
        logger.info("Next EMI Due: " + nextEMI);
        Assert.assertFalse(nextEMI.isEmpty(), "Next EMI Due should not be empty");
    }

    @Test(priority = 14)
    public void testCurrentLiabilityIsDisplayed() {
        logger.info("Verifying Current Liability is displayed in info grid");
        String liability = detailPage.getCurrentLiability();
        logger.info("Current Liability: " + liability);
        Assert.assertFalse(liability.isEmpty(), "Current Liability should not be empty");
    }

    // ── Summary cards ─────────────────────────────────────────────────────────

    @Test(priority = 15)
    public void testDisbursedAmountCardIsDisplayed() {
        logger.info("Verifying DISBURSED AMOUNT card is shown");
        try {
            System.out.println("--- DOM DUMP ---");
            System.out.println(driver.getPageSource());
            System.out.println("--- END DOM DUMP ---");
        } catch (Exception e) {}
        String amount = detailPage.getDisbursedAmount();
        logger.info("Disbursed Amount: " + amount);
        Assert.assertFalse(amount.isEmpty(), "DISBURSED AMOUNT card value should not be empty");
    }

    @Test(priority = 16)
    public void testNetOutstandingBalanceCardIsDisplayed() {
        logger.info("Verifying NET OUTSTANDING BALANCE card is shown");
        String balance = detailPage.getNetOutstandingBalance();
        logger.info("Net Outstanding Balance: " + balance);
        Assert.assertFalse(balance.isEmpty(), "NET OUTSTANDING BALANCE card value should not be empty");
    }

    @Test(priority = 17)
    public void testPaidAmountCardIsDisplayed() {
        logger.info("Verifying PAID AMOUNT card is shown");
        String paid = detailPage.getPaidAmount();
        logger.info("Paid Amount: " + paid);
        Assert.assertFalse(paid.isEmpty(), "PAID AMOUNT card value should not be empty");
    }

    @Test(priority = 18)
    public void testOverdueAmountCardIsDisplayed() {
        logger.info("Verifying OVERDUE AMOUNT card is shown");
        String overdue = detailPage.getOverdueAmount();
        logger.info("Overdue Amount: " + overdue);
        Assert.assertFalse(overdue.isEmpty(), "OVERDUE AMOUNT card value should not be empty");
    }

    // ── Tab navigation — one by one ───────────────────────────────────────────
    // A single test clicks through every tab in sequence on the same page load.
    // This shows tabs being exercised one by one without reloading the detail page.

    @Test(priority = 19)
    public void testAllTabsOneByOne() {
        String basePattern = ".*/lms/accounts/\\d+.*";

        logger.info("── Tab 1: Overview (already active) ──");
        detailPage.clickOverviewTab();
        pause(1000);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Overview");
        String borrowerName = detailPage.getBorrowerName();
        Assert.assertFalse(borrowerName.isEmpty(), "Borrower name should be visible on Overview tab");
        logger.info("Overview: borrower=" + borrowerName);

        logger.info("── Tab 2: Schedules ──");
        detailPage.clickSchedulesTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Schedules");
        logger.info("Schedules: URL=" + driver.getCurrentUrl());

        logger.info("── Tab 3: Ledger ──");
        detailPage.clickLedgerTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Ledger");
        logger.info("Ledger: URL=" + driver.getCurrentUrl());

        logger.info("── Tab 4: Transaction ──");
        detailPage.clickTransactionTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Transaction");
        logger.info("Transaction: URL=" + driver.getCurrentUrl());

        logger.info("── Tab 5: Payment ──");
        detailPage.clickPaymentTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Payment");
        logger.info("Payment: URL=" + driver.getCurrentUrl());

        logger.info("── Tab 6: Nach Presentation ──");
        detailPage.clickNachPresentationTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Nach Presentation");
        logger.info("Nach Presentation: URL=" + driver.getCurrentUrl());

        logger.info("── Tab 7: Documents ──");
        detailPage.clickDocumentsTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Documents");
        logger.info("Documents: URL=" + driver.getCurrentUrl());

        logger.info("── Tab 8: Tracking History ──");
        detailPage.clickTrackingHistoryTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Tracking History");
        logger.info("Tracking History: URL=" + driver.getCurrentUrl());

        logger.info("── Tab 9: Charges ──");
        detailPage.clickChargesTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Charges");
        logger.info("Charges: URL=" + driver.getCurrentUrl());

        logger.info("── Tab 10: Disbursements ──");
        detailPage.clickDisbursementsTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Disbursements");
        logger.info("Disbursements: URL=" + driver.getCurrentUrl());

        logger.info("── Tab 11: Balance Transfer ──");
        detailPage.clickBalanceTransferTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Balance Transfer");
        logger.info("Balance Transfer: URL=" + driver.getCurrentUrl());

        logger.info("── Tab 12: Insurance Claims ──");
        detailPage.clickInsuranceClaimsTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Insurance Claims");
        logger.info("Insurance Claims: URL=" + driver.getCurrentUrl());

        logger.info("── Tab 13: Audit Trail ──");
        detailPage.clickAuditTrailTab();
        pause(1500);
        Assert.assertTrue(driver.getCurrentUrl().matches(basePattern), "URL should stay on detail page at Audit Trail");
        logger.info("Audit Trail: URL=" + driver.getCurrentUrl());

        logger.info("── Back to Overview ──");
        detailPage.clickOverviewTab();
        pause(1500);
        String nameAfterReturn = detailPage.getBorrowerName();
        Assert.assertFalse(nameAfterReturn.isEmpty(),
            "Borrower name should be visible after returning to Overview tab");
        logger.info("Back to Overview: borrower=" + nameAfterReturn);
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
