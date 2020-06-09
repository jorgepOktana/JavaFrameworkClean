package Utils;

import org.apache.commons.io.FileUtils;
import org.testng.IExecutionListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.File;

/**
 * @author smartData
 * <h1>Suite Events</h1>
 * <p>Purpose: This class is used to handle Events when executing suite</p>
 */

public class SuiteEvents implements ISuiteListener, IExecutionListener {

    @Override
    public void onStart(ISuite iSuite) {
        Log.info("Test Suite started:" + iSuite.getName());
    }

    @Override
    public void onFinish(ISuite iSuite) {
        Log.info("Test Suite finished:" + iSuite.getName());
    }

    @Override
    public void onExecutionStart() {
        //Remove All previous screenshots
        try {
            //Common.KillChromeDriverProcessForWindows();
            ExcelUtil.GetBrowser();
            if(Common.GetUserData("EnableCoverage").equalsIgnoreCase("Yes"))
            {
                Log.info("Coverage is enabled");
                ExcelUtil.GetTestCoverageData();
            }else {
                Log.info("Coverage is disabled");
            }


            //Set clean up as NO. Whenever new suite is started set clean up NO.

            File ScreenShotFolder = new File(System.getProperty("user.dir") + "/ExecutionReports/HtmlReport/Screenshots");
            if (FileUtils.getFile(ScreenShotFolder).exists())
                FileUtils.cleanDirectory(ScreenShotFolder);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        }

    @Override
    public void onExecutionFinish() {
        Log.info("All suites has finished");
        Log.info("Clean up process running...");
        ExecuteBatchMessageProcessor.CleanUpOrgs();
        Log.info("Clean up process finished...");
    }
}
