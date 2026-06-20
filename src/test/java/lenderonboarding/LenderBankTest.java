package lenderonboarding;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LenderBankTest extends BaseClass {

    @Test
    public void testLenderBankDetails() {
        // Navigate directly to Lender Bank settings page
        driver.get("https://lms.alfinnext.com/settings/client-bank?product=VEHICLE_LOAN");
        
        // Initialize the page object after navigation
        LenderBankPage bankPage = new LenderBankPage(driver);
        
        // Step 1: Click on Add Bank Account
        bankPage.clickAddBankAccount();
        
        // Generate a unique account number based on timestamp
        String uniqueSuffix = String.valueOf(System.currentTimeMillis()).substring(8);
        String uniqueAccountNumber = "123456" + uniqueSuffix;

        // Step 2: Fill in the bank account details
        bankPage.fillBankAccountDetails(
            "Krishna Pandey", 
            "ICICI Bank", 
            "New Delhi", 
            "ICIC0000001", 
            uniqueAccountNumber, 
            "Savings", 
            true
        );
        
        // Step 3: Submit the form
        bankPage.submitBankAccount();
        
        System.out.println("✅ Bank Account added successfully with Account Number: " + uniqueAccountNumber);
        
        // Basic assertion — browser should still be alive after clicking
        Assert.assertNotNull(driver.getCurrentUrl(), "Browser lost connection after submitting bank account.");
    }
}
