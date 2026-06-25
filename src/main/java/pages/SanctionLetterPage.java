package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the Loan Sanction Details page (client-lms.alfinnext.com/sanction-letter).
 */
public class SanctionLetterPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // ─── Elements ────────────────────────────────────────────────────────
    @FindBy(xpath = "//button[normalize-space(.)='View Sanction Letter']")
    private WebElement viewSanctionLetterButton;

    @FindBy(xpath = "//input[@type='checkbox']")
    private WebElement acceptTermsCheckbox;

    @FindBy(xpath = "//button[normalize-space(.)='Submit Sanction']")
    private WebElement submitSanctionButton;

    public SanctionLetterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
    }

    /**
     * Wait for the Sanction Details page to load.
     */
    public void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOf(viewSanctionLetterButton));
        System.out.println("[INFO] Loan Sanction Details page loaded.");
    }

    /**
     * Click the "View Sanction Letter" button.
     */
    public void clickViewSanctionLetter() {
        wait.until(ExpectedConditions.elementToBeClickable(viewSanctionLetterButton));
        // Use JS click to bypass overlapping iframe issues
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", viewSanctionLetterButton);
        System.out.println("[INFO] Clicked 'View Sanction Letter'.");
    }

    /**
     * Accept the terms and conditions by checking the checkbox.
     */
    public void acceptTermsAndConditions() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='checkbox']")));
        if (!acceptTermsCheckbox.isSelected()) {
            // Use JS to click checkbox to avoid any overlapping label issues
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", acceptTermsCheckbox);
            System.out.println("[INFO] Terms and Conditions checkbox checked.");
        }
    }

    /**
     * Click "Submit Sanction".
     */
    public void clickSubmitSanction() {
        wait.until(ExpectedConditions.elementToBeClickable(submitSanctionButton));
        // Use JS click to bypass overlapping iframe issues
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", submitSanctionButton);
        System.out.println("[INFO] Clicked 'Submit Sanction'.");
    }
}
