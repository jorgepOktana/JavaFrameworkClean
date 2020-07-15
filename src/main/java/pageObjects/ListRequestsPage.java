package pageObjects;

import Utils.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ListRequestsPage extends BasePage {

    public ListRequestsPage() {
        super();
    }

    @FindBy(xpath = "//tbody/tr//th//a")
    public List<WebElement> requestNameList;

    public boolean isCurrentPage() {
        return listNameObject.getText().equalsIgnoreCase("Requests");
    }

    public void clickNewRequest() {
        if (isCurrentPage()) {
            waitUntilDisplayed(newButton);
            clickButton(newButton);
        } else {
            Log.error("Not on Requests List Page");
        }
    }

    public void searchRequestByName(String requestName){
        waitUntilDisplayed(newButton);
        waitUntilDisplayed(searchObjectBar);
        enterText(searchObjectBar, requestName);
        clickButton(refreshListBtn);
        sleepSeconds(3);
        clickFirstMatchingText(requestNameList, requestName);
    }
}
