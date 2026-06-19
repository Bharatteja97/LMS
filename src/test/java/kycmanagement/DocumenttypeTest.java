package kycmanagement;

import base.BaseClass;
import org.testng.annotations.Test;

public class DocumenttypeTest extends BaseClass {

    @Test
    public void testCreateDocumentType() {
        driver.get("https://lms.alfinnext.com/settings/document-master/type/create?product=VEHICLE_LOAN");

        DocumenttypePage typePage = new DocumenttypePage(driver);
        
        // Step 1: Click create document type
        typePage.clickCreateDocumentType();
        
        // Step 2: Basic details
        typePage.enterBasicDetails("Passport", "5", "Address Proof"); // Make sure "Address Proof" category exists
        
        // Step 3: Settings flags
        typePage.toggleCheckboxes(); // Toggles OCR
        
        // Step 4: Description
        typePage.enterDescription("Customer's valid passport scan for identity verification.");
        
        // Step 5: Select formats
        typePage.selectFormats(true, true, true, false); // Selects JPG, PNG, PDF
        
        // Step 6: Save
        typePage.clickSave();
    }
}
