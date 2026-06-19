package lenderonboarding;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
    private By dateOfIncorporationInput = By.xpath("//input[contains(translate(@placeholder, 'YMD', 'ymd'), 'yyyy') or @type='date'] | //label[contains(text(), 'Incorporation')]/following::input[1]");
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
    private By addressLine1Input = By.xpath("//input[contains(translate(@placeholder, 'SBA', 'sba'), 'street') or contains(@name, 'address')] | //label[contains(text(), 'Address') or contains(text(), 'Line 1')]/following::*[self::input or self::textarea][1] | //textarea");
    private By addressLine2Input = By.xpath("//input[contains(translate(@placeholder, 'LFE', 'lfe'), 'landmark') or contains(@name, 'address2')] | //label[contains(text(), 'Landmark') or contains(text(), 'Line 2')]/following::*[self::input or self::textarea][1] | (//textarea)[2]");
    private By pincodeInput = By.xpath("//input[contains(translate(@placeholder, 'P', 'p'), 'pin') or contains(@name, 'pin')] | //label[contains(text(), 'Pin') or contains(text(), 'Zip')]/following::input[1]");
    private By addAddressButton = By.xpath("//button[contains(text(), 'Add') or contains(text(), 'Save')] | //button[@type='submit']");

    // ---------------------------------------------------------
    // Locators: Step 3 - Branding
    // ---------------------------------------------------------
    // Assuming file inputs are hidden or adjacent to the 'Browse' buttons
    private By companyLogoInput = By.xpath("//input[@type='file' and contains(@accept, 'image')] | //label[contains(text(),'Company Logo')]/ancestor::div[1]//input[@type='file'] | (//input[@type='file'])[1]");
    private By faviconInput = By.xpath("//input[@type='file' and contains(@accept, 'image')][2] | //label[contains(text(),'Favicon')]/ancestor::div[1]//input[@type='file'] | (//input[@type='file'])[2]");


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
        wait.until(ExpectedConditions.presenceOfElementLocated(dateOfIncorporationInput));
        WebElement dateInput = driver.findElement(dateOfIncorporationInput);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dateInput);
        try {
            dateInput.sendKeys(date);
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].value='" + date + "';", dateInput);
        }
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
        WebElement btn = driver.findElement(continueButton);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
        try {
            Thread.sleep(500);
            btn.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    // --- Step 2 Actions ---
    public void fillOfficeAddress(String address1, String address2, String pincode, String state, String city) {
        WebElement line1 = null;
        try {
            line1 = wait.until(ExpectedConditions.presenceOfElementLocated(addressLine1Input));
        } catch (Exception e) {
            try {
                java.nio.file.Files.writeString(java.nio.file.Paths.get("target/pagesource.html"), driver.getPageSource());
                System.out.println("Dumped page source to target/pagesource.html");
            } catch (Exception ex) {}
            throw e;
        }
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", line1);
        try {
            Thread.sleep(500);
            line1.sendKeys(address1);
        } catch(Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].value='" + address1 + "';", line1);
        }
        
        WebElement line2 = driver.findElement(addressLine2Input);
        try { line2.sendKeys(address2); } catch(Exception e) { ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].value='" + address2 + "';", line2); }
        
        WebElement pin = driver.findElement(pincodeInput);
        try { pin.sendKeys(pincode); } catch(Exception e) { ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].value='" + pincode + "';", pin); }

        try {
            WebElement stateSelect = wait.until(ExpectedConditions.elementToBeClickable(By.name("stateId")));
            new org.openqa.selenium.support.ui.Select(stateSelect).selectByVisibleText(state);
            
            Thread.sleep(1000); // Wait for city dropdown to populate/enable based on state
            
            WebElement citySelect = wait.until(ExpectedConditions.elementToBeClickable(By.name("cityId")));
            new org.openqa.selenium.support.ui.Select(citySelect).selectByVisibleText(city);
        } catch (Exception e) {
            System.err.println("Failed to select state/city: " + e.getMessage());
            try {
                java.nio.file.Files.writeString(java.nio.file.Paths.get("target/dropdown_source.html"), driver.getPageSource());
            } catch (Exception ex) {}
        }
    }

    public void clickAddAddress() {
        WebElement btn = driver.findElement(By.xpath("//button[contains(text(), 'Add Office Address') or contains(text(), 'Add Address') or contains(text(), 'Add')]"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
        try {
            Thread.sleep(500);
            btn.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public void clickSaveAddress() {
        By locator = By.xpath("(//button[contains(text(), 'Save') or contains(text(), 'Submit') or contains(text(), 'Add') or contains(text(), 'Create') or @type='submit'])[last()]");
        try {
            WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
            Thread.sleep(500);
            btn.click();
        } catch (Exception e) {
            try {
                java.nio.file.Files.writeString(java.nio.file.Paths.get("target/modal_source.html"), driver.getPageSource());
                System.out.println("Dumped modal page source to target/modal_source.html");
            } catch (Exception ex) {}
            try {
                WebElement btn = driver.findElement(locator);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            } catch(Exception ex2) {
                throw new RuntimeException(e);
            }
        }
    }

    // --- Step 3 Actions ---
    public void uploadLogo(String filePath) {
        try {
            driver.findElement(companyLogoInput).sendKeys(filePath);
            Thread.sleep(1000);
            WebElement uploadBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Upload')]")));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", uploadBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", uploadBtn);
            Thread.sleep(2000);
        } catch (Exception e) {
            try {
                java.nio.file.Files.writeString(java.nio.file.Paths.get("target/branding_source.html"), driver.getPageSource());
                System.out.println("Dumped branding page source to target/branding_source.html");
            } catch (Exception ex) {}
            throw new RuntimeException(e);
        }
    }

    public void uploadFavicon(String filePath) {
        try {
            driver.findElement(faviconInput).sendKeys(filePath);
            Thread.sleep(1000);
            WebElement uploadBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Upload')]")));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", uploadBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", uploadBtn);
            Thread.sleep(2000);
        } catch (Exception e) {
            try {
                java.nio.file.Files.writeString(java.nio.file.Paths.get("target/branding_source.html"), driver.getPageSource());
                System.out.println("Dumped branding page source to target/branding_source.html");
            } catch (Exception ex) {}
            throw new RuntimeException(e);
        }
    }

    public void clickSubmit() {
        By submitBtn = By.xpath("//button[contains(text(), 'Submit') or contains(text(), 'Save') or contains(text(), 'Finish') or contains(text(), 'Complete')]");
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
        try {
            Thread.sleep(500);
            btn.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}

