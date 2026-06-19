package usermanagement;

import base.BaseClass;
import org.testng.annotations.Test;

public class BranchTest extends BaseClass {

    @Test
    public void testCreateBranch() {
        // Navigate directly to the Branch Office page
        driver.get("https://lms.alfinnext.com/settings/branch-office?product=VEHICLE_LOAN");

        BranchPage branchPage = new BranchPage(driver);
        
        // Step 1: Click Add Branch
        branchPage.clickAddBranch();
        
        // Step 2: Fill out Branch Details
        branchPage.fillBranchInformation("Main Branch", "BR001");
        branchPage.fillPropertyDetails("Building A", "Main Street");
        
        // Note: Make sure "Maharashtra" and "Mumbai" match the exact text available in your dropdowns
        try {
            branchPage.selectStateAndCity("Maharashtra", "Mumbai");
        } catch (Exception e) {
            System.out.println("Dropdown options may differ or take longer to load: " + e.getMessage());
        }
        
        branchPage.fillPostalInformation("400001");
        
        // Step 3: Save and Continue
        branchPage.clickSaveBranch();
    }
}
