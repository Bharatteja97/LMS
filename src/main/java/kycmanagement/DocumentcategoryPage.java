package kycmanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DocumentcategoryPage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators based on the exact screenshots
    private By createCategoryBtn = By.xpath("//button[contains(text(), 'Create document category')]");
    
    private By nameInput = By.xpath("//input[@placeholder='Enter category name']");
    private By descriptionInput = By.xpath("//textarea[@placeholder='Enter description...']");
    private By customerTypeDropdown = By.xpath("//div[text()='Select customer type']");

    private By saveBtn = By.xpath("//button[contains(text(), 'Save')]");

    public DocumentcategoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateCategory() {
        wait.until(ExpectedConditions.elementToBeClickable(createCategoryBtn)).click();
    }

    public void enterCategoryDetails(String name, String description, String customerType) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput)).sendKeys(name);
        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(description);
        
        try {
            wait.until(ExpectedConditions.elementToBeClickable(customerTypeDropdown)).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + customerType + "')]"))).click();
        } catch (Exception e) {
            System.out.println("Dropdown customer type options may differ or not interactable: " + customerType);
        }
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
    }
}
