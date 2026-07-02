package accounts;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SettlementPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//div[@role='dialog']//*[contains(text(), 'Settlement - ')] | //h2[contains(text(), 'Settlement')] | //*[contains(@class, 'modal-title') and contains(text(), 'Settlement')]")
    private WebElement modalTitle;

    @FindBy(xpath = "//input[@placeholder='Enter amount'] | //*[normalize-space(text())='Settled Amount (₹)' or contains(text(), 'Settled Amount')]/following::input[1]")
    private WebElement settledAmountInput;

    @FindBy(xpath = "//input[@placeholder='Committee / ops head'] | //*[normalize-space(text())='Authorized By' or contains(text(), 'Authorized By')]/following::input[1]")
    private WebElement authorizedByInput;

    @FindBy(xpath = "//textarea[@placeholder='Why is a settlement being accepted?'] | //*[normalize-space(text())='Reason' or contains(text(), 'Reason')]/following::textarea[1]")
    private WebElement reasonTextarea;

    @FindBy(xpath = "//button[normalize-space(text())='Confirm Settlement' or contains(normalize-space(.), 'Confirm Settlement')]")
    private WebElement confirmSettlementBtn;

    @FindBy(xpath = "//button[normalize-space(text())='Cancel' or contains(normalize-space(.), 'Cancel')]")
    private WebElement cancelBtn;

    public SettlementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public String getModalTitle() {
        return wait.until(ExpectedConditions.visibilityOf(modalTitle)).getText().trim();
    }

    public boolean isModalOpened() {
        try {
            wait.until(ExpectedConditions.visibilityOf(modalTitle));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void enterSettledAmount(String amount) {
        wait.until(ExpectedConditions.visibilityOf(settledAmountInput)).clear();
        settledAmountInput.sendKeys(amount);
    }

    public void selectFirstAuthorizedBy() {
        wait.until(ExpectedConditions.elementToBeClickable(authorizedByInput)).click();
        try {
            Thread.sleep(500); // wait for dropdown animation
            WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//*[@role='option' or contains(@class,'option') or contains(@class, 'MenuItem')])[1]")));
            firstOption.click();
        } catch (Exception e) {
            authorizedByInput.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
            try { Thread.sleep(500); } catch (Exception ex) {}
            authorizedByInput.sendKeys(org.openqa.selenium.Keys.ENTER);
        }
    }

    public void enterReason(String reason) {
        wait.until(ExpectedConditions.visibilityOf(reasonTextarea)).clear();
        reasonTextarea.sendKeys(reason);
    }

    public void clickConfirmSettlement() {
        wait.until(ExpectedConditions.elementToBeClickable(confirmSettlementBtn)).click();
    }

    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
    }
}
