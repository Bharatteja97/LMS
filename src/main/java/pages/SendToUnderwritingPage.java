package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the Underwriting module.
 * After clicking "Send to Underwriting" on the Lead Details page,
 * the lead moves to the Underwriting queue.
 */
public class SendToUnderwritingPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ─── Underwriting list / dashboard ───────────────────────────────────────
    @FindBy(xpath = "//*[contains(text(),'Underwriting') and (self::h1 or self::h2 or self::h3)]")
    private WebElement underwritingHeader;

    // Status badge shown on the lead detail after being sent to UW
    @FindBy(xpath = "//span[contains(normalize-space(.),'SENT_TO_UW')] | " +
                    "//span[contains(normalize-space(.),'Sent to UW')] | " +
                    "//span[contains(normalize-space(.),'UNDERWRITING')]")
    private WebElement sentToUwStatusBadge;

    // Approve button in underwriting
    @FindBy(xpath = "//button[contains(normalize-space(.),'Approve')] | " +
                    "//button[contains(normalize-space(.),'APPROVE')]")
    private WebElement approveButton;

    // Reject button in underwriting
    @FindBy(xpath = "//button[contains(normalize-space(.),'Reject')] | " +
                    "//button[contains(normalize-space(.),'REJECT')]")
    private WebElement rejectButton;

    public SendToUnderwritingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
    }

    /**
     * Wait until the lead status changes to a "Sent to Underwriting" state.
     * Call this after clicking "Send to Underwriting" on the Lead Details page.
     */
    public void waitForSentToUwStatus() {
        wait.until(ExpectedConditions.visibilityOf(sentToUwStatusBadge));
        System.out.println("[INFO] Lead status updated to: " + sentToUwStatusBadge.getText().trim());
    }

    /**
     * Returns the current lead status badge text from the underwriting page.
     */
    public String getLeadStatus() {
        try {
            return sentToUwStatusBadge.getText().trim();
        } catch (Exception e) {
            // Try broader fallback
            java.util.List<WebElement> badges = driver.findElements(
                    By.xpath("//span[contains(@class,'badge') or contains(@class,'status') or contains(@class,'tag')]"));
            return badges.isEmpty() ? "UNKNOWN" : badges.get(0).getText().trim();
        }
    }

    /**
     * Click the Approve button in underwriting.
     */
    public void clickApprove() {
        wait.until(ExpectedConditions.elementToBeClickable(approveButton));
        scrollToElement(approveButton);
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", approveButton);
        System.out.println("[INFO] Clicked 'Approve' in Underwriting.");
    }

    /**
     * Click the Reject button in underwriting.
     */
    public void clickReject() {
        wait.until(ExpectedConditions.elementToBeClickable(rejectButton));
        scrollToElement(rejectButton);
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", rejectButton);
        System.out.println("[INFO] Clicked 'Reject' in Underwriting.");
    }

    private void scrollToElement(WebElement element) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'});", element);
            Thread.sleep(300);
        } catch (Exception ignored) {}
    }
}
