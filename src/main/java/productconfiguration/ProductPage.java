package productconfiguration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class ProductPage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators based on the provided screenshots
    private By createProductBtn = By.xpath("//button[contains(normalize-space(.), 'Create product') or contains(normalize-space(.), 'Create Product')]");

    private By nameInput = By.xpath("//label[contains(normalize-space(.), 'Name')]/following::input[1] | //input[@placeholder='Enter name']");
    private By descriptionInput = By.xpath("//label[contains(normalize-space(.), 'Description')]/following::textarea[1] | //textarea[@placeholder='Enter description...']");

    // Product Type: could be a native select OR a custom div dropdown
    private By productTypeSelect = By.xpath("//label[contains(normalize-space(.), 'Product Type')]/following::select[1]");
    private By productTypeCustomDropdown = By.xpath("//label[contains(normalize-space(.), 'Product Type')]/following::div[contains(@class,'select') or contains(@class,'dropdown')][1] | //*[normalize-space(.)='Select Product Type']");

    private By saveBtn = By.xpath("//button[contains(normalize-space(.), 'Save') or contains(normalize-space(.), 'Submit') or contains(normalize-space(.), 'Create')]");

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateProduct() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(createProductBtn)).click();
        } catch (Exception e) {
            WebElement btn = driver.findElement(createProductBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public void enterProductDetails(String name, String productType, String description) {
        // Enter Name first
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput)).sendKeys(name);

        // Select Product Type - try native <select> first, then custom dropdown
        boolean selectedViaSelect = false;
        try {
            List<WebElement> selects = driver.findElements(productTypeSelect);
            if (!selects.isEmpty() && selects.get(0).isDisplayed()) {
                org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(selects.get(0));
                try {
                    select.selectByVisibleText(productType);
                } catch (Exception ex) {
                    select.selectByValue(productType);
                }
                selectedViaSelect = true;
            }
        } catch (Exception ignored) {}

        if (!selectedViaSelect) {
            // Fall back to custom dropdown: click to open, then click the option
            try {
                WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(productTypeCustomDropdown));
                dropdown.click();
            } catch (Exception e) {
                WebElement dropdown = driver.findElement(productTypeCustomDropdown);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);
            }
            // Wait for options and click matching one
            By option = By.xpath("//*[normalize-space(text())='" + productType + "'] | //li[contains(normalize-space(.), '" + productType + "')]");
            try {
                WebElement opt = wait.until(ExpectedConditions.elementToBeClickable(option));
                opt.click();
            } catch (Exception e) {
                WebElement opt = driver.findElement(option);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
            }
        }

        // Enter Description
        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(description);
    }

    public void clickSave() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
        } catch (Exception e) {
            WebElement btn = driver.findElement(saveBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}
