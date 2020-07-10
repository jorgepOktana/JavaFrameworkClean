import TestData.RequestTestData;
import TestData.Users;
import Utils.Log;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.*;

public class CreateRequestTest extends TestBase{

    @Test
    public void createNewRequest() throws Exception {
        beforeTest();
        populateRequest();
        attachDocuments();
        fillAttachedForms();
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
        newRequestPage.enterRequestName("This is a test request");
//        newRequestPage.enterRequestName(RequestTestData.getRequestName());
//        Assert.assertTrue(newRequestPage.setRequestTypeAndSearch(RequestTestData.getRequestType(),RequestTestData.getTpOrPrdctName()));
        Assert.assertTrue(newRequestPage.setRequestTypeAndSearch("Product", "QA-41 Jorge Product - Test"));
    }

    private void attachDocuments() {
        newRequestPage.clickSelectDocumentBtn();
        selectDocumentsPage.attachDocumentForm("Product Test", "Certification Testing Program");
//        selectDocumentsPage.attachDocumentForm(RequestTestData.getDocCategory(), RequestTestData.getDocName());
//        selectDocumentsPage.addRelatedRequest(true, "");
        selectDocumentsPage.clickSave();
    }

    private void fillAttachedForms() throws InterruptedException {
        getMainWindow();
        for (WebElement form: newRequestPage.selectedDocumentsList) {
            if(form.getText().equals("Certification Testing Program")){
                clickElement(form, false, true);
                switchToLastWindow();
                certificationTestingForm.fillCertificationTestingProgram();
                driver.close();
                switchToMainWindow();
                newRequestPage.switchToRequestFrame();
            }
        }
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
