package pageObjects;

import Utils.Log;
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
    public List<WebElement> requestTypeRadio;

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
    @FindBy(xpath = "(//div[contains(@ng-if, 'product')])[1]//span[contains(@class,'slds-pill__label stretch ng-binding')]")
    public List<WebElement> selectedProducts;

    @FindBy(xpath = "(//div[contains(@ng-if, 'product')])[2]//span[contains(@class,'slds-pill__label stretch ng-binding')]")
    public List<WebElement> selectedTPAssociatedToProducts;

    @FindBy(xpath = "(//div[contains(@ng-if, 'tp')])[1]//span[contains(@class,'slds-pill__label stretch ng-binding')]")
    public List<WebElement> selectedTradingPartners;

    @FindBy(xpath = "(//span[contains(@class,'slds-pill ng-scope slds-pill--editable')]/svg-icon[2])")
    public List<WebElement> removeSelectedTP;

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
    public WebElement txtRequestComments;

    @FindBy(xpath = "(//span[contains(.,'Cancel')])[3]")
    public WebElement cancelRequestBtn;

    @FindBy(xpath = "(//span[contains(.,'Save as Draft')])[3]")
    public WebElement saveAsDraftBtn;

    @FindBy(xpath = "(//span[contains(.,'Send')])[2]")
    public WebElement sendRequestBtn;

    @FindBy(xpath = "//a[@ng-if='vm.showLink(row, col)']")
    public List<WebElement> confirmationTPOrProductsList;

    @FindBy(xpath = "(//span[contains(.,'Close')])[7]")
    public WebElement closeRequestConfirmation;

    @FindBy(xpath = "(//span[contains(.,'No')])[4]")
    public WebElement btnNoConfirmation;

    @FindBy(xpath = "(//span[contains(.,'Yes')])[2]")
    public WebElement btnYesConfirmation;

    @FindBy(xpath = "(//span[contains(.,'Yes')])[3]")
    public WebElement btnYesConfirmationExisting;

    @FindBy(xpath = "//iframe[contains(@height,'100%')]")
    public WebElement requestIframe;

    @FindBy(xpath = "//slds-toast//h2")
    public WebElement requestToast;

    public boolean isCurrentPage() {
        return pageName.getText().equalsIgnoreCase("Create New Request");
    }

    public void switchToRequestFrame() {
        switchToIFrame(requestIframe);
    }

    public void enterRequestName(String requestName) {
        waitUntilDisplayed(txtRequestName);
        enterText(txtRequestName, requestName);
        currentRequestName = requestName;
    }

    public void appendToRequest(String append){
        clickFirstMatchingText(appendCheckbox, append);
    }

    public boolean setRequestTypeAndSearch(String requestType, String TpOrProductName){
        waitForVisibilityOfAllElem(requestTypeRadio);
        currentRequestType = requestType;
        if(requestType.equalsIgnoreCase("Trading Partner")){
            return setRequestTypeTP(TpOrProductName);
        } else { //request Type = Product
            return setRequestTypeProduct(TpOrProductName);
        }
    }

    public boolean setRequestTypeTP(String TpOrProductName){
        clickElement(requestTypeRadio.get(0), false, false);
        waitUntilDisplayed(tPartnerPicklist);
        enterText(tPartnerPicklist,TpOrProductName);
        waitForVisibilityOfAllElem(picklistResults);
        clickFirstMatchingText(picklistResults, TpOrProductName);
        for (WebElement tpSelected : selectedTradingPartners){
            if(tpSelected.getText().contains(TpOrProductName)){
                Log.info("Trading Partner selected: " + TpOrProductName);
                currentTradingPartner = TpOrProductName;
                return true;
            }
        }
        return false;
    }

    public boolean setRequestTypeProduct(String TpOrProductName) {
        clickElement(requestTypeRadio.get(1), false, false);
        waitUntilDisplayed(productPicklist);
        enterText(productPicklist, TpOrProductName);
        waitForVisibilityOfAllElem(picklistResults);
        clickFirstMatchingText(picklistResults, TpOrProductName);
        waitForVisibilityOfAllElem(selectedProducts);
        for (WebElement productSelected : selectedProducts){
            if(productSelected.getText().contains(TpOrProductName)){
                Log.info("Product Selected: " + TpOrProductName);
                currentProduct = TpOrProductName;
                return true;
            }
        }
        return false;
    }

    public void clickSelectDocumentBtn(){
        waitUntilDisplayed(selectDocumentsBtn);
        clickButton(selectDocumentsBtn);
    }

    public void setDueDateNextMonth() {
        waitUntilDisplayed(dueDate);
        clickElement(dueDate, false, true);
        waitUntilDisplayed(nextMonth);
        clickElement(nextMonth, false, false);
        int size = dateDays.size();
        clickElement(dateDays.get(size - 1), false, false);
    }

    public void setTxtRequestComments(String requestComments){
        waitUntilDisplayed(txtRequestComments);
        enterText(txtRequestComments, requestComments);
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

    public boolean confirmSelectedItems(){
        if (currentRequestType.equalsIgnoreCase("Trading Partner")){
            for(WebElement selectedTP: confirmationTPOrProductsList){
                if (selectedTP.getText().contains(currentTradingPartner)){
                    return true;
                }
            }
        } else {
            for(WebElement selectedProduct: confirmationTPOrProductsList){
                if(selectedProduct.getText().contains(currentProduct)){
                    return true;
                }
            }
        }
        return false;
    }

    public void clickClose() {
        clickButton(closeRequestConfirmation);
    }

    public void clickNo() {
        clickButton(btnNoConfirmation);
    }

    public void clickYes(){
        clickButton(btnYesConfirmation);
    }

    public void clickYesExisting(){
        clickButton(btnYesConfirmationExisting);
    }

    public void switchToDefault() {
        SwitchToDefaultContent();
    }

    public String getToast() {
//        sleepSeconds(1);
        waitUntilDisplayed(requestToast);
        String toast = requestToast.getText();
        Log.info(toast);
        return toast;
    }
}