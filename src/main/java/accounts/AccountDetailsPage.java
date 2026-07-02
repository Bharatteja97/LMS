package accounts;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AccountDetailsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // ── Header ────────────────────────────────────────────────────────────────
    // The borrower name appears as the largest heading next to the avatar

    @FindBy(xpath = "//h1 | //h2 | //*[contains(@class,'borrower') or contains(@class,'customer-name')][1]")
    private WebElement borrowerName;

    // Inline labels: "LAN: VEHICLE_000026", "AN: APP82647478", etc.
    @FindBy(xpath = "//*[contains(normalize-space(.),'LAN:')][not(.//*[contains(.,'LAN:')])][1]")
    private WebElement lanLabel;

    // "AN:" must NOT also contain "LAN:" to avoid matching the LAN element
    @FindBy(xpath = "//*[contains(normalize-space(.),'AN:') and not(contains(normalize-space(.),'LAN:'))][not(.//*[contains(normalize-space(.),'AN:') and not(contains(normalize-space(.),'LAN:'))])][1]")
    private WebElement anLabel;

    @FindBy(xpath = "//*[contains(normalize-space(.),'Product Type:')][not(.//*[contains(.,'Product Type:')])][1]")
    private WebElement productTypeLabel;

    @FindBy(xpath = "//*[contains(normalize-space(.),'Disbursed On:')][not(.//*[contains(.,'Disbursed On:')])][1]")
    private WebElement disbursedOnLabel;

    // Status badge: first element in document order whose full text equals a known loan status.
    // Not restricted to leaf nodes — the badge may have icon children (e.g. a green-dot <span>).
    // [1] picks the header badge; it appears before any other status-like text deeper in the page.
    @FindBy(xpath = "(//*[normalize-space(.)='Active' or normalize-space(.)='Overdue' or normalize-space(.)='Settled' or normalize-space(.)='Foreclosed' or normalize-space(.)='NPA' or normalize-space(.)='Closed' or normalize-space(.)='Moratorium' or normalize-space(.)='Balance Transferred' or normalize-space(.)='Insurance Closed' or normalize-space(.)='Written Off' or normalize-space(.)='Cancelled' or normalize-space(.)='Disbursed' or normalize-space(.)='Pre Emi' or normalize-space(.)='PRE_EMI' or normalize-space(.)='ACTIVE' or normalize-space(.)='OVERDUE'][not(self::option or self::select)])[1]")
    private WebElement statusBadge;

    // ── Info grid fields (label → value sibling pattern) ──────────────────────

    @FindBy(xpath = "//*[normalize-space(text())='Loan Amount']/following-sibling::*[1]")
    private WebElement loanAmount;

    @FindBy(xpath = "//*[normalize-space(text())='Outstanding']/following-sibling::*[1]")
    private WebElement outstanding;

    @FindBy(xpath = "//*[normalize-space(text())='Current DPD']/following-sibling::*[1]")
    private WebElement currentDPD;

    @FindBy(xpath = "//*[normalize-space(text())='Current Liability']/following-sibling::*[1]")
    private WebElement currentLiability;

    @FindBy(xpath = "//*[normalize-space(text())='Next EMI Due']/following-sibling::*[1]")
    private WebElement nextEMIDue;

    @FindBy(xpath = "//*[normalize-space(text())='Lead no']/following-sibling::*[1]")
    private WebElement leadNo;

    @FindBy(xpath = "//*[normalize-space(text())='Contact']/following-sibling::*[1]")
    private WebElement contact;

    @FindBy(xpath = "//*[normalize-space(text())='PAN']/following-sibling::*[1]")
    private WebElement pan;

    @FindBy(xpath = "//*[normalize-space(text())='Email']/following-sibling::*[1]")
    private WebElement email;

    @FindBy(xpath = "//*[normalize-space(text())='Employment Type']/following-sibling::*[1]")
    private WebElement employmentType;

    @FindBy(xpath = "//*[normalize-space(text())='Residential Type']/following-sibling::*[1]")
    private WebElement residentialType;

    @FindBy(xpath = "//*[normalize-space(text())='Tenure']/following-sibling::*[1]")
    private WebElement tenure;

    @FindBy(xpath = "//*[normalize-space(text())='Interest']/following-sibling::*[1]")
    private WebElement interest;

    // ── Coloured summary cards ─────────────────────────────────────────────────
    // Strategy: find the deepest element whose text CONTAINS the card label but does NOT
    // contain any other card labels (ruling out the parent row that holds all 4 cards).
    // getText() on the card container returns both the label and the amount.

    @FindBy(xpath = "(//*[contains(normalize-space(.),'DISBURSED AMOUNT') and not(contains(normalize-space(.),'NET OUTSTANDING')) and not(contains(normalize-space(.),'PAID AMOUNT')) and not(contains(normalize-space(.),'OVERDUE'))])[1]")
    private WebElement disbursedAmountCard;

    @FindBy(xpath = "(//*[contains(normalize-space(.),'NET OUTSTANDING BALANCE') and not(contains(normalize-space(.),'DISBURSED AMOUNT')) and not(contains(normalize-space(.),'PAID AMOUNT')) and not(contains(normalize-space(.),'OVERDUE'))])[1]")
    private WebElement netOutstandingCard;

    @FindBy(xpath = "(//*[contains(normalize-space(.),'PAID AMOUNT') and not(contains(normalize-space(.),'DISBURSED')) and not(contains(normalize-space(.),'NET OUTSTANDING')) and not(contains(normalize-space(.),'OVERDUE'))])[1]")
    private WebElement paidAmountCard;

    @FindBy(xpath = "(//*[contains(normalize-space(.),'OVERDUE AMOUNT') and not(contains(normalize-space(.),'DISBURSED')) and not(contains(normalize-space(.),'NET OUTSTANDING')) and not(contains(normalize-space(.),'PAID AMOUNT'))])[1]")
    private WebElement overdueAmountCard;

    // ── Tabs ──────────────────────────────────────────────────────────────────
    // Tab labels observed: Overview, Schedules, Ledger, Transaction, Payment,
    // Nach Presentation, Documents, Tracking History, Charges, Disbursements,
    // Balance Transfer, Insurance Claims, Audit Trail
    //
    // normalize-space(.) matches all descendant text (e.g. <button><span>Schedules</span></button>).
    // normalize-space(text()) would only match direct text nodes and fail for wrapped labels.
    // not(.//*[normalize-space(.)='...']) ensures we click the deepest (leaf) matching element.

    @FindBy(xpath = "//*[normalize-space(.)='Overview' and not(.//*[normalize-space(.)='Overview'])]")
    private WebElement overviewTab;

    @FindBy(xpath = "//*[normalize-space(.)='Schedules' and not(.//*[normalize-space(.)='Schedules'])]")
    private WebElement schedulesTab;

    @FindBy(xpath = "//*[normalize-space(.)='Ledger' and not(.//*[normalize-space(.)='Ledger'])]")
    private WebElement ledgerTab;

    @FindBy(xpath = "//*[normalize-space(.)='Transaction' and not(.//*[normalize-space(.)='Transaction'])]")
    private WebElement transactionTab;

    @FindBy(xpath = "//*[normalize-space(.)='Payment' and not(.//*[normalize-space(.)='Payment'])]")
    private WebElement paymentTab;

    @FindBy(xpath = "//*[normalize-space(.)='Nach Presentation' and not(.//*[normalize-space(.)='Nach Presentation'])]")
    private WebElement nachPresentationTab;

    @FindBy(xpath = "//*[normalize-space(.)='Documents' and not(.//*[normalize-space(.)='Documents'])]")
    private WebElement documentsTab;

    @FindBy(xpath = "//*[normalize-space(.)='Tracking History' and not(.//*[normalize-space(.)='Tracking History'])]")
    private WebElement trackingHistoryTab;

    @FindBy(xpath = "//*[normalize-space(.)='Charges' and not(.//*[normalize-space(.)='Charges'])]")
    private WebElement chargesTab;

    @FindBy(xpath = "//*[normalize-space(.)='Disbursements' and not(.//*[normalize-space(.)='Disbursements'])]")
    private WebElement disbursementsTab;

    @FindBy(xpath = "//*[normalize-space(.)='Balance Transfer' and not(.//*[normalize-space(.)='Balance Transfer'])]")
    private WebElement balanceTransferTab;

    @FindBy(xpath = "//*[normalize-space(.)='Insurance Claims' and not(.//*[normalize-space(.)='Insurance Claims'])]")
    private WebElement insuranceClaimsTab;

    @FindBy(xpath = "//*[normalize-space(.)='Audit Trail' and not(.//*[normalize-space(.)='Audit Trail'])]")
    private WebElement auditTrailTab;

    public AccountDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // ── Header getters ────────────────────────────────────────────────────────

    public String getBorrowerName() {
        return wait.until(ExpectedConditions.visibilityOf(borrowerName)).getText().trim();
    }

    /** Returns the full "LAN: VEHICLE_000026" text; use substring for just the value. */
    public String getLANText() {
        return wait.until(ExpectedConditions.visibilityOf(lanLabel)).getText().trim();
    }

    public String getLANNumber() {
        String full = getLANText();
        return full.contains(":") ? full.substring(full.indexOf(':') + 1).trim() : full;
    }

    public String getANText() {
        return wait.until(ExpectedConditions.visibilityOf(anLabel)).getText().trim();
    }

    public String getProductType() {
        String full = wait.until(ExpectedConditions.visibilityOf(productTypeLabel)).getText().trim();
        return full.contains(":") ? full.substring(full.indexOf(':') + 1).trim() : full;
    }

    public String getDisbursedOn() {
        String full = wait.until(ExpectedConditions.visibilityOf(disbursedOnLabel)).getText().trim();
        return full.contains(":") ? full.substring(full.indexOf(':') + 1).trim() : full;
    }

    public String getStatusBadge() {
        return wait.until(ExpectedConditions.visibilityOf(statusBadge)).getText().trim();
    }

    // ── Info grid getters ─────────────────────────────────────────────────────

    public String getLoanAmount()      { return wait.until(ExpectedConditions.visibilityOf(loanAmount)).getText().trim(); }
    public String getOutstanding()     { return wait.until(ExpectedConditions.visibilityOf(outstanding)).getText().trim(); }
    public String getCurrentDPD()      { return wait.until(ExpectedConditions.visibilityOf(currentDPD)).getText().trim(); }
    public String getCurrentLiability(){ return wait.until(ExpectedConditions.visibilityOf(currentLiability)).getText().trim(); }
    public String getNextEMIDue()      { return wait.until(ExpectedConditions.visibilityOf(nextEMIDue)).getText().trim(); }
    public String getLeadNo()          { return wait.until(ExpectedConditions.visibilityOf(leadNo)).getText().trim(); }
    public String getContact()         { return wait.until(ExpectedConditions.visibilityOf(contact)).getText().trim(); }
    public String getPAN()             { return wait.until(ExpectedConditions.visibilityOf(pan)).getText().trim(); }
    public String getEmail()           { return wait.until(ExpectedConditions.visibilityOf(email)).getText().trim(); }
    public String getEmploymentType()  { return wait.until(ExpectedConditions.visibilityOf(employmentType)).getText().trim(); }
    public String getResidentialType() { return wait.until(ExpectedConditions.visibilityOf(residentialType)).getText().trim(); }
    public String getTenure()          { return wait.until(ExpectedConditions.visibilityOf(tenure)).getText().trim(); }
    public String getInterest()        { return wait.until(ExpectedConditions.visibilityOf(interest)).getText().trim(); }

    // ── Summary card getters ──────────────────────────────────────────────────
    // getText() on the card container returns multi-line text: label + amount (e.g. "DISBURSED AMOUNT\n₹70,000")

    public String getDisbursedAmount()       { return wait.until(ExpectedConditions.visibilityOf(disbursedAmountCard)).getText().trim(); }
    public String getNetOutstandingBalance() { return wait.until(ExpectedConditions.visibilityOf(netOutstandingCard)).getText().trim(); }
    public String getPaidAmount()            { return wait.until(ExpectedConditions.visibilityOf(paidAmountCard)).getText().trim(); }
    public String getOverdueAmount()         { return wait.until(ExpectedConditions.visibilityOf(overdueAmountCard)).getText().trim(); }

    // ── Tab navigation ────────────────────────────────────────────────────────

    public AccountDetailsPage clickOverviewTab() {
        wait.until(ExpectedConditions.elementToBeClickable(overviewTab)).click();
        return this;
    }

    public SchedulePage clickSchedulesTab() {
        wait.until(ExpectedConditions.elementToBeClickable(schedulesTab)).click();
        return new SchedulePage(driver);
    }

    public LedgerPage clickLedgerTab() {
        wait.until(ExpectedConditions.elementToBeClickable(ledgerTab)).click();
        return new LedgerPage(driver);
    }

    public TransactionPage clickTransactionTab() {
        wait.until(ExpectedConditions.elementToBeClickable(transactionTab)).click();
        return new TransactionPage(driver);
    }

    public PaymentPage clickPaymentTab() {
        wait.until(ExpectedConditions.elementToBeClickable(paymentTab)).click();
        return new PaymentPage(driver);
    }

    public AccountDetailsPage clickNachPresentationTab() {
        wait.until(ExpectedConditions.elementToBeClickable(nachPresentationTab)).click();
        return this;
    }

    public DocumentsPage clickDocumentsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(documentsTab)).click();
        return new DocumentsPage(driver);
    }

    public AccountDetailsPage clickTrackingHistoryTab() {
        wait.until(ExpectedConditions.elementToBeClickable(trackingHistoryTab)).click();
        return this;
    }

    public ChargesPage clickChargesTab() {
        wait.until(ExpectedConditions.elementToBeClickable(chargesTab)).click();
        return new ChargesPage(driver);
    }

    public AccountDetailsPage clickDisbursementsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(disbursementsTab)).click();
        return this;
    }

    public BalanceTransferPage clickBalanceTransferTab() {
        wait.until(ExpectedConditions.elementToBeClickable(balanceTransferTab)).click();
        return new BalanceTransferPage(driver);
    }

    public AccountDetailsPage clickInsuranceClaimsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(insuranceClaimsTab)).click();
        return this;
    }

    public AccountDetailsPage clickAuditTrailTab() {
        wait.until(ExpectedConditions.elementToBeClickable(auditTrailTab)).click();
        return this;
    }

    // ── Action Menu ───────────────────────────────────────────────────────────

    public SettlementPage clickSettlementOption() {
        String[] menuLocators = {
            "//*[@aria-haspopup='menu']",
            "//*[@aria-label='Actions']",
            "//*[@aria-label='More']",
            "//*[@aria-label='more']",
            "(//*[normalize-space(.)='Active' or normalize-space(.)='ACTIVE' or normalize-space(.)='Overdue' or normalize-space(.)='OVERDUE']/following-sibling::*//button)[1]",
            "//button[.//svg]"
        };

        for (String locator : menuLocators) {
            try {
                java.util.List<WebElement> buttons = driver.findElements(By.xpath(locator));
                for (WebElement btn : buttons) {
                    try {
                        btn.click();
                        Thread.sleep(1000);
                        
                        // Look for the deepest element containing 'settlement' case-insensitively
                        String settlementXPath = "//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'settlement') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'settlement')])]";
                        java.util.List<WebElement> options = driver.findElements(By.xpath(settlementXPath));
                        
                        // Filter out invisible elements and elements that are just the page title
                        for (WebElement option : options) {
                            if (option.isDisplayed() && !option.getTagName().equalsIgnoreCase("title")) {
                                wait.until(ExpectedConditions.elementToBeClickable(option)).click();
                                return new SettlementPage(driver);
                            }
                        }
                        
                        // If not found, try pressing Escape to close any wrong menu opened
                        new org.openqa.selenium.interactions.Actions(driver).sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
                        Thread.sleep(500);
                    } catch (Exception e) {
                        // ignore and try next
                    }
                }
            } catch (Exception e) {}
        }
        
        throw new RuntimeException("Could not find or open the Action Menu with Settlement option.");
    }

    public ForeclosurePage clickForeclosureOption() {
        String[] menuLocators = {
            "//*[@aria-haspopup='menu']",
            "//*[@aria-label='Actions']",
            "//*[@aria-label='More']",
            "//*[@aria-label='more']",
            "(//*[normalize-space(.)='Active' or normalize-space(.)='ACTIVE' or normalize-space(.)='Overdue' or normalize-space(.)='OVERDUE']/following-sibling::*//button)[1]",
            "//button[.//svg]"
        };

        for (String locator : menuLocators) {
            try {
                java.util.List<WebElement> buttons = driver.findElements(By.xpath(locator));
                for (WebElement btn : buttons) {
                    try {
                        btn.click();
                        Thread.sleep(1000);

                        String foreclosureXPath = "//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'foreclosure') and not(.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'foreclosure')])]";
                        java.util.List<WebElement> options = driver.findElements(By.xpath(foreclosureXPath));

                        for (WebElement option : options) {
                            if (option.isDisplayed() && !option.getTagName().equalsIgnoreCase("title")) {
                                wait.until(ExpectedConditions.elementToBeClickable(option)).click();
                                return new ForeclosurePage(driver);
                            }
                        }

                        new org.openqa.selenium.interactions.Actions(driver).sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
                        Thread.sleep(500);
                    } catch (Exception e) {
                        // ignore and try next
                    }
                }
            } catch (Exception e) {}
        }

        throw new RuntimeException("Could not find or open the Action Menu with Foreclosure option.");
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    /** URL pattern: /lms/accounts/{numericId}?product=... */
    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.and(
                ExpectedConditions.urlMatches(".*/lms/accounts/\\d+.*"),
                ExpectedConditions.visibilityOf(borrowerName)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
