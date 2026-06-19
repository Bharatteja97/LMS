package productconfiguration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class SchemePage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators
    private By createSchemeBtn = By.xpath("//button[contains(normalize-space(.), 'Create scheme') or contains(normalize-space(.), 'Create Scheme')]");

    // Basic Information
    private By schemeNameInput = By.xpath("//input[@placeholder='Enter scheme name'] | //label[contains(normalize-space(.), 'Scheme Name')]/following::input[1]");
    private By schemeCodeInput = By.xpath("//input[@placeholder='Enter scheme code'] | //label[contains(normalize-space(.), 'Scheme Code')]/following::input[1]");

    // Product dropdown — could be native <select> or custom div
    private By productSelectNative = By.xpath("//label[contains(normalize-space(.), 'Product')]/following::select[1]");
    private By productDropdownCustom = By.xpath("//*[normalize-space(.)='Select a product']");

    // Financial Parameters (pre-filled with '0', target by label)
    private By minLoanAmountInput = By.xpath("//label[contains(normalize-space(.), 'Minimum Loan Amount')]/following::input[1]");
    private By maxLoanAmountInput = By.xpath("//label[contains(normalize-space(.), 'Maximum Loan Amount')]/following::input[1]");
    private By minLTVInput = By.xpath("//label[contains(normalize-space(.), 'Minimum LTV')]/following::input[1]");
    private By maxLTVInput = By.xpath("//label[contains(normalize-space(.), 'Maximum LTV')]/following::input[1]");

    // Interest & Tenures
    private By minInterestInput = By.xpath("//label[contains(normalize-space(.), 'Minimum Interest')]/following::input[1]");
    private By maxInterestInput = By.xpath("//label[contains(normalize-space(.), 'Maximum Interest')]/following::input[1]");

    // Tenure Frequency — native <select>
    private By tenureFreqSelectNative = By.xpath("//label[contains(normalize-space(.), 'Tenure') and contains(normalize-space(.), 'Frequen')]/following::select[1]");
    private By tenureFreqDropdownCustom = By.xpath("//*[normalize-space(.)='Select tenure frequency']");

    private By minTenureInput = By.xpath("//label[contains(normalize-space(.), 'Minimum Tenure')]/following::input[1]");
    private By maxTenureInput = By.xpath("//label[contains(normalize-space(.), 'Maximum Tenure')]/following::input[1]");
    private By defaultTenureInput = By.xpath("//label[contains(normalize-space(.), 'Default Tenure')]/following::input[1]");

    // Personal Configuration section
    private By employmentTypeSelect = By.xpath("//label[contains(normalize-space(.), 'Employment Type')]/following::select[1]");
    private By minSalaryInput = By.xpath("//label[contains(normalize-space(.), 'Minimum Salary')]/following::input[1]");
    private By minCibilInput = By.xpath("//label[contains(normalize-space(.), 'Minimum CIBIL')]/following::input[1]");
    private By minWorkExpInput = By.xpath("//label[contains(normalize-space(.), 'Minimum Work Experience')]/following::input[1]");
    private By maxFoirInput = By.xpath("//label[contains(normalize-space(.), 'Maximum FOIR') or contains(normalize-space(.), 'FOIR')]/following::input[1]");

    // Charges & Details — custom dropdown with chevron arrow
    private By associatedChargesDropdown = By.xpath(
        "//div[contains(normalize-space(.), 'Select charges') and (contains(@class,'select') or contains(@class,'dropdown') or contains(@class,'control') or @role='combobox')] | " +
        "//*[@placeholder='Select charges']/ancestor::div[1] | " +
        "//*[normalize-space(text())='Select charges']/parent::div"
    );
    private By descriptionInput = By.xpath("//textarea[contains(@placeholder, 'Enter scheme description')] | //label[contains(normalize-space(.), 'Description')]/following::textarea[1]");

    // Actions
    private By saveSchemeBtn = By.xpath("//button[contains(normalize-space(.), 'Save Scheme') or contains(normalize-space(.), 'Save scheme')]");

    public SchemePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void clickCreateScheme() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(createSchemeBtn)).click();
        } catch (Exception e) {
            WebElement btn = driver.findElement(createSchemeBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public void enterBasicInformation(String name, String code, String productName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(schemeNameInput)).sendKeys(name);
        wait.until(ExpectedConditions.visibilityOfElementLocated(schemeCodeInput)).sendKeys(code);

        // Try native <select> first for product
        boolean selected = false;
        try {
            List<WebElement> selects = driver.findElements(productSelectNative);
            if (!selects.isEmpty() && selects.get(0).isDisplayed()) {
                Select sel = new Select(selects.get(0));
                try { sel.selectByVisibleText(productName); }
                catch (Exception e) { sel.selectByValue(productName); }
                selected = true;
            }
        } catch (Exception ignored) {}

        if (!selected) {
            // Custom dropdown fallback
            clickElementWithFallback(productDropdownCustom);
            clickOptionByText(productName);
        }
    }

    private void clearAndSendKeys(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='';", element);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(value);
    }

    public void enterFinancialParameters(String minLoan, String maxLoan, String minLTV, String maxLTV) {
        clearAndSendKeys(minLoanAmountInput, minLoan);
        clearAndSendKeys(maxLoanAmountInput, maxLoan);
        clearAndSendKeys(minLTVInput, minLTV);
        clearAndSendKeys(maxLTVInput, maxLTV);
    }

    public void enterInterestAndTenures(String minInt, String maxInt, String freq, String minTen, String maxTen, String defTen) {
        clearAndSendKeys(minInterestInput, minInt);
        clearAndSendKeys(maxInterestInput, maxInt);

        // Try native <select> first for tenure frequency
        boolean selected = false;
        try {
            List<WebElement> selects = driver.findElements(tenureFreqSelectNative);
            if (!selects.isEmpty() && selects.get(0).isDisplayed()) {
                Select sel = new Select(selects.get(0));
                try { sel.selectByVisibleText(freq); }
                catch (Exception e) { sel.selectByValue(freq); }
                selected = true;
            }
        } catch (Exception ignored) {}

        if (!selected) {
            clickElementWithFallback(tenureFreqDropdownCustom);
            clickOptionByText(freq);
        }

        clearAndSendKeys(minTenureInput, minTen);
        clearAndSendKeys(maxTenureInput, maxTen);
        clearAndSendKeys(defaultTenureInput, defTen);
    }

    /**
     * Fills the Personal Configuration section.
     * employmentType: visible text of the Employment Type native select option
     */
    public void enterPersonalConfiguration(String employmentType, String minSalary,
                                           String minCibil, String minWorkExp, String maxFoir) {
        // Employment Type is a native <select>
        try {
            WebElement sel = wait.until(ExpectedConditions.presenceOfElementLocated(employmentTypeSelect));
            Select select = new Select(sel);
            try { select.selectByVisibleText(employmentType); }
            catch (Exception e) { select.selectByValue(employmentType); }
        } catch (Exception e) {
            System.out.println("Could not select Employment Type: " + e.getMessage());
        }

        clearAndSendKeys(minSalaryInput, minSalary);
        clearAndSendKeys(minCibilInput, minCibil);
        clearAndSendKeys(minWorkExpInput, minWorkExp);
        clearAndSendKeys(maxFoirInput, maxFoir);
    }

    public void enterChargesAndDetails(String charge, String desc) {
        try {
            // Click the dropdown trigger
            clickElementWithFallback(associatedChargesDropdown);
            // Wait briefly for options to render
            try { Thread.sleep(800); } catch (InterruptedException ignored) {}

            // Try multiple option element types used by custom dropdowns
            By optionLocator = By.xpath(
                "//*[contains(normalize-space(.), '" + charge + "') and " +
                "(self::li or self::div or self::span or self::option or @role='option' or @role='menuitem')]"
            );
            try {
                WebElement opt = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
                opt.click();
            } catch (Exception e) {
                try {
                    WebElement opt = driver.findElement(optionLocator);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
                } catch (Exception ex) {
                    System.out.println("Could not select charge: " + charge + " - " + ex.getMessage());
                }
            }
            // Close multi-select dropdown if still open
            try { driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE); } catch (Exception ignored) {}
        } catch (Exception e) {
            System.out.println("Could not open charges dropdown: " + e.getMessage());
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(desc);
    }

    public void clickSaveScheme() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(saveSchemeBtn)).click();
        } catch (Exception e) {
            WebElement btn = driver.findElement(saveSchemeBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    // ---- Helpers ----

    private void clickElementWithFallback(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            WebElement el = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    private void clickOptionByText(String text) {
        By opt = By.xpath(
            "//li[contains(normalize-space(.), '" + text + "')] | " +
            "//*[@role='option' and contains(normalize-space(.), '" + text + "')] | " +
            "//*[@role='menuitem' and contains(normalize-space(.), '" + text + "')]"
        );
        try {
            wait.until(ExpectedConditions.elementToBeClickable(opt)).click();
        } catch (Exception e) {
            try {
                WebElement o = driver.findElement(opt);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", o);
            } catch (Exception ex) {
                System.out.println("Could not click option: " + text);
            }
        }
    }
}
