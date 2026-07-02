package pages;

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
 * Page Object Model for the LMS Reports Dashboard.
 * URL: https://lms.alfinnext.com/lms/reports?product=VEHICLE_LOAN
 */
public class ReportPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // ── Page Header & Search ──────────────────────────────────────────────────

    @FindBy(xpath = "(//*[normalize-space(.)='Reports Dashboard' and not(.//*[normalize-space(.)='Reports Dashboard'])])[1]")
    private WebElement pageTitle;

    @FindBy(xpath = "//input[@placeholder='Search reports...' or @type='text']")
    private WebElement searchInput;

    @FindBy(xpath = "(//*[normalize-space(.)='As Of Date']/following::input)[1]")
    private WebElement asOfDateInput;

    // ── Stat Cards ────────────────────────────────────────────────────────────

    @FindBy(xpath = "(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'total reports') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'total reports')])]/following-sibling::*)[1]")
    private WebElement totalReportsStat;

    @FindBy(xpath = "(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'filtered') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'filtered')])]/following-sibling::*)[1]")
    private WebElement filteredStat;

    @FindBy(xpath = "(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'est. time') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'est. time')])]/following-sibling::*)[1]")
    private WebElement estTimeStat;

    @FindBy(xpath = "(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'format') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'format')])]/following-sibling::*)[1]")
    private WebElement formatStat;

    // ── Report Cards & Buttons ────────────────────────────────────────────────

    @FindBy(xpath = "//button[contains(normalize-space(.), 'Download')] | //a[contains(normalize-space(.), 'Download')]")
    private List<WebElement> downloadButtons;

    // ── Bottom Section ────────────────────────────────────────────────────────

    @FindBy(xpath = "(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'report generation guidelines') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'report generation guidelines')])])[1]")
    private WebElement guidelinesHeading;

    public ReportPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageTitle));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void searchReport(String keyword) {
        wait.until(ExpectedConditions.elementToBeClickable(searchInput)).clear();
        searchInput.sendKeys(keyword);
    }

    public void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        pause(500);
    }

    public void scrollToTop() {
        js.executeScript("window.scrollTo(0, 0)");
        pause(500);
    }

    public void clickDownloadReport(String reportName) {
        String lowerCaseReportName = reportName.toLowerCase();
        String xpath = String.format("(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')])]/following::button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'download')])[1]", lowerCaseReportName, lowerCaseReportName);
        WebElement downloadBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        
        // Scroll into view to ensure it's clickable
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", downloadBtn);
        pause(500);
        downloadBtn.click();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getTotalReportsStat() {
        return wait.until(ExpectedConditions.visibilityOf(totalReportsStat)).getText().trim();
    }

    public String getFilteredStat() {
        return wait.until(ExpectedConditions.visibilityOf(filteredStat)).getText().trim();
    }

    public String getEstTimeStat() {
        return wait.until(ExpectedConditions.visibilityOf(estTimeStat)).getText().trim();
    }

    public String getFormatStat() {
        return wait.until(ExpectedConditions.visibilityOf(formatStat)).getText().trim();
    }

    public int getDownloadButtonsCount() {
        return downloadButtons.size();
    }

    public boolean isGuidelinesVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(guidelinesHeading)).isDisplayed();
        } catch (Exception e) {
            return false;
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
