package Utils;

import com.relevantcodes.extentreports.LogStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.io.IOException;

import static Utils.Common.GetUserData;
import static Utils.ReportUtil.getTest;

@Listeners({TestEvents.class, SuiteEvents.class})

public class TestBase {

    protected static String Requestor = "Requestor_Admin";
    protected static String Responder = "Responder_Admin";
    protected static String Laboratory = "Laboratory_Admin";
    private static Logger log = LogManager.getLogger(TestBase.class);

    /**
     * <h1>Log test step info in html report</h1>
     * @param step
     */
    public static void logTestStep(String step) {
        log.info(step);
        getTest().log(LogStatus.INFO, step);
    }

    /**
     * <h1>Log test step pass in html report</h1>
     * @param step
     */
    public static void logTestStepPass(String step) {
        log.info("Test Step PASS - " + step);
        String PassLabel = "<span style='border:1px solid green;border-radius: 5px; padding:1px;background:green;color:white;margin-right:2px;'>PASS</span>";
        String html = PassLabel + "<span style='color:green;'>" + step + "</span>";
        getTest().log(LogStatus.PASS, html);
    }

    /**
     * <h1>Log test step fail in html report</h1>
    * @param step
     */
    public static void logTestStepFail(String step) {
        log.info("Test Step FAIL - " + step);
        String FailLabel = "<span style='border:1px solid red;border-radius: 5px; padding:1px;background:red;color:white;margin-right:2px;'>FAIL</span>";
        String html = FailLabel + "<span style='color:red;'>" + step + "</span>";
        getTest().log(LogStatus.FAIL, html);
    }

    public static void logTestStepWarn(String step) {
        log.warn("Test Step WARN - " + step);
        String FailLabel = "<span style='border:1px solid red;border-radius: 5px; padding:1px;background:yellow;color:red;margin-right:2px;'>WARN</span>";
        String html = FailLabel + "<span style='color:red;'>" + step + "</span>";
        getTest().log(LogStatus.INFO, html);
    }

    /**
     * <h1>Log test step pass/fail in html report</h1>
     * @param result,step
     * @throws Exception
     */
    public static void logTestStepPassOrFail(boolean result, String step) throws Exception {
        if (result) {
            logTestStepPass(step);
        } else {
            logTestStepFail(step);
            throw new TestStepFailException(step);
        }
    }//End- Log Step

    public static void logStepWithSoftAssert(boolean result, String step) {
        if (result) {
            logTestStepPass(step);
        } else {
            logTestStepFail(step);
        }
    }

    public static void logScreenshot(String name) {
        getTest().log(LogStatus.INFO, name);
        if (GetUserData("EnableScreenShot").equalsIgnoreCase("Yes"))
            getTest().log(LogStatus.INFO, "Informative screenshot" + getTest().addBase64ScreenShot("data:image/png;base64," + Common.CaptureScreenForReport_Base64()));
    }

    public static void logInfoStepColored(COLOR color, String message) {
        Log.info(message);
        String html = String.format("<span style='color:%s;'>%s</span>", color.getValue(), message);
        getTest().log(LogStatus.INFO, html);
    }

    @BeforeSuite
    public void beforeSuite() throws IOException {
        //Set Form to use
        Common.saveFormName(Common.GetUserData("FormName"));
    }

    @BeforeClass
    public void beforeClass() throws Exception {
        Common.Init();
    }

    @AfterClass
    public void close() {
        Common.closeBrowser();
    }


    public static enum COLOR {
        RED("red"), GREEN("green"), BLUE("blue"), PURPLE("purple"), ORANGE("OrangeRed"), MAGENTA("Magenta");
        private String value;

        private COLOR(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }//end enm

    public static void logStepWithInfo(boolean result, String step) {
        if (result) {
            String PassLabel = "<span style='border:1px solid green;border-radius: 5px; padding:1px;background:green;color:white;margin-right:2px;'>PASS</span>";
            String html = PassLabel + "<span style='color:green;'>" + step + "</span>";
           logInfoStepColored(COLOR.BLUE,html);
        } else {
            String FailLabel = "<span style='border:1px solid red;border-radius: 5px; padding:1px;background:red;color:white;margin-right:2px;'>FAIL</span>";
            String html = FailLabel + "<span style='color:red;'>" + step + "</span>";
           logInfoStepColored(COLOR.RED,html);
        }
    }

}
