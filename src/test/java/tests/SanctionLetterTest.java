package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.SanctionLetterPage;
import utils.EmailUtil;

public class SanctionLetterTest extends BaseClass {

    // Gmail IMAP credentials
    private static final String IMAP_HOST = "imap.gmail.com";
    private static final String IMAP_USER = "bharatteja09@gmail.com";
    private static final String IMAP_PASSWORD = "oxtlolbsglttfqde";

    private static String sanctionLink;

    @Test(priority = 1)
    public void testFetchSanctionLinkFromEmail() throws Exception {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Fetching Sanction letter link from Gmail inbox via IMAP...");
        System.out.println("═══════════════════════════════════════════════════════════");

        // Wait a moment for email delivery
        Thread.sleep(5000);

        // Retry up to 3 times
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.println("[INFO] Attempt " + attempt + " to fetch Sanction link...");
            try {
                sanctionLink = EmailUtil.getSanctionLinkFromGmail(IMAP_USER, IMAP_PASSWORD);
            } catch (Exception e) {
                System.out.println("[ERROR] Error reading inbox on attempt " + attempt + ": " + e.getMessage());
            }
            if (sanctionLink != null)
                break;
            if (attempt < 3) {
                System.out.println("[INFO] Link not yet found. Waiting 10 seconds...");
                Thread.sleep(10000);
            }
        }

        Assert.assertNotNull(sanctionLink, "Sanction letter link must be retrieved from email inbox.");
        System.out.println("[INFO] Sanction Letter Link retrieved: " + sanctionLink);
    }

    @Test(priority = 2, dependsOnMethods = "testFetchSanctionLinkFromEmail")
    public void testSubmitSanctionLetter() throws InterruptedException {
        Assert.assertNotNull(sanctionLink, "Sanction link must be present.");

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating to Sanction Letter Details Page...");
        System.out.println("═══════════════════════════════════════════════════════════");

        driver.get(sanctionLink);
        System.out.println("[INFO] Navigated to: " + sanctionLink);

        SanctionLetterPage sanctionPage = new SanctionLetterPage(driver);
        sanctionPage.waitForPageLoad();

        // 1. Click View Sanction Letter
        sanctionPage.clickViewSanctionLetter();
        Thread.sleep(2000);

        // 2. Accept terms and conditions
        sanctionPage.acceptTermsAndConditions();
        Thread.sleep(1000);

        // 3. Click Submit Sanction
        sanctionPage.clickSubmitSanction();
        Thread.sleep(3000);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Sanction letter submitted and approved successfully.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}
