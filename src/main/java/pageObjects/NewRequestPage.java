package pageObjects;

import jdk.nashorn.internal.objects.annotations.Function;
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

    //This returns both append checkboxes, 1 is Product Name, 2 is Trading Partner.
    @FindBy(xpath = "//span[@class='slds-checkbox--faux clickable']")
    public List<WebElement> appendCheckbox;

    //This returns both requests types checkboxes, 1 is Trading Partner, 2 is Product
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

    public boolean isCurrentPage() {
        return pageName.getText().equalsIgnoreCase("Create New Request");
    }

    public void switchToRequestFrame() {
        switchToIFrame();
    }

    public void enterRequestName(String requestName) {
        waitUntilDisplayed(txtRequestName);
        txtRequestName.clear();
        txtRequestName.sendKeys(requestName);
    }
}