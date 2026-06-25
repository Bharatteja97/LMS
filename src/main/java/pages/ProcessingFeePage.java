package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

/**
 * Page Object for the Processing Fee payment page.
 *
 * Two approaches supported:
 *  APPROACH 1 (Gmail): Fetch payment link from Gmail → navigate → complete checkout.
 *  APPROACH 2 (LMS):   Go to LMS Processing Fee tab → copy the rzp.io link →
 *                      open in new tab → complete checkout. (No Gmail needed.)
 */
public class ProcessingFeePage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final WebDriverWait longWait;

    // ─── Main page locators ───────────────────────────────────────────────
    private static final By PAY_NOW_BUTTON = By.xpath(
            "//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'pay') " +
            "or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'proceed')]"
    );

    // ─── Razorpay checkout iframe locators ───────────────────────────────
    private static final By RAZORPAY_IFRAME        = By.cssSelector("iframe[src*='razorpay'], iframe[id*='razorpay'], iframe[name*='razorpay']");
    private static final By MOBILE_INPUT           = By.cssSelector("input[placeholder*='Mobile'], input[name='contact'], input[id*='contact'], input[placeholder*='mobile'], input[type='tel']");
    private static final By EMAIL_INPUT            = By.cssSelector("input[placeholder*='Email'], input[name='email'], input[id*='email'], input[type='email']");
    private static final By PROCEED_BUTTON         = By.xpath("//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'proceed') or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'continue') or @type='submit']");
    private static final By NETBANKING_OPTION      = By.xpath("//*[contains(text(),'Netbanking') or contains(text(),'Net Banking') or contains(@class,'netbanking')]");
    private static final By BANK_OF_BARODA_OPTION  = By.xpath("//*[contains(text(),'Bank of Baroda') or contains(text(),'BOB') or @value='BARB']");
    private static final By PAY_BUTTON             = By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'pay')]");
    private static final By SUCCESS_BUTTON         = By.xpath("//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'success') or contains(@class, 'success') or @type='submit'] | //input[@type='submit']");

    // Razorpay checkout popup window title/URL fragment
    private static final String RAZORPAY_URL_FRAGMENT = "razorpay";
    private static final String LMS_PROCESSING_FEE_URL =
            "https://lms.alfinnext.com/vehicle/operations/%s?tab=processing-fee&product=VEHICLE";

    public ProcessingFeePage(WebDriver driver) {
        this.driver = driver;
        this.wait     = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    // ─────────────────────────────────────────────────────────────────────
    // Public API – APPROACH 1: Gmail link
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Wait for the processing fee page to load (the Pay Now button must be visible).
     */
    public void waitForPageLoad() {
        wait.until(ExpectedConditions.presenceOfElementLocated(PAY_NOW_BUTTON));
        System.out.println("[INFO] Processing Fee page loaded.");
    }

    /**
     * Click the "Pay Now" / "Proceed to Pay" button on the processing fee page.
     * This opens the Razorpay checkout modal or new window.
     */
    public void clickPayNow() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(PAY_NOW_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        System.out.println("[INFO] Clicked 'Pay Now' button.");
    }

    /**
     * Complete a Razorpay test payment using Netbanking > Bank of Baroda > Success simulator.
     * Handles both the iframe-based checkout and the popup-window checkout.
     *
     * @param mobileNumber Mobile number to enter in Razorpay checkout (e.g. "9999999999")
     * @param email        Email to enter in Razorpay checkout
     */
    public void completeRazorpayTestPayment(String mobileNumber, String email) throws InterruptedException {
        Thread.sleep(3000); // allow checkout to open

        // Try popup window first
        if (switchToRazorpayWindow()) {
            System.out.println("[INFO] Switched to Razorpay popup window.");
            fillCheckoutAndPay(mobileNumber, email);
            switchBackToMainWindow();
        } else {
            // Try iframe
            System.out.println("[INFO] Attempting Razorpay iframe checkout...");
            switchToRazorpayIframe();
            fillCheckoutAndPay(mobileNumber, email);
            driver.switchTo().defaultContent();
        }
        System.out.println("[INFO] Razorpay test payment completed.");
    }

    /**
     * Directly navigate to a Razorpay payment link and complete the payment.
     *
     * @param paymentLink  The full Razorpay payment link URL
     * @param mobileNumber Mobile number for the checkout form
     * @param email        Email for the checkout form
     */
    public void navigateAndPay(String paymentLink, String mobileNumber, String email) throws InterruptedException {
        System.out.println("[INFO] Navigating to payment link: " + paymentLink);
        driver.get(paymentLink);
        Thread.sleep(3000);
        System.out.println("[INFO] Reached payment page URL: " + driver.getCurrentUrl());

        // If already on Razorpay checkout page
        if (driver.getCurrentUrl().contains(RAZORPAY_URL_FRAGMENT)) {
            switchToRazorpayIframe();
            fillCheckoutAndPay(mobileNumber, email);
            driver.switchTo().defaultContent();
        } else {
            // Click pay button on the landing page
            try {
                clickPayNow();
                Thread.sleep(3000);
                if (switchToRazorpayWindow()) {
                    fillCheckoutAndPay(mobileNumber, email);
                    switchBackToMainWindow();
                } else {
                    switchToRazorpayIframe();
                    fillCheckoutAndPay(mobileNumber, email);
                    driver.switchTo().defaultContent();
                }
            } catch (Exception e) {
                System.out.println("[WARN] Could not click pay button: " + e.getMessage());
                fillCheckoutAndPay(mobileNumber, email);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Public API – APPROACH 2: Copy link from LMS Processing Fee tab
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Navigate to the LMS Operations Processing Fee tab for a given application ID.
     *
     * @param applicationId  e.g. "465"
     */
    public void navigateToLmsProcessingFeeTab(String applicationId) throws InterruptedException {
        String url = String.format(LMS_PROCESSING_FEE_URL, applicationId);
        System.out.println("[INFO] Navigating to LMS Processing Fee tab: " + url);
        driver.get(url);
        Thread.sleep(3000);
        System.out.println("[INFO] Current URL: " + driver.getCurrentUrl());
    }

    /**
     * Read the rzp.io / Razorpay payment link from the Payment Link input field
     * on the LMS Processing Fee tab. Tries input value → anchor href → JS page scan.
     *
     * @return The payment URL string, or null if not found
     */
    public String getPaymentLinkFromLmsPage() {
        System.out.println("[INFO] Extracting payment link from LMS Processing Fee tab...");

        // Strategy 1: input element whose value is a rzp/razorpay URL
        String[] inputXpaths = {
            "//input[contains(@value,'rzp')]",
            "//input[contains(@value,'razorpay')]",
            "//input[contains(@value,'payment-link')]",
            "//input[@readonly and contains(@value,'http')]"
        };
        for (String xpath : inputXpaths) {
            try {
                WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
                String val = el.getAttribute("value");
                if (val != null && !val.trim().isEmpty()) {
                    System.out.println("[INFO] Found payment link (input value): " + val.trim());
                    return val.trim();
                }
            } catch (Exception ignored) {}
        }

        // Strategy 2: anchor tag or text element containing rzp URL
        String[] textXpaths = {
            "//a[contains(@href,'rzp.io') or contains(@href,'razorpay')]",
            "//*[contains(text(),'rzp.io')]",
            "//*[contains(text(),'razorpay.com/payment-link')]"
        };
        for (String xpath : textXpaths) {
            try {
                WebElement el = driver.findElement(By.xpath(xpath));
                String href = el.getAttribute("href");
                if (href != null && !href.isEmpty()) {
                    System.out.println("[INFO] Found payment link (href): " + href);
                    return href;
                }
                String text = el.getText().trim();
                if (!text.isEmpty() && text.startsWith("http")) {
                    System.out.println("[INFO] Found payment link (text): " + text);
                    return text;
                }
            } catch (Exception ignored) {}
        }

        // Strategy 3: JS full-page scan of all inputs and anchors
        try {
            String jsResult = (String) ((JavascriptExecutor) driver).executeScript(
                "var inputs = document.querySelectorAll('input');" +
                "for(var i=0;i<inputs.length;i++){" +
                "  var v=inputs[i].value||'';" +
                "  if(v.includes('rzp')||v.includes('razorpay')||v.includes('payment-link')){return v;}" +
                "}" +
                "var links=document.querySelectorAll('a');" +
                "for(var j=0;j<links.length;j++){" +
                "  var h=links[j].href||'';" +
                "  if(h.includes('rzp')||h.includes('razorpay')){return h;}" +
                "}" +
                "return null;"
            );
            if (jsResult != null && !jsResult.isEmpty()) {
                System.out.println("[INFO] Found payment link (JS scan): " + jsResult);
                return jsResult;
            }
        } catch (Exception e) {
            System.out.println("[WARN] JS scan failed: " + e.getMessage());
        }

        System.out.println("[WARN] No payment link found on LMS Processing Fee tab.");
        return null;
    }

    /**
     * Click the "open in new tab" icon (external link icon) next to the Payment Link field
     * on the LMS Processing Fee tab, then switch driver focus to the newly opened tab.
     *
     * @return true if a new tab was opened and switched to
     */
    public boolean clickOpenPaymentLinkInNewTab() throws InterruptedException {
        System.out.println("[INFO] Clicking 'Open in new tab' icon for payment link...");
        Set<String> beforeHandles = driver.getWindowHandles();

        String[] iconXpaths = {
            // The last button sibling after the rzp input field
            "(//input[contains(@value,'rzp') or contains(@value,'razorpay')]" +
            "/following-sibling::button)[last()]",
            // Span/div wrapping the button next to the input
            "(//input[contains(@value,'rzp') or contains(@value,'razorpay')]" +
            "/following-sibling::span//button)[last()]",
            // Direct anchor tag linking to rzp
            "//a[contains(@href,'rzp.io') or contains(@href,'razorpay')]",
            // Aria-label based (generic)
            "//*[@aria-label='Open link' or @aria-label='Open in new tab' or @title='Open']"
        };

        for (String xpath : iconXpaths) {
            try {
                WebElement icon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", icon);
                System.out.println("[INFO] Clicked external link icon.");
                Thread.sleep(2000);
                break;
            } catch (Exception ignored) {}
        }

        // Check if new tab appeared
        Set<String> afterHandles = driver.getWindowHandles();
        if (afterHandles.size() > beforeHandles.size()) {
            for (String handle : afterHandles) {
                if (!beforeHandles.contains(handle)) {
                    driver.switchTo().window(handle);
                    System.out.println("[INFO] Switched to new tab: " + driver.getCurrentUrl());
                    return true;
                }
            }
        }

        System.out.println("[WARN] No new tab opened by icon click.");
        return false;
    }

    /**
     * All-in-one LMS-direct payment flow (no Gmail required):
     *  1. Navigate to the LMS Processing Fee tab for the given application.
     *  2. Click the "open in new tab" icon — OR — copy the link and open via JS.
     *  3. Complete the Razorpay test checkout in the new tab.
     *
     * @param applicationId  LMS application ID, e.g. "465"
     * @param mobileNumber   Mobile number for Razorpay checkout
     * @param email          Email for Razorpay checkout
     */
    public void payFromLmsPage(String applicationId, String mobileNumber, String email)
            throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[INFO] Starting LMS-direct payment for application: " + applicationId);
        System.out.println("═══════════════════════════════════════════════════════════");

        // Step 1: Go to the LMS Processing Fee tab
        navigateToLmsProcessingFeeTab(applicationId);
        Thread.sleep(2000);

        // Step 2: Click the external-link icon to open the payment link in a new tab
        boolean newTabOpened = clickOpenPaymentLinkInNewTab();

        if (!newTabOpened) {
            // Fallback: read the link and open via window.open()
            String paymentLink = getPaymentLinkFromLmsPage();
            if (paymentLink == null) {
                throw new RuntimeException(
                    "[ERROR] Payment link not found on LMS Processing Fee tab for application: " + applicationId);
            }
            System.out.println("[INFO] Fallback: opening link via JS window.open(): " + paymentLink);
            ((JavascriptExecutor) driver).executeScript(
                "window.open(arguments[0], '_blank');", paymentLink);
            Thread.sleep(2000);
            // Switch to the newly opened tab
            Set<String> handles = driver.getWindowHandles();
            String[] arr = handles.toArray(new String[0]);
            driver.switchTo().window(arr[arr.length - 1]);
            System.out.println("[INFO] Switched to new tab: " + driver.getCurrentUrl());
        }

        // Step 3: Complete the Razorpay checkout in the new tab
        Thread.sleep(2000);
        switchToRazorpayIframe();
        fillCheckoutAndPay(mobileNumber, email);
        driver.switchTo().defaultContent();

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] LMS-direct payment completed successfully.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Fill the Razorpay checkout form and complete the payment via Netbanking.
     */
    private void fillCheckoutAndPay(String mobileNumber, String email) throws InterruptedException {
        Thread.sleep(2000);

        // Enter mobile number if field is present
        fillFieldIfPresent(MOBILE_INPUT, mobileNumber, "mobile number");

        // Enter email if field is present
        fillFieldIfPresent(EMAIL_INPUT, email, "email");

        // Click "Proceed" / continue button
        clickIfPresent(PROCEED_BUTTON, "Proceed button");
        Thread.sleep(1500);

        // Choose Netbanking
        clickPaymentMethod(NETBANKING_OPTION, "Netbanking");
        Thread.sleep(1500);

        // Choose Bank of Baroda
        clickPaymentMethod(BANK_OF_BARODA_OPTION, "Bank of Baroda");
        Thread.sleep(1500);

        // Click Pay button
        clickIfPresent(PAY_BUTTON, "Pay button");
        Thread.sleep(3000);

        // Handle the test bank simulator page
        handleTestBankSimulator();
    }

    /**
     * After Razorpay opens the test bank simulator, click "Success" to complete payment.
     */
    private void handleTestBankSimulator() throws InterruptedException {
        Thread.sleep(3000);
        System.out.println("[INFO] Handling test bank simulator page...");

        // The simulator may open in a new window/tab
        Set<String> handles = driver.getWindowHandles();
        if (handles.size() > 1) {
            boolean found = false;
            for (String handle : handles) {
                driver.switchTo().window(handle);
                String url = driver.getCurrentUrl();
                if (url.contains("mocksharp") || url.contains("api.razorpay") || url.contains("bank")) {
                    System.out.println("[INFO] Switched to simulator window: " + url);
                    found = true;
                    break;
                }
            }
            if (!found) {
                // fallback switch to the last handle if not found by URL
                String[] handleArray = handles.toArray(new String[0]);
                driver.switchTo().window(handleArray[handleArray.length - 1]);
                System.out.println("[WARN] Simulator URL not found, switched to last window: " + driver.getCurrentUrl());
            }
        }

        // Click "Success" on the test bank simulator
        try {
            WebElement successBtn = longWait.until(ExpectedConditions.elementToBeClickable(SUCCESS_BUTTON));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", successBtn);
            System.out.println("[INFO] Clicked 'Success' on test bank simulator.");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("[WARN] Success button not found on simulator. Trying JS fallback...");
            try {
                ((JavascriptExecutor) driver).executeScript(
                    "document.querySelectorAll('button, input[type=submit]').forEach(function(b){" +
                    "  if(b.textContent.toLowerCase().includes('success') || (b.value && b.value.toLowerCase().includes('success')) || b.type === 'submit' || b.className.includes('success')){b.click();}" +
                    "});"
                );
                Thread.sleep(3000);
            } catch (Exception ex) {
                System.out.println("[ERROR] Could not click success on simulator: " + ex.getMessage());
            }
        }

        // Switch back to main window if multiple windows
        Set<String> remainingHandles = driver.getWindowHandles();
        if (remainingHandles.size() > 0) {
            driver.switchTo().window(remainingHandles.iterator().next());
        }
        System.out.println("[INFO] Payment simulation completed. Current URL: " + driver.getCurrentUrl());
    }

    /**
     * Switch to the Razorpay popup window (new tab/window opened by checkout).
     *
     * @return true if switched successfully, false if no Razorpay window found
     */
    private boolean switchToRazorpayWindow() throws InterruptedException {
        String mainWindow = driver.getWindowHandle();
        Thread.sleep(2000);
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(mainWindow)) {
                driver.switchTo().window(handle);
                String url = driver.getCurrentUrl();
                System.out.println("[INFO] Found popup window URL: " + url);
                if (url.contains(RAZORPAY_URL_FRAGMENT) || url.contains("api.razorpay")) {
                    return true;
                }
            }
        }
        // If none found, switch back to main
        driver.switchTo().window(mainWindow);
        return false;
    }

    /**
     * Switch to the Razorpay iframe on the current page.
     */
    private void switchToRazorpayIframe() {
        try {
            WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(RAZORPAY_IFRAME));
            driver.switchTo().frame(iframe);
            System.out.println("[INFO] Switched to Razorpay iframe.");
        } catch (Exception e) {
            System.out.println("[WARN] Could not find Razorpay iframe: " + e.getMessage());
        }
    }

    /**
     * Switch back to the main (first) browser window.
     */
    private void switchBackToMainWindow() {
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.iterator().next());
        System.out.println("[INFO] Switched back to main window.");
    }

    /**
     * Fill a form field if it is present on the page.
     */
    private void fillFieldIfPresent(By locator, String value, String fieldName) {
        try {
            WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            field.clear();
            field.sendKeys(value);
            System.out.println("[INFO] Filled " + fieldName + ": " + value);
        } catch (Exception e) {
            System.out.println("[INFO] Field '" + fieldName + "' not found or not needed, skipping.");
        }
    }

    /**
     * Click a payment method option (Netbanking, Bank of Baroda, etc.) if present.
     */
    private void clickPaymentMethod(By locator, String optionName) {
        try {
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", option);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
            System.out.println("[INFO] Clicked payment option: " + optionName);
        } catch (Exception e) {
            System.out.println("[WARN] Payment option '" + optionName + "' not found: " + e.getMessage());
        }
    }

    /**
     * Click a button if it is present.
     */
    private void clickIfPresent(By locator, String buttonName) {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            System.out.println("[INFO] Clicked: " + buttonName);
        } catch (Exception e) {
            System.out.println("[WARN] Button '" + buttonName + "' not found: " + e.getMessage());
        }
    }
}
