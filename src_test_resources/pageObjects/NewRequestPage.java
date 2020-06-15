package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
