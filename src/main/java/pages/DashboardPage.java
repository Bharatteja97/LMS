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
 * Page Object Model for the LMS Dashboard.
 * URL: https://lms.alfinnext.com/lms/dashboard?product=VEHICLE_LOAN
 */
public class DashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // ── Page Header & Filters ──────────────────────────────────────────────────

    @FindBy(xpath = "(//*[normalize-space(.)='LMS Dashboard' and not(.//*[normalize-space(.)='LMS Dashboard'])])[1]")
    private WebElement pageTitle;

    @FindBy(xpath = "(//*[normalize-space(.)='As Of Date']/following::input)[1]")
    private WebElement asOfDateInput;

    @FindBy(xpath = "//button[normalize-space(.)='Reset']")
    private WebElement resetButton;

    // ── KPI Cards ─────────────────────────────────────────────────────────────

    // Using robust XPath to find the value immediately following the label inside the KPI card
    @FindBy(xpath = "(//*[normalize-space(text())='Total Portfolio']/following-sibling::*)[1]")
    private WebElement totalPortfolioValue;

    @FindBy(xpath = "(//*[normalize-space(text())='NPA Accounts']/following-sibling::*)[1]")
    private WebElement npaAccountsValue;

    @FindBy(xpath = "(//*[normalize-space(text())='Active Loans']/following-sibling::*)[1]")
    private WebElement activeLoansValue;

    @FindBy(xpath = "(//*[normalize-space(text())='Collection Efficiency']/following-sibling::*)[1]")
    private WebElement collectionEfficiencyValue;

    // ── Charts & Alerts ───────────────────────────────────────────────────────

    @FindBy(xpath = "(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'dpd bucket distribution') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'dpd bucket distribution')])])[1]")
    private WebElement dpdBucketDistributionHeading;

    @FindBy(xpath = "(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'live alerts') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'live alerts')])])[1]")
    private WebElement liveAlertsHeading;

    // ── Recent Loan Activities (Bottom Section) ───────────────────────────────

    @FindBy(xpath = "(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'recent loan activities') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'recent loan activities')])])[1]")
    private WebElement recentLoanActivitiesHeading;

    @FindBy(xpath = "//table[.//th[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'lan')]]//tbody/tr")
    private List<WebElement> recentActivitiesRows;

    public DashboardPage(WebDriver driver) {
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

    public void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        pause(500); // Give time for lazy loading if any
    }
    
    public void scrollToTop() {
        js.executeScript("window.scrollTo(0, 0)");
        pause(500);
    }

    public void setAsOfDate(String date) {
        wait.until(ExpectedConditions.elementToBeClickable(asOfDateInput));
        asOfDateInput.clear();
        asOfDateInput.sendKeys(date);
    }

    public void clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(resetButton)).click();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getTotalPortfolio() {
        return wait.until(ExpectedConditions.visibilityOf(totalPortfolioValue)).getText().trim();
    }

    public String getNpaAccounts() {
        return wait.until(ExpectedConditions.visibilityOf(npaAccountsValue)).getText().trim();
    }

    public String getActiveLoans() {
        return wait.until(ExpectedConditions.visibilityOf(activeLoansValue)).getText().trim();
    }

    public String getCollectionEfficiency() {
        return wait.until(ExpectedConditions.visibilityOf(collectionEfficiencyValue)).getText().trim();
    }

    public boolean isDpdBucketVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(dpdBucketDistributionHeading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLiveAlertsVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(liveAlertsHeading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRecentActivitiesVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(recentLoanActivitiesHeading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public int getRecentActivitiesRowCount() {
        if (!isRecentActivitiesVisible()) {
            return 0;
        }
        return recentActivitiesRows.size();
    }
    
    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
