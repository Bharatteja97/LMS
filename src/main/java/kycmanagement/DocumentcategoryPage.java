package kycmanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DocumentcategoryPage {

    WebDriver driver;
    WebDriverWait wait;

    // ─── Main Page Locators ───────────────────────────────────────────────────
    // "Create document category" button (top-right of the listing page)
    private By createCategoryBtn = By.xpath(
        "//button[contains(., 'Create document category')]"
    );

    // ─── Modal Form Locators ──────────────────────────────────────────────────
    // Name input – plain <input> with placeholder
    private By nameInput = By.xpath("//input[@placeholder='Enter category name']");

    // Description – <textarea> with placeholder
    private By descriptionInput = By.xpath("//textarea[@placeholder='Enter description...']");

    // Customer Type – custom dropdown trigger (the visible div/button showing "Select customer type")
    private By customerTypeDropdownTrigger = By.xpath(
        "//*[contains(text(), 'Select customer type')]"
    );

    // Save button – has an SVG icon child, so use contains() on text node
    // Using normalize-space on the full button text including icon may vary; use contains() instead
    private By saveBtn = By.xpath(
        "//button[contains(., 'Save') and not(contains(., 'Create'))]"
    );

    // Close button inside the modal footer
    private By closeBtn = By.xpath(
        "//button[contains(., 'Close') and not(@aria-label)]"
    );

    // ─── Constructor ──────────────────────────────────────────────────────────
    public DocumentcategoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ─── Actions ──────────────────────────────────────────────────────────────

    /** Clicks the "Create document category" button on the listing page. */
    public void clickCreateCategory() {
        wait.until(ExpectedConditions.elementToBeClickable(createCategoryBtn)).click();
    }

    /**
     * Fills in the Create Category modal form.
     *
     * @param name         Category name  (e.g. "Address Proof")
     * @param description  Description text
     * @param customerType Visible option text shown in the dropdown (e.g. "Borrower")
     */
    public void enterCategoryDetails(String name, String description, String customerType) {

        // Name
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput)).sendKeys(name);

        // Description
        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(description);

        // Customer Type – custom dropdown (click trigger, then click option)
        selectCustomerType(customerType);
    }

    /**
     * Selects a Customer Type from the custom dropdown.
     * Tries multiple option-locator strategies to handle various dropdown implementations.
     */
    public void selectCustomerType(String customerType) {
        // 1. Click the dropdown trigger to open the options list
        try {
            wait.until(ExpectedConditions.elementToBeClickable(customerTypeDropdownTrigger)).click();
            System.out.println("Clicked customer type dropdown trigger.");
        } catch (Exception e) {
            System.out.println("Primary dropdown trigger not found, trying label-based locator: " + e.getMessage());
            try {
                WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//label[contains(., 'Customer Type')]/following::*[contains(@class,'select') or contains(@role,'combobox')][1]")
                ));
                trigger.click();
            } catch (Exception ex) {
                System.out.println("Fallback dropdown trigger also failed: " + ex.getMessage());
            }
        }

        // 2. Click the matching option in the opened list
        try {
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(@role,'option') and contains(., '" + customerType + "')] | " +
                         "//li[contains(., '" + customerType + "')] | " +
                         "//div[contains(@class,'option') and contains(., '" + customerType + "')] | " +
                         "//*[contains(@class,'item') and normalize-space(text())='" + customerType + "']")
            ));
            option.click();
            System.out.println("Selected customer type: " + customerType);
        } catch (Exception e) {
            System.out.println("Could not select customer type option '" + customerType + "': " + e.getMessage());
        }
    }

    /** Clicks the Save button to submit the form. */
    public void clickSave() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveBtn));
        btn.click();
    }

    /** Clicks the Close button to dismiss the modal without saving. */
    public void clickClose() {
        wait.until(ExpectedConditions.elementToBeClickable(closeBtn)).click();
    }
}
