package tests;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DashboardPage;

public class DashboardTest extends BaseClass {

    @Test
    public void testDashboardStatsAndSearch() throws Exception {
        System.out.println("-----------------------------------------------------------");
        System.out.println("[TEST] Navigating to Dashboard and checking elements...");
        System.out.println("-----------------------------------------------------------");

        DashboardPage dashboardPage = new DashboardPage(driver);
        
        System.out.println("[INFO] Getting Total Applications...");
        String totalApps = dashboardPage.getTotalApplicationsCount();
        System.out.println("[RESULT] Total Applications: " + totalApps);
        Assert.assertNotNull(totalApps, "Total Applications should not be null");
        Assert.assertFalse(totalApps.isEmpty(), "Total Applications should not be empty");

        System.out.println("[INFO] Getting New Leads...");
        String newLeads = dashboardPage.getNewLeadCount();
        System.out.println("[RESULT] New Leads: " + newLeads);
        Assert.assertNotNull(newLeads, "New Leads should not be null");

        System.out.println("[INFO] Getting Approval Count...");
        String approvalCount = dashboardPage.getApprovalCount();
        System.out.println("[RESULT] Approvals: " + approvalCount);
        Assert.assertNotNull(approvalCount, "Approval count should not be null");

        System.out.println("[INFO] Getting Disbursement Count...");
        String disbursementCount = dashboardPage.getDisbursementCount();
        System.out.println("[RESULT] Disbursements: " + disbursementCount);
        Assert.assertNotNull(disbursementCount, "Disbursement count should not be null");

        System.out.println("[INFO] Clicking Filters Sidebar...");
        dashboardPage.clickFilters();
        
        System.out.println("[INFO] Interacting with Filter Dashboard...");
        dashboardPage.setFilterStartDate("01/01/2026");
        dashboardPage.setFilterEndDate("12/31/2026");
        
        System.out.println("[INFO] Selecting Scheme: USED CAR LOAN...");
        dashboardPage.selectFilterScheme("USED CAR LOAN");
        
        System.out.println("[INFO] Clicking Apply Filters...");
        dashboardPage.clickApplyFilters();
        Thread.sleep(1000); // Allow time for filter animations
        
        System.out.println("[INFO] Searching Audit Trails by App No...");
        dashboardPage.searchAuditTrailsByApplicationNo("APP12345678");
        Thread.sleep(3000); // Allow time for search results to load
        
        System.out.println("-----------------------------------------------------------");
        System.out.println("[PASS] Dashboard interactions tested successfully.");
        System.out.println("-----------------------------------------------------------");
    }
}
