package pageObjects;

import org.openqa.selenium.WebDriver;

public class SalesforceSetupPage extends BasePage{

	public static final String SEARCH_BOX_XPATH = "//div[contains(@class, 'onesetupSetupNavTree')]//input";
	public static final String SEARCH_BOX_CSS = "[class*='onesetupSetupNavTree'] input";
	public static final String SEARCH_RESULT_CSS = "mark";

	public SalesforceSetupPage() {
		super();
	}

}
