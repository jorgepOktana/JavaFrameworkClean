package pageObjects;

import TestData.RequestTestData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class NewRequestPage extends BasePage {

    /**
     * This NewRequestPage seems to be inside of an iframe so before acccesing the
     * elements we probably need to move to the iframe (iframe is on BasePage)
     */

    public NewRequestPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//h1[@class='slds-page-header__title slds-p-right--x-small ng-binding']")
    public WebElement pageName;

    @FindBy(xpath = "//input[@id='requestName']")
    public WebElement txtRequestName;

    //This returns both append checkboxes, 0 is Product Name, 1 is Trading Partner.
    @FindBy(xpath = "//span[@class='slds-checkbox--faux clickable']")
    public List<WebElement> appendCheckbox;

    //This returns both requests types checkboxes, 0 is Trading Partner, 1 is Product
    @FindBy(xpath = "//span[@class='slds-radio--faux']")
    public List<WebElement> requestType;

    @FindBy(xpath = "//input[contains(@placeholder,'Select a Trading Partner')]")
    public WebElement tPartnerPicklist;

    @FindBy(xpath = "//input[contains(@placeholder,'Select a product')]")
    public WebElement productPicklist;

    //This returns the list of results when looking for a TP or Product on Request Type
    @FindBy(xpath = "//span[@class='clickable ng-binding']")
    public List<WebElement> picklistResults;

    //This returns a list of elements selected on the Request Type
    //If request type = TP the list size = number of selected TP
    //If request type = Product the list size = number of products + associated TP
    @FindBy(xpath = "//span[contains(@class,'slds-pill__label stretch ng-binding')]")
    public List<WebElement> elementsSelected;

    @FindBy(xpath = "(//span[contains(@class,'slds-pill ng-scope slds-pill--editable')]/svg-icon[2])")
    public List<WebElement> removeSelectedElem;

    @FindBy(xpath = "//span[@class='ng-binding'][contains(.,'Add CC/BCC')]")
    public WebElement addCCBCC;

    @FindBy(xpath = "//button[contains(.,'Select Documents / Forms')]")
    public WebElement selectDocumentsBtn;

    @FindBy(xpath = "//ul//a[@class='ng-binding']")
    public List<WebElement> selectedDocumentsList;

    @FindBy(xpath = "//input[@id='dueDate']")
    public WebElement dueDate;

    @FindBy(xpath = "//*[@Title='Next Month']")
    public WebElement nextMonth;

    @FindBy(xpath = "//*[contains(@ng-repeat,'day in week')]//span")
    public List<WebElement> dateDays;

    @FindBy(id = "comments")
    public WebElement requestComments;

    @FindBy(xpath = "(//span[contains(.,'Cancel')])[3]")
    public WebElement cancelRequestBtn;

    @FindBy(xpath = "(//span[contains(.,'Save as Draft')])[3]")
    public WebElement saveAsDraftBtn;

    @FindBy(xpath = "(//span[contains(.,'Send')])[2]")
    public WebElement sendRequestBtn;

    public boolean isCurrentPage() {
        return pageName.getText().equalsIgnoreCase("Create New Request");
    }

    public void switchToRequestFrame() {
        switchToIFrame();
    }

    public void enterRequestName(RequestTestData requestTestData) {
        String requestName = requestTestData.getRequestName();
        waitUntilDisplayed(txtRequestName);
        enterText(txtRequestName, requestName);
    }

    public void appendToRequest(String append){
        clickFirstMatchingText(appendCheckbox, append);
    }

    public void setRequestType(RequestTestData requestTestData){
        if(requestTestData.getRequestType().equalsIgnoreCase("Trading Partner")){
            waitUntilDisplayed(requestType.get(0));
            clickElement(requestType.get(0), false, false);
            waitUntilDisplayed(tPartnerPicklist);
            enterText(tPartnerPicklist,requestTestData.getTpOrPrdctName());
        } else { //request Type = Product
            waitUntilDisplayed(requestType.get(1));
            clickElement(requestType.get(1), false, false);
            waitUntilDisplayed(productPicklist);
            enterText(productPicklist, requestTestData.getTpOrPrdctName());
        }
        waitUntilDisplayed(picklistResults.get(0));
        clickFirstMatchingText(picklistResults, requestTestData.getTpOrPrdctName());
    }

    public void clickSelectDocumentBtn(){
        waitUntilDisplayed(selectDocumentsBtn);
        clickButton(selectDocumentsBtn);
    }

    public void setDueDate() {
        waitUntilDisplayed(dueDate);
        clickElement(dueDate, false, false);
        waitUntilDisplayed(nextMonth);
        clickElement(nextMonth, false, false);
        int size = dateDays.size();
//        int index = dateDays.lastIndexOf(dateDays);
        dateDays.get(size-1);
    }

    public void setRequestComments(RequestTestData requestTestData){
        waitUntilDisplayed(requestComments);
        enterText(requestComments, requestTestData.getComment());
    }

    public void clickSendBtn(){
        waitUntilDisplayed(sendRequestBtn);
        clickButton(sendRequestBtn);
    }

    public void clickSaveAsDraft() {
        waitUntilDisplayed(saveAsDraftBtn);
        clickButton(saveAsDraftBtn);
    }

    public void clickCancelRequestBtn() {
        waitUntilDisplayed(cancelRequestBtn);
        clickButton(cancelRequestBtn);
    }
}