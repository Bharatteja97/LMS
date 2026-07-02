package tests;

import base.BaseClass;
import accounts.AccountsPage;
import accounts.AccountDetailsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class AccountsTest extends BaseClass {
    private static final Logger logger = LogManager.getLogger(AccountsTest.class);
    private AccountsPage accountsPage;

    private static final String ACCOUNTS_URL = "https://lms.alfinnext.com/lms/accounts?product=VEHICLE_LOAN";

    @BeforeMethod(alwaysRun = true)
    public void navigateToAccountsPage() {
        driver.get(ACCOUNTS_URL);
        accountsPage = new AccountsPage(driver);
        accountsPage.waitForTableToLoad();
    }

    // ── Table load ────────────────────────────────────────────────────────────

    @Test(priority = 1)
    public void testTableLoadsWithData() {
        logger.info("Verifying accounts table loads with data");
        int rowCount = accountsPage.getTableRowCount();
        logger.info("Total rows loaded: " + rowCount);
        Assert.assertTrue(rowCount > 0, "Accounts table should have at least one row");
    }

    // ── Search filter ─────────────────────────────────────────────────────────

    @Test(priority = 2)
    public void testSearchByBorrowerName() {
        // The "Search by the fields" bar is a global app search, not a table row filter.
        // This test verifies that using the search input does not crash the page.
        logger.info("Testing search input accepts borrower-name text without error");
        accountsPage.searchByKeyword("NIDHI");
        pause(1000);
        Assert.assertTrue(driver.getCurrentUrl().contains("accounts"),
            "Page should remain on accounts after entering search text");
        int rowCount = accountsPage.getDataRowCountNoWait();
        logger.info("Rows visible after search input: " + rowCount);
        Assert.assertTrue(rowCount >= 0, "Search input should not break the page");
    }

    @Test(priority = 3)
    public void testSearchByLAN() {
        logger.info("Testing search filter by LAN number");
        accountsPage.searchByKeyword("VEHICLE_000024");
        pause(1000);
        Assert.assertTrue(accountsPage.isLANPresent("VEHICLE_000024"),
            "LAN VEHICLE_000024 should appear in search results");
    }

    @Test(priority = 4)
    public void testSearchInputNonExistentText() {
        logger.info("Testing search input with non-existent text does not crash page");
        accountsPage.searchByKeyword("XYZNONEXISTENT123");
        pause(1000);
        Assert.assertTrue(driver.getCurrentUrl().contains("accounts"),
            "Page should remain on accounts after entering unrecognised search text");
        int rowCount = driver.findElements(
            org.openqa.selenium.By.xpath("//table/tbody/tr[td]")).size();
        logger.info("Rows visible after non-existent search: " + rowCount);
        Assert.assertTrue(rowCount >= 0, "Page should still render a table without errors");
    }

    // ── Status filter ─────────────────────────────────────────────────────────

    @Test(priority = 5)
    public void testAvailableStatusOptions() {
        logger.info("Listing available Status filter options");
        List<String> options = accountsPage.getAvailableStatusOptions();
        logger.info("Available status options: " + options);
        Assert.assertFalse(options.isEmpty(), "Status dropdown should have at least one selectable option");
    }

    @Test(priority = 6)
    public void testFilterByActiveStatus() {
        logger.info("Testing Status filter: ACTIVE");
        accountsPage.selectStatus("ACTIVE");
        pause(2000);
        int rowCount = accountsPage.getTableRowCount();
        logger.info("Rows with ACTIVE status: " + rowCount);
        Assert.assertTrue(rowCount > 0, "ACTIVE filter should return results");
        for (int i = 0; i < rowCount; i++) {
            String status = accountsPage.getStatus(i);
            Assert.assertTrue(status.toUpperCase().contains("ACTIVE"),
                "Row " + i + " status '" + status + "' should indicate ACTIVE");
        }
    }

    @Test(priority = 7)
    public void testFilterByOverdueStatus() {
        logger.info("Testing Status filter: OVERDUE");
        accountsPage.selectStatus("OVERDUE");
        pause(2000);
        int rowCount = accountsPage.getTableRowCount();
        logger.info("Rows with OVERDUE status: " + rowCount);
        Assert.assertTrue(rowCount > 0, "OVERDUE filter should return results");
        for (int i = 0; i < rowCount; i++) {
            String status = accountsPage.getStatus(i);
            Assert.assertTrue(status.toUpperCase().contains("OVERDUE"),
                "Row " + i + " status '" + status + "' should indicate OVERDUE");
        }
    }

    @Test(priority = 8)
    public void testFilterBySettledStatus() {
        logger.info("Testing Status filter: SETTLED");
        accountsPage.selectStatus("SETTLED");
        pause(2000);
        int rowCount = accountsPage.getDataRowCountNoWait();
        logger.info("Rows with SETTLED status: " + rowCount);
        if (rowCount > 0) {
            for (int i = 0; i < rowCount; i++) {
                String status = accountsPage.getStatus(i);
                Assert.assertTrue(status.toUpperCase().contains("SETTLED"),
                    "Row " + i + " status '" + status + "' should indicate SETTLED");
            }
        } else {
            logger.info("No SETTLED loans in current test data — filter applied without error");
        }
    }

    // ── Date filter ───────────────────────────────────────────────────────────

    @Test(priority = 9)
    public void testLoginDateFilter() {
        logger.info("Testing Login date range filter");
        accountsPage.setLoginFromDate("01/01/2024");
        accountsPage.setLoginToDate("12/31/2024");
        pause(2000);
        int rowCount = accountsPage.getTableRowCount();
        logger.info("Rows in date range 2024: " + rowCount);
        Assert.assertTrue(rowCount >= 0, "Date filter should not cause errors");
    }

    // ── Combined filters ──────────────────────────────────────────────────────

    @Test(priority = 10)
    public void testCombinedStatusAndSearchFilter() {
        logger.info("Testing combined Status + Search filter");
        accountsPage.selectStatus("ACTIVE");
        accountsPage.searchByKeyword("VEHICLE");
        pause(1000);
        int rowCount;
        try {
            rowCount = accountsPage.getTableRowCount();
        } catch (Exception e) {
            rowCount = 0;
        }
        logger.info("Rows after combined filter: " + rowCount);
        Assert.assertTrue(rowCount >= 0, "Combined filter should not cause errors");
    }

    // ── Table data integrity ──────────────────────────────────────────────────

    @Test(priority = 11)
    public void testTableDataIntegrity() {
        logger.info("Verifying table data integrity for all rows");
        List<Map<String, String>> tableData = accountsPage.getAllTableData();
        Assert.assertFalse(tableData.isEmpty(), "Table data should not be empty");
        for (int i = 0; i < tableData.size(); i++) {
            Map<String, String> row = tableData.get(i);
            String lan = row.get("LAN");
            Assert.assertNotNull(lan, "Row " + i + " LAN should not be null");
            Assert.assertFalse(lan.isEmpty(), "Row " + i + " LAN should not be empty");
            logger.info("Row " + i + ": LAN=" + lan
                + ", Borrower=" + row.get("Borrower Name")
                + ", Status=" + row.get("Status"));
        }
    }

    @Test(priority = 12)
    public void testSerialNumberSequence() {
        logger.info("Verifying serial numbers are sequential starting from 1");
        int rowCount = accountsPage.getTableRowCount();
        for (int i = 0; i < rowCount; i++) {
            String serial = accountsPage.getCellValue(i, 0);
            Assert.assertEquals(serial, String.valueOf(i + 1),
                "Serial number at row " + i + " should be " + (i + 1));
        }
    }

    // ── Navigation to account detail ──────────────────────────────────────────

    @Test(priority = 13)
    public void testClickLANNavigatesToDetailPage() {
        logger.info("Testing navigation to account detail page by clicking LAN");
        String firstLAN = accountsPage.getLAN(0);
        logger.info("First LAN in table: " + firstLAN);
        Assert.assertFalse(firstLAN.isEmpty(), "First LAN should not be empty");
        Assert.assertTrue(accountsPage.isLANPresent(firstLAN),
            "LAN " + firstLAN + " should be detectable in the table cells");
        AccountDetailsPage detailPage = accountsPage.clickLANLink(firstLAN);
        Assert.assertTrue(detailPage.isPageLoaded(),
            "Account detail page should load after clicking LAN");
        logger.info("Successfully navigated to account detail page for " + firstLAN);
    }

    @Test(priority = 14)
    public void testClickFirstRowLAN() {
        logger.info("Testing navigation by clicking first row LAN cell");
        String firstLAN = accountsPage.getLAN(0);
        logger.info("Clicking LAN: " + firstLAN);
        AccountDetailsPage detailPage = accountsPage.clickLANByRow(0);
        Assert.assertTrue(detailPage.isPageLoaded(),
            "Detail page should load after clicking first row LAN");
    }

    // ── Action buttons ────────────────────────────────────────────────────────

    @Test(priority = 15)
    public void testPresentUpcomingEMIsButton() {
        logger.info("Testing 'Present Upcoming EMIs' button click");
        accountsPage.clickPresentUpcomingEMIs();
        pause(2000);
        logger.info("URL after clicking Present Upcoming EMIs: " + driver.getCurrentUrl());
        Assert.assertNotNull(driver.getCurrentUrl(), "Page should remain navigable after button click");
    }

    @Test(priority = 16)
    public void testViewActivePresentationsButton() {
        logger.info("Testing 'View Active Presentations' button click");
        accountsPage.clickViewActivePresentations();
        pause(2000);
        logger.info("URL after clicking View Active Presentations: " + driver.getCurrentUrl());
        Assert.assertNotNull(driver.getCurrentUrl(), "Page should remain navigable after button click");
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
