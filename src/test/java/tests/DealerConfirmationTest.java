package tests;

import base.BaseClass;
import org.testng.annotations.Test;
import pages.DealerConfirmationPage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DealerConfirmationTest extends BaseClass {

    private static final String DEALER_URL = "https://client-lms.alfinnext.com/dealer?data=v2%253AX%252BWFUk0U5bOnDlHRBvcDzEBobDCg5NUITgOQqNnXu3lvEm6tCg9L0aNRcin%252FaUETFtT56KNS0yAuo%252FZU6EuPbmdhHILS1AnAICMf%252FOBLhxMCEiPM0m5Tyju9MYQ2vceJTFiGAQfe1ip6tAvgoVH6YS2KWbYg7NerE8tHyQUNH09pDwyNQfc%253D&hmac=kHtDQzLsOutYsh7CXGGVHb8p4PnFpGpIGUgwTBrHLQ4";

    @Test
    public void testDealerConfirmationFill() throws InterruptedException, IOException {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("[TEST] Navigating to Dealer Confirmation URL...");
        System.out.println("═══════════════════════════════════════════════════════════");

        driver.get(DEALER_URL);
        
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
