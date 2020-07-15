import TestData.RequestTestData;
import TestData.Users;
import Utils.Log;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.*;

public class CreateRequestTest extends TestBase{

    /**
     * End to End test.
     * Preconditions: we have already created a Product and there are reviewers/labs associated to our requestor org.
     * In this scenario we create 1 request for an Assortment product with at least 1 retail associated (so 2 requests are created).
     * Certification Testing Program form is selected.
     * @throws Exception
     */
    @Test
    public void createNewRequest() throws Exception {
        loginAs(Users.TYPE.Requester_Admin);
        goToRequests();
        populateRequest();
        attachDocuments("Product Test", "Certification Testing Program");
        fillAttachedForms();
        setDueDate();
        setComments();
        saveRequest();
        confirmRequest();
        getToast();
        getRequestTaskID();
        //Still needs to validate on requestor side,
        //Have to wait for the task id to appear on requestor list
    }

    /**
     * Todo: change this product to get one with at least 1 retail associated
     */
    private void populateRequest() {
        listRequestsPage.clickNewRequest();
        newRequestPage.switchToRequestFrame();
        newRequestPage.enterRequestName("This is a test request 123");
        Assert.assertTrue(newRequestPage.setRequestTypeAndSearch("Product", "QA-41 Jorge Product - Test"));
//        newRequestPage.enterRequestName(RequestTestData.getRequestName());
//        Assert.assertTrue(newRequestPage.setRequestTypeAndSearch(RequestTestData.getRequestType(),RequestTestData.getTpOrPrdctName()));
    }


    private void attachDocuments(String category, String document) {
//        selectDocumentsPage.attachDocumentForm(RequestTestData.getDocCategory(), RequestTestData.getDocName());
//        selectDocumentsPage.addRelatedRequest(true, "");
//        selectDocumentsPage.clickSave();
        newRequestPage.clickSelectDocumentBtn();
        selectDocumentsPage.attachDocumentForm(category, document);
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
            } else if (form.getText().equals("Non-Certification Non QN Testing Program")){
                clickElement(form, false, true);
                switchToLastWindow();
                nonCertificationTestingForm.fillNonCertNonQNCertification();
                driver.close();
                switchToMainWindow();
                newRequestPage.switchToRequestFrame();
            } else if (form.getText().equals("Non-Certification QN Testing Program")){
                clickElement(form, false, true);
                switchToLastWindow();
                nonCertificationTestingForm.fillNonCertQNCertification();
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
        newRequestPage.setTxtRequestComments("this are test comments");
//        newRequestPage.setTxtRequestComments(RequestTestData.getComment());
    }

    private void saveRequest() {
        newRequestPage.clickSendBtn();
    }

    private void confirmRequest(){ //this can be improved to be faster by looking at the ui and determining what screen is displayed.
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

    private void getRequestTaskID() {
        driver.switchTo().defaultContent();
        listRequestsPage.searchRequestByName(newRequestPage.getCurrentRequestName());
        currentTaskID = requestPage.getRelatedProductParentRequestID();
    }

}
