package lenderonboarding;

import base.BaseClass;
import org.testng.annotations.Test;
import pages.DashboardPage;

public class LenderOnboardingTest extends BaseClass {

    @Test
    public void testLenderOnboardingFlow() {
        // Navigate via Dashboard
        dashboard.selectMenu("Lender Onboarding", "Lender Onboarding");
        
        // Initialize the page object after navigation
        LenderOnboardingPage onboardingPage = new LenderOnboardingPage(driver);
        
        // NOTE: Since login might be required before accessing this URL,
        // you may need to add login steps here first or handle session cookies.

        // -----------------------------------------
        // Step 1: Fill Company Information
        // -----------------------------------------
        onboardingPage.fillCompanyInformation(
                "Test Company Ltd",
                "Test Company Private Limited",
                "TestBrand",
                "TC-1001");

        onboardingPage.fillRegistrationDetails(
                "01/01/2020",
                "U72900KA2020PTC123456",
                "ABCDE1234F",
                "29ABCDE1234F1Z5");

        onboardingPage.fillContactInformation(
                "https://testcompany.com",
                "official@testcompany.com",
                "support@testcompany.com",
                "+91-9876543210");

        // onboardingPage.clickContinue(); // Navigate to next step

        // -----------------------------------------
        // Step 2: Office Address
        // -----------------------------------------
        // onboardingPage.fillOfficeAddress("123 Tech Park", "Floor 5", "560001");
        // onboardingPage.clickAddAddress();

        // -----------------------------------------
        // Step 3: Branding
        // -----------------------------------------
        // To test uploads, provide an absolute path to a local image file.
        // onboardingPage.uploadLogo("C:\\path\\to\\your\\logo.png");
        // onboardingPage.uploadFavicon("C:\\path\\to\\your\\favicon.png");
    }
}
