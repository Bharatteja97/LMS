package origination;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OriginationPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//button[contains(., 'Add lead')]")
    private WebElement addLeadButton;

    @FindBy(xpath = "//input[@placeholder='Search by the fields mentioned in the table']")
    private WebElement searchInput;

    public OriginationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public void clickAddLead() {
        wait.until(ExpectedConditions.elementToBeClickable(addLeadButton)).click();
    }

    public void searchAndOpenLead(String leadName) {
        wait.until(ExpectedConditions.visibilityOf(searchInput));
        searchInput.click();
        searchInput.clear();
        searchInput.sendKeys(leadName, org.openqa.selenium.Keys.ENTER);

        // Wait a short moment for table search results to filter
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Locate lead name cell in the table (using normalize-space(.) to support nested spans/divs)
        By leadCellLocator = By.xpath("//td[contains(normalize-space(.), '" + leadName + "')]");
        WebElement leadCell = wait.until(ExpectedConditions.elementToBeClickable(leadCellLocator));

        // Scroll to element and click
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", leadCell);
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {}

        leadCell.click();
    }
}
