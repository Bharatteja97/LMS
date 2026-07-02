package tests;

import base.BaseClass;
import accounts.SchedulePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScheduleTest extends BaseClass {
    private static final Logger logger = LogManager.getLogger(ScheduleTest.class);
    private static final String SCHEDULE_URL = "https://lms.alfinnext.com/lms/accounts/208?tab=repayment_schedule&product=VEHICLE_LOAN";
    private SchedulePage schedulePage;

    @BeforeMethod(alwaysRun = true)
    public void navigateToScheduleTab() {
        driver.get(SCHEDULE_URL);
        schedulePage = new SchedulePage(driver);
        schedulePage.isPageLoaded();
        logger.info("Navigated to Schedule tab: " + SCHEDULE_URL);
    }

    @Test(priority = 1)
    public void testPresentEMI() {
        logger.info("Verifying Present EMI flow from Schedules tab");
        schedulePage.clickFirstRowActionMenu();
        schedulePage.clickPresentEMI();
        schedulePage.confirmPresentEMI();
        logger.info("Present EMI flow completed successfully");
    }
}
