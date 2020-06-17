package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ListRequestsPage extends BasePage {

    public ListRequestsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCurrentPage() {
        return listNameObject.getText().equalsIgnoreCase("Requests");
    }

    public void clickNewRequest() {
        waitUntilDisplayed(newButton);
        clickButton(newButton);
    }
}
