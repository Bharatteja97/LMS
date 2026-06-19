package lenderonboarding;

import base.BaseClass;
import org.testng.annotations.Test;

public class LenderBankTest extends BaseClass {

    @Test
    public void testLenderBankDetails() {
        // Navigate via Dashboard (this leads to https://lms.alfinnext.com/settings/client-bank?product=VEHICLE_LOAN)
        dashboard.selectMenu("Lender Onboarding", "Lender Bank");
        
        // Initialize the page object after navigation
        LenderBankPage bankPage = new LenderBankPage(driver);
        
        // Step 1: Click on Add Bank Account
        bankPage.clickAddBankAccount();
        
        // Step 2: Fill in the bank account details
        bankPage.fillBankAccountDetails(
            "Krishna Pandey", 
            "ICICI Bank", 
            "New Delhi", 
            "ICIC0000001", 
            "12345678901234", 
            "Savings", 
            true
        );
        
        // Step 3: Submit the form
        bankPage.submitBankAccount();
    }
}
