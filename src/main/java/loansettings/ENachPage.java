package loansettings;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ENachPage {

    WebDriver driver;
    WebDriverWait wait;

    // List Page Button
    private By createEnachBtn = By.xpath("//button[contains(., 'Create Enach')]");

    // Modal Locators
    // Native <select> elements
    private By bankDropdown = By.xpath("//label[contains(.,'Bank')]/following::select[1]");
    private By schemeDropdown = By.xpath("//label[contains(.,'Scheme')]/following::select[1]");
    
    // Text Inputs
    private By corporateConfigIdInput = By.xpath("//label[contains(.,'Corporate Config ID')]/following::input[1]");
    private By expiryDaysInput = By.xpath("//label[contains(.,'Expiry Days')]/following::input[1]");
    
    // Modal buttons
    private By saveBtn = By.xpath("//button[contains(., 'Save')]");

    public ENachPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    public void clickCreateEnach() {
        wait.until(ExpectedConditions.elementToBeClickable(createEnachBtn)).click();
        System.out.println("Clicked 'Create Enach' button on list page.");
    }
    
    public void enterEnachDetails(String bankName, String configId, String expiryDays, String schemeName) {
        // Select Bank
        try {
            WebElement bankEl = wait.until(ExpectedConditions.elementToBeClickable(bankDropdown));
            Select bankSelect = new Select(bankEl);
            
            boolean selected = false;
            for (WebElement option : bankSelect.getOptions()) {
                if (option.getText().contains(bankName)) {
                    bankSelect.selectByVisibleText(option.getText());
                    selected = true;
                    break;
                }
            }
            if (!selected && bankSelect.getOptions().size() > 1) {
                bankSelect.selectByIndex(1);
            }
            System.out.println("Selected Bank: " + bankName);
        } catch (Exception e) {
            System.out.println("Could not select Bank '" + bankName + "': " + e.getMessage());
        }
        
        // Corporate Config ID Input
        WebElement configIdEl = wait.until(ExpectedConditions.visibilityOfElementLocated(corporateConfigIdInput));
        configIdEl.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        configIdEl.sendKeys(configId);
        System.out.println("Entered Corporate Config ID: " + configId);
        
        // Expiry Days Input
        WebElement expiryDaysEl = wait.until(ExpectedConditions.visibilityOfElementLocated(expiryDaysInput));
        expiryDaysEl.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        expiryDaysEl.sendKeys(expiryDays);
        System.out.println("Entered Expiry Days: " + expiryDays);
        
        // Select Scheme
        try {
            WebElement schemeEl = wait.until(ExpectedConditions.elementToBeClickable(schemeDropdown));
            Select schemeSelect = new Select(schemeEl);
            
            boolean selected = false;
            for (WebElement option : schemeSelect.getOptions()) {
                if (option.getText().contains(schemeName)) {
                    schemeSelect.selectByVisibleText(option.getText());
                    selected = true;
                    break;
                }
            }
            if (!selected && schemeSelect.getOptions().size() > 1) {
                schemeSelect.selectByIndex(1);
            }
            System.out.println("Selected Scheme: " + schemeName);
        } catch (Exception e) {
            System.out.println("Could not select Scheme '" + schemeName + "': " + e.getMessage());
        }
    }
    
    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
        System.out.println("Clicked 'Save' button.");
    }
}
