package loansettings;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class EStampPage {

    WebDriver driver;
    WebDriverWait wait;

    // List Page Button
    private By addEStampBtn = By.xpath("//button[contains(., 'Add E-stamp')]");

    // Modal Locators
    // Native <select> element
    private By stateDropdown = By.xpath("//label[contains(.,'State')]/following::select[1]");
    
    // Text Inputs
    private By tagNameInput = By.xpath("//input[@placeholder='Enter tag name']");
    private By denominationInput = By.xpath("//input[@placeholder='Enter denomination']");
    private By quantityInput = By.xpath("//input[@placeholder='Enter quantity']");
    
    // Modal buttons
    private By saveBtn = By.xpath("//button[contains(., 'Save')]");

    public EStampPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    public void clickAddEStamp() {
        wait.until(ExpectedConditions.elementToBeClickable(addEStampBtn)).click();
        System.out.println("Clicked 'Add E-stamp' button on list page.");
    }
    
    public void enterEStampDetails(String stateName, String tagName, String denomination, String quantity) {
        // Select State
        try {
            WebElement stateEl = wait.until(ExpectedConditions.elementToBeClickable(stateDropdown));
            Select stateSelect = new Select(stateEl);
            
            boolean selected = false;
            for (WebElement option : stateSelect.getOptions()) {
                if (option.getText().contains(stateName)) {
                    stateSelect.selectByVisibleText(option.getText());
                    selected = true;
                    break;
                }
            }
            if (!selected && stateSelect.getOptions().size() > 1) {
                stateSelect.selectByIndex(1);
            }
            System.out.println("Selected State: " + stateName);
        } catch (Exception e) {
            System.out.println("Could not select State '" + stateName + "': " + e.getMessage());
        }
        
        // Tag Name Input
        WebElement tagEl = wait.until(ExpectedConditions.visibilityOfElementLocated(tagNameInput));
        tagEl.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        tagEl.sendKeys(tagName);
        System.out.println("Entered Tag Name: " + tagName);
        
        // Denomination Input
        WebElement denominationEl = wait.until(ExpectedConditions.visibilityOfElementLocated(denominationInput));
        denominationEl.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        denominationEl.sendKeys(denomination);
        System.out.println("Entered Denomination: " + denomination);
        
        // Quantity Input
        WebElement quantityEl = wait.until(ExpectedConditions.visibilityOfElementLocated(quantityInput));
        quantityEl.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        quantityEl.sendKeys(quantity);
        System.out.println("Entered Quantity: " + quantity);
    }
    
    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
        System.out.println("Clicked 'Save' button.");
    }
}
