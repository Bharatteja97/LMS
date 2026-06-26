package operations;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the Operations module.
 *
 * Covers two views:
 * 1. Operations LIST page  — /vehicle/operations?product=VEHICLE_LOAN
 * 2. Operations DETAIL page — /vehicle/operations/{id}?product=VEHICLE_LOAN
 */
public class OperationsPage {

    public static final String BASE_URL =
            "https://lms.alfinnext.com/vehicle/operations?product=VEHICLE_LOAN";
    public static final String DETAIL_URL_TEMPLATE =
            "https://lms.alfinnext.com/vehicle/operations/%s?product=VEHICLE_LOAN";

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    // ════════════════════════════════════════════════════════════════════════════
    // LIST PAGE ELEMENTS
    // ════════════════════════════════════════════════════════════════════════════

    @FindBy(xpath = "//input[contains(@placeholder,'Search by the fields')]")
    private WebElement quickSearchInput;

    @FindBy(xpath = "//button[normalize-space(.)='Reset']")
    private WebElement resetButton;

    @FindBy(xpath = "//button[contains(normalize-space(.), 'NEFT Report')]")
    private WebElement neftReportButton;

    // ════════════════════════════════════════════════════════════════════════════
    // DETAIL PAGE ELEMENTS
    // ════════════════════════════════════════════════════════════════════════════

    @FindBy(xpath = "//button[normalize-space(.)='Sanction']")
    private WebElement sanctionButton;

    @FindBy(xpath = "//button[normalize-space(.)='Update status' or normalize-space(.)='Update Status']")
    private WebElement updateStatusButton;

    @FindBy(xpath = "//button[contains(., 'Processing Fee Link') or contains(., 'Processing fee link') or contains(., 'Processing Fee')]")
    private WebElement dispatchProcessingFeeLinkButton;

    @FindBy(xpath = "//div[@role='dialog']//button[normalize-space(.)='Dispatch']")
    private WebElement modalDispatchButton;

    // ════════════════════════════════════════════════════════════════════════════
    // TABS
    // ════════════════════════════════════════════════════════════════════════════

    @FindBy(xpath = "//div[normalize-space(.)='Borrower Details']")
    private WebElement tabBorrowerDetails;

    @FindBy(xpath = "//div[normalize-space(.)='Asset details' or normalize-space(.)='Asset Details']")
    private WebElement tabAssetDetails;

    @FindBy(xpath = "//div[normalize-space(.)='Loan Details']")
    private WebElement tabLoanDetails;

    @FindBy(xpath = "//div[normalize-space(.)='Deal Parameter' or normalize-space(.)='Deal Parameters']")
    private WebElement tabDealParameter;

    @FindBy(xpath = "//div[normalize-space(.)='Bank Details']")
    private WebElement tabBankDetails;

    @FindBy(xpath = "//div[normalize-space(.)='Documents']")
    private WebElement tabDocuments;

    @FindBy(xpath = "//div[normalize-space(.)='Processing fee' or normalize-space(.)='Processing Fee']")
    private WebElement tabProcessingFee;

    @FindBy(xpath = "//div[normalize-space(.)='Disbursement details' or normalize-space(.)='Disbursement Details']")
    private WebElement tabDisbursementDetails;

    @FindBy(xpath = "//div[normalize-space(.)='Loan Agreement']")
    private WebElement tabLoanAgreement;

    @FindBy(xpath = "//div[normalize-space(.)='Nach Registration' or normalize-space(.)='NACH Registration' or normalize-space(.)='Nach Activation' or normalize-space(.)='NACH Activation']")
    private WebElement tabNachRegistration;

    @FindBy(xpath = "//div[normalize-space(.)='Repayment schedule' or normalize-space(.)='Repayment Schedule']")
    private WebElement tabRepaymentSchedule;

    @FindBy(xpath = "//div[normalize-space(.)='Tracking History']")
    private WebElement tabTrackingHistory;

