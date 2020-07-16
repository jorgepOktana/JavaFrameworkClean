import TestData.RequestTestData;
import TestData.Users;
import Utils.Log;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.*;

public class CreateRequestTest extends TestBase{

    /**
     * TO-DO: need to be able to create Products via API to insert this product before running the test.
     * This below is just a test product.
     */
    String productName = "JAG1ASSORTMENT_HASBRO_12Mar - Test";

    /**
     * Preconditions: we have already created a Product and there are reviewers/labs associated to our requestor org.
     * In this scenario we create 1 request for an Assortment product with at least 1 retail associated (so 2 requests are created).
     * Certification Testing Program form is selected.
     * @throws Exception
     */
    @Test
    public void createRequestForAssortmentWithRetailCertification() throws Exception {
        apiTests.createAssortment();
        loginAs(Users.TYPE.Requester_Admin);
        goToRequests();
        populateRequest("Product", productName);
        attachDocuments("Product Test", "Certification Testing Program");
        fillAttachedForms();
        setDueDate();
        setComments("this are test comments");
        saveRequest();
        confirmRequest();
        getToast("2 requests sent successfully");
        getRequestTaskID();
        //Still needs to validate on requestor side,
        //Have to wait for the task id to appear on requestor list
    }

    /**
     * Preconditions: we have already created a Product and there are reviewers/labs associated to our requestor org.
     * In this scenario we create 1 request for an Assortment product with at least 1 retail associated (so 2 requests are created).
     * Non Certification QN Testing Program form is selected.
     * @throws Exception
     */
    @Test
    public void createRequestForAssortmentWithRetailNonCertificationQN() throws Exception {
        loginAs(Users.TYPE.Requester_Admin);
        goToRequests();
        populateRequest("Product", productName);
        attachDocuments("Product Test", "Non-Certification QN Testing Program");
        fillAttachedForms();
        setDueDate();
        setComments("this are test comments");
        saveRequest();
        confirmRequest();
        getToast("2 requests sent successfully");
        getRequestTaskID();
        //Still needs to validate on requestor side,
        //Have to wait for the task id to appear on requestor list
    }

    /**
     * Preconditions: we have already created a Product and there are reviewers/labs associated to our requestor org.
     * In this scenario we create 1 request for an Assortment product with at least 1 retail associated (so 2 requests are created).
     * Non Certification Non QN Testing Program form is selected.
     * @throws Exception
     */
    @Test
    public void createRequestForAssortmentWithRetailNonCertificationNonQN() throws Exception {
        loginAs(Users.TYPE.Requester_Admin);
        goToRequests();
        populateRequest("Product", productName);
        attachDocuments("Product Test", "Non-Certification Non QN Testing Program");
        fillAttachedForms();
        setDueDate();
        setComments("this are test comments");
        saveRequest();
        confirmRequest();
        getToast("2 requests sent successfully");
        getRequestTaskID();
        //Still needs to validate on requestor side,
        //Have to wait for the task id to appear on requestor list
    }

    private void populateRequest(String requestType, String tpOrProductName) {
        listRequestsPage.clickNewRequest();
        newRequestPage.switchToRequestFrame();
        newRequestPage.enterRequestName(productName + " - "+ getTimeStamp());
        Assert.assertTrue(newRequestPage.setRequestTypeAndSearch(requestType, tpOrProductName));
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

    private void fillAttachedForms() {
        getMainWindow();
        for (WebElement form: newRequestPage.selectedDocumentsList) {
            if(form.getText().equals("Certification Testing Program")){
                clickElement(form, false, true);
                //add try catch
                switchToLastWindow();
                certificationTestingForm.fillCertificationTestingProgram();
//                driver.close();
                switchToMainWindow();
                newRequestPage.switchToRequestFrame();
            } else if (form.getText().equals("Non-Certification Non QN Testing Program")){
                clickElement(form, false, true);
                switchToLastWindow();
                nonCertificationTestingForm.fillNonCertNonQNCertification();
//                driver.close();
                switchToMainWindow();
                newRequestPage.switchToRequestFrame();
            } else if (form.getText().equals("Non-Certification QN Testing Program")){
                clickElement(form, false, true);
                switchToLastWindow();
                nonCertificationTestingForm.fillNonCertQNCertification();
//                driver.close();
                switchToMainWindow();
                newRequestPage.switchToRequestFrame();
            }
        }
    }

    private void setDueDate() {
        newRequestPage.setDueDateNextMonth();
    }

    private void setComments(String comments) {
        newRequestPage.setTxtRequestComments(comments);
//        newRequestPage.setTxtRequestComments(RequestTestData.getComment());
    }

    private void saveRequest() {
        newRequestPage.clickSendBtn();
    }

    private void confirmRequest(){
        Assert.assertTrue(newRequestPage.confirmSelectedItems());
        if(newRequestPage.validateModal()) {
            newRequestPage.clickYes();
        } else {
            newRequestPage.clickYesExisting();
        }

    }

    public void getToast(String toast) {
        Assert.assertTrue(newRequestPage.getToast().equals(toast));
    }

    private void getRequestTaskID() {
        driver.switchTo().defaultContent();
        listRequestsPage.searchRequestByName(newRequestPage.getCurrentRequestName());
        currentTaskID = requestPage.getRelatedProductParentRequestID();
    }

}
