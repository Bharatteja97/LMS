package underwriting;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the Underwriting module.
 *
 * Covers two views:
 * 1. Underwriting LIST page  — /vehicle/underwriting?product=VEHICLE_LOAN
 *    • Search / filter applications
 *    • Click an application row to open the detail view
 *
 * 2. Underwriting DETAIL page — /vehicle/underwriting/{id}?product=VEHICLE_LOAN
 *    • Read summary header (loan amount, EMI, status …)
 *    • Click "Approve" button
 *    • Click "Update status" button and interact with the status-update modal
 *    • Navigate between tabs (Borrower Details, Loan Details, Documents, Tracking History …)
 */
public class UnderWritingPage {

    // ─── Constants ──────────────────────────────────────────────────────────────
    public static final String BASE_URL =
            "https://lms.alfinnext.com/vehicle/underwriting?product=VEHICLE_LOAN";
    public static final String DETAIL_URL_TEMPLATE =
            "https://lms.alfinnext.com/vehicle/underwriting/%s?product=VEHICLE_LOAN";

    // ─── Infrastructure ──────────────────────────────────────────────────────────
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    // ════════════════════════════════════════════════════════════════════════════
    // LIST PAGE ELEMENTS
    // ════════════════════════════════════════════════════════════════════════════

    /** Quick-search text box ("Search by the fields mentioned in the table") */
    @FindBy(xpath = "//input[contains(@placeholder,'Search by the fields')]")
    private WebElement quickSearchInput;

    /** Reset button that clears filters */
    @FindBy(xpath = "//button[normalize-space(.)='Reset']")
    private WebElement resetButton;

    /** Vehicle Type filter dropdown */
    @FindBy(xpath = "//select[contains(@class,'border') and preceding-sibling::*[contains(text(),'Vehicle')] " +
                    "or //label[contains(text(),'Vehicle Type')]/following::select[1]]")
    private WebElement vehicleTypeFilter;

    /** Status filter dropdown */
    @FindBy(xpath = "//select[contains(@class,'border') and (preceding-sibling::label[contains(text(),'Status')] " +
                    "or following-sibling::label[contains(text(),'Status')])]")
    private WebElement statusFilter;

    /** "No results" indicator — shown when the search returns nothing */
    @FindBy(xpath = "//*[contains(normalize-space(.),'Sorry') and contains(normalize-space(.),\"couldn't find\")]")
    private WebElement noResultsLabel;

    // ════════════════════════════════════════════════════════════════════════════
    // DETAIL PAGE — HEADER SUMMARY
    // ════════════════════════════════════════════════════════════════════════════

    @FindBy(xpath = "//button[contains(@class,'rounded') and (contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'processing') " +
                    "or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'approved') " +
                    "or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'rejected'))]")
    private WebElement applicationStatusBadge;

    /** Loan amount shown in the top summary strip */
    @FindBy(xpath = "//*[contains(@class,'font-bold') and (contains(@class,'text-') or true) " +
                    "and (preceding-sibling::*[contains(normalize-space(.),'Loan Amount')] " +
                    "or following-sibling::*[contains(normalize-space(.),'Loan Amount')] " +
                    "or ../preceding-sibling::*[contains(normalize-space(.),'Loan Amount')])]")
    private WebElement loanAmountDisplay;

    /** EMI amount in the header strip */
    @FindBy(xpath = "//*[contains(normalize-space(.),'EMI') and not(self::button)]" +
                    "/following-sibling::*[1] | " +
                    "//*[@data-field='emi'] | " +
                    "//*[contains(@class,'emi')]")
    private WebElement emiDisplay;

    // ════════════════════════════════════════════════════════════════════════════
    // DETAIL PAGE — PRIMARY ACTION BUTTONS
    // ════════════════════════════════════════════════════════════════════════════

    /** "Approve" button in the detail header */
    @FindBy(xpath = "//button[contains(normalize-space(.),'Approve') and not(contains(normalize-space(.),'Update'))]")
    private WebElement approveButton;

    /** "Update status" button in the detail header */
    @FindBy(xpath = "//button[normalize-space(.)='Update status' or normalize-space(.)='Update Status']")
    private WebElement updateStatusButton;

    /** "Dispatch Sanction Mail" or "Re-Dispatch Sanction Mail" button in the detail header */
    @FindBy(xpath = "//button[normalize-space(.)='Dispatch Sanction Mail' or normalize-space(.)='Re-Dispatch Sanction Mail']")
    private WebElement dispatchSanctionMailButton;

    /** "Send" button inside the Sanction Mail dialog */
    @FindBy(xpath = "//div[@role='dialog']//button[normalize-space(.)='Send' or .//span[normalize-space(.)='Send']]")
    private WebElement sanctionMailDialogSendButton;

    /** "Cancel" button inside the Sanction Mail dialog */
    @FindBy(xpath = "//div[@role='dialog']//button[normalize-space(.)='Cancel']")
    private WebElement sanctionMailDialogCancelButton;

