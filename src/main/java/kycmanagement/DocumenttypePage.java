package kycmanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DocumenttypePage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators based on screenshots
    private By createDocumentTypeBtn = By.xpath("//button[contains(text(), 'Create document type')]");
    
    // Inputs
    private By documentNameInput = By.xpath("//input[@placeholder='Enter document type name']");
    private By maxSizeInput = By.xpath("//label[contains(text(), 'Max size')]/following::input[1]");
    
    // Dropdown
    private By categoryDropdown = By.xpath("//div[contains(text(), 'Select a category')]");
    
    // Checkboxes (clicking labels is safer)
    private By isRequiredLabel = By.xpath("//label[contains(text(), 'Is Required')]");
    private By enableOcrLabel = By.xpath("//label[contains(text(), 'Enable OCR')]");
    
    // Description
    private By descriptionInput = By.xpath("//textarea[contains(@placeholder, 'Describe document type')]");
    
    // Format Cards
    private By jpgFormatCard = By.xpath("//div[text()='JPG' or contains(text(), 'JPG')]");
    private By pngFormatCard = By.xpath("//div[text()='PNG' or contains(text(), 'PNG')]");
    private By pdfFormatCard = By.xpath("//div[text()='PDF' or contains(text(), 'PDF')]");
    private By docxFormatCard = By.xpath("//div[text()='DOCX' or contains(text(), 'DOCX')]");

    // Action buttons
    private By saveBtn = By.xpath("//button[contains(text(), 'Save')]");

    public DocumenttypePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickCreateDocumentType() {
        wait.until(ExpectedConditions.elementToBeClickable(createDocumentTypeBtn)).click();
    }
    
    public void enterBasicDetails(String name, String maxSize, String categoryName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(documentNameInput)).sendKeys(name);
        
        WebElement maxEl = wait.until(ExpectedConditions.visibilityOfElementLocated(maxSizeInput));
        maxEl.sendKeys(Keys.CONTROL + "a");
        maxEl.sendKeys(Keys.BACK_SPACE);
        maxEl.sendKeys(maxSize);
        
        try {
            wait.until(ExpectedConditions.elementToBeClickable(categoryDropdown)).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(text(), '" + categoryName + "')]"))).click();
        } catch (Exception e) {
            System.out.println("Could not interact with Category dropdown or option: " + categoryName);
        }
    }
    
    public void toggleCheckboxes() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(enableOcrLabel)).click();
        } catch (Exception e) {
            System.out.println("Could not click OCR checkbox label.");
        }
    }
    
    public void enterDescription(String description) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(description);
    }
    
    public void selectFormats(boolean jpg, boolean png, boolean pdf, boolean docx) {
        if (jpg) {
            try { wait.until(ExpectedConditions.elementToBeClickable(jpgFormatCard)).click(); } catch(Exception e){}
        }
        if (png) {
            try { wait.until(ExpectedConditions.elementToBeClickable(pngFormatCard)).click(); } catch(Exception e){}
        }
        if (pdf) {
            try { wait.until(ExpectedConditions.elementToBeClickable(pdfFormatCard)).click(); } catch(Exception e){}
        }
        if (docx) {
            try { wait.until(ExpectedConditions.elementToBeClickable(docxFormatCard)).click(); } catch(Exception e){}
        }
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
    }
}
