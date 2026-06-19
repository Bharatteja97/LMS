package productconfiguration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class ChargePage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators
    private By createChargeBtn = By.xpath("//button[contains(normalize-space(.), 'Create charge') or contains(normalize-space(.), 'Create Charge')]");

    // Basic Configuration
    private By chargeNameInput = By.xpath("//input[@placeholder='Enter charge name'] | //label[contains(normalize-space(.), 'Charge Name')]/following::input[1]");

    // Charge Type custom dropdown
    private By chargeTypeDropdown = By.xpath("//*[contains(normalize-space(.), 'Select charge type') or contains(normalize-space(.), 'Charge Type')]");

    // Calculation Type
    private By calculationTypeDropdown = By.xpath("//*[contains(normalize-space(.), 'Select calculation type') or contains(normalize-space(.), 'Calculation Type')]");

    // Value input
    private By valueInput = By.xpath("//label[contains(normalize-space(.), 'Value')]/following::input[1]");

    // Deduction & Stage Configuration - native HTML <select> elements
    private By deductionTypeDropdown = By.xpath("//label[contains(normalize-space(.), 'Deduction Type')]/following::select[1]");
    private By applicationStageDropdown = By.xpath("//label[contains(normalize-space(.), 'Application Stage')]/following::select[1]");
    private By amountBasisDropdown = By.xpath("//label[contains(normalize-space(.), 'Amount Basis')]/following::select[1]");
    private By descriptionInput = By.xpath("//textarea[contains(@placeholder, 'Add descriptive') or contains(@placeholder, 'description')]");

    // Settings Flags
    private By taxApplicableLabel = By.xpath("//label[contains(normalize-space(.), 'Tax Applicable')]");

    // Save button
    private By saveChargeBtn = By.xpath("//button[contains(normalize-space(.), 'Save Charge') or contains(normalize-space(.), 'Save charge')]");

    public ChargePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void clickCreateCharge() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(createChargeBtn)).click();
        } catch (Exception e) {
            WebElement btn = driver.findElement(createChargeBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    /**
     * Generic dropdown selector: tries to click the dropdown trigger and then select the option by text.
     * Falls back to JavascriptExecutor on click interception.
     */
    private void selectDropdownOption(By dropdownLocator, String optionText) {
        // First check if it's a native select element
        try {
            List<WebElement> selects = driver.findElements(dropdownLocator);
            for (WebElement el : selects) {
                if ("select".equalsIgnoreCase(el.getTagName())) {
                    org.openqa.selenium.support.ui.Select sel = new org.openqa.selenium.support.ui.Select(el);
                    try {
                        sel.selectByVisibleText(optionText);
                    } catch (Exception e) {
                        sel.selectByValue(optionText);
                    }
                    return;
                }
            }
        } catch (Exception ignored) {}

        // Custom dropdown: click trigger, then click matching option
        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
            dropdown.click();
        } catch (Exception e) {
            try {
                WebElement dropdown = driver.findElement(dropdownLocator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);
            } catch (Exception ex) {
                System.out.println("Could not open dropdown for option: " + optionText);
                return;
            }
        }

        // Wait for and click the matching option
        By optionLocator = By.xpath(
            "//li[normalize-space(text())='" + optionText + "'] | " +
            "//li[contains(normalize-space(.), '" + optionText + "')] | " +
            "//*[@role='option' and contains(normalize-space(.), '" + optionText + "')]"
        );
        try {
            WebElement opt = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
            opt.click();
        } catch (Exception e) {
            try {
                WebElement opt = driver.findElement(optionLocator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
            } catch (Exception ex) {
                System.out.println("Could not select dropdown option: " + optionText + " - " + ex.getMessage());
            }
        }
    }

    public void enterBasicConfiguration(String name, String chargeType, String calcType, String value) {
        // Enter name first
        wait.until(ExpectedConditions.visibilityOfElementLocated(chargeNameInput)).sendKeys(name);

        // Select charge type
        selectDropdownOption(chargeTypeDropdown, chargeType);

        // Select calculation type
        selectDropdownOption(calculationTypeDropdown, calcType);

        // Clear existing value and type new one
        WebElement valueEl = wait.until(ExpectedConditions.visibilityOfElementLocated(valueInput));
        valueEl.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        valueEl.sendKeys(Keys.BACK_SPACE);
        valueEl.sendKeys(value);
    }

    public void enterDeductionAndStageConfig(String deductionType, String stage, String amountBasis, String description) {
        selectNativeDropdown(deductionTypeDropdown, deductionType);
        selectNativeDropdown(applicationStageDropdown, stage);
        selectNativeDropdown(amountBasisDropdown, amountBasis);

        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(description);
    }

    private void selectNativeDropdown(By locator, String optionText) {
        try {
            WebElement selectEl = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            org.openqa.selenium.support.ui.Select sel = new org.openqa.selenium.support.ui.Select(selectEl);
            try {
                sel.selectByVisibleText(optionText);
            } catch (Exception e) {
                sel.selectByValue(optionText);
            }
        } catch (Exception e) {
            System.out.println("Could not select native dropdown option: " + optionText + " - " + e.getMessage());
        }
    }

    public void checkTaxApplicable() {
        try {
            WebElement label = wait.until(ExpectedConditions.elementToBeClickable(taxApplicableLabel));
            label.click();
        } catch (Exception e) {
            try {
                WebElement label = driver.findElement(taxApplicableLabel);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);
            } catch (Exception ex) {
                System.out.println("Could not click Tax Applicable checkbox: " + ex.getMessage());
            }
        }
    }

    public void clickSaveCharge() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(saveChargeBtn)).click();
        } catch (Exception e) {
            WebElement btn = driver.findElement(saveChargeBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}
