package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DashboardPage {

    WebDriver driver;
    WebDriverWait wait;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /**
     * Navigate to a settings page by first going to the settings area,
     * then opening the sidebar and clicking the parent menu to expand it,
     * then clicking the child menu item.
     */
    public void selectMenu(String parentMenu, String childMenu) {
        // Step 1: Ensure we're on the settings page (sidebar only exists there)
        String currentUrl = driver.getCurrentUrl();
        if (!currentUrl.contains("/settings/")) {
            System.out.println("Navigating to settings page first...");
            driver.get("https://lms.alfinnext.com/settings/client-onboard?product=VEHICLE_LOAN");
            wait.until(ExpectedConditions.urlContains("/settings/"));
        }

        // Step 2: Open sidebar if collapsed (click the hamburger/toggle button)
        boolean isSidebarVisible = false;
        By parentLocator = By.xpath("//*[normalize-space(text())='" + parentMenu + "' or contains(text(), '" + parentMenu + "')]");
        try {
            // Wait up to 3 seconds for the parent menu to be visible naturally (sidebar already open by default)
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(parentLocator));
            isSidebarVisible = true;
            System.out.println("Sidebar parent menu '" + parentMenu + "' is visible.");
        } catch (Exception e) {
            System.out.println("Sidebar parent menu '" + parentMenu + "' not visible after 3 seconds. Assuming collapsed.");
        }

        if (!isSidebarVisible) {
            System.out.println("Toggling sidebar to open it...");
            try {
                WebElement toggleBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@aria-label='Toggle sidebar'] | //button[contains(@class,'sidebar')] | //header//button[1]")));
                toggleBtn.click();
                // Wait for it to become visible after click
                wait.until(ExpectedConditions.visibilityOfElementLocated(parentLocator));
                System.out.println("Sidebar toggled successfully.");
                Thread.sleep(500); // Brief pause for sidebar animation
            } catch (Exception ex) {
                System.out.println("Failed to click toggle button or parent menu not visible: " + ex.getMessage());
            }
        }

        // Step 3: Click the parent menu to expand accordion
        WebElement parentElement = wait.until(ExpectedConditions.visibilityOfElementLocated(parentLocator));
        
        boolean isChildVisible = false;
        By childLocator = By.xpath(
            "//*[normalize-space(text())='" + childMenu + "' or contains(text(), '" + childMenu + "')]"
        );
        
        try {
            WebElement childElement = driver.findElement(childLocator);
            if (childElement.isDisplayed()) {
                isChildVisible = true;
            }
        } catch (Exception e) {
            // Child not visible or not found
        }

        if (!isChildVisible) {
            System.out.println("Child menu '" + childMenu + "' is not visible. Clicking parent menu to expand: " + parentMenu);
            wait.until(ExpectedConditions.elementToBeClickable(parentElement));
            try {
                parentElement.click();
            } catch (Exception e) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", parentElement);
            }
            try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        } else {
            System.out.println("Child menu '" + childMenu + "' is already visible. Skipping parent menu click.");
        }

        // Step 4: Click the child menu item
        WebElement child = wait.until(ExpectedConditions.visibilityOfElementLocated(childLocator));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", child);
        wait.until(ExpectedConditions.elementToBeClickable(child));
        try {
            child.click();
            System.out.println("Successfully clicked child menu: " + childMenu);
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", child);
            System.out.println("Successfully clicked child menu via JS: " + childMenu);
        }

        // Wait for page to load after navigation
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }
}
