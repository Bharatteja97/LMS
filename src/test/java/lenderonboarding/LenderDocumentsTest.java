package lenderonboarding;

import base.BaseClass;
import org.testng.annotations.Test;

public class LenderDocumentsTest extends BaseClass {

    @Test
    public void testLenderDocuments() {
        // Navigate via Dashboard (this leads to https://lms.alfinnext.com/settings/client-documents?product=VEHICLE_LOAN)
        dashboard.selectMenu("Lender Onboarding", "Lender Documents");
        
        // Initialize the page object after navigation
        LenderDocumentsPage documentsPage = new LenderDocumentsPage(driver);
        
        // Step 1: Click on Upload Document
        documentsPage.clickUploadDocument();
        
        // Step 2: Fill in the document details
        // Note: Replace this sample file path with a valid absolute path to a real test file on your system
        String sampleFilePath = "C:\\Users\\i-tech\\eclipse-workspace\\LMS\\src\\test\\resources\\testdata\\TestData.xlsx"; 
        
        documentsPage.fillDocumentDetails(
            "Krishna Enterprises", 
            "Agreement with Alphaware", 
            "1.0", 
            "Test Agreement Document", 
            sampleFilePath, 
            "Initial draft of the agreement"
        );
        
        // Step 3: Submit the form
        documentsPage.submitDocument();
    }
}