    // ════════════════════════════════════════════════════════════════════════════
    // DETAIL PAGE — UPDATE-STATUS MODAL
    // ════════════════════════════════════════════════════════════════════════════

    /** Modal / dialog container for "Update Application status" */
    @FindBy(xpath = "//div[@role='dialog'] | //div[contains(@class,'dialog')] | //div[contains(@class,'modal')]")
    private WebElement updateStatusModal;

    /** Lead Status <select> inside the modal */
    @FindBy(xpath = "//div[@role='dialog']//select | " +
                    "//div[contains(@class,'dialog')]//select | " +
                    "//div[contains(@class,'modal')]//select")
    private WebElement leadStatusSelect;

    /** Reason / notes <textarea> inside the modal */
    @FindBy(xpath = "//div[@role='dialog']//textarea | " +
                    "//div[contains(@class,'dialog')]//textarea | " +
                    "//div[contains(@class,'modal')]//textarea")
    private WebElement reasonTextarea;

    /** "Update Status" submit button inside the modal */
    @FindBy(xpath = "//div[@role='dialog']//button[contains(normalize-space(.),'Update Status') " +
                    "or contains(normalize-space(.),'Update status')] | " +
                    "//div[contains(@class,'dialog')]//button[contains(normalize-space(.),'Update Status')]")
    private WebElement modalUpdateStatusButton;

    /** "Cancel" button inside the modal */
    @FindBy(xpath = "//div[@role='dialog']//button[normalize-space(.)='Cancel'] | " +
                    "//div[contains(@class,'dialog')]//button[normalize-space(.)='Cancel'] | " +
                    "//div[contains(@class,'modal')]//button[normalize-space(.)='Cancel']")
    private WebElement modalCancelButton;

    // ════════════════════════════════════════════════════════════════════════════
    // DETAIL PAGE — TABS
    // ════════════════════════════════════════════════════════════════════════════

    @FindBy(xpath = "//div[normalize-space(.)='Borrower Details']")
    private WebElement tabBorrowerDetails;

    @FindBy(xpath = "//div[normalize-space(.)='Asset details' or normalize-space(.)='Asset Details']")
    private WebElement tabAssetDetails;

    @FindBy(xpath = "//div[normalize-space(.)='Loan Details']")
    private WebElement tabLoanDetails;

    @FindBy(xpath = "//div[normalize-space(.)='Bank Details']")
    private WebElement tabBankDetails;

    @FindBy(xpath = "//div[normalize-space(.)='Documents']")
    private WebElement tabDocuments;

    @FindBy(xpath = "//div[normalize-space(.)='CREDIT Report' or normalize-space(.)='Credit Report']")
    private WebElement tabCreditReport;

    @FindBy(xpath = "//div[normalize-space(.)='Deal parameters' or normalize-space(.)='Deal Parameters']")
    private WebElement tabDealParameters;

    @FindBy(xpath = "//div[normalize-space(.)='Tracking History']")
    private WebElement tabTrackingHistory;

    @FindBy(xpath = "//div[normalize-space(.)='AI Insights']")
    private WebElement tabAiInsights;

    // ════════════════════════════════════════════════════════════════════════════
    // DEAL PARAMETERS TAB — EDIT FORM ELEMENTS
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * "Update parameters" button — shown in the Deal parameters read view.
     * Clicking it switches the tab into edit mode.
     */
    @FindBy(xpath = "//button[normalize-space(.)='Update parameters']")
    private WebElement updateParametersButton;

    /**
     * "Prefered repayment date" date input (type=date, format mm/dd/yyyy).
     * Present only when the Deal parameters form is in edit mode.
     */
    @FindBy(xpath = "//input[@type='date'] | " +
                    "//label[contains(normalize-space(.),'repayment date')]/following-sibling::input | " +
                    "//label[contains(normalize-space(.),'repayment date')]/..//input")
    private WebElement preferredRepaymentDateInput;

    /**
     * "Save" button at the bottom of the Deal parameters edit form.
     */
    @FindBy(xpath = "//button[normalize-space(.)='Save']")
    private WebElement dealParamSaveButton;

    /**
     * "Cancel Update" button at the bottom of the Deal parameters edit form.
     */
    @FindBy(xpath = "//button[normalize-space(.)='Cancel Update' or normalize-space(.)='Cancel update']")
    private WebElement dealParamCancelButton;

    // ════════════════════════════════════════════════════════════════════════════
    // BANK DETAILS TAB — ADD CUSTOMER BANK MODAL ELEMENTS
    // ════════════════════════════════════════════════════════════════════════════

    /** "+" Add Customer bank button on the Bank Details tab */
    @FindBy(xpath = "//button[contains(normalize-space(.),'Add Customer bank') " +
                    "or contains(normalize-space(.),'Add customer bank')]")
    private WebElement addCustomerBankButton;

    /** 'Create bank' modal/dialog container */
    @FindBy(xpath = "//div[@role='dialog'] | //div[contains(@class,'dialog')]")
    private WebElement createBankModal;

