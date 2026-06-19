package lenderonboarding;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LenderBankPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By addBankAccountBtn = By.xpath("//button[contains(., 'Add Bank Account')]");
    
    private By accountHolderNameInput = By.xpath("//label[contains(., 'Account Holder Name')]/following::input[1]");
    private By bankNameInput = By.xpath("//label[contains(., 'Bank Name')]/following::input[1]");
    private By branchNameInput = By.xpath("//label[contains(., 'Branch Name')]/following::input[1]");
    private By ifscCodeInput = By.xpath("//label[contains(., 'IFSC Code')]/following::input[1]");
    private By accountNumberInput = By.xpath("//label[contains(., 'Account Number')]/following::input[1]");
    
    // Account Type dropdown usually requires clicking the container, then the option
    private By accountTypeDropdown = By.xpath("//label[contains(., 'Account Type')]/following::div[contains(@class, 'select') or contains(@class, 'dropdown') or contains(@class, 'css-')]");
    
    // Specific Add Account button in the modal to avoid matching the one on the main page
    private By submitAddAccountBtn = By.xpath("//button[contains(., 'Add Account') and not(contains(., 'Bank'))]");

    public LenderBankPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickAddBankAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(addBankAccountBtn)).click();
    }

    public void fillBankAccountDetails(String holderName, String bankName, String branchName, String ifsc, String accNumber, String accType, boolean isPrimary) {
        // Wait for the modal to open and the first input to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(accountHolderNameInput)).sendKeys(holderName);
        driver.findElement(bankNameInput).sendKeys(bankName);
        driver.findElement(branchNameInput).sendKeys(branchName);
        driver.findElement(ifscCodeInput).sendKeys(ifsc);
        driver.findElement(accountNumberInput).sendKeys(accNumber);
        
        // Handle Account Type Dropdown
        // Fallback to a broader locator if the specific div is not found
        try {
            driver.findElement(accountTypeDropdown).click();
        } catch (Exception e) {
            driver.findElement(By.xpath("//label[contains(., 'Account Type')]/following::div[1]")).click();
        }
        
        By accountTypeOption = By.xpath("//*[text()='" + accType + "']");
        wait.until(ExpectedConditions.elementToBeClickable(accountTypeOption)).click();

        if (isPrimary) {
            By checkboxLabel = By.xpath("//label[contains(., 'Mark as primary account')]");
            driver.findElement(checkboxLabel).click();
        }
    }

    public void submitBankAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(submitAddAccountBtn)).click();
    }
}

