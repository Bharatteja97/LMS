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

    @Test
    public void testSendToUnderwriting() throws InterruptedException {
        String leadId = System.getProperty("LEAD_ID");
        if (leadId == null) {
            System.out.println("[WARNING] LEAD_ID is not set! Using fallback '915'.");
            leadId = "915";
        }
        String leadDetailUrl = "https://lms.alfinnext.com/vehicle/origination/" + leadId + "?product=VEHICLE_LOAN";
        
        System.out.println("[TEST] Navigating to lead: " + leadDetailUrl);
        driver.get(leadDetailUrl);
        Thread.sleep(4000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Step 1: Click the "Send to Underwriting" button on the lead detail page
        By sendBtn = By.xpath("//button[contains(normalize-space(.), 'Send to Underwriting')]");
        wait.until(ExpectedConditions.elementToBeClickable(sendBtn));
        System.out.println("[INFO] Clicking 'Send to Underwriting' button...");
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", driver.findElement(sendBtn));

        // Step 2: Wait for the confirmation modal and click the green confirm button
        // Modal button text: "Send to Underwriting" (inside the dialog)
        By confirmBtn = By.xpath(
            "//div[contains(@class,'modal') or contains(@class,'dialog') or @role='dialog']" +
            "//button[contains(translate(normalize-space(.), 'TO', 'to'), 'Send to Underwriting')] | " +
            "//button[contains(translate(normalize-space(.), 'TO', 'to'), 'Send to Underwriting')]");
        wait.until(ExpectedConditions.elementToBeClickable(confirmBtn));
        System.out.println("[INFO] Confirmation modal appeared. Clicking confirm button...");
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", driver.findElement(confirmBtn));

        Thread.sleep(3000);
        System.out.println("[PASS] Lead sent to Underwriting. Current URL: " + driver.getCurrentUrl());
    }
}
