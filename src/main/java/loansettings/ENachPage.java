package loansettings;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ENachPage {

    WebDriver driver;
    WebDriverWait wait;

    // List Page Button
    private By createEnachBtn = By.xpath("//button[contains(text(), 'Create Enach')]");

    // Modal Locators
    private By bankDropdown = By.xpath("//div[contains(text(), 'Select a bank')]");
    private By corporateConfigIdInput = By.xpath("//label[contains(text(), 'Corporate Config ID')]/following::input[1]");
    private By expiryDaysInput = By.xpath("//label[contains(text(), 'Expiry Days')]/following::input[1]");
    private By schemeDropdown = By.xpath("//div[contains(text(), 'Select a scheme')]");
    
    // Modal buttons
    private By saveBtn = By.xpath("//button[contains(text(), 'Save')]");

    public ENachPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateEnach() {
        wait.until(ExpectedConditions.elementToBeClickable(createEnachBtn)).click();
    }
    
    public void enterEnachDetails(String bankName, String configId, String expiryDays, String schemeName) {
        // Select Bank
        try {
            wait.until(ExpectedConditions.elementToBeClickable(bankDropdown)).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + bankName + "')]"))).click();
        } catch (Exception e) {
            System.out.println("Could not interact with Bank dropdown: " + bankName);
        }
        
        // Inputs
        wait.until(ExpectedConditions.visibilityOfElementLocated(corporateConfigIdInput)).sendKeys(configId);
        wait.until(ExpectedConditions.visibilityOfElementLocated(expiryDaysInput)).sendKeys(expiryDays);
        
        // Select Scheme
        try {
            wait.until(ExpectedConditions.elementToBeClickable(schemeDropdown)).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + schemeName + "')]"))).click();
        } catch (Exception e) {
            System.out.println("Could not interact with Scheme dropdown: " + schemeName);
        }
    }
    
    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
    }
}
