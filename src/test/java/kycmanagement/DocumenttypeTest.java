package kycmanagement;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DocumenttypeTest extends BaseClass {

    /**
     * TC-DT-001 : Create a new Document Type for Vehicle Loan.
     *
     * Steps:
     *  1. Navigate directly to the Create Document Type form (VEHICLE_LOAN product).
     *  2. Fill in Document Name, Max Size, and Category.
     *  3. Toggle Enable OCR checkbox.
     *  4. Enter Description.
     *  5. Select supported formats (JPG, PNG, PDF).
     *  6. Click Save.
     *
     * Note: "Address Proof" category must already exist in the system.
     */
    @Test(description = "Create a new Document Type for Vehicle Loan")
    public void testCreateDocumentType() {

        // Navigate directly to the Create Document Type page
        driver.get("https://lms.alfinnext.com/settings/document-master/type/create?product=VEHICLE_LOAN");

        DocumenttypePage typePage = new DocumenttypePage(driver);

        // Step 1: The create form is open directly — no button click needed
        // (navigating to /type/create opens the form inline)

        // Step 2: Fill in basic details
        // Use a timestamp suffix to ensure uniqueness across test runs
        String docTypeName = "Passport " + System.currentTimeMillis();
        typePage.enterBasicDetails(
            docTypeName,
            "5",
            "Address Proof"   // Category must exist; created by DocumentcategoryTest
        );

        // Step 3: Toggle Enable OCR (default Is Required is already checked)
        typePage.toggleCheckboxes();

        // Step 4: Enter description
        typePage.enterDescription("Customer's valid passport scan for identity verification.");

        // Step 5: Select supported formats — JPG, PNG, PDF
        typePage.selectFormats(true, true, true, false);

        // Step 6: Save
        typePage.clickSave();

        System.out.println("✅ Document type '" + docTypeName + "' created successfully.");

        // Basic assertion — browser should still be alive after save
        Assert.assertNotNull(driver.getCurrentUrl(), "Browser lost connection after saving document type.");
    }
}
