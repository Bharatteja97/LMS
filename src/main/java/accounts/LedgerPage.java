package accounts;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LedgerPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//button[contains(normalize-space(.),'Export')]")
    private WebElement exportButton;

    @FindBy(xpath = "//*[contains(normalize-space(.),'ACCOUNT LEDGER') and not(.//*[contains(normalize-space(.),'ACCOUNT LEDGER')])]")
    private WebElement accountLedgerHeading;

    public LedgerPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public boolean isPageLoaded() {
        try {
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(accountLedgerHeading));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public LedgerPage clickExportButton() {
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(exportButton)).click();
        return this;
    }
}
