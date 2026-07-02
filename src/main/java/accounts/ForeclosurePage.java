package accounts;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ForeclosurePage {
    private WebDriver driver;
    private WebDriverWait wait;

    // ── Modal title ──────────────────────────────────────────────────────────
    @FindBy(xpath = "(//*[contains(normalize-space(.), 'Loan Foreclosure Payoff') and not(.//*[contains(normalize-space(.), 'Loan Foreclosure Payoff')])])[1]")
    private WebElement modalTitle;

    // ── Summary view fields ──────────────────────────────────────────────────
    @FindBy(xpath = "(//*[normalize-space(text())='Loan Account Number']/following-sibling::* | //*[normalize-space(text())='Loan Account Number']/parent::*/following-sibling::*//p | //*[normalize-space(text())='Loan Account Number']/following::*[not(self::label)][1])[1]")
    private WebElement loanAccountNumber;

    @FindBy(xpath = "(//*[normalize-space(text())='Current Loan Status']/following-sibling::* | //*[normalize-space(text())='Current Loan Status']/parent::*/following-sibling::*//span | //*[normalize-space(text())='Current Loan Status']/following::*[not(self::label)][1])[1]")
    private WebElement currentLoanStatus;

    @FindBy(xpath = "(//*[normalize-space(text())='Outstanding Principal']/following-sibling::* | //*[normalize-space(text())='Outstanding Principal']/parent::*/following-sibling::*//p | //*[normalize-space(text())='Outstanding Principal']/following::*[not(self::label)][1])[1]")
    private WebElement outstandingPrincipal;

    @FindBy(xpath = "(//*[normalize-space(text())='Total Net Dues']/following-sibling::* | //*[normalize-space(text())='Total Net Dues']/parent::*/following-sibling::*//p | //*[normalize-space(text())='Total Net Dues']/following::*[not(self::label)][1])[1]")
    private WebElement totalNetDues;

    // ── Execute Foreclosure Payment form ─────────────────────────────────────
    @FindBy(xpath = "//select[ancestor::*[contains(normalize-space(.), 'Repayment Mode')]] | //*[normalize-space(text())='Repayment Mode']/following::select[1]")
    private WebElement repaymentModeSelect;

    @FindBy(xpath = "//*[@placeholder='Enter bank reference'] | //*[contains(normalize-space(.), 'Bank UTR')]/following::input[1]")
    private WebElement bankUtrInput;

    @FindBy(xpath = "//*[@placeholder='Enter details / reason for foreclosure...'] | //*[normalize-space(text())='Remarks']/following::textarea[1]")
    private WebElement remarksTextarea;

    @FindBy(xpath = "//button[contains(normalize-space(.), 'Confirm') and contains(normalize-space(.), 'Foreclose')]")
    private WebElement confirmAndForecloseBtn;

    public ForeclosurePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    // ── State detection ──────────────────────────────────────────────────────

    public String getModalTitle() {
        return wait.until(ExpectedConditions.visibilityOf(modalTitle)).getText().trim();
    }

    public boolean isModalOpened() {
        try {
            wait.until(ExpectedConditions.visibilityOf(modalTitle));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Summary view: initial state before any quote is generated. */
    public boolean isOnSummaryView() {
        try {
            return driver.findElement(
                By.xpath("//button[contains(normalize-space(.), 'Generate Quote') and not(contains(normalize-space(.), 'New'))]"))
                .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * History view: at least one FCL- quote is visible, or the FORECLOSURE QUOTES HISTORY
     * heading is present. Avoids depending solely on the heading which can flash during load.
     */
    public boolean isOnHistoryView() {
        try {
            return driver.findElement(By.xpath(
                "(//*[contains(normalize-space(.), 'FORECLOSURE QUOTES HISTORY')])[1]" +
                " | (//*[contains(normalize-space(.), 'FCL-') and not(.//*[contains(normalize-space(.), 'FCL-')])])[1]" +
                " | (//button[contains(normalize-space(.), 'Foreclose') and not(contains(normalize-space(.), 'Confirm'))])[1]"))
                .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Payment form view: "Back to Quotes History" link is visible. */
    public boolean isOnPaymentFormView() {
        try {
            return driver.findElement(
                By.xpath("(//*[contains(normalize-space(.), 'Back to Quotes History')])[1]"))
                .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Navigation helpers ───────────────────────────────────────────────────

    /**
     * Closes the modal using Escape key (safe from any view).
     * Falls back to clicking an explicit aria-label="Close" button only.
     */
    public void clickClose() {
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        } catch (Exception e) {
            try {
                driver.findElement(By.xpath(
                    "//div[@role='dialog']//button[@aria-label='Close' or @aria-label='close']"))
                    .click();
            } catch (Exception ex) { /* modal may already be gone */ }
        }
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.invisibilityOf(modalTitle));
        } catch (Exception ignored) {}
    }

    /**
     * If on the payment form view, navigates back to the quotes history view.
     * No-op if already on history or summary view.
     */
    public void backToHistoryIfOnPaymentForm() {
        if (isOnPaymentFormView()) {
            WebElement back = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//*[contains(normalize-space(.), 'Back to Quotes History')])[1]")));
            back.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//*[contains(normalize-space(.), 'FORECLOSURE QUOTES HISTORY')])[1]")));
        }
    }

    // ── Summary view getters ─────────────────────────────────────────────────

    public String getLoanAccountNumber() {
        return wait.until(ExpectedConditions.visibilityOf(loanAccountNumber)).getText().trim();
    }

    public String getCurrentLoanStatus() {
        return wait.until(ExpectedConditions.visibilityOf(currentLoanStatus)).getText().trim();
    }

    public String getOutstandingPrincipal() {
        return wait.until(ExpectedConditions.visibilityOf(outstandingPrincipal)).getText().trim();
    }

    public String getTotalNetDues() {
        return wait.until(ExpectedConditions.visibilityOf(totalNetDues)).getText().trim();
    }

    // ── Quote history actions ────────────────────────────────────────────────

    public boolean isQuotesHistoryVisible() {
        return isOnHistoryView();
    }

    /**
     * Generates a foreclosure quote. Handles all three modal states:
     *  - Payment form view → goes back to history, then clicks "Generate New Quote"
     *  - History view      → clicks "Generate New Quote"
     *  - Summary view      → clicks "Generate Quote"
     * Waits for an FCL- quote ID or a Foreclose button to appear (more reliable
     * than waiting for the section heading, which may flash in/out during load).
     */
    public void clickGenerateQuote() {
        backToHistoryIfOnPaymentForm();

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//div[@role='dialog']//button[contains(normalize-space(.), 'Generate') and contains(normalize-space(.), 'Quote')])[1]")));
        btn.click();

        // Wait up to 45 s for an FCL- quote ID or a "Foreclose" button to appear
        new WebDriverWait(driver, Duration.ofSeconds(45)).until(d -> {
            try {
                return d.findElement(By.xpath(
                    "(//*[contains(normalize-space(.), 'FCL-') and not(.//*[contains(normalize-space(.), 'FCL-')])])[1]" +
                    " | (//button[contains(normalize-space(.), 'Foreclose') and not(contains(normalize-space(.), 'Confirm'))])[1]"))
                    .isDisplayed();
            } catch (Exception e) {
                return false;
            }
        });
    }

    /** Returns the first quote ID visible in the history list (e.g. "FCL-20260630-E5AF9B"). */
    public String getFirstQuoteId() {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("(//*[contains(normalize-space(.), 'FCL-') and not(.//*[contains(normalize-space(.), 'FCL-')])])[1]")));
        return el.getText().trim();
    }

    /** Returns the payoff amount on the first quote card (e.g. "₹70,237.34"). */
    public String getFirstQuotePayoffAmount() {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("(//*[normalize-space(text())='PAYOFF AMOUNT']/following-sibling::* | //*[normalize-space(text())='PAYOFF AMOUNT']/following::*[not(self::*[normalize-space(text())='PAYOFF AMOUNT'])][1])[1]")));
        return el.getText().trim();
    }

    /** Returns true if the first quote card has an Active badge. */
    public boolean isFirstQuoteActive() {
        try {
            WebElement badge = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//div[@role='dialog']//*[normalize-space(text())='Active' and not(.//*[normalize-space(text())='Active'])])[1]")));
            return badge.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clicks "Foreclose →" on the first active quote card.
     * Waits for the payment form view to load before returning.
     */
    public void clickForecloseOnFirstQuote() {
        // Make sure we're on history view first
        backToHistoryIfOnPaymentForm();

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//button[contains(normalize-space(.), 'Foreclose') and not(contains(normalize-space(.), 'Confirm'))])[1]")));
        btn.click();

        // Wait for payment form to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("(//*[contains(normalize-space(.), 'Back to Quotes History')])[1]")));
    }

    // ── Execute Foreclosure Payment form ─────────────────────────────────────

    public String getQuoteReferenceOnPaymentForm() {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("(//*[normalize-space(text())='QUOTE REFERENCE']/following::*[contains(normalize-space(.), 'FCL-')][1])[1]")));
        return el.getText().trim();
    }

    public String getTotalPayoffAmount() {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("(//*[contains(normalize-space(text()), 'Total payoff') or contains(normalize-space(text()), 'Foreclosure Amount')]/following-sibling::* | //*[contains(normalize-space(text()), 'Total payoff')]/following::*[contains(normalize-space(.), '₹')][1])[1]")));
        return el.getText().trim();
    }

    /**
     * Selects repayment mode (e.g. "RTGS", "NEFT", "IMPS").
     * Falls back to JavaScript click if the select is a styled component.
     */
    public void selectRepaymentMode(String visibleText) {
        try {
            WebElement select = wait.until(ExpectedConditions.visibilityOf(repaymentModeSelect));
            new Select(select).selectByVisibleText(visibleText);
        } catch (Exception e) {
            // Styled dropdown — click to open, then pick the option
            WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//*[contains(normalize-space(.), 'Repayment Mode')]/following::*[contains(@role,'button') or contains(@class,'select') or contains(@class,'dropdown')])[1]")));
            trigger.click();
            try { Thread.sleep(500); } catch (Exception ex) {}
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@role='option' and contains(normalize-space(.), '" + visibleText + "')] | //li[contains(normalize-space(.), '" + visibleText + "')]")));
            option.click();
        }
    }

    public void enterBankUtr(String utr) {
        WebElement input = wait.until(ExpectedConditions.visibilityOf(bankUtrInput));
        input.clear();
        input.sendKeys(utr);
    }

    public void enterRemarks(String remarks) {
        WebElement textarea = wait.until(ExpectedConditions.visibilityOf(remarksTextarea));
        textarea.clear();
        textarea.sendKeys(remarks);
    }

    public void clickConfirmAndForeclose() {
        wait.until(ExpectedConditions.elementToBeClickable(confirmAndForecloseBtn)).click();
    }

    public boolean isConfirmAndForecloseEnabled() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(confirmAndForecloseBtn)).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickBack() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[normalize-space(text())='Back']")));
        btn.click();
    }
}
