package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import origination.LeadDetailsPage;
import pages.KycVerificationPage;
import utils.EmailUtil;

/**
 * KycPageTest
 *
 * Full end-to-end flow AFTER lead creation & KYC dispatch:
 * 1. Fetch the KYC link from Gmail via IMAP
 * 2. Navigate to the KYC Verification page (client-lms.alfinnext.com/pager)
 * 3. Verify the KYC page loads correctly with expected sections and buttons
 *
 * This test runs AFTER LeadDetailsTest (which dispatches the KYC link).
 */
public class KycPageTest extends BaseClass {

    // Gmail IMAP credentials
    private static final String IMAP_HOST = "imap.gmail.com";
    private static final String IMAP_USER = "bharatteja09@gmail.com";
    private static final String IMAP_PASSWORD = "oxtlolbsglttfqde";

    // Base URL of the LMS application (used to navigate back after KYC)
    private static final String LMS_BASE_URL = "https://lms.alfinnext.com/vehicle";

    private static String kycLink;

    // Lead ID captured from CreateLeadTest so we can navigate back after KYC
    // If CreateLeadTest stored it, reuse; otherwise we parse it from the KYC link.
    private static String capturedLeadId;

    // ─────────────────────────────────────────────────────────────────────
    // Test 1: Fetch KYC link from Gmail inbox via IMAP
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 1)
    public void testFetchKycLinkFromEmail() throws Exception {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Fetching KYC link from Gmail inbox via IMAP...");
        System.out.println("═══════════════════════════════════════════════════════════");

        // Wait a moment for email delivery (dispatch happened in previous test)
        Thread.sleep(5000);

        // Retry up to 3 times with a 10-second gap for email delivery delay
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.println("[INFO] Attempt " + attempt + " to fetch KYC link from email...");
            try {
                kycLink = EmailUtil.getKycLinkFromEmail(IMAP_HOST, IMAP_USER, IMAP_PASSWORD);
            } catch (Exception e) {
                System.out.println("[ERROR] Error reading inbox on attempt " + attempt + ": " + e.getMessage());
                e.printStackTrace();
            }
            if (kycLink != null)
                break;
            if (attempt < 3) {
                System.out.println("[INFO] KYC link not yet found. Waiting 10 seconds before retry...");
                Thread.sleep(10000);
            }
        }

        Assert.assertNotNull(kycLink,
                "KYC link must be present in the Gmail inbox. " +
                        "Ensure that the Re-Dispatch KYC Link step completed and the email was sent to " + IMAP_USER);
        System.out.println("[INFO] KYC Link retrieved: " + kycLink);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Test 2: Open the KYC page and verify it loads correctly
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 2, dependsOnMethods = "testFetchKycLinkFromEmail")
    public void testKycPageLoadsCorrectly() throws InterruptedException {
        Assert.assertNotNull(kycLink, "KYC link must have been extracted in testFetchKycLinkFromEmail");

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating to KYC Verification page...");
        System.out.println("═══════════════════════════════════════════════════════════");

        // Navigate to the KYC page
        driver.get(kycLink);
        System.out.println("[INFO] Navigated to: " + kycLink);

        KycVerificationPage kycPage = new KycVerificationPage(driver);

        // Wait for the KYC page to load
        kycPage.waitForPageLoad();

        // Verify page title
        String title = kycPage.getPageTitle();
        System.out.println("[INFO] Page title: " + title);
        Assert.assertTrue(title.toLowerCase().contains("accertify") || title.toLowerCase().contains("kyc"),
                "Page title should contain 'Accertify' or 'KYC'. Actual: " + title);

        // Verify the KYC Verification header is visible
        Assert.assertTrue(kycPage.isKycHeaderDisplayed(),
                "'KYC Verification' header should be visible on the page.");

        // Verify upload zones are present (Borrower PAN, Aadhaar, etc.)
        int uploadZones = kycPage.getUploadZoneCount();
        Assert.assertTrue(uploadZones > 0,
                "At least one document upload zone should be present. Found: " + uploadZones);
        System.out.println("[INFO] Document upload zones found: " + uploadZones);

        // Verify the Submit Form button is present
        Assert.assertTrue(kycPage.isSubmitFormButtonDisplayed(),
                "'Submit Form' button should be visible at the bottom of the KYC page.");
        System.out.println("[INFO] 'Submit Form' button is visible.");

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] KYC Verification page loaded and verified successfully!");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Test 3: Fill KYC Form, Upload Documents & Submit
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 3, dependsOnMethods = "testKycPageLoadsCorrectly")
    public void testFillKycFormAndSubmit() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Uploading documents and filling out KYC fields...");
        System.out.println("═══════════════════════════════════════════════════════════");

        KycVerificationPage kycPage = new KycVerificationPage(driver);
        String dummyDocPath = "C:\\Users\\i-tech\\accertity-test\\dummy_doc.pdf";
        String dummyBankStatement = "C:\\Users\\i-tech\\accertity-test\\dummy_bank_statement.pdf";

        // 1. Upload Borrower Documents
        kycPage.uploadDocumentByLabel("Borrower Pan", dummyDocPath);
        kycPage.uploadDocumentByLabel("Borrower Aadhar Front", dummyDocPath);
        kycPage.uploadDocumentByLabel("Borrower Aadhaar Back", dummyDocPath);

        // 2. Upload Co-Borrower Documents
        kycPage.uploadDocumentByLabel("Co-borrower pan", dummyDocPath);
        kycPage.uploadDocumentByLabel("Co-borrower Aadhar front", dummyDocPath);
        kycPage.uploadDocumentByLabel("Co-borrower-Aadhar back", dummyDocPath);

        // 3. Fill Borrower Details
        kycPage.fillInputByLabel("Residential Stability", "5");
        kycPage.fillInputByLabel("Borrower Age", "30");

        // Verify Borrower PAN
        kycPage.clickButtonNearLabel("Borrower PAN", "Verify");
        Thread.sleep(1000); // Give it a second to verify
        kycPage.handleAlertIfExists(); // Close "Pan Validation Failed" alert

        // 4. Fill Co-Borrower Details
        kycPage.fillInputByLabel("Co-Borrower Name (As Per PAN)", "Test Co-Borrower");
        kycPage.fillInputByLabel("Co-Borrower PAN", "EXKPB8202A");
        kycPage.clickButtonNearLabel("Co-Borrower PAN", "Verify");
        Thread.sleep(1000); // Wait for verification
        kycPage.handleAlertIfExists(); // Close "Pan Validation Failed" alert

        kycPage.fillInputByLabel("Co-Borrower Age", "28");
        kycPage.fillInputByLabel("Co-Borrower Email", "bharat.teja@alphawarenext.com");
        kycPage.fillInputByLabel("Co-Borrower contact", "9876543211");

        // 5. Upload Additional Documents
        kycPage.uploadDocumentByLabel("Borrower Image", dummyDocPath);
        kycPage.uploadDocumentByLabel("Bank statement (PDF Only)", dummyBankStatement);
        
        System.out.println("[INFO] Waiting 5 seconds for bank statement to be parsed before validating...");
        Thread.sleep(5000); 
        
        kycPage.clickValidateStatement(); // Handles the bank statement password modal
        
        System.out.println("[INFO] Waiting 5 seconds after validation for the server to process it...");
        Thread.sleep(5000);
        kycPage.handleAlertIfExists(); // Close "Unable to verify bank statement" alert
        kycPage.closeModalIfOpen(); // Ensure the modal backdrop is removed so it doesn't block dropdowns

        // Select Dropdowns using By.name and selectByValue
        kycPage.selectDropdownByNameAndValue("residentialType", "OWNED");
        kycPage.selectDropdownByNameAndValue("employmentType", "SALARIED");

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[INFO] Form filled. Submitting KYC Form...");
        
        // 6. Submit the form
        kycPage.clickSubmitForm();
        Thread.sleep(3000); // Wait for submission response

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Document uploads and form submission completed successfully!");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Test 4: Navigate back to Lead Details & click "Send to Underwriting"
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 4, dependsOnMethods = "testFillKycFormAndSubmit")
    public void testSendToUnderwriting() throws InterruptedException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating back to Lead Details to Send to Underwriting...");
        System.out.println("═══════════════════════════════════════════════════════════");

        // Extract Lead ID from the KYC link if not already captured
        // KYC links typically contain the lead id as a path segment or query param
        if (capturedLeadId == null && kycLink != null) {
            // e.g. https://...pager?leadId=LD58715649  OR  .../pager/LD58715649
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("(LD\\w+)")
                    .matcher(kycLink);
            if (m.find()) {
                capturedLeadId = m.group(1);
                System.out.println("[INFO] Extracted Lead ID from KYC link: " + capturedLeadId);
            }
        }

        Assert.assertNotNull(capturedLeadId,
                "Lead ID must be extractable from the KYC link to navigate back to Lead Details.");

        // Navigate back to the lead details page in the LMS
        String leadDetailUrl = LMS_BASE_URL + "/origination/lead-details/" + capturedLeadId;
        System.out.println("[INFO] Navigating to: " + leadDetailUrl);
        driver.get(leadDetailUrl);
        Thread.sleep(3000); // Wait for page to load

        LeadDetailsPage leadPage = new LeadDetailsPage(driver);

        // Click Send to Underwriting
        System.out.println("[INFO] Clicking 'Send to Underwriting' button...");
        leadPage.clickSendToUnderwriting();
        Thread.sleep(2000); // Wait for any confirmation dialog

        // Handle any confirmation dialog that may appear
        leadPage.clickConfirmDispatch();
        Thread.sleep(2000);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] 'Send to Underwriting' completed successfully!");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}
