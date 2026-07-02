# ProductAccounts - Test Script Report
Generated: 2026-07-01 18:43  |  Suite: testng_e2e.xml

---

## Project Overview

| Item | Detail |
|------|--------|
| Project | ProductAccounts |
| Test Framework | TestNG + Selenium |
| Suite File | testng_e2e.xml |
| Total Test Classes | 9 (8 active in suite + 1 not in suite) |
| Total @Test Methods | 65 |
| Methods Active in Suite | 63 |

---

## Test Classes Summary

| # | Test File | @Test Methods | In Suite |
|---|-----------|:---:|:---:|
| 1 | AccountsTest.java | 16 | YES |
| 2 | AccountsDetailsTest.java | 19 | YES |
| 3 | PaymentTest.java | 14 | YES |
| 4 | SettlementTest.java | 2 | YES |
| 5 | ForeclosureTest.java | 5 | YES |
| 6 | BalanceTransferTest.java | 5 | YES |
| 7 | LedgerTest.java | 1 | YES |
| 8 | ScheduleTest.java | 1 | YES |
| 9 | ChargesTest.java | 1 | NO |

TOTAL: 65 @Test methods (63 active in suite, 2 not in suite)

---

## 1. AccountsTest.java - 16 Test Methods
Suite Group: Accounts List Tests

| # | Method Name |
|---|-------------|
| 1 | navigateToAccountsPage |
| 2 | testTableLoadsWithData |
| 3 | testSearchByBorrowerName |
| 4 | testSearchByLAN |
| 5 | testSearchInputNonExistentText |
| 6 | testAvailableStatusOptions |
| 7 | testFilterByActiveStatus |
| 8 | testFilterByOverdueStatus |
| 9 | testFilterBySettledStatus |
| 10 | testLoginDateFilter |
| 11 | testCombinedStatusAndSearchFilter |
| 12 | testTableDataIntegrity |
| 13 | testSerialNumberSequence |
| 14 | testClickLANNavigatesToDetailPage |
| 15 | testClickFirstRowLAN |
| 16 | testPresentUpcomingEMIsButton |
| 17 | testViewActivePresentationsButton |

---

## 2. AccountsDetailsTest.java - 19 Test Methods
Suite Group: Account Details Tests

| # | Method Name |
|---|-------------|
| 1 | captureDetailPageUrl |
| 2 | navigateToDetailPage |
| 3 | testPageLoadsSuccessfully |
| 4 | testUrlContainsAccountId |
| 5 | testBorrowerNameIsVisible |
| 6 | testLANNumberIsVisible |
| 7 | testLANTextContainsPrefix |
| 8 | testStatusBadgeIsVisible |
| 9 | testANTextIsVisible |
| 10 | testProductTypeIsVisible |
| 11 | testDisbursedOnDateIsVisible |
| 12 | testLoanAmountIsDisplayed |
| 13 | testOutstandingIsDisplayed |
| 14 | testCurrentDPDIsDisplayed |
| 15 | testNextEMIDueIsDisplayed |
| 16 | testCurrentLiabilityIsDisplayed |
| 17 | testDisbursedAmountCardIsDisplayed |
| 18 | testNetOutstandingBalanceCardIsDisplayed |
| 19 | testPaidAmountCardIsDisplayed |
| 20 | testOverdueAmountCardIsDisplayed |
| 21 | testAllTabsOneByOne |

---

## 3. PaymentTest.java - 14 Test Methods
Suite Group: Payment Tests

| # | Method Name |
|---|-------------|
| 1 | setupPaymentTab |
| 2 | navigateToPaymentTab |
| 3 | testPaymentTabLoads |
| 4 | testPaymentHistorySubTabVisible |
| 5 | testStagedRepaymentsSubTabVisible |
| 6 | testStagedRepaymentRowCount |
| 7 | testPaymentHistoryRowCount |
| 8 | testPaymentHistoryFirstRowData |
| 9 | testPayButtonOpensModal |
| 10 | testModalTitleIsCorrect |
| 11 | testModalShowsScheduledEMI |
| 12 | testModalShowsRemainingDue |
| 13 | testModalShowsMaxPaymentLimit |
| 14 | testModalCancelClosesModal |
| 15 | testModalCloseButtonDismissesModal |
| 16 | testMakePaymentWithScheduledEMIAmount |

