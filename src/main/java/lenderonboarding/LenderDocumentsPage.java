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

        // Text inputs first
        wait.until(ExpectedConditions.elementToBeClickable(versionInput)).sendKeys(version);
        wait.until(ExpectedConditions.elementToBeClickable(nameInput)).sendKeys(name);

        if (remarks != null && !remarks.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(remarksInput)).sendKeys(remarks);
        }

        // Company
        if (company != null && !company.isEmpty()) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(companyDropdown)).click();
            } catch (Exception e) {
                org.openqa.selenium.WebElement el = driver.findElement(By.xpath("//label[contains(., 'Select Company')]/following::div[1]"));
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            }
            By companyOption = By.xpath("//*[text()='" + company + "']");
            org.openqa.selenium.WebElement optionEl = wait.until(ExpectedConditions.presenceOfElementLocated(companyOption));
            try {
                wait.until(ExpectedConditions.elementToBeClickable(companyOption)).click();
            } catch (Exception e) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", optionEl);
            }
        }

        // Category
        if (category != null && !category.isEmpty()) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(categoryDropdown)).click();
            } catch (Exception e) {
                org.openqa.selenium.WebElement el = driver.findElement(By.xpath("//label[contains(., 'Category')]/following::div[1]"));
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            }
            By categoryOption = By.xpath("//*[text()='" + category + "']");
            org.openqa.selenium.WebElement optionEl = wait.until(ExpectedConditions.presenceOfElementLocated(categoryOption));
            try {
                wait.until(ExpectedConditions.elementToBeClickable(categoryOption)).click();
            } catch (Exception e) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", optionEl);
            }
        }

        if (filePath != null && !filePath.isEmpty()) {
            // Selenium can directly upload to input type="file" without clicking browse
            // Fallback to JS if sendKeys fails due to element not being interactable (sometimes the case for hidden inputs)
            try {
                driver.findElement(fileUploadInput).sendKeys(filePath);
            } catch (Exception e) {
                org.openqa.selenium.WebElement uploadEl = driver.findElement(fileUploadInput);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].style.display='block'; arguments[0].style.visibility='visible';", uploadEl);
                uploadEl.sendKeys(filePath);
            }
        }
    }

    public void submitDocument() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(submitUploadBtn)).click();
        } catch (Exception e) {
            org.openqa.selenium.WebElement btn = driver.findElement(submitUploadBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}

