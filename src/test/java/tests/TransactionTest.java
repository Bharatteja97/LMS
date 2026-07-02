package tests;

import base.BaseClass;
import accounts.TransactionPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransactionTest extends BaseClass {
    private static final Logger logger = LogManager.getLogger(TransactionTest.class);
    private TransactionPage transactionPage;

    private static final String TRANSACTION_URL = "https://lms.alfinnext.com/lms/accounts/212?tab=transactions&product=VEHICLE_LOAN";

    @BeforeMethod(alwaysRun = true)
    public void navigateToTransactionTab() {
        logger.info("Navigating to Transaction tab: " + TRANSACTION_URL);
        driver.get(TRANSACTION_URL);
        transactionPage = new TransactionPage(driver);
        Assert.assertTrue(transactionPage.isPageLoaded(), "Transaction tab failed to load");
        logger.info("Transaction tab loaded successfully");
    }

    @Test(priority = 1)
    public void testTransactionTableAndPagination() {
        logger.info("Verifying Transaction table and pagination");
        transactionPage.scrollToBottom();
        
        int rowCount = transactionPage.getTransactionRowsCount();
        logger.info("Transaction rows found: " + rowCount);
        Assert.assertTrue(rowCount > 0, "There should be at least one transaction row");

        String pagination = transactionPage.getPaginationText();
        logger.info("Pagination text: " + pagination);
        Assert.assertFalse(pagination.isEmpty(), "Pagination text should not be empty");
        Assert.assertTrue(pagination.toLowerCase().contains("showing"), "Pagination text should contain 'showing'");
    }

    @Test(priority = 2)
    public void testExportExcelButton() {
        logger.info("Clicking on Export Excel button");
        try {
            transactionPage.clickExportExcel();
            logger.info("Successfully clicked Export Excel button");
        } catch (Exception e) {
            Assert.fail("Failed to click Export Excel button: " + e.getMessage());
        }
    }
}
