package pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DisbursementPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    @FindBy(xpath = "//button[contains(normalize-space(.), 'Send to Disbursal') and not(ancestor::div[@role='dialog'])]")
    private WebElement sendToDisbursalBtn;

    @FindBy(xpath = "//div[@role='dialog']//button[contains(normalize-space(.), 'Send to Disbursal')]")
    private WebElement modalSendToDisbursalBtn;

    public DisbursementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isSendToDisbursalPresent() {
        try {
            return sendToDisbursalBtn.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickSendToDisbursal() {
        wait.until(ExpectedConditions.visibilityOf(sendToDisbursalBtn));
        wait.until(ExpectedConditions.elementToBeClickable(sendToDisbursalBtn));
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'}); arguments[0].click();", sendToDisbursalBtn);
        System.out.println("[INFO] Clicked 'Send to Disbursal' main button.");
        sleep(2000); // Wait for modal to appear
    }

    public void confirmSendToDisbursalModal() {
        wait.until(ExpectedConditions.visibilityOf(modalSendToDisbursalBtn));
        wait.until(ExpectedConditions.elementToBeClickable(modalSendToDisbursalBtn));
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'}); arguments[0].click();", modalSendToDisbursalBtn);
        System.out.println("[INFO] Clicked 'Send to Disbursal' inside confirmation modal.");
        sleep(5000); // Wait for the backend API call to finish
    }

    @FindBy(xpath = "//button[normalize-space(.)='Disburse' and not(ancestor::div[@role='dialog'])]")
    private WebElement disburseBtn;

    @FindBy(xpath = "//input[contains(@placeholder, 'UTR/PR')]")
    private WebElement utrInput;

    @FindBy(xpath = "//input[contains(@placeholder, 'transfer type')]")
    private WebElement transferTypeInput;

    @FindBy(xpath = "//div[@role='dialog']//button[normalize-space(.)='Disburse']")
    private WebElement modalDisburseBtn;

    public void clickDisburse() {
        wait.until(ExpectedConditions.visibilityOf(disburseBtn));
        wait.until(ExpectedConditions.elementToBeClickable(disburseBtn));
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'}); arguments[0].click();", disburseBtn);
        System.out.println("[INFO] Clicked 'Disburse' main button.");
        sleep(2000); // Wait for modal to appear
    }

    public void fillDisburseModalAndConfirm(String utr, String transferType) {
        wait.until(ExpectedConditions.visibilityOf(utrInput));
        utrInput.sendKeys(utr);
        System.out.println("[INFO] Entered UTR: " + utr);

        if (transferType != null && !transferType.isEmpty()) {
            try {
                transferTypeInput.sendKeys(transferType);
                System.out.println("[INFO] Entered Transfer Type: " + transferType);
            } catch (Exception e) {
                System.out.println("[WARNING] Could not enter transfer type.");
            }
        }

        wait.until(ExpectedConditions.elementToBeClickable(modalDisburseBtn));
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'}); arguments[0].click();", modalDisburseBtn);
        System.out.println("[INFO] Clicked 'Disburse' inside confirmation modal.");
        sleep(5000); // Wait for the backend API call to finish
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
