package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SalesforceSetupPage extends BasePage{

	public static final String SEARCH_BOX_XPATH = "//div[contains(@class, 'onesetupSetupNavTree')]//input";
	public static final String SEARCH_BOX_CSS = "[class*='onesetupSetupNavTree'] input";
	public static final String SEARCH_RESULT_CSS = "mark";

	public SalesforceSetupPage(WebDriver driver) {
		super(driver);
	}

}
