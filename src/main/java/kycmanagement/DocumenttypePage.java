package kycmanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DocumenttypePage {

    WebDriver driver;
    WebDriverWait wait;

    // ─── Listing Page Locators ────────────────────────────────────────────────
    // "Create document type" button on the listing page
    private By createDocumentTypeBtn = By.xpath("//button[contains(., 'Create document type')]");

    // ─── Create Form Locators (full-page form at /type/create) ───────────────

    // Document name input — placeholder: "Enter document type name"
    private By documentNameInput = By.xpath("//input[@placeholder='Enter document type name']");

    // Max size input — placeholder: "eg 5"
    private By maxSizeInput = By.xpath("//input[@placeholder='eg 5']");

    // Category — native <select> element, identified by @name from DOM inspection
    private By categorySelect = By.xpath("//select[@name='category'] | //select[option[contains(text(),'Select a category')]]");

    // Is Required — checkbox with name='isRequired' (confirmed from DOM JS inspection)
    private By isRequiredCheckbox = By.xpath("//input[@name='isRequired' and @type='checkbox']");

    // Enable OCR — checkbox with name='enableOcr' (confirmed from DOM JS inspection)
    private By enableOcrCheckbox = By.xpath("//input[@name='enableOcr' and @type='checkbox']");

    // Description textarea — placeholder confirmed: exactly 5 dots
    private By descriptionInput = By.xpath("//textarea[@placeholder='Describe document type.....']");

    // Format cards — <button> elements
    private By jpgFormatCard  = By.xpath("//button[contains(., 'JPG')]");
    private By pngFormatCard  = By.xpath("//button[contains(., 'PNG')]");
    private By pdfFormatCard  = By.xpath("//button[contains(., 'PDF')]");
    private By docxFormatCard = By.xpath("//button[contains(., 'DOCX')]");

    // Save button — has SVG child, use contains(.) not contains(text())
    private By saveBtn   = By.xpath("//button[contains(., 'Save') and not(contains(., 'Cancel'))]");

    // Cancel button
    private By cancelBtn = By.xpath("//button[contains(., 'Cancel')]");

    // ─── Constructor ──────────────────────────────────────────────────────────
    public DocumenttypePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ─── Actions ──────────────────────────────────────────────────────────────

    /**
     * Clicks the "Create document type" button from the listing page.
     * NOTE: If you navigate directly to /type/create, this is NOT needed.
     */
    public void clickCreateDocumentType() {
        wait.until(ExpectedConditions.elementToBeClickable(createDocumentTypeBtn)).click();
    }

    /**
     * Fills in the basic details section of the Create Document Type form.
     *
     * @param name         Document type name (e.g. "Passport")
     * @param maxSize      Max file size in MB (e.g. "5")
     * @param categoryName Visible text of the category option (e.g. "Address Proof")
     */
    public void enterBasicDetails(String name, String maxSize, String categoryName) {

        // Document name
        wait.until(ExpectedConditions.visibilityOfElementLocated(documentNameInput)).sendKeys(name);

        // Max size — clear existing default value (page shows "2") then type new value
        WebElement maxEl = wait.until(ExpectedConditions.visibilityOfElementLocated(maxSizeInput));
        maxEl.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        maxEl.sendKeys(Keys.BACK_SPACE);
        maxEl.sendKeys(maxSize);

        // Category — native <select>, use Selenium Select class
        selectCategory(categoryName);
    }

    /** Selects a category from the native <select> dropdown by visible text. */
    public void selectCategory(String categoryName) {
        try {
            WebElement selectEl = wait.until(ExpectedConditions.elementToBeClickable(categorySelect));
            new Select(selectEl).selectByVisibleText(categoryName);
            System.out.println("Selected category: " + categoryName);
        } catch (Exception e) {
            System.out.println("Could not select category '" + categoryName + "': " + e.getMessage());
            // Fallback: try selecting by partial text via option XPath
            try {
                WebElement selectEl = driver.findElement(By.xpath("//select"));
                new Select(selectEl).selectByVisibleText(categoryName);
            } catch (Exception ex) {
                System.out.println("Fallback category select also failed: " + ex.getMessage());
            }
        }
    }

    /**
     * Toggles checkboxes.
     * By default "Is Required" is already checked; this method clicks "Enable OCR".
     * Call with care — clicking "Is Required" will uncheck it.
     */
    public void toggleCheckboxes() {
        // Toggle Enable OCR (default is unchecked → clicking enables it)
        try {
            wait.until(ExpectedConditions.elementToBeClickable(enableOcrCheckbox)).click();
            System.out.println("Toggled 'Enable OCR' checkbox.");
        } catch (Exception e) {
            System.out.println("Could not click 'Enable OCR' checkbox: " + e.getMessage());
        }
    }

    /** Enters text into the Description textarea. */
    public void enterDescription(String description) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(description);
    }

    /**
     * Selects supported file format cards.
     * Each format is a <button> — clicking it toggles selection.
     *
     * @param jpg  Select JPG format
     * @param png  Select PNG format
     * @param pdf  Select PDF format
     * @param docx Select DOCX format
     */
    public void selectFormats(boolean jpg, boolean png, boolean pdf, boolean docx) {
        if (jpg)  clickFormat(jpgFormatCard,  "JPG");
        if (png)  clickFormat(pngFormatCard,  "PNG");
        if (pdf)  clickFormat(pdfFormatCard,  "PDF");
        if (docx) clickFormat(docxFormatCard, "DOCX");
    }

    private void clickFormat(By locator, String label) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
            System.out.println("Selected format: " + label);
        } catch (Exception e) {
            System.out.println("Could not select format '" + label + "': " + e.getMessage());
        }
    }

    /** Clicks the Save button to submit the form. */
    public void clickSave() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveBtn));
        btn.click();
        System.out.println("Clicked Save button.");
    }

    /** Clicks the Cancel button to discard the form. */
    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
    }
}
