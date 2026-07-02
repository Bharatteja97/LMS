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
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TestNG Listener that tracks every test method result and writes a
 * detailed plain-text report under:
 *
 *   reports/TestExecutionReport_<timestamp>.txt
 *
 * Registration: add the following to testng_e2e.xml (already done):
 *   <listeners>
 *       <listener class-name="listeners.TestReportListener"/>
 *   </listeners>
 */
public class TestReportListener implements ITestListener, ISuiteListener {

    // ── Per-class aggregated counters ──────────────────────────────────────
    // int[0]=passed, int[1]=failed, int[2]=skipped
    private final Map<String, int[]> classStats = new LinkedHashMap<>();

    // ── Per-class lists of individual method results ───────────────────────
    private final Map<String, List<String>> classPassedMethods  = new LinkedHashMap<>();
    private final Map<String, List<String>> classFailedMethods  = new LinkedHashMap<>();
    private final Map<String, List<String>> classSkippedMethods = new LinkedHashMap<>();

    // ── Failed test failure messages ───────────────────────────────────────
    private final Map<String, String> failureMessages = new LinkedHashMap<>();

    // ── Suite-level totals ─────────────────────────────────────────────────
    private int totalPassed  = 0;
    private int totalFailed  = 0;
    private int totalSkipped = 0;

    private long   suiteStartTime;
    private String suiteName = "ProductAccounts E2E Suite";

    private static final String REPORT_DIR = "reports";

    // ─────────────────────────────────────────────────────────────────────
    //  ISuiteListener
    // ─────────────────────────────────────────────────────────────────────

    @Override
    public void onStart(ISuite suite) {
        suiteStartTime = System.currentTimeMillis();
        suiteName      = suite.getName();
        System.out.println("[TestReportListener] Suite started: " + suiteName);
    }

    @Override
    public void onFinish(ISuite suite) {
        generateReport();
    }

    // ─────────────────────────────────────────────────────────────────────
    //  ITestListener
    // ─────────────────────────────────────────────────────────────────────

