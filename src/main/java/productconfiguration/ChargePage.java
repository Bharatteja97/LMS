package productconfiguration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ChargePage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators
    private By createChargeBtn = By.xpath("//button[contains(text(), 'Create charge')]");

    // Basic Configuration
    private By chargeNameInput = By.xpath("//input[@placeholder='Enter charge name']");
    private By chargeTypeDropdown = By.xpath("//div[contains(text(), 'Select charge type')]");
    
    // Value input is pre-filled with '0', using following::input[1] to safely target it
    private By valueInput = By.xpath("//label[contains(text(), 'Value')]/following::input[1]");

    // Deduction & Stage Configuration
    private By deductionTypeDropdown = By.xpath("//div[contains(text(), 'Select deduction type')]");
    private By applicationStageDropdown = By.xpath("//div[contains(text(), 'Select application stage')]");
    private By amountBasisDropdown = By.xpath("//div[contains(text(), 'Select amount basis')]");
    private By descriptionInput = By.xpath("//textarea[contains(@placeholder, 'Add descriptive details')]");

    // Settings Flags (Labels are safer to click for custom checkboxes)
    private By taxApplicableLabel = By.xpath("//label[contains(text(), 'Tax Applicable')]");

    // Actions
    private By saveChargeBtn = By.xpath("//button[contains(text(), 'Save Charge')]");

    public ChargePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateCharge() {
        wait.until(ExpectedConditions.elementToBeClickable(createChargeBtn)).click();
    }
    
    private void selectDropdownOption(By dropdownLocator, String optionText) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator)).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + optionText + "')]"))).click();
        } catch (Exception e) {
            System.out.println("Dropdown or option not found: " + optionText);
        }
    }

    public void enterBasicConfiguration(String name, String chargeType, String calcType, String value) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(chargeNameInput)).sendKeys(name);
        
        selectDropdownOption(chargeTypeDropdown, chargeType);
        
        // Calculation Type might already be populated, we try clicking its wrapper
        try {
            driver.findElement(By.xpath("//label[contains(text(), 'Calculation Type')]/following-sibling::div")).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + calcType + "')]"))).click();
        } catch (Exception e) {
            System.out.println("Calculation Type dropdown interaction skipped or failed.");
        }
        
        WebElement valueEl = wait.until(ExpectedConditions.visibilityOfElementLocated(valueInput));
        valueEl.sendKeys(Keys.CONTROL + "a");
        valueEl.sendKeys(Keys.BACK_SPACE);
        valueEl.sendKeys(value);
    }
    
    public void enterDeductionAndStageConfig(String deductionType, String stage, String amountBasis, String description) {
        selectDropdownOption(deductionTypeDropdown, deductionType);
        selectDropdownOption(applicationStageDropdown, stage);
        selectDropdownOption(amountBasisDropdown, amountBasis);
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(description);
    }
    
    public void checkTaxApplicable() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(taxApplicableLabel)).click();
        } catch (Exception e) {
            System.out.println("Could not click Tax Applicable checkbox.");
        }
    }

    public void clickSaveCharge() {
        wait.until(ExpectedConditions.elementToBeClickable(saveChargeBtn)).click();
    }
}
