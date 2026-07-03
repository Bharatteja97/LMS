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
    @FindBy(xpath = "//*[normalize-space(text())='Start Date']/following::input[@type='date'][1]")
    private WebElement filterStartDate;

    @FindBy(xpath = "//*[normalize-space(text())='End Date']/following::input[@type='date'][1]")
    private WebElement filterEndDate;

    @FindBy(xpath = "//*[normalize-space(text())='Branch']/following::select[1]")
    private WebElement filterBranchDropdown;

    @FindBy(xpath = "//*[normalize-space(text())='Scheme']/following::select[1]")
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
        selectDropdownOption(filterBranchDropdown, branchText);
    }

    public void selectFilterScheme(String schemeText) {
        wait.until(ExpectedConditions.visibilityOf(filterSchemeDropdown));
        selectDropdownOption(filterSchemeDropdown, schemeText);
    }

    // React-controlled <select> elements ignore a plain WebDriver click/JS value
    // assignment in some environments (the visible label doesn't update even though
    // an option gets highlighted). Select via the native value setter + dispatched
    // 'change' event so React's onChange fires, then verify it actually stuck.
    private void selectDropdownOption(WebElement selectElement, String visibleText) {
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(selectElement);
        WebElement matchedOption = null;
        for (WebElement option : select.getOptions()) {
            if (option.getText().trim().equalsIgnoreCase(visibleText.trim())) {
                matchedOption = option;
                break;
            }
        }

        select.selectByVisibleText(matchedOption != null ? matchedOption.getText() : visibleText);

        boolean applied = select.getFirstSelectedOption().getText().trim().equalsIgnoreCase(visibleText.trim());
        if (!applied) {
            String optionValue = matchedOption != null ? matchedOption.getAttribute("value") : visibleText;
            js.executeScript(
                "var el = arguments[0];" +
                "var value = arguments[1];" +
                "var setter = Object.getOwnPropertyDescriptor(window.HTMLSelectElement.prototype, 'value').set;" +
                "setter.call(el, value);" +
                "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                "el.dispatchEvent(new Event('change', { bubbles: true }));",
                selectElement, optionValue);
        }
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
