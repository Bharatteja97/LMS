package kycmanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class RuleEnginePage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators based on screenshots
    private By createScorecardBtn = By.xpath("//button[contains(text(), 'Create Scorecard')]");
    
    // Scorecard Information
    private By schemeDropdown = By.xpath("//div[contains(text(), 'Select a scheme')]");
    // Name field is pre-filled, so we use following::input[1] to safely target it
    private By scorecardNameInput = By.xpath("//label[contains(text(), 'Scorecard Name')]/following::input[1]");
    
    // Thresholds
    private By addThresholdBtn = By.xpath("//button[contains(text(), 'Add Threshold')]");

    // Action buttons
    private By saveScorecardBtn = By.xpath("//button[contains(text(), 'Save Scorecard')]");

    public RuleEnginePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateScorecard() {
        wait.until(ExpectedConditions.elementToBeClickable(createScorecardBtn)).click();
    }
    
    public void enterScorecardInformation(String schemeName, String scorecardName) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(schemeDropdown)).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + schemeName + "')]"))).click();
        } catch (Exception e) {
            System.out.println("Scheme dropdown interaction failed or scheme not found: " + schemeName);
        }
        
        WebElement nameEl = wait.until(ExpectedConditions.visibilityOfElementLocated(scorecardNameInput));
        nameEl.sendKeys(Keys.CONTROL + "a");
        nameEl.sendKeys(Keys.BACK_SPACE);
        nameEl.sendKeys(scorecardName);
    }

    public void clickAddThreshold() {
        wait.until(ExpectedConditions.elementToBeClickable(addThresholdBtn)).click();
    }

    public void clickSaveScorecard() {
        wait.until(ExpectedConditions.elementToBeClickable(saveScorecardBtn)).click();
    }
}
