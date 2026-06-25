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

        String uniqueName = "Test User " + System.currentTimeMillis();
        System.setProperty("LEAD_NAME", uniqueName);
        System.out.println("[INFO] Generated unique Lead Name: " + uniqueName);

        // Generate a random PAN number (ABCDE + 4 random digits + E) to avoid duplicates
        String randomPan = "ABCDE" + String.format("%04d", new java.util.Random().nextInt(10000)) + "E";
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
        System.out.println("Lead creation script executed successfully.");
    }
}
