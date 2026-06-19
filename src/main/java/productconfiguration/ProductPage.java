package productconfiguration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ProductPage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators based on the provided screenshots
    private By createProductBtn = By.xpath("//button[contains(text(), 'Create product')]");
    
    private By nameInput = By.xpath("//input[@placeholder='Enter name']");
    private By productTypeDropdown = By.xpath("//div[text()='Select Product Type']"); 
    private By descriptionInput = By.xpath("//textarea[@placeholder='Enter description...']");
    
    private By saveBtn = By.xpath("//button[contains(text(), 'Save')]");

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateProduct() {
        wait.until(ExpectedConditions.elementToBeClickable(createProductBtn)).click();
    }

    public void enterProductDetails(String name, String productType, String description) {
        // Enter Name
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput)).sendKeys(name);
        
        // Select Product Type from Custom Dropdown
        wait.until(ExpectedConditions.elementToBeClickable(productTypeDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='" + productType + "']"))).click();
        
        // Enter Description
        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(description);
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
    }
}
