package usermanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BranchPage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators based on the screenshots provided
    private By addBranchBtn = By.xpath("//button[contains(text(), 'Add Branch')]");
    private By branchNameInput = By.xpath("//input[@placeholder='Enter branch name']");
    private By branchCodeInput = By.xpath("//input[@placeholder='Enter branch code']");
    private By houseBuildingInput = By.xpath("//input[@placeholder='House/Building number']");
    private By streetInput = By.xpath("//input[@placeholder='Street/Road name']");
    
    // Custom dropdown selectors
    private By stateDropdown = By.xpath("//div[text()='Select a state']"); 
    private By cityDropdown = By.xpath("//div[text()='Select a city']");
    
    private By pinCodeInput = By.xpath("//input[@placeholder='e.g. 400001']");
    private By saveBranchBtn = By.xpath("//button[contains(text(), 'Save Branch')]");

    public BranchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickAddBranch() {
        wait.until(ExpectedConditions.elementToBeClickable(addBranchBtn)).click();
    }

    public void fillBranchInformation(String name, String code) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(branchNameInput)).sendKeys(name);
        wait.until(ExpectedConditions.visibilityOfElementLocated(branchCodeInput)).sendKeys(code);
    }

    public void fillPropertyDetails(String house, String street) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(houseBuildingInput)).sendKeys(house);
        wait.until(ExpectedConditions.visibilityOfElementLocated(streetInput)).sendKeys(street);
    }
    
    public void selectStateAndCity(String state, String city) {
        // Clicks the dropdown placeholder and then selects the specific option by text
        wait.until(ExpectedConditions.elementToBeClickable(stateDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='" + state + "']"))).click();
        
        wait.until(ExpectedConditions.elementToBeClickable(cityDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='" + city + "']"))).click();
    }

    public void fillPostalInformation(String pin) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pinCodeInput)).sendKeys(pin);
    }

    public void clickSaveBranch() {
        wait.until(ExpectedConditions.elementToBeClickable(saveBranchBtn)).click();
    }
}
