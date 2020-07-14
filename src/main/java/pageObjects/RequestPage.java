package pageObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RequestPage extends BasePage {

    /**
     * Request Details tab
     */
    @FindBy(xpath = "(//lightning-formatted-text[@data-output-element-id='output-field'])[1]")
    public WebElement hubId;

    @FindBy(xpath = "(//lightning-formatted-text[@data-output-element-id='output-field'])[2]")
    public WebElement requestName;

    @FindBy(xpath = "(//lightning-formatted-text[@data-output-element-id='output-field'])[3]")
    public WebElement requestGlobalID;

    @FindBy(xpath = "(//lightning-formatted-text[@data-output-element-id='output-field'])[4]")
    public WebElement requestStatus;

    @FindBy(xpath = "(//lightning-formatted-text[@data-output-element-id='output-field'])[5]")
    public WebElement requestType;

    //If only one request was created = Task ID will be the same as requestGlobalID
    //If there are several requests created at the same time, this will be the task ID
    @FindBy(xpath = "(//lightning-formatted-text[@data-output-element-id='output-field'])[6]")
    public WebElement relatedProductParentRequestID;

    public String getRelatedProductParentRequestID(){
        waitUntilDisplayed(relatedProductParentRequestID);
        return relatedProductParentRequestID.getText();
    }
}
