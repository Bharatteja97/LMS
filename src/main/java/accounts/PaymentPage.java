package accounts;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the Payment tab of the Account Details page.
 *
 * URL pattern: /lms/accounts/{id}?tab=payment&product=VEHICLE_LOAN
 *
 * Covers:
 *  - Payment History section (sub-tabs, table, Pay button)
 *  - Staged Repayments sub-tab (table, status filter, Approve/Reject actions)
 *  - Make Payment modal (all fields + Submit / Cancel)
 */
public class PaymentPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Section header ────────────────────────────────────────────────────────

    /** "PAYMENT HISTORY" heading — uses contains() to tolerate subtitle/icon sibling text */
    @FindBy(xpath = "(//*[contains(normalize-space(.),'PAYMENT HISTORY') and not(.//*[contains(normalize-space(.),'PAYMENT HISTORY')])])[1]")
    private WebElement paymentHistoryHeading;

    // ── Sub-tabs ──────────────────────────────────────────────────────────────

    /** "Payment History" sub-tab button */
    @FindBy(xpath = "//*[normalize-space(.)='Payment History' and not(.//*[normalize-space(.)='Payment History'])]")
    private WebElement paymentHistorySubTab;

    /**
     * "Staged Repayments (N)" sub-tab button.
     * Uses contains() to tolerate the count suffix like "(0)" or "(1)".
     */
    @FindBy(xpath = "//*[contains(normalize-space(.),'Staged Repayments') and not(.//*[contains(normalize-space(.),'Staged Repayments')])]")
    private WebElement stagedRepaymentsSubTab;

    // ── Pay button ────────────────────────────────────────────────────────────

    /**
     * "Pay" button that opens the Make Payment modal.
     * Scoped by the actual CSS classes from the DOM: bg-primary + h-7 uniquely identify
     * this small primary-style button, avoiding matches on Submit/Cancel which are larger.
     */
    @FindBy(xpath = "//button[contains(@class,'bg-primary') and contains(@class,'h-7') and contains(normalize-space(.),'Pay')]")
    private WebElement payButton;

    // ── Payment History table ─────────────────────────────────────────────────

    /**
     * All body rows in the Payment History table.
     * We use [td[3]] to ensure we only match data rows (which have at least 3 columns)
     * and ignore "Loading..." or "No data" rows that use a single td with colspan.
     */
    @FindBy(xpath = "//table[.//th[normalize-space(.)='Txn ID']]//tbody/tr[td[3]]")
    private List<WebElement> paymentHistoryRows;

    // ── Staged Repayments table ───────────────────────────────────────────────

    /**
     * All body rows in the Staged Repayments table.
     * The table header contains "Request ID".
     */
    @FindBy(xpath = "//table[.//th[normalize-space(.)='Request ID']]//tbody/tr")
    private List<WebElement> stagedRepaymentRows;

    // ── Make Payment modal ────────────────────────────────────────────────────

    /** Modal title "Make Payment" */
    @FindBy(xpath = "(//*[normalize-space(.)='Make Payment' and not(.//*[normalize-space(.)='Make Payment'])])[1]")
    private WebElement modalTitle;

    /** Close (×) button of the Make Payment modal */
    @FindBy(xpath = "(//button[@aria-label='Close' or @aria-label='close' or normalize-space(.)='\u00d7' or normalize-space(.)='x'])[1]")
    private WebElement modalCloseButton;

    // ── Modal – payment flow options ──────────────────────────────────────────

    /** "Unified Payment Flow" radio / option card */
    @FindBy(xpath = "(//*[contains(normalize-space(.),'Unified Payment Flow') and not(.//*[contains(normalize-space(.),'Unified Payment Flow')])])[1]")
    private WebElement unifiedPaymentFlowOption;

    /** "Mark as Single Payment" checkbox */
    @FindBy(xpath = "//input[@type='checkbox'][following-sibling::*[contains(normalize-space(.),'Mark as Single Payment')] or preceding-sibling::*[contains(normalize-space(.),'Mark as Single Payment')]][1]")
    private WebElement markAsSinglePaymentCheckbox;

    // ── Modal – RBI Waterfall Due Breakdown ───────────────────────────────────

    /**
     * Due date badge inside the RBI Waterfall section, e.g. "Due 03 Jul 2026".
     * Scoped to the breakdown section to avoid matching unrelated "Due" text.
     */
    @FindBy(xpath = "(//*[contains(normalize-space(.),'RBI WATERFALL') or contains(normalize-space(.),'DUE BREAKDOWN')]//following-sibling::*[contains(normalize-space(.),'Due ')] | //*[contains(@class,'due') or contains(@class,'badge')][contains(normalize-space(.),'Due ')])[1]")
    private WebElement dueDateBadge;

    /** Scheduled EMI value cell, e.g. "\u20b92,806.64" */
    @FindBy(xpath = "(//*[normalize-space(text())='Scheduled EMI']/following-sibling::*[1])[1]")
    private WebElement scheduledEMIValue;

    /** Outstanding Charges value cell, e.g. "\u20b9222" */
    @FindBy(xpath = "(//*[normalize-space(text())='Outstanding Charges']/following-sibling::*[1])[1]")
    private WebElement outstandingChargesValue;

    /** Interest Component value cell, e.g. "\u20b9107.4" */
    @FindBy(xpath = "(//*[normalize-space(text())='Interest Component']/following-sibling::*[1])[1]")
    private WebElement interestComponentValue;

    /** Principal Component value cell, e.g. "\u20b92,699.24" */
    @FindBy(xpath = "(//*[normalize-space(text())='Principal Component']/following-sibling::*[1])[1]")
    private WebElement principalComponentValue;

    /** Already Paid value cell, e.g. "\u20b90" */
    @FindBy(xpath = "(//*[normalize-space(text())='Already Paid']/following-sibling::*[1])[1]")
    private WebElement alreadyPaidValue;

    /** Remaining Due value cell, e.g. "\u20b92,806.64" */
    @FindBy(xpath = "(//*[normalize-space(text())='Remaining Due']/following-sibling::*[1])[1]")
    private WebElement remainingDueValue;

    /**
     * Suggested Payment Amount value, e.g. "\u20b9222".
     * The label and subtitle ("EMI Remaining + Upfront Charges") are inside a left column div.
     * The value (\u20b9222) is in the sibling right column. We go up to the label's parent and
     * then take its first following-sibling to reach the value column.
     */
    @FindBy(xpath = "(//*[normalize-space(text())='Suggested Payment Amount']/parent::*/following-sibling::*)[1]")
    private WebElement suggestedPaymentAmountValue;

    /**
     * Max Payment Limit value, e.g. "\u20b975,844.58".
     * Same two-column layout as Suggested Payment Amount \u2014 label+subtitle in left column,
     * value in the sibling right column.
     */
    @FindBy(xpath = "(//*[normalize-space(text())='Max Payment Limit']/parent::*/following-sibling::*)[1]")
    private WebElement maxPaymentLimitValue;

    // ── Modal – payment form fields ───────────────────────────────────────────

    /** Payment Amount input field */
    @FindBy(xpath = "(//input[@name='paymentAmount' or @id='paymentAmount' or @placeholder='0.00'])[1]")
    private WebElement paymentAmountInput;

    /**
     * Repayment Mode field trigger — broad match on label text + any combobox/select.
     */
    @FindBy(xpath = "(//*[contains(normalize-space(text()),'Repayment') and (contains(normalize-space(text()),'Mode') or contains(normalize-space(text()),'mode'))]/following::*[self::select or @role='combobox' or @aria-haspopup='listbox' or @aria-haspopup='true'])[1]")
    private WebElement repaymentModeTrigger;

    /**
     * Prepay Option field trigger — Radix/shadcn combobox button scoped after its label.
     */
    @FindBy(xpath = "(//*[normalize-space(text())='Prepay Option' or normalize-space(text())='Prepay option']/following::button[@role='combobox' or @aria-haspopup='listbox' or @aria-haspopup='true'])[1]")
    private WebElement prepayOptionTrigger;

    /** Value Date input (type=date or date-picker input) */
    @FindBy(xpath = "(//input[@type='date' or @name='valueDate' or @id='valueDate'])[1]")
    private WebElement valueDateInput;

    /**
     * Authorized By field trigger — broad match on label, any combobox/select/button after it.
     */
    @FindBy(xpath = "(//*[contains(normalize-space(text()),'Authorized') and (contains(normalize-space(text()),'By') or contains(normalize-space(text()),'by'))]/following::*[self::select or @role='combobox' or @aria-haspopup='listbox' or @aria-haspopup='true'])[1]")
    private WebElement authorizedByTrigger;

    /** Instrument / UTR # input */
    @FindBy(xpath = "(//input[@placeholder='Cheque/Txn ref' or @name='instrumentUTR' or @id='instrumentUTR'])[1]")
    private WebElement instrumentUTRInput;

    /** Bank Reference # input */
    @FindBy(xpath = "(//input[@placeholder='Bank ref number' or @name='bankReference' or @id='bankReference'])[1]")
    private WebElement bankReferenceInput;

    /**
     * Discount Amount input.
     * Scoped after the "Discount Amount" label to avoid matching the Payment Amount field.
     */
    @FindBy(xpath = "(//*[normalize-space(text())='Discount Amount']/following::input[1])[1]")
    private WebElement discountAmountInput;

    /** Discount Remark input */
    @FindBy(xpath = "(//input[@placeholder='Enter discount remark' or @name='discountRemark' or @id='discountRemark'])[1]")
    private WebElement discountRemarkInput;

    /** Remarks textarea */
    @FindBy(xpath = "(//textarea[@placeholder='Enter payment remarks...' or @name='remarks' or @id='remarks'])[1]")
    private WebElement remarksTextarea;

    // ── Modal – action buttons ────────────────────────────────────────────────

    /** Cancel button inside the Make Payment modal */
    @FindBy(xpath = "(//button[normalize-space(.)='Cancel' and not(.//*[normalize-space(.)='Cancel'])])[1]")
    private WebElement cancelButton;

    /** Submit Payment button */
    @FindBy(xpath = "(//button[normalize-space(.)='Submit Payment' and not(.//*[normalize-space(.)='Submit Payment'])])[1]")
    private WebElement submitPaymentButton;

    // ─────────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────────

    public PaymentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Page-load check
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} when the PAYMENT HISTORY heading is visible, confirming
     * the Payment tab has fully loaded.
     */
    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOf(paymentHistoryHeading));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns {@code true} when the "Payment History" sub-tab is visible,
     * confirming the PAYMENT HISTORY section has rendered.
     * The sub-tab is more reliably located than the section heading because
     * heading text may be uppercased via CSS (actual DOM text is "Payment History").
     */
    public boolean isPaymentHistoryVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOf(paymentHistorySubTab));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sub-tab navigation
    // ─────────────────────────────────────────────────────────────────────────

    /** Click the "Payment History" sub-tab. */
    public PaymentPage clickPaymentHistorySubTab() {
        wait.until(ExpectedConditions.elementToBeClickable(paymentHistorySubTab)).click();
        return this;
    }

    /** Click the "Staged Repayments" sub-tab. */
    public PaymentPage clickStagedRepaymentsSubTab() {
        wait.until(ExpectedConditions.elementToBeClickable(stagedRepaymentsSubTab)).click();
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Payment History table
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Waits for the table to stop loading.
     * Assumes the table shows a spinner while loading.
     */
    public void waitForTableToLoad() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
            By.xpath("//table//tbody//tr//td[1]//*[local-name()='svg' or contains(@class,'spin') or contains(@class,'loader')]")
        ));
    }

    /** Returns the number of rows in the Payment History table body. */
    public int getPaymentHistoryRowCount() {
        wait.until(ExpectedConditions.visibilityOf(paymentHistorySubTab));
        waitForTableToLoad();
        return paymentHistoryRows.size();
    }

    /**
     * Returns the text of every cell in the specified Payment History row (0-indexed).
     * Column order: Serial | Txn ID | Description | Category/Mode | Status | Amount | Date | Remarks | Action
     */
    public List<String> getPaymentHistoryRowData(int rowIndex) {
        waitForTableToLoad();
        WebElement row = paymentHistoryRows.get(rowIndex);
        return row.findElements(By.tagName("td"))
                  .stream()
                  .map(WebElement::getText)
                  .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Staged Repayments table
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns the number of rows in the Staged Repayments table body. */
    public int getStagedRepaymentRowCount() {
        // Wait for at least the sub-tab to be present, then give the table time to render.
        wait.until(ExpectedConditions.elementToBeClickable(stagedRepaymentsSubTab));
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//table[.//th[normalize-space(.)='Request ID']]")));
        return stagedRepaymentRows.size();
    }

    /**
     * Returns the text of every cell in the specified Staged Repayments row (0-indexed).
     * Column order: Serial | Request ID | Amount | Mode | Value Date | Maker Remark | Checker Remark | Status | Actions
     */
    public List<String> getStagedRepaymentRowData(int rowIndex) {
        WebElement row = stagedRepaymentRows.get(rowIndex);
        return row.findElements(By.tagName("td"))
                  .stream()
                  .map(WebElement::getText)
                  .collect(Collectors.toList());
    }

    /**
     * Returns the status text of a Staged Repayments row (e.g. "Pending", "Approved").
     * Status is typically in the 8th cell (index 7).
     */
    public String getStagedRepaymentStatus(int rowIndex) {
        List<String> data = getStagedRepaymentRowData(rowIndex);
        return data.size() > 7 ? data.get(7).trim() : "";
    }

    // ── Three-dot action menu ─────────────────────────────────────────────────

    /**
     * Clicks the "…" (three-dot) action button in the given Staged Repayments row
     * to open the Approve / Reject dropdown.
     */
    public PaymentPage clickStagedRepaymentActionMenu(int rowIndex) {
        WebElement row = wait.until(ExpectedConditions.visibilityOf(stagedRepaymentRows.get(rowIndex)));
        // Try specific selectors first; fall back to the last button in the row (the "..." icon button)
        WebElement actionBtn;
        try {
            actionBtn = row.findElement(By.xpath(
                ".//button[normalize-space(.)='...' or normalize-space(.)='…' "
                + "or @aria-label='More' or @aria-label='Open menu' or @aria-label='Actions' "
                + "or @aria-haspopup='menu' or contains(@class,'action') or contains(@class,'more')]"));
        } catch (Exception e) {
            // Fall back: last button in the row is the "..." actions menu button
            actionBtn = row.findElement(By.xpath("(.//button)[last()]"));
        }
        wait.until(ExpectedConditions.elementToBeClickable(actionBtn)).click();
        return this;
    }

    /**
     * Clicks "Approve" from the "..." action dropdown of the given row, fills the
     * required Remarks field in the confirmation modal, then clicks the modal Approve button.
     */
    public PaymentPage approveStagedRepayment(int rowIndex) {
        return approveStagedRepayment(rowIndex, "Approved");
    }

    public PaymentPage approveStagedRepayment(int rowIndex, String remarks) {
        clickStagedRepaymentActionMenu(rowIndex);
        // Allow the Radix UI DropdownMenu portal to render
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        // Click "Approve" from the dropdown menu
        WebElement approveOption = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//*[@role='menuitem' and contains(normalize-space(.),'Approve')] "
                   + "| //button[contains(normalize-space(.),'Approve')] "
                   + "| //*[contains(@class,'approve') or contains(@class,'Approve')])[1]")));
        approveOption.click();
        // Confirmation modal appears with a required Remarks field
        WebElement remarksField = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//textarea[contains(@placeholder,'remarks') or contains(@placeholder,'Remarks') "
                   + "or @name='remarks' or @id='remarks'] "
                   + "| //input[contains(@placeholder,'remarks') or @name='remarks' or @id='remarks']")));
        remarksField.clear();
        remarksField.sendKeys(remarks);
        // Click the Approve button in the confirmation modal footer
        WebElement confirmApprove = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//button[contains(normalize-space(.),'Approve') and not(@disabled)])[last()]")));
        confirmApprove.click();
        return this;
    }

    /** Clicks "Reject" from the action dropdown of the given Staged Repayments row. */
    public PaymentPage rejectStagedRepayment(int rowIndex) {
        clickStagedRepaymentActionMenu(rowIndex);
        WebElement rejectOption = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[normalize-space(.)='Reject' and not(.//*[normalize-space(.)='Reject'])]")));
        rejectOption.click();
        return this;
    }

    /** Clicks the "Details" link in the given Staged Repayments row. */
    public PaymentPage clickStagedRepaymentDetails(int rowIndex) {
        WebElement row = wait.until(ExpectedConditions.visibilityOf(stagedRepaymentRows.get(rowIndex)));
        WebElement detailsLink = row.findElement(
            By.xpath(".//a[normalize-space(.)='Details'] | .//button[normalize-space(.)='Details']"));
        wait.until(ExpectedConditions.elementToBeClickable(detailsLink)).click();
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Pay button → opens Make Payment modal
    // ─────────────────────────────────────────────────────────────────────────

    /** Clicks the "Pay" button and waits for the Make Payment modal to appear. */
    public PaymentPage clickPayButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(payButton));
        try {
            btn.click();
        } catch (Exception e) {
            // Fallback: JS click when the element is intercepted by an overlay
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        wait.until(ExpectedConditions.visibilityOf(modalTitle));
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Make Payment modal – getters (RBI Waterfall breakdown)
    // ─────────────────────────────────────────────────────────────────────────

    public String getModalTitle() {
        return wait.until(ExpectedConditions.visibilityOf(modalTitle)).getText().trim();
    }

    /** e.g. "Due 03 Jul 2026" */
    public String getDueDateBadge() {
        return wait.until(ExpectedConditions.visibilityOf(dueDateBadge)).getText().trim();
    }

    /** e.g. "₹2,806.64" */
    public String getScheduledEMI() {
        return wait.until(ExpectedConditions.visibilityOf(scheduledEMIValue)).getText().trim();
    }

    /** e.g. "₹222" */
    public String getOutstandingCharges() {
        return wait.until(ExpectedConditions.visibilityOf(outstandingChargesValue)).getText().trim();
    }

    /** e.g. "₹107.4" */
    public String getInterestComponent() {
        return wait.until(ExpectedConditions.visibilityOf(interestComponentValue)).getText().trim();
    }

    /** e.g. "₹2,699.24" */
    public String getPrincipalComponent() {
        return wait.until(ExpectedConditions.visibilityOf(principalComponentValue)).getText().trim();
    }

    /** e.g. "₹0" */
    public String getAlreadyPaid() {
        return wait.until(ExpectedConditions.visibilityOf(alreadyPaidValue)).getText().trim();
    }

    /** e.g. "₹2,806.64" */
    public String getRemainingDue() {
        return wait.until(ExpectedConditions.visibilityOf(remainingDueValue)).getText().trim();
    }

    /** Suggested Payment Amount (EMI Remaining + Upfront Charges), e.g. "₹222" */
    public String getSuggestedPaymentAmount() {
        return wait.until(ExpectedConditions.visibilityOf(suggestedPaymentAmountValue)).getText().trim();
    }

    /** Max Payment Limit (cap for this payment cycle), e.g. "₹75,844.58" */
    public String getMaxPaymentLimit() {
        return wait.until(ExpectedConditions.visibilityOf(maxPaymentLimitValue)).getText().trim();
    }

    /**
     * Clicks the Scheduled EMI value cell in the breakdown to auto-populate
     * the Payment Amount input with the scheduled EMI amount.
     */
    public PaymentPage clickScheduledEMIAmount() {
        wait.until(ExpectedConditions.elementToBeClickable(scheduledEMIValue)).click();
        return this;
    }

    /**
     * Returns the current value attribute of the Payment Amount input field.
     * Use after {@link #clickScheduledEMIAmount()} to verify it was populated.
     */
    public String getPaymentAmountValue() {
        return wait.until(ExpectedConditions.visibilityOf(paymentAmountInput)).getAttribute("value");
    }

    /**
     * Selects the first non-placeholder option in the Authorized By dropdown.
     * Works for both native {@code <select>} and custom React dropdowns.
     */
    public PaymentPage selectFirstAuthorizedBy() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(authorizedByTrigger));
        if ("select".equalsIgnoreCase(el.getTagName())) {
            Select select = new Select(el);
            List<WebElement> options = select.getOptions();
            int idx = (options.size() > 1 && options.get(0).getAttribute("value").isEmpty()) ? 1 : 0;
            select.selectByIndex(idx);
        } else {
            el.click();
            WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//*[@role='option' or contains(@class,'option') or contains(@class,'item')])[1]")));
            firstOption.click();
        }
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Make Payment modal – form interactions
    // ─────────────────────────────────────────────────────────────────────────

    /** Selects the "Unified Payment Flow" option if not already selected. */
    public PaymentPage selectUnifiedPaymentFlow() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(unifiedPaymentFlowOption));
        // The card may be a div/label, not a radio — clicking it always toggles selection.
        el.click();
        return this;
    }

    /**
     * Ticks or un-ticks the "Mark as Single Payment" checkbox.
     *
     * @param check {@code true} to check, {@code false} to uncheck
     */
    public PaymentPage setMarkAsSinglePayment(boolean check) {
        WebElement cb = wait.until(ExpectedConditions.elementToBeClickable(markAsSinglePaymentCheckbox));
        boolean isChecked = Boolean.parseBoolean(cb.getAttribute("checked")) || cb.isSelected();
        if (check != isChecked) {
            cb.click();
        }
        return this;
    }

    /**
     * Clears and enters the Payment Amount.
     *
     * @param amount numeric string, e.g. "2806.64"
     */
    public PaymentPage enterPaymentAmount(String amount) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(paymentAmountInput));
        input.clear();
        input.sendKeys(amount);
        return this;
    }

    /**
     * Selects the Repayment Mode.
     * Handles both native HTML {@code <select>} and custom React dropdown components.
     *
     * @param mode visible text of the option, e.g. "RTGS", "NEFT", "Cash"
     */
    public PaymentPage selectRepaymentMode(String mode) {
        selectDropdown(repaymentModeTrigger, mode);
        return this;
    }

    /**
     * Selects the Prepay Option.
     * Handles both native HTML {@code <select>} and custom React dropdown components.
     *
     * @param option visible text, e.g. "Reduce Tenure", "Reduce EMI"
     */
    public PaymentPage selectPrepayOption(String option) {
        selectDropdown(prepayOptionTrigger, option);
        return this;
    }

    /**
     * Sets the Value Date.
     *
     * @param date in MM/DD/YYYY format, e.g. "06/28/2026"
     */
    public PaymentPage enterValueDate(String date) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(valueDateInput));
        input.clear();
        input.sendKeys(date);
        return this;
    }

    /**
     * Selects the Authorized By user.
     * Handles both native HTML {@code <select>} and custom React dropdown components.
     *
     * @param user visible text of the option
     */
    public PaymentPage selectAuthorizedBy(String user) {
        selectDropdown(authorizedByTrigger, user);
        return this;
    }

    /**
     * Enters the Instrument / UTR reference number.
     *
     * @param utr e.g. "UTR123456789"
     */
    public PaymentPage enterInstrumentUTR(String utr) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(instrumentUTRInput));
        input.clear();
        input.sendKeys(utr);
        return this;
    }

    /**
     * Enters the Bank Reference number.
     *
     * @param ref e.g. "BANKREF001"
     */
    public PaymentPage enterBankReference(String ref) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(bankReferenceInput));
        input.clear();
        input.sendKeys(ref);
        return this;
    }

    /**
     * Enters the Discount Amount.
     *
     * @param amount e.g. "0.00" or "50"
     */
    public PaymentPage enterDiscountAmount(String amount) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(discountAmountInput));
        input.clear();
        input.sendKeys(amount);
        return this;
    }

    /**
     * Enters the Discount Remark.
     *
     * @param remark e.g. "Waiver approved by manager"
     */
    public PaymentPage enterDiscountRemark(String remark) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(discountRemarkInput));
        input.clear();
        input.sendKeys(remark);
        return this;
    }

    /**
     * Enters free-text payment remarks.
     *
     * @param remarks remark text
     */
    public PaymentPage enterRemarks(String remarks) {
        WebElement ta = wait.until(ExpectedConditions.elementToBeClickable(remarksTextarea));
        ta.clear();
        ta.sendKeys(remarks);
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Make Payment modal – submit / cancel
    // ─────────────────────────────────────────────────────────────────────────

    /** Clicks "Submit Payment" to submit the payment. */
    public PaymentPage clickSubmitPayment() {
        wait.until(ExpectedConditions.elementToBeClickable(submitPaymentButton)).click();
        return this;
    }

    /** Clicks "Cancel" to dismiss the modal without submitting. */
    public PaymentPage clickCancelPayment() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
        return this;
    }

    /** Dismisses the modal via Escape key (the X button has no accessible label). */
    public PaymentPage closeModal() {
        driver.findElement(By.tagName("body")).sendKeys(org.openqa.selenium.Keys.ESCAPE);
        wait.until(ExpectedConditions.elementToBeClickable(payButton));
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Convenience – end-to-end payment submission
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Opens the Make Payment modal, fills in all mandatory fields and submits.
     * Pass {@code null} or empty string for optional fields to skip them.
     *
     * @param paymentAmount   e.g. "2806.64"
     * @param repaymentMode   e.g. "RTGS"
     * @param valueDate       e.g. "06/28/2026"
     * @param instrumentUTR   (optional) Instrument / UTR #
     * @param bankReference   (optional) Bank Reference #
     * @param remarks         (optional) remarks text
     * @return this PaymentPage instance
     */
    public PaymentPage submitPayment(String paymentAmount,
                                     String repaymentMode,
                                     String valueDate,
                                     String instrumentUTR,
                                     String bankReference,
                                     String remarks) {
        clickPayButton();
        enterPaymentAmount(paymentAmount);
        selectRepaymentMode(repaymentMode);
        enterValueDate(valueDate);

        if (instrumentUTR != null && !instrumentUTR.isEmpty()) {
            enterInstrumentUTR(instrumentUTR);
        }
        if (bankReference != null && !bankReference.isEmpty()) {
            enterBankReference(bankReference);
        }
        if (remarks != null && !remarks.isEmpty()) {
            enterRemarks(remarks);
        }

        clickSubmitPayment();
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Generic dropdown helper that works for both native {@code <select>} elements
     * and custom React/div dropdown components.
     *
     * <ol>
     *   <li>If the trigger is a native {@code <select>}, uses {@link Select#selectByVisibleText}.</li>
     *   <li>Otherwise, clicks the trigger to open the dropdown, then clicks the option
     *       whose visible text matches {@code optionText}.</li>
     * </ol>
     *
     * @param trigger    the field trigger element (select or custom dropdown container)
     * @param optionText the visible text of the option to select
     */
    private void selectDropdown(WebElement trigger, String optionText) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(trigger));
        if ("select".equalsIgnoreCase(el.getTagName())) {
            new Select(el).selectByVisibleText(optionText);
        } else {
            // Custom dropdown — click to open, then click matching option
            el.click();
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[normalize-space(.)='" + optionText
                    + "' and not(.//*[normalize-space(.)='" + optionText + "'])]"
                    + "[last()]")
            ));
            option.click();
        }
    }
}