---

## 4. SettlementTest.java - 2 Test Methods
Suite Group: Settlement Tests

| # | Method Name |
|---|-------------|
| 1 | captureDetailPageUrl |
| 2 | navigateToDetailPage |
| 3 | testOpenSettlementModal |
| 4 | testCompleteSettlementFlow |

---

## 5. ForeclosureTest.java - 5 Test Methods
Suite Group: Foreclosure Tests

| # | Method Name |
|---|-------------|
| 1 | navigateToAccount |
| 2 | resetToDetailPage |
| 3 | testOpenForeclosureModal |
| 4 | testLoanPayoffSummaryOrHistoryFields |
| 5 | testGenerateForeclosureQuote |
| 6 | testForecloseButtonOpensPaymentForm |
| 7 | testForeclosureFullFlow |

---

## 6. BalanceTransferTest.java - 5 Test Methods
Suite Group: Balance Transfer Tests

| # | Method Name |
|---|-------------|
| 1 | openBalanceTransferTab |
| 2 | resetTab |
| 3 | testBalanceTransferTabLoads |
| 4 | testInfoNoticeText |
| 5 | testFormFieldsAcceptInput |
| 6 | testResetClearsForm |
| 7 | testConfirmTransferFullFlow |

---

## 7. LedgerTest.java - 1 Test Method
Suite Group: Ledger Tests

| # | Method Name |
|---|-------------|
| 1 | navigateToLedgerTab |
| 2 | testExportLedger |

---

## 8. ScheduleTest.java - 1 Test Method
Suite Group: Schedule Tests

| # | Method Name |
|---|-------------|
| 1 | navigateToScheduleTab |
| 2 | testPresentEMI |

---

## 9. ChargesTest.java - 1 Test Method (NOT IN SUITE)
WARNING: This class is NOT included in testng_e2e.xml

| # | Method Name |
|---|-------------|
| 1 | navigateToChargesTab |
| 2 | testWaiveCharge |

---

## TestNG Suite Execution Order (testng_e2e.xml)

Suite: ProductAccounts E2E Suite (parallel=none)
[1] Accounts List Tests       -> AccountsTest.java        (16 methods)
[2] Account Details Tests     -> AccountsDetailsTest.java (19 methods)
[3] Payment Tests             -> PaymentTest.java         (14 methods)
[4] Settlement Tests          -> SettlementTest.java      ( 2 methods)
[5] Foreclosure Tests         -> ForeclosureTest.java     ( 5 methods)
[6] Balance Transfer Tests    -> BalanceTransferTest.java ( 5 methods)
[7] Ledger Tests              -> LedgerTest.java          ( 1 method)
[8] Schedule Tests            -> ScheduleTest.java        ( 1 method)

Total in Suite : 63 @Test methods
Not in Suite   :  2 @Test methods (ChargesTest.java)
Grand Total    : 65 @Test methods

---

## Page Object Model Classes (src/main/java/accounts/)

| Class | Size |
|-------|------|
| AccountDetailsPage.java | 20 KB |
| AccountsPage.java | 10 KB |
| BalanceTransferPage.java | 6 KB |
| ChargesPage.java | 3 KB |
| DocumentsPage.java | 0.3 KB |
| ForeclosurePage.java | 14 KB |
| LedgerPage.java | 1.2 KB |
| PaymentPage.java | 36 KB |
| SchedulePage.java | 2.1 KB |
| SettlementPage.java | 3.4 KB |
| TransactionPage.java | 0.3 KB |
| LoginPage.java | 0.3 KB |
| DashboardPage.java | 0.3 KB |

---

## Utility & Base Classes

| Class | Purpose |
|-------|---------|
| BaseClass.java | Test setup and teardown |
| ConfigReader.java | Read config.properties |
| ExcelUtils.java | Excel data utilities |
| ScreenshotUtils.java | Screenshot capture on failure |
| WaitUtils.java | Explicit and implicit waits |
| WebDriverUtils.java | WebDriver helper methods |

---
Report for: ProductAccounts Selenium + TestNG Automation Suite
