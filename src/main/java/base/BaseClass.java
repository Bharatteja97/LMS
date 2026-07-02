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
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // Add CI compatibility
        if (System.getenv("CI") != null) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(15));
        
        // Navigate to login page
        driver.get("https://lms.alfinnext.com/login");
        
        // Automate login
        org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(30));
        
        // Wait for Username field and enter username
        org.openqa.selenium.WebElement usernameField = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
            org.openqa.selenium.By.xpath("//input[@type='text' or contains(@name, 'username') or contains(@name, 'email')]")
        ));
        usernameField.clear();
        usernameField.sendKeys("superadmin");
        
        // Enter Password
        org.openqa.selenium.WebElement passwordField = driver.findElement(
            org.openqa.selenium.By.xpath("//input[@type='password']")
        );
        passwordField.clear();
        passwordField.sendKeys("Alphaware@2026");
        
        // Wait for Cloudflare Turnstile "Success!" to resolve automatically in test environment
        try {
            Thread.sleep(4000); // 4 seconds is usually enough for the widget to verify
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Click Sign In button
        org.openqa.selenium.WebElement signInButton = driver.findElement(
            org.openqa.selenium.By.xpath("//button[contains(normalize-space(.), 'Sign in') or @type='submit']")
        );
        signInButton.click();
        
        // Wait for login to complete (URL changes away from /login)
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.not(
            org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/login")
        ));
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

