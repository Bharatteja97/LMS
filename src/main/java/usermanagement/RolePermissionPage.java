package usermanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class RolePermissionPage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators based on the screenshots provided
    private By createNewRoleBtn = By.xpath("//button[contains(normalize-space(.), 'Create New Role')]");
    private By roleNameInput = By.xpath("//input[@placeholder='Enter role name' or @name='roleName' or contains(@placeholder, 'role name')]");
    private By descriptionInput = By.xpath("//input[@placeholder='Enter description' or @name='description' or contains(@placeholder, 'description')]");
    private By saveRoleDetailsBtn = By.xpath("//button[contains(normalize-space(.), 'Save Role Details')]");

    public RolePermissionPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateNewRole() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(createNewRoleBtn)).click();
        } catch (Exception e) {
            org.openqa.selenium.WebElement btn = driver.findElement(createNewRoleBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public void enterRoleDetails(String roleName, String description) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(roleNameInput)).sendKeys(roleName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(description);
    }

    public void clickSaveRoleDetails() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(saveRoleDetailsBtn)).click();
        } catch (Exception e) {
            org.openqa.selenium.WebElement btn = driver.findElement(saveRoleDetailsBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}
