package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage{

	public static final String PAGE_LOOK_UP_SEARCH_FRAME = "//frame[contains(@name,'searchFrame')]";

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public WebElement getUsernameInput() {
		By locator = By.cssSelector(USERNAME_INPUT);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

    	return element;
	}

	public WebElement getPasswordInput() {
		By locator = By.cssSelector(PASSWORD_INPUT);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}

	public WebElement getLoginButton() {
		By locator = By.cssSelector(LOGIN_BUTTON);
		waitForElementToLoad(locator);
		WebElement element =  driver.findElement(locator);

		return element;
	}

	public void navigateToPage(String pageURL){
		driver.get(pageURL);
	}

}
