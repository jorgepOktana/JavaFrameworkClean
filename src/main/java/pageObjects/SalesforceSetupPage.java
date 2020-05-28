package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static pageConstants.SalesforceSetupPageConstants.*;


public class SalesforceSetupPage extends BasePage{

	public SalesforceSetupPage(WebDriver driver) {
		super(driver);
	}

	public WebElement getSearchBoxInput() {
		By locator = By.xpath(SEARCH_BOX_XPATH);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}

	public WebElement getMarkedSearchResult() {
		By locator = By.cssSelector(SEARCH_RESULT_CSS);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}

}
