package tests;

import base.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Navigates to the lead details page, clicks "Send to Underwriting",
 * then confirms the modal dialog by clicking the green "Send To Underwriting" button.
 */
public class SendToUnderwritingTest extends BaseClass {

    private static final String LEAD_NUMERIC_ID = "907";
    private static final String LEAD_DETAIL_URL =
            "https://lms.alfinnext.com/vehicle/origination/" + LEAD_NUMERIC_ID + "?product=VEHICLE_LOAN";

    @Test
    public void testSendToUnderwriting() throws InterruptedException {
        System.out.println("[TEST] Navigating to lead: " + LEAD_DETAIL_URL);
        driver.get(LEAD_DETAIL_URL);
        Thread.sleep(4000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Step 1: Click the "Send to Underwriting" button on the lead detail page
        By sendBtn = By.xpath("//button[contains(normalize-space(.), 'Send to Underwriting')]");
        wait.until(ExpectedConditions.elementToBeClickable(sendBtn));
        System.out.println("[INFO] Clicking 'Send to Underwriting' button...");
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", driver.findElement(sendBtn));

        // Step 2: Wait for the confirmation modal and click the green confirm button
        // Modal button text: "Send To Underwriting" (inside the dialog)
        By confirmBtn = By.xpath(
            "//div[contains(@class,'modal') or contains(@class,'dialog') or @role='dialog']" +
            "//button[contains(normalize-space(.), 'Send To Underwriting')] | " +
            "//button[contains(normalize-space(.), 'Send To Underwriting')]");
        wait.until(ExpectedConditions.elementToBeClickable(confirmBtn));
        System.out.println("[INFO] Confirmation modal appeared. Clicking confirm button...");
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", driver.findElement(confirmBtn));

        Thread.sleep(3000);
        System.out.println("[PASS] Lead sent to Underwriting. Current URL: " + driver.getCurrentUrl());
    }
}
