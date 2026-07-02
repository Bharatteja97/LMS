package tests;

import base.BaseClass;
import accounts.AccountsPage;
import accounts.AccountDetailsPage;
import accounts.SettlementPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettlementTest extends BaseClass {
    private static final Logger logger = LogManager.getLogger(SettlementTest.class);
    private static final String ACCOUNTS_URL = "https://lms.alfinnext.com/lms/accounts?product=VEHICLE_LOAN";

    private String detailPageUrl;
    private AccountDetailsPage detailPage;

    @BeforeClass(alwaysRun = true)
    public void captureDetailPageUrl() {
        driver.get(ACCOUNTS_URL);
        AccountsPage accountsPage = new AccountsPage(driver);
        accountsPage.waitForTableToLoad();
        detailPage = accountsPage.clickLANByRow(0);
        detailPage.isPageLoaded();
        detailPageUrl = driver.getCurrentUrl();
        logger.info("Detail page URL captured: " + detailPageUrl);
    }

    @BeforeMethod(alwaysRun = true)
    public void navigateToDetailPage() {
        driver.get(detailPageUrl);
        detailPage = new AccountDetailsPage(driver);
        detailPage.isPageLoaded();
    }

    @Test(priority = 1)
    public void testOpenSettlementModal() {
        logger.info("Verifying Settlement modal opens from action menu");
        SettlementPage settlementPage = detailPage.clickSettlementOption();

        String title = settlementPage.getModalTitle();
        logger.info("Settlement Modal title: " + title);
        Assert.assertTrue(title.contains("Settlement"), "Modal title should contain 'Settlement'");

        settlementPage.clickCancel();
    }

    @Test(priority = 2)
    public void testCompleteSettlementFlow() {
        logger.info("Verifying complete Settlement flow");
        SettlementPage settlementPage = detailPage.clickSettlementOption();

        Assert.assertTrue(settlementPage.isModalOpened(), "Settlement modal should be opened");

        settlementPage.enterSettledAmount("50000");
        logger.info("Entered settled amount");

        settlementPage.selectFirstAuthorizedBy();
        logger.info("Selected authorized by");

        settlementPage.enterReason("Customer requested settlement");
        logger.info("Entered reason");

        settlementPage.clickConfirmSettlement();
        logger.info("Clicked Confirm Settlement");

        // Wait to see if it closes or any success message appears. For now, pause and
        // assert we stay on detail page.
        pause(2000);
        Assert.assertTrue(detailPage.isPageLoaded(), "Should remain on account details page after settlement");
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
