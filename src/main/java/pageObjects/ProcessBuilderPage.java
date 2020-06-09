package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class ProcessBuilderPage extends BasePage{

	public static final String NEW_BUTTON_XPATH = "//button/span[text()='New']";
	public static final String NEW_BUTTON_CSS = ".label.bBody";
	public static final String WAFFLE_ICON = "//div[@class='slds-icon-waffle']";
	public static final String IFRAME = "//iframe";
	public static final String PROCESS_NAME_INPUT = "//span[text()='Process Name']/following-sibling::div/input";
	public static final String PROCESS_STARTS_WHEN_SELECT = "//select";
	public static final String PROCESS_SAVE_BUTTON = "//button/span[text()='Save']";
	public static final String ADD_OBJECT_BUTTON = "//span[text()='Add Object']";

	public ProcessBuilderPage(WebDriver driver) {
		super(driver);
	}

	public WebElement getNewButton() {
		By locator = By.xpath(NEW_BUTTON_XPATH);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}

	public WebElement getWaffleIcon() {
		By locator = By.xpath(WAFFLE_ICON);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}

	public WebElement getIframe() {
		By locator = By.xpath(IFRAME);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}

	public void switchToProcessBuiderIframe() {
		driver.switchTo().frame(getIframe());
	}

	public WebElement getProcessNameInput() {
		By locator = By.xpath(PROCESS_NAME_INPUT);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}

	public Select getProcessStartsWhenSelect() {
		By locator = By.xpath(PROCESS_STARTS_WHEN_SELECT);
		waitForElementToLoad(locator);
		Select element = new Select(driver.findElement(locator));

		return element;
	}

	public WebElement getProcessSaveButton() {
		By locator = By.xpath(PROCESS_SAVE_BUTTON);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}
	public WebElement getAddObjectButton() {
		By locator = By.xpath(ADD_OBJECT_BUTTON);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}

	public WebElement getWebElementByXpath(String locatorInXpath){
		By locator = By.xpath(locatorInXpath);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}

	public WebElement getWebElementByCss(String locatorInCss){
		By locator = By.cssSelector(locatorInCss);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}

}
