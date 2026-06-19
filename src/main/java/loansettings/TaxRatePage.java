package loansettings;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class TaxRatePage {

    WebDriver driver;
    WebDriverWait wait;

    // List Page Locator
    private By createTaxRateBtnList = By.xpath("//button[contains(text(), 'Create Tax Rate')]");

    // Modal Locators
    private By taxRateNameInput = By.xpath("//input[contains(@placeholder, 'e.g. GST')]");
    
    // Dates
    private By effectiveFromInput = By.xpath("//label[contains(text(), 'Effective From')]/following::input[1]");
    private By effectiveToInput = By.xpath("//label[contains(text(), 'Effective To')]/following::input[1]");
    
    // Notes
    private By notesTextarea = By.xpath("//textarea[contains(@placeholder, 'Optional notes')]");
    
    // Modal Submit Button - Uses the last button with this text to differentiate from the list page button
    private By modalSubmitBtn = By.xpath("(//button[contains(text(), 'Create Tax Rate')])[last()]");

    public TaxRatePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateTaxRateList() {
        wait.until(ExpectedConditions.elementToBeClickable(createTaxRateBtnList)).click();
    }
    
    public void enterRateDefinition(String name, String type, String fromDate, String toDate, String notes) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(taxRateNameInput)).sendKeys(name);
        
        try {
            // Dropdown click
            WebElement typeDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[contains(text(), 'Tax Type')]/following::div[1]")));
            typeDropdown.click();
            // Select option
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + type + "')]"))).click();
        } catch (Exception e) {
            System.out.println("Could not select Tax Type: " + type);
        }
        
        WebElement from = wait.until(ExpectedConditions.visibilityOfElementLocated(effectiveFromInput));
        from.sendKeys(fromDate);
        from.sendKeys(Keys.ESCAPE); // to close datepicker if it stays open
        
        WebElement to = wait.until(ExpectedConditions.visibilityOfElementLocated(effectiveToInput));
        to.sendKeys(toDate);
        to.sendKeys(Keys.ESCAPE);
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(notesTextarea)).sendKeys(notes);
    }
    
    public void submitTaxRate() {
        wait.until(ExpectedConditions.elementToBeClickable(modalSubmitBtn)).click();
    }
}
