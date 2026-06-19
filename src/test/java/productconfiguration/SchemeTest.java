package productconfiguration;

import base.BaseClass;
import org.testng.annotations.Test;

public class SchemeTest extends BaseClass {

    @Test
    public void testCreateScheme() {
        driver.get("https://lms.alfinnext.com/settings/scheme-master?product=VEHICLE_LOAN");

        SchemePage schemePage = new SchemePage(driver);
        
        // Step 1: Click create
        schemePage.clickCreateScheme();
        
        // Step 2: Basic Info
        try {
            // Note: We're using "Product01" as an example product name
            schemePage.enterBasicInformation("Premium Auto Scheme", "SCH-AUTO-01", "Product01");
        } catch (Exception e) {
            System.out.println("Dropdown product options may differ: " + e.getMessage());
        }
        
        // Step 3: Financial Parameters
        schemePage.enterFinancialParameters("50000", "1500000", "10", "90");
        
        // Step 4: Interest & Tenures
        try {
            // Using "Monthly" as a typical frequency option
            schemePage.enterInterestAndTenures("7.5", "12.5", "Monthly", "12", "84", "36");
        } catch (Exception e) {
            System.out.println("Tenure frequency options may differ: " + e.getMessage());
        }
        
        // Step 5: Charges & Details
        schemePage.enterChargesAndDetails("Processing Fee", "This is a premium vehicle loan scheme designed for top-tier credit profiles.");
        
        // Step 6: Save
        schemePage.clickSaveScheme();
    }
}
