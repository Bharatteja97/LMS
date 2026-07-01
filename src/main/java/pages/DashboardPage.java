package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DashboardPage {

    WebDriver driver;
    WebDriverWait wait;

    /**
     * Fallback direct URL map: parentMenu -> childMenu -> URL.
     * Used when the sidebar toggle approach fails to find the menu item.
     */
    private static final Map<String, Map<String, String>> DIRECT_URLS = new HashMap<>();

    static {
        Map<String, String> lenderMenus = new HashMap<>();
        lenderMenus.put("Lender List",      "https://lms.alfinnext.com/settings/client-onboard?product=VEHICLE_LOAN");
        lenderMenus.put("Lender Documents", "https://lms.alfinnext.com/settings/client-documents?product=VEHICLE_LOAN");
        DIRECT_URLS.put("Lender Onboarding", lenderMenus);

        Map<String, String> userMenus = new HashMap<>();
        userMenus.put("Users",    "https://lms.alfinnext.com/settings/users?product=VEHICLE_LOAN");
        userMenus.put("Roles",    "https://lms.alfinnext.com/settings/roles?product=VEHICLE_LOAN");
        userMenus.put("Branches", "https://lms.alfinnext.com/settings/branches?product=VEHICLE_LOAN");
        DIRECT_URLS.put("User Management", userMenus);
    }

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /**
     * Navigate to a settings page via the sidebar.
     * Uses a broader XPath (contains(., 'text')) so elements whose text is
     * split across child <span> / <i> nodes are still matched.
     * Falls back to a direct URL navigation if the sidebar cannot be found.
     */
    public void selectMenu(String parentMenu, String childMenu) {
        // Step 1: Ensure we're on the settings page (sidebar only exists there)
        String currentUrl = driver.getCurrentUrl();
        if (!currentUrl.contains("/settings/")) {
            System.out.println("Navigating to settings page first...");
            driver.get("https://lms.alfinnext.com/settings/client-onboard?product=VEHICLE_LOAN");
            wait.until(ExpectedConditions.urlContains("/settings/"));
        }

        // Broader XPath: matches any element whose full concatenated text contains the label.
        // This handles sidebars where text is wrapped in child <span> elements.
        By parentLocator = By.xpath(
            "//*[self::a or self::li or self::div or self::span or self::button]"
            + "[contains(normalize-space(.), '" + parentMenu + "')]"
            + "[not(.//a[contains(normalize-space(.), '" + parentMenu + "')])]" // prefer leaf-most match
        );

        // Step 2: Check if sidebar parent is already visible
        boolean isSidebarVisible = false;
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(parentLocator));
            isSidebarVisible = true;
            System.out.println("Sidebar parent menu '" + parentMenu + "' is visible.");
        } catch (Exception e) {
            System.out.println("Sidebar parent menu '" + parentMenu + "' not visible after 3 seconds. Attempting toggle.");
        }

        // Step 3: If not visible, try to toggle the sidebar open
        if (!isSidebarVisible) {
            try {
                WebElement toggleBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@aria-label='Toggle sidebar']"
                           + " | //button[contains(@class,'sidebar-toggle')]"
                           + " | //button[contains(@class,'hamburger')]"
                           + " | //header//button[1]")));
                toggleBtn.click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(parentLocator));
                isSidebarVisible = true;
                System.out.println("Sidebar opened via toggle.");
                Thread.sleep(500);
            } catch (Exception ex) {
                System.out.println("Sidebar toggle failed: " + ex.getMessage());
            }
        }

        // Step 4: If sidebar still not visible, use direct URL fallback
        if (!isSidebarVisible) {
            Map<String, String> childUrls = DIRECT_URLS.get(parentMenu);
            if (childUrls != null && childUrls.containsKey(childMenu)) {
                String url = childUrls.get(childMenu);
                System.out.println("Sidebar unavailable. Navigating directly to: " + url);
                driver.get(url);
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                return;
            } else {
                // No fallback URL available — throw to surface the issue clearly
                throw new RuntimeException(
                    "Cannot navigate to '" + parentMenu + " > " + childMenu
                    + "': sidebar not accessible and no direct URL fallback defined.");
            }
        }

        // Step 5: Click the parent menu to expand the accordion (if child not already visible)
        By childLocator = By.xpath(
            "//*[self::a or self::li or self::div or self::span]"
            + "[contains(normalize-space(.), '" + childMenu + "')]"
            + "[not(.//a[contains(normalize-space(.), '" + childMenu + "')])]" // prefer leaf-most
        );

        boolean isChildVisible = false;
        try {
            WebElement childEl = driver.findElement(childLocator);
            isChildVisible = childEl.isDisplayed();
        } catch (Exception e) {
            // not yet visible
        }

        if (!isChildVisible) {
            System.out.println("Expanding parent menu: " + parentMenu);
            WebElement parentElement = wait.until(ExpectedConditions.elementToBeClickable(parentLocator));
            try {
                parentElement.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", parentElement);
            }
            try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        } else {
            System.out.println("Child menu '" + childMenu + "' already visible.");
        }

        // Step 6: Click the child menu item
        WebElement child = wait.until(ExpectedConditions.visibilityOfElementLocated(childLocator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", child);
        wait.until(ExpectedConditions.elementToBeClickable(child));
        try {
            child.click();
            System.out.println("Clicked child menu: " + childMenu);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", child);
            System.out.println("Clicked child menu via JS: " + childMenu);
        }

        // Brief wait for page navigation
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }
}
