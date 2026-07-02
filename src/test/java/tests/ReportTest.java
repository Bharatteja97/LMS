package tests;

import base.BaseClass;
import pages.ReportPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReportTest extends BaseClass {
    private static final Logger logger = LogManager.getLogger(ReportTest.class);
    private ReportPage reportPage;

    private static final String REPORTS_URL = "https://lms.alfinnext.com/lms/reports?product=VEHICLE_LOAN";

    @BeforeMethod(alwaysRun = true)
    public void navigateToReports() {
        logger.info("Navigating to Reports Dashboard: " + REPORTS_URL);
        driver.get(REPORTS_URL);
        reportPage = new ReportPage(driver);
        Assert.assertTrue(reportPage.isPageLoaded(), "Reports Dashboard failed to load");
        logger.info("Reports Dashboard loaded successfully");
    }

    @Test(priority = 1)
    public void testStatsCardsVisibility() {
        logger.info("Verifying Stat cards visibility and values");
        String totalReports = reportPage.getTotalReportsStat();
        String filtered = reportPage.getFilteredStat();
        String estTime = reportPage.getEstTimeStat();
        String format = reportPage.getFormatStat();

        logger.info("TOTAL REPORTS: " + totalReports);
        logger.info("FILTERED: " + filtered);
        logger.info("EST. TIME: " + estTime);
        logger.info("FORMAT: " + format);

        Assert.assertFalse(totalReports.isEmpty(), "TOTAL REPORTS value should not be empty");
        Assert.assertFalse(filtered.isEmpty(), "FILTERED value should not be empty");
        Assert.assertFalse(estTime.isEmpty(), "EST. TIME value should not be empty");
        Assert.assertFalse(format.isEmpty(), "FORMAT value should not be empty");
    }

    @Test(priority = 2)
    public void testReportCardsPresence() {
        logger.info("Verifying Download buttons are present for Report cards");
        int downloadCount = reportPage.getDownloadButtonsCount();
        logger.info("Found " + downloadCount + " Download buttons");
        Assert.assertTrue(downloadCount > 0, "There should be at least one Download button on the Reports page");
    }

    @Test(priority = 3)
    public void testGuidelinesVisibilityAfterScroll() {
        logger.info("Scrolling to bottom to check Report Generation Guidelines");
        reportPage.scrollToBottom();
        Assert.assertTrue(reportPage.isGuidelinesVisible(), "Report Generation Guidelines should be visible after scroll");
        reportPage.scrollToTop();
    }

    @Test(priority = 4)
    public void testDownloadReport() {
        logger.info("Clicking Download button for 'DPD Graph Report'");
        reportPage.clickDownloadReport("DPD Graph Report");
        logger.info("Download button clicked successfully");
    }
}
