package pageObjects;

import Utils.Log;
import Utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

public class BasePage extends Utils {

    private static Logger logger = LogManager.getLogger(BasePage.class);

    public static String baseUrl = "https://login.salesforce.com";

    protected static final int WAIT_TIME = 30;
    protected static final int POLL_INTERVAL = 2;
    protected static final int IMPLICIT_WAIT_TIME = 8;
    protected static final int ELEMENT_EXISTS_WAIT_TIME = 10;

    private boolean isLogin = false;
    private String currentLoginUser = "NA";

    public String currentRequestName = null;
    public String currentProduct = null;
    public String currentTradingPartner = null;
    public String currentRequestType = null;

    public void navigateToPage(String pageURL) {
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

    @FindBy(xpath = "//one-app-launcher-menu-item//span")
    public List<WebElement> allItemsResult;

    @FindBy(xpath = "//button[contains(.,'View All')]")
    public WebElement allItemsViewAll;

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

    @FindBy(xpath = "//*[contains(@class, 'alert-danger')]")
    private static List<WebElement> errorElementList;

    @FindBy(css = ".toastContent .toastMessage")
    public WebElement toastMessage;

    /**
     * Constructor to initialize WebDriver and FluentWait objects using default
     * wait time and poll interval. Also sets the implicit wait and initializes
     * page elements using PageFactory.
     *
     * @param driver
     */
    public BasePage(WebDriver driver) {
        driver = getDriver();
        PageFactory.initElements(driver, this);
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
        clickButton(allItems);
        waitUntilDisplayed(allItemsSearch);
        enterText(allItemsSearch, Item.getValue());
//        driver.switchTo().defaultContent();
        Assert.assertTrue(clickFirstMatchingText(allItemsResult, Item.getValue()));
    }

    /**
     * Returns a list of errors, if any, on the current page.
     *
     * @return List of errors if present, null otherwise
     */
    public List<String> checkErrors() {

        List<String> errorList = new ArrayList<String>();
        try {
            for (WebElement errorElement : waitForVisibilityOfAllElem(errorElementList)) {
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

    /**
     * <h1>Switch to lightning view <h1/>
     * <p>Purpose:This method is used for switch to lightning view </p>
     *
     * @throws Exception
     */
    public void switchToLightning() {
        try {
            waitForPageLoad();
            int size = userNavButton.size();
            if (tryNowButton.isDisplayed()) {
                clickButton(tryNowButton);
                waitForPageLoad();
            } else if (size > 0) {
                Log.info("Switching to Lightning view");
                clickElement(drpSwitchToLightningById, false, false);
                waitUntilDisplayed(optionSwitchToLightning.get(0));
                clickFirstMatchingText(optionSwitchToLightning, "Switch to Lightning Experience");
                waitForPageLoad();
            }
        } catch (Exception e) {
            Log.fail("Unable to switch to Lightning - " + e.getMessage());
        }
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
            waitForPageLoad();
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
            waitForPageLoad();
            SwitchToDefaultContent();
            do {
                try {
                    clickElement(imgProfile, false, false);
                    waitUntilDisplayed(panelContent);
                    waitUntilDisplayed(profileCard);
                } catch (Exception ex) {
                    Log.error("Logout Imgae clicked but there is problem. " + ex.getMessage());
                }
            } while (logOutLink.isDisplayed());
            waitUntilDisplayed(profileCardName);
            clickElement(logOutLink, true, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}