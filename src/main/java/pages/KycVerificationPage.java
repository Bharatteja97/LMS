package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the KYC Verification page (client-lms.alfinnext.com/pager).
 * Title: "Client Accertify"
 *
 * Sections on the page:
 * - KYC Verification header
 * - Borrower Document (PAN, Aadhaar Front, Aadhaar Back uploads)
 * - Borrower Details (Residential Type, Residential Stability, Employment Type,
 * Name, PAN field + Verify button, Age)
 * - Co-Borrower Details
 * - Dealer Details
 * - Additional uploads (Quotation, Bank Statement, Borrower Image, Address
 * Verification,
 * Utility Bill, Salary Slips, ITR)
 * - Seller Document (Seller Aadhaar, Seller Pan)
 * - Submit Form button (blue, bg-blue-600) at the bottom
 */
public class KycVerificationPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // ─── Header ──────────────────────────────────────────────────────────
    @FindBy(xpath = "//*[contains(text(),'KYC Verification')]")
    private WebElement kycVerificationHeader;

    // ─── Borrower Details fields ─────────────────────────────────────────
    @FindBy(xpath = "//select[@name='residentialType']")
    private WebElement residentialTypeSelect;

    @FindBy(xpath = "//label[contains(normalize-space(),'Residential Stability')]/following::select[1]")
    private WebElement residentialStabilitySelect;

    @FindBy(xpath = "//select[@name='employmentType']")
    private WebElement employmentTypeSelect;

    // Borrower PAN verify button (for in-page PAN verification)
    @FindBy(xpath = "(//button[text()='Verify'])[1]")
    private WebElement borrowerPanVerifyButton;

    // ─── Submit Form button ──────────────────────────────────────────────
    @FindBy(xpath = "//button[normalize-space(text())='Submit Form']")
    private WebElement submitFormButton;

    public KycVerificationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
    }

    /**
     * Wait for the KYC page to fully load by checking for the header.
     */
    public void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOf(kycVerificationHeader));
        System.out.println("[INFO] KYC Verification page loaded. Header visible.");
    }

    /**
     * Verify the page title is "Client Accertify".
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Check whether the KYC Verification header is displayed.
     */
    public boolean isKycHeaderDisplayed() {
        try {
            return kycVerificationHeader.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check whether the Submit Form button is present and visible.
     */
    public boolean isSubmitFormButtonDisplayed() {
        try {
            scrollToElement(submitFormButton);
            return submitFormButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click the Submit Form button at the bottom of the page.
     */
    public void clickSubmitForm() {
        scrollToElement(submitFormButton);
        wait.until(ExpectedConditions.elementToBeClickable(submitFormButton));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", submitFormButton);
        System.out.println("[INFO] Clicked 'Submit Form' button.");
    }

    /**
     * Click the Borrower PAN Verify button.
     */
    public void clickBorrowerPanVerify() {
        scrollToElement(borrowerPanVerifyButton);
        wait.until(ExpectedConditions.elementToBeClickable(borrowerPanVerifyButton));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();",
                borrowerPanVerifyButton);
        System.out.println("[INFO] Clicked Borrower PAN 'Verify' button.");
    }

    /**
     * Returns the count of upload zones found on the page (areas with "Click to
     * upload").
     */
    public int getUploadZoneCount() {
        List<WebElement> zones = driver.findElements(
                By.xpath("//*[contains(text(),'Click to upload')]"));
        System.out.println("[INFO] Found " + zones.size() + " upload zones on KYC page.");
        return zones.size();
    }

    /**
     * Returns all section heading texts found on the page
     * (Borrower Document, Borrower Details, Co-Borrower Details, etc.)
     */
    public List<String> getSectionHeadings() {
        List<WebElement> headings = driver.findElements(
                By.xpath("//h2 | //h3 | //*[contains(@class,'font-bold') or contains(@class,'font-semibold')]"));
        List<String> texts = new java.util.ArrayList<>();
        for (WebElement h : headings) {
            String t = h.getText().trim();
            if (!t.isEmpty())
                texts.add(t);
        }
        System.out.println("[INFO] Section headings: " + texts);
        return texts;
    }

    private void scrollToElement(WebElement element) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'});", element);
            Thread.sleep(300);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception ignored) {
        }
    }

    /**
     * Uploads a document to the n-th file input on the page.
     *
     * @param index            1-based index of the file input
     * @param absoluteFilePath Absolute path to the file to upload
     */
    public void uploadDocumentByIndex(int index, String absoluteFilePath) {
        System.out.println("[INFO] Uploading document to file input index " + index + "...");
        String xpath = "(//input[@type='file'])[" + index + "]";
        try {
            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

            // Unhide the element if it's hidden to ensure sendKeys works
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.display='block'; arguments[0].style.visibility='visible'; arguments[0].style.opacity='1';",
                    fileInput);

            fileInput.sendKeys(absoluteFilePath);
            System.out.println("[INFO] Successfully uploaded file to input index " + index);
            Thread.sleep(2000); // Allow time for upload to process visually
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.out.println("[WARN] Upload interrupted at index " + index);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to upload document at index " + index + ": " + e.getMessage());
        }
    }

    /**
     * Uploads a document by finding the file input closest to a specific label.
     */
    public void uploadDocumentByLabel(String labelText, String absoluteFilePath) {
        System.out.println("[INFO] Uploading document for label: " + labelText);
        String xpath = "//*[contains(normalize-space(text()), '" + labelText + "')]/following::input[@type='file'][1]";
        try {
            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.display='block'; arguments[0].style.visibility='visible'; arguments[0].style.opacity='1';",
                    fileInput);

            fileInput.sendKeys(absoluteFilePath);
            System.out.println("[INFO] Successfully uploaded file for label " + labelText);
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.out.println("[WARN] Upload interrupted for label: " + labelText);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to upload document for label " + labelText + ": " + e.getMessage());
        }
    }

    /**
     * Fills an input field based on its preceding label text.
     * Skips hidden inputs so React state inputs do not cause a 30s timeout.
     * Uses JS value setter so React number/text inputs register the change.
     */
    public void fillInputByLabel(String labelText, String value) {
        System.out.println("[INFO] Filling input for label: " + labelText);
        // [not(@type='hidden')] skips hidden React state inputs
        String xpath = "//label[contains(normalize-space(.), '" + labelText
                + "')]/following::input[not(@type='hidden')][1]";
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            scrollToElement(input);
            // JS setter fires React synthetic events; sendKeys alone can miss them on number inputs
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "var el = arguments[0]; var val = arguments[1];" +
                    "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                    "setter.call(el, val);" +
                    "el.dispatchEvent(new Event('input',  {bubbles:true}));" +
                    "el.dispatchEvent(new Event('change', {bubbles:true}));",
                    input, value);
            System.out.println("[INFO] Successfully filled '" + labelText + "' with value: " + value);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to fill input for label " + labelText + ": " + e.getMessage());
        }
    }

    /**
     * Selects an option from a dropdown (select) based on its preceding label.
     * Uses JavaScript to set the value and fires both 'input' and 'change' events
     * so React's synthetic event system picks up the change.
     */
    public void selectDropdownByLabel(String labelText, String value) {
        System.out.println("[INFO] Selecting '" + value + "' for dropdown: " + labelText);
        String xpath = "//label[contains(normalize-space(.), '" + labelText + "')]/following::select[1]";
        try {
            WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            scrollToElement(selectElement);

            // Use JavaScript to set value and trigger React-compatible events.
            // NOTE: 'newVal' is used instead of 'value' to avoid shadowing
            // HTMLSelectElement.value in the nativeInputValueSetter call.
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "var select = arguments[0];" +
                    "var newVal = arguments[1];" +
                    "var setter = Object.getOwnPropertyDescriptor(window.HTMLSelectElement.prototype, 'value').set;" +
                    "setter.call(select, newVal);" +
                    "select.dispatchEvent(new Event('input',  { bubbles: true }));" +
                    "select.dispatchEvent(new Event('change', { bubbles: true }));",
                    selectElement, value);

            Thread.sleep(500);
            System.out.println("\u2705 Selected '" + value + "' for dropdown label '" + labelText + "'");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.out.println("[WARN] selectDropdownByLabel interrupted for label: " + labelText);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to select dropdown for label " + labelText + ": " + e.getMessage());
        }
    }

    /**
     * Selects an option from a dropdown using its name attribute and value.
     * Uses JavaScript to set the value and fires both 'input' and 'change' events
     * so React's synthetic event system picks up the change.
     */
    public void selectDropdownByNameAndValue(String name, String value) {
        try {
            WebElement selectElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//select[@name='" + name + "']")));

            scrollToElement(selectElement);

            // Use JavaScript to set value and trigger React-compatible events.
            // NOTE: 'newVal' is used instead of 'value' to avoid shadowing
            // HTMLSelectElement.value in the native setter call.
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "var select = arguments[0];" +
                    "var newVal = arguments[1];" +
                    "var setter = Object.getOwnPropertyDescriptor(window.HTMLSelectElement.prototype, 'value').set;" +
                    "setter.call(select, newVal);" +
                    "select.dispatchEvent(new Event('input',  { bubbles: true }));" +
                    "select.dispatchEvent(new Event('change', { bubbles: true }));",
                    selectElement, value);

            Thread.sleep(500);
            System.out.println("\u2705 Selected '" + value + "' from dropdown '" + name + "'");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.out.println("[WARN] selectDropdownByNameAndValue interrupted for name: " + name);
        } catch (Exception e) {
            System.out.println("\u26a0\ufe0f Failed to select Dropdown '" + name + "': " + e.getMessage());
        }
    }

    /**
     * Clicks a button near a label or specific text (e.g. "Verify" next to PAN).
     */
    public void clickButtonNearLabel(String labelText, String buttonText) {
        System.out.println("[INFO] Clicking '" + buttonText + "' near label: " + labelText);
        String xpath = "//label[contains(normalize-space(.), '" + labelText
                + "')]/following::button[contains(normalize-space(.), '" + buttonText + "')][1]";
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            scrollToElement(btn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            System.out.println("[INFO] Successfully clicked '" + buttonText + "' near label '" + labelText + "'");
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to click button '" + buttonText + "' near label " + labelText + ": "
                    + e.getMessage());
        }
    }

    // Handles any unexpected alerts. Uses a 3-second timeout so valid PAN (no alert) does not add a 30s delay.
    public void handleAlertIfExists() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            org.openqa.selenium.Alert alert = shortWait.until(ExpectedConditions.alertIsPresent());
            System.out.println("[INFO] Handling alert: " + alert.getText());
            alert.accept();
        } catch (Exception e) {
            // No alert present - move on immediately
        }
    }

    // Clicks 'Validate Statement' in the bank statement modal.
    // Uses a 10-second timeout so it skips if the modal does not appear.
    public void clickValidateStatement() {
        System.out.println("[INFO] Waiting for 'Validate Bank Statement' modal...");
        String xpath = "//button[contains(normalize-space(.), 'Validate Statement')]";
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement btn = shortWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            System.out.println("[INFO] Clicked 'Validate Statement' button.");
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.out.println("[WARN] clickValidateStatement interrupted.");
        } catch (Exception e) {
            System.out.println("[INFO] Validate Statement modal not found - skipping.");
        }
    }

    /**
     * Closes any open modal dialogs (like the Validate Bank Statement modal)
     * if they get stuck open after an alert.
     */
    public void closeModalIfOpen() {
        System.out.println("[INFO] Attempting to close any open modals...");
        try {
            // Try to click the close (X) button, or remove the dialog from DOM as a fallback
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "var dialog = document.querySelector('div[role=\"dialog\"]');" +
                            "if (dialog) {" +
                            "  var btns = dialog.querySelectorAll('button');" +
                            "  if (btns.length > 0) {" +
                            "    btns[0].click();" +
                            "  } else {" +
                            "    dialog.remove();" +
                            "  }" +
                            "}");
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.out.println("[WARN] closeModalIfOpen interrupted.");
        } catch (Exception e) {
            System.out.println("[INFO] Could not close modal: " + e.getMessage());
        }
    }
}
