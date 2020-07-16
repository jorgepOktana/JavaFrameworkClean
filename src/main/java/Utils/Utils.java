package Utils;

import TestData.Users;
import TestData.UsersTestData;
import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import pageObjects.BasePage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Utils {

    private static Logger logger = LogManager.getLogger(BasePage.class);

    //Webdriver Init
    protected static WebDriver driver = null;
    public static String currentBrowser;
    private static String env;
    protected Wait<WebDriver> wait = null;
    protected Wait<WebDriver> elementExistsWait = null;
    public String mainHandle = null;

    String currentTabHandle;
    String newTabHandle;

    protected static final int WAIT_TIME = 30;
    protected static final int POLL_INTERVAL = 2;
    protected static final int IMPLICIT_WAIT_TIME = 8;
    protected static final int ELEMENT_EXISTS_WAIT_TIME = 10;

    /**
     * Initialize WebDriver and FluentWait objects using default
     * wait time and poll interval. Also sets the implicit wait and initializes
     * page elements using PageFactory.
     *
     */
    public WebDriver getDriver() {
        if (driver == null) {
            try {
                GetBrowser();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (currentBrowser.toLowerCase().contains("chrome")) {
                driver = new ChromeDriver();
            }
        }
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
        return driver;
    }

    public void getMainWindow(){
        currentTabHandle = driver.getWindowHandle();
    }

    public String setCurrentWindow(){
        return mainHandle;
    }

    public void switchToIFrame(WebElement iframe) {
        waitUntilDisplayed(iframe);
        try {
            driver.switchTo().frame(iframe);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void switchToLastWindow() {
        sleepSeconds(10);
        newTabHandle = driver.getWindowHandles()
                .stream()
                .filter(handle -> !handle.equals(currentTabHandle ))
                .findFirst()
                .get();
        driver.switchTo().window(newTabHandle);
    }

    public void switchToMainWindow() {
        sleepSeconds(5);
        driver.switchTo().window(currentTabHandle);
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
        waitUntilDisplayed(parentElement);
        return driver.findElement(By.xpath(relativeXpath));
    }

    /**
     * Common method to check if an element is enabled.
     *
     * @param element
     * @return
     */
    public boolean isElementEnabled(WebElement element) {
        waitUntilDisplayed(element);
        return element.isEnabled();
    }


    /**
     * Common method to select given visible text from the given drop down.
     *
     * @param element
     * @param visibleText
     */
    public void selectByVisibleText(WebElement element, String visibleText) {
        waitUntilDisplayed(element);
        Select dropDown = new Select(element);
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
        waitUntilDisplayed(element);
        String textReceived = element.getText();
        logger.info("Text Received : " + textReceived);
        return textReceived;
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
        } catch (UnhandledAlertException uae) {
            isUAEEncountered = true;
            handleUnhandledAlertException(uae);
        } catch (Exception e) {
            logger.info("elementExists::Exception thrown::element does not exist");
            return false;
        }

        logger.info("Is UnhandledAlertException Encountered = " + isUAEEncountered);
        if (isUAEEncountered) {
            elementExists(locator);
        }

        logger.info("elementExists::Exception not thrown::element exists");
        return true;
    }

    public boolean elementExists(WebElement element) {
        try {
            elementExistsWait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            logger.info("Element does not exist");
            return false;
        }
        logger.info("Exception not thrown::element exists");
        return true;
    }

    public WebElement findElement(String xpathToBeReplaced, String replacableItem, String itemToReplace) {
        String replacedXpath = xpathToBeReplaced.replace(replacableItem, itemToReplace);
        logger.info("replacedXpath = " + replacedXpath);
        WebElement replaced = driver.findElement(By.xpath(replacedXpath));
        waitUntilDisplayed(replaced);
        return replaced;
    }

    public List<WebElement> findElements(String xpathToBeReplaced, String replacableItem, String itemToReplace) {
        String replacedXpath = xpathToBeReplaced.replace(replacableItem, itemToReplace);
        logger.info("replacedXpath = " + replacedXpath);
        return driver.findElements(By.xpath(replacedXpath));
    }

    /**
     * Scroll the element into view.
     *
     * @param element WebElement
     */
    public void scrollElementIntoView(WebElement element) {
        waitUntilDisplayed(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Scroll window right by x pixels and down by y pixels.
     *
     * @param x
     * @param y
     */
    public void scrollBy(int x, int y) {
        logger.info("Scrolling window by (" + x + ", " + y + ")");
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("scroll(" + x + ", " + y + ");");
        sleep(1000);
    }

    public void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

    public void waitForElementToLoad(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    //New Utils Methods
    //Wait methods

    /**
     * Common method to wait for page load
     */
    public static void waitForPageLoad() {
        new WebDriverWait(driver, 30).until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Common method to wait for visibility of specified element.
     * @param element
     * @return
     */
    public static void waitUntilDisplayed(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Time.HALF_MINUTE.getValue());
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Common method to wait until element is enabled
     */
    protected static void waitUntilEnabled(WebElement element) {
        for (int i = 0; i < 60; i++) {
            try {
                if (element.isEnabled()) {
                    break;
                }
            } catch (StaleElementReferenceException e) {
                System.out.println("Stale element referenced, refreshing");
            } catch (NullPointerException e) {
                throw new NullPointerException("WaitUntilEnabled called on a null element: " + element);
            }
            sleepSeconds(Time.MINIMUM.getValue());
        }
    }

    protected void waitUntilDisappears(WebElement element) {
        try {
            getWebCSSSelector(element);
        } catch (NoSuchElementException e) {
            return;
        }
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(30, TimeUnit.SECONDS)
                .pollingEvery(2, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

        wait.until(driver -> !element.isDisplayed());
    }

    private static String getWebCSSSelector(WebElement element) {
        final String JS_BUILD_CSS_SELECTOR =
                "for(var e=arguments[0],n=[],i=function(e,n){if(!e||!n)return 0;f" +
                        "or(var i=0,a=e.length;a>i;i++)if(-1==n.indexOf(e[i]))return 0;re" +
                        "turn 1};e&&1==e.nodeType&&'HTML'!=e.nodeName;e=e.parentNode){if(" +
                        "e.id){n.unshift('#'+e.id);break}for(var a=1,r=1,o=e.localName,l=" +
                        "e.className&&e.className.trim().split(/[\\s,]+/g),t=e.previousSi" +
                        "bling;t;t=t.previousSibling)10!=t.nodeType&&t.nodeName==e.nodeNa" +
                        "me&&(i(l,t.className)&&(l=null),r=0,++a);for(var t=e.nextSibling" +
                        ";t;t=t.nextSibling)t.nodeName==e.nodeName&&(i(l,t.className)&&(l" +
                        "=null),r=0);n.unshift(r?o:o+(l?'.'+l.join('.'):':nth-child('+a+'" +
                        ")'))}return n.join(' > ');";

        return (String) ((JavascriptExecutor) driver).executeScript(JS_BUILD_CSS_SELECTOR, element);
    }

    //Common action methods

    /**
     * Common method to wait until element is displayed, enabled and then click on it
     */
    public static void clickButton(WebElement button) {
        waitUntilDisplayed(button);
        waitUntilEnabled(button);
        assertTrue(button.isEnabled());
        button.click();
    }

    public static void clickElement(WebElement webElement, boolean addSleep, boolean scrollToClick) {
        boolean clicked = false;
        for (int i = 0; i < 30; i++) {
            try {
                assertNotNull(webElement);
                if (scrollToClick && !(webElement.isDisplayed())) {
                    // Scroll to make the element visible
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", webElement);
                    sleepSeconds(Time.AVERAGE.getValue());
                }
                waitUntilDisplayed(webElement);
//                clickUsingJs(webElement);
                webElement.click();
                if (addSleep) {
                    sleepSeconds(Time.MINIMUM.getValue());
                }
                clicked = true;
            } catch (StaleElementReferenceException e) {
                sleepSeconds(Time.MINIMUM.getValue());
                if (i == 29) {
                    throw new StaleElementReferenceException("Cannot click element after thirty seconds");
                }
            }
            if (clicked) {
                break;
            }
        }
    }

    /**
     * Common method to clear a given text box and enter text in it.
     *
     * @param editText
     * @param text
     */
    public static void enterText(WebElement editText, String text) {
        for (int i = 0; i < 2; i++) {
            try {
                waitUntilDisplayed(editText);
                editText.clear();
                editText.sendKeys(text);
                break;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                System.out.println("Following exception thrown while entering text '" + text + "': " + e.getMessage());
            }
        }
    }

    /**
     * Common method used to click the first matching text-element on a list of elements
     *
     * @param elements, list of WebElements
     * @param text,     text to search and click
     */
    public boolean clickFirstMatchingText(List<WebElement> elements, String text) {
        if (isNotEmpty(elements)) {
            for (WebElement element : elements) {
                waitUntilDisplayed(element);
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

    public boolean clickFirstMatchingTextJS(List<WebElement> elements, String text) {
        if (isNotEmpty(elements)) {
            for (WebElement element : elements) {
                waitUntilDisplayed(element);
                try {
                    if (element.getText().toLowerCase().equals(text.toLowerCase())) {
                        clickUsingJs(element);
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

    public static void clickUsingJs(WebElement wb){
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript("arguments[0].click();", wb);
    }

    public static boolean clickFirstContainsText(List<WebElement> elements, String text) {
        if (isNotEmpty(elements)) {
            for (WebElement element : elements) {
                if (element.getText() == null || element.getText().isEmpty()) {
                    continue;
                } else if (element.getText().toLowerCase().contains(text.toLowerCase())) {
//                    clickUsingJs(element);
                    element.click();
                    return true;
                }
            }
        }
        return false;
    }

    protected static void clickFirstContainsTextParent(List<WebElement> elements, String text, List<WebElement> parent) {
        if (isNotEmpty(elements)) {
            int i = 0;
            for (WebElement element : elements) {
                if (element.getText().toLowerCase().contains(text.toLowerCase())) {
                    parent.get(i).click();
                    return;
                }
                i++;
            }
        }
    }

    protected static void clickFirstMatchingTextChild(List<WebElement> elements, String text, List<WebElement> childElement) {
        if (isNotEmpty(elements)) {
            int i = 0;
            for (WebElement element : elements) {
                if (element.getText().toLowerCase().equals(text.toLowerCase())) {
                    childElement.get(i).click();
                    return;
                }
                i++;
            }
        }
    }

    public static boolean clickFirstContainsTextChild(List<WebElement> elements, String text, List<WebElement> childElement) {
        if (isNotEmpty(elements)) {
            int i = 0;
            for (WebElement element : elements) {
                if (element.getText().toLowerCase().contains(text.toLowerCase())) {
                    childElement.get(i).click();
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    public static boolean checkIfMatchingText(List<WebElement> elements, String text) {
        if (isNotEmpty(elements)) {
            for (WebElement element : elements) {
                try {
                    if (element.getText() == null) {
                        continue;
                    } else if (element.getText().toLowerCase().equals(text.toLowerCase())) {
                        return true;
                    }
                } catch (NullPointerException | NoSuchElementException | StaleElementReferenceException e) {
                    System.out.println("Exception thrown while searching for text: " + text);
                }
            }
        }
        return false;
    }

    public static boolean checkIfContainingText(List<WebElement> elements, String text) {
        if (isNotEmpty(elements)) {
            for (WebElement element : elements) {
                try {
                    if (element.getText() == null) {
                        continue;
                    } else if (element.getText().toLowerCase().contains(text.toLowerCase())) {
                        return true;
                    }
                } catch (NullPointerException | NoSuchElementException | StaleElementReferenceException e) {
                    System.out.println("Exception thrown while searching for text: " + text);
                }
            }
        }
        return false;
    }

    public static boolean isElementPresent(String findByMethod, String selectorString) {
        setImplicitWaitTime(Time.MINIMUM.getValue());
        boolean toReturn;
        try {
            switch (findByMethod.toLowerCase()) {
                case "id":
                    toReturn = driver.findElement(By.id(selectorString)).isDisplayed();
                    break;
                case "cssselector":
                    toReturn = driver.findElement(By.cssSelector(selectorString)).isDisplayed();
                    break;
                case "classname":
                    toReturn = driver.findElement(By.className(selectorString)).isDisplayed();
                    break;
                case "xpath":
                    toReturn = driver.findElement(By.xpath(selectorString)).isDisplayed();
                    break;
                case "tagname":
                    toReturn = driver.findElement(By.tagName(selectorString)).isDisplayed();
                    break;
                default:
                    throw new IllegalArgumentException("Please define a valid findByMethod");
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            toReturn = false;
        }
        setImplicitWaitTime(Time.HALF_MINUTE.getValue());
        return toReturn;
    }

    /**
     * Method setImplicitWaitTime: This method is used to set implicit wait time for current driver being used to a given value in seconds.
     *
     * @param seconds : seconds to set the implicit wait time to in int.
     */
    protected static void setImplicitWaitTime(int seconds) {
            driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
    }

    /**
     * Common method to validate a collection is not empty
     */
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

    public static void sleepSeconds(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000.0));
        } catch (InterruptedException e) {
            System.out.println("Sleep was interrupted");
        }
    }

    public static void SwitchToDefaultContent() {
        try {
            System.out.println("Switching default");
            driver.switchTo().defaultContent();
        } catch (Exception e) {
            Log.error("There is an exception: " + e.toString());
        }
    }

    public static void RefreshPage() throws Exception {
        driver.navigate().refresh();
        waitForPageLoad();
    }

    public static String getUserData(String key) {
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

    public synchronized static void getUserData() throws Exception {
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

    public static String getTimeStamp() {
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

    /**
     * <h1> Get Current URL<h1/>
     * <p>Purpose:This method is used for get current url </p>
     */
    public static String GetCurrentUrl() {
        if (driver != null) {
            return driver.getCurrentUrl();
        } else
            return "";
    }

    /**
     * <h1>Get Browser Data form Excel Sheet <h1/>
     * <p>Purpose: This method is used to get test data.
     * </p>
     *
     * @throws Exception
     */
    public synchronized static void GetBrowser() throws Exception {
        try {
            String FS = File.separator;
            String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "TestData" + FS + "UserData.xls";
            File f = new File(testDataFilePath);
            Connection connection = fillo.getConnection(f.getAbsolutePath());

            String strQuery = String.format("Select * from Browser");
            Recordset recordset = connection.executeQuery(strQuery);
            while (recordset.next()) {
                currentBrowser = recordset.getField("Browser");
            }
            recordset.close();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.info("Error in user test data reading");
            throw new Exception(String.format("Browser Test data not found ->" + ex.getMessage()));
        }
    }

    /**
     * Method to copy file from src to dest
     */
    protected static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
