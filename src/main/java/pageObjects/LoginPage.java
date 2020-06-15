package pageObjects;

import Utils.TestStepFailException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import Utils.Log;

import static java.lang.Thread.sleep;

public class LoginPage extends BasePage{

	//these are the old (used) selectors
//	public static By txtUsername = By.id("username");
//	public static By txtPassword = By.id("password");
//	public static By btnLogin = By.id("Login");
//	public static By drpSwitchToLighteningById = By.id("userNavLabel");
//	public static By drpSwitchToLighteningByXPath = By.xpath("//span[@id='userNavLabel']");
//	public static By OptionSwitchToLightening = By.xpath("//*[@id='userNav-menuItems']/a");
//	public static By appLauncherIcon = By.xpath("//div[@class='slds-icon-waffle']");
//	public static By pageHeaderForLighteningView = By.xpath(".//*[@id='oneHeader']");
//	public static By imgWaffle = By.cssSelector("nav.appLauncher button span");
//	public static By imgProfile = By.cssSelector(".uiImage");
//	public static By imgProfileTrigger = By.xpath("//img[contains(@class,'profileTrigger')]");
//	public static By lnkLogout = By.linkText("Log Out");

	//define new selectors like this:
	public static final String USERNAME_INPUT = "#username";
	public static final String PASSWORD_INPUT = "#password";
	public static final String LOGIN_BUTTON = "#Login";
	public static final	String NEW_PASSWORD = "#newpassword";
	public static final	String HEADER = "#header";

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


	public WebElement getNewPassword() {
		By locator = By.cssSelector(NEW_PASSWORD);
		waitForElementToLoad(locator);
		WebElement element = driver.findElement(locator);

		return element;
	}

	public WebElement getHeader() {
		By locator = By.cssSelector(HEADER);
		waitForElementToLoad(locator);
		WebElement element =  driver.findElement(locator);

		return element;
	}

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * <h1>Login User <h1/>
	 * <p>Purpose:This method is used for login to application</p>
	 *
	 * @param UserName,Password
	 * @throws Exception
	 */

	public void LoginUser(String UserName, String Password) throws Exception {
		Log.info("Start Log in to application");
		Log.info("Enter user name");
		getUsernameInput().sendKeys(UserName);
//		FindAnElement(txtUsername).sendKeys(UserName);
		Log.info("Enter password");
		getPasswordInput().sendKeys(Password);
//		FindAnElement(txtPassword).sendKeys(Password);
		getLoginButton().click();
//		ClickElement(By.id("Login"), "Login button");
//		sleep(5000);
		waitForPageLoadToComplete();
		boolean isLogin = !getUsernameInput().isDisplayed();
//		boolean isLogin = $(txtUsername).is(hidden);

		//Verify if Change password screen appear
		if (isLogin) {
			if (getNewPassword().isDisplayed()) {
				Log.error("Salesforce message: " + getHeader().getText());
				throw new TestStepFailException("Login Failed> SALESFORCE CHANGE PASSWORD WINDOW APPEARED");
			}
		}

		if (!isLogin) {
			throw new TestStepFailException("Login Failed");
		}
		waitForPageLoadToComplete();
	}

}
