package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class SelectDocumentsPage extends BasePage {

    public SelectDocumentsPage() {
        super();
    }

    //This returns a list of document categories (All, Audit, Product Spec, Form, Incident, Recall, Product Test
    @FindBy(xpath = "//div[@class='slds-m-bottom--small ng-scope']/a")
    public List<WebElement> documentCategory;

    @FindBy(xpath = "//input[@placeholder='Search']")
    public WebElement documentSearch;

    @FindBy(xpath = "//div[@class='slds-grid scrollable-columns ng-scope']//*[@class='slds-checkbox']")
    public List<WebElement> documentList;

    @FindBy(xpath = "(//span[@ng-if='vm.footerText'])[1]")
    public WebElement documentsFooter;

    @FindBy(xpath = "//*[text()='Attach']/parent::*[1]")
    public WebElement attachBtn;

    @FindBy(xpath = "//div[@class='slds-modal__footer ng-scope']//span[contains(.,'Cancel')]")
    public WebElement cancelBtn;

    @FindBy(xpath = "(//svg-button[@icon='close']//span[text()='Close'])[2]")
    public WebElement closeDocumentsModal;

    @FindBy(xpath = "//ul[@class='relatedRequestUL disc slds-p-around--small']//b[@class='ng-binding']")
    public WebElement selectedDocumentsList;

    @FindBy(xpath = "//input[@id='txtRelatedRequestMod']")
    public WebElement relatedRequestPicklist;

    @FindBy(xpath = "//div[@class='slds-modal__footer ng-scope']//span[contains(.,'Back')]")
    public WebElement backButton;

    @FindBy(xpath = "//div[@class='slds-modal__footer ng-scope']//span[contains(.,'Skip')]")
    public WebElement skipButton;

    @FindBy(xpath = "//div[@class='slds-modal__footer ng-scope']//span[contains(.,'Save')]")
    public WebElement saveButton;

    public void selectDocumentCategory(String category){
        waitUntilDisplayed(documentCategory.get(0));
        clickFirstMatchingText(documentCategory, category);
    }

    public void searchDocument(String document){
//        switchToIFrame();
        waitUntilDisplayed(documentSearch);
        enterText(documentSearch, document);
    }

    public void selectDocumentForm(String document){
        waitUntilDisplayed(documentList.get(0));
        clickFirstMatchingTextJS(documentList, document);
    }

    public void clickAttach(){
        waitUntilDisplayed(attachBtn);
        clickButton(attachBtn);
    }

    /**
     * <h1>Attach Form<h1/>
     * <p>Purpose: This method is used to attach form in documents selection modal</p>
     *
     * @param document
     * @param category
     * can be updated to pass a list of documents? so we dont need to do the whole process adding one by one ??
     */
    public void attachDocumentForm(String category, String document){
//        switchToIFrame();
        selectDocumentCategory(category);
        selectDocumentForm(document);
        clickAttach();
    }

    /**
     * <h1>Search and Attach Form<h1/>
     * <p>Purpose: This method is used to attach form in documents selection modal</p>
     *
     * @param document
     */
    public void searchAndAttachForm(String document){
        searchDocument(document);
        selectDocumentForm(document);
        clickAttach();
    }

    public void closeDocumentModal(){
        waitUntilDisplayed(closeDocumentsModal);
        clickButton(closeDocumentsModal);
    }

    public void clickCancelBtn(){
        waitUntilDisplayed(cancelBtn);
        clickButton(cancelBtn);
    }

    public boolean isAttachEnabled() {
        waitUntilDisplayed(attachBtn);
        return attachBtn.isEnabled();
    }

    public void clickSave() {
        waitUntilDisplayed(saveButton);
        clickButton(saveButton);
    }

    public void clickSkip() {
        waitUntilDisplayed(skipButton);
        clickButton(skipButton);
    }

    public void clickBack() {
        waitUntilDisplayed(backButton);
        clickButton(backButton);
    }

    public void addRelatedRequest(boolean add, String relatedRequest){
        waitUntilDisplayed(relatedRequestPicklist);
        if (add){
            enterText(relatedRequestPicklist, relatedRequest);
            clickSave();
        } else {
            clickSkip();
        }
    }

}
