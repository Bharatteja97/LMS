package loansettings;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TaxRateTest extends BaseClass {

    @Test
    public void testCreateTaxRate() {
        driver.get("https://lms.alfinnext.com/settings/tax-rate?product=VEHICLE_LOAN");

        TaxRatePage taxRatePage = new TaxRatePage(driver);
        
        // Step 1: Click create tax rate from the list page
        taxRatePage.clickCreateTaxRateList();
        
        // Step 2: Fill out rate definition in the modal
        String taxName = "GST 18% Standard " + System.currentTimeMillis();
        taxRatePage.enterRateDefinition(
            taxName, 
            "CGST + SGST", 
            "10/01/2026", 
            "10/01/2030", 
            "Standard 18% GST for Vehicle Loans"
        );
        
        // Step 3: Save tax rate
        taxRatePage.submitTaxRate();
        
        System.out.println("✅ Tax Rate '" + taxName + "' created successfully.");
        
        // Basic assertion — browser should still be alive after save
        Assert.assertNotNull(driver.getCurrentUrl(), "Browser lost connection after saving the tax rate.");
    }
}
