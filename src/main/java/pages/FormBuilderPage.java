package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class FormBuilderPage {

    WebDriver driver;
    WebDriverWait wait;

    // List Page Button
    private By createFormBtn = By.xpath("//button[contains(., 'Create Form')]");

    // Modal Locators
    // Using position in dialog to reliably find the select dropdowns
    private By productDropdown = By.xpath("(//div[@role='dialog']//select)[1]");
    private By schemeDropdown = By.xpath("(//div[@role='dialog']//select)[2]");
    
    // Modal buttons
    private By buildFormBtn = By.xpath("//button[contains(., 'Build Form')]");

    public FormBuilderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    public void clickCreateForm() {
        wait.until(ExpectedConditions.elementToBeClickable(createFormBtn)).click();
        System.out.println("Clicked '+ Create Form' button on dashboard.");
    }
    
    public void configureForm(String productName, String schemeName) {
        // Select Product
        try {
            WebElement productEl = wait.until(ExpectedConditions.elementToBeClickable(productDropdown));
            Select productSelect = new Select(productEl);
            
            boolean selected = false;
            for (WebElement option : productSelect.getOptions()) {
                if (option.getText().contains(productName)) {
                    productSelect.selectByVisibleText(option.getText());
                    selected = true;
                    break;
                }
            }
            if (!selected && productSelect.getOptions().size() > 1) {
                productSelect.selectByIndex(1);
            }
            System.out.println("Selected Product: " + productName);
        } catch (Exception e) {
            System.out.println("Could not select Product '" + productName + "': " + e.getMessage());
        }
        
        // Select Scheme (this becomes enabled after selecting a product)
        try {
            WebElement schemeEl = wait.until(ExpectedConditions.elementToBeClickable(schemeDropdown));
            Select schemeSelect = new Select(schemeEl);
            
            // Wait for options to load via API
            wait.until(driver -> schemeSelect.getOptions().size() > 1);
            
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
    
    public void clickBuildForm() {
        wait.until(ExpectedConditions.elementToBeClickable(buildFormBtn)).click();
        System.out.println("Clicked 'Build Form' button.");
    }
}
