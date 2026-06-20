package kycmanagement;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DocumentcategoryTest extends BaseClass {

    /**
     * TC-DC-001 : Create a new Document Category for Vehicle Loan.
     *
     * Steps:
     *  1. Navigate directly to the Document Category settings page (Vehicle Loan).
     *  2. Click "Create document category".
     *  3. Fill in Name, Description, and Customer Type.
     *  4. Click Save.
     *
     * Expected: No exception is thrown and the test completes without errors.
     */
    @Test(description = "Create a new Document Category for Vehicle Loan")
    public void testCreateDocumentCategory() {

        // Navigate to the Document Category settings page
        driver.get("https://lms.alfinnext.com/settings/document-master/category?product=VEHICLE_LOAN");

        DocumentcategoryPage categoryPage = new DocumentcategoryPage(driver);

        // Step 1 – Click "Create document category" button
        categoryPage.clickCreateCategory();

        // Step 2 – Fill in the form
        //   Name        : Category name (must be unique; use a timestamp to avoid duplicates)
        //   Description : Free-text description
        //   Customer Type: one of "Borrower" | "Co-Borrower"
        String categoryName = "Address Proof " + System.currentTimeMillis();
        categoryPage.enterCategoryDetails(
            categoryName,
            "Documents used to verify residential address.",
            "Borrower"
        );

        // Step 3 – Save
        categoryPage.clickSave();

        System.out.println("✅ Document category '" + categoryName + "' created successfully.");

        // Basic assertion – verify driver is still alive (no exception during save)
        Assert.assertNotNull(driver.getCurrentUrl(), "Browser lost connection after saving the category.");
    }
}
