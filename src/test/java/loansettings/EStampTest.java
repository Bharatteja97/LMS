package loansettings;

import base.BaseClass;
import org.testng.annotations.Test;

public class EStampTest extends BaseClass {

    @Test
    public void testCreateEStamp() {
        driver.get("https://lms.alfinnext.com/settings/estamp?product=VEHICLE_LOAN");

        EStampPage eStampPage = new EStampPage(driver);
        
        // Step 1: Click "Add E-stamp" from the list page
        eStampPage.clickAddEStamp();
        
        // Step 2: Fill out the modal
        // Note: Ensure the state exists in the dropdown
        eStampPage.enterEStampDetails("Maharashtra", "MH-Auto-Stamp", "500", "100");
        
        // Step 3: Click Save
        eStampPage.clickSave();
    }
}
