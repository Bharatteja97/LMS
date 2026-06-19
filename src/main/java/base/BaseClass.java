package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import pages.DashboardPage;

import javax.swing.*;
import java.time.Duration;

public class BaseClass {

    protected static WebDriver driver;
    protected DashboardPage dashboard;

    @BeforeSuite
    public void setUp() {
        // Step 1: Windows confirmation prompt BEFORE opening browser
        int confirm = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to proceed with the test suite?\n\n" +
            "The browser will open to lms.alfinnext.com.\n" +
            "You will need to log in manually before tests begin.\n\n" +
            "Click YES to proceed, NO to cancel.",
            "LMS Test Suite - Confirm Start",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            System.out.println("Test suite cancelled by user.");
            System.exit(0);
        }

        // Step 2: Open browser and navigate to the LMS URL
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://lms.alfinnext.com/");

        // Step 3: Windows prompt - wait for manual login
        JOptionPane.showMessageDialog(null,
            "The browser is now open at lms.alfinnext.com.\n\n" +
            "Please log in manually in the browser.\n" +
            "  Username: superadmin\n" +
            "  Password: Alphaware@2026\n\n" +
            "Click OK here ONLY after you are successfully logged in.\n" +
            "Tests will start running immediately after you click OK.",
            "Manual Login Required",
            JOptionPane.INFORMATION_MESSAGE);

        // Step 4: Initialize DashboardPage after login
        dashboard = new DashboardPage(driver);
    }

    @AfterSuite
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}
