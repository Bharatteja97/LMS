package tests;

import base.BaseClass;
import org.testng.annotations.Test;
import pages.DealerConfirmationPage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DealerConfirmationTest extends BaseClass {

    @Test
    public void testDealerConfirmationFill() throws Exception {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating to Dealer Confirmation URL...");
        System.out.println("═══════════════════════════════════════════════════════════");

        System.out.println("[INFO] Waiting for dealer email to arrive...");
        Thread.sleep(7000);
        String dealerUrl = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            dealerUrl = utils.EmailUtil.getDealerLinkFromGmail("bharatteja09@gmail.com", "oxtlolbsglttfqde");
            if (dealerUrl != null) break;
            Thread.sleep(10000);
        }
        
        if (dealerUrl == null) {
            throw new IllegalStateException("Failed to retrieve Dealer Confirmation link from email.");
        }
        System.out.println("[INFO] Fetched Dealer URL: " + dealerUrl);
        driver.get(dealerUrl);
        
        // Create a dummy PDF file for the hypothecation letter upload
        Path dummyFile = Files.createTempFile("dummy_hypothecation", ".pdf");
        Files.writeString(dummyFile, "dummy pdf content");
        String absoluteFilePath = dummyFile.toAbsolutePath().toString();

        DealerConfirmationPage dealerPage = new DealerConfirmationPage(driver);
        
        dealerPage.fillFormAndSubmit(
                "Test Dealer Name",
                "HDFC Bank",
                "12345678901234",
                "Main Branch",
                "Mumbai",
                "HDFC0001234",
                "Current",
                absoluteFilePath
        );

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[PASS] Dealer Confirmation form filled and submitted successfully.");
        System.out.println("═══════════════════════════════════════════════════════════");
    }
}
