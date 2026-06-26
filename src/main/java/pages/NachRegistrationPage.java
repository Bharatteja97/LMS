package pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NachRegistrationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    @FindBy(xpath = "//div[normalize-space(.)='Nach Registration' or normalize-space(.)='NACH Registration' or normalize-space(.)='Nach Activation' or normalize-space(.)='NACH Activation']")
    private WebElement tabNachRegistration;

    @FindBy(xpath = "//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'create e-mandate')] | //a[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'create e-mandate')]")
    private WebElement createEMandateBtn;

    @FindBy(xpath = "//div[@role='dialog']//button[normalize-space(.)='Create']")
    private WebElement modalCreateBtn;

    public NachRegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public void clickNachRegistrationTab() {
        wait.until(ExpectedConditions.elementToBeClickable(tabNachRegistration));
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'}); arguments[0].click();", tabNachRegistration);
        System.out.println("[INFO] Clicked 'Nach Registration' tab.");
        sleep(2000);
    }

    public void clickCreateEMandate() {
        wait.until(ExpectedConditions.visibilityOf(createEMandateBtn));
        wait.until(ExpectedConditions.elementToBeClickable(createEMandateBtn));
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'}); arguments[0].click();", createEMandateBtn);
        System.out.println("[INFO] Clicked 'Create E-Mandate' button.");
        sleep(1000); // Wait for modal to appear
    }

    public void confirmCreateModal() {
        wait.until(ExpectedConditions.visibilityOf(modalCreateBtn));
        wait.until(ExpectedConditions.elementToBeClickable(modalCreateBtn));
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'}); arguments[0].click();", modalCreateBtn);
        System.out.println("[INFO] Clicked 'Create' inside E-Mandate modal.");
        sleep(5000); // Wait for the backend API call to finish generating the mandate
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
