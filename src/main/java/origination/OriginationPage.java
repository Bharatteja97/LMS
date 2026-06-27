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

    public void searchAndOpenLead(String searchTerm) {
        wait.until(ExpectedConditions.visibilityOf(searchInput));
        searchInput.click();
        searchInput.clear();
        searchInput.sendKeys(searchTerm, org.openqa.selenium.Keys.ENTER);

        // Wait a short moment for table search results to filter
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Just click the first row in the table after filtering!
        By firstRowLocator = By.xpath("//tbody/tr[1]/td[2]"); // Clicking 2nd column just in case 1st is a checkbox
        WebElement firstRow = wait.until(ExpectedConditions.elementToBeClickable(firstRowLocator));
        
        // Scroll and JS-click to avoid ElementClickInterceptedException from overlapping elements
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", firstRow);
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {}

        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", firstRow);
    }
}
