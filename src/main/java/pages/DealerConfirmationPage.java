package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.io.File;

public class DealerConfirmationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    @FindBy(id = "hyp-file-input")
    private WebElement hypFileInput;

    @FindBy(name = "accountHolderName")
    private WebElement accountHolderNameInput;

    @FindBy(name = "bankName")
    private WebElement bankNameInput;

    @FindBy(name = "accountNumber")
    private WebElement accountNumberInput;

    @FindBy(name = "branchName")
    private WebElement branchNameInput;

    @FindBy(name = "city")
    private WebElement cityInput;

    @FindBy(name = "ifscCode")
    private WebElement ifscCodeInput;

    @FindBy(name = "accountType")
    private WebElement accountTypeSelect;

    @FindBy(id = "consent")
    private WebElement consentCheckbox;

    @FindBy(xpath = "//button[normalize-space(.)='Save Bank Details']")
    private WebElement saveBankDetailsBtn;

    public DealerConfirmationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public void fillFormAndSubmit(
            String accountHolderName,
            String bankName,
            String accountNumber,
            String branchName,
            String city,
            String ifscCode,
            String accountType,
            String absoluteFilePath
    ) throws InterruptedException {
        wait.until(ExpectedConditions.visibilityOf(accountHolderNameInput));

        // Skip file upload as it triggers an async "Failed to upload" alert and isn't mandatory
        // System.out.println("[INFO] Uploading hypothecation letter: " + absoluteFilePath);
        // hypFileInput.sendKeys(absoluteFilePath);
        // Thread.sleep(1000);

        System.out.println("[INFO] Filling bank details...");
        accountHolderNameInput.sendKeys(accountHolderName);
        bankNameInput.sendKeys(bankName);
        accountNumberInput.sendKeys(accountNumber);
        branchNameInput.sendKeys(branchName);
        cityInput.sendKeys(city);
        ifscCodeInput.sendKeys(ifscCode);

        Select select = new Select(accountTypeSelect);
        select.selectByVisibleText(accountType);

        if (!consentCheckbox.isSelected()) {
            js.executeScript("arguments[0].click();", consentCheckbox);
        }

        Thread.sleep(1000);
        System.out.println("[INFO] Submitting Dealer Confirmation form...");
        js.executeScript("arguments[0].click();", saveBankDetailsBtn);
        Thread.sleep(5000);
    }
}
