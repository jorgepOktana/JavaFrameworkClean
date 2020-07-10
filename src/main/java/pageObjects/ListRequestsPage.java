package pageObjects;

import Utils.Log;
import org.openqa.selenium.WebDriver;

public class ListRequestsPage extends BasePage {

    public ListRequestsPage() {
        super();
    }

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
}
