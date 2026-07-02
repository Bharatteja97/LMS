package tests;

import base.BaseClass;
import accounts.ChargesPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChargesTest extends BaseClass {
    private static final Logger logger = LogManager.getLogger(ChargesTest.class);
    private static final String CHARGES_URL = "https://lms.alfinnext.com/lms/accounts/209?tab=charges&product=VEHICLE_LOAN";
    private ChargesPage chargesPage;

    @BeforeMethod(alwaysRun = true)
    public void navigateToChargesTab() {
        driver.get(CHARGES_URL);
        chargesPage = new ChargesPage(driver);
        chargesPage.isPageLoaded();
        logger.info("Navigated to Charges tab: " + CHARGES_URL);
    }

    @Test(priority = 1)
    public void testWaiveCharge() {
        logger.info("Verifying Waive Charge flow from Charges tab");
        chargesPage.clickFirstRowActionMenu();
        chargesPage.clickWaive();
        chargesPage.enterWaiverAmount("222");
        chargesPage.enterRemarks("Testing");
        chargesPage.clickApplyWaiver();
        logger.info("Waive Charge flow completed successfully");
    }
}
