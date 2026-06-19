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
    private By createNewRoleBtn = By.xpath("//button[contains(text(), 'Create New Role')]");
    private By roleNameInput = By.xpath("//input[@placeholder='Enter role name']");
    private By descriptionInput = By.xpath("//input[@placeholder='Enter description']");
    private By saveRoleDetailsBtn = By.xpath("//button[contains(text(), 'Save Role Details')]");

    public RolePermissionPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateNewRole() {
        wait.until(ExpectedConditions.elementToBeClickable(createNewRoleBtn)).click();
    }

    public void enterRoleDetails(String roleName, String description) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(roleNameInput)).sendKeys(roleName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(description);
    }

    public void clickSaveRoleDetails() {
        wait.until(ExpectedConditions.elementToBeClickable(saveRoleDetailsBtn)).click();
    }
}
