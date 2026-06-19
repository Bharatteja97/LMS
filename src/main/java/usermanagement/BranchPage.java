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
    private By addBranchBtn = By.xpath("//button[contains(normalize-space(.), 'Add Branch')]");
    private By branchNameInput = By.xpath("//input[@placeholder='Enter branch name' or @name='branchName']");
    private By branchCodeInput = By.xpath("//input[@placeholder='Enter branch code' or @name='branchCode']");
    private By houseBuildingInput = By.xpath("//input[@placeholder='House/Building number']");
    private By streetInput = By.xpath("//input[@placeholder='Street/Road name']");
    
    // Custom dropdown selectors (standard HTML select elements)
    private By stateDropdown = By.xpath("//label[contains(normalize-space(.), 'State')]/following::select[1] | //select[contains(@class, 'state') or @name='state']"); 
    private By cityDropdown = By.xpath("//label[contains(normalize-space(.), 'City')]/following::select[1] | //select[contains(@class, 'city') or @name='city']");
    
    private By pinCodeInput = By.xpath("//input[@placeholder='e.g. 400001' or @placeholder='Pin code']");
    private By saveBranchBtn = By.xpath("//button[contains(normalize-space(.), 'Save Branch')]");

    public BranchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickAddBranch() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(addBranchBtn)).click();
        } catch (Exception e) {
            org.openqa.selenium.WebElement btn = driver.findElement(addBranchBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
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
        org.openqa.selenium.WebElement stateEl = wait.until(ExpectedConditions.presenceOfElementLocated(stateDropdown));
        org.openqa.selenium.support.ui.Select selectState = new org.openqa.selenium.support.ui.Select(stateEl);
        try {
            selectState.selectByVisibleText(state);
        } catch (Exception e) {
            try {
                selectState.selectByValue(state);
            } catch (Exception ex) {
                // fallback to find elements and click
                org.openqa.selenium.WebElement opt = stateEl.findElement(By.xpath(".//option[contains(text(), '" + state + "')]"));
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
            }
        }
        
        // Wait a short moment for city options to load/populate based on state selection
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        org.openqa.selenium.WebElement cityEl = wait.until(ExpectedConditions.presenceOfElementLocated(cityDropdown));
        org.openqa.selenium.support.ui.Select selectCity = new org.openqa.selenium.support.ui.Select(cityEl);
        try {
            selectCity.selectByVisibleText(city);
        } catch (Exception e) {
            try {
                selectCity.selectByValue(city);
            } catch (Exception ex) {
                org.openqa.selenium.WebElement opt = cityEl.findElement(By.xpath(".//option[contains(text(), '" + city + "')]"));
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
            }
        }
    }

    public void fillPostalInformation(String pin) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pinCodeInput)).sendKeys(pin);
    }

    public void clickSaveBranch() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(saveBranchBtn)).click();
        } catch (Exception e) {
            org.openqa.selenium.WebElement btn = driver.findElement(saveBranchBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}
