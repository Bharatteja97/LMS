package accounts;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BalanceTransferPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // ── Section heading ──────────────────────────────────────────────────────
    @FindBy(xpath = "(//*[contains(normalize-space(.), 'Balance Transfer Settlement') and not(.//*[contains(normalize-space(.), 'Balance Transfer Settlement')])])[1]")
    private WebElement sectionHeading;

    // ── Info notice ──────────────────────────────────────────────────────────
    @FindBy(xpath = "(//*[contains(normalize-space(.), 'outstanding liability') and contains(normalize-space(.), 'BALANCE_TRANSFERRED')])[1]")
    private WebElement infoNotice;

    // ── Form fields ──────────────────────────────────────────────────────────
    @FindBy(xpath = "//*[@placeholder='e.g. HDFC Bank, SBI'] | //*[normalize-space(text())='New Lender Name']/following::input[1]")
    private WebElement newLenderNameInput;

    @FindBy(xpath = "//*[@placeholder='Enter NOC number if available'] | //*[contains(normalize-space(text()),'NOC Reference')]/following::input[1]")
    private WebElement nocReferenceInput;

    @FindBy(xpath = "//*[@placeholder='e.g. Lower interest rate, better terms'] | //*[normalize-space(text())='Reason for Transfer']/following::textarea[1]")
    private WebElement reasonForTransferTextarea;

    // ── Buttons ──────────────────────────────────────────────────────────────
    @FindBy(xpath = "//button[normalize-space(text())='Reset' or contains(normalize-space(.), 'Reset')]")
    private WebElement resetBtn;

    @FindBy(xpath = "//button[contains(normalize-space(.), 'Confirm Transfer')]")
    private WebElement confirmTransferBtn;

    public BalanceTransferPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // ── State ────────────────────────────────────────────────────────────────

    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOf(sectionHeading));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSectionHeading() {
        return wait.until(ExpectedConditions.visibilityOf(sectionHeading)).getText().trim();
    }

    public String getInfoNoticeText() {
        return wait.until(ExpectedConditions.visibilityOf(infoNotice)).getText().trim();
    }

    // ── Form interactions ────────────────────────────────────────────────────

    public void enterNewLenderName(String lenderName) {
        WebElement el = wait.until(ExpectedConditions.visibilityOf(newLenderNameInput));
        el.clear();
        el.sendKeys(lenderName);
    }

    public void enterNocReference(String nocRef) {
        WebElement el = wait.until(ExpectedConditions.visibilityOf(nocReferenceInput));
        el.clear();
        el.sendKeys(nocRef);
    }

    public void enterReasonForTransfer(String reason) {
        WebElement el = wait.until(ExpectedConditions.visibilityOf(reasonForTransferTextarea));
        el.clear();
        el.sendKeys(reason);
    }

    public String getNewLenderNameValue() {
        return wait.until(ExpectedConditions.visibilityOf(newLenderNameInput)).getAttribute("value");
    }

    public String getReasonForTransferValue() {
        return wait.until(ExpectedConditions.visibilityOf(reasonForTransferTextarea)).getAttribute("value");
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    public void clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(resetBtn)).click();
    }

    public void clickConfirmTransfer() {
        wait.until(ExpectedConditions.elementToBeClickable(confirmTransferBtn)).click();
    }

    public boolean isConfirmTransferEnabled() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(confirmTransferBtn)).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /** Waits for a success toast / status change after confirming transfer. */
    public boolean isTransferSuccessful() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20)).until(d -> {
                try {
                    // Look for a success toast, status change, or URL change
                    String url = d.getCurrentUrl();
                    if (url.contains("BALANCE_TRANSFERRED") || url.contains("balance_transferred")) return true;
                    d.findElement(By.xpath(
                        "//*[contains(normalize-space(.), 'successfully') or " +
                        "contains(normalize-space(.), 'Balance Transferred') or " +
                        "contains(normalize-space(.), 'BALANCE_TRANSFERRED')]"));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
