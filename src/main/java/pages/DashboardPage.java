package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DashboardPage {

    WebDriver driver;
    WebDriverWait wait;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void selectMenu(String parentMenu, String childMenu) {
        WebElement parent = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(), '" + parentMenu + "')]")));
        parent.click();

        WebElement child = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(), '" + childMenu + "')]")));
        child.click();
    }
}



