package accounts;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ChargesPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//tbody/tr[1]/td[last()]//button | (//tbody/tr[1]//button)[last()]")
    private WebElement firstRowActionButton;

    @FindBy(xpath = "//li[contains(.,'Waive')] | //div[@role='menuitem' and contains(.,'Waive')] | //div[contains(@class,'popover')]//*[contains(text(),'Waive')] | //span[contains(text(),'Waive')]")
    private WebElement waiveMenuItem;

    @FindBy(xpath = "//*[contains(text(),'Waiver Amount')]/following::input[1] | //div[@role='dialog']//input[1]")
    private WebElement waiverAmountInput;

    @FindBy(xpath = "//*[contains(text(),'Remarks')]/following::textarea[1] | //textarea")
    private WebElement remarksInput;

    @FindBy(xpath = "//button[contains(normalize-space(.), 'Apply Waiver')] | //*[contains(text(), 'Apply Waiver')]")
    private WebElement applyWaiverButton;

    public ChargesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOf(firstRowActionButton));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ChargesPage clickFirstRowActionMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(firstRowActionButton)).click();
        return this;
    }

    public ChargesPage clickWaive() {
        wait.until(ExpectedConditions.elementToBeClickable(waiveMenuItem));
        try {
            waiveMenuItem.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", waiveMenuItem);
        }
        return this;
    }

    public ChargesPage enterWaiverAmount(String amount) {
        wait.until(ExpectedConditions.visibilityOf(waiverAmountInput));
        waiverAmountInput.clear();
        waiverAmountInput.sendKeys(amount);
        return this;
    }

    public ChargesPage enterRemarks(String remarks) {
        wait.until(ExpectedConditions.visibilityOf(remarksInput));
        remarksInput.clear();
        remarksInput.sendKeys(remarks);
        return this;
    }

    public ChargesPage clickApplyWaiver() {
        wait.until(ExpectedConditions.elementToBeClickable(applyWaiverButton));
        try {
            applyWaiverButton.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyWaiverButton);
        }
        return this;
    }
}
