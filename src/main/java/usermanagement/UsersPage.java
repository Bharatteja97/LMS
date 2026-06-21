package usermanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UsersPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ---------------------------------------------------------
    // Locators: User Management Main Page
    // ---------------------------------------------------------
    private By createUserBtn = By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'create user')] | //a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'create user')]");

    // ---------------------------------------------------------
    // Locators: Create User Modal
    // ---------------------------------------------------------
    private By usernameInput = By.xpath("//label[contains(., 'Username')]/following::input[1]");
    private By passwordInput = By.xpath("//label[contains(., 'Password')]/following::input[1]");
    private By firstNameInput = By.xpath("//label[contains(., 'First name')]/following::input[1]");
    private By lastNameInput = By.xpath("//label[contains(., 'Last name')]/following::input[1]");
    private By emailInput = By.xpath("//label[contains(., 'Email')]/following::input[1]");
    private By employeeIdInput = By.xpath("//label[contains(., 'Employee ID')]/following::input[1]");
    private By phoneInput = By.xpath("//label[contains(., 'Phone')]/following::input[1]");
    
    // Dropdowns
    private By rolesDropdown = By.xpath("//label[contains(., 'Roles')]/following::div[contains(@class, 'select') or contains(@class, 'dropdown') or contains(@class, 'css-')]");
    private By mainBranchDropdown = By.xpath("//label[contains(., 'Main Branch')]/following::div[contains(@class, 'select') or contains(@class, 'dropdown') or contains(@class, 'css-')]");
    private By parentUserDropdown = By.xpath("//label[contains(., 'Parent User')]/following::div[contains(@class, 'select') or contains(@class, 'dropdown') or contains(@class, 'css-')]");
    
    // Checkboxes
    private By allowAccessAnyBranchCheckbox = By.xpath("//label[contains(., 'Allow access to any branch')]/preceding-sibling::input[@type='checkbox'] | //label[contains(., 'Allow access to any branch')]/../input[@type='checkbox']");
    private By markAsLoanOfficerCheckbox = By.xpath("//label[contains(., 'Mark as loan officer')]/preceding-sibling::input[@type='checkbox'] | //label[contains(., 'Mark as loan officer')]/../input[@type='checkbox']");

    // Form Buttons
    private By saveBtn = By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'save') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'create') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'submit')]");
    private By closeBtn = By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'close') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cancel')]");


    public UsersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // --- Actions ---
    
    public void clickCreateUser() {
        wait.until(ExpectedConditions.elementToBeClickable(createUserBtn)).click();
    }

    public void fillUserDetails(String username, String password, String firstName, String lastName, 
                                String email, String empId, String phone) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput)).sendKeys(username);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(firstNameInput).sendKeys(firstName);
        driver.findElement(lastNameInput).sendKeys(lastName);
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(employeeIdInput).sendKeys(empId);
        driver.findElement(phoneInput).sendKeys(phone);
    }
    
    public void selectRole(String role) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(rolesDropdown)).click();
        } catch (Exception e) {
            org.openqa.selenium.WebElement el = driver.findElement(rolesDropdown);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
        By option = By.xpath("//*[text()='" + role + "']");
        org.openqa.selenium.WebElement optionEl = wait.until(ExpectedConditions.presenceOfElementLocated(option));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(option)).click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", optionEl);
        }
    }

    public void selectMainBranch(String branch) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(mainBranchDropdown)).click();
        } catch (Exception e) {
            org.openqa.selenium.WebElement el = driver.findElement(mainBranchDropdown);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
        By option = By.xpath("//*[text()='" + branch + "']");
        org.openqa.selenium.WebElement optionEl = wait.until(ExpectedConditions.presenceOfElementLocated(option));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(option)).click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", optionEl);
        }
    }

    public void selectParentUser(String parentUser) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(parentUserDropdown)).click();
        } catch (Exception e) {
            org.openqa.selenium.WebElement el = driver.findElement(parentUserDropdown);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
        By option = By.xpath("//*[text()='" + parentUser + "']");
        org.openqa.selenium.WebElement optionEl = wait.until(ExpectedConditions.presenceOfElementLocated(option));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(option)).click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", optionEl);
        }
    }

    public void setAllowAccessAnyBranch(boolean allow) {
        boolean isSelected = driver.findElement(allowAccessAnyBranchCheckbox).isSelected();
        if (isSelected != allow) {
            org.openqa.selenium.WebElement label = driver.findElement(By.xpath("//label[contains(., 'Allow access to any branch')]"));
            try {
                label.click();
            } catch (Exception e) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", label);
            }
        }
    }

    public void setMarkAsLoanOfficer(boolean mark) {
        boolean isSelected = driver.findElement(markAsLoanOfficerCheckbox).isSelected();
        if (isSelected != mark) {
            org.openqa.selenium.WebElement label = driver.findElement(By.xpath("//label[contains(., 'Mark as loan officer')]"));
            try {
                label.click();
            } catch (Exception e) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", label);
            }
        }
    }

    public void clickSave() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
        } catch (Exception e) {
            org.openqa.selenium.WebElement btn = driver.findElement(saveBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
    
    public void clickClose() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(closeBtn)).click();
        } catch (Exception e) {
            org.openqa.selenium.WebElement btn = driver.findElement(closeBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}

