package accounts;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String[] COLUMNS = {
        "Serial", "LAN", "Borrower Name", "Disbursed", "Outstanding", "EMI Date", "DPD", "Bucket", "Status"
    };

    // Filter dropdowns (native <select> elements)
    @FindBy(xpath = "//select[.//option[contains(.,'product type')]]")
    private WebElement propertyTypeSelect;

    @FindBy(xpath = "//select[.//option[contains(.,'loan status')]]")
    private WebElement statusSelect;

    // Parenthesised XPath picks Nth element in document order (not Nth sibling)
    @FindBy(xpath = "(//input[@type='date'])[1]")
    private WebElement loginFromDateInput;

    @FindBy(xpath = "(//input[@type='date'])[2]")
    private WebElement loginToDateInput;

    @FindBy(xpath = "//input[contains(@placeholder,'Search by the fields')]")
    private WebElement searchInput;

    @FindBy(xpath = "//button[contains(.,'Present Upcoming EMIs')]")
    private WebElement presentUpcomingEMIsButton;

    @FindBy(xpath = "//button[contains(.,'View Active Presentations')]")
    private WebElement viewActivePresentationsButton;

    // Data rows only (rows with at least one <td>)
    @FindBy(xpath = "//table/tbody/tr[td]")
    private List<WebElement> tableRows;

    public AccountsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // ── Wait helpers ──────────────────────────────────────────────────────────

    /**
     * Blocks until data rows with ≥3 cells appear.
     * Catches StaleElementReferenceException that can occur when the SPA
     * re-renders the table between the two findElements calls inside the lambda.
     */
    public void waitForTableToLoad() {
        wait.until(d -> {
            try {
                List<WebElement> rows = d.findElements(By.xpath("//table/tbody/tr[td]"));
                if (rows.isEmpty()) return false;
                List<WebElement> cells = rows.get(0).findElements(By.tagName("td"));
                return cells.size() >= 3;
            } catch (StaleElementReferenceException e) {
                return false; // DOM still updating — retry
            }
        });
    }

    // ── Filter methods ────────────────────────────────────────────────────────

    public void selectPropertyType(String visibleText) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(propertyTypeSelect));
        new Select(el).selectByVisibleText(visibleText);
    }

    public void selectStatus(String visibleText) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(statusSelect));
        new Select(el).selectByVisibleText(visibleText);
    }

    /** Returns the non-placeholder option texts from the Status dropdown (e.g. "ACTIVE", "OVERDUE"). */
    public List<String> getAvailableStatusOptions() {
        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(statusSelect)));
        return select.getOptions().stream()
            .map(WebElement::getText)
            .filter(t -> !t.isBlank() && !t.toLowerCase().contains("select"))
            .collect(Collectors.toList());
    }

    public void setLoginFromDate(String date) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(loginFromDateInput));
        el.clear();
        el.sendKeys(date);
    }

    public void setLoginToDate(String date) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(loginToDateInput));
        el.clear();
        el.sendKeys(date);
    }

    /**
     * Types keyword into the search field and waits 2 s for the SPA debounce
     * to apply. Enter is also sent as some implementations require it.
     */
    public void searchByKeyword(String keyword) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        el.clear();
        el.sendKeys(keyword);
        el.sendKeys(Keys.RETURN);
        pause(2000);
    }

    public void clearAllFilters() {
        new Select(wait.until(ExpectedConditions.elementToBeClickable(propertyTypeSelect))).selectByIndex(0);
        new Select(wait.until(ExpectedConditions.elementToBeClickable(statusSelect))).selectByIndex(0);
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        el.clear();
        el.sendKeys(Keys.RETURN);
    }

    // ── Button methods ────────────────────────────────────────────────────────

    public void clickPresentUpcomingEMIs() {
        wait.until(ExpectedConditions.elementToBeClickable(presentUpcomingEMIsButton)).click();
    }

    public void clickViewActivePresentations() {
        wait.until(ExpectedConditions.elementToBeClickable(viewActivePresentationsButton)).click();
    }

    // ── Table read methods ────────────────────────────────────────────────────

    public int getTableRowCount() {
        waitForTableToLoad();
        return tableRows.size();
    }

    public String getCellValue(int rowIndex, int colIndex) {
        waitForTableToLoad();
        List<WebElement> cells = tableRows.get(rowIndex).findElements(By.tagName("td"));
        return cells.get(colIndex).getText().trim();
    }

    public String getLAN(int rowIndex)             { return getCellValue(rowIndex, 1); }
    public String getBorrowerName(int rowIndex)    { return getCellValue(rowIndex, 2); }
    public String getDisbursedAmount(int rowIndex) { return getCellValue(rowIndex, 3); }
    public String getOutstandingAmount(int rowIndex){ return getCellValue(rowIndex, 4); }
    public String getEMIDate(int rowIndex)         { return getCellValue(rowIndex, 5); }
    public String getDPD(int rowIndex)             { return getCellValue(rowIndex, 6); }
    public String getBucket(int rowIndex)          { return getCellValue(rowIndex, 7); }
    public String getStatus(int rowIndex)          { return getCellValue(rowIndex, 8); }

    public List<Map<String, String>> getAllTableData() {
        waitForTableToLoad();
        List<Map<String, String>> data = new ArrayList<>();
        for (WebElement row : tableRows) {
            try {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() < 3) continue;
                Map<String, String> rowData = new HashMap<>();
                for (int i = 0; i < Math.min(cells.size(), COLUMNS.length); i++) {
                    rowData.put(COLUMNS[i], cells.get(i).getText().trim());
                }
                data.add(rowData);
            } catch (StaleElementReferenceException e) {
                // Row disappeared mid-iteration; skip it
            }
        }
        return data;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    /**
     * Clicks a LAN by finding the table cell that contains its text.
     * LAN cells on this app use custom elements (not plain <a> tags),
     * so we target the <td> directly.
     */
    public AccountDetailsPage clickLANLink(String lanNumber) {
        WebElement cell = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//table/tbody/tr/td[contains(normalize-space(.),'" + lanNumber + "')]")));
        cell.click();
        return new AccountDetailsPage(driver);
    }

    public AccountDetailsPage clickLANByRow(int rowIndex) {
        waitForTableToLoad();
        WebElement cell = wait.until(ExpectedConditions.elementToBeClickable(
            tableRows.get(rowIndex).findElement(By.xpath(".//td[2]"))));
        cell.click();
        return new AccountDetailsPage(driver);
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    /** Checks for the LAN in table cells (not restricted to <a> tags). */
    public boolean isLANPresent(String lanNumber) {
        return !driver.findElements(
            By.xpath("//table/tbody/tr/td[contains(normalize-space(.),'" + lanNumber + "')]")).isEmpty();
    }

    public boolean isTableLoaded() {
        try {
            waitForTableToLoad();
            return !tableRows.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the current data-row count immediately without waiting.
     * Use this after applying a filter that may legitimately return 0 rows,
     * where waitForTableToLoad() would otherwise time out.
     */
    public int getDataRowCountNoWait() {
        // [td[3]] means the row has at least 3 <td> elements — same guard as waitForTableToLoad(),
        // so a "no data" colspan row (which has only 1 <td>) is not counted.
        return driver.findElements(By.xpath("//table/tbody/tr[td[3]]")).size();
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
