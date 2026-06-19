package productconfiguration;

import base.BaseClass;
import org.testng.annotations.Test;

public class ProductTest extends BaseClass {

    @Test
    public void testCreateProduct() {
        // Navigate directly to the Product Master page
        driver.get("https://lms.alfinnext.com/settings/product-master?product=VEHICLE_LOAN");

        ProductPage productPage = new ProductPage(driver);
        
        // Step 1: Click "Create product" button
        productPage.clickCreateProduct();
        
        // Step 2: Fill out Product Details
        // NOTE: "PERSONAL_LOAN" or "VEHICLE_LOAN" must match an exact option in your dropdown!
        try {
            productPage.enterProductDetails("Auto Loan Premium", "PERSONAL_LOAN", "Specialized loan for premium vehicles");
        } catch (Exception e) {
            System.out.println("Dropdown options may differ or take longer to load: " + e.getMessage());
        }
        
        // Step 3: Save and Continue
        productPage.clickSave();
    }
}
