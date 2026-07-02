package tests;

import base.BaseClass;
import accounts.LedgerPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LedgerTest extends BaseClass {
    private static final Logger logger = LogManager.getLogger(LedgerTest.class);
    private static final String LEDGER_URL = "https://lms.alfinnext.com/lms/accounts/208?tab=ledger&product=VEHICLE_LOAN";
    private LedgerPage ledgerPage;

    @BeforeMethod(alwaysRun = true)
    public void navigateToLedgerTab() {
        driver.get(LEDGER_URL);
        ledgerPage = new LedgerPage(driver);
        ledgerPage.isPageLoaded();
        logger.info("Navigated to Ledger tab: " + LEDGER_URL);
    }

    @Test(priority = 1)
    public void testExportLedger() {
        logger.info("Verifying Export button click on Ledger tab");
        ledgerPage.clickExportButton();
        logger.info("Export button clicked successfully");
    }
}
