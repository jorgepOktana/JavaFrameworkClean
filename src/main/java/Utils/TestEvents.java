package Utils;

import com.relevantcodes.extentreports.LogStatus;
import icix.TestData.GlobalTestData;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import static icix.Utils.Common.*;

public class TestEvents implements ITestListener {
    @Override
    public void onFinish(ITestContext arg0) {
    }

    @Override
    public void onStart(ITestContext arg0) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
    }

    @Override
    public void onTestFailure(ITestResult testResult) {
        String testCaseName;
        if (testResult.getMethod().getMethodName().contains("QA_")) {
            testCaseName = testResult.getMethod().getMethodName();
        } else {
            testCaseName = testResult.getTestClass().getRealClass().getSimpleName();
        }

        ReportUtil.getTest().log(LogStatus.FAIL, testResult.getThrowable());
        if (getUserData("EnableScreenShot").equalsIgnoreCase("Yes"))
            ReportUtil.getTest().log(LogStatus.INFO, "Test Case Failed screenshot" + ReportUtil.getTest().addBase64ScreenShot("data:image/png;base64," + captureScreenForReport_Base64()));

        ReportUtil.endTest();

        //Add coverage to report
        addCoverage(testResult,LogStatus.FAIL);
    }

    @Override
    public void onTestSkipped(ITestResult testResult) {
        String testCaseName;
        if (testResult.getMethod().getMethodName().contains("QA_")) {
            testCaseName = testResult.getMethod().getMethodName();
        } else {
            testCaseName = testResult.getTestClass().getRealClass().getSimpleName();
        }

        Log.info("Test Case Skipped: " + testCaseName);
        ReportUtil.startTest(testCaseName, testResult.getMethod().getDescription(), testResult.getMethod().getGroups());
        ReportUtil.getTest().log(LogStatus.SKIP, "Test Case Skipped " + testResult.getThrowable());
        ReportUtil.endTest();

        //Add coverage to report
        addCoverage(testResult,LogStatus.SKIP);

    }


    @Override
    public void onTestStart(ITestResult testResult) {
        String testCaseName;
        if (testResult.getMethod().getMethodName().contains("QA_")) {
            testCaseName = testResult.getMethod().getMethodName();
        } else {
            testCaseName = testResult.getTestClass().getRealClass().getSimpleName();
        }
        Log.info("Test Case Started: " + testCaseName);
        ReportUtil.startTest(testCaseName, testResult.getMethod().getDescription(), testResult.getMethod().getGroups());
    }

    @Override
    public void onTestSuccess(ITestResult testResult) {
        String testCaseName;
        if (testResult.getMethod().getMethodName().contains("QA_")) {
            testCaseName = testResult.getMethod().getMethodName();
        } else {
            testCaseName = testResult.getTestClass().getRealClass().getSimpleName();
        }

        Log.info("Test Case Passed - " + testCaseName);
        ReportUtil.getTest().log(LogStatus.PASS, "Test Case Passed");
        ReportUtil.endTest();

        //Add coverage to report
        addCoverage(testResult,LogStatus.PASS);
    }

    public void addCoverage(ITestResult testResult, LogStatus status ){
        String testCaseName;
        if (testResult.getMethod().getMethodName().contains("QA_")) {
            testCaseName = testResult.getMethod().getMethodName();
        } else {
            testCaseName = testResult.getTestClass().getRealClass().getSimpleName();
        }

        String parentTestName=testCaseName.replaceAll("_Test", "").replaceAll("_", "-");
        for (String coverageId : GlobalTestData.getCoverageIds(parentTestName)) {
            ReportUtil.startTestForCoverage(coverageId,
                    "This is coverage of test " + parentTestName
                    , testResult.getMethod().getGroups());
            ReportUtil.getTest()
                    .log(status,
                            "Parent Test has status " +status.name() +" see test >"+ parentTestName);
            ReportUtil.endTest();
        }

    }

}
