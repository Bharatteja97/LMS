package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DealerLetterPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    @FindBy(xpath = "//div[normalize-space(.)='Disbursement details' or normalize-space(.)='Disbursement Details']")
    private WebElement tabDisbursementDetails;

    @FindBy(xpath = "//button[contains(., 'Dispatch mail to dealer/seller for bank details')]")
    private WebElement dispatchMailToDealerBtn;

    @FindBy(xpath = "//input[@type='email' or contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'email') or @name='email']")
    private WebElement emailInput;

    @FindBy(xpath = "//div[@role='dialog']//button[normalize-space(.)='Dispatch' or normalize-space(.)='Send' or normalize-space(.)='Submit' or normalize-space(.)='Save'] | //button[contains(@class,'submit') and not(contains(@class,'hidden'))]")
    private WebElement modalDispatchBtn;

    public DealerLetterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public void clickDisbursementDetailsTab() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(tabDisbursementDetails));
            js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'});", tabDisbursementDetails);
            js.executeScript("arguments[0].click();", tabDisbursementDetails);
            System.out.println("[INFO] Clicked Disbursement Details tab.");
            sleep(2000);
        } catch (Exception e) {
            System.out.println("[WARN] Tab not found via PageFactory, trying direct JS...");
            try {
                WebElement tab = driver.findElement(By.xpath("//div[translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='disbursement details']"));
                js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'}); arguments[0].click();", tab);
                sleep(2000);
            } catch (Exception ex) {
                System.out.println("[ERROR] Failed to click Disbursement Details tab.");
            }
        }
    }

    public void clickDispatchMailToDealer() {
        wait.until(ExpectedConditions.elementToBeClickable(dispatchMailToDealerBtn));
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'});", dispatchMailToDealerBtn);
        js.executeScript("arguments[0].click();", dispatchMailToDealerBtn);
        System.out.println("[INFO] Clicked 'Dispatch mail to dealer/seller for bank details' button.");
        sleep(2000);
    }

    public void enterEmailAndDispatch(String email) {
        try {
            wait.until(ExpectedConditions.visibilityOf(emailInput));
            emailInput.clear();
            emailInput.sendKeys(email);
            System.out.println("[INFO] Entered email: " + email);
        } catch (Exception e) {
            System.out.println("[WARN] Email input not found. If this is unexpected, verify the modal locator.");
        }
        
        try {
            wait.until(ExpectedConditions.elementToBeClickable(modalDispatchBtn));
            js.executeScript("arguments[0].click();", modalDispatchBtn);
            System.out.println("[INFO] Clicked Dispatch/Send/Submit in modal.");
            sleep(3000);
        } catch (Exception e) {
            System.out.println("[WARN] Dispatch button in modal not found.");
            try {
                // ultimate fallback
                ((JavascriptExecutor) driver).executeScript(
                    "document.querySelectorAll('div[role=dialog] button').forEach(function(b){" +
                    "  if(b.textContent.toLowerCase().includes('dispatch')){b.click();}" +
                    "});"
                );
                sleep(3000);
            } catch (Exception ex) {}
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
