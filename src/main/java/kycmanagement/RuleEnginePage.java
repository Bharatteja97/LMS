package kycmanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class RuleEnginePage {

    WebDriver driver;
    WebDriverWait wait;

    // ─── Listing Page Locators ────────────────────────────────────────────────
    // "Create Scorecard" button — has SVG icon, use contains(.)
    private By createScorecardBtn = By.xpath("//button[contains(., 'Create Scorecard')]");

    // ─── Modal Form Locators ──────────────────────────────────────────────────

    // Scheme — native <select> (confirmed from DOM)
    private By schemeSelect = By.xpath(
        "//label[contains(., 'Scheme')]/following-sibling::select | " +
        "//select[option[contains(text(),'Select a scheme')]]"
    );

    // Scorecard Name — input with placeholder "Enter scorecard name"
    // Pre-filled with "Default scorecard" — clear and type new value
    private By scorecardNameInput = By.xpath("//input[@placeholder='Enter scorecard name']");

    // Description — textarea with placeholder "Enter scorecard description"
    private By descriptionInput = By.xpath("//textarea[@placeholder='Enter scorecard description']");

    // Save Scorecard button — has SVG child, use contains(.)
    private By saveScorecardBtn = By.xpath(
        "//button[contains(., 'Save Scorecard')]"
    );

    // Add Threshold button
    private By addThresholdBtn = By.xpath("//button[contains(., 'Add Threshold')]");

    // Add New Rule button (left panel)
    private By addNewRuleBtn = By.xpath("//button[contains(., 'Add New Rule')]");

    // Close modal button (X icon, aria-label)
    private By closeModalBtn = By.xpath("//button[@aria-label='Close modal']");

    // ─── Constructor ──────────────────────────────────────────────────────────
    public RuleEnginePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ─── Actions ──────────────────────────────────────────────────────────────

    /** Clicks the "Create Scorecard" button on the listing page to open the modal. */
    public void clickCreateScorecard() {
        wait.until(ExpectedConditions.elementToBeClickable(createScorecardBtn)).click();
        System.out.println("Clicked 'Create Scorecard' button.");
    }

    /**
     * Fills in the Scorecard Information section of the modal.
     *
     * @param schemeName    Visible text of the scheme option in the dropdown
     * @param scorecardName Name to set for the scorecard (replaces pre-filled "Default scorecard")
     */
    public void enterScorecardInformation(String schemeName, String scorecardName) {
        // Select Scheme from native <select>
        selectScheme(schemeName);

        // Scorecard Name — clear the pre-filled "Default scorecard" and enter new value
        WebElement nameEl = wait.until(ExpectedConditions.visibilityOfElementLocated(scorecardNameInput));
        nameEl.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        nameEl.sendKeys(Keys.BACK_SPACE);
        nameEl.sendKeys(scorecardName);
        System.out.println("Entered scorecard name: " + scorecardName);
    }

    /** Selects a scheme from the native <select> dropdown by visible text. */
    public void selectScheme(String schemeName) {
        try {
            WebElement selectEl = wait.until(ExpectedConditions.elementToBeClickable(schemeSelect));
            Select sel = new Select(selectEl);
            sel.selectByVisibleText(schemeName);
            System.out.println("Selected scheme: " + schemeName);
        } catch (Exception e) {
            System.out.println("Could not select scheme '" + schemeName + "': " + e.getMessage());
            // Fallback: select first available option (index 1 skips the placeholder)
            try {
                WebElement selectEl = driver.findElement(
                    By.xpath("//select[option[contains(text(),'Select a scheme')]]")
                );
                Select sel = new Select(selectEl);
                if (sel.getOptions().size() > 1) {
                    sel.selectByIndex(1);
                    System.out.println("Fallback: selected first available scheme option.");
                }
            } catch (Exception ex) {
                System.out.println("Fallback scheme selection also failed: " + ex.getMessage());
            }
        }
    }

    /** Enters a description into the Description textarea. */
    public void enterDescription(String description) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput)).sendKeys(description);
            System.out.println("Entered description.");
        } catch (Exception e) {
            System.out.println("Could not enter description: " + e.getMessage());
        }
    }

    /** Clicks the "+ Add Threshold" button. */
    public void clickAddThreshold() {
        wait.until(ExpectedConditions.elementToBeClickable(addThresholdBtn)).click();
        System.out.println("Clicked 'Add Threshold'.");
    }

    /** Clicks the "+ Add New Rule" button in the left panel. */
    public void clickAddNewRule() {
        wait.until(ExpectedConditions.elementToBeClickable(addNewRuleBtn)).click();
        System.out.println("Clicked 'Add New Rule'.");
    }

    /** Clicks the "Save Scorecard" button to submit the form. */
    public void clickSaveScorecard() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveScorecardBtn));
        btn.click();
        System.out.println("Clicked 'Save Scorecard'.");
    }

    /** Closes the modal via the X button. */
    public void closeModal() {
        wait.until(ExpectedConditions.elementToBeClickable(closeModalBtn)).click();
    }
}
