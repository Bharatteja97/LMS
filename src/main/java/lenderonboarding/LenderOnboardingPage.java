package lenderonboarding;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LenderOnboardingPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ---------------------------------------------------------
    // Locators: Step 1 - Company Information
    // ---------------------------------------------------------
    private By companyNameInput = By.xpath("//input[@placeholder='Enter company name']");
    private By officialLegalNameInput = By.xpath("//input[@placeholder='Enter official legal name']");
    private By brandNameInput = By.xpath("//input[@placeholder='Enter brand name']");
    private By clientCodeInput = By.xpath("//input[@placeholder='Enter unique client code']");
    
    // Registration & Tax
    private By dateOfIncorporationInput = By.xpath("//input[@placeholder='mm/dd/yyyy']");
    private By cinInput = By.xpath("//input[@placeholder='Enter CIN or registration number']");
    private By panInput = By.xpath("//input[@placeholder='Enter PAN number']");
    private By gstInput = By.xpath("//input[@placeholder='Enter GST number']");

    // Contact Information
    private By websiteInput = By.xpath("//input[@placeholder='https://example.com']");
    private By officialEmailInput = By.xpath("//input[@placeholder='official@company.com']");
    private By supportEmailInput = By.xpath("//input[@placeholder='support@company.com']");
    private By phoneInput = By.xpath("//input[@placeholder='+91-XXXXXXXXXX']");

    // Navigation buttons (best guess based on common patterns)
    private By continueButton = By.xpath("//button[contains(text(), 'Continue') or contains(text(), 'Next')]");

    // ---------------------------------------------------------
    // Locators: Step 2 - Office Address
    // ---------------------------------------------------------
    private By addressLine1Input = By.xpath("//input[@placeholder='Street, building, area']");
    private By addressLine2Input = By.xpath("//input[@placeholder='Landmark, floor number, etc.']");
    private By pincodeInput = By.xpath("//input[@placeholder='6-digit pincode']");
    private By addAddressButton = By.xpath("//button[contains(text(), 'Add Address')]");

    // ---------------------------------------------------------
    // Locators: Step 3 - Branding
    // ---------------------------------------------------------
    // Assuming file inputs are hidden or adjacent to the 'Browse' buttons
    private By companyLogoInput = By.xpath("//label[contains(text(),'Company Logo')]/ancestor::div[1]//input[@type='file']");
    private By faviconInput = By.xpath("//label[contains(text(),'Favicon')]/ancestor::div[1]//input[@type='file']");


    public LenderOnboardingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // --- Step 1 Actions ---
    public void fillCompanyInformation(String companyName, String legalName, String brandName, String clientCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(companyNameInput)).sendKeys(companyName);
        driver.findElement(officialLegalNameInput).sendKeys(legalName);
        driver.findElement(brandNameInput).sendKeys(brandName);
        driver.findElement(clientCodeInput).sendKeys(clientCode);
    }
    
    public void fillRegistrationDetails(String date, String cin, String pan, String gst) {
        driver.findElement(dateOfIncorporationInput).sendKeys(date);
        driver.findElement(cinInput).sendKeys(cin);
        driver.findElement(panInput).sendKeys(pan);
        if(gst != null && !gst.isEmpty()) {
            driver.findElement(gstInput).sendKeys(gst);
        }
    }

    public void fillContactInformation(String website, String officialEmail, String supportEmail, String phone) {
        driver.findElement(websiteInput).sendKeys(website);
        driver.findElement(officialEmailInput).sendKeys(officialEmail);
        driver.findElement(supportEmailInput).sendKeys(supportEmail);
        driver.findElement(phoneInput).sendKeys(phone);
    }
    
    public void clickContinue() {
        driver.findElement(continueButton).click();
    }

    // --- Step 2 Actions ---
    public void fillOfficeAddress(String address1, String address2, String pincode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(addressLine1Input)).sendKeys(address1);
        driver.findElement(addressLine2Input).sendKeys(address2);
        driver.findElement(pincodeInput).sendKeys(pincode);
    }

    public void clickAddAddress() {
        driver.findElement(addAddressButton).click();
    }

    // --- Step 3 Actions ---
    public void uploadLogo(String filePath) {
        // Elements of type='file' can be uploaded directly via sendKeys in Selenium
        driver.findElement(companyLogoInput).sendKeys(filePath);
    }

    public void uploadFavicon(String filePath) {
        driver.findElement(faviconInput).sendKeys(filePath);
    }
}

