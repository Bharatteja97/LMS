package loansettings;

import base.BaseClass;
import org.testng.annotations.Test;

public class TaxRateTest extends BaseClass {

    @Test
    public void testCreateTaxRate() {
        driver.get("https://lms.alfinnext.com/settings/tax-rate?product=VEHICLE_LOAN");

        TaxRatePage taxRatePage = new TaxRatePage(driver);
        
        // Step 1: Click create tax rate from the list page
        taxRatePage.clickCreateTaxRateList();
        
        // Step 2: Fill out rate definition in the modal
        // "CGST + SGST" automatically populates the components grid based on the UI behavior
        taxRatePage.enterRateDefinition(
            "GST 18% Standard", 
            "CGST + SGST", 
            "10/01/2026", 
            "10/01/2030", 
            "Standard 18% GST for Vehicle Loans"
        );
        
        // Step 3: Save tax rate
        taxRatePage.submitTaxRate();
    }
}
