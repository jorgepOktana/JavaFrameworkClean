import Utils.Utils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import pageObjects.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestBase extends Utils {

    LoginPage loginPage;
    HomePage homePage;
    ListRequestsPage listRequestsPage;
    NewRequestPage newRequestPage;
    SelectDocumentsPage selectDocumentsPage;
    CertificationTestingForm certificationTestingForm;
    NonCertificationTestingForms nonCertificationTestingForm;

    @BeforeClass
    public void initPages (){
        loginPage = new LoginPage();
        homePage = new HomePage();
        listRequestsPage = new ListRequestsPage();
        newRequestPage = new NewRequestPage();
        selectDocumentsPage = new SelectDocumentsPage();
        certificationTestingForm = new CertificationTestingForm();
        nonCertificationTestingForm = new NonCertificationTestingForms();
    }

    @AfterClass
    public void closeBrowser() throws Exception {
        if (driver == null) {
            return;
        }
        driver.close();
        driver.quit();
    }

        /**
     * Method to take screenshots on failure
     */
    @AfterMethod(alwaysRun = true)
    public void catchFailureScreenshot(ITestResult result)
            throws InterruptedException {

        // don't attempt to take a screen shot if the browser already died.
        if (driver == null) {
            return;
        }
        String dateSuffix = new SimpleDateFormat("MM_dd_yyyy_hh_mm_ss").format(
                Calendar.getInstance().getTime()).toString();
        String methodName = result.getName();
        if (!result.isSuccess()) {
            // Taking screentshot of failed_screens...
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            Assert.assertNotNull(screenshot, "Can't get TakesScreenshot!"
                    + screenshot);
            if (screenshot != null) {
                File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
                File destFile = new File("Screenshots" + File.separator
                        + methodName + "-" + dateSuffix + ".png");
                try {
                    copyFileUsingStream(srcFile, destFile);
//                    FileUtils.copyFile(srcFile, destFile);
                } catch (IOException ioe) {
                    System.out
                            .println("Exception while creating the screenshot file!"
                                    + ioe.getMessage());
                }
            } else
                return;
            Thread.sleep(IMPLICIT_WAIT_TIME);
        }
    }

    public void createProduct

}
