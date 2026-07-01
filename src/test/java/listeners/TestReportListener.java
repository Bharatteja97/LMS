package listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TestNG Listener that tracks test execution and generates a plain-text report
 * at the end of the suite under:  reports/TestExecutionReport_<timestamp>.txt
 */
public class TestReportListener implements ITestListener, ISuiteListener {

    // ── Per-class counters ──────────────────────────────────────────────────
    private final Map<String, int[]> classStats = new LinkedHashMap<>();
    //  int[0] = passed, int[1] = failed, int[2] = skipped

    // ── Suite-level totals ──────────────────────────────────────────────────
    private int totalPassed  = 0;
    private int totalFailed  = 0;
    private int totalSkipped = 0;

    private long suiteStartTime;
    private String suiteName  = "LOS Test Suite";

    // ── Report output directory (relative to project root) ──────────────────
    private static final String REPORT_DIR = "reports";

    // ───────────────────────────────────────────────────────────────────────
    //  ISuiteListener callbacks
    // ───────────────────────────────────────────────────────────────────────

    @Override
    public void onStart(ISuite suite) {
        suiteStartTime = System.currentTimeMillis();
        suiteName      = suite.getName();
        System.out.println("[TestReportListener] Suite started: " + suiteName);
    }

    @Override
    public void onFinish(ISuite suite) {
        generateReport(suite.getName());
    }

    // ───────────────────────────────────────────────────────────────────────
    //  ITestListener callbacks
    // ───────────────────────────────────────────────────────────────────────

    @Override
    public void onStart(ITestContext context) { /* nothing needed */ }

    @Override
    public void onFinish(ITestContext context) { /* nothing needed */ }

    @Override
    public void onTestStart(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();
        classStats.putIfAbsent(className, new int[]{0, 0, 0});
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();
        classStats.putIfAbsent(className, new int[]{0, 0, 0});
        classStats.get(className)[0]++;
        totalPassed++;
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();
        classStats.putIfAbsent(className, new int[]{0, 0, 0});
        classStats.get(className)[1]++;
        totalFailed++;
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();
        classStats.putIfAbsent(className, new int[]{0, 0, 0});
        classStats.get(className)[2]++;
        totalSkipped++;
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) { /* unused */ }

    // ───────────────────────────────────────────────────────────────────────
    //  Report generator
    // ───────────────────────────────────────────────────────────────────────

    private void generateReport(String suiteName) {
        // Create reports directory if it doesn't exist
        File reportDir = new File(REPORT_DIR);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        // Build a timestamped filename
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileName  = REPORT_DIR + File.separator + "TestExecutionReport_" + timestamp + ".txt";

        long   durationMs  = System.currentTimeMillis() - suiteStartTime;
        long   seconds     = durationMs / 1000;
        long   minutes     = seconds / 60;
        long   remainSecs  = seconds % 60;
        int    totalTests  = totalPassed + totalFailed + totalSkipped;
        int    totalScripts = classStats.size();

        StringBuilder sb = new StringBuilder();

        sb.append("================================================================\n");
        sb.append("          LOS PRODUCT VEHICLE – TEST EXECUTION REPORT           \n");
        sb.append("================================================================\n");
        sb.append("Suite Name   : ").append(suiteName).append("\n");
        sb.append("Run Date     : ").append(new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date())).append("\n");
        sb.append("Duration     : ").append(minutes).append(" min ").append(remainSecs).append(" sec\n");
        sb.append("----------------------------------------------------------------\n\n");

        sb.append("  TOTAL TEST SCRIPTS (CLASSES) IN SUITE : ").append(totalScripts).append("\n");
        sb.append("  TOTAL TEST METHODS RUN                : ").append(totalTests).append("\n");
        sb.append("  PASSED                                : ").append(totalPassed).append("\n");
        sb.append("  FAILED                                : ").append(totalFailed).append("\n");
        sb.append("  SKIPPED                               : ").append(totalSkipped).append("\n\n");

        sb.append("================================================================\n");
        sb.append("  BREAKDOWN BY TEST SCRIPT\n");
        sb.append("================================================================\n");
        sb.append(String.format("  %-40s %6s %6s %7s%n", "Test Class", "PASS", "FAIL", "SKIP"));
        sb.append("  ").append("-".repeat(61)).append("\n");

        for (Map.Entry<String, int[]> entry : classStats.entrySet()) {
            int[] c = entry.getValue();
            sb.append(String.format("  %-40s %6d %6d %7d%n", entry.getKey(), c[0], c[1], c[2]));
        }

        sb.append("  ").append("-".repeat(61)).append("\n");
        sb.append(String.format("  %-40s %6d %6d %7d%n", "TOTAL", totalPassed, totalFailed, totalSkipped));

        sb.append("\n================================================================\n");
        sb.append("  ALL TEST SCRIPTS CONFIGURED IN testng.xml (11 Classes)\n");
        sb.append("================================================================\n");
        String[] configuredScripts = {
            "1.  CreateLeadTest",
            "2.  LeadDetailsTest",
            "3.  KycPageTest",
            "4.  SendToUnderwritingTest",
            "5.  UnderWritingTest",
            "6.  ProcessingFeeTest",
            "7.  DealerLetterTest",
            "8.  DealerConfirmationTest",
            "9.  LoanAgreementTest",
            "10. NachRegistrationTest",
            "11. DisbursementTest"
        };
        for (String s : configuredScripts) {
            sb.append("  ").append(s).append("\n");
        }

        sb.append("\n================================================================\n");
        sb.append("  ALL TEST SCRIPT FILES PRESENT IN /tests directory (19 Files)\n");
        sb.append("================================================================\n");
        String[] allFiles = {
            " 1. CheckEmails.java",
            " 2. CreateLeadTest.java",
            " 3. DealerConfirmationTempTest.java",
            " 4. DealerConfirmationTest.java",
            " 5. DealerLetterTest.java",
            " 6. DisbursementTest.java",
            " 7. KycPageTest.java",
            " 8. LeadDetailsTest.java",
            " 9. LoanAgreementTempTest.java",
            "10. LoanAgreementTest.java",
            "11. LoginTest.java",
            "12. NachRegistrationTest.java",
            "13. OperationsTest.java",
            "14. ProcessingFeeTest.java",
            "15. SanctionLetterTest.java",
            "16. SendToUnderwritingTest.java",
            "17. UnderWritingTest.java",
            "18. DealerConfirmationTempTest.java",
            "19. ProcessingFeeTest.java"
        };
        for (String f : allFiles) {
            sb.append("  ").append(f).append("\n");
        }

        sb.append("\n================================================================\n");
        sb.append("                        END OF REPORT                          \n");
        sb.append("================================================================\n");

        // Write to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(sb.toString());
            System.out.println("[TestReportListener] Report saved to: " + new File(fileName).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[TestReportListener] Failed to write report: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
