package lenderonboarding;

import base.BaseClass;
import org.testng.annotations.Test;

public class LenderDocumentsTest extends BaseClass {

    @Test
    public void testLenderDocuments() {
        // Navigate directly to the Lender Documents page
        // (avoids unreliable sidebar toggle)
        driver.get("https://lms.alfinnext.com/settings/client-documents?product=VEHICLE_LOAN");

        // Initialize the page object after navigation
        LenderDocumentsPage documentsPage = new LenderDocumentsPage(driver);

        // Step 1: Click 'Upload Document' button — also waits for modal to open
        documentsPage.clickUploadDocument();

        // Step 2: Fill in the document details
        // Signature: fillDocumentDetails(company, category, version, name, filePath, remarks)
        //
        // company  → must match an existing option in the "Select Company" dropdown
        //            (leave null/"" to skip if none is pre-populated)
        // category → must match an existing option (e.g., "Agreement with Alphaware")
        //            (leave null/"" to skip if none is pre-populated)
        String sampleFilePath = System.getProperty("user.dir")
                + "\\src\\test\\resources\\q_logo.png";

        documentsPage.fillDocumentDetails(
            null,                        // company  — skip (select first available if needed)
            null,                        // category — skip (the screenshot shows pre-selected value)
            "1.0",                       // version
            "Test Agreement Document",   // name (required)
            sampleFilePath,             // file attachment
            "Initial draft of the agreement" // remarks
        );

        // Step 3: Submit the form
        documentsPage.submitDocument();
    }
}
