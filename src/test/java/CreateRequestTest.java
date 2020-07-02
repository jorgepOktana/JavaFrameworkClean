import TestData.RequestTestData;
import TestData.Users;
import Utils.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.*;

public class CreateRequestTest extends TestBase{

    @Test
    public void createNewRequest() throws Exception {
        beforeTest();
        populateRequest();
        attachDocuments();
        setDueDate();
        setComments();
        saveRequest();
        confirmRequest();
        getToast();
    }

    public void beforeTest() throws Exception {
        login();
        RequestTestData.GetData("QA_7540_Test");
        homePage.clickAppLauncherItem(BasePage.AppLauncherItems.REQUESTS);
    }

    private void login() throws Exception {
        loginPage.loginAs(Users.TYPE.Requester_Admin);
        Assert.assertTrue(homePage.isCurrentPage());
    }

    private void populateRequest() {
        listRequestsPage.clickNewRequest();
        newRequestPage.switchToRequestFrame();
//        newRequestPage.enterRequestName("This is a test request");
        newRequestPage.enterRequestName(RequestTestData.getRequestName());
        Assert.assertTrue(newRequestPage.setRequestTypeAndSearch(RequestTestData.getRequestType(),RequestTestData.getTpOrPrdctName()));
//        Assert.assertTrue(newRequestPage.setRequestTypeAndSearch("Product", "QA-41 Jorge Product - Test"));

        //Need to discuss if this is the best approach to get testData (testId is required so far)
//        newRequestPage.enterRequestName(RequestTestData.getRequestName());
//        newRequestPage.setRequestTypeAndSearch(RequestTestData.getRequestType(),RequestTestData.getTpOrPrdctName());

    }

    private void attachDocuments() {
        newRequestPage.clickSelectDocumentBtn();
//        selectDocumentsPage.attachDocumentForm("Form", "test");
        selectDocumentsPage.attachDocumentForm(RequestTestData.getDocCategory(), RequestTestData.getDocName());
//        selectDocumentsPage.addRelatedRequest(true, "");
        selectDocumentsPage.clickSave();
    }

    private void setDueDate() {
        newRequestPage.setDueDateNextMonth();
    }

    private void setComments() {
//        newRequestPage.setTxtRequestComments("this are test comments");
        newRequestPage.setTxtRequestComments(RequestTestData.getComment());
    }

    private void saveRequest() {
        newRequestPage.clickSendBtn();
    }

    private void confirmRequest(){
        Assert.assertTrue(newRequestPage.confirmSelectedItems());
        try {
            newRequestPage.clickYes();
        } catch (Exception e) {
            newRequestPage.clickYesExisting();
        }

    }

    public void getToast() {
        Assert.assertTrue(newRequestPage.getToast().equals("1 request sent successfully"));
    }
}
