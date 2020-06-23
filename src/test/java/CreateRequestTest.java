import TestData.RequestTestData;
import TestData.Users;
import Utils.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.*;

public class CreateRequestTest {

    WebDriver driver = new ChromeDriver();
    LoginPage loginPage = new LoginPage(driver);
    HomePage homePage = new HomePage(driver);
    ListRequestsPage listRequestsPage = new ListRequestsPage(driver);
    NewRequestPage newRequestPage = new NewRequestPage(driver);
    SelectDocumentsPage selectDocumentsPage = new SelectDocumentsPage(driver);

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
//        requestTestData.GetData("QA_59_Test");
        homePage.clickAppLauncherItem(BasePage.AppLauncherItems.REQUESTS);
    }

    private void login() throws Exception {
        loginPage.loginAs(Users.TYPE.Requester_Admin);
        Assert.assertTrue(homePage.isCurrentPage());
    }

    private void populateRequest() {
        listRequestsPage.clickNewRequest();
        newRequestPage.switchToIFrame();
        newRequestPage.enterRequestName("This is a test request");
        Assert.assertTrue(newRequestPage.setRequestTypeAndSearch("Product", "QA-41 Jorge Product - Test"));

        //Need to discuss if this is the best approach to get testData (testId is required so far)
//        newRequestPage.enterRequestName(RequestTestData.getRequestName());
//        newRequestPage.setRequestTypeAndSearch(RequestTestData.getRequestType(),RequestTestData.getTpOrPrdctName());

    }

    private void attachDocuments() {
        newRequestPage.clickSelectDocumentBtn();
        selectDocumentsPage.attachDocumentForm("Form", "test");
//        selectDocumentsPage.attachDocumentForm(RequestTestData.getDocCategory(), RequestTestData.getDocName());
//        selectDocumentsPage.addRelatedRequest(true, "");
        selectDocumentsPage.clickSave();
    }

    private void setDueDate() {
        newRequestPage.setDueDateNextMonth();
    }

    private void setComments() {
        newRequestPage.setTxtRequestComments("this are test comments");
//        newRequestPage.setRequestComments(RequestTestData.getComment());
    }

    private void saveRequest() {
        newRequestPage.clickSendBtn();
    }

    private void confirmRequest(){
        Assert.assertTrue(newRequestPage.confirmSelectedItems());
        newRequestPage.clickYes();
    }

    public void getToast() {
        newRequestPage.waitUntilDisplayed(newRequestPage.toastMessage);
        String toast = newRequestPage.toastMessage.getText();
        Log.info(toast);
    }

}