    /** Bank name text input inside the Create-bank modal */
    @FindBy(xpath = "//input[@placeholder='Enter bank name']")
    private WebElement bankNameInput;

    /** Name as per cheque (account holder name) input */
    @FindBy(xpath = "//input[@placeholder='Enter account holder name']")
    private WebElement nameAsPerChequeInput;

    /** Account number input */
    @FindBy(xpath = "//input[@placeholder='Enter account number']")
    private WebElement accountNumberInput;

    /** Branch name input */
    @FindBy(xpath = "//input[@placeholder='Enter branch name']")
    private WebElement branchInput;

    /** City input */
    @FindBy(xpath = "//input[@placeholder='Enter city']")
    private WebElement cityInput;

    /** IFSC code input */
    @FindBy(xpath = "//input[@placeholder='Enter IFSC code']")
    private WebElement ifscCodeInput;

    /** Account type dropdown (\"Select account type\") */
    @FindBy(xpath = "//select[.//option[normalize-space(.)='Select account type'] " +
                    "or @name='accountType' or @id='accountType']")
    private WebElement accountTypeSelect;

    /** Mark default checkbox (checked by default) */
    @FindBy(xpath = "//input[@type='checkbox']")
    private WebElement markDefaultCheckbox;

    /** Save button inside the Create-bank modal */
    @FindBy(xpath = "//div[@role='dialog']//button[normalize-space(.)='Save'] | " +
                    "//div[contains(@class,'dialog')]//button[normalize-space(.)='Save'] | " +
                    "//div[contains(@class,'modal')]//button[normalize-space(.)='Save']")
    private WebElement bankModalSaveButton;

    /** Cancel button inside the Create-bank modal */
    @FindBy(xpath = "//div[@role='dialog']//button[normalize-space(.)='Cancel'] | " +
                    "//div[contains(@class,'dialog')]//button[normalize-space(.)='Cancel'] | " +
                    "//div[contains(@class,'modal')]//button[normalize-space(.)='Cancel']")
    private WebElement bankModalCancelButton;

    // ════════════════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ════════════════════════════════════════════════════════════════════════════

    public UnderWritingPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js     = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // LIST PAGE METHODS
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Navigate to the Underwriting list page.
     */
    public void navigateToList() {
        driver.get(BASE_URL);
        waitForTableToLoad();
        System.out.println("[INFO] Navigated to Underwriting list page.");
    }

    /**
     * Navigate directly to the underwriting detail page for a given application DB id.
     *
     * @param applicationId the numeric DB id (e.g. 465)
     */
    public void navigateToDetail(String applicationId) {
        String url = String.format(DETAIL_URL_TEMPLATE, applicationId);
        driver.get(url);
        waitForDetailPageToLoad();
        System.out.println("[INFO] Navigated to Underwriting detail: " + url);
    }

    /**
     * Type text into the quick-search box and wait for the table to update.
     *
     * @param searchText application number, lead number, or borrower name fragment
     */
    public void search(String searchText) {
        wait.until(ExpectedConditions.elementToBeClickable(quickSearchInput));
        quickSearchInput.clear();
        quickSearchInput.sendKeys(searchText);
        sleep(1500); // debounce
        System.out.println("[INFO] Searched for: " + searchText);
    }

