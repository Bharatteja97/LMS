package productconfiguration;

import base.BaseClass;
import org.testng.annotations.Test;

public class ChargeTest extends BaseClass {

    @Test
    public void testCreateCharge() {
        driver.get("https://lms.alfinnext.com/settings/charge?product=VEHICLE_LOAN");

        ChargePage chargePage = new ChargePage(driver);
        
        // Step 1: Click create charge
        chargePage.clickCreateCharge();
        
        // Step 2: Basic Configuration
        chargePage.enterBasicConfiguration(
            "Processing Fee", 
            "Processing Fee", // Provide valid dropdown option here
            "Flat Amount", 
            "2500"
        );
        
        // Step 3: Deduction & Stage
        chargePage.enterDeductionAndStageConfig(
            "Pre Disburse",    // Deduction Type
            "Loan Activation", // Application Stage
            "Total Outstanding", // Amount Basis
            "This is a standard processing fee for vehicle loans."
        );
        
        // Step 4: Settings Flags
        // Assuming "Is Active Charge" and "Global Charge" are already checked by default
        chargePage.checkTaxApplicable();
        
        // Step 5: Save
        chargePage.clickSaveCharge();
    }
}
