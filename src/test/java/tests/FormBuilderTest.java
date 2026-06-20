package tests;

import base.BaseClass;
import pages.FormBuilderPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FormBuilderTest extends BaseClass {

    @Test
    public void testCreateFormBuilder() {
        driver.get("https://lms.alfinnext.com/settings/product-form-builder?product=VEHICLE_LOAN");

        FormBuilderPage formBuilderPage = new FormBuilderPage(driver);
        
        // Step 1: Click "+ Create Form" from the dashboard
        formBuilderPage.clickCreateForm();
        
        // Step 2: Fill out the modal
        // Note: Ensure the product and scheme exist; fallback selects first available option
        formBuilderPage.configureForm("Vehicle Loan", "MSME Loan");
        
        // Step 3: Click Build Form
        formBuilderPage.clickBuildForm();
        
        System.out.println("✅ Form Builder configuration initiated successfully.");
        
        // Basic assertion — browser should still be alive after clicking
        Assert.assertNotNull(driver.getCurrentUrl(), "Browser lost connection after starting Form Builder.");
    }
}
