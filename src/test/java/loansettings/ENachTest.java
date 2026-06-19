package loansettings;

import base.BaseClass;
import org.testng.annotations.Test;

public class ENachTest extends BaseClass {

    @Test
    public void testCreateENach() {
        driver.get("https://lms.alfinnext.com/settings/enach?product=VEHICLE_LOAN");

        ENachPage eNachPage = new ENachPage(driver);

        // Step 1: Click "Create Enach" from the list page
        eNachPage.clickCreateEnach();

        // Step 2: Fill out the modal (sliders left at default 1x)
        // Ensure "HDFC Bank" and "Premium Auto Scheme" exist in your environment
        eNachPage.enterEnachDetails("HDFC Bank", "CORP-CFG-102", "30", "Premium Auto Scheme");

        // Step 3: Click Save
        eNachPage.clickSave();
    }
}
