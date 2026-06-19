package usermanagement;

import base.BaseClass;
import org.testng.annotations.Test;

public class RolePermissionTest extends BaseClass {

    @Test
    public void testCreateNewRole() {
        // Navigate directly to the Role Management page
        driver.get("https://lms.alfinnext.com/settings/role-master?product=VEHICLE_LOAN");

        RolePermissionPage rolePage = new RolePermissionPage(driver);
        
        // Step 1: Click Create New Role
        rolePage.clickCreateNewRole();
        
        // Step 2: Fill out Role Details
        rolePage.enterRoleDetails("Manager", "Handles approvals and team tracking");
        
        // Step 3: Save and Continue
        rolePage.clickSaveRoleDetails();
    }
}
