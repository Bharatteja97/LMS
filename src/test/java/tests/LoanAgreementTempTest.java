package tests;

import base.BaseClass;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.Test;
import operations.OperationsPage;

public class LoanAgreementTempTest extends BaseClass {
    @Test
    public void testInvestigateLoanAgreementApi() throws InterruptedException {
        OperationsPage opPage = new OperationsPage(driver);
        opPage.navigateToDetail("465");
        Thread.sleep(5000); // give time for network calls

        JavascriptExecutor js = (JavascriptExecutor) driver;

        String urls = (String) js.executeScript(
            "return performance.getEntriesByType('resource')" +
            ".filter(r => r.name.includes('/api/') || r.initiatorType === 'fetch' || r.initiatorType === 'xmlhttprequest')" +
            ".map(r => r.name).join('\\n');"
        );
        System.out.println("NETWORK REQUESTS MADE BY FRONTEND:");
        System.out.println(urls);
    }
}
