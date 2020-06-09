package Utils;

import com.codeborne.selenide.WebDriverRunner;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry_3AWF implements IRetryAnalyzer {

    private int retryCount = 1;
    private int maxRetryCount = 2;

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (!iTestResult.isSuccess()) { //Check if test not succeed
            if (retryCount < maxRetryCount) { //Check if maxtry count is reached
                try {
                    WebDriverRunner.getWebDriver().navigate().refresh();
                }catch (Throwable th){}

                TestBase.logTestStepFail("Test case failed");
                TestBase.logTestStep("Retrying test " + iTestResult.getName() + " with status "
                        + getResultStatusName(iTestResult.getStatus()) + " for the " + (retryCount + 1) + " time(s).");

                retryCount++;                                     //Increase the maxTry count by 1
                iTestResult.setStatus(ITestResult.FAILURE);  //Mark test as failed
                return true;                                 //Tells TestNG to re-run the test
            } else {
                iTestResult.setStatus(ITestResult.FAILURE);  //If maxCount reached,test marked as failed
            }
        } else {
            iTestResult.setStatus(ITestResult.SUCCESS);      //If test passes, TestNG marks it as passed
        }
        return false;
    }


    public String getResultStatusName(int status) {
        String resultName = null;
        if (status == 1)
            resultName = "SUCCESS";
        if (status == 2)
            resultName = "FAILURE";
        if (status == 3)
            resultName = "SKIP";
        return resultName;
    }

}
