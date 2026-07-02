package pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // --- Dashboard Overview Stats ---
    @FindBy(xpath = "//*[normalize-space(text())='Total Applications' or contains(text(), 'Total Applications')]/following-sibling::*[1] | //div[div[contains(text(), 'Total Applications')]]/div[2]")
    private WebElement totalApplicationsStat;

    @FindBy(xpath = "//*[normalize-space(text())='New Lead' or contains(text(), 'New Lead')]/following-sibling::*[1] | //div[div[contains(text(), 'New Lead')]]/div[2]")
    private WebElement newLeadStat;

    @FindBy(xpath = "//*[normalize-space(text())='Approval' or contains(text(), 'Approval')]/following-sibling::*[1] | //div[div[contains(text(), 'Approval')]]/div[2]")
    private WebElement approvalStat;

    @FindBy(xpath = "//*[normalize-space(text())='Disbursement' or contains(text(), 'Disbursement')]/following-sibling::*[1] | //div[div[contains(text(), 'Disbursement')]]/div[2]")
    private WebElement disbursementStat;

    @FindBy(xpath = "//button[contains(., 'Filters')]")
    private WebElement filtersBtn;

    // --- Audit Trails & Activity Log Filters ---
    @FindBy(xpath = "//input[@placeholder='Username...']")
    private WebElement auditUsernameInput;

    @FindBy(xpath = "//input[@placeholder='Application No...']")
    private WebElement auditApplicationNoInput;

    @FindBy(xpath = "//input[@placeholder='Application No...']/following-sibling::button[contains(@class, 'bg-primary')]")
    private WebElement searchBtn;

    // --- Recent Applications ---
    @FindBy(xpath = "//select[option[contains(text(), 'All Status')]]")
    private WebElement statusDropdown;

    // --- Filter Dashboard Sidebar ---
    @FindBy(xpath = "//*[contains(text(), 'Start Date')]/ancestor::div[1]/following-sibling::*//input | //*[contains(text(), 'Start Date')]/parent::*/parent::*//input | //input[ancestor::div[*[contains(text(), 'Start Date')]]]")
    private WebElement filterStartDate;

    @FindBy(xpath = "//*[contains(text(), 'End Date')]/ancestor::div[1]/following-sibling::*//input | //*[contains(text(), 'End Date')]/parent::*/parent::*//input | //input[ancestor::div[*[contains(text(), 'End Date')]]]")
    private WebElement filterEndDate;

    @FindBy(xpath = "//*[contains(text(), 'Branch')]/ancestor::div[1]/following-sibling::*//select | //*[contains(text(), 'Branch')]/parent::*/parent::*//select | //select[ancestor::div[*[contains(text(), 'Branch')]]]")
    private WebElement filterBranchDropdown;

    @FindBy(xpath = "//*[contains(text(), 'Scheme')]/ancestor::div[1]/following-sibling::*//select | //*[contains(text(), 'Scheme')]/parent::*/parent::*//select | //select[ancestor::div[*[contains(text(), 'Scheme')]]]")
    private WebElement filterSchemeDropdown;

    @FindBy(xpath = "//button[contains(., 'Reset')]")
    private WebElement filterResetBtn;

    @FindBy(xpath = "//button[contains(., 'Apply Filters')]")
    private WebElement filterApplyBtn;

    @FindBy(xpath = "//*[contains(text(), 'Filter Dashboard')]/following-sibling::button | //*[contains(text(), 'Filter Dashboard')]/parent::*//button")
    private WebElement filterCloseBtn;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public String getTotalApplicationsCount() {
        wait.until(ExpectedConditions.visibilityOf(totalApplicationsStat));
        return totalApplicationsStat.getText().trim();
    }

    public String getNewLeadCount() {
        wait.until(ExpectedConditions.visibilityOf(newLeadStat));
        return newLeadStat.getText().trim();
    }

    public String getApprovalCount() {
        wait.until(ExpectedConditions.visibilityOf(approvalStat));
        return approvalStat.getText().trim();
    }

    public String getDisbursementCount() {
        wait.until(ExpectedConditions.visibilityOf(disbursementStat));
        return disbursementStat.getText().trim();
    }

    public void clickFilters() {
        wait.until(ExpectedConditions.elementToBeClickable(filtersBtn));
        js.executeScript("arguments[0].click();", filtersBtn);
    }

    public void searchAuditTrailsByApplicationNo(String appNo) {
        wait.until(ExpectedConditions.visibilityOf(auditApplicationNoInput));
        auditApplicationNoInput.clear();
        auditApplicationNoInput.sendKeys(appNo);
        
        if (searchBtn != null) {
            js.executeScript("arguments[0].click();", searchBtn);
        }
    }

    public void searchAuditTrailsByUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(auditUsernameInput));
        auditUsernameInput.clear();
        auditUsernameInput.sendKeys(username);
        
        if (searchBtn != null) {
            js.executeScript("arguments[0].click();", searchBtn);
        }
    }

    // --- Filter Dashboard Methods ---
    public void setFilterStartDate(String date) {
        wait.until(ExpectedConditions.visibilityOf(filterStartDate));
        filterStartDate.clear();
        filterStartDate.sendKeys(date);
    }

    public void setFilterEndDate(String date) {
        wait.until(ExpectedConditions.visibilityOf(filterEndDate));
        filterEndDate.clear();
        filterEndDate.sendKeys(date);
    }

    public void selectFilterBranch(String branchText) {
        wait.until(ExpectedConditions.visibilityOf(filterBranchDropdown));
        org.openqa.selenium.support.ui.Select branchSelect = new org.openqa.selenium.support.ui.Select(filterBranchDropdown);
        for (WebElement option : branchSelect.getOptions()) {
            if (option.getText().trim().equalsIgnoreCase(branchText.trim())) {
                branchSelect.selectByVisibleText(option.getText());
                return;
            }
        }
        branchSelect.selectByVisibleText(branchText); // Fallback
    }

    public void selectFilterScheme(String schemeText) {
        wait.until(ExpectedConditions.visibilityOf(filterSchemeDropdown));
        org.openqa.selenium.support.ui.Select schemeSelect = new org.openqa.selenium.support.ui.Select(filterSchemeDropdown);
        for (WebElement option : schemeSelect.getOptions()) {
            if (option.getText().trim().equalsIgnoreCase(schemeText.trim())) {
                schemeSelect.selectByVisibleText(option.getText());
                return;
            }
        }
        schemeSelect.selectByVisibleText(schemeText); // Fallback
    }

    public void clickApplyFilters() {
        wait.until(ExpectedConditions.elementToBeClickable(filterApplyBtn));
        js.executeScript("arguments[0].click();", filterApplyBtn);
    }

    public void clickResetFilters() {
        wait.until(ExpectedConditions.elementToBeClickable(filterResetBtn));
        js.executeScript("arguments[0].click();", filterResetBtn);
    }

    public void closeFiltersDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(filterCloseBtn));
        js.executeScript("arguments[0].click();", filterCloseBtn);
    }
}
