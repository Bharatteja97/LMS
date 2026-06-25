package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import underwriting.UnderWritingPage;
import pages.SanctionLetterPage;
import utils.EmailUtil;

import java.util.List;

/**
 * UnderWritingTest
 *
 * End-to-end tests for the Vehicle Loan Underwriting module.
 *
 * Test flow:
 *  1. Open the Underwriting list page and verify it loads with data.
 *  2. Search for an application and verify the table updates.
 *  3. Open the FIRST application from the list and verify the detail page.
 *  4. Navigate each tab on the detail page.
 *  5. Open the "Update status" modal and verify available statuses.
 *  6. Cancel the modal (no state change).
 *  7. Click "Approve" and verify no error.
 *
 * The suite uses the shared {@link BaseClass} browser session that is
 * opened once before the suite and closed after all tests complete.
 */
public class UnderWritingTest extends BaseClass {

    // ─── Underwriting URL ──────────────────────────────────────────────────────
    private static final String UW_LIST_URL =
            "https://lms.alfinnext.com/vehicle/underwriting?product=VEHICLE_LOAN";

    // Application opened during Test 1; reused by subsequent tests
    private static String openedApplicationNo;

    // ════════════════════════════════════════════════════════════════════════════
    // Test 1 – List page loads and shows applications
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 1)
    public void testUnderwritingListPageLoads() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating to Underwriting list page...");
        System.out.println("═══════════════════════════════════════════════════════════");

        driver.get(UW_LIST_URL);
        Thread.sleep(3000);

        UnderWritingPage uwPage = new UnderWritingPage(driver);
        uwPage.waitForTableToLoad();

        int rowCount = uwPage.getTableRowCount();
        System.out.println("[INFO] Applications displayed in table: " + rowCount);
        Assert.assertTrue(rowCount > 0,
                "Underwriting list page should show at least one application row. Found: " + rowCount);

        List<String> appNumbers = uwPage.getApplicationNumbers();
        System.out.println("[INFO] Application numbers visible: " + appNumbers);
        Assert.assertFalse(appNumbers.isEmpty(),
                "At least one application number should be visible in the table.");

        List<String> statuses = uwPage.getApplicationStatuses();
        System.out.println("[INFO] Application statuses visible: " + statuses);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Underwriting list page loaded with " + rowCount + " application(s).");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Test 2 – Quick-search filter
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 2, dependsOnMethods = "testUnderwritingListPageLoads")
    public void testSearchFunctionality() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Testing quick-search filter...");
        System.out.println("═══════════════════════════════════════════════════════════");

        UnderWritingPage uwPage = new UnderWritingPage(driver);

        // Search for a string unlikely to match — should show no-results message
        uwPage.search("ZZZZZ_NONEXISTENT_9999");
        Thread.sleep(2000);
        boolean noResults = uwPage.isNoResultsDisplayed();
        System.out.println("[INFO] 'No results' displayed for junk search: " + noResults);

        // Reset and confirm table rows reappear
        uwPage.clickReset();
        Thread.sleep(1500);
        int rowCount = uwPage.getTableRowCount();
        Assert.assertTrue(rowCount > 0,
                "After Reset, the table should show applications again. Found: " + rowCount);
        System.out.println("[INFO] After reset, row count: " + rowCount);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Search and Reset functionality working correctly.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Test 3 – Open first application → detail page loads
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 3, dependsOnMethods = "testSearchFunctionality")
    public void testOpenApplicationDetail() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Opening first application from the list...");
        System.out.println("═══════════════════════════════════════════════════════════");

        UnderWritingPage uwPage = new UnderWritingPage(driver);

        // Navigate back to the list to ensure we have a fresh list
        driver.get(UW_LIST_URL);
        Thread.sleep(3000);
        uwPage.waitForTableToLoad();

        // Click the first application – this opens the detail view
        openedApplicationNo = uwPage.openFirstApplication();
        Thread.sleep(3000);

        // Verify we landed on the detail URL
        String currentUrl = driver.getCurrentUrl();
        System.out.println("[INFO] Current URL after opening application: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("/vehicle/underwriting/"),
                "URL should contain '/vehicle/underwriting/' after opening an application. Actual: " + currentUrl);

        // Extract APPLICATION_ID and save to System Properties
        String appId = currentUrl.split("/underwriting/")[1].split("\\?")[0];
        System.setProperty("APPLICATION_ID", appId);
        System.out.println("[INFO] Saved APPLICATION_ID to System properties: " + appId);

        // Verify status badge is present
        String status = uwPage.getApplicationStatus();
        System.out.println("[INFO] Application status: " + status);
        Assert.assertFalse(status.isEmpty() || status.equals("UNKNOWN"),
                "Application status badge should be readable. Got: " + status);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Opened application '" + openedApplicationNo
                + "' | Status: " + status);
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Test 4 – Tab navigation on the detail page
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 4, dependsOnMethods = "testOpenApplicationDetail")
    public void testTabNavigation() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating through detail page tabs...");
        System.out.println("═══════════════════════════════════════════════════════════");

        UnderWritingPage uwPage = new UnderWritingPage(driver);

        // Borrower Details (default tab)
        uwPage.clickBorrowerDetailsTab();
        Thread.sleep(800);
        System.out.println("[INFO] Borrower Details tab clicked – OK");

        // Asset Details
        uwPage.clickAssetDetailsTab();
        Thread.sleep(800);
        System.out.println("[INFO] Asset Details tab clicked – OK");

        // Loan Details
        uwPage.clickLoanDetailsTab();
        Thread.sleep(800);
        System.out.println("[INFO] Loan Details tab clicked – OK");

        // Documents
        uwPage.clickDocumentsTab();
        Thread.sleep(800);
        System.out.println("[INFO] Documents tab clicked – OK");

        // Tracking History
        uwPage.clickTrackingHistoryTab();
        Thread.sleep(800);
        System.out.println("[INFO] Tracking History tab clicked – OK");

        // Return to Borrower Details
        uwPage.clickBorrowerDetailsTab();
        Thread.sleep(800);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] All tabs navigated successfully.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Test 5 – Update status modal opens and shows status options
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 5, dependsOnMethods = "testTabNavigation")
    public void testUpdateStatusModalOpens() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Opening 'Update status' modal...");
        System.out.println("═══════════════════════════════════════════════════════════");

        UnderWritingPage uwPage = new UnderWritingPage(driver);

        // Click Update status → modal should appear
        uwPage.clickUpdateStatus();
        Thread.sleep(1500);

        Assert.assertTrue(uwPage.isModalOpen(),
                "The 'Update Application status' modal should be visible after clicking 'Update status'.");
        System.out.println("[INFO] Modal is open.");

        // Read available statuses
        List<String> statuses = uwPage.getAvailableStatuses();
        System.out.println("[INFO] Available status options: " + statuses);
        Assert.assertFalse(statuses.isEmpty(),
                "The Lead Status dropdown should have at least one option.");

        // Cancel — do NOT change status
        uwPage.cancelUpdateStatus();
        Thread.sleep(1000);

        Assert.assertFalse(uwPage.isModalOpen(),
                "Modal should be closed after clicking 'Cancel'.");
        System.out.println("[INFO] Modal closed after Cancel.");

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Update status modal works correctly.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Test 6 – Verify Approve button is present and enabled
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 6, dependsOnMethods = "testUpdateStatusModalOpens")
    public void testApproveButtonPresent() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Verifying 'Approve' button is present and enabled...");
        System.out.println("═══════════════════════════════════════════════════════════");

        UnderWritingPage uwPage = new UnderWritingPage(driver);

        boolean approveEnabled = uwPage.isApproveButtonEnabled();
        System.out.println("[INFO] Approve button enabled: " + approveEnabled);
        Assert.assertTrue(approveEnabled,
                "'Approve' button should be visible and enabled on the Underwriting detail page.");

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] 'Approve' button is present and enabled.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Test 7 – Navigate directly to the underwriting detail by known application DB id
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 7)
    public void testDirectNavigationToApplicationDetail() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Direct navigation to known application detail page...");
        System.out.println("═══════════════════════════════════════════════════════════");

        // Application DB ID is fetched dynamically from earlier test
        final String knownAppDbId = System.getProperty("APPLICATION_ID");
        if (knownAppDbId == null) {
            throw new IllegalStateException("APPLICATION_ID is not set! Run testOpenApplicationDetail first.");
        }

        UnderWritingPage uwPage = new UnderWritingPage(driver);
        uwPage.navigateToDetail(knownAppDbId);
        Thread.sleep(3000);

        String currentUrl = driver.getCurrentUrl();
        System.out.println("[INFO] URL after direct navigation: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("/vehicle/underwriting/" + knownAppDbId),
                "URL should reference application id " + knownAppDbId + ". Actual: " + currentUrl);

        String status = uwPage.getApplicationStatus();
        System.out.println("[INFO] Application status: " + status);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Direct navigation to application " + knownAppDbId
                + " succeeded. Status: " + status);
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Test 8 – Deal parameters: set Preferred Repayment Date and save
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 8, dependsOnMethods = "testDirectNavigationToApplicationDetail")
    public void testDealParametersSetPreferredRepaymentDate() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Deal parameters — set Preferred Repayment Date & save...");
        System.out.println("═══════════════════════════════════════════════════════════");

        UnderWritingPage uwPage = new UnderWritingPage(driver);

        // ── Step 1: Open the Deal parameters tab ─────────────────────────────
        System.out.println("[INFO] Clicking 'Deal parameters' tab...");
        uwPage.clickDealParametersTab();
        Thread.sleep(1500);

        // ── Step 2: Click "Update parameters" to open the edit form ──────────
        System.out.println("[INFO] Clicking 'Update parameters' button...");
        uwPage.clickUpdateParameters();
        Thread.sleep(1500);

        // ── Step 3: Build a date 7 days from today (YYYY-MM-DD) ─────────────
        java.time.LocalDate repaymentDate = java.time.LocalDate.now().plusDays(7);
        String dateStr = repaymentDate.toString();          // e.g. "2025-07-01"
        System.out.println("[INFO] Setting Preferred Repayment Date to: " + dateStr);

        // ── Step 4: Enter the preferred repayment date ───────────────────────
        uwPage.enterPreferredRepaymentDate(dateStr);
        Thread.sleep(500);

        // Verify the input registered the value before saving
        String storedValue = uwPage.getPreferredRepaymentDateValue();
        System.out.println("[INFO] Date value in input after entry: " + storedValue);
        Assert.assertEquals(storedValue, dateStr,
                "Preferred Repayment Date input should hold the entered value before saving. " +
                "Expected: " + dateStr + " | Actual: " + storedValue);

        // ── Step 5: Click Save ───────────────────────────────────────────────
        System.out.println("[INFO] Clicking 'Save' to persist Deal parameters...");
        uwPage.clickSaveDealParameters();
        Thread.sleep(2500);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Preferred Repayment Date set to '" + dateStr
                + "' and saved successfully.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Test 9 – Bank Details: Add Customer Bank
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 9, dependsOnMethods = "testDealParametersSetPreferredRepaymentDate")
    public void testBankDetailsAddCustomerBank() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Bank Details — Add Customer Bank...");
        System.out.println("═══════════════════════════════════════════════════════════");

        UnderWritingPage uwPage = new UnderWritingPage(driver);

        // ── Step 1: Navigate to Bank Details tab ─────────────────────────────
        System.out.println("[INFO] Clicking 'Bank Details' tab...");
        uwPage.clickBankDetailsTab();
        Thread.sleep(1500);

        // Record row count before adding
        int rowsBefore = uwPage.getBankTableRowCount();
        System.out.println("[INFO] Bank table rows before adding: " + rowsBefore);

        // ── Step 2: Click '+ Add Customer bank' ──────────────────────────────
        System.out.println("[INFO] Clicking 'Add Customer bank' button...");
        uwPage.clickAddCustomerBank();
        Thread.sleep(1500);

        Assert.assertTrue(uwPage.isBankModalOpen(),
                "'Create bank' modal should be open after clicking 'Add Customer bank'.");
        System.out.println("[INFO] 'Create bank' modal is open.");

        // ── Step 3: Inspect available Account type options ───────────────────
        List<String> accountTypes = uwPage.getAccountTypeOptions();
        System.out.println("[INFO] Account type options: " + accountTypes);
        Assert.assertFalse(accountTypes.isEmpty(),
                "Account type dropdown should have at least one selectable option.");

        // ── Step 4: Fill in all fields ────────────────────────────────────────
        String bankName      = "HDFC Bank";
        String holderName    = "Ramesh Kumar";
        String accountNumber = "50100123456789";
        String branch        = "Koramangala";
        String city          = "Bangalore";
        String ifscCode      = "HDFC0001234";
        String accountType   = accountTypes.get(0);   // use first real option

        System.out.println("[INFO] Filling Bank name: " + bankName);
        uwPage.fillBankName(bankName);

        System.out.println("[INFO] Filling Name as per cheque: " + holderName);
        uwPage.fillNameAsPerCheque(holderName);

        System.out.println("[INFO] Filling Account number: " + accountNumber);
        uwPage.fillAccountNumber(accountNumber);

        System.out.println("[INFO] Filling Branch: " + branch);
        uwPage.fillBranch(branch);

        System.out.println("[INFO] Filling City: " + city);
        uwPage.fillCity(city);

        System.out.println("[INFO] Filling IFSC code: " + ifscCode);
        uwPage.fillIfscCode(ifscCode);

        System.out.println("[INFO] Selecting Account type: " + accountType);
        uwPage.selectAccountType(accountType);

        // Mark default is already checked by default — leave it checked
        uwPage.setMarkDefault(true);
        System.out.println("[INFO] Mark default: true");

        // ── Step 5: Click Save ────────────────────────────────────────────────
        System.out.println("[INFO] Clicking 'Save' to create the bank record...");
        uwPage.clickSaveBankDetails();
        Thread.sleep(2500);

        // ── Step 6: Verify the table now has one more row ─────────────────────
        int rowsAfter = uwPage.getBankTableRowCount();
        System.out.println("[INFO] Bank table rows after adding: " + rowsAfter);
        Assert.assertTrue(rowsAfter > rowsBefore,
                "Bank table should have more rows after adding a bank. Before: "
                + rowsBefore + " | After: " + rowsAfter);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Customer bank '" + bankName + "' added successfully.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Test 10 – Click Approve button on the Underwriting detail page
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 10, dependsOnMethods = "testBankDetailsAddCustomerBank")
    public void testClickApproveButton() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Clicking 'Approve' button on Underwriting detail page...");
        System.out.println("═══════════════════════════════════════════════════════════");

        final String APP_DB_ID = System.getProperty("APPLICATION_ID");
        if (APP_DB_ID == null) {
            throw new IllegalStateException("APPLICATION_ID is not set! Run testOpenApplicationDetail first.");
        }

        UnderWritingPage uwPage = new UnderWritingPage(driver);

        // ── Step 1: Navigate directly to the application detail page ─────────
        System.out.println("[INFO] Navigating to application detail: " + APP_DB_ID);
        uwPage.navigateToDetail(APP_DB_ID);
        Thread.sleep(3000);

        // ── Step 2: Confirm we are on the right page ──────────────────────────
        String currentUrl = driver.getCurrentUrl();
        System.out.println("[INFO] URL: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("/vehicle/underwriting/" + APP_DB_ID),
                "Should be on application " + APP_DB_ID + " detail page. Actual URL: " + currentUrl);

        // ── Step 3: Read status BEFORE approving ──────────────────────────────
        String statusBefore = uwPage.getApplicationStatus();
        System.out.println("[INFO] Application status BEFORE approve: " + statusBefore);

        // ── Step 4: Assert the Approve button is visible and enabled ──────────
        Assert.assertTrue(uwPage.isApproveButtonEnabled(),
                "'Approve' button must be visible and enabled before clicking.");
        System.out.println("[INFO] 'Approve' button is enabled — clicking...");

        // ── Step 5: Click Approve ─────────────────────────────────────────────
        uwPage.clickApprove();
        Thread.sleep(3000); // allow any confirmation dialog / page refresh

        // ── Step 6: Handle confirmation modal if it appears ───────────────────
        // Some flows show a modal asking to confirm; dismiss it if present.
        try {
            org.openqa.selenium.support.ui.WebDriverWait shortWait =
                    new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5));
            // Look for a "Confirm" / "Yes" / "OK" button inside a dialog
            org.openqa.selenium.WebElement confirmBtn = shortWait.until(
                    org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                            org.openqa.selenium.By.xpath(
                                    "//div[@role='dialog']//button[contains(normalize-space(.),'Confirm') " +
                                    "or contains(normalize-space(.),'Yes') " +
                                    "or contains(normalize-space(.),'OK') " +
                                    "or contains(normalize-space(.),'Approve')]")));
            System.out.println("[INFO] Confirmation dialog appeared — button text: '"
                    + confirmBtn.getText().trim() + "'. Clicking confirm...");
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", confirmBtn);
            Thread.sleep(2500);
        } catch (org.openqa.selenium.TimeoutException noModal) {
            System.out.println("[INFO] No confirmation dialog appeared — Approve was accepted directly.");
        }

        // ── Step 7: Read status AFTER approving ───────────────────────────────
        String statusAfter = uwPage.getApplicationStatus();
        System.out.println("[INFO] Application status AFTER approve: " + statusAfter);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] 'Approve' button clicked successfully.");
        System.out.println("       Status: " + statusBefore + " → " + statusAfter);
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Test 11 – Dispatch Sanction Mail
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 11, dependsOnMethods = "testClickApproveButton")
    public void testDispatchSanctionMail() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Dispatching Sanction Mail...");
        System.out.println("═══════════════════════════════════════════════════════════");

        UnderWritingPage uwPage = new UnderWritingPage(driver);

        // Click Dispatch Sanction Mail button
        System.out.println("[INFO] Clicking 'Dispatch Sanction Mail' button...");
        uwPage.clickDispatchSanctionMail();
        Thread.sleep(1500);

        // Verify confirmation dialog is visible
        Assert.assertTrue(uwPage.isSanctionMailDialogVisible(),
                "Sanction Mail confirmation dialog should be visible.");
        System.out.println("[INFO] Sanction Mail confirmation dialog is visible.");

        // Click Send
        System.out.println("[INFO] Clicking Send inside confirmation dialog...");
        uwPage.clickSendSanctionMail();
        Thread.sleep(3000);

        // Verify modal closed
        Assert.assertFalse(uwPage.isSanctionMailDialogVisible(),
                "Sanction Mail confirmation dialog should be closed after sending.");
        System.out.println("[INFO] Sanction Mail confirmation dialog closed.");

        // Verify button text or sent status text
        String buttonText = uwPage.getDispatchSanctionMailButtonText();
        System.out.println("[INFO] Sanction Mail button text after sending: " + buttonText);
        Assert.assertEquals(buttonText, "Re-Dispatch Sanction Mail",
                "Button text should update to 'Re-Dispatch Sanction Mail' after sending.");

        String sentStatusText = uwPage.getSanctionMailSentStatusText();
        System.out.println("[INFO] Sent status text: " + sentStatusText);
        Assert.assertTrue(sentStatusText.contains("sent at:"),
                "Sent status label containing 'sent at:' should be present under the button.");

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Dispatch Sanction Mail flow verified successfully.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Test 12 – Retrieve Sanction Letter Link and Submit Approval
    // ════════════════════════════════════════════════════════════════════════════

    @Test(priority = 12, dependsOnMethods = "testDispatchSanctionMail")
    public void testSubmitSanctionLetter() throws Exception {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Retrieving Sanction Letter Link & Submitting Approval...");
        System.out.println("═══════════════════════════════════════════════════════════");

        // Gmail IMAP credentials
        final String IMAP_USER = "bharatteja09@gmail.com";
        final String IMAP_PASSWORD = "oxtlolbsglttfqde";

        // Wait a few seconds for email delivery
        System.out.println("[INFO] Waiting for sanction email to arrive...");
        Thread.sleep(7000);

        String sanctionLink = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.println("[INFO] Attempt " + attempt + " to fetch Sanction link from email...");
            try {
                sanctionLink = EmailUtil.getSanctionLinkFromGmail(IMAP_USER, IMAP_PASSWORD);
            } catch (Exception e) {
                System.out.println("[ERROR] Error reading inbox on attempt " + attempt + ": " + e.getMessage());
            }
            if (sanctionLink != null) break;
            if (attempt < 3) {
                System.out.println("[INFO] Sanction link not yet found. Waiting 10 seconds before retry...");
                Thread.sleep(10000);
            }
        }

        Assert.assertNotNull(sanctionLink, "Sanction letter link must be retrieved from email.");
        System.out.println("[INFO] Navigating to Sanction Letter URL: " + sanctionLink);

        driver.get(sanctionLink);
        Thread.sleep(3000);

        SanctionLetterPage sanctionPage = new SanctionLetterPage(driver);
        sanctionPage.waitForPageLoad();

        // Click View Sanction Letter
        sanctionPage.clickViewSanctionLetter();
        Thread.sleep(1500);

        // Check the accept terms checkbox
        sanctionPage.acceptTermsAndConditions();
        Thread.sleep(1000);

        // Click Submit Sanction
        sanctionPage.clickSubmitSanction();
        Thread.sleep(3000);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Sanction letter submitted and approved successfully.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}