    @Override public void onStart(ITestContext context) {}
    @Override public void onFinish(ITestContext context) {}
    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult r) {}

    @Override
    public void onTestStart(ITestResult result) {
        String cls = getClass(result);
        classStats.putIfAbsent(cls, new int[]{0, 0, 0});
        classPassedMethods .putIfAbsent(cls, new ArrayList<>());
        classFailedMethods .putIfAbsent(cls, new ArrayList<>());
        classSkippedMethods.putIfAbsent(cls, new ArrayList<>());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String cls    = getClass(result);
        String method = result.getMethod().getMethodName();
        ensureClass(cls);
        classStats.get(cls)[0]++;
        classPassedMethods.get(cls).add(method);
        totalPassed++;
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String cls    = getClass(result);
        String method = result.getMethod().getMethodName();
        ensureClass(cls);
        classStats.get(cls)[1]++;
        classFailedMethods.get(cls).add(method);
        // Capture failure message
        Throwable t = result.getThrowable();
        if (t != null) {
            failureMessages.put(cls + "#" + method,
                t.getClass().getSimpleName() + ": " + truncate(t.getMessage(), 120));
        }
        totalFailed++;
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String cls    = getClass(result);
        String method = result.getMethod().getMethodName();
        ensureClass(cls);
        classStats.get(cls)[2]++;
        classSkippedMethods.get(cls).add(method);
        totalSkipped++;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Report generator
    // ─────────────────────────────────────────────────────────────────────

    private void generateReport() {
        File dir = new File(REPORT_DIR);
        if (!dir.exists()) dir.mkdirs();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String filePath  = REPORT_DIR + File.separator + "TestExecutionReport_" + timestamp + ".txt";

        long durationMs   = System.currentTimeMillis() - suiteStartTime;
        long mins         = durationMs / 60000;
        long secs         = (durationMs % 60000) / 1000;
        int  totalMethods = totalPassed + totalFailed + totalSkipped;
        int  totalScripts = classStats.size();

        StringBuilder sb = new StringBuilder();

        // ── Header ────────────────────────────────────────────────────────
        sb.append("=================================================================\n");
        sb.append("          PRODUCT ACCOUNTS – TEST EXECUTION REPORT              \n");
        sb.append("=================================================================\n");
        sb.append(String.format("  Suite Name  : %s%n", suiteName));
        sb.append(String.format("  Run Date    : %s%n",
                  new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date())));
        sb.append(String.format("  Duration    : %d min %d sec%n", mins, secs));
        sb.append("-----------------------------------------------------------------\n\n");

        // ── Summary ───────────────────────────────────────────────────────
        sb.append("  SUMMARY\n");
        sb.append("  -------\n");
        sb.append(String.format("  Total Test Scripts (Classes) Run  : %d%n", totalScripts));
        sb.append(String.format("  Total Test Methods Run            : %d%n", totalMethods));
        sb.append(String.format("  ✅ PASSED                         : %d%n", totalPassed));
        sb.append(String.format("  ❌ FAILED                         : %d%n", totalFailed));
        sb.append(String.format("  ⏭ SKIPPED                        : %d%n", totalSkipped));
        sb.append("\n");

        // ── Per-class breakdown ───────────────────────────────────────────
        sb.append("=================================================================\n");
        sb.append("  BREAKDOWN BY TEST SCRIPT\n");
        sb.append("=================================================================\n\n");

        int scriptNo = 1;
        for (Map.Entry<String, int[]> entry : classStats.entrySet()) {
            String cls  = entry.getKey();
            int[]  c    = entry.getValue();
            int    tot  = c[0] + c[1] + c[2];

            String status = (c[1] > 0) ? "❌ HAS FAILURES"
                          : (c[2] == tot) ? "⏭ ALL SKIPPED"
                          : "✅ ALL PASSED";

            sb.append(String.format("  [%d] %s  →  %s%n", scriptNo++, cls, status));
            sb.append(String.format("       Methods: %d total | %d passed | %d failed | %d skipped%n",
                                    tot, c[0], c[1], c[2]));

            // Passed methods
            List<String> passed = classPassedMethods.getOrDefault(cls, List.of());
            if (!passed.isEmpty()) {
                sb.append("       ✅ Passed  : ");
                sb.append(String.join(", ", passed)).append("\n");
            }

            // Failed methods
            List<String> failed = classFailedMethods.getOrDefault(cls, List.of());
            if (!failed.isEmpty()) {
                sb.append("       ❌ Failed  :\n");
                for (String m : failed) {
                    sb.append("           - ").append(m);
                    String msg = failureMessages.get(cls + "#" + m);
                    if (msg != null) sb.append("\n             ↳ ").append(msg);
                    sb.append("\n");
                }
            }

            // Skipped methods
            List<String> skipped = classSkippedMethods.getOrDefault(cls, List.of());
            if (!skipped.isEmpty()) {
                sb.append("       ⏭ Skipped : ");
                sb.append(String.join(", ", skipped)).append("\n");
            }

            sb.append("\n");
        }

        // ── Configured scripts ────────────────────────────────────────────
        sb.append("=================================================================\n");
        sb.append("  TEST SCRIPTS CONFIGURED IN testng_e2e.xml (8 Classes)\n");
        sb.append("=================================================================\n");
        String[] configured = {
            "1. AccountsTest",
            "2. AccountsDetailsTest",
            "3. PaymentTest",
            "4. SettlementTest",
            "5. ForeclosureTest",
            "6. BalanceTransferTest",
            "7. LedgerTest",
            "8. ScheduleTest"
        };
        for (String s : configured) sb.append("  ").append(s).append("\n");

        sb.append("\n=================================================================\n");
        sb.append("  ALL TEST SCRIPT FILES IN /tests DIRECTORY (10 Files)\n");
        sb.append("=================================================================\n");
        String[] allFiles = {
            " 1. AccountsTest.java",
            " 2. AccountsDetailsTest.java",
            " 3. PaymentTest.java",
            " 4. SettlementTest.java",
            " 5. ForeclosureTest.java",
            " 6. BalanceTransferTest.java",
            " 7. LedgerTest.java",
            " 8. ScheduleTest.java",
            " 9. ChargesTest.java",
            "10. DomDumper.java"
        };
        for (String f : allFiles) sb.append("  ").append(f).append("\n");

        // ── Footer ────────────────────────────────────────────────────────
        sb.append("\n=================================================================\n");
        sb.append("                       END OF REPORT                            \n");
        sb.append("=================================================================\n");

        // Write file
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filePath))) {
            w.write(sb.toString());
            System.out.println("[TestReportListener] ✅ Report saved → "
                               + new File(filePath).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[TestReportListener] ❌ Failed to write report: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────────────────────────

    private String getClass(ITestResult r) {
        return r.getTestClass().getRealClass().getSimpleName();
    }

    private void ensureClass(String cls) {
        classStats         .putIfAbsent(cls, new int[]{0, 0, 0});
        classPassedMethods .putIfAbsent(cls, new ArrayList<>());
        classFailedMethods .putIfAbsent(cls, new ArrayList<>());
        classSkippedMethods.putIfAbsent(cls, new ArrayList<>());
    }

    private String truncate(String s, int max) {
        if (s == null) return "null";
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }
}
