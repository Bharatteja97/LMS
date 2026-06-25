package origination;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LeadDetailsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locator for Lead ID in the details list or top header
    @FindBy(xpath = "//*[contains(text(), 'Lead ID') or contains(text(), 'Lead No')]/following-sibling::*[1] | //*[contains(text(), 'Lead No:')]")
    private WebElement leadIdElement;

    // Locator for Lead Status badge or label
    @FindBy(xpath = "//*[contains(text(), 'Lead Status')]/following-sibling::*[1] | //span[contains(text(), 'NEW') or contains(text(), 'New')]")
    private WebElement leadStatusElement;

    // Locator for Loan Type
    @FindBy(xpath = "//*[contains(text(), 'Loan Type') or contains(text(), 'Loan type')]/following-sibling::*[1] | //*[contains(text(), 'Loan type:')]")
    private WebElement loanTypeElement;

    @FindBy(xpath = "//button[contains(., 'Re-Dispatch KYC Link')]")
    private WebElement reDispatchKycLinkButton;

    @FindBy(xpath = "//button[contains(normalize-space(.), 'Dispatch') and not(contains(normalize-space(.), 'Re-Dispatch'))]")
    private WebElement confirmDispatchButton;

    // "Send to Underwriting" button visible on lead detail page after KYC_UPDATED
    @FindBy(xpath = "//button[contains(normalize-space(.), 'Send to Underwriting')]")
    private WebElement sendToUnderwritingButton;

    public LeadDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public String getLeadId() {
        wait.until(ExpectedConditions.visibilityOf(leadIdElement));
        return leadIdElement.getText().trim();
    }

    public String getLeadStatus() {
        wait.until(ExpectedConditions.visibilityOf(leadStatusElement));
        return leadStatusElement.getText().trim();
    }

    public String getLoanType() {
        wait.until(ExpectedConditions.visibilityOf(loanTypeElement));
        return loanTypeElement.getText().trim();
    }

    public void clickReDispatchKycLink() {
        wait.until(ExpectedConditions.elementToBeClickable(reDispatchKycLinkButton));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", reDispatchKycLinkButton);
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {}
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", reDispatchKycLinkButton);
        System.out.println("[INFO] Clicked Re-Dispatch KYC Link button.");
    }

    public void clickConfirmDispatch() {
        wait.until(ExpectedConditions.elementToBeClickable(confirmDispatchButton));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", confirmDispatchButton);
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {}
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmDispatchButton);
        System.out.println("[INFO] Clicked Confirm Dispatch button in the dialog.");
    }

    /**
     * Clicks the "Send to Underwriting" button on the Lead Details page.
     * This button appears after the KYC form has been submitted and lead status
     * becomes KYC_UPDATED.
     */
    public void clickSendToUnderwriting() {
        wait.until(ExpectedConditions.elementToBeClickable(sendToUnderwritingButton));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", sendToUnderwritingButton);
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {}
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", sendToUnderwritingButton);
        System.out.println("[INFO] Clicked 'Send to Underwriting' button.");
    }

    /**
     * Waits until the lead status badge on the page contains the expected text.
     * Useful for confirming the status changed to e.g. "SENT_TO_UW" after clicking
     * Send to Underwriting.
     *
     * @param expectedStatusText partial text to wait for (e.g. "SENT_TO_UW", "KYC_UPDATED")
     */
    public void waitForLeadStatus(String expectedStatusText) {
        wait.until(ExpectedConditions.textToBePresentInElement(leadStatusElement, expectedStatusText));
        System.out.println("[INFO] Lead status is now: " + leadStatusElement.getText().trim());
    }
}
