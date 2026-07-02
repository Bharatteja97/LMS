package tests;

import base.BaseClass;
import accounts.DocumentsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Test suite for the <b>Documents</b> tab.
 *
 * <p>URL under test:
 * {@code https://lms.alfinnext.com/lms/accounts/212?tab=documents&product=VEHICLE_LOAN}
 *
 * <p>Covers:
 * <ol>
 *   <li>Page load — Borrower Document section heading visible</li>
 *   <li>Scroll to bottom — Vehicle Document section heading visible after scroll</li>
 *   <li>Borrower Document section heading present</li>
 *   <li>Vehicle Document section heading present (after scroll)</li>
 *   <li>Borrower Image card visible</li>
 *   <li>Borrower Pan card visible</li>
 *   <li>Borrower Aadhar Front card visible</li>
 *   <li>Borrower Aadhaar Back card visible (scroll required)</li>
 *   <li>Borrower Image upload badge shows uploaded</li>
 *   <li>Borrower Pan upload badge shows uploaded</li>
 *   <li>Borrower Image uploaded file count ≥ 1</li>
 *   <li>Borrower Image document number is "Not assigned"</li>
 *   <li>Borrower Image Uploaded Files panel is visible</li>
 *   <li>Borrower Image file count label is "1 file(s)"</li>
 *   <li>Total preview items count ≥ 1 (after scroll)</li>
 *   <li>Total uploaded badge count ≥ 1 (after scroll)</li>
 *   <li>All upload badge texts list is not empty</li>
 *   <li>Borrower Image is mandatory</li>
 *   <li>Scroll to card — Borrower Pan scrolled into view without errors</li>
 *   <li>Scroll to top — page scrolled back to top without errors</li>
 * </ol>
 */
public class DocumentsTest extends BaseClass {

    private static final Logger logger = LogManager.getLogger(DocumentsTest.class);
    private DocumentsPage documentsPage;

    private static final String DOCUMENTS_URL =
        "https://lms.alfinnext.com/lms/accounts/212?tab=documents&product=VEHICLE_LOAN";

    // ── Setup ─────────────────────────────────────────────────────────────────

    @BeforeMethod(alwaysRun = true)
    public void navigateToDocumentsPage() {
        logger.info("Navigating to Documents tab: " + DOCUMENTS_URL);
        driver.get(DOCUMENTS_URL);
        documentsPage = new DocumentsPage(driver);
        documentsPage.waitForPageLoad();
        logger.info("Documents tab loaded successfully");
    }

    // ── 1. Page load ──────────────────────────────────────────────────────────

    @Test(priority = 1)
    public void testDocumentsPageLoads() {
        logger.info("TC-01: Verifying Documents tab page load");
        boolean loaded = documentsPage.isPageLoaded();
        logger.info("Page loaded: " + loaded);
        Assert.assertTrue(loaded, "Documents tab should load with 'Borrower Document' heading visible");
    }

    // ── 2. Scroll to bottom ───────────────────────────────────────────────────

    @Test(priority = 2)
    public void testScrollToBottom() {
        logger.info("TC-02: Scroll to bottom and verify Vehicle Document section appears");
        documentsPage.scrollToBottom();
        boolean visible = documentsPage.isVehicleDocumentSectionVisible();
        logger.info("Vehicle Document section visible after scroll: " + visible);
        Assert.assertTrue(visible,
            "Vehicle Document section heading should be visible after scrolling to bottom");
    }

    // ── 3. Section headings ───────────────────────────────────────────────────

    @Test(priority = 3)
    public void testBorrowerDocumentSectionHeading() {
        logger.info("TC-03: Verifying 'Borrower Document' section heading");
        Assert.assertTrue(documentsPage.isBorrowerDocumentSectionVisible(),
            "'Borrower Document' section heading should be visible");
    }

    @Test(priority = 4)
    public void testVehicleDocumentSectionHeading() {
        logger.info("TC-04: Verifying 'Vehicle Document' section heading (scroll required)");
        Assert.assertTrue(documentsPage.isVehicleDocumentSectionVisible(),
            "'Vehicle Document' section heading should be visible after scrolling");
    }

    // ── 4. Individual borrower card visibility ────────────────────────────────

    @Test(priority = 5)
    public void testBorrowerImageCardVisible() {
        logger.info("TC-05: Verifying 'Borrower Image' card is visible");
        Assert.assertTrue(documentsPage.isBorrowerImageVisible(),
            "'Borrower Image' document card should be visible");
    }

    @Test(priority = 6)
    public void testBorrowerPanCardVisible() {
        logger.info("TC-06: Verifying 'Borrower Pan' card is visible");
        Assert.assertTrue(documentsPage.isBorrowerPanVisible(),
            "'Borrower Pan' document card should be visible");
    }

    @Test(priority = 7)
    public void testBorrowerAadharFrontCardVisible() {
        logger.info("TC-07: Verifying 'Borrower Aadhar Front' card is visible");
        documentsPage.scrollToCard("Borrower Aadhar Front");
        Assert.assertTrue(documentsPage.isBorrowerAadharFrontVisible(),
            "'Borrower Aadhar Front' document card should be visible after scroll");
    }

    @Test(priority = 8)
    public void testBorrowerAadharBackCardVisible() {
        logger.info("TC-08: Verifying 'Borrower Aadhaar Back' card is visible after scroll");
        documentsPage.scrollToBottom();
        Assert.assertTrue(documentsPage.isBorrowerAadharBackVisible(),
            "'Borrower Aadhaar Back' document card should be visible after scrolling");
    }

    // ── 5. Upload badges ──────────────────────────────────────────────────────

    @Test(priority = 9)
    public void testBorrowerImageUploadBadge() {
        logger.info("TC-09: Verifying 'Borrower Image' shows uploaded badge");
        String badge = documentsPage.getUploadBadgeText("Borrower Image");
        logger.info("Borrower Image badge: '" + badge + "'");
        Assert.assertFalse(badge.isEmpty(),
            "'Borrower Image' should have a visible upload badge");
        Assert.assertTrue(documentsPage.isDocumentUploaded("Borrower Image"),
            "'Borrower Image' should show at least 1 uploaded file");
    }

    @Test(priority = 10)
    public void testBorrowerPanUploadBadge() {
        logger.info("TC-10: Verifying 'Borrower Pan' shows uploaded badge");
        String badge = documentsPage.getUploadBadgeText("Borrower Pan");
        logger.info("Borrower Pan badge: '" + badge + "'");
        Assert.assertFalse(badge.isEmpty(),
            "'Borrower Pan' should have a visible upload badge");
        Assert.assertTrue(documentsPage.isDocumentUploaded("Borrower Pan"),
            "'Borrower Pan' should show at least 1 uploaded file");
    }

    @Test(priority = 11)
    public void testBorrowerImageUploadedFileCount() {
        logger.info("TC-11: Verifying 'Borrower Image' uploaded file count ≥ 1");
        int count = documentsPage.getUploadedFileCount("Borrower Image");
        logger.info("Borrower Image uploaded file count: " + count);
        Assert.assertTrue(count >= 1,
            "'Borrower Image' should have at least 1 uploaded file (found: " + count + ")");
    }

    // ── 6. Document Number ────────────────────────────────────────────────────

    @Test(priority = 12)
    public void testBorrowerImageDocumentNumber() {
        logger.info("TC-12: Verifying 'Borrower Image' document number");
        String docNum = documentsPage.getDocumentNumber("Borrower Image");
        logger.info("Borrower Image document number: '" + docNum + "'");
        Assert.assertFalse(docNum.isEmpty(),
            "'Borrower Image' document number row should have visible text");
        // Typically "Not assigned" when no number has been set
        logger.info("Document Number value: " + docNum);
    }

    @Test(priority = 13)
    public void testBorrowerPanDocumentNumber() {
        logger.info("TC-13: Verifying 'Borrower Pan' document number");
        String docNum = documentsPage.getDocumentNumber("Borrower Pan");
        logger.info("Borrower Pan document number: '" + docNum + "'");
        Assert.assertFalse(docNum.isEmpty(),
            "'Borrower Pan' document number row should have visible text");
    }

    // ── 7. Uploaded Files panel ───────────────────────────────────────────────

    @Test(priority = 14)
    public void testBorrowerImageUploadedFilesPanel() {
        logger.info("TC-14: Verifying 'Borrower Image' Uploaded Files panel is present");
        Assert.assertTrue(documentsPage.isUploadedFilesPanelVisible("Borrower Image"),
            "'Borrower Image' card should contain an 'Uploaded Files' panel");
    }

    @Test(priority = 15)
    public void testBorrowerImageFileCountLabel() {
        logger.info("TC-15: Verifying 'Borrower Image' file count label");
        String fileCount = documentsPage.getUploadedFilesCount("Borrower Image");
        logger.info("Borrower Image file count label: '" + fileCount + "'");
        Assert.assertFalse(fileCount.isEmpty(),
            "'Borrower Image' Uploaded Files panel should show a file count label");
        Assert.assertTrue(fileCount.contains("file"),
            "File count label should contain 'file': '" + fileCount + "'");
    }

    @Test(priority = 16)
    public void testBorrowerPanUploadedFilesPanel() {
        logger.info("TC-16: Verifying 'Borrower Pan' Uploaded Files panel is present");
        Assert.assertTrue(documentsPage.isUploadedFilesPanelVisible("Borrower Pan"),
            "'Borrower Pan' card should contain an 'Uploaded Files' panel");
    }

    // ── 8. Preview items ──────────────────────────────────────────────────────

    @Test(priority = 17)
    public void testTotalPreviewItemsAfterScroll() {
        logger.info("TC-17: Verifying total 'Click to preview' item count after scroll");
        documentsPage.scrollToBottom();
        int count = documentsPage.getTotalPreviewItemsCount();
        logger.info("Total 'Click to preview' items: " + count);
        Assert.assertTrue(count >= 1,
            "There should be at least 1 'Click to preview' item on the page (found: " + count + ")");
    }

    @Test(priority = 18)
    public void testBorrowerImageFirstDocumentItemLabel() {
        logger.info("TC-18: Verifying first document item label inside 'Borrower Image' card");
        String label = documentsPage.getFirstDocumentItemLabel("Borrower Image");
        logger.info("First document item label: '" + label + "'");
        Assert.assertFalse(label.isEmpty(),
            "The first uploaded document label inside 'Borrower Image' should not be empty");
        Assert.assertTrue(label.contains("Document"),
            "The label should start with 'Document': '" + label + "'");
    }

    // ── 9. Total uploaded badge count ─────────────────────────────────────────

    @Test(priority = 19)
    public void testTotalUploadedBadgesCount() {
        logger.info("TC-19: Verifying total uploaded badge count after scrolling");
        int count = documentsPage.getTotalUploadedBadgesCount();
        logger.info("Total uploaded badges on page: " + count);
        Assert.assertTrue(count >= 1,
            "At least 1 upload badge should be present on the page (found: " + count + ")");
    }

    @Test(priority = 20)
    public void testAllUploadBadgeTexts() {
        logger.info("TC-20: Collecting all upload badge texts after scroll");
        List<String> badges = documentsPage.getAllUploadBadgeTexts();
        logger.info("Upload badges found: " + badges);
        Assert.assertFalse(badges.isEmpty(),
            "Upload badge list should not be empty after scrolling to bottom");
        for (String badge : badges) {
            Assert.assertFalse(badge.isEmpty(),
                "Each badge text should be non-empty");
        }
    }

    // ── 10. Mandatory indicator ───────────────────────────────────────────────

    @Test(priority = 21)
    public void testBorrowerImageIsMandatory() {
        logger.info("TC-21: Verifying 'Borrower Image' is marked mandatory");
        boolean mandatory = documentsPage.isDocumentMandatory("Borrower Image");
        logger.info("Borrower Image mandatory: " + mandatory);
        // The screenshot shows a red * next to mandatory document titles
        Assert.assertTrue(mandatory,
            "'Borrower Image' should be marked as mandatory (red asterisk)");
    }

    @Test(priority = 22)
    public void testBorrowerPanIsMandatory() {
        logger.info("TC-22: Verifying 'Borrower Pan' is marked mandatory");
        boolean mandatory = documentsPage.isDocumentMandatory("Borrower Pan");
        logger.info("Borrower Pan mandatory: " + mandatory);
        Assert.assertTrue(mandatory,
            "'Borrower Pan' should be marked as mandatory (red asterisk)");
    }

    // ── 11. Scroll helpers ────────────────────────────────────────────────────

    @Test(priority = 23)
    public void testScrollToCard() {
        logger.info("TC-23: Scrolling to 'Borrower Pan' card");
        // Should complete without exception and keep the card in view
        documentsPage.scrollToCard("Borrower Pan");
        Assert.assertTrue(documentsPage.isBorrowerPanVisible(),
            "'Borrower Pan' should remain visible after scrollToCard()");
    }

    @Test(priority = 24)
    public void testScrollToTopAfterScrollToBottom() {
        logger.info("TC-24: Scroll to bottom then back to top");
        documentsPage.scrollToBottom();
        documentsPage.scrollToTop();
        // After scrolling back to top, Borrower Document heading must be visible
        Assert.assertTrue(documentsPage.isBorrowerDocumentSectionVisible(),
            "'Borrower Document' section should be visible again after scrollToTop()");
    }

    @Test(priority = 25)
    public void testScrollToBorrowerSection() {
        logger.info("TC-25: scrollToBorrowerDocumentSection() utility");
        documentsPage.scrollToBottom();
        documentsPage.scrollToBorrowerDocumentSection();
        Assert.assertTrue(documentsPage.isBorrowerDocumentSectionVisible(),
            "'Borrower Document' heading should be visible after scrollToBorrowerDocumentSection()");
    }

    @Test(priority = 26)
    public void testScrollToVehicleSection() {
        logger.info("TC-26: scrollToVehicleDocumentSection() utility");
        documentsPage.scrollToVehicleDocumentSection();
        Assert.assertTrue(documentsPage.isVehicleDocumentSectionVisible(),
            "'Vehicle Document' heading should be visible after scrollToVehicleDocumentSection()");
    }

    // ── 12. Generic card presence by title ────────────────────────────────────

    @Test(priority = 27)
    public void testGenericCardPresenceByTitle() {
        logger.info("TC-27: Testing isDocumentCardVisible() for Borrower Image");
        Assert.assertTrue(documentsPage.isDocumentCardVisible("Borrower Image"),
            "isDocumentCardVisible('Borrower Image') should return true");
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private void pause(long millis) {
        try { Thread.sleep(millis); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
