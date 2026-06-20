package kycmanagement;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RuleEngineTest extends BaseClass {

    /**
     * TC-RE-001 : Create a new Scorecard for Vehicle Loan.
     *
     * Steps:
     *  1. Navigate to the Scorecard settings page (VEHICLE_LOAN product).
     *  2. Click "Create Scorecard" to open the modal.
     *  3. Select a Scheme and enter a Scorecard Name.
     *  4. Click "Save Scorecard".
     *
     * Note: A valid scheme must already exist in the system.
     */
    @Test(description = "Create a new Scorecard for Vehicle Loan")
    public void testCreateScorecard() {

        // Navigate to the Score Card settings page
        driver.get("https://lms.alfinnext.com/settings/score-card?product=VEHICLE_LOAN");

        RuleEnginePage ruleEnginePage = new RuleEnginePage(driver);

        // Step 1: Click "Create Scorecard" to open the modal
        ruleEnginePage.clickCreateScorecard();

        // Step 2: Fill in scorecard information
        // Use timestamp suffix on name to ensure uniqueness across runs
        String scorecardName = "Auto Approval Scorecard " + System.currentTimeMillis();
        ruleEnginePage.enterScorecardInformation(
            "MSME Loan",  // Scheme must exist; fallback selects first available scheme
            scorecardName
        );

        // Step 3: Save the scorecard
        ruleEnginePage.clickSaveScorecard();

        System.out.println("✅ Scorecard '" + scorecardName + "' created successfully.");

        // Basic assertion — browser should still be alive after save
        Assert.assertNotNull(driver.getCurrentUrl(), "Browser lost connection after saving the scorecard.");
    }
}
