package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

    public HomePage() {
        super();
    }

    @FindBy(className = "slds-global-header__logo")
    public WebElement orgLogo;

    public boolean isCurrentPage() {
        return orgLogo.isDisplayed();
    }

}
