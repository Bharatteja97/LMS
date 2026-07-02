package accounts;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SchedulePage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//tbody/tr[1]/td[last()]//button | (//tbody/tr[1]//button)[last()]")
    private WebElement firstRowActionButton;

    @FindBy(xpath = "//li[contains(.,'Present EMI')] | //div[@role='menuitem' and contains(.,'Present EMI')] | //div[contains(@class,'popover')]//*[contains(text(),'Present EMI')] | //span[text()='Present EMI']")
    private WebElement presentEMIMenuItem;

    @FindBy(xpath = "//button[contains(normalize-space(.), 'Yes, Present EMI')] | //*[contains(text(), 'Yes, Present EMI')] | //button[contains(.,'Yes') and contains(.,'Present')]")
    private WebElement yesPresentEMIButton;

    public SchedulePage(WebDriver driver) {
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

    public SchedulePage clickFirstRowActionMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(firstRowActionButton)).click();
        return this;
    }

    public SchedulePage clickPresentEMI() {
        wait.until(ExpectedConditions.elementToBeClickable(presentEMIMenuItem)).click();
        return this;
    }

    public SchedulePage confirmPresentEMI() {
        wait.until(ExpectedConditions.elementToBeClickable(yesPresentEMIButton));
        try {
            yesPresentEMIButton.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", yesPresentEMIButton);
        }
        return this;
    }
}
