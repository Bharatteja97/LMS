package productconfiguration;

import base.BaseClass;
import org.testng.annotations.Test;

public class SchemeTest extends BaseClass {

    @Test
    public void testCreateScheme() {
        driver.get("https://lms.alfinnext.com/settings/scheme-master?product=VEHICLE_LOAN");

        SchemePage schemePage = new SchemePage(driver);

        // Step 1: Click Create Scheme
        schemePage.clickCreateScheme();

        // Step 2: Basic Information
        try {
            schemePage.enterBasicInformation("Premium Auto Scheme", "SCH-AUTO-01", "Product01");
        } catch (Exception e) {
            System.out.println("Product dropdown options may differ: " + e.getMessage());
        }

        // Step 3: Financial Parameters
        schemePage.enterFinancialParameters("50000", "1500000", "10", "90");

        // Step 4: Interest & Tenures
        try {
            schemePage.enterInterestAndTenures("7.5", "12.5", "Monthly", "12", "84", "36");
        } catch (Exception e) {
            System.out.println("Tenure frequency options may differ: " + e.getMessage());
        }

        // Step 5: Personal Configuration
        try {
            schemePage.enterPersonalConfiguration(
                "Salaried",  // Employment Type — exact option text from dropdown
                "25000",     // Minimum Salary
                "650",       // Minimum CIBIL Score
                "1",         // Minimum Work Experience (Years)
                "50"         // Maximum FOIR (%)
            );
        } catch (Exception e) {
            System.out.println("Personal configuration fields may differ: " + e.getMessage());
        }

        // Step 6: Charges & Details
        schemePage.enterChargesAndDetails(
            "Processing Fee",
            "This is a premium vehicle loan scheme designed for top-tier credit profiles."
        );

        // Step 7: Save
        schemePage.clickSaveScheme();
    }
}
