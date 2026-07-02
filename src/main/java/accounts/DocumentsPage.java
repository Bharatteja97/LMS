package accounts;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the <b>Documents</b> tab of the Account Details page.
 *
 * <p>URL pattern:
 * {@code /lms/accounts/{id}?tab=documents&amp;product=VEHICLE_LOAN}
 *
 * <p>Covers:
 * <ul>
 *   <li>Borrower Document section (Image, Pan, Aadhar Front, Aadhaar Back, …)</li>
 *   <li>Vehicle Document section (RC Book, Insurance, …)</li>
 *   <li>Co-Borrower / Guarantor document sections (if present)</li>
 *   <li>Upload badge verification ("+N uploaded" / "Not uploaded")</li>
 *   <li>Add document interaction</li>
 *   <li>Uploaded-Files panel and "Click to preview" interaction</li>
 *   <li>Full-page scroll utility to reveal lazily-rendered sections</li>
 * </ul>
 */
public class DocumentsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    // ── URL / Tab navigation ──────────────────────────────────────────────────

    private static final String DOCUMENTS_TAB_URL =
        "https://lms.alfinnext.com/lms/accounts/212?tab=documents&product=VEHICLE_LOAN";

    // ── Tab button ────────────────────────────────────────────────────────────

    /**
     * "Documents" tab button in the top navigation bar.
     * Matches both the active and inactive states.
     */
    @FindBy(xpath = "//button[normalize-space(.)='Documents'] | //a[normalize-space(.)='Documents']")
    private WebElement documentsTabButton;

    // ── Section headings ──────────────────────────────────────────────────────

    /** "Borrower Document" section heading */
    @FindBy(xpath = "(//*[normalize-space(.)='Borrower Document'"
                  + " and not(.//*[normalize-space(.)='Borrower Document'])])[1]")
    private WebElement borrowerDocumentHeading;

    /** "Vehicle Document" section heading */
    @FindBy(xpath = "(//*[normalize-space(.)='Vehicle Document'"
                  + " and not(.//*[normalize-space(.)='Vehicle Document'])])[1]")
    private WebElement vehicleDocumentHeading;

    // ── All document cards (generic) ──────────────────────────────────────────

    /**
     * Every document card block on the page.
     * A card is identified by containing a Document-Number row plus an upload badge.
     */
    @FindBy(xpath = "//div[.//*[contains(normalize-space(.),'Document Number')]"
                  + " and .//*[contains(normalize-space(.),'uploaded')"
                  + "          or contains(normalize-space(.),'Not uploaded')]]")
    private List<WebElement> allDocumentCards;

    // ── Borrower Document individual card titles ──────────────────────────────

    /** Borrower Image card title */
    @FindBy(xpath = "(//*[normalize-space(.)='Borrower Image'"
                  + " and not(.//*[normalize-space(.)='Borrower Image'])])[1]")
    private WebElement borrowerImageTitle;

    /** Borrower Pan card title */
    @FindBy(xpath = "(//*[normalize-space(.)='Borrower Pan'"
                  + " and not(.//*[normalize-space(.)='Borrower Pan'])])[1]")
    private WebElement borrowerPanTitle;

    /** Borrower Aadhar Front card title */
    @FindBy(xpath = "(//*[contains(normalize-space(.),'Aadhar Front')"
                  + " or contains(normalize-space(.),'Aadhaar Front')])[1]")
    private WebElement borrowerAadharFrontTitle;

    /** Borrower Aadhaar Back card title */
    @FindBy(xpath = "(//*[contains(normalize-space(.),'Aadhaar Back')"
                  + " or contains(normalize-space(.),'Aadhar Back')])[1]")
    private WebElement borrowerAadharBackTitle;

    // ── All "Add" buttons ─────────────────────────────────────────────────────

    /**
     * Every "Add" button / link across all document cards on the page.
     */
    @FindBy(xpath = "//button[normalize-space(.)='Add']"
                  + " | //a[normalize-space(.)='Add']"
                  + " | //*[@role='button' and normalize-space(.)='Add']")
    private List<WebElement> allAddButtons;

    // ── Uploaded Files panels ─────────────────────────────────────────────────

    /**
     * All "Uploaded Files" section headers on the page (one per card that has uploads).
     */
    @FindBy(xpath = "//*[normalize-space(.)='Uploaded Files'"
                  + " and not(.//*[normalize-space(.)='Uploaded Files'])]")
    private List<WebElement> uploadedFilesPanels;

    /**
     * All "Click to preview" items across every Uploaded Files panel.
     */
    @FindBy(xpath = "//*[contains(normalize-space(.),'Click to preview')]")
    private List<WebElement> clickToPreviewItems;

    // ── Upload count badges ───────────────────────────────────────────────────

    /**
     * All upload-count badges on the page, e.g. "+1 uploaded", "+2 uploaded".
     */
    @FindBy(xpath = "//*[contains(normalize-space(.),'uploaded')"
                  + " and not(.//*[contains(normalize-space(.),'uploaded')])]")
    private List<WebElement> uploadBadges;

    /**
     * All "Not uploaded" indicators on the page.
     */
    @FindBy(xpath = "//*[contains(normalize-space(.),'Not uploaded')"
                  + " and not(.//*[contains(normalize-space(.),'Not uploaded')])]")
    private List<WebElement> notUploadedBadges;

    // ── Document Number rows ──────────────────────────────────────────────────

    /**
     * All "Document Number:" label rows (one per card).
     */
    @FindBy(xpath = "//*[contains(normalize-space(.),'Document Number')]")
    private List<WebElement> documentNumberLabels;

    // ── File-count labels ─────────────────────────────────────────────────────

    /**
     * All "N file(s)" labels inside Uploaded Files panels.
     */
    @FindBy(xpath = "//*[contains(normalize-space(.),'file(s)')"
                  + " and not(.//*[contains(normalize-space(.),'file(s)')])]")
    private List<WebElement> fileCountLabels;

    // ─────────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────────

    public DocumentsPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Navigation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Navigates directly to the Documents tab for account 212 and waits for load.
     */
    public DocumentsPage navigateToDocumentsTab() {
        driver.get(DOCUMENTS_TAB_URL);
        waitForPageLoad();
        return this;
    }

    /**
     * Clicks the "Documents" tab button from within the account detail page.
     */
    public DocumentsPage clickDocumentsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(documentsTabButton)).click();
        waitForPageLoad();
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Page-load check
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} when the "Borrower Document" heading is visible.
     */
    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOf(borrowerDocumentHeading));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Blocks until the Borrower Document heading is visible.
     */
    public void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOf(borrowerDocumentHeading));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Scroll utilities
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Scrolls the page to the very bottom using a progressive scroll so that
     * lazily-rendered sections (Vehicle Document, Co-Borrower, etc.) are loaded.
     * Pauses 400 ms per step to allow React re-renders.
     */
    public DocumentsPage scrollToBottom() {
        long lastHeight = getLong("return document.body.scrollHeight");
        while (true) {
            js.executeScript("window.scrollBy(0, window.innerHeight)");
            pause(400);
            long newHeight = getLong("return document.body.scrollHeight");
            long scrollTop = getLong("return window.pageYOffset + window.innerHeight");
            if (scrollTop >= newHeight || newHeight == lastHeight) {
                break;
            }
            lastHeight = newHeight;
        }
        return this;
    }

    /**
     * Scrolls the page back to the very top.
     */
    public DocumentsPage scrollToTop() {
        js.executeScript("window.scrollTo(0, 0)");
        pause(300);
        return this;
    }

    /**
     * Scrolls the named document card title smoothly into the centre of the viewport.
     *
     * @param cardTitleText exact card title, e.g. {@code "Borrower Image"}
     */
    public DocumentsPage scrollToCard(String cardTitleText) {
        WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("(//*[normalize-space(.)='" + cardTitleText
                   + "' and not(.//*[normalize-space(.)='" + cardTitleText + "'])])[1]")));
        js.executeScript(
            "arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", title);
        pause(500);
        return this;
    }

    /**
     * Scrolls the "Vehicle Document" section heading into view.
     */
    public DocumentsPage scrollToVehicleDocumentSection() {
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("(//*[normalize-space(.)='Vehicle Document'])[1]")));
        js.executeScript(
            "arguments[0].scrollIntoView({behavior:'smooth', block:'start'});",
            vehicleDocumentHeading);
        pause(500);
        return this;
    }

    /**
     * Scrolls the "Borrower Document" section heading into view.
     */
    public DocumentsPage scrollToBorrowerDocumentSection() {
        js.executeScript(
            "arguments[0].scrollIntoView({behavior:'smooth', block:'start'});",
            borrowerDocumentHeading);
        pause(500);
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Section heading assertions
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns {@code true} if the "Borrower Document" section heading is visible. */
    public boolean isBorrowerDocumentSectionVisible() {
        try {
            return wait.until(
                ExpectedConditions.visibilityOf(borrowerDocumentHeading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns {@code true} if the "Vehicle Document" section heading is visible.
     * Scrolls to the bottom first to ensure the section has rendered.
     */
    public boolean isVehicleDocumentSectionVisible() {
        try {
            scrollToBottom();
            return wait.until(
                ExpectedConditions.visibilityOf(vehicleDocumentHeading)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Document card presence checks
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if a document card with the given title is visible.
     *
     * @param cardTitle e.g. {@code "Borrower Image"}, {@code "RC Book"}
     */
    public boolean isDocumentCardVisible(String cardTitle) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//*[normalize-space(.)='" + cardTitle
                       + "' and not(.//*[normalize-space(.)='" + cardTitle + "'])])[1]")));
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the total number of document cards rendered on the page.
     * Call {@link #scrollToBottom()} first to include all sections.
     */
    public int getTotalDocumentCardCount() {
        return allDocumentCards.size();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Upload badge / status
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the upload-badge text for a named document card, e.g.
     * {@code "+1 uploaded"} or {@code "Not uploaded"}.
     *
     * @param cardTitle e.g. {@code "Borrower Image"}
     * @return badge text, or empty string if not found
     */
    public String getUploadBadgeText(String cardTitle) {
        try {
            WebElement badge = driver.findElement(By.xpath(
                "(//*[normalize-space(.)='" + cardTitle + "']"
              + "/ancestor::*[position()<=5]"
              + "//*[contains(normalize-space(.),'uploaded')"
              + "   or contains(normalize-space(.),'Not uploaded')])[1]"));
            return badge.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Returns {@code true} if the named card shows at least one uploaded file.
     *
     * @param cardTitle e.g. {@code "Borrower Pan"}
     */
    public boolean isDocumentUploaded(String cardTitle) {
        String badge = getUploadBadgeText(cardTitle);
        return badge.toLowerCase().contains("uploaded")
            && !badge.toLowerCase().contains("not");
    }

    /**
     * Parses the "+N uploaded" badge and returns N. Returns 0 if none / not found.
     *
     * @param cardTitle e.g. {@code "Borrower Pan"}
     */
    public int getUploadedFileCount(String cardTitle) {
        String badge = getUploadBadgeText(cardTitle);
        try {
            String numeric = badge.replaceAll("[^0-9]", "").trim();
            return numeric.isEmpty() ? 0 : Integer.parseInt(numeric);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Returns the count of cards across the whole page that have at least one upload.
     * Scrolls to the bottom first.
     */
    public int getTotalUploadedBadgesCount() {
        scrollToBottom();
        return (int) uploadBadges.stream()
            .filter(b -> !b.getText().toLowerCase().contains("not"))
            .count();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Document Number
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the document-number value for a named card,
     * e.g. {@code "Not assigned"} or an actual number string.
     *
     * @param cardTitle e.g. {@code "Borrower Image"}
     */
    public String getDocumentNumber(String cardTitle) {
        try {
            WebElement numRow = driver.findElement(By.xpath(
                "(//*[normalize-space(.)='" + cardTitle + "']"
              + "/ancestor::*[position()<=5]"
              + "//*[contains(normalize-space(.),'Document Number')])[1]"));
            String full = numRow.getText().trim(); // "Document Number:  Not assigned"
            int colon = full.indexOf(':');
            return colon >= 0 ? full.substring(colon + 1).trim() : full;
        } catch (Exception e) {
            return "";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Uploaded Files panel
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the file-count label inside the Uploaded Files panel for a card,
     * e.g. {@code "1 file(s)"}.
     *
     * @param cardTitle e.g. {@code "Borrower Image"}
     */
    public String getUploadedFilesCount(String cardTitle) {
        try {
            WebElement label = driver.findElement(By.xpath(
                "(//*[normalize-space(.)='" + cardTitle + "']"
              + "/ancestor::*[position()<=8]"
              + "//*[contains(normalize-space(.),'file(s)')])[1]"));
            return label.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Returns {@code true} if the "Uploaded Files" panel is present for the card.
     *
     * @param cardTitle e.g. {@code "Borrower Pan"}
     */
    public boolean isUploadedFilesPanelVisible(String cardTitle) {
        try {
            driver.findElement(By.xpath(
                "(//*[normalize-space(.)='" + cardTitle + "']"
              + "/ancestor::*[position()<=8]"
              + "//*[normalize-space(.)='Uploaded Files'])[1]"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the total count of "Click to preview" items visible on the page.
     * Call {@link #scrollToBottom()} first for an accurate count.
     */
    public int getTotalPreviewItemsCount() {
        return clickToPreviewItems.size();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Add button
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the count of "Add" buttons visible on the page.
     * Call {@link #scrollToBottom()} first for an accurate count.
     */
    public int getAllAddButtonCount() {
        return allAddButtons.size();
    }

    /**
     * Clicks the "Add" button for the given document card.
     * Scrolls the card into view before clicking.
     *
     * @param cardTitle e.g. {@code "Borrower Image"}
     */
    public DocumentsPage clickAddButton(String cardTitle) {
        scrollToCard(cardTitle);
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//*[normalize-space(.)='" + cardTitle + "']"
                   + "/ancestor::*[position()<=8]"
                   + "//button[normalize-space(.)='Add']"
                   + " | //*[normalize-space(.)='" + cardTitle + "']"
                   + "/ancestor::*[position()<=8]"
                   + "//a[normalize-space(.)='Add'])[1]")));
        try {
            addBtn.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", addBtn);
        }
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Preview / Click-to-preview
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Clicks the first "Click to preview" item inside the given card's
     * Uploaded Files panel.
     *
     * @param cardTitle e.g. {@code "Borrower Image"}
     */
    public DocumentsPage clickPreviewDocument(String cardTitle) {
        scrollToCard(cardTitle);
        WebElement previewItem = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//*[normalize-space(.)='" + cardTitle + "']"
                   + "/ancestor::*[position()<=10]"
                   + "//*[contains(normalize-space(.),'Click to preview')])[1]")));
        try {
            previewItem.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", previewItem);
        }
        return this;
    }

    /**
     * Returns the label of the first document item (e.g. "Document 1") inside
     * the Uploaded Files panel for the given card.
     *
     * @param cardTitle e.g. {@code "Borrower Image"}
     */
    public String getFirstDocumentItemLabel(String cardTitle) {
        try {
            // Second match: first match is the card title itself
            WebElement label = driver.findElement(By.xpath(
                "(//*[normalize-space(.)='" + cardTitle + "']"
              + "/ancestor::*[position()<=10]"
              + "//*[starts-with(normalize-space(.),'Document ')"
              + "   and not(contains(normalize-space(.),'Number'))])[2]"));
            return label.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mandatory / Required indicator
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the named card is marked as mandatory
     * (red asterisk or "required" CSS class next to the title).
     *
     * @param cardTitle e.g. {@code "Borrower Image"}
     */
    public boolean isDocumentMandatory(String cardTitle) {
        try {
            driver.findElement(By.xpath(
                "(//*[normalize-space(.)='" + cardTitle + "']"
              + "/parent::*"
              + "//*[normalize-space(.)='*'"
              + "   or contains(@class,'required')"
              + "   or contains(@class,'mandatory')])[1]"
              + " | (//*[normalize-space(.)='" + cardTitle + "']"
              + "/following-sibling::*[normalize-space(.)='*'])[1]"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Borrower Document card — individual visibility helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns {@code true} if the "Borrower Image" card title is visible. */
    public boolean isBorrowerImageVisible() {
        try { return wait.until(ExpectedConditions.visibilityOf(borrowerImageTitle)).isDisplayed(); }
        catch (Exception e) { return false; }
    }

    /** Returns {@code true} if the "Borrower Pan" card title is visible. */
    public boolean isBorrowerPanVisible() {
        try { return wait.until(ExpectedConditions.visibilityOf(borrowerPanTitle)).isDisplayed(); }
        catch (Exception e) { return false; }
    }

    /** Returns {@code true} if the "Borrower Aadhar Front" card title is visible. */
    public boolean isBorrowerAadharFrontVisible() {
        try { return wait.until(ExpectedConditions.visibilityOf(borrowerAadharFrontTitle)).isDisplayed(); }
        catch (Exception e) { return false; }
    }

    /** Returns {@code true} if the "Borrower Aadhaar Back" card title is visible. */
    public boolean isBorrowerAadharBackVisible() {
        try { return wait.until(ExpectedConditions.visibilityOf(borrowerAadharBackTitle)).isDisplayed(); }
        catch (Exception e) { return false; }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Bulk helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Scrolls to the bottom and returns the visible text of all document-card
     * title elements on the page.
     *
     * @return list of card title strings
     */
    public List<String> getAllDocumentCardTitles() {
        scrollToBottom();
        List<WebElement> titles = driver.findElements(By.xpath(
            "//*[contains(@class,'font-medium') or contains(@class,'font-semibold')]"
          + "[not(normalize-space(.)='Uploaded Files')]"
          + "[not(contains(normalize-space(.),'Document Number'))]"
          + "[not(.//*[contains(normalize-space(.),'uploaded')])]"
          + "[normalize-space(.)!='']"));
        return titles.stream()
                     .map(e -> e.getText().trim())
                     .filter(t -> !t.isEmpty())
                     .collect(Collectors.toList());
    }

    /**
     * Scrolls to the bottom and returns the text of every upload-badge element
     * visible on the page.
     *
     * @return list of badge texts, e.g. {@code ["+1 uploaded", "+1 uploaded", …]}
     */
    public List<String> getAllUploadBadgeTexts() {
        scrollToBottom();
        return uploadBadges.stream()
                           .map(e -> e.getText().trim())
                           .filter(t -> !t.isEmpty())
                           .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────────────

    private long getLong(String script) {
        Object result = js.executeScript(script);
        if (result instanceof Long) return (Long) result;
        if (result instanceof Number) return ((Number) result).longValue();
        return 0L;
    }

    private void pause(long millis) {
        try { Thread.sleep(millis); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
