package tests;

import base.BaseClass;
import org.testng.annotations.Test;
import origination.CreateLeadPage;
import origination.OriginationPage;

public class CreateLeadTest extends BaseClass {

    /**
     * DIAGNOSTIC TEST — Run this first to discover the exact option texts
     * returned by the API for every product type.
     * Output printed to console: exact vehicle type options per product.
     * 
     * *** After running, pick the product URL that shows "New Two Wheeler" (or
     * similar) in its option list and update testCreateNewLead accordingly. ***
     */

    @Test(priority = 0)
    public void testCreateNewLead() {
        // ----------------------------------------------------------------
        // Change product param here to match the desired vehicleType.
        // e.g. use TWO_WHEELER_LOAN for "New Two Wheeler" options.
        // ----------------------------------------------------------------
        driver.get("https://lms.alfinnext.com/vehicle/origination?product=VEHICLE_LOAN");

        OriginationPage originationPage = new OriginationPage(driver);
        originationPage.clickAddLead();

        CreateLeadPage createLeadPage = new CreateLeadPage(driver);

        java.util.Random rng = new java.util.Random();
        String vowels = "aeiou";
        String consonants = "bcdfghjklmnprstvwy";
        java.util.function.Supplier<String> namePart = () -> {
            char[] c = consonants.toCharArray();
            char[] v = vowels.toCharArray();
            return "" + Character.toUpperCase(c[rng.nextInt(c.length)])
                    + v[rng.nextInt(v.length)]
                    + c[rng.nextInt(c.length)]
                    + v[rng.nextInt(v.length)]
                    + c[rng.nextInt(c.length)];
        };
        String uniqueName = namePart.get() + " " + namePart.get();
        System.setProperty("LEAD_NAME", uniqueName);
        System.out.println("[INFO] Generated unique Lead Name: " + uniqueName);

        // Generate a random PAN number (ABCDE + 4 random digits + E) to avoid duplicates
        String randomPan = "ABCDE" + String.format("%04d", new java.util.Random().nextInt(10000)) + "E";
        System.setProperty("LEAD_PAN", randomPan);
        System.out.println("[INFO] Generated unique PAN: " + randomPan);

        createLeadPage.createNewLead(
                "NEW TWO WHEELER", // vehicleType
                randomPan, // pan
                uniqueName, // name
                "8962866849", // contact
                "bharatteja09@gmail.com", // email
                "100000", // estCost
                "70000", // loanAmt
                "20000", // downPayment
                "24", // tenure
                "Maharashtra", // state
                "Mumbai", // city
                "Toyota" // vehicleBrand
        );
        System.out.println("[INFO] Lead form submitted.");

        // Wait for the Create New Lead modal to close
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                .until(org.openqa.selenium.support.ui.ExpectedConditions
                        .invisibilityOfElementLocated(
                                org.openqa.selenium.By.xpath("//h2[contains(text(),'Create New Lead')] | //h1[contains(text(),'Create New Lead')]")));
        System.out.println("[INFO] Create New Lead modal closed — lead created successfully.");

        // Navigate to list and click the first row — newest lead is always at the top
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        driver.get("https://lms.alfinnext.com/vehicle/origination?product=VEHICLE_LOAN");
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        org.openqa.selenium.WebElement firstRow = new org.openqa.selenium.support.ui.WebDriverWait(
                driver, java.time.Duration.ofSeconds(15))
                .until(org.openqa.selenium.support.ui.ExpectedConditions
                        .elementToBeClickable(org.openqa.selenium.By.xpath("//tbody/tr[1]/td[2]")));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", firstRow);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        String leadUrl = driver.getCurrentUrl();
        String leadId = leadUrl.contains("/origination/")
                ? leadUrl.split("/origination/")[1].split("\\?")[0]
                : "";
        System.setProperty("LEAD_ID", leadId);
        System.out.println("[INFO] Captured LEAD_ID: " + leadId + " from URL: " + leadUrl);
    }
}
