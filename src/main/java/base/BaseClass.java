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

    protected static ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<>();
    protected WebDriver driver;
    protected DashboardPage dashboard;

    @org.testng.annotations.Parameters("browser")
    @org.testng.annotations.BeforeTest(alwaysRun = true)
    public void setupBrowser(@org.testng.annotations.Optional("chrome") String browser) {
        WebDriver webDriver;
        
        switch (browser.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                org.openqa.selenium.firefox.FirefoxOptions firefoxOptions = new org.openqa.selenium.firefox.FirefoxOptions();
                if (System.getenv("CI") != null) firefoxOptions.addArguments("--headless");
                webDriver = new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                org.openqa.selenium.edge.EdgeOptions edgeOptions = new org.openqa.selenium.edge.EdgeOptions();
                if (System.getenv("CI") != null) edgeOptions.addArguments("--headless");
                webDriver = new org.openqa.selenium.edge.EdgeDriver(edgeOptions);
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--remote-allow-origins=*");
                if (System.getenv("CI") != null) {
                    chromeOptions.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
                }
                webDriver = new ChromeDriver(chromeOptions);
                break;
        }

        threadLocalDriver.set(webDriver);
        driver = webDriver;
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
        this.driver = threadLocalDriver.get();
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

    @org.testng.annotations.AfterTest(alwaysRun = true)
    public void tearDown() {
        WebDriver currentDriver = threadLocalDriver.get();
        if (currentDriver != null) {
            currentDriver.quit();
            threadLocalDriver.remove();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}

