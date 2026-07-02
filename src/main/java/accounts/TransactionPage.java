package accounts;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object Model for the Transaction Tab.
 * URL: https://lms.alfinnext.com/lms/accounts/212?tab=transactions&product=VEHICLE_LOAN
 */
public class TransactionPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // ── Headings & Controls ───────────────────────────────────────────────────

    @FindBy(xpath = "(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'transaction history') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'transaction history')])])[1]")
    private WebElement transactionHistoryHeading;

    @FindBy(xpath = "//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'export excel')] | //a[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'export excel')] | //*[contains(@class, 'export') and contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'excel')]")
    private WebElement exportExcelButton;

    @FindBy(xpath = "(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'from date')]/following::input)[1]")
    private WebElement fromDateInput;

    @FindBy(xpath = "(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'to date')]/following::input)[1]")
    private WebElement toDateInput;

    // ── Table & Pagination ────────────────────────────────────────────────────

    @FindBy(xpath = "//table[.//th[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'reference')]]//tbody/tr")
    private List<WebElement> transactionRows;

    @FindBy(xpath = "//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'showing') and contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'results') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'showing') and contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'results')])]")
    private WebElement paginationText;

    public TransactionPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOf(transactionHistoryHeading));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setFromDate(String date) {
        wait.until(ExpectedConditions.elementToBeClickable(fromDateInput)).clear();
        fromDateInput.sendKeys(date);
    }

    public void setToDate(String date) {
        wait.until(ExpectedConditions.elementToBeClickable(toDateInput)).clear();
        toDateInput.sendKeys(date);
    }

    public void clickExportExcel() {
        wait.until(ExpectedConditions.elementToBeClickable(exportExcelButton));
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", exportExcelButton);
        pause(500);
        exportExcelButton.click();
    }

    public void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        pause(500);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int getTransactionRowsCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(transactionRows));
            return transactionRows.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getPaginationText() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(paginationText)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
