package productconfiguration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SchemePage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators
    private By createSchemeBtn = By.xpath("//button[contains(text(), 'Create scheme')]");

    // Basic Information
    private By schemeNameInput = By.xpath("//input[@placeholder='Enter scheme name']");
    private By schemeCodeInput = By.xpath("//input[@placeholder='Enter scheme code']");
    private By productDropdown = By.xpath("//div[text()='Select a product']");

    // Financial Parameters (using following::input[1] because they are pre-filled with '0')
    private By minLoanAmountInput = By.xpath("//label[contains(text(), 'Minimum Loan Amount')]/following::input[1]");
    private By maxLoanAmountInput = By.xpath("//label[contains(text(), 'Maximum Loan Amount')]/following::input[1]");
    private By minLTVInput = By.xpath("//label[contains(text(), 'Minimum LTV')]/following::input[1]");
    private By maxLTVInput = By.xpath("//label[contains(text(), 'Maximum LTV')]/following::input[1]");

    // Interest & Tenures
    private By minInterestInput = By.xpath("//label[contains(text(), 'Minimum Interest')]/following::input[1]");
    private By maxInterestInput = By.xpath("//label[contains(text(), 'Maximum Interest')]/following::input[1]");
    
    private By tenureFreqDropdown = By.xpath("//div[text()='Select tenure frequency']");
    private By minTenureInput = By.xpath("//label[contains(text(), 'Minimum Tenure')]/following::input[1]");
    private By maxTenureInput = By.xpath("//label[contains(text(), 'Maximum Tenure')]/following::input[1]");
    private By defaultTenureInput = By.xpath("//label[contains(text(), 'Default Tenure')]/following::input[1]");

    // Charges & Details
    private By associatedChargesDropdown = By.xpath("//div[contains(text(), 'Select charges')]");
    private By descriptionInput = By.xpath("//textarea[@placeholder='Enter scheme description...']");
    
    // Actions
    private By saveSchemeBtn = By.xpath("//button[contains(text(), 'Save Scheme')]");

    public SchemePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateScheme() {
        wait.until(ExpectedConditions.elementToBeClickable(createSchemeBtn)).click();
    }

    public void enterBasicInformation(String name, String code, String productName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(schemeNameInput)).sendKeys(name);
        wait.until(ExpectedConditions.visibilityOfElementLocated(schemeCodeInput)).sendKeys(code);
        
        wait.until(ExpectedConditions.elementToBeClickable(productDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + productName + "')]"))).click();
    }
    
    private void clearAndSendKeys(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        // Clear using hotkeys to ensure React/Vue bindings update properly
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(value);
    }
    
    public void enterFinancialParameters(String minLoan, String maxLoan, String minLTV, String maxLTV) {
        clearAndSendKeys(minLoanAmountInput, minLoan);
        clearAndSendKeys(maxLoanAmountInput, maxLoan);
        clearAndSendKeys(minLTVInput, minLTV);
        clearAndSendKeys(maxLTVInput, maxLTV);
    }
    
    public void enterInterestAndTenures(String minInt, String maxInt, String freq, String minTen, String maxTen, String defTen) {
        clearAndSendKeys(minInterestInput, minInt);
        clearAndSendKeys(maxInterestInput, maxInt);
        
        wait.until(ExpectedConditions.elementToBeClickable(tenureFreqDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + freq + "')]"))).click();
        
        clearAndSendKeys(minTenureInput, minTen);
        clearAndSendKeys(maxTenureInput, maxTen);
        clearAndSendKeys(defaultTenureInput, defTen);
    }
    
    public void enterChargesAndDetails(String charge, String desc) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(associatedChargesDropdown)).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + charge + "')]"))).click();
            // Press escape in case it's a multi-select dropdown that stays open
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        } catch (Exception e) {
            System.out.println("Could not select charge: " + charge);
        }
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(desc);
    }

    public void clickSaveScheme() {
        wait.until(ExpectedConditions.elementToBeClickable(saveSchemeBtn)).click();
    }
}
