package pageObjects;

import TestData.Users;
import TestData.UsersTestData;
import Utils.Log;
import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BasePage {

	private static Logger logger = LogManager.getLogger(BasePage.class);

	//Webdriver Init
	protected static WebDriver driver = null;
	protected Wait<WebDriver> wait = null;
	protected Wait<WebDriver> elementExistsWait = null;

	public static String baseUrl = "https://login.salesforce.com";

	protected static final int WAIT_TIME = 30;
	protected static final int POLL_INTERVAL = 2;
	protected static final int IMPLICIT_WAIT_TIME = 8;
	protected static final int ELEMENT_EXISTS_WAIT_TIME = 10;

	private boolean isLogin = false;
	private String currentLoginUser ="NA";

	public void navigateToPage(String pageURL){
		driver.get(pageURL);
	}

	protected String env;

	//List of common selectors
	//Items to check if we are on Classic and change
	@FindBy(xpath = "//div[@id='userNavButton']")
	public static List<WebElement> userNavButton;

	@FindBy(xpath = ".//input[@id='lexTryNow']")
	public WebElement tryNowButton;

	@FindBy(id = "userNavLabel")
	public WebElement drpSwitchToLightningById;

	@FindBy(xpath = "//*[@id='userNav-menuItems']/a")
	public List<WebElement> optionSwitchToLightning;

	//Items to log out - go to user profile
	@FindBy(className = "uiImage")
	public WebElement imgProfile;

	@FindBy(className = "panel-content")
	public WebElement panelContent;

	@FindBy(css = ".panel-content .profile-card-toplinks")
	public WebElement profileCard;

	@FindBy(linkText = "Log Out")
	public WebElement logOutLink;

	@FindBy(className = "profile-card-name")
	public WebElement profileCardName;

	//To navigate through SF objects -> All Items search
	@FindBy(xpath = "//div[@class='slds-icon-waffle']")
	public WebElement allItems;

	@FindBy(xpath = "//input[@class='slds-input']")
	public WebElement allItemsSearch;

	@FindBy(xpath = "//one-app-launcher-menu-item")
	public List<WebElement> allItemsResult;

	//Common selectors for Objects - list views and button
	@FindBy(xpath = "//span[contains(@data-aura-class,'uiOutputText forceBreadCrumbItem')]")
	public WebElement listNameObject;

	@FindBy(css = "span.triggerLinkText.selectedListView.uiOutputText")
	public WebElement listView;

	@FindBy(xpath = "//div[@class='scroller']//li[contains(@class,'slds-dropdown__item')]")
	public List<WebElement> listViewItems;

	@FindBy(xpath = "//div[@title='New']")
	public WebElement newButton;

	@FindBy(xpath = "//div[@title='Import']")
	public WebElement importButton;

	@FindBy(xpath = "//div[@title='Change Owner']")
	public WebElement changeOwnerButton;

	@FindBy(xpath = "//input[@placeholder='Search this list...']")
	public WebElement searchObjectBar;

	@FindBy(xpath = "//button[@title='List View Controls']")
	public WebElement listViewControls;

	@FindBy(xpath = "//button[@title='Display as Table']")
	public WebElement displayTable;

	@FindBy(xpath = "//button[@name='refreshButton']")
	public WebElement refreshListBtn;

	@FindBy(xpath = "//tbody/tr")
	public List<WebElement> listRows;

	//
	@FindBy(xpath = "//iframe")
	public WebElement iframe;

	@FindBy(xpath = "//*[contains(@class, 'alert-danger')]")
	private static List<WebElement> errorElementList;

	/**
	 * Constructor to initialize WebDriver and FluentWait objects using default
	 * wait time and poll interval. Also sets the implicit wait and initializes
	 * page elements using PageFactory.
	 *
	 * @param driver
	 */
	public BasePage (WebDriver driver) {

		this.driver = driver;
		env = System.getProperty("run.env");

		driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);

		wait = new FluentWait<>(driver)
				.withTimeout(WAIT_TIME, TimeUnit.SECONDS)
				.pollingEvery(POLL_INTERVAL, TimeUnit.SECONDS)
				.ignoring(StaleElementReferenceException.class)
				.ignoring(NoSuchElementException.class);

		elementExistsWait = new FluentWait<>(driver)
				.withTimeout(ELEMENT_EXISTS_WAIT_TIME, TimeUnit.SECONDS)
				.pollingEvery(POLL_INTERVAL, TimeUnit.SECONDS)
				.ignoring(StaleElementReferenceException.class)
				.ignoring(NoSuchElementException.class);

		PageFactory.initElements(driver, this);
	}

	/**
	 * Constructor to initialize WebDriver and FluentWait objects using specified
	 * wait time and poll interval. Also sets the implicit wait and initializes
	 * page elements using PageFactory.
	 *
	 * @param driver
	 */
	public BasePage(WebDriver driver, int waitTime, int pollInterval) {

		this.driver = driver;
		env = System.getProperty("run.env");

		driver.manage().timeouts().implicitlyWait(waitTime, TimeUnit.SECONDS);

		wait = new FluentWait<WebDriver>(driver)
				.withTimeout(waitTime, TimeUnit.SECONDS)
				.pollingEvery(pollInterval, TimeUnit.SECONDS)
				.ignoring(StaleElementReferenceException.class)
				.ignoring(NoSuchElementException.class);

		PageFactory.initElements(driver, this);
	}

	public void switchToIFrame() {
		driver.switchTo().frame(iframe);
	}

	public enum AppLauncherItems {
		ACCOUNT("Accounts"), ATTRIBUTES("Attributes"), CLAENDER("Calendar"), CONTACTS("Contacts"), CONTAINERTEMPLATE("Container Templates"),
		DASHBOARD("Dashboards"), DOCUMENTLIBRARY("Document Library"), FORMS("Forms"), HEALTHCHECT("HealthCheck"),
		HELP("Help"), ICIXAPPLICATIONPREFERENCE("ICIX Application Preferences"), ICIXDOCUMENTS("ICIX Documents"),
		ICIXPRODUCTSUNIVERSALID("ICIX Product Universal IDs"), HASBROPRODUCTS("Hasbro Products"), ICIXTASKS("ICIX Tasks"),
		LOGS("Logs"), MESSAGE("Messages"), PRODUCTGROUPS("Product Groups"), PRODUCTTESTMANAGER("Product Test Manager"),
		PRODUCTS("Products"), REPOSTS("Reports"), REQUESTS("Requests"), SETUP("Setup"), TASKS("Tasks"),
		TESTINGPROGRAM("Testing Program"), TRADINGPARTNERGROUP("Trading Partner Groups"), WORKFLOWS("Workflows"), ICIXSETUP("ICIX Setup"),
		PRODUCT_SEARCH("Product Search"), ICIX_PRODUCT("ICIX Products");

		String value;

		AppLauncherItems(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

	}

	/**
	 * Method to click All Items wafle and search for the passed Item and click on it
	 *
	 * @param Item
	 */
	//Should this be on HomePage?
	public void clickAppLauncherItem(AppLauncherItems Item) {
		Log.info("Click App launcher Item: " + Item.getValue());
		waitUntilDisplayed(allItems);
		allItems.click();
		waitUntilDisplayed(allItemsSearch);
		allItemsSearch.sendKeys(Item.getValue());
		clickFirstMatchingText(allItemsResult, Item.getValue());
	}


	/**
	 * Common method to wait for presence of element located by specified locator.
	 *
	 * @param locator
	 * @return
	 */
	public WebElement waitForPresenceOfElemLocated(By locator) {
		return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
	}

	/**
	 * Common method to wait for visibility of specified element.
	 *
	 * @param element
	 * @return
	 */
	public WebElement waitForVisibilityOfElem(WebElement element) {
		return wait.until(ExpectedConditions.visibilityOf(element));
	}

	/**
	 * Common method to wait for visibility of element located by specified locator.
	 *
	 * @param locator
	 * @return WebElement
	 */
	public WebElement waitForVisibilityOfElemLocated(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Common method to wait for invisibility of element located by specified locator.
	 *
	 * @param locator
	 * @return boolean
	 */
	public boolean waitForInvisibilityOfElemLocated(By locator) {
		return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	/**
	 * Common method to wait for visibility of all elements specified.
	 *
	 * @param elementList
	 * @return
	 */
	public List<WebElement> waitForVisibilityOfAllElem(List<WebElement> elementList) {
		return wait.until(ExpectedConditions.visibilityOfAllElements(elementList));
	}

	/**
	 * Common method to wait for element to be enabled and clickable.
	 *
	 * @param element
	 * @return
	 */
	public WebElement waitForElemToBeClickable(WebElement element) {
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	/**
	 * Common method to wait for element located by specified locator to be enabled and clickable.
	 *
	 * @param locator
	 * @return
	 */
	public WebElement waitForElemToBeClickable(By locator) {
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	/**
	 * Common method to find the relative element in a list of elements starting
	 * with parent element as root.
	 * <br/><br/>
	 * Relative xpath should be of the format: <code>".//some/html/xpath"</code> e.g. <code>".//td[3]/div[1]/button/i"</code>.
	 *
	 * @param parentElement
	 * @param relativeXpath
	 * @return WebElement
	 */
	public WebElement findRelativeElem(WebElement parentElement, String relativeXpath) {
		return waitForVisibilityOfElem(parentElement).findElement(By.xpath(relativeXpath));
	}

	/**
	 * Common method to check if an element is enabled.
	 *
	 * @param element
	 * @return
	 */
	public boolean isElementEnabled(WebElement element) {
		return waitForVisibilityOfElem(element).isEnabled();
	}

	/**
	 * Common method to clear a given text box and enter text in it.
	 *
	 * @param element
	 * @param textToBeEntered
	 */
	public void enterInTextBox(WebElement element, String textToBeEntered) {
		waitForVisibilityOfElem(element).clear();
		element.sendKeys(textToBeEntered);
	}

	/**
	 * Common method to select given visible text from the given drop down.
	 *
	 * @param element
	 * @param visibleText
	 */
	public void selectByVisibleText(WebElement element, String visibleText) {
		Select dropDown = new Select(waitForVisibilityOfElem(element));
		dropDown.selectByVisibleText(visibleText);
		logger.info("Selected Text [" + element + "]: " + visibleText);
	}

	/**
	 * Common method to get text
	 *
	 * @param element
	 * @return
	 */
	public String getTextValue(WebElement element) {
		String textReceived = waitForVisibilityOfElem(element).getText();
		logger.info("Text Received : " + textReceived);
		return textReceived ;
	}

	/**
	 * Clicks an invisible button like edit or delete buttons appearing on mouse hover.
	 *
	 * @param buttonXpath
	 */
	public void clickInvisibleButton(String buttonXpath) {

		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		WebElement button = wait.until(
				ExpectedConditions.presenceOfElementLocated(
						By.xpath(buttonXpath)));
		jsExecutor.executeScript("arguments[0].click();", button);
	}

	/**
	 * Clicks an invisible button like edit or delete buttons appearing on mouse hover.
	 *
	 * @param button the hidden button WebElement
	 */
	public void clickInvisibleButton(WebElement button) {

		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		jsExecutor.executeScript("arguments[0].click();", button);
	}

	/**
	 * Returns a list of errors, if any, on the current page.
	 *
	 * @return List of errors if present, null otherwise
	 */
	public List<String> checkErrors() {

		List<String> errorList = new ArrayList<String>();
		try {
			for(WebElement errorElement : waitForVisibilityOfAllElem(errorElementList)) {
				errorList.add(errorElement.getText().trim().replaceAll("\n", ""));
			}
			logger.info("errorList.size() = " + errorList.size());
			logger.info("errorList = " + errorList);

		} catch (Exception e) {
			logger.info("No errors found on the page!");
			return null;
		}
		return errorList;
	}

	private void handleUnhandledAlertException(UnhandledAlertException uae) {
		try {
			logger.info("Encountered UnhandledAlertException: " + uae.getMessage());
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			System.out.println("Alert data: " + alertText);
			alert.dismiss();
		} catch (NoAlertPresentException nae) {
			logger.info("No alert present: " + nae.getMessage());
		}
	}

	public boolean elementExists(By locator) {

		boolean isUAEEncountered = false;

		try {
			elementExistsWait.until(ExpectedConditions.presenceOfElementLocated(locator));
		}
		catch (UnhandledAlertException uae) {
			isUAEEncountered = true;
			handleUnhandledAlertException(uae);
		}
		catch (Exception e) {
			logger.info("elementExists::Exception thrown::element does not exist");
			return false;
		}

		logger.info("Is UnhandledAlertException Encountered = " + isUAEEncountered);
		if(isUAEEncountered) {
			elementExists(locator);
		}

		logger.info("elementExists::Exception not thrown::element exists");
		return true;
	}

	public boolean elementExists(WebElement element) {
		try {
			elementExistsWait.until(ExpectedConditions.elementToBeClickable(element));
		} catch (Exception e) {
			logger.info("elementExists::Exception thrown::element does not exist");
			return false;
		}
		logger.info("elementExists::Exception not thrown::element exists");
		return true;
	}

	public WebElement findElement(String xpathToBeReplaced, String replacableItem, String itemToReplace) {
		String replacedXpath = xpathToBeReplaced.replace(replacableItem, itemToReplace);
		logger.info("replacedXpath = " + replacedXpath);
		return waitForVisibilityOfElemLocated(By.xpath(replacedXpath));
	}

	public List<WebElement> findElements(String xpathToBeReplaced, String replacableItem, String itemToReplace) {
		String replacedXpath = xpathToBeReplaced.replace(replacableItem, itemToReplace);
		logger.info("replacedXpath = " + replacedXpath);
		return driver.findElements(By.xpath(replacedXpath));
	}

	/**
	 * Scroll the element into view.
	 *
	 * @param locator By
	 */
	public void scrollElementIntoView(By locator) {
		WebElement elementToScrollTo = waitForVisibilityOfElemLocated(locator);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elementToScrollTo);
	}

	/**
	 * Scroll the element into view.
	 *
	 * @param element WebElement
	 */
	public void scrollElementIntoView(WebElement element) {
		WebElement elementToScrollTo = waitForVisibilityOfElem(element);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elementToScrollTo);
		sleep(1000);
	}

	/**
	 * Scroll window right by x pixels and down by y pixels.
	 *
	 * @param x
	 * @param y
	 */
	public void scrollBy(int x, int y) {
		logger.info("Scrolling window by (" + x + ", " + y + ")");
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("scroll("+ x +", " + y + ");");
		sleep(1000);
	}

	public void sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	/**
	 * <h1>Change URL<h1/>
	 * <p>Purpose: This method is used to change url</p>
	 *
	 * @throws Exception
	 */
	public void changeUrl() {
		url();
	}

	public static void url() {
		driver.get(baseUrl);
	}

	public void waitForElementToLoad(By locator){
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.elementToBeClickable(locator));
	}
	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean login) {
		isLogin = login;
	}

	public String getCurrentLoginUser() {
		return currentLoginUser;
	}

	public void setCurrentLoginUser(String currentLoginUser) {
		this.currentLoginUser = currentLoginUser;
	}

	public static void waitForPageLoadToComplete() throws Exception {
		waitForPageLoad();
	}

	public static void waitForPageLoad() {
		new WebDriverWait(driver, 30).until((ExpectedCondition<Boolean>) wd ->
				((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
	}
	/**
	 * <h1>Switch to lightning view <h1/>
	 * <p>Purpose:This method is used for switch to lightning view </p>
	 *
	 * @throws Exception
	 */
	public void SwitchToLightiningView() {
		try {
			waitForPageLoadToComplete();
			sleep(2000);
			waitForPageLoadToComplete();
			int size = userNavButton.size();
			if (tryNowButton.isDisplayed()){
				tryNowButton.click();
				waitForPageLoadToComplete();
			} else if (size > 0) {
				Log.info("Switching to Lightening view");
				drpSwitchToLightningById.click();
				waitUntilDisplayed(optionSwitchToLightning.get(0));
				clickFirstMatchingText(optionSwitchToLightning, "Switch to Lightning Experience");
				waitForPageLoadToComplete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void waitUntilDisplayed(WebElement element) {

		WebDriverWait wait = new WebDriverWait(driver, Time.HALF_MINUTE.getValue());
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public static boolean clickFirstMatchingText(List<WebElement> elements, String text) {
		if (isNotEmpty(elements)) {
			for (WebElement element : elements) {
				try {
					if (element.getText().toLowerCase().equals(text.toLowerCase())) {
						element.click();
						return true;
					}
				} catch (NullPointerException e) {
					System.out.println("Null element within list, skip it");
				} catch (WebDriverException e) {
					if (e.getMessage().toLowerCase().contains("not visible on the screen")) {
						element.click();
						System.out.println("Following exception thrown while clicking: " + e.getMessage());
					} else {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}

	public static <T> boolean isNotEmpty(Collection<T> collection) {
		return collection != null && !collection.isEmpty();
	}

	/**
	 * Enum Time: Define all the various constant time values to be used through out the framework e.g. for various wait times.
	 */
	public enum Time {
		ZERO(0),
		MINIMUM(1),
		MIN_PLUS(2),
		SHORT(5),
		AVERAGE(10),
		AVG_PLUS(20),
		HALF_MINUTE(30),
		MINUTE(60),
		LONG(120);

		private final int amtOfTime;

		Time(final int newAmtOfTime) {
			amtOfTime = newAmtOfTime;
		}

		public int getValue() {
			return amtOfTime;
		}
	}

	public static void SwitchToDefaultContent(int Interval) {
		try {
			System.out.println("Switching default");
			driver.switchTo().defaultContent();
			Thread.sleep(Interval);
		} catch (Exception e) {
			Log.error("There is an exception: " + e.toString());
		}
	}

	public static void RefreshPage() throws Exception {
		driver.navigate().refresh();
		waitForPageLoadToComplete();
	}

	/**
	 * <h1>Logout from Application<h1/>
	 * <p>Purpose: This method is used to logout from</p>
	 *
	 * @throws Exception
	 */
	public void logout() {
		Log.info("Logging out the user");
		try {
			LogoutUser();
			this.isLogin = false;
			this.setCurrentLoginUser("NA");
			waitForPageLoadToComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		changeUrl();
	}

	/**
	 * <h1>Logout User <h1/>
	 * <p>Purpose:This method is used for logout to application</p>
	 */
	public void LogoutUser() {
		try {
			RefreshPage();
			waitForPageLoadToComplete();
			SwitchToDefaultContent(10);
			do {
				try {
					imgProfile.click();
					waitUntilDisplayed(panelContent);
					waitUntilDisplayed(profileCard);
				} catch (Exception ex) {
					Log.error("Logout Imgae clicked but there is problem. " + ex.getMessage());
				}
			} while (logOutLink.isDisplayed());
			waitUntilDisplayed(profileCardName);
			logOutLink.click();
			sleep(5000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String GetUserData(String key) {
		Properties prop = new Properties();
		InputStream input = null;
		String projectDir = System.getProperty("user.dir") + "/src/test/resources/";
		try {
			input = new FileInputStream(projectDir + "users.properties");
			// load a properties file
			prop.load(input);
			// get the property value and print it out

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {

			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop.getProperty(key);
	}

	/**
	 * <h1>Get Login Credentials <h1/>
	 * <p>Purpose: This method is used to get get username, password, TP Name and required data to run scripts
	 * form Excel sheet (UserData.xlsx)
	 * <p>
	 * </p>
	 *
	 * @param
	 * @throws Exception
	 */

	public synchronized static void GetUserData() throws Exception {
		try {
//			String FS = File.separator;
//			String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "TestData" + FS + "UserData.xls";
			String testDataFilePath = "src/test/resources/TestData/UserData.xls";
			File f = new File(testDataFilePath);
			Connection connection = fillo.getConnection(f.getAbsolutePath());
			String strQuery = String.format("Select * from Admin_Users");
			Recordset recordset = connection.executeQuery(strQuery);

			while (recordset.next()) {
				UsersTestData usersTestData = new UsersTestData();
				//Get data from Excel and Set for Requester_Admin object
				usersTestData.setUserId(recordset.getField("UserName"));
				usersTestData.setPassword(recordset.getField("Password"));
				usersTestData.setName(recordset.getField("Name"));
				usersTestData.setIcixId(recordset.getField("IcixID"));
				usersTestData.setTPRelationshipTag(recordset.getField("TPRelationshipTag"));
				usersTestData.setTPRelationshipStatus(recordset.getField("TPRelationshipStatus"));
				usersTestData.setTPRelationshipType(recordset.getField("TPRelationshipType"));
				usersTestData.setUrl(recordset.getField("Url"));

				Users.adminUsersTestDataMap.put(recordset.getField("UserType"), usersTestData);
			}
			recordset.close();
			strQuery = String.format("Select * from Users");
			recordset = connection.executeQuery(strQuery);

			while (recordset.next()) {
				UsersTestData usersTestData = new UsersTestData();
				usersTestData.setUserId(recordset.getField("UserName"));
				usersTestData.setPassword(recordset.getField("Password"));

				Users.spuUsersTestDataMap.put(recordset.getField("UserType"), usersTestData);
			}
			recordset.close();
			connection.close();
		} catch (FilloException e) {
			Log.info("Error in user test data reading");
			throw new Exception(String.format("User Test data not found ->" + e.getMessage()));
		}
	}//End of function

	public static String GetTimeStamp() {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
		return timeStamp;
	}

	static Fillo fillo = new Fillo();

	/**
	 * <h1>Get Data form Excel Sheet <h1/>
	 * <p>Purpose: This method is used to get test date for execute test cases form excel file after passing
	 * module name as sheet name and testId name as testcaseid as parameter
	 * </p>
	 *
	 * @param module,testId
	 * @throws Exception
	 */
	public synchronized static Recordset GetTestData(String module, String testId) throws Exception {
		try {
			String FS = File.separator;
			String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "TestData" + FS + "TestData.xls";
			File f = new File(testDataFilePath);
			Connection connection = fillo.getConnection(f.getAbsolutePath());
			String strQuery = String.format("Select * from %s Where TestID='%s'", module, testId);
			Recordset recordset = connection.executeQuery(strQuery);
			connection.close();
			return recordset;
		} catch (FilloException e) {
			Log.info("Error in test data reading");
			//Throw the exception To fail the test
			e.printStackTrace();
			throw new Exception(String.format("Test data not found for Module: '%s' and TestId: '%s'", module, testId));
		}
	}//End function
}
