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
 * Page Object for the Hostinger Webmail (mail.hostinger.com).
 * Handles login, finding emails by subject, and opening them.
 */
public class HostingerMailPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String WEBMAIL_URL = "https://mail.hostinger.com";

    // ─── Login page elements ──────────────────────────────────────────────
    @FindBy(xpath = "//input[@type='email' or @name='email' or @placeholder='Email']")
    private WebElement emailInput;

    @FindBy(xpath = "//input[@type='password' or @name='password']")
    private WebElement passwordInput;

    @FindBy(xpath = "//button[@type='submit' or contains(normalize-space(.), 'Log in') or contains(normalize-space(.), 'Login')]")
    private WebElement loginButton;

    // ─── Inbox elements ───────────────────────────────────────────────────
    // Email rows use the data-qa="message-row-link" attribute in Hostinger webmail
    @FindBy(xpath = "//a[@data-qa='message-row-link']")
    private List<WebElement> emailRows;

    public HostingerMailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
    }

    /**
     * Navigate to Hostinger webmail login page.
     */
    public void navigateToWebmail() {
        driver.get(WEBMAIL_URL);
        System.out.println("[INFO] Navigated to Hostinger webmail: " + WEBMAIL_URL);
    }

    /**
     * Log in to Hostinger webmail.
     */
    public void login(String email, String password) {
        wait.until(ExpectedConditions.visibilityOf(emailInput));
        emailInput.clear();
        emailInput.sendKeys(email);

        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);

        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
        System.out.println("[INFO] Submitted Hostinger webmail login for: " + email);

        // Wait for inbox to load (email rows or inbox folder link)
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@data-qa='message-row-link']")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Inbox')]"))
        ));
        System.out.println("[INFO] Hostinger inbox loaded successfully.");
    }

    /**
     * Find and open an email by subject text.
     * Hostinger webmail renders each email row as an &lt;a data-qa="message-row-link"&gt;
     * with the subject in a nested element (often a sr-only div or a visible span).
     *
     * @param subjectText The partial or full subject to search for (case-insensitive contains).
     */
    public void openEmailBySubject(String subjectText) {
        System.out.println("[INFO] Looking for email with subject containing: \"" + subjectText + "\"");

        // Wait for at least one message row
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@data-qa='message-row-link']")));
        // Small pause for all rows to render
        sleep(2000);

        // Re-fetch rows from the DOM
        List<WebElement> rows = driver.findElements(By.xpath("//a[@data-qa='message-row-link']"));
        System.out.println("[INFO] Found " + rows.size() + " email rows in inbox.");

        String subjectLower = subjectText.toLowerCase();
        for (WebElement row : rows) {
            String rowText = row.getText().toLowerCase();
            // Also check any sr-only divs inside the row
            String ariaLabel = row.getAttribute("aria-label");
            String innerHtml = row.getAttribute("innerHTML");

            if (rowText.contains(subjectLower)
                    || (ariaLabel != null && ariaLabel.toLowerCase().contains(subjectLower))
                    || (innerHtml != null && innerHtml.toLowerCase().contains(subjectLower))) {
                System.out.println("[INFO] Found matching email row. Clicking...");
                scrollAndClick(row);
                // Wait for email body to render
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//iframe")),
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Complete KYC')]")),
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Dear')]"))
                ));
                sleep(2000);
                System.out.println("[INFO] Email opened successfully.");
                return;
            }
        }
        throw new RuntimeException("Could not find email with subject: \"" + subjectText + "\"");
    }

    /**
     * Extracts the "Complete KYC" link URL from the currently opened email.
     * The link might be inside an iframe (rendered email body) or directly in the DOM.
     *
     * @return The KYC link URL, or null if not found.
     */
    public String extractKycLinkFromOpenedEmail() {
        System.out.println("[INFO] Extracting KYC link from opened email...");

        // Check if the email body is rendered inside an iframe
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        if (!iframes.isEmpty()) {
            // Try each iframe to find the KYC link
            for (WebElement iframe : iframes) {
                try {
                    driver.switchTo().frame(iframe);
                    String link = findKycLinkInCurrentContext();
                    driver.switchTo().defaultContent();
                    if (link != null) return link;
                } catch (Exception e) {
                    driver.switchTo().defaultContent();
                }
            }
        }

        // Fallback: search in the main DOM (email rendered without iframe)
        return findKycLinkInCurrentContext();
    }

    /**
     * Extracts the sanction letter link URL from the currently opened email.
     *
     * @return The sanction letter link URL, or null if not found.
     */
    public String extractSanctionLetterLinkFromOpenedEmail() {
        System.out.println("[INFO] Extracting Sanction Letter link from opened email...");

        // Check if the email body is rendered inside an iframe
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        if (!iframes.isEmpty()) {
            for (WebElement iframe : iframes) {
                try {
                    driver.switchTo().frame(iframe);
                    String link = findSanctionLinkInCurrentContext();
                    driver.switchTo().defaultContent();
                    if (link != null) return link;
                } catch (Exception e) {
                    driver.switchTo().defaultContent();
                }
            }
        }

        return findSanctionLinkInCurrentContext();
    }

    private String findSanctionLinkInCurrentContext() {
        // Look for any link containing "sanction-letter"
        List<WebElement> allLinks = driver.findElements(By.tagName("a"));
        for (WebElement a : allLinks) {
            String href = a.getAttribute("href");
            if (href != null && href.contains("sanction-letter")) {
                System.out.println("[INFO] Found Sanction Letter link: " + href);
                return href;
            }
        }
        return null;
    }

    private String findKycLinkInCurrentContext() {
        // Look for link with text "Complete KYC"
        List<WebElement> links = driver.findElements(
                By.xpath("//a[contains(normalize-space(.), 'Complete KYC')]"));
        if (!links.isEmpty()) {
            String href = links.get(0).getAttribute("href");
            System.out.println("[INFO] Found 'Complete KYC' link: " + href);
            return href;
        }

        // Fallback: any link containing "client-lms.alfinnext.com/pager" or "kyc"
        List<WebElement> allLinks = driver.findElements(By.tagName("a"));
        for (WebElement a : allLinks) {
            String href = a.getAttribute("href");
            if (href != null && (href.contains("client-lms.alfinnext.com/pager")
                    || href.contains("kyc")
                    || href.contains("alfinnext"))) {
                System.out.println("[INFO] Found KYC link via href scan: " + href);
                return href;
            }
        }

        System.out.println("[WARN] No KYC link found in current context.");
        return null;
    }

    private void scrollAndClick(WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'});", element);
        sleep(300);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
