package lenderonboarding;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page object for the "Legal & Compliance" (Lender Documents) page.
 * URL: /settings/client-documents?product=VEHICLE_LOAN
 *
 * Modal title: "Upload Legal Document"
 * Fields (in DOM order):
 *   1. Select Company  — <button role="combobox">
 *   2. Category        — <button role="combobox">
 *   3. Version         — <input> (text)
 *   4. Name *          — <input> (text)
 *   5. Attachments     — drag-and-drop zone; hidden <input type="file"> inside
 *   6. Remarks         — <input> (text)
 * Submit button text: "Upload"
 */
public class LenderDocumentsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final WebDriverWait shortWait;

    // ── List page ────────────────────────────────────────────────────────────
    private final By uploadDocumentBtn =
        By.xpath("//button[normalize-space(.)='Upload Document']");

    // ── Modal — wait anchor ───────────────────────────────────────────────────
    // The dialog is identified by its title
    private final By modalTitle =
        By.xpath("//h2[contains(normalize-space(.), 'Upload Legal Document')]"
               + " | //*[@role='dialog' or contains(@class,'dialog')]"
               + "    //*[contains(normalize-space(.), 'Upload Legal Document')]");

    // ── Modal — Select Company (role=combobox button) ─────────────────────────
    // First combobox in the dialog (the company selector)
    private final By companyCombobox =
        By.xpath("(//*[@role='dialog' or contains(@class,'dialog-content')]"
               + "  //button[@role='combobox'])[1]"
               + " | //label[contains(normalize-space(.),'Select Company')]"
               + "     /following::button[@role='combobox'][1]");

    // ── Modal — Category (role=combobox button) ───────────────────────────────
    private final By categoryCombobox =
        By.xpath("//label[contains(normalize-space(.),'Category')]"
               + "  /following::button[@role='combobox'][1]");

    // ── Modal — text inputs (Version, Name, Remarks) ─────────────────────────
    // Version input (first plain input after the category row)
    private final By versionInput =
        By.xpath("//label[contains(normalize-space(.),'Version')]"
               + "  /following::input[1]");

    // Name input (document name, required)
    private final By nameInput =
        By.xpath("//label[contains(normalize-space(.),'Name') and not(contains(.,'Company'))]"
               + "  /following::input[1]");

    // Remarks input or textarea
    private final By remarksInput =
        By.xpath("//label[contains(normalize-space(.),'Remarks')]"
               + "  /following::input[1]"
               + " | //label[contains(normalize-space(.),'Remarks')]"
               + "     /following::textarea[1]");

    // ── Modal — file upload ───────────────────────────────────────────────────
    // The <input type="file"> is hidden inside the drag-and-drop zone.
    // We reveal it via JS then sendKeys the file path.
    private final By fileInput =
        By.xpath("//*[@role='dialog' or contains(@class,'dialog-content')]"
               + "  //input[@type='file']"
               + " | //input[@type='file']");

    // ── Modal — submit button ────────────────────────────────────────────────
    // Button text is exactly "Upload" (not "Upload Document")
    private final By submitBtn =
        By.xpath("//*[@role='dialog' or contains(@class,'dialog-content')]"
               + "  //button[normalize-space(.)='Upload']"
               + " | //button[normalize-space(.)='Upload']");

    // ── Dropdown option (appears as listbox/option after combobox click) ──────
    private By dropdownOption(String text) {
        return By.xpath(
            "//*[@role='option' and contains(normalize-space(.), '" + text + "')]"
            + " | //div[@role='listbox']//*[contains(normalize-space(.), '" + text + "')]"
            + " | //li[contains(normalize-space(.), '" + text + "')]"
        );
    }

    public LenderDocumentsPage(WebDriver driver) {
        this.driver = driver;
        this.wait      = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ── List-page action ─────────────────────────────────────────────────────

    public void clickUploadDocument() {
        wait.until(ExpectedConditions.elementToBeClickable(uploadDocumentBtn)).click();
        System.out.println("Clicked 'Upload Document' button.");
        // Wait for modal to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput));
        System.out.println("Modal is open and ready.");
    }

    // ── Modal fill ───────────────────────────────────────────────────────────

    /**
     * Fill the "Upload Legal Document" modal.
     *
     * @param company   Company name to select (must match an option in the dropdown).
     *                  Pass null or "" to skip.
     * @param category  Category to select (must match an option in the dropdown).
     *                  Pass null or "" to skip.
     * @param version   Version string (plain text input). Pass null or "" to skip.
     * @param name      Document name (required plain text input).
     * @param filePath  Absolute path to the file to upload. Pass null or "" to skip.
     * @param remarks   Remarks text. Pass null or "" to skip.
     */
    public void fillDocumentDetails(String company, String category,
                                    String version, String name,
                                    String filePath, String remarks) {

        // 1. Select Company
        if (company != null && !company.isEmpty()) {
            selectCombobox(companyCombobox, company, "Select Company");
        }

        // 2. Select Category
        if (category != null && !category.isEmpty()) {
            selectCombobox(categoryCombobox, category, "Category");
        }

        // 3. Version
        if (version != null && !version.isEmpty()) {
            fillInput(versionInput, version, "Version");
        }

        // 4. Name (required)
        if (name != null && !name.isEmpty()) {
            fillInput(nameInput, name, "Name");
        }

        // 5. Remarks
        if (remarks != null && !remarks.isEmpty()) {
            fillInput(remarksInput, remarks, "Remarks");
        }

        // 6. File attachment
        if (filePath != null && !filePath.isEmpty()) {
            uploadFile(filePath);
        }
    }

    // ── Submit ───────────────────────────────────────────────────────────────

    public void submitDocument() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block:'center'});", btn);
        try {
            btn.click();
            System.out.println("Clicked 'Upload' submit button.");
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            System.out.println("Clicked 'Upload' submit button via JS.");
        }
        // Brief wait for server response / success toast
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Click a combobox button, wait for options to appear, then click the matching option.
     */
    private void selectCombobox(By comboboxLocator, String optionText, String fieldLabel) {
        try {
            WebElement combobox = wait.until(ExpectedConditions.elementToBeClickable(comboboxLocator));
            try {
                combobox.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", combobox);
            }
            System.out.println("Opened '" + fieldLabel + "' combobox.");

            // Wait for option list to appear
            By optionLocator = dropdownOption(optionText);
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
            try {
                option.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
            }
            System.out.println("Selected '" + optionText + "' in '" + fieldLabel + "'.");
        } catch (Exception e) {
            System.out.println("Warning: Could not select '" + optionText
                + "' in '" + fieldLabel + "': " + e.getMessage());
        }
    }

    /**
     * Clear and type into a plain input or textarea field.
     */
    private void fillInput(By locator, String value, String fieldLabel) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            el.clear();
            el.sendKeys(value);
            System.out.println("Filled '" + fieldLabel + "': " + value);
        } catch (Exception e) {
            System.out.println("Warning: Could not fill '" + fieldLabel + "': " + e.getMessage());
        }
    }

    /**
     * Upload a file by revealing the hidden <input type="file"> via JavaScript
     * and then using sendKeys to set the file path.
     */
    private void uploadFile(String filePath) {
        try {
            // Find the hidden file input
            WebElement input = driver.findElement(fileInput);
            // Make it visible/interactable so Selenium can send keys
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.display='block';"
                + "arguments[0].style.visibility='visible';"
                + "arguments[0].style.opacity='1';"
                + "arguments[0].removeAttribute('hidden');",
                input);
            input.sendKeys(filePath);
            System.out.println("Uploaded file: " + filePath);
            // Give the UI time to show the preview / handle the file
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        } catch (Exception e) {
            System.out.println("Warning: Could not upload file '" + filePath + "': " + e.getMessage());
        }
    }
}
