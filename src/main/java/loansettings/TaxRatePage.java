package loansettings;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class TaxRatePage {

    WebDriver driver;
    WebDriverWait wait;

    // List Page Locator
    // "Create Tax Rate" has an SVG icon
    private By createTaxRateBtnList = By.xpath("(//button[contains(., 'Create Tax Rate')])[1]");

    // Modal Locators
    private By taxRateNameInput = By.xpath("//input[@placeholder='e.g. GST 18% Standard']");
    
    // Tax Type (Native Select)
    private By taxTypeSelect = By.xpath("//label[contains(text(), 'Tax Type')]/following-sibling::select");
    
    // Dates
    private By effectiveFromInput = By.xpath("//label[contains(text(), 'Effective From')]/following::input[1]");
    private By effectiveToInput = By.xpath("//label[contains(text(), 'Effective To')]/following::input[1]");
    
    // Notes
    private By notesTextarea = By.xpath("//textarea[@placeholder='Optional notes about this tax rate...']");
    
    // Modal Submit Button - Uses the last button with this text to differentiate from the list page button
    private By modalSubmitBtn = By.xpath("(//button[contains(., 'Create Tax Rate')])[last()]");

    public TaxRatePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void clickCreateTaxRateList() {
        wait.until(ExpectedConditions.elementToBeClickable(createTaxRateBtnList)).click();
        System.out.println("Clicked Create Tax Rate (List Page)");
    }
    
    public void enterRateDefinition(String name, String type, String fromDate, String toDate, String notes) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(taxRateNameInput)).sendKeys(name);
        System.out.println("Entered Tax Rate Name: " + name);
        
        try {
            WebElement selectEl = wait.until(ExpectedConditions.elementToBeClickable(taxTypeSelect));
            Select sel = new Select(selectEl);
            // Try to select by visible text
            boolean selected = false;
            for (WebElement option : sel.getOptions()) {
                if (option.getText().contains(type)) {
                    sel.selectByVisibleText(option.getText());
                    selected = true;
                    break;
                }
            }
            if (!selected) {
                sel.selectByIndex(1);
            }
            System.out.println("Selected Tax Type: " + type);
        } catch (Exception e) {
            System.out.println("Could not select Tax Type '" + type + "': " + e.getMessage());
        }
        
        WebElement from = wait.until(ExpectedConditions.visibilityOfElementLocated(effectiveFromInput));
        from.sendKeys(fromDate);
        from.sendKeys(Keys.ESCAPE); // to close datepicker if it stays open
        System.out.println("Entered Effective From: " + fromDate);
        
        WebElement to = wait.until(ExpectedConditions.visibilityOfElementLocated(effectiveToInput));
        to.sendKeys(toDate);
        to.sendKeys(Keys.ESCAPE);
        System.out.println("Entered Effective To: " + toDate);
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(notesTextarea)).sendKeys(notes);
        System.out.println("Entered Notes.");
    }
    
    public void submitTaxRate() {
        wait.until(ExpectedConditions.elementToBeClickable(modalSubmitBtn)).click();
        System.out.println("Clicked Create Tax Rate (Modal Submit)");
    }
}
