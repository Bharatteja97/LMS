package loansettings;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class EStampPage {

    WebDriver driver;
    WebDriverWait wait;

    // List Page Button
    private By addEStampBtn = By.xpath("//button[contains(text(), 'Add E-stamp')]");

    // Modal Locators
    private By stateDropdown = By.xpath("//div[contains(text(), 'Select a state')]");
    
    // Inputs
    private By tagNameInput = By.xpath("//input[@placeholder='Enter tag name']");
    private By denominationInput = By.xpath("//input[@placeholder='Enter denomination']");
    private By quantityInput = By.xpath("//input[@placeholder='Enter quantity']");
    
    // Modal buttons
    private By saveBtn = By.xpath("//button[contains(text(), 'Save')]");

    public EStampPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickAddEStamp() {
        wait.until(ExpectedConditions.elementToBeClickable(addEStampBtn)).click();
    }
    
    public void enterEStampDetails(String stateName, String tagName, String denomination, String quantity) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(stateDropdown)).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + stateName + "')]"))).click();
        } catch (Exception e) {
            System.out.println("Could not interact with State dropdown: " + stateName);
        }
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(tagNameInput)).sendKeys(tagName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(denominationInput)).sendKeys(denomination);
        wait.until(ExpectedConditions.visibilityOfElementLocated(quantityInput)).sendKeys(quantity);
    }
    
    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
    }
}