    /**
     * Click the Reset button to clear all filters.
     */
    public void clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(resetButton));
        jsClick(resetButton);
        waitForTableToLoad();
        System.out.println("[INFO] Filters reset.");
    }

    /**
     * Return true when the "Sorry couldn't find any leads" message is visible.
     */
    public boolean isNoResultsDisplayed() {
        try {
            return noResultsLabel.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Click the Application No cell of a table row identified by the application code.
     * This opens the underwriting detail view for that application.
     *
     * @param applicationNo e.g. "APP52393182"
     */
    public void openApplicationByNo(String applicationNo) {
        By cellLocator = By.xpath(
                "//table//td[normalize-space(.)='" + applicationNo + "']");
        WebElement cell = wait.until(ExpectedConditions.elementToBeClickable(cellLocator));
        jsClick(cell);
        waitForDetailPageToLoad();
        System.out.println("[INFO] Opened application: " + applicationNo);
    }

    /**
     * Open the first application in the list (row 1, Application No column).
     *
     * @return the application number text that was clicked
     */
    public String openFirstApplication() {
        By firstAppCell = By.xpath("//table//tbody//tr[1]//td[2]");
        WebElement cell = wait.until(ExpectedConditions.elementToBeClickable(firstAppCell));
        String appNo = cell.getText().trim();
        jsClick(cell);
        waitForDetailPageToLoad();
        System.out.println("[INFO] Opened first application: " + appNo);
        return appNo;
    }

    /**
     * Get all visible Application Numbers from the table.
     */
    public List<String> getApplicationNumbers() {
        By cells = By.xpath("//table//tbody//tr//td[2]");
        List<WebElement> elements = driver.findElements(cells);
        return elements.stream()
                       .map(e -> e.getText().trim())
                       .filter(t -> !t.isEmpty())
                       .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get all visible Application Statuses from the table (column 6).
     */
    public List<String> getApplicationStatuses() {
        By statusCells = By.xpath("//table//tbody//tr//td[6]//button");
        List<WebElement> elements = driver.findElements(statusCells);
        return elements.stream()
                       .map(e -> e.getText().trim())
                       .filter(t -> !t.isEmpty())
                       .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Return the number of rows currently displayed in the table.
     */
    public int getTableRowCount() {
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        return rows.size();
    }

    // ════════════════════════════════════════════════════════════════════════════
    // DETAIL PAGE — HEADER INFO
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Return the current application status badge text (e.g. "in processing").
     */
    public String getApplicationStatus() {
        try {
            wait.until(ExpectedConditions.visibilityOf(applicationStatusBadge));
            return applicationStatusBadge.getText().trim();
        } catch (Exception e) {
            // Fallback: look for any status chip in the page header
            List<WebElement> chips = driver.findElements(
                    By.xpath("//header//button | //*[contains(@class,'badge') or contains(@class,'chip')]"));
            for (WebElement chip : chips) {
                String txt = chip.getText().trim();
                if (txt.toLowerCase().contains("processing") || txt.toLowerCase().contains("approved")
                        || txt.toLowerCase().contains("rejected")) {
                    return txt;
                }
            }
            return "UNKNOWN";
        }
    }

    /**
     * Return the application/lead number shown in the detail page header.
     */
    public String getApplicationNumber() {
        By appNoLocator = By.xpath(
                "//*[contains(normalize-space(.),'APP') and " +
                "(self::span or self::p or self::h1 or self::h2 or self::div) " +
                "and string-length(normalize-space(.)) < 20]");
        try {
            List<WebElement> els = driver.findElements(appNoLocator);
            for (WebElement el : els) {
                String t = el.getText().trim();
                if (t.startsWith("APP")) return t;
            }
        } catch (Exception ignored) {}
        return driver.getCurrentUrl();
    }

    // ════════════════════════════════════════════════════════════════════════════
    // DETAIL PAGE — APPROVE ACTION
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Click the "Approve" button on the detail page.
     * Waits for it to be clickable first.
     */
    public void clickApprove() {
        By approveLocator = By.xpath(
            "//button[contains(normalize-space(.),'Approve') and not(contains(normalize-space(.),'Update'))]");
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(approveLocator));
        jsClick(btn);
        System.out.println("[INFO] Clicked 'Approve' button.");
        sleep(1500);
    }

    /**
     * Return true if the Approve button is displayed and enabled.
     */
    public boolean isApproveButtonEnabled() {
        try {
            return approveButton.isDisplayed() && approveButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // DETAIL PAGE — UPDATE STATUS ACTION
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Click the "Update status" button to open the status-update modal.
     */
    public void clickUpdateStatus() {
        wait.until(ExpectedConditions.elementToBeClickable(updateStatusButton));
        scrollToElement(updateStatusButton);
        jsClick(updateStatusButton);
        System.out.println("[INFO] Clicked 'Update status' button.");
        waitForModalToOpen();
    }

    /**
     * Wait until the "Update Application status" modal is visible.
     */
    public void waitForModalToOpen() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='dialog'] | //div[contains(@class,'dialog')]")));
        System.out.println("[INFO] Status-update modal is visible.");
    }

    /**
     * Select a status from the Lead Status dropdown inside the modal.
     *
     * @param statusValue the option value or visible text to select
     */
    public void selectLeadStatus(String statusValue) {
        wait.until(ExpectedConditions.visibilityOf(leadStatusSelect));
        Select select = new Select(leadStatusSelect);
        try {
            select.selectByValue(statusValue);
        } catch (NoSuchElementException e) {
            select.selectByVisibleText(statusValue);
        }
        System.out.println("[INFO] Selected lead status: " + statusValue);
    }

    /**
     * Return all available status options from the Lead Status dropdown.
     */
    public List<String> getAvailableStatuses() {
        wait.until(ExpectedConditions.visibilityOf(leadStatusSelect));
        Select select = new Select(leadStatusSelect);
        return select.getOptions().stream()
                     .map(opt -> opt.getText().trim())
                     .filter(t -> !t.isEmpty())
                     .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Enter a reason/note in the modal textarea.
     *
     * @param reason text to enter
     */
    public void enterReason(String reason) {
        wait.until(ExpectedConditions.visibilityOf(reasonTextarea));
        reasonTextarea.clear();
        reasonTextarea.sendKeys(reason);
        System.out.println("[INFO] Entered reason: " + reason);
    }

    /**
     * Click the "Update Status" confirmation button inside the modal.
     */
    public void confirmUpdateStatus() {
        wait.until(ExpectedConditions.elementToBeClickable(modalUpdateStatusButton));
        jsClick(modalUpdateStatusButton);
        System.out.println("[INFO] Confirmed status update.");
        sleep(2000);
    }

    /**
     * Click the "Cancel" button inside the modal to dismiss it without saving.
     */
    public void cancelUpdateStatus() {
        wait.until(ExpectedConditions.elementToBeClickable(modalCancelButton));
        jsClick(modalCancelButton);
        System.out.println("[INFO] Cancelled status update.");
        sleep(500);
    }

    /**
     * Return true if the Update-Status modal is currently open/visible.
     */
    public boolean isModalOpen() {
        try {
            List<WebElement> dialogs = driver.findElements(
                    By.xpath("//div[@role='dialog'] | //div[contains(@class,'dialog')]"));
            return dialogs.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // DETAIL PAGE — TAB NAVIGATION
    // ════════════════════════════════════════════════════════════════════════════

    /** Click the "Borrower Details" tab and wait for content to load. */
    public void clickBorrowerDetailsTab() {
        clickTab(tabBorrowerDetails, "Borrower Details");
    }

    /** Click the "Asset details" tab and wait for content to load. */
    public void clickAssetDetailsTab() {
        clickTab(tabAssetDetails, "Asset Details");
    }

    /** Click the "Loan Details" tab and wait for content to load. */
    public void clickLoanDetailsTab() {
        clickTab(tabLoanDetails, "Loan Details");
    }

    /** Click the "Bank Details" tab and wait for content to load. */
    public void clickBankDetailsTab() {
        clickTab(tabBankDetails, "Bank Details");
    }

    /** Click the "Documents" tab and wait for content to load. */
    public void clickDocumentsTab() {
        clickTab(tabDocuments, "Documents");
    }

    /** Click the "CREDIT Report" tab and wait for content to load. */
    public void clickCreditReportTab() {
        clickTab(tabCreditReport, "CREDIT Report");
    }

    /** Click the "Deal parameters" tab and wait for content to load. */
    public void clickDealParametersTab() {
        clickTab(tabDealParameters, "Deal Parameters");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // DEAL PARAMETERS — EDIT FLOW
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Click the "Update parameters" button on the Deal parameters read view.
     * This switches the tab into edit mode and reveals the form fields
     * (Loan amount, Tenure, Interest, Estimated amount, Down payment, IRR%, and
     * Prefered repayment date).
     */
    public void clickUpdateParameters() {
        wait.until(ExpectedConditions.elementToBeClickable(updateParametersButton));
        scrollToElement(updateParametersButton);
        jsClick(updateParametersButton);
        sleep(1000);
        System.out.println("[INFO] Clicked 'Update parameters' — edit form is now open.");
    }

    /**
     * Enter the preferred repayment date in the Deal parameters edit form.
     *
     * <p>The browser renders a native date-picker with the format
     * {@code mm/dd/yyyy}. We set the value via JavaScript
     * ({@code YYYY-MM-DD} format required by the HTML date input's {@code value}
     * attribute), then fire a React-compatible {@code input} + {@code change}
     * event so the framework picks up the new value.
     *
     * <p>Example usage:
     * <pre>
     *   uwPage.enterPreferredRepaymentDate("2025-07-01");  // YYYY-MM-DD
     * </pre>
     *
     * @param dateYyyyMmDd date string in {@code YYYY-MM-DD} format, e.g. {@code "2025-07-01"}
     */
    public void enterPreferredRepaymentDate(String dateYyyyMmDd) {
        wait.until(ExpectedConditions.visibilityOf(preferredRepaymentDateInput));
        scrollToElement(preferredRepaymentDateInput);

        // Set value via JS to bypass native date-picker UI, then trigger events
        js.executeScript(
            "var el = arguments[0];" +
            "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(" +
            "    window.HTMLInputElement.prototype, 'value').set;" +
            "nativeInputValueSetter.call(el, arguments[1]);" +
            "el.dispatchEvent(new Event('input', { bubbles: true }));" +
            "el.dispatchEvent(new Event('change', { bubbles: true }));",
            preferredRepaymentDateInput, dateYyyyMmDd);

        System.out.println("[INFO] Set Preferred Repayment Date to: " + dateYyyyMmDd);
        sleep(500);
    }

    /**
     * Return the current value of the Preferred Repayment Date input
     * (as stored in the DOM — {@code YYYY-MM-DD} format).
     *
     * @return date string or empty string if not set
     */
    public String getPreferredRepaymentDateValue() {
        wait.until(ExpectedConditions.visibilityOf(preferredRepaymentDateInput));
        return preferredRepaymentDateInput.getAttribute("value");
    }

    /**
     * Click the "Save" button to persist the Deal parameters changes.
     * Waits up to 30 s for the button to become clickable, then waits an
     * additional 2 s for the server round-trip to complete.
     */
    public void clickSaveDealParameters() {
        wait.until(ExpectedConditions.elementToBeClickable(dealParamSaveButton));
        scrollToElement(dealParamSaveButton);
        jsClick(dealParamSaveButton);
        System.out.println("[INFO] Clicked 'Save' — Deal parameters saved.");
        sleep(2000);
    }

    /**
     * Click the "Cancel Update" button to discard changes and return to the
     * Deal parameters read view without saving.
     */
    public void clickCancelDealParameters() {
        wait.until(ExpectedConditions.elementToBeClickable(dealParamCancelButton));
        scrollToElement(dealParamCancelButton);
        jsClick(dealParamCancelButton);
        System.out.println("[INFO] Clicked 'Cancel Update' — edit discarded.");
        sleep(500);
    }

    /**
     * Convenience method: navigate to the Deal parameters tab, click
     * "Update parameters", set the preferred repayment date, and click "Save".
     *
     * @param dateYyyyMmDd date in {@code YYYY-MM-DD} format, e.g. {@code "2025-07-01"}
     */
    public void updatePreferredRepaymentDate(String dateYyyyMmDd) {
        clickDealParametersTab();
        clickUpdateParameters();
        enterPreferredRepaymentDate(dateYyyyMmDd);
        clickSaveDealParameters();
        System.out.println("[INFO] Preferred Repayment Date updated to: " + dateYyyyMmDd);
    }

    /** Click the "Tracking History" tab and wait for content to load. */
    public void clickTrackingHistoryTab() {
        clickTab(tabTrackingHistory, "Tracking History");
    }

    /** Click the "AI Insights" tab and wait for content to load. */
    public void clickAiInsightsTab() {
        clickTab(tabAiInsights, "AI Insights");
    }

    /**
     * Generic tab-click helper.
     */
    private void clickTab(WebElement tabButton, String tabName) {
        wait.until(ExpectedConditions.elementToBeClickable(tabButton));
        scrollToElement(tabButton);
        jsClick(tabButton);
        sleep(1000);
        System.out.println("[INFO] Clicked tab: " + tabName);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // DETAIL PAGE — FIELD READERS (Borrower Details tab)
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Read a labelled field value from the currently visible tab content.
     * Searches for a label element whose text contains {@code labelText} and
     * returns the adjacent value element's text.
     *
     * @param labelText partial or exact label text
     * @return field value text, or "NOT_FOUND"
     */
    public String getFieldValue(String labelText) {
        By locator = By.xpath(
                "//*[contains(normalize-space(.)='" + labelText + "')]" +
                "/following-sibling::*[1] | " +
                "//label[normalize-space(.)='" + labelText + "']/following-sibling::*[1] | " +
                "//*[normalize-space(text())='" + labelText + "']/../following-sibling::*[1]");
        try {
            List<WebElement> els = driver.findElements(locator);
            if (!els.isEmpty()) return els.get(0).getText().trim();
        } catch (Exception ignored) {}
        return "NOT_FOUND";
    }

    // ════════════════════════════════════════════════════════════════════════════
    // BANK DETAILS — ADD CUSTOMER BANK FLOW
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Click the "+ Add Customer bank" button on the Bank Details tab.
     * This opens the "Create bank" modal.
     */
    public void clickAddCustomerBank() {
        wait.until(ExpectedConditions.elementToBeClickable(addCustomerBankButton));
        scrollToElement(addCustomerBankButton);
        jsClick(addCustomerBankButton);
        sleep(1000);
        System.out.println("[INFO] Clicked 'Add Customer bank' — Create bank modal should open.");
    }

    /**
     * Return true if the Create-bank modal is currently visible.
     */
    public boolean isBankModalOpen() {
        try {
            List<WebElement> dialogs = driver.findElements(
                    By.xpath("//div[@role='dialog'] | //div[contains(@class,'dialog')]"));
            return dialogs.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fill in the Bank name field in the Create-bank modal.
     *
     * @param bankName e.g. "HDFC Bank"
     */
    public void fillBankName(String bankName) {
        wait.until(ExpectedConditions.visibilityOf(bankNameInput));
        bankNameInput.clear();
        bankNameInput.sendKeys(bankName);
        System.out.println("[INFO] Bank name: " + bankName);
    }

    /**
     * Fill in the Name as per cheque (account holder name) field.
     *
     * @param holderName e.g. "Ramesh Kumar"
     */
    public void fillNameAsPerCheque(String holderName) {
        wait.until(ExpectedConditions.visibilityOf(nameAsPerChequeInput));
        nameAsPerChequeInput.clear();
        nameAsPerChequeInput.sendKeys(holderName);
        System.out.println("[INFO] Name as per cheque: " + holderName);
    }

    /**
     * Fill in the Account number field.
     *
     * @param accountNumber e.g. "1234567890"
     */
    public void fillAccountNumber(String accountNumber) {
        wait.until(ExpectedConditions.visibilityOf(accountNumberInput));
        accountNumberInput.clear();
        accountNumberInput.sendKeys(accountNumber);
        System.out.println("[INFO] Account number: " + accountNumber);
    }

    /**
     * Fill in the Branch field.
     *
     * @param branch e.g. "Koramangala"
     */
    public void fillBranch(String branch) {
        wait.until(ExpectedConditions.visibilityOf(branchInput));
        branchInput.clear();
        branchInput.sendKeys(branch);
        System.out.println("[INFO] Branch: " + branch);
    }

    /**
     * Fill in the City field.
     *
     * @param city e.g. "Bangalore"
     */
    public void fillCity(String city) {
        wait.until(ExpectedConditions.visibilityOf(cityInput));
        cityInput.clear();
        cityInput.sendKeys(city);
        System.out.println("[INFO] City: " + city);
    }

    /**
     * Fill in the IFSC code field.
     *
     * @param ifscCode e.g. "HDFC0001234"
     */
    public void fillIfscCode(String ifscCode) {
        wait.until(ExpectedConditions.visibilityOf(ifscCodeInput));
        ifscCodeInput.clear();
        ifscCodeInput.sendKeys(ifscCode);
        System.out.println("[INFO] IFSC code: " + ifscCode);
    }

    /**
     * Select an account type from the Account type dropdown.
     * Tries by visible text first, then by value attribute.
     *
     * <p>Common values visible in the UI: {@code "SAVINGS"}, {@code "CURRENT"}, {@code "SALARY"}
     *
     * @param accountType visible text or value, e.g. {@code "SAVINGS"}
     */
    public void selectAccountType(String accountType) {
        wait.until(ExpectedConditions.visibilityOf(accountTypeSelect));
        Select select = new Select(accountTypeSelect);
        try {
            select.selectByVisibleText(accountType);
        } catch (NoSuchElementException e1) {
            try {
                select.selectByValue(accountType);
            } catch (NoSuchElementException e2) {
                // fallback: match by partial text via JS option search
                js.executeScript(
                    "var sel = arguments[0]; var txt = arguments[1];" +
                    "for(var i=0;i<sel.options.length;i++){" +
                    "  if(sel.options[i].text.toUpperCase().indexOf(txt.toUpperCase())>=0){" +
                    "    sel.selectedIndex=i;" +
                    "    sel.dispatchEvent(new Event('change',{bubbles:true}));" +
                    "    break; } }",
                    accountTypeSelect, accountType);
            }
        }
        System.out.println("[INFO] Account type selected: " + accountType);
    }

    /**
     * Return all available options from the Account type dropdown.
     */
    public List<String> getAccountTypeOptions() {
        wait.until(ExpectedConditions.visibilityOf(accountTypeSelect));
        Select select = new Select(accountTypeSelect);
        return select.getOptions().stream()
                     .map(o -> o.getText().trim())
                     .filter(t -> !t.isEmpty() && !t.equalsIgnoreCase("Select account type"))
                     .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Set the "Mark default" checkbox state.
     * The checkbox is checked by default when the modal opens.
     *
     * @param check {@code true} to check, {@code false} to uncheck
     */
    public void setMarkDefault(boolean check) {
        wait.until(ExpectedConditions.visibilityOf(markDefaultCheckbox));
        boolean isChecked = markDefaultCheckbox.isSelected();
        if (check != isChecked) {
            jsClick(markDefaultCheckbox);
            System.out.println("[INFO] Mark default checkbox toggled to: " + check);
        } else {
            System.out.println("[INFO] Mark default checkbox already set to: " + check);
        }
    }

    /**
     * Click the "Save" button inside the Create-bank modal to submit the form.
     * Waits 2 s after clicking for the server round-trip.
     */
    public void clickSaveBankDetails() {
        wait.until(ExpectedConditions.elementToBeClickable(bankModalSaveButton));
        scrollToElement(bankModalSaveButton);
        jsClick(bankModalSaveButton);
        System.out.println("[INFO] Clicked 'Save' — bank record submitted.");
        sleep(2000);
    }

    /**
     * Click the "Cancel" button inside the Create-bank modal to close it without saving.
     */
    public void clickCancelBankDetails() {
        wait.until(ExpectedConditions.elementToBeClickable(bankModalCancelButton));
        jsClick(bankModalCancelButton);
        System.out.println("[INFO] Clicked 'Cancel' — Create bank modal dismissed.");
        sleep(500);
    }

    /**
     * Return the number of bank rows currently shown in the Bank Details table.
     * Returns 0 when the table shows "No data found".
     */
    public int getBankTableRowCount() {
        List<WebElement> rows = driver.findElements(
                By.xpath("//table//tbody//tr[not(contains(@class,'empty') or .//td[normalize-space(.)=''])]"));
        // Fallback for tables without tbody
        if (rows.isEmpty()) {
            rows = driver.findElements(By.xpath("//table//tr[td]"));
        }
        // If the only content is the no-data icon return 0
        boolean noData = !driver.findElements(
                By.xpath("//*[contains(normalize-space(.),'No data found')]")).isEmpty();
        return noData ? 0 : rows.size();
    }

    /**
     * Convenience method: navigate to the Bank Details tab, click
     * "Add Customer bank", fill all mandatory fields, select account type,
     * optionally toggle the default checkbox, and click Save.
     *
     * @param bankName      e.g. "HDFC Bank"
     * @param holderName    e.g. "Ramesh Kumar"
     * @param accountNumber e.g. "1234567890"
     * @param branch        e.g. "Koramangala"
     * @param city          e.g. "Bangalore"
     * @param ifscCode      e.g. "HDFC0001234"
     * @param accountType   e.g. "SAVINGS"
     * @param markAsDefault whether to leave the 'Mark default' checkbox checked
     */
    public void addCustomerBank(String bankName, String holderName, String accountNumber,
                                String branch, String city, String ifscCode,
                                String accountType, boolean markAsDefault) {
        clickBankDetailsTab();
        clickAddCustomerBank();
        wait.until(ExpectedConditions.visibilityOf(bankNameInput));

        fillBankName(bankName);
        fillNameAsPerCheque(holderName);
        fillAccountNumber(accountNumber);
        fillBranch(branch);
        fillCity(city);
        fillIfscCode(ifscCode);
        selectAccountType(accountType);
        setMarkDefault(markAsDefault);
        clickSaveBankDetails();

        System.out.println("[INFO] Bank added — Name: " + bankName
                + " | Account: " + accountNumber + " | Type: " + accountType);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // SANCTION MAIL METHODS
    // ════════════════════════════════════════════════════════════════════════════

    /** Click the "Dispatch Sanction Mail" or "Re-Dispatch Sanction Mail" button. */
    public void clickDispatchSanctionMail() {
        wait.until(ExpectedConditions.elementToBeClickable(dispatchSanctionMailButton));
        scrollToElement(dispatchSanctionMailButton);
        jsClick(dispatchSanctionMailButton);
        System.out.println("[INFO] Clicked 'Dispatch/Re-Dispatch Sanction Mail' button.");
        sleep(1000);
    }

    /** Check if the Sanction Mail dialog/modal is open. */
    public boolean isSanctionMailDialogVisible() {
        try {
            WebElement title = driver.findElement(By.xpath("//div[@role='dialog']//*[normalize-space(.)='Sanction Mail']"));
            return title.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Click the "Send" button in the Sanction Mail confirmation dialog. */
    public void clickSendSanctionMail() {
        wait.until(ExpectedConditions.elementToBeClickable(sanctionMailDialogSendButton));
        jsClick(sanctionMailDialogSendButton);
        System.out.println("[INFO] Clicked 'Send' inside Sanction Mail confirmation dialog.");
        sleep(3000);
    }

    /** Click the "Cancel" button in the Sanction Mail confirmation dialog. */
    public void clickCancelSanctionMail() {
        wait.until(ExpectedConditions.elementToBeClickable(sanctionMailDialogCancelButton));
        jsClick(sanctionMailDialogCancelButton);
        System.out.println("[INFO] Clicked 'Cancel' inside Sanction Mail confirmation dialog.");
        sleep(1000);
    }

    /** Get the button text of the sanction mail button (to check if it changes from Dispatch to Re-Dispatch). */
    public String getDispatchSanctionMailButtonText() {
        try {
            return dispatchSanctionMailButton.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Retrieve text of the sent timestamp labels next to the button (if present). */
    public String getSanctionMailSentStatusText() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath(
                "//*[contains(text(), 'First sent at:') or contains(text(), 'Last sent at:')]"));
            if (!elements.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (WebElement el : elements) {
                    sb.append(el.getText().trim()).append(" | ");
                }
                return sb.toString();
            }
        } catch (Exception e) {
            // ignore
        }
        return "";
    }

    // ════════════════════════════════════════════════════════════════════════════
    // WAIT HELPERS
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Wait until the table body is populated with at least one row,
     * OR the "no results" message appears.
     */
    public void waitForTableToLoad() {
        wait.until(d ->
                !d.findElements(By.xpath("//table//tbody//tr")).isEmpty()
                || !d.findElements(By.xpath(
                        "//*[contains(normalize-space(.),'Sorry') and " +
                        "contains(normalize-space(.),\"couldn't find\")]")).isEmpty()
        );
    }

    /**
     * Wait until the detail page header summary (status badge + Approve button) is visible.
     */
    public void waitForDetailPageToLoad() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//button[contains(normalize-space(.),'Approve') and not(contains(normalize-space(.),'Update'))]")),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//button[contains(normalize-space(.),'Update status') or contains(normalize-space(.),'Update Status')]"))
        ));
        System.out.println("[INFO] Underwriting detail page loaded. URL: " + driver.getCurrentUrl());
    }

    // ════════════════════════════════════════════════════════════════════════════
    // PRIVATE UTILITIES
    // ════════════════════════════════════════════════════════════════════════════

    private void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    private void scrollToElement(WebElement element) {
        try {
            js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'});", element);
            sleep(300);
        } catch (Exception ignored) {}
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }
}