    @FindBy(xpath = "//div[normalize-space(.)='Application Stepper']")
    private WebElement tabApplicationStepper;

    @FindBy(xpath = "//div[normalize-space(.)='AI Insights']")
    private WebElement tabAiInsights;

    public OperationsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // LIST PAGE ACTIONS
    // ════════════════════════════════════════════════════════════════════════════

    public void navigateToList() {
        driver.get(BASE_URL);
        waitForTableToLoad();
        System.out.println("[INFO] Navigated to Operations list page.");
    }

    public void navigateToDetail(String dbId) {
        String url = String.format(DETAIL_URL_TEMPLATE, dbId);
        driver.get(url);
        waitForDetailPageToLoad();
        System.out.println("[INFO] Navigated directly to Operations detail ID: " + dbId);
    }

    public void search(String query) {
        wait.until(ExpectedConditions.visibilityOf(quickSearchInput));
        quickSearchInput.clear();
        quickSearchInput.sendKeys(query);
        quickSearchInput.sendKeys(Keys.ENTER);
        sleep(2000);
        System.out.println("[INFO] Searched Operations table for: " + query);
    }

    public void clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(resetButton));
        jsClick(resetButton);
        sleep(2000);
        System.out.println("[INFO] Clicked Operations Reset button.");
    }

    public String openFirstApplication() {
        By firstAppCell = By.xpath("//table//tbody//tr[1]//td[3]");
        WebElement cell = wait.until(ExpectedConditions.elementToBeClickable(firstAppCell));
        String appNo = cell.getText().trim();
        jsClick(cell);
        waitForDetailPageToLoad();
        System.out.println("[INFO] Opened first application: " + appNo);
        return appNo;
    }

    public int getTableRowCount() {
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        return rows.size();
    }

    // ════════════════════════════════════════════════════════════════════════════
    // DETAIL PAGE ACTIONS
    // ════════════════════════════════════════════════════════════════════════════

    public String getApplicationStatus() {
        try {
            WebElement statusBadge = driver.findElement(By.xpath(
                "//button[contains(@class,'rounded') and (contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'approved') " +
                "or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'processing'))]"
            ));
            return statusBadge.getText().trim();
        } catch (Exception e) {
            // Fallback
            try {
                WebElement badge = driver.findElement(By.xpath("//*[contains(@class,'badge') or contains(@class,'chip')]"));
                return badge.getText().trim();
            } catch (Exception ex) {
                return "UNKNOWN";
            }
        }
    }

    public void clickSanction() {
        wait.until(ExpectedConditions.elementToBeClickable(sanctionButton));
        jsClick(sanctionButton);
        System.out.println("[INFO] Clicked 'Sanction' button.");
        sleep(2000);
    }

    public boolean isSanctionButtonEnabled() {
        try {
            return sanctionButton.isDisplayed() && sanctionButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickUpdateStatus() {
        wait.until(ExpectedConditions.elementToBeClickable(updateStatusButton));
        jsClick(updateStatusButton);
        System.out.println("[INFO] Clicked 'Update status' button.");
        sleep(1000);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // TAB NAVIGATION
    // ════════════════════════════════════════════════════════════════════════════

    public void clickBorrowerDetailsTab() { clickTab(tabBorrowerDetails, "Borrower Details"); }
    public void clickAssetDetailsTab() { clickTab(tabAssetDetails, "Asset Details"); }
    public void clickLoanDetailsTab() { clickTab(tabLoanDetails, "Loan Details"); }
    public void clickDealParameterTab() { clickTab(tabDealParameter, "Deal Parameter"); }
    public void clickBankDetailsTab() { clickTab(tabBankDetails, "Bank Details"); }
    public void clickDocumentsTab() { clickTab(tabDocuments, "Documents"); }
    public void clickProcessingFeeTab() { clickTab(tabProcessingFee, "Processing Fee"); }
    public void clickDisbursementDetailsTab() { clickTab(tabDisbursementDetails, "Disbursement Details"); }
    public void clickLoanAgreementTab() { clickTab(tabLoanAgreement, "Loan Agreement"); }
    public void clickNachRegistrationTab() { clickTab(tabNachRegistration, "NACH Registration"); }
    public void clickRepaymentScheduleTab() { clickTab(tabRepaymentSchedule, "Repayment Schedule"); }
    public void clickTrackingHistoryTab() { clickTab(tabTrackingHistory, "Tracking History"); }
    public void clickApplicationStepperTab() { clickTab(tabApplicationStepper, "Application Stepper"); }
    public void clickAiInsightsTab() { clickTab(tabAiInsights, "AI Insights"); }

    private void clickTab(WebElement tabButton, String tabName) {
        wait.until(ExpectedConditions.elementToBeClickable(tabButton));
        scrollToElement(tabButton);
        jsClick(tabButton);
        sleep(1000);
        System.out.println("[INFO] Clicked tab: " + tabName);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // PROCESSING FEE METHODS
    // ════════════════════════════════════════════════════════════════════════════

    public void clickDispatchProcessingFeeLink() {
        wait.until(ExpectedConditions.elementToBeClickable(dispatchProcessingFeeLinkButton));
        jsClick(dispatchProcessingFeeLinkButton);
        System.out.println("[INFO] Clicked 'Dispatch Processing Fee Link' button.");
        sleep(1000);
    }

    public void clickModalDispatch() {
        wait.until(ExpectedConditions.elementToBeClickable(modalDispatchButton));
        jsClick(modalDispatchButton);
        System.out.println("[INFO] Clicked 'Dispatch' inside confirmation modal.");
        sleep(3000);
    }

    public boolean isDispatchProcessingFeeModalOpen() {
        try {
            return modalDispatchButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getProcessingFeePaymentLink() {
        By linkInputLocator = By.xpath("//input[contains(@value, 'rzp') or contains(@value, 'razorpay')] | //*[contains(text(), 'Payment Link')]/following::input[1]");
        WebElement linkInput = wait.until(ExpectedConditions.visibilityOfElementLocated(linkInputLocator));
        String link = linkInput.getAttribute("value");
        if (link == null || link.isEmpty()) {
            link = linkInput.getText().trim();
        }
        System.out.println("[INFO] Extracted Processing Fee Payment Link from UI: " + link);
        return link;
    }

    public void clickProcessingFeePaymentLink() {
        try {
            By openButtonLocator = By.xpath("//*[contains(text(), 'Payment Link')]/following::a[1] | //input[contains(@value, 'rzp')]/following-sibling::a | //input[contains(@value, 'rzp')]/following::button[1]");
            WebElement openButton = wait.until(ExpectedConditions.elementToBeClickable(openButtonLocator));
            jsClick(openButton);
            System.out.println("[INFO] Clicked external payment link button in UI.");
        } catch (Exception e) {
            System.out.println("[WARN] Could not click redirect button, navigating directly to the extracted URL: " + e.getMessage());
            String link = getProcessingFeePaymentLink();
            driver.get(link);
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // INFRASTRUCTURE
    // ════════════════════════════════════════════════════════════════════════════

    public void waitForTableToLoad() {
        wait.until(d -> !d.findElements(By.xpath("//table//tbody//tr")).isEmpty());
    }

    public void waitForDetailPageToLoad() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[normalize-space(.)='Sanction']")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[normalize-space(.)='Update status' or normalize-space(.)='Update Status']")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[normalize-space(.)='Borrower Details']"))
        ));
    }

    private void jsClick(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    private void scrollToElement(WebElement element) {
        try {
            js.executeScript("arguments[0].scrollIntoView({behavior:'instant',block:'center'});", element);
            sleep(300);
        } catch (Exception ignored) {}
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }
}
