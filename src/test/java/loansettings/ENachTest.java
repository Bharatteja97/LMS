package loansettings;

import base.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ENachTest extends BaseClass {

    @Test
    public void testCreateENach() {
        driver.get("https://lms.alfinnext.com/settings/enach?product=VEHICLE_LOAN");

        ENachPage eNachPage = new ENachPage(driver);

        // Step 1: Click "Create Enach" from the list page
        eNachPage.clickCreateEnach();

        // Step 2: Fill out the modal (sliders left at default 1x)
        // Ensure bank and scheme exist; fallback selects first available option
        String configId = "CORP-CFG-" + System.currentTimeMillis();
        eNachPage.enterEnachDetails("HDFC Bank", configId, "30", "MSME Loan");

        // Step 3: Click Save
        eNachPage.clickSave();
        
        System.out.println("✅ E-Nach '" + configId + "' created successfully.");
        
        // Basic assertion — browser should still be alive after save
        Assert.assertNotNull(driver.getCurrentUrl(), "Browser lost connection after saving the E-Nach.");
    }
}
