package kycmanagement;

import base.BaseClass;
import org.testng.annotations.Test;

public class DocumentcategoryTest extends BaseClass {

    @Test
    public void testCreateDocumentCategory() {
        driver.get("https://lms.alfinnext.com/settings/document-master/category?product=VEHICLE_LOAN");

        DocumentcategoryPage categoryPage = new DocumentcategoryPage(driver);
        
        // Step 1: Click create category
        categoryPage.clickCreateCategory();
        
        // Step 2: Enter Details
        categoryPage.enterCategoryDetails(
            "Address Proof", 
            "Documents used to verify residential address.",
            "Individual" // Example Customer Type
        );
        
        // Step 3: Save
        categoryPage.clickSave();
    }
}
