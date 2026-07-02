package tests;

import base.BaseClass;
import pages.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DashboardTest extends BaseClass {

    private static final Logger logger = LogManager.getLogger(DashboardTest.class);
    private DashboardPage dashboardPage;

    private static final String DASHBOARD_URL = "https://lms.alfinnext.com/lms/dashboard?product=VEHICLE_LOAN";

    @BeforeMethod(alwaysRun = true)
    public void navigateToDashboard() {
        logger.info("Navigating to Dashboard: " + DASHBOARD_URL);
        driver.get(DASHBOARD_URL);
        dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isPageLoaded(), "Dashboard failed to load");
        logger.info("Dashboard loaded successfully");
    }

    @Test(priority = 1)
    public void testKpiCardsVisibility() {
        logger.info("Verifying KPI cards visibility and values");
        String portfolio = dashboardPage.getTotalPortfolio();
        String npa = dashboardPage.getNpaAccounts();
        String activeLoans = dashboardPage.getActiveLoans();
        String collectionEff = dashboardPage.getCollectionEfficiency();

        logger.info("Total Portfolio: " + portfolio);
        logger.info("NPA Accounts: " + npa);
        logger.info("Active Loans: " + activeLoans);
        logger.info("Collection Efficiency: " + collectionEff);

        Assert.assertFalse(portfolio.isEmpty(), "Total Portfolio value should not be empty");
        Assert.assertFalse(npa.isEmpty(), "NPA Accounts value should not be empty");
        Assert.assertFalse(activeLoans.isEmpty(), "Active Loans value should not be empty");
        Assert.assertFalse(collectionEff.isEmpty(), "Collection Efficiency value should not be empty");
    }

    @Test(priority = 2)
    public void testSectionsVisibilityAndScroll() {
        logger.info("Verifying DPD Bucket and Live Alerts visibility (Top of page)");
        Assert.assertTrue(dashboardPage.isDpdBucketVisible(), "DPD Bucket Distribution should be visible");
        Assert.assertTrue(dashboardPage.isLiveAlertsVisible(), "Live Alerts should be visible");

        logger.info("Scrolling to bottom to check Recent Loan Activities");
        dashboardPage.scrollToBottom();
        
        Assert.assertTrue(dashboardPage.isRecentActivitiesVisible(), "Recent Loan Activities section should be visible after scroll");
        
        int rows = dashboardPage.getRecentActivitiesRowCount();
        logger.info("Recent Activities table row count: " + rows);
        Assert.assertTrue(rows > 0, "Recent Activities table should have at least 1 row");
        
        logger.info("Scrolling back to top");
        dashboardPage.scrollToTop();
    }
}
