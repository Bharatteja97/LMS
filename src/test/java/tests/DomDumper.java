package tests;

import base.BaseClass;
import accounts.AccountsPage;
import accounts.AccountDetailsPage;
import org.testng.annotations.Test;

public class DomDumper extends BaseClass {

    private static final String ACCOUNTS_URL = "https://lms.alfinnext.com/lms/accounts?product=VEHICLE_LOAN";

    @Test
    public void dumpPaymentTabDom() {
        driver.get(ACCOUNTS_URL);
        AccountsPage accountsPage = new AccountsPage(driver);
        accountsPage.waitForTableToLoad();

        AccountDetailsPage detailPage = accountsPage.clickLANByRow(0);
        detailPage.isPageLoaded();

        detailPage.clickPaymentTab();

        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        System.out.println("=== PAYMENT TAB DOM ===");
        System.out.println(driver.getPageSource());
        System.out.println("=== END DOM ===");
    }
}
