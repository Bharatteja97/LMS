package lenderonboarding;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LenderBankPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    // Main Add Bank Account button
    private By addBankAccountBtn = By.xpath("//button[contains(., 'Add Bank Account')]");
    
    // Inputs (using following::input[1] or placeholder fallbacks if needed)
    private By accountHolderNameInput = By.xpath("//label[contains(., 'Account Holder Name')]/following::input[1]");
    private By bankNameInput = By.xpath("//label[contains(., 'Bank Name')]/following::input[1]");
    private By branchNameInput = By.xpath("//label[contains(., 'Branch Name')]/following::input[1]");
    private By ifscCodeInput = By.xpath("//label[contains(., 'IFSC Code')]/following::input[1]");
    private By accountNumberInput = By.xpath("//label[contains(., 'Account Number')]/following::input[1]");
    
    // Account Type Dropdown (custom button with combobox role)
    private By accountTypeDropdown = By.xpath("//label[contains(., 'Account Type')]/following::button[@role='combobox'][1]");
    
    // Primary checkbox
    private By primaryCheckboxInput = By.cssSelector("input#isDefault");
    private By primaryCheckboxLabel = By.xpath("//label[contains(., 'Mark as primary account')]");
    
    // Modal Add Account button
    private By submitAddAccountBtn = By.xpath("//button[contains(., 'Add Account') and ancestor::div[@role='dialog']]");

    public LenderBankPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    public void clickAddBankAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(addBankAccountBtn)).click();
        System.out.println("Clicked 'Add Bank Account' button on list page.");
    }

    public void fillBankAccountDetails(String holderName, String bankName, String branchName, String ifsc, String accNumber, String accType, boolean isPrimary) {
        // Account Holder Name
        WebElement holderEl = wait.until(ExpectedConditions.visibilityOfElementLocated(accountHolderNameInput));
        holderEl.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        holderEl.sendKeys(holderName);
        System.out.println("Entered Account Holder Name: " + holderName);

        // Bank Name
        WebElement bankEl = wait.until(ExpectedConditions.visibilityOfElementLocated(bankNameInput));
        bankEl.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        bankEl.sendKeys(bankName);
        System.out.println("Entered Bank Name: " + bankName);

        // Branch Name
        WebElement branchEl = wait.until(ExpectedConditions.visibilityOfElementLocated(branchNameInput));
        branchEl.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        branchEl.sendKeys(branchName);
        System.out.println("Entered Branch Name: " + branchName);

        // IFSC Code
        WebElement ifscEl = wait.until(ExpectedConditions.visibilityOfElementLocated(ifscCodeInput));
        ifscEl.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        ifscEl.sendKeys(ifsc);
        System.out.println("Entered IFSC Code: " + ifsc);

        // Account Number
        WebElement accNumEl = wait.until(ExpectedConditions.visibilityOfElementLocated(accountNumberInput));
        accNumEl.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        accNumEl.sendKeys(accNumber);
        System.out.println("Entered Account Number: " + accNumber);
        
        // Handle Account Type Dropdown
        try {
            wait.until(ExpectedConditions.elementToBeClickable(accountTypeDropdown)).click();
            By accountTypeOption = By.xpath("//div[@role='option' and contains(., '" + accType + "')] | //span[contains(text(), '" + accType + "')]/ancestor::div[@role='option']");
            wait.until(ExpectedConditions.elementToBeClickable(accountTypeOption)).click();
            System.out.println("Selected Account Type: " + accType);
        } catch (Exception e) {
            System.out.println("Could not interact with Account Type dropdown: " + e.getMessage());
        }

        // Handle Primary Checkbox
        if (isPrimary) {
            try {
                WebElement checkbox = driver.findElement(primaryCheckboxInput);
                if (!checkbox.isSelected()) {
                    driver.findElement(primaryCheckboxLabel).click();
                    System.out.println("Checked 'Mark as primary account'.");
                }
            } catch (Exception e) {
                try {
                    driver.findElement(primaryCheckboxLabel).click();
                    System.out.println("Checked 'Mark as primary account' via label.");
                } catch (Exception ex) {
                    System.out.println("Could not check 'Mark as primary account': " + ex.getMessage());
                }
            }
        }
    }

    public void submitBankAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(submitAddAccountBtn)).click();
        System.out.println("Clicked 'Add Account' modal submit button.");
    }
}
