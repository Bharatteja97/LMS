package usermanagement;

import base.BaseClass;
import org.testng.annotations.Test;

public class UsersTest extends BaseClass {

    @Test
    public void testCreateUserFlow() {
        // Navigate directly to the Users page
        driver.get("https://lms.alfinnext.com/settings/users?product=VEHICLE_LOAN");

        UsersPage usersPage = new UsersPage(driver);

        // Step 1: Click on 'Create user' button to open the modal
        usersPage.clickCreateUser();

        // Step 2: Fill in the user details form
        usersPage.fillUserDetails(
                "johndoe123",
                "SecurePass!@123",
                "John",
                "Doe",
                "john.doe@example.com",
                "EMP001",
                "+91-9876543210");

        // Step 3: Handle Dropdowns and Checkboxes
        // Note: You may need to change "Admin" and "Main Branch" to values that actually exist in your system's dropdowns
        try {
            usersPage.selectRole("Admin");
            usersPage.selectMainBranch("Head Office"); 
            usersPage.selectParentUser("Super Admin");
        } catch (Exception e) {
            System.out.println("Could not select dropdown values, they might not exist: " + e.getMessage());
        }

        usersPage.setAllowAccessAnyBranch(true);
        usersPage.setMarkAsLoanOfficer(false);

        // Step 4: Click Save
        usersPage.clickSave();
    }
}
