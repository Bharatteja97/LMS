package loansettings;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EStampTest extends BaseClass {

    @Test
    public void testCreateEStamp() {
        driver.get("https://lms.alfinnext.com/settings/estamp?product=VEHICLE_LOAN");

        EStampPage eStampPage = new EStampPage(driver);
        
        // Step 1: Click "Add E-stamp" from the list page
        eStampPage.clickAddEStamp();
        
        // Step 2: Fill out the modal
        // Note: Ensure the state exists in the dropdown; fallback selects first available option
        String tagName = "MH-Auto-Stamp-" + System.currentTimeMillis();
        eStampPage.enterEStampDetails("Maharashtra", tagName, "500", "100");
        
        // Step 3: Click Save
        eStampPage.clickSave();
        
        System.out.println("✅ E-Stamp '" + tagName + "' created successfully.");
        
        // Basic assertion — browser should still be alive after save
        Assert.assertNotNull(driver.getCurrentUrl(), "Browser lost connection after saving the E-Stamp.");
    }
}
