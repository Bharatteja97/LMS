package lenderonboarding;

import base.BaseClass;
import org.testng.annotations.Test;

public class LenderListTest extends BaseClass {

    @Test
    public void testLenderList() {
        // Navigate via Dashboard
        dashboard.selectMenu("Lender Onboarding", "Lender List");

        // Initialize the page object after navigation
        LenderListPage listPage = new LenderListPage(driver);

        // TODO: Add test steps here
    }
}
