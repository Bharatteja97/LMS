package lenderonboarding;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LenderDocumentsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By uploadDocumentBtn = By.xpath("//button[contains(., 'Upload Document')]");
    
    // Modal Locators
    private By companyDropdown = By.xpath("//label[contains(., 'Select Company')]/following::div[contains(@class, 'select') or contains(@class, 'dropdown') or contains(@class, 'css-')]");
    private By categoryDropdown = By.xpath("//label[contains(., 'Category')]/following::div[contains(@class, 'select') or contains(@class, 'dropdown') or contains(@class, 'css-')][1]");
    private By versionInput = By.xpath("//label[contains(., 'Version')]/following::input[1]");
    private By nameInput = By.xpath("//label[contains(., 'Name')]/following::input[1]");
    
    // For file upload, usually there's a hidden input[type='file'] we can sendKeys to
    private By fileUploadInput = By.xpath("//input[@type='file']");
    
    private By remarksInput = By.xpath("//label[contains(., 'Remarks')]/following::input[1] | //label[contains(., 'Remarks')]/following::textarea[1]");
    
    private By submitUploadBtn = By.xpath("//button[contains(., 'Upload') and not(contains(., 'Document'))] | //div[@role='dialog']//button[contains(., 'Upload')]");

    public LenderDocumentsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickUploadDocument() {
        wait.until(ExpectedConditions.elementToBeClickable(uploadDocumentBtn)).click();
    }

    public void fillDocumentDetails(String company, String category, String version, String name, String filePath, String remarks) {
        // Wait for modal to appear (e.g., version input is visible)
        wait.until(ExpectedConditions.visibilityOfElementLocated(versionInput));

        // Company
        if (company != null && !company.isEmpty()) {
            try {
                driver.findElement(companyDropdown).click();
            } catch (Exception e) {
                driver.findElement(By.xpath("//label[contains(., 'Select Company')]/following::div[1]")).click();
            }
            By companyOption = By.xpath("//*[text()='" + company + "']");
            wait.until(ExpectedConditions.elementToBeClickable(companyOption)).click();
        }

        // Category
        if (category != null && !category.isEmpty()) {
            try {
                driver.findElement(categoryDropdown).click();
            } catch (Exception e) {
                driver.findElement(By.xpath("//label[contains(., 'Category')]/following::div[1]")).click();
            }
            By categoryOption = By.xpath("//*[text()='" + category + "']");
            wait.until(ExpectedConditions.elementToBeClickable(categoryOption)).click();
        }

        driver.findElement(versionInput).sendKeys(version);
        driver.findElement(nameInput).sendKeys(name);

        if (filePath != null && !filePath.isEmpty()) {
            // Selenium can directly upload to input type="file" without clicking browse
            driver.findElement(fileUploadInput).sendKeys(filePath);
        }

        if (remarks != null && !remarks.isEmpty()) {
            driver.findElement(remarksInput).sendKeys(remarks);
        }
    }

    public void submitDocument() {
        wait.until(ExpectedConditions.elementToBeClickable(submitUploadBtn)).click();
    }
}

