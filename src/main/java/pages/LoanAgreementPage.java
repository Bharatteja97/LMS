package pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoanAgreementPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    @FindBy(xpath = "//div[normalize-space(.)='Loan Agreement']")
    private WebElement tabLoanAgreement;

    @FindBy(xpath = "//button[contains(., 'Dispatch Loan Agreement')]")
    private WebElement dispatchLoanAgreementBtn;

    @FindBy(xpath = "//button[contains(., 'E-Sign')]")
    private WebElement eSignBtn;

    @FindBy(xpath = "//div[@role='dialog']//button[normalize-space(.)='Send']")
    private WebElement modalSendBtn;

    @FindBy(xpath = "//*[contains(text(), 'Fully Signed')]")
    private WebElement fullySignedLabel;

    @FindBy(xpath = "//div[contains(@class,'alert') or contains(@class,'toast')]")
    private WebElement toastMessage;

    public LoanAgreementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public void clickLoanAgreementTab() {
        wait.until(ExpectedConditions.elementToBeClickable(tabLoanAgreement));
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'}); arguments[0].click();", tabLoanAgreement);
        System.out.println("[INFO] Clicked 'Loan Agreement' tab.");
        sleep(2000);
    }

    public boolean clickDispatchLoanAgreement() {
        try {
            // Monkey-patch fetch and XHR to steal the Digio Doc ID from the dispatch response!
            js.executeScript(
                "if (!window._fetchPatched) {" +
                "  window._fetchPatched = true;" +
                "  var originalFetch = window.fetch;" +
                "  window.fetch = async function() {" +
                "    var response = await originalFetch.apply(this, arguments);" +
                "    var url = arguments[0] && typeof arguments[0] === 'string' ? arguments[0] : (arguments[0] && arguments[0].url ? arguments[0].url : '');" +
                "    if (url.includes('loan-agreement')) {" +
                "      try {" +
                "        var clone = response.clone();" +
                "        var data = await clone.json();" +
                "        var id = data.documentId || data.digioDocId || (data.document && data.document.id);" +
                "        if(!id && JSON.stringify(data).match(/(DID[a-zA-Z0-9]{15,})/)) { id = JSON.stringify(data).match(/(DID[a-zA-Z0-9]{15,})/)[1]; }" +
                "        if (id) { window._realDigioDocId = id; console.log('Intercepted Fetch Doc ID:', id); }" +
                "      } catch(e) {}" +
                "    }" +
                "    return response;" +
                "  };" +
                "  var originalOpen = XMLHttpRequest.prototype.open;" +
                "  XMLHttpRequest.prototype.open = function() {" +
                "    this.addEventListener('load', function() {" +
                "      if (this.responseURL && this.responseURL.includes('loan-agreement')) {" +
                "        try {" +
                "          var data = JSON.parse(this.responseText);" +
                "          var id = data.documentId || data.digioDocId || (data.document && data.document.id);" +
                "          if(!id && this.responseText.match(/(DID[a-zA-Z0-9]{15,})/)) { id = this.responseText.match(/(DID[a-zA-Z0-9]{15,})/)[1]; }" +
                "          if (id) { window._realDigioDocId = id; console.log('Intercepted XHR Doc ID:', id); }" +
                "        } catch(e) {}" +
                "      }" +
                "    });" +
                "    originalOpen.apply(this, arguments);" +
                "  };" +
                "}"
            );
            
            wait.until(ExpectedConditions.visibilityOf(dispatchLoanAgreementBtn));
            wait.until(ExpectedConditions.elementToBeClickable(dispatchLoanAgreementBtn));
            js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'}); arguments[0].click();", dispatchLoanAgreementBtn);
            System.out.println("[INFO] Clicked 'Dispatch Loan Agreement' button.");
            sleep(2000);
            return true;
        } catch (Exception e) {
            System.out.println("[INFO] 'Dispatch Loan Agreement' button not found or not clickable. It might already be dispatched.");
            return false;
        }
    }

    public void confirmDispatchModal() {
        wait.until(ExpectedConditions.elementToBeClickable(modalSendBtn));
        js.executeScript("arguments[0].click();", modalSendBtn);
        System.out.println("[INFO] Clicked 'Send' inside dispatch modal.");
        sleep(5000); // Wait for the backend API call to finish generating the document
    }

    public void verifyFullySigned() {
        wait.until(ExpectedConditions.elementToBeClickable(eSignBtn));
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'}); arguments[0].click();", eSignBtn);
        System.out.println("[INFO] Clicked 'E-Sign' button to open History modal.");
        
        try {
            wait.until(ExpectedConditions.visibilityOf(fullySignedLabel));
            System.out.println("[PASS] Verified 'Fully Signed' status in the UI!");
        } catch (Exception e) {
            throw new RuntimeException("Could not find 'Fully Signed' status in the E-Sign modal UI.");
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
