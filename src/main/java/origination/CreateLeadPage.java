package origination;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;

public class CreateLeadPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators based on placeholder text or label text (can be updated with actual
    // IDs/CSS later)

    // Vehicle Type Dropdown — actual element is a <select> (not a combobox div)
    @FindBy(xpath = "//select[@name='schemeMasterId']")
    private WebElement vehicleTypeDropdown;

    // PAN Number
    @FindBy(xpath = "//input[@placeholder='Enter Borrower PAN']")
    private WebElement panNumberInput;

    @FindBy(xpath = "//button[contains(text(),'Verify')]")
    private WebElement verifyButton;

    // Name
    @FindBy(xpath = "//input[@placeholder='Enter name']")
    private WebElement nameInput;

    // Contact
    @FindBy(xpath = "//input[@placeholder='Enter contact number']")
    private WebElement contactInput;

    @FindBy(xpath = "//button[contains(text(),'Fetch CIBIL')]")
    private WebElement fetchCibilButton;

    // Email
    @FindBy(xpath = "//input[@placeholder='Enter email']")
    private WebElement emailInput;

    // Estimated Cost
    @FindBy(xpath = "//input[@placeholder='Enter estimated cost']")
    private WebElement estimatedCostInput;

    // Loan Amount
    @FindBy(xpath = "//input[@placeholder='Enter loan amount']")
    private WebElement loanAmountInput;

    // Down Payment
    @FindBy(xpath = "//input[@placeholder='Enter down payment']")
    private WebElement downPaymentInput;

    // Tenure
    @FindBy(xpath = "//input[@placeholder='Enter tenure']")
    private WebElement tenureInput;

    // State Dropdown
    @FindBy(xpath = "//label[contains(., 'State')]/following::select[1] | //input[contains(@placeholder, 'state') or contains(@placeholder, 'State')] | //*[contains(text(), 'Select a state') or contains(text(), 'Select State')]")
    private WebElement stateInput;

    // City Dropdown
    @FindBy(xpath = "//label[contains(., 'City')]/following::select[1] | //input[contains(@placeholder, 'city') or contains(@placeholder, 'City')] | //*[contains(text(), 'Select a city') or contains(text(), 'Select City')]")
    private WebElement cityInput;

    // Vehicle Brand field (loaded dynamically under additional info, if visible)
    @FindBy(xpath = "//label[contains(normalize-space(text()), 'Vehicle Brand')]/following::input[1] | //input[@placeholder='Enter vehicle brand']")
    private WebElement vehicleBrandInput;


    // Submit Button
    @FindBy(xpath = "//button[@type='submit']")
    private WebElement submitButton;

    public CreateLeadPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    private void clickWithJs(WebElement element) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(element));
                scrollToElement(element);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                break;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                System.out.println("[INFO] Stale element encountered in clickWithJs. Retrying...");
            }
            attempts++;
        }
    }

    private void scrollToElement(WebElement element) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", element);
            Thread.sleep(200); // Give a small pause for the scroll to settle
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
            // Ignore stale exception in scrolling, it will be handled in the main interaction
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void enterText(WebElement element, String text) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                wait.until(ExpectedConditions.visibilityOf(element));
                scrollToElement(element);
                element.click();
                element.clear();
                element.sendKeys(text);
                break;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                System.out.println("[INFO] Stale element encountered in enterText. Retrying...");
            } catch (org.openqa.selenium.InvalidElementStateException e) {
                // InvalidElementStateException is thrown for disabled or readonly fields (like
                // auto-calculated Down Payment)
                // ElementNotInteractableException is a subclass of it.
                System.out.println(
                        "Could not interact natively with element (might be readonly/calculated). Attempting JS fallback...");
                try {
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                            "if (!arguments[0].readOnly && !arguments[0].disabled) {" +
                                    "  arguments[0].focus();" +
                                    "  arguments[0].value = arguments[1];" +
                                    "  arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                                    "  arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
                                    "}",
                            element, text);
                } catch (Exception jsException) {
                    System.out.println("JS Fallback also failed or skipped. Field is likely read-only.");
                }
                break;
            }
            attempts++;
        }
    }

    private void waitAndSelectByText(By locator, String visibleText, String fieldName) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            scrollToElement(element);
                if (!element.getTagName().equalsIgnoreCase("select")) {
                    System.out.println("[WARNING] Expected a <select> element for " + fieldName + ", but found " + element.getTagName());
                    // Fallback for non-select dropdowns could be implemented here if needed.
                    return;
                }

            org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(element);
            try {
                select.selectByVisibleText(visibleText);
            } catch (Exception e) {
                // Try case-insensitive match
                for (WebElement opt : select.getOptions()) {
                    if (opt.getText().equalsIgnoreCase(visibleText) || opt.getText().toLowerCase().contains(visibleText.toLowerCase())) {
                        select.selectByVisibleText(opt.getText());
                        return;
                    }
                }
                System.out.println("[WARNING] Could not select " + visibleText + " in " + fieldName);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Exception while selecting " + fieldName + ": " + e.getMessage());
        }
    }

    /**
     * DIAGNOSTIC HELPER — prints every option in the Vehicle Type dropdown to
     * System.out so that the correct exact text can be hard-coded in tests.
     *
     * Usage: call this after clickAddLead() to see what the API returns for
     * each product type URL (VEHICLE_LOAN, TWO_WHEELER_LOAN, etc.).
     */
    public void dumpVehicleTypeOptions() {
        By selectLocator = By.xpath("//select[@name='schemeMasterId']");
        try {
            WebElement selectElement =
                    wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
            
            // Wait for API to load options
            wait.until(d -> {
                List<WebElement> opts = selectElement.findElements(By.tagName("option"));
                return opts.stream()
                
                        .anyMatch(o -> !o.getText().trim().isEmpty()
                                && !o.getText().trim().toLowerCase().startsWith("select"));
            });

            List<WebElement> opts = selectElement.findElements(By.tagName("option"));
            System.out.println("[DUMP] Vehicle Type options loaded: " + opts.size());
            for (WebElement opt : opts) {
                System.out.println("  OPTION  value=\"" + opt.getAttribute("value")
                        + "\"  text=\"" + opt.getText().trim() + "\"");
            }
        } catch (Exception e) {
            System.out.println("[DUMP] Could not load Vehicle Type options: " + e.getMessage());
        }
    }

    public void enterPanNumber(String pan) {
        enterText(panNumberInput, pan);
    }

    public void clickVerifyPan() {
        clickWithJs(verifyButton);
    }

    public void selectVehicleType(String type) {
        try {
            // Wait for the <select> element to be present and visible
            WebElement selectEl = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//select[@name='schemeMasterId']")));

            scrollToElement(selectEl);

            // Wait specifically for the matching option to be populated by the API
            String normalizedType = type.toLowerCase().replaceAll("[^a-z0-9]", "");
            wait.until(d -> {
                Select select = new Select(selectEl);
                for (WebElement opt : select.getOptions()) {
                    String text = opt.getText().trim();
                    String val = opt.getAttribute("value");
                    String normalizedOpt = text.toLowerCase().replaceAll("[^a-z0-9]", "");
                    if (val.equals(type) || normalizedOpt.equals(normalizedType) 
                            || normalizedType.contains(normalizedOpt) 
                            || normalizedOpt.contains(normalizedType)) {
                        return true;
                    }
                }
                return false;
            });

            Select select = new Select(selectEl);

            // Try selecting by visible text first (with normalization for hyphens, spaces, and casing)
            boolean selected = false;
            for (WebElement opt : select.getOptions()) {
                String optionText = opt.getText().trim();
                String normalizedOpt = optionText.toLowerCase().replaceAll("[^a-z0-9]", "");
                if (!normalizedOpt.isEmpty() && !normalizedOpt.equals("selectvehicletype")) {
                    if (normalizedOpt.equals(normalizedType) 
                            || normalizedType.contains(normalizedOpt) 
                            || normalizedOpt.contains(normalizedType)) {
                        select.selectByVisibleText(optionText);
                        selected = true;
                        break;
                    }
                }
            }

            // Fallback: select by value attribute
            if (!selected) {
                try {
                    select.selectByValue(type);
                    selected = true;
                } catch (Exception ignored) {}
            }

            if (selected) {
                System.out.println("[INFO] Vehicle Type selected successfully: " + type);
            } else {
                System.out.println("[ERROR] Vehicle Type '" + type + "' not found in options.");
                System.out.println("[INFO] Available options:");
                for (WebElement opt : select.getOptions()) {
                    System.out.println("  -> '" + opt.getText().trim() + "' (value='" + opt.getAttribute("value") + "')");
                }
                throw new RuntimeException("Could not select Vehicle Type: " + type);
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to select Vehicle Type: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to select Vehicle Type: " + type, e);
        }
    }


    public void enterName(String name) {
        enterText(nameInput, name);
    }

    public void enterContact(String contact) {
        enterText(contactInput, contact);
    }

    public void clickFetchCibil() {
        clickWithJs(fetchCibilButton);
    }

    public void enterEmail(String email) {
        enterText(emailInput, email);
    }

    public void enterEstimatedCost(String cost) {
        enterText(estimatedCostInput, cost);
    }

    public void enterLoanAmount(String amount) {
        enterText(loanAmountInput, amount);
    }

    public void enterDownPayment(String downPayment) {
        enterText(downPaymentInput, downPayment);
    }

    public void enterTenure(String tenure) {
        enterText(tenureInput, tenure);
    }



    public void selectState(String state) {
        By locator = By.xpath("//label[contains(., 'State')]/following::select[1]");
        waitAndSelectByText(locator, state, "State");
    }

    public void selectCity(String city) {
        By locator = By.xpath("//label[contains(., 'City')]/following::select[1]");
        waitAndSelectByText(locator, city, "City");
    }

    public void enterVehicleBrand(String brand) {
        try {
            // Wait a short duration to see if the element becomes visible dynamically
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement element = shortWait.until(ExpectedConditions.visibilityOf(vehicleBrandInput));
            enterText(element, brand);
            System.out.println("[INFO] Entered Vehicle Brand '" + brand + "' successfully.");
        } catch (Exception e) {
            System.out.println("[INFO] Vehicle Brand input is not present or visible in the UI. Skipping brand selection.");
            System.out.println("[DIAGNOSTIC] Exception: " + e.getMessage());
            try {
                List<WebElement> labels = driver.findElements(By.tagName("label"));
                System.out.println("[DIAGNOSTIC] Found labels on page:");
                for (WebElement label : labels) {
                    if (!label.getText().trim().isEmpty()) {
                        System.out.println("  Label: " + label.getText().trim());
                    }
                }
                List<WebElement> inputs = driver.findElements(By.tagName("input"));
                System.out.println("[DIAGNOSTIC] Found inputs (placeholders) on page:");
                for (WebElement input : inputs) {
                    String ph = input.getAttribute("placeholder");
                    String id = input.getAttribute("id");
                    String name = input.getAttribute("name");
                    String type = input.getAttribute("type");
                    System.out.println("  Input: id='" + id + "', name='" + name + "', type='" + type + "', placeholder='" + ph + "'");
                }
                List<WebElement> selects = driver.findElements(By.tagName("select"));
                System.out.println("[DIAGNOSTIC] Found select elements on page:");
                for (WebElement select : selects) {
                    String id = select.getAttribute("id");
                    String name = select.getAttribute("name");
                    System.out.println("  Select: id='" + id + "', name='" + name + "'");
                }
            } catch (Exception ex) {
                System.out.println("[DIAGNOSTIC] Failed to dump page elements: " + ex.getMessage());
            }
        }
    }

    public void submitLead() {
        clickWithJs(submitButton);
    }

    public void createNewLead(String vehicleType, String pan, String name, String contact,
            String email, String estCost, String loanAmt, String downPayment,
            String tenure, String state, String city, String brand) {
        selectVehicleType(vehicleType);
        enterPanNumber(pan);
        clickVerifyPan();
        enterName(name);
        enterContact(contact);
        clickFetchCibil();
        enterEmail(email);
        enterEstimatedCost(estCost);
        enterLoanAmount(loanAmt);
        enterDownPayment(downPayment);
        enterTenure(tenure);
        selectState(state);
        selectCity(city);
        enterVehicleBrand(brand);
        
        // Wait briefly for any async autofills to finish, then forcefully re-enter the unique name
        try {
            Thread.sleep(3000);
        } catch(Exception e) {}
        enterName(name);

        submitLead();
    }
}
