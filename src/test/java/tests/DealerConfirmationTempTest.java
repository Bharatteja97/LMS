package tests;

import base.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import java.time.Duration;

public class DealerConfirmationTempTest extends BaseClass {

    private static final String DEALER_URL = "https://client-lms.alfinnext.com/dealer?data=v2%253AX%252BWFUk0U5bOnDlHRBvcDzEBobDCg5NUITgOQqNnXu3lvEm6tCg9L0aNRcin%252FaUETFtT56KNS0yAuo%252FZU6EuPbmdhHILS1AnAICMf%252FOBLhxMCEiPM0m5Tyju9MYQ2vceJTFiGAQfe1ip6tAvgoVH6YS2KWbYg7NerE8tHyQUNH09pDwyNQfc%253D&hmac=kHtDQzLsOutYsh7CXGGVHb8p4PnFpGpIGUgwTBrHLQ4";

    @Test
    public void testInspectDealerForm() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating to Dealer Confirmation URL to inspect form...");
        System.out.println("═══════════════════════════════════════════════════════════");

        driver.get(DEALER_URL);
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            // Wait for some input field or a heading to appear indicating the page has loaded
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input | //form | //h1 | //h2 | //h3")));
            Thread.sleep(3000); // Give it a little more time to fully render
            
            // Output the body content to figure out what fields exist
            String bodyHtml = driver.findElement(By.tagName("body")).getAttribute("innerHTML");
            
            // Clean up html to avoid massive output (just keep inputs, labels, buttons)
            System.out.println("--- FORM ELEMENTS ---");
            Object formInfo = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('input, select, textarea, button, label')).map(el => {" +
                "  if(el.tagName === 'LABEL') return 'LABEL: ' + el.textContent.trim();" +
                "  return el.tagName + ' (type=' + el.type + ', name=' + el.name + ', id=' + el.id + ', placeholder=' + el.placeholder + ') : ' + el.textContent.trim();" +
                "}).join('\\n');"
            );
            System.out.println(formInfo);
            System.out.println("---------------------");
            
        } catch (Exception e) {
            System.out.println("Error loading dealer form: " + e.getMessage());
        }
    }
}
