package kycmanagement;

import base.BaseClass;
import org.testng.annotations.Test;

public class RuleEngineTest extends BaseClass {

    @Test
    public void testCreateScorecard() {
        driver.get("https://lms.alfinnext.com/settings/score-card?product=VEHICLE_LOAN");

        RuleEnginePage ruleEnginePage = new RuleEnginePage(driver);
        
        // Step 1: Click create scorecard
        ruleEnginePage.clickCreateScorecard();
        
        // Step 2: Scorecard information
        // (Ensure "Premium Auto Scheme" or the scheme you want exists)
        ruleEnginePage.enterScorecardInformation("Premium Auto Scheme", "Auto Approval Scorecard");
        
        // Step 3: Save Scorecard
        ruleEnginePage.clickSaveScorecard();
    }
}
