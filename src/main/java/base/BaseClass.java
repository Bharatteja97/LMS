package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import pages.DashboardPage;

import javax.swing.*;
import java.time.Duration;

public class BaseClass {

    protected static WebDriver driver;
    protected DashboardPage dashboard;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetUp() {

        // Step 2: Open browser and navigate to the LMS URL
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        driver.get("https://lms.alfinnext.com/");

        // Step 3: Automated Login
        try {
            org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(15));

            // Find and fill username
            org.openqa.selenium.WebElement usernameField = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(org.openqa.selenium.By.xpath("//input[@type='text' or @name='username' or @name='email']")));
            usernameField.clear();
            usernameField.sendKeys("superadmin");

            // Find and fill password
            org.openqa.selenium.WebElement passwordField = driver.findElement(org.openqa.selenium.By.xpath("//input[@type='password' or @name='password']"));
            passwordField.clear();
            passwordField.sendKeys("Alphaware@2026");

            // Click Sign in button
            org.openqa.selenium.WebElement signInButton = driver.findElement(org.openqa.selenium.By.xpath("//button[contains(., 'Sign in') or @type='submit']"));
            signInButton.click();

            // Wait for dashboard to load (assuming URL changes from login)
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions.not(
                    org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/login")
            ));

            System.out.println("Automated login successful.");
        } catch (Exception e) {
            System.err.println("Automated login failed. Please check locators or network.");
            e.printStackTrace();
        }
    }

    @BeforeClass(alwaysRun = true)
    public void classSetUp() {
        // Initialize dashboard for EACH test class instance using the shared static driver
        dashboard = new DashboardPage(driver);
    }

    @BeforeMethod(alwaysRun = true)
    public void methodSetUp() {
        // Double-check dashboard initialization before each test method to prevent NPEs
        if (dashboard == null && driver != null) {
            dashboard = new DashboardPage(driver);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}
