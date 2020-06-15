package Utils;

import com.codeborne.selenide.*;
import Locators.GlobalRepo;
import Locators.LoginOutRepo;
import Locators.ProductRepo;
import Locators.RequestRepo;
import Modules.ICIX_Common;
import Modules.LoginOut;
import Modules.PageTabs;
import Modules.Request;
import Start.Start;
import TestData.LoginOutTestData;
import Urls.Urls;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static Modules.ICIX_Common.GetButtonOfOpenedModalDialog;
import static Modules.ICIX_Common.GetOpendModalDialog;
import static Utils.TestBase.*;
import static java.lang.Thread.sleep;

/**
 * @author smartData
 * <h1>Common Functions</h1>
 * <p>Purpose: This class is for common function</p>
 * It is used to handle all the common functionalities which can be used in other classes
 */
public class Common extends Start {
    public static final long DEFAULT_WAIT = 90000;
    public static final long MEDIUM_WAIT_60S = 60000;
    public static final long SMALL_WAIT_30S = 30000;
    public static final long DEFAULT_IMPLICIT_WAIT_IN_SEC = 15;
    public static SelenideElement currentOpenedModel=null;

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
     * <h1>Search Trading partner <h1/>
     * <p>Purpose:This method is used for browser initialization</p>
     */
    public static void Init() throws Exception {
        InitilizeBrowser();
    }

    /**
     * <h1>Login User <h1/>
     * <p>Purpose:This method is used for login to application</p>
     *
     * @param UserName,Password
     * @throws Exception
     */
    // already moved to
    public static void LoginUser(String UserName, String Password) throws Exception {
        Log.info("Start Log in to application");
        Log.info("Enter user name");
        FindAnElement(LoginOutRepo.txtUsername).sendKeys(UserName);
        //ClearAndSendKeys(LoginOutRepo.txtUsername,UserName,"Enter user name");
        Log.info("Enter password");
        FindAnElement(LoginOutRepo.txtPassword).sendKeys(Password);
        //ClearAndSendKeys(LoginOutRepo.txtPassword, Password,"Enter password");
        ClickElement(By.id("Login"), "Login button");
        sleep(5000);
        waitForPageLoadToComplete();
        boolean isLogin = $(LoginOutRepo.txtUsername).is(hidden);

        //Verify if Change password screen appear
        if (isLogin) {
            if ($("#newpassword").isDisplayed()) {
                Log.error("Salesforce message: " + $("#header").text());
                throw new TestStepFailException("Login Failed> SALESFORCE CHANGE PASSWORD WINDOW APPEARED");
            }
        }

        if (!isLogin) {
            throw new TestStepFailException("Login Failed");
        }
        waitForPageLoadToComplete();
        sleep(15000);

    }

    /**
     * <h1>Logout User <h1/>
     * <p>Purpose:This method is used for logout to application</p>
     */
    public static void LogoutUser() {
        try {
            sleep(2000);
            RefreshPage();
            sleep(3000);
            waitForPageLoadToComplete();
            SwitchToDefaultContent(10);
            do {
                try {
                    ClickElement(LoginOutRepo.imgProfile, "Image Profile");
                    sleep(8000);
                    $(".panel-content")
                            .waitUntil(visible, 60000);
                    $(".panel-content .profile-card-toplinks").waitUntil(visible, 60000);
                } catch (Exception ex) {
                    Log.error("Logout Imgae clicked but there is problem. " + ex.getMessage());
                }

            } while (!$(LoginOutRepo.lnkLogout).isDisplayed());
            $(".profile-card-name").waitUntil(Condition.appear, DEFAULT_WAIT);
            ClickElement(LoginOutRepo.lnkLogout, "Link Logout");
            sleep(5000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * <h1>Verify Element Exists <h1/>
     * <p>Purpose:This method is used to verify is element exists </p>
     *
     * @param by
     * @throws Exception
     */

    public static boolean IsElementExists(By by) {
        try {
            sleep(2000);
            return $(by).exists();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * <h1>Switch to lightning view <h1/>
     * <p>Purpose:This method is used for switch to lightning view </p>
     *
     * @throws Exception
     */
    public static void SwitchToLightiningView() {
        try {
            waitForPageLoadToComplete();
            sleep(2000);
            waitForPageLoadToComplete();
            int size = $$(By.xpath("//div[@id='userNavButton']")).size();

            if ($x(".//input[@id='lexTryNow']").isDisplayed()) {
                $x(".//input[@id='lexTryNow']").click();
                waitForPageLoadToComplete();
            } else if (size > 0) {
                Log.info("Switching to Lightening view");
                ClickElement(LoginOutRepo.drpSwitchToLighteningById, "Dropdown Switch to lightening");
                $(By.xpath("//*[@id='userNav-menuItems']")).waitUntil(Condition.appear, DEFAULT_WAIT);
                $$(LoginOutRepo.OptionSwitchToLightening)
                        .filter(text("Switch to Lightning Experience"))
                        .first()
                        .waitUntil(appear, DEFAULT_WAIT).click();
                waitForPageLoadToComplete();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    /**
     * <h1>Click on Element <h1/>
     * <p>Purpose:This method is used to perform click functionality using By locator  </p>
     *
     * @param Element,detail
     * @throws Exception
     */

    public static void ClickElement(By Element, String detail) throws Exception {
        try {
            Log.info("Clicking on:" + detail);
            $(Element)
                    .waitUntil(appear, DEFAULT_WAIT)
                    .click();
        } catch (Exception e) {
            Log.error("Error on:" + detail);
            Log.error("There is exception: " + e.getMessage());
            throw e;

        }
    }

    /**
     * <h1>Click on Element <h1/>
     * <p>Purpose:This method is used to perform click functionality </p>
     *
     * @param Element,detail
     * @throws Exception
     */


    public static void ClickElement(WebElement Element, String detail) {
        try {
            Log.info("Clicking on:" + detail);
            $(Element)
                    .waitUntil(appear, DEFAULT_WAIT)
                    .click();
//			WaitTool.waitForElementToBeClickable(WebDriverRunner.getWebDriver(), Element, timeOutInSeconds);
//			Element.click();
        } catch (Exception e) {
            Log.error("There is an exception: " + e.toString());
        }
    }

    /**
     * <h1>Find All Elements<h1/>
     * <p>Purpose:This method is used to get all web elements </p>
     *
     * @param Element,timeOutInSeconds
     * @throws Exception
     */


    public static List<WebElement> FindAllElements(By Element) throws Exception {
        List<WebElement> TempElement = null;
        try {
            WaitTool.waitForElementsPresentAndDisplay(Element);
            TempElement = getWebDriver().findElements(Element);
            return TempElement;
        } catch (Exception e) {
            Log.error("There is exception: " + e.toString());
            throw e;
        }

    }

    /**
     * <h1>Find Elements By ExactText<h1/>
     * <p>Purpose:This method is used to find element by exacts text (String text) </p>
     *
     * @param text
     */


    public static WebElement FindAnElementByExactText(String text) {
        return $(byText(text)).waitUntil(appear, DEFAULT_WAIT).getWrappedElement();
    }

    public static WebElement FindAnElementByTextContains(String text) {
        return $(withText(text)).waitUntil(appear, DEFAULT_WAIT).getWrappedElement();
    }

    public static void HighlightElement(By by) {
        WebElement elm = $(by).getWrappedElement();
        // draw a border around the found element
        executeJavaScript("arguments[0].style.backgroundColor='#80ff80'", elm);
    }

    public static void HighlightElement(WebElement elm) {
        // draw a border around the found element
        executeJavaScript("arguments[0].style.backgroundColor='#80ff80'", elm);
    }

    public static WebElement FindAnElement(By Element) {
        WebElement TempElement = null;
        try {
            TempElement = $(Element)
                    .waitUntil(appear, DEFAULT_WAIT).getWrappedElement();
        } catch (Exception e) {
            Log.error("There is an exception: " + e.toString());
            throw e;
        }
        return TempElement;
    }

    /**
     * <h1>Switch To Frame<h1/>
     * <p>Purpose:This method is used for switch control to another frame</p>
     *
     * @throws Exception
     */

    public static void SwitchToFrame() throws Exception {
        try {
            List<WebElement> NewRequestFrame = Common.FindAllElements(GlobalRepo.frame);
            int size = NewRequestFrame.size();
            getWebDriver().switchTo().frame(size - 1);
            Log.info("Switching to frame: " + size);
            Thread.sleep(2000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.error("Problem in Switching to frame ");
            Log.error(e.getMessage());
            throw e;
        }

    }

    /**
     * <h1>Switch To Active Frame<h1/>
     * <p>Purpose:This method is used for switch control to active frame</p>
     *
     * @param frames,interval
     * @throws Exception
     */

    public static void SwitchToFrame(WebElement frames, int interval) throws Exception {
        try {
            switchTo().frame(frames);
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            Log.error("Problem in Switching to frame ");
            Log.error(e.getMessage());
            throw e;
        }

    }

    /**
     * <h1>Clear and Send Key<h1/>
     * <p>Purpose:This method is used for clear present element and send new value into text field</p>
     *
     * @param Element,KeysToSend,Detail
     * @throws Exception
     */
    public static void ClearAndSendKey(By Element, String KeysToSend[], String Detail) throws Exception {
        try {
            Log.info(Detail);
            WebElement element = $(Element)
                    .waitUntil(appear, DEFAULT_WAIT)
                    .waitUntil(enabled, DEFAULT_WAIT);
            element.clear();
            element.sendKeys(KeysToSend);

        } catch (Exception e) {
            Log.error("There is an exception in setting a text value: " + e.toString());
            throw e;
        }
    }

    /**
     * <h1>Switch Control<h1/>
     * <p>Purpose:This method is used for switch control from one windows to another windows</p>
     */
    public static void switchWindow() {
        // Store the current window handle
        String winHandleBefore = getWebDriver().getWindowHandle();

        // Perform the click operation that opens new window

        // Switch to new window opened
        for (String winHandle : getWebDriver().getWindowHandles()) {
            getWebDriver().switchTo().window(winHandle);
        }

        // Perform the actions on new window

        // Close the new window, if that window no more required
        getWebDriver().close();

        // Switch back to original browser (first window)
        getWebDriver().switchTo().window(winHandleBefore);

        // Continue with original browser (first window)
    }

    public static void ClearAndSendKeys(By Element, String KeysToSend, String Detail) throws Exception {
        try {
            sleep(3000);
            Log.info("Input " + Detail);
            WebElement elm = $(Element)
                    .waitUntil(appear, DEFAULT_WAIT)
                    .waitUntil(enabled, DEFAULT_WAIT);
            elm.clear();
            elm.sendKeys(KeysToSend);

        } catch (Exception e) {
            Log.error("Error in setting a text value: " + e.toString());
            throw e;
        }
    }

    public static void sendKeys(By Element, Keys enter, String Detail) {
        try {
            Log.info(Detail);
            WebElement element = $(Element)
                    .waitUntil(appear, DEFAULT_WAIT)
                    .waitUntil(enabled, DEFAULT_WAIT);
            element.sendKeys(enter);

        } catch (Exception e) {
            Log.error("Error in send Keys" + Element.toString());
            Log.error(e.getMessage());
            throw e;

        }
    }

    /**
     * <h1>Search Anything<h1/>
     * <p>Purpose:This method is used for search request and wait for request to come and then it will click on Request link</p>
     *
     * @param moduleName,KeywordToSearch
     * @throws Exception
     */
    public static void GlobalSearchs(AppLauncherItems moduleName, String KeywordToSearch) throws Exception {
        RefreshPage();
        switchTo().defaultContent();
        moveToICIXApp();
        long startTime = System.currentTimeMillis();
        Log.info(String.format("Search '%s' from module '%s'", KeywordToSearch, moduleName.getValue()));
        try {
            Thread.sleep(6000);
            WebElement globalSearchBox = Common.FindAnElement(GlobalRepo.txtGlobalSrc); //driver.findElement(GlobalRepo.txtGlobalSrc);
            globalSearchBox.click();
            Log.info("Global search text box clicked.");
            globalSearchBox.clear();
            globalSearchBox.sendKeys(KeywordToSearch);
            sleep(1000);
            $(globalSearchBox).pressEnter();
            sleep(2000);

            SelenideElement left_nav = $(byText("Search Results")).closest("nav");
            left_nav.$(".scopesBlock__moreButton");
            if (!left_nav.$(".scopesBlock__moreButton").text().equalsIgnoreCase("Show Less")) {
                left_nav.$(".scopesBlock__moreButton").click();
                sleep(2000);
            }//end if

            //Load menu items
            $$(By.xpath(".//*[@id='allItemsList']//li/a")).size();

            WaitUntilResultsPresent(moduleName.getValue(), KeywordToSearch);

            long endTime = System.currentTimeMillis();
            double seconds = (endTime - startTime) / 1000.0;
            logTestStep("Total time taken to reflect the request: " + seconds + "sec");
            sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void moveToICIXApp() throws TestStepFailException, InterruptedException {
        while (!$(".oneAppNavContainer").isDisplayed()) {
            Log.info("ICIX Application is not default in salesforce lets make it");
            if (!$(".oneAppNavContainer").isDisplayed()) {
                clickAppLauncher();
                sleep(2000);
                $$("#cards li a").findBy(exactText("ICIX")).click();
                ICIX_Common.WaitForSpinner();
            }
        }
    }

    public static void GlobalSearch(AppLauncherItems moduleName, String KeywordToSearch) throws Exception {
        sleep(4000);
        WebDriverRunner.getWebDriver().navigate().refresh();
        switchTo().defaultContent();
        ICIX_Common.WaitForSpinner();
        moveToICIXApp();
        sleep(3000);
        long startTime = System.currentTimeMillis();
        Log.info(String.format("Search '%s' from module '%s'", KeywordToSearch, moduleName.getValue()));
        try {
            Thread.sleep(5000);
            WebElement globalSearchBox = Common.FindAnElement(GlobalRepo.txtGlobalSrc); //driver.findElement(GlobalRepo.txtGlobalSrc);
            globalSearchBox.click();
            Log.info("Global search text box clicked.");
            $(globalSearchBox).setValue(KeywordToSearch);
            sleep(1000);
            $(globalSearchBox).pressEnter();
            sleep(4000);
            ICIX_Common.WaitForSpinner();
            $(".forceSearchResultsRegion").waitUntil(visible, DEFAULT_WAIT);
            SelenideElement left_nav = $(byText("Search Results")).closest("nav");
            //SelenideElement moreButton =left_nav.$(".scopesBlock__moreButton"); //For Summer 19
            SelenideElement moreButton=  $x("//span[@class='slds-text-not-selected']");
            //SelenideElement moreButton = left_nav.$(".moreButton");
            if (!moreButton.text().equalsIgnoreCase("Show Less")) {
                moreButton.click();
                sleep(2000);
            }//end if

            //Load menu items
            $$(By.xpath(".//*[@id='allItemsList']//li/a")).size();
            WaitUntilResultsPresent1(moduleName.getValue(), KeywordToSearch);
            long endTime = System.currentTimeMillis();
            double seconds = (endTime - startTime) / 1000.0;
            logTestStep("Total time taken to reflect the request: " + seconds + "sec");
            sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * <h1>Click Partial link<h1/>
     * <p>Purpose:This method is used for clicking on a link that contains given text.
     * </p>
     *
     * @param LinkToClick
     * @throws Exception
     */

    public static void clickPartialLinkText(String LinkToClick) throws Exception {

        ClickElement((By.partialLinkText(LinkToClick)), "Click Link " + LinkToClick);
    }

    //TBD
    public static void ClickLink(String LinkToClick) {
        $(byLinkText(LinkToClick)).waitUntil(appear, DEFAULT_WAIT).click();
    }

    public static void ClickAppLauncherItem(String Item) throws Exception {
        sleep(1000);
        String xpath = String.format("//li[contains(@class,'oneAppLauncherItem')]//a[@title='%s']", Item);
        TestBase.logTestStep("Click App Launcher Item: " + Item);
        ClickElement(By.xpath(xpath), "App Launcher Item: " + Item);
    }

    public static WebElement getOpenedModalDialogBody() throws Exception {
        return $(getOpendModalDialog()).$(".slds-modal__content").waitUntil(visible, DEFAULT_WAIT);
    }

    public static WebElement getOpendModalDialog() throws InterruptedException {

        try {
            if(currentOpenedModel==null){
                waitForSpinner();
                currentOpenedModel =$$(".slds-modal").filter(visible).first();
            }
            currentOpenedModel.isEnabled();
            Log.info("Current opened Modal dialog is : " +currentOpenedModel.$("h2.slds-text-heading--medium").text());
            return currentOpenedModel;
        } catch (Throwable e) {
            Log.error("Open Modal not found due to following error > " + e.getMessage());
            Log.info("Lets try again");
            currentOpenedModel =$$(".slds-modal").filter(visible).first();
        }
        return currentOpenedModel;
    }

    public static void waitForSpinner() throws InterruptedException {
        sleep(2000);
        $(".slds-spinner_container").waitUntil(hidden, DEFAULT_WAIT);
        sleep(2000);
    }


    public static void ClickArrowIcon(By ArrowToClick) {
        try {
            WaitTool.waitForElementsPresentAndDisplay(ArrowToClick);
            List<WebElement> ArrowToOpenMenu = Common.FindAllElements(ArrowToClick);

            if (ArrowToOpenMenu.size() > 0) {
                if (ArrowToOpenMenu.size() > 1) {
                    ClickElement(ArrowToOpenMenu.get(1), "Arrow to open menu");
                } else {
                    ClickElement(ArrowToOpenMenu.get(0), "Arrow to open menu");
                }
            }
        } catch (Exception e) {
            Log.error("Error in ClickArrowIcon: " + e.toString());
        }
    }

    public static void SelectDropdownText(By Dropdown, String TextToSelect) {
        try {
            WaitTool.waitForElementPresentAndDisplay(Dropdown);
            Select drpToSelectFrom = new Select(getWebDriver().findElement(Dropdown));
            drpToSelectFrom.selectByVisibleText(TextToSelect);
        } catch (Exception e) {
            Log.error("There is an exception: " + e.toString());
        }
    }

    public static void SelectDropdownText(WebElement Dropdown, String TextToSelect) {
        try {
            Select drpToSelectFrom = new Select(Dropdown);
            drpToSelectFrom.selectByVisibleText(TextToSelect);
        } catch (Exception e) {
            Log.error("There is an exception: " + e.toString());
        }
    }

    public static void setInputValueUsingJS(By by, String value) {
        WebElement elm = $(by).waitUntil(appear, DEFAULT_WAIT);
        String js = String.format("arguments[0].value=\"%s\"", value);
        executeJavaScript(js, elm);
    }

    //TBD
    public static void javascriptExecutor(WebElement Element, int Interval) {
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].click();", Element);
        try {
            Thread.sleep(Interval);
        } catch (Exception e) {
            Log.error("There is an exception: " + e.toString());
        }
    }

    //TBD
    public static void SwitchToDefaultContent(int Interval) {
        try {
            System.out.println("Switching default");
            getWebDriver().switchTo().defaultContent();
            Thread.sleep(Interval);
        } catch (Exception e) {
            Log.error("There is an exception: " + e.toString());
        }
    }

    public static void RefreshPage(int Interval) {
        try {
            Thread.sleep(6000);
            RefreshPage();
            Thread.sleep(Interval);
        } catch (Exception e) {
            Log.error("There is an exception: " + e.toString());
        }
    }

    /**
     * <h1>selectDueDate<h1/>
     * <p>Purpose:This method is used for select due date while send or save TP compliance requirement </p>
     *
     * @param Interval
     * @throws InterruptedException
     */
    public static void selectDueDate(int Interval) throws InterruptedException {
        Actions action = new Actions(getWebDriver());
        WebElement we1 = getWebDriver().findElement(By.id("dueDate"));
        action.moveToElement(we1).moveToElement(getWebDriver().findElement(By.xpath("//span[contains(.,'30')]"))).click().build().perform();
        Thread.sleep(Interval);
    }

    /**
     * <h1>selectDueDate<h1/>
     * <p>Purpose:This method is used for select due date  while send or save TP compliance requirement </p>
     *
     * @param Interval
     * @throws InterruptedException
     */
    public static void selectDueDate1(By ElementFirst, By ElementSecond, int Interval) throws InterruptedException {

        WebElement we1 = getWebDriver().findElement(ElementFirst);
        actions().moveToElement(we1).moveToElement(getWebDriver().findElement(ElementSecond)).click().build().perform();
        Thread.sleep(Interval);
    }

    // Double click on element
    public static void doubleClick(By Element) throws InterruptedException {
        try {
            WaitTool.waitForElementPresentAndDisplay(Element);
            Actions action = new Actions(getWebDriver());
            action.doubleClick(getWebDriver().findElement(Element)).build().perform();
            //action.doubleClick(driver.findElement(By.xpath("//span[contains(.,'"+SendRequestComments+"')]"))).build().perform();
        } catch (Exception e) {
            Log.error("There is an exception: " + e.toString());
        }

    }

    /**
     * <h1>Close Browser <h1/>
     * <p>Purpose:This method is used for close open browser after test executions</p>
     *
     * @throws Exception
     */
    public static void closeBrowser() {
        try {
            if (getWebDriver() != null) {
                getWebDriver().quit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    // Verify test present in page
    public static void verifyTextPresentorNot(String AutoRequeestName) {
        getWebDriver().getPageSource().contains(AutoRequeestName);
    }

    public static void mouseHover(By ElementToHover, By ElementToClick) {
        try {
            WaitTool.waitForElementPresentAndDisplay(ElementToHover);
            WebElement elemenToHover = getWebDriver().findElement(ElementToHover);
            Actions action = new Actions(getWebDriver());
            action.moveToElement(elemenToHover).build().perform();
            WaitTool.waitForElementPresentAndDisplay(ElementToClick);
            getWebDriver().findElement(ElementToClick).click();
        } catch (Exception e) {
            Log.error("There is an exception: " + e.toString());
        }
    }

    /**
     * <h1> Search and Publish form<h1/>
     * <p>Purpose:This method is used for search and publish the form </p>
     *
     * @param FormName
     * @throws Exception
     */
    public static void searchAndPublishProductForm(String FormName) throws Exception {
        WebElement txtSrc = $(GlobalRepo.txtGlobalSrc).waitUntil(appear, DEFAULT_WAIT).getWrappedElement();
        txtSrc.click();
        txtSrc.sendKeys(FormName);
        sleep(500);
        txtSrc.sendKeys(Keys.ENTER);
        sleep(500);
        Common.ClickLink(FormName);
        sleep(2000);
        doubleClick(ProductRepo.Status);
    }

    //Assertions
    public static void assertElementContainsText(By by, String text) {
        $(by).waitUntil(appear, DEFAULT_WAIT).shouldHave(text(text));
    }

    public static void assertText(By Element, String ExpectedText) throws Exception {
        String actual = $(Element).waitUntil(visible, DEFAULT_WAIT).getText().trim();
        String message = "|Expected Text: " + ExpectedText + "| |Actual Text: " + actual + "|";
        String html = "<span style='color:blue;'>" + message + " </span>";
        logTestStep(html);
        logTestStepPassOrFail(actual.equalsIgnoreCase(ExpectedText), "Verify Text : " + message);
    }

    public static void assertText(String actualText, String ExpectedText) throws Exception {
        String message = "|Expected Text: " + ExpectedText + "| |Actual Text: " + actualText + "|";
        String html = "<span style='color:blue;'>" + message + " </span>";
        logTestStep(html);
        logTestStepPassOrFail(actualText.equalsIgnoreCase(ExpectedText), "Verify Text : " + message);
    }

    public static void assertExactTextPresent(String text) {
        WebElement element = $(byText(text)).waitUntil(appear, DEFAULT_WAIT).shouldBe(exist).getWrappedElement();
        HighlightElement(element);
    }

    public static void assertAnyTextPresent(String text) {
        WebElement element = $(withText(text)).waitUntil(appear, DEFAULT_WAIT).shouldBe(exist).getWrappedElement();
        HighlightElement(element);
    }

    public static void assertElementPresentAndDisplayed(By by) {
        $(by).waitUntil(appear, DEFAULT_WAIT).shouldBe(visible);
    }

    public static boolean assertValue(By Element, String ExpectedValue) {
        return $(Element).waitUntil(appear, DEFAULT_WAIT)
                .shouldHave(value(ExpectedValue))
                .exists();
    }

    public static void alert(int Interval) throws InterruptedException {
        Alert alert = getWebDriver().switchTo().alert();
        sleep(3000);
        alert.accept();
    }

    public static void switchToActiveElementString(String Element, int Interval) {
        getWebDriver().switchTo().activeElement().sendKeys(Element);
    }

    public static void switchToActiveElementKeys(Keys tab) {
        getWebDriver().switchTo().activeElement().sendKeys(tab);
    }

    public static void SwitchToWindowHandle(String Element) {
        getWebDriver().switchTo().window(Element);
    }

    public static String WindowHandle() {
        return getWebDriver().getWindowHandle();
    }

    public static Set<String> WindowHandles() {
        getWebDriver().getWindowHandles();
        return null;
    }

    public static Set<String> getWindowHandles() {
        return getWebDriver().getWindowHandles();
    }

    public static void Action(WebElement Element) {
        Actions act = new Actions(getWebDriver());
        act.moveToElement(Element);
    }

    public static void switchFrame(By Element, int Interval) throws InterruptedException {
        getWebDriver().switchTo().frame(getWebDriver().findElement(Element));
        sleep(Interval);

    }

    public static void deleteCookies(int Interval) throws InterruptedException {
        getWebDriver().manage().deleteAllCookies();
        sleep(Interval);
    }

    private static SelenideElement waitForAppLauncherItems() {
        SelenideElement modelBody = null;
        try {
            Log.info("Get Modal body...");
            //WebDriverRunner.getWebDriver().navigate().refresh();
            WaitTool.waitForPageLoadToComplete();
          //  ClickElement(LoginOutRepo.imgWaffle, "Waffle image");
            actions().moveToElement( $x("//span[text()='App Launcher']")).click().build().perform();
            sleep(4000);
            modelBody = $("div.slds-section.oneAppLauncherItemList").waitUntil(Condition.appear, SMALL_WAIT_30S);
        } catch (Throwable ex) {
            Log.error("App launcher was clicked but Links popup not opened, Trying again...");
            modelBody = null;
        }
        return modelBody;
    }

    public static void clickAppLauncher() throws TestStepFailException {
        SwitchToDefaultContent(1000);
        Log.info("Opening App Launcher");
        int count = 1, maxTry = 5;
        SelenideElement modelBody = null;

        while (modelBody == null && count <= maxTry) {
            System.out.println("Open App launcher try counter# " + count + " of " + maxTry);
            modelBody = waitForAppLauncherItems();
            count++;
        }

        if (modelBody == null) {
            Log.error("Application failed to open app launcher items after 5 time tries");
            throw new TestStepFailException("Application failed to open app launcher items after trying " + maxTry + " times");
        }
    }

    public static boolean checkExistenceOfElement(By Element) {
        boolean elementStatus = false;
        try {
            WaitTool.waitForElementPresentAndDisplay(Element);
            elementStatus = getWebDriver().findElements(Element).size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elementStatus;
    }

    /**
     * <h1> Get Text<h1/>
     * <p>Purpose: If null is returned then the comparison fails and execution of fails </p>
     *
     * @param Element,timeOutInSeconds
     * @throws Exception
     */

    public static String getElementText(By Element, int timeOutInSeconds) {
        return $(Element).waitUntil(appear, DEFAULT_WAIT).getText();

    }

    public static void ClickElementByIndex(By Element, int index) {
        try {
            WaitTool.waitForElementToBeClickable(Element);
            getWebDriver().findElements(Element).get(index).click();
        } catch (Exception e) {
            Log.error("Element is not clickable: " + e.toString());
        }
    }

    /**
     * <h1>ClickElementByLastIndex<h1/>
     * <p>Purpose: To click the last index from all the elements found </p>
     *
     * @param Element,timeOutInSeconds
     * @throws Exception
     */

    public static void ClickElementByLastIndex(By Element, int Interval) {
        try {
            WaitTool.waitForElementToBeClickable(Element);
            int elementCount = Common.getCount(Element, Interval);
            getWebDriver().findElements(Element).get(elementCount).click();

        } catch (Exception e) {
            Log.error("Element is not clickable: " + e.toString());
        }
    }

    /**
     * <h1>Count<h1/>
     * <p>Purpose: To get count of total number of elements found </p>
     *
     * @param Element,Interval
     * @throws Exception
     */

    public static int getCount(By Element, int Interval) throws InterruptedException {

        List<WebElement> myElements = getWebDriver().findElements(Element);
        int size = myElements.size();
        return size;
    }

    /**
     * <h1>Select Duedate<h1/>
     * <p>Purpose: This method is used for select due date </p>
     *
     * @param element,Interval
     * @throws Exception
     */
    public static void selectDateNextMonth(By element) throws Exception {
        Actions action = new Actions(getWebDriver());
        Thread.sleep(1000);
        WaitTool.isElementPresentAndDisplay(element);
        action.moveToElement(getWebDriver().findElement(element))
                .click(getWebDriver().findElement(GlobalRepo.tblNextMonth)).build().perform();

        List<WebElement> tblTds = Common.FindAllElements(GlobalRepo.tblTd);
        Log.info("Clicking on date");
        WaitTool.waitForElementToBeClickable(tblTds.get(10));
        action.click(Common.FindAllElements(GlobalRepo.tblTd).get(10))
                .build().perform();
    }

    public static void selectDateNextMonth(WebElement element) throws InterruptedException {
        $(element).scrollIntoView(true)
                .hover().click();
        sleep(1000);
        $(byTitle("Next Month")).waitUntil(Condition.visible, 5000).click();
        $$(By.xpath("//*[contains(@ng-repeat,'day in week')]")).filter(visible)
                .last()
                .find("span")
                .click();
    }

    public static void selectDatePreviousMonth(WebElement element) throws InterruptedException {
        $(element).scrollIntoView(true)
                .hover().click();
        sleep(1000);
        $(byTitle("Previous Month")).waitUntil(Condition.visible, 5000).click();
        $$(By.xpath("//*[contains(@ng-repeat,'day in week')]")).filter(visible)
                .last()
                .find("span")
                .click();
    }

    /**
     * <h1>ProjectDirectory<h1/>
     * <p>Purpose: This method is used for get project directory to get the file to upload document in document library </p>
     *
     * @throws Exception
     */

    public static String getProjectDirectory() {
        return System.getProperty("user.dir");
    }

    public static String getTextFile(String fileType) {
        String FS = File.separator;
        String filePath = getProjectDirectory() + FS + "src" + FS + "test" + FS + "resources" + FS + "DocumentTypes";

        switch (fileType) {
            case ".txt":
                filePath = filePath + FS + "textDoc.txt";
                break;
            case ".jpg":
                filePath = filePath + FS + "jpegImage.jpeg";
                break;
            case ".jpeg":
                filePath = filePath + FS + "jpegImage.jpeg";
                break;
            case ".xml":
                filePath = filePath + FS + "xmlFile.xml";
                break;
            default:
                filePath = filePath + FS + "textDoc.txt";
                break;
        }
        return filePath;

    }

    public static void assertTrue(String str1, String str2) {
        Assert.assertTrue(str1.equalsIgnoreCase(str2));

    }

    public static void assertTrue(String str1, String str2, String message) {
        Assert.assertTrue(str1.equalsIgnoreCase(str2), message);

    }

    public static void assertTrue(boolean isTrue) {
        Assert.assertTrue(isTrue);

    }

    public static void assertTrue(int num1, int num2) {
        Assert.assertTrue(num1 == num2);

    }

    public static void waitForElementToBeVisible(By element) throws Exception {
        WaitTool.waitForElementPresentAndDisplay(element);
    }

    public static void waitForElementTobeInvisible(By element) throws Exception {
        WaitTool.waitForElementsToBeInvisible(element);
    }

    public static void waitForPageLoadToComplete() {
        try {
            WaitTool.waitForPageLoadToComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <h1>Close App Launcher<h1/>
     * <p>Purpose: This method is used for Close app launcher </p>
     *
     * @throws Exception
     */

    public static void closeAppLauncher() throws Exception {
        if (WaitTool.isElementPresentAndDisplay(GlobalRepo.closeAppLauncher)) {
            Log.info("Closing App Launcher");
            Common.ClickElement(GlobalRepo.closeAppLauncher, "Close App launcher");
        } else
            Log.info("App Laucher is already closed or not opened");
    }

    public static void RefreshPage() throws Exception {
        getWebDriver().navigate().refresh();
        WaitTool.waitForPageLoadToComplete();
    }

    /**
     * <h1>Click Element Using JS<h1/>
     * <p>Purpose: This method is used for click on any element using java scripts  </p>
     *
     * @throws Exception
     */

    public static void clickUsingJS(By by) throws Exception {
        try {
            Log.info("Clicking on New Form button");
            executeJavaScript("arguments[0].click();", $(by).waitUntil(appear, DEFAULT_WAIT).getWrappedElement());
        } catch (Exception e) {
            Log.error("Error in ClickUsingJs ");
            Log.error("Exception: " + e.getMessage());
            throw e;
        }
    }

    public static void clickUsingJS(WebElement elm, String detail) throws Exception {
        try {
            Log.info("Click:" + detail);
            executeJavaScript("arguments[0].click();", elm);
        } catch (Exception e) {
            Log.error("Error in ClickUsingJs ");
            Log.error("Exception: " + e.getMessage());
            throw e;
        }
    }

    /**
     * <h1>Click Element Using JS<h1/>
     * <p>Purpose: This method is used for click on any element using java scripts  </p>
     *
     * @throws Exception
     */

    public static void clickUsingJS(By by, String detail) throws Exception {
        try {
            Log.info("Click:" + detail);
            executeJavaScript("arguments[0].click();", $(by).waitUntil(appear, DEFAULT_WAIT).getWrappedElement());
        } catch (Exception e) {
            Log.error("Error in ClickUsingJs ");
            Log.error("Exception: " + e.getMessage());
            throw e;
        }
    }

    /**
     * <h1>Get Data From Property File<h1/>
     * <p>Purpose: This method is used to get form name from property file  </p>
     *
     * @param key
     * @throws Exception
     */


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

    public static String Screenshot(String ImagesPath) {
        TakesScreenshot oScn = (TakesScreenshot) getWebDriver();
        File oScnShot = oScn.getScreenshotAs(OutputType.FILE);
        File oDest = new File(ImagesPath + ".jpg");
        try {
            FileUtils.copyFile(oScnShot, oDest);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return oDest.getName();
    }

    public static String CaptureScreenForReport_Base64() {
        try {
            TakesScreenshot oScn = (TakesScreenshot) WebDriverRunner.getWebDriver();
            return oScn.getScreenshotAs(OutputType.BASE64);
        } catch (Throwable e) {
            Log.error("There was a problem while capturing screenshot");
            e.printStackTrace();
        }
        return "No screen shot captured";
    }

    public static String GetTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
        return timeStamp;
    }

    public static void executeStep(boolean result, String step) throws Exception {
        if (result) {
            Log.info("Execution of: " + step + "-PASS");
        } else {
            throw new Exception("Execution of: " + step + "-FAILED");
        }

    }

    public static void verifyStep(boolean result, String step) throws Exception {
        if (result) {
            Log.info("Verification of: " + step + "-PASS");
        } else {
            throw new Exception("Verification of: " + step + "-FAILED");
        }

    }

    //New Added 25Aug
    public static void url() {
        getWebDriver().get(Urls.baseUrl);
    }

    private static void WaitUntilResultsPresent1(String globalSearchRequestTab, String RequestName) throws Exception {

        int counter = 0;
        long time = 0;
        boolean clickFlag = false;
        do {
            if (counter <= 50) {
                ClickGlobalSearchItem(globalSearchRequestTab);
                counter = counter + 1;
                sleep(1000);
                boolean resMsg = $(GlobalRepo.globalSearchResultNumber1).isDisplayed();
                String reqLinkXp = String.format("//a[contains(text(),'%s')]", RequestName.trim());
                boolean resReqLink = $$(By.xpath(reqLinkXp)).filter(Condition.visible).size() > 0;
                System.out.println("IsDisplayed: No results for > " + resMsg);

                System.out.println("IsDisplayed: Request Link > " + resReqLink);

                if ((!resMsg) && (resReqLink)) {
                    Log.info(RequestName + " Link has reflected clicking on it");
                    // For TP Compliance Request
                    SelenideElement tableElm = $$("table.uiVirtualDataTable").filter(visible).first().waitUntil(visible, DEFAULT_WAIT);
                    tableElm.$$("tbody th[scope='row'] a").findBy(text(RequestName.trim())).waitUntil(visible, DEFAULT_WAIT).click();
                    sleep(2000);
                    clickFlag = true;
                    break;
                } else {
                    Log.info("Request not reflected retry after 10 sec. Try#" + counter);
                    sleep(10000);
                    time = time + 10;
                    if (time >= 80) {
                        time = 0;
                        Log.info("Request not reflected after 2 min");
                        ExecuteBatchMessageProcessor.ExecuteBatchWithLastLoginUser();
                    }
                }
            } else {
                TestBase.logTestStepPassOrFail(false, "Application response fail- Request was not received after 8 min");
                clickFlag = true;
                break;
            }
        }
        while (clickFlag == false);
    }

    /**
     * <h1>Handle Request Popup<h1/>
     * <p>Purpose:This method is used for handing request popup when TP and Product is already sent</p>
     *
     * @throws Exception
     */

    public static void sendReqPopup() throws Exception {
        sleep(4000);
        //slds-modal__content
        WebElement openedModalElm = GetOpendModalDialog();
        if ($(openedModalElm).$$(By.xpath("//div[@id='sendRequestTable']//p[contains(text(),'A request already exists for the following')]")).size() > 0) {
            switchToActiveFrame();
            $(openedModalElm).$$(".slds-checkbox input").forEach(elm -> {
                if (!elm.isSelected()) {
                   elm.closest("label").click();
                }
            });
            SelenideElement btnYes = $(GetButtonOfOpenedModalDialog("Yes"));
            if (btnYes.isEnabled()) {
                btnYes.click();
                sleep(3000);
            } else {
                throw new TestStepFailException("Yes button is not enabled in Send Request popup");
            }
            Log.info("Request has already sent");
        } else {
            ICIX_Common.GetFooterButtonOfOpenedModalDialog("Yes").click();
        }
        sleep(3000);
    }//End- sendReqPopup

    /**
     * <h1> Save Form Name <h1/>
     * <p>Purpose:This method is used save the form name </p>
     *
     * @param formName
     * @throws Exception
     */

    //Save A form into file
    public static void saveFormName(String formName) {

        String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "Form.properties";
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            Properties prop = new Properties();
            prop.load(inputStream);
            inputStream.close();
            FileOutputStream fos = new FileOutputStream(filePath);
            prop.setProperty("Form", formName);
            prop.store(fos, "Form name for scripts");
            fos.close();
            Log.info("Form saved successfully: " + getForm());
        } catch (Exception ex) {
            Log.error("Problem in Form save");
            ex.printStackTrace();
        }
    }

    /**
     * <h1> Save Form  <h1/>
     * <p>Purpose:This method is used to save form into file </p>
     *
     * @throws Exception
     */


    //Save A form into file
    public static String getForm() {
        Properties prop = new Properties();
        String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "Form.properties";
        try (FileInputStream fis = new FileInputStream(filePath)) {
            prop.load(fis);
            return prop.getProperty("Form");
        } catch (Exception ex) {
            ex.printStackTrace();
            return "NA";
        }
    }//End save form

    /**
     * <h1> Switch to Active Frame <h1/>
     * <p>Purpose:This method is used for switch control to active frame </p>
     */

    public static void switchToActiveFrame() {
        ICIX_Common.WaitForSpinner();
        switchTo().defaultContent();
        Log.info("Switching to active frame");
        WebElement element = $(".active.oneContent").$("iframe").waitUntil(appear, DEFAULT_WAIT);
        WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), (DEFAULT_WAIT / 1000));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
        ICIX_Common.WaitForSpinner();
        Log.info("Switched to active frame successfully");

    }

    //End save form

    /**
     * <h1> Verify Request And Workflow Status <h1/>
     * <p>Purpose:This method is used to verify request and workflow statues using Request status workflow
     * status and request name as a parameter </p>
     *
     * @param RequestStatus,WorkflowStatus,requestName
     * @throws Exception
     */
    public static void verifyRequestAndWorkflowStatus(String RequestStatus, String WorkflowStatus, String requestName) throws Exception {
        Request ObjReq = new Request();
        logTestStep(String.format("Verify |Request status = %s|Workflow status = %s for request name = %s", RequestStatus, WorkflowStatus, requestName));

        ObjReq.searchRequest(AppLauncherItems.REQUESTS, requestName);
        Common.RefreshPage();
        Common.assertText(RequestRepo.txtRequestStatus, RequestStatus);

        Common.ClickElement(By.xpath("(//span[contains(text(),'Related')])[position()=1]"), "Request");
        String workflowStatus = String.format("(//span[contains(.,'%s')])[position()=1]", WorkflowStatus);
        Common.assertText(RequestRepo.workflowStatus, WorkflowStatus);

    }

    /**
     * <h1> Click on AppLauncher Item <h1/>
     * <p>Purpose:This method is used to click on item present on app launcher using item name(Ex Requests)as parameter </p>
     *
     * @param Item
     * @throws Exception
     */
    public static void clickAppLauncherItem(AppLauncherItems Item) {
        Log.info("Click App launcher Item: " + Item.getValue());
        $(".oneAppLauncherItem a").waitUntil(visible, SMALL_WAIT_30S);
        $$(".oneAppLauncherItem a").filter(visible).find(exactText(Item.getValue())).click();
        ICIX_Common.WaitForSpinner();
    }

    /**
     * <h1> Get Current URL<h1/>
     * <p>Purpose:This method is used for get current url </p>
     */
    public static String GetCurrentUrl() {
        if (getWebDriver() != null) {
            return getWebDriver().getCurrentUrl();
        } else
            return "";

    }

    /**
     * <h1>Select Due Date<h1/>
     * <p>Purpose:This method is used to select tp compliance due date </p>
     *
     * @param element,element2,element3
     * @throws Exception
     */

    public static void selectDateNextMonth(By element, By element2, By element3) throws Exception {

        Actions action = new Actions(getWebDriver());
        Thread.sleep(1000);
        WaitTool.isElementPresentAndDisplay(element);

        action.moveToElement(getWebDriver().findElement(element))

                //   .click(driver.findElement(GlobalRepo.tblNextMonth)) .build().perform();
                .click(getWebDriver().findElement(element2)).build().perform(); // New Added
        Thread.sleep(1000);
        Common.ClickElement(element3, "SelectData");
    }

    /**
     * <h1>Click Search Items<h1/>
     * <p>Purpose:This method is used to click on search list(Ex Requests,Workflows etc </p>
     *
     * @param item
     * @throws Exception
     */

    /*** This method created for spring 19 *********/

    public static void ClickGlobalSearchItem(String item) throws TestStepFailException {
        //This is used to to load all global search items
        try {
            Log.info("Click on global search item: " + item);
            $$(By.xpath("//*[@id='allItemsList']/ul/li/a")).size();
            sleep(1000);
            SelenideElement xp = $$("#allItemsList .scopesListSection  .scopesItem_name").
                    filter(visible)
                    .findBy(text(item));
            if (xp.isDisplayed()) {
                xp.waitUntil(appear, DEFAULT_WAIT)
                        .scrollTo().click();
                sleep(2000);
                ICIX_Common.WaitForSpinner();
            } else {
                sleep(1000);
                $$(".slds-has-block-links--space .scopesItem_name").
                        filter(visible).findBy(text(item)).click();
                sleep(500);
            }
        } catch (Throwable e) {
            logTestStepFail(e.getMessage());
            logTestStepWarn("It look like given item is hidden please perform following steps");
            logTestStep("Admin user >Goto SF classic >Setup >Profiles >Standard Platform User >Click Edit >Scroll to Tab Settings >" + item + " >Select >Default On >Save");
            throw new TestStepFailException(item + " not found or hidden by system admin");
        }
    }

   /*
    public static void ClickGlobalSearchItem(String item) throws InterruptedException {
        //This is used to to load all global search items


        Log.info("Click on global search item: " + item);
        $$(By.xpath("//*[@id='allItemsList']/ul/li/a")).size();
        sleep(1000);
        String xp = String.format(".//*[@id='allItemsList']/ul/li/a[contains(text(),'%s')]", item);
        if($x(xp).isDisplayed()) {
            $x(xp).waitUntil(appear, DEFAULT_WAIT)
                    .scrollTo().click();
            sleep(7000);
        }else {
            sleep(1000);
            String itemList = String.format("//a[contains(text(),'%s')]", item);
            $$x(itemList).
                    filter(visible).last().click();
            sleep(500);

        }

    }
    */

    public static void resetImplicitlyWait() {
        getWebDriver().manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT_IN_SEC, TimeUnit.SECONDS); // reset implicitlyWait

    }

    private static void WaitUntilResultsPresent(String globalSearchRequestTab, String RequestName) throws Exception {

        int counter = 0;
        long time = 0;
        boolean clickFlag = false;
        do {
            if (counter <= 50) {
                ClickGlobalSearchItem(globalSearchRequestTab);
                counter = counter + 1;
                sleep(1000);
                boolean resMsg = $(GlobalRepo.globalSearchResultNumber1).isDisplayed();
                String reqLinkXp = String.format("//a[contains(text(),'%s')]", RequestName.trim());
                boolean resReqLink = $$(By.xpath(reqLinkXp)).filter(Condition.visible).size() > 0;

                System.out.println("IsDisplayed: No results for > " + resMsg);
                System.out.println("IsDisplayed: Request Link > " + resReqLink);

                if ((!resMsg) && (resReqLink)) {
                    logTestStep(RequestName + " Link has reflected clicking on it");
                    // For TP Compliance Request
                    sleep(2000);
                    clickFlag = true;
                    break;
                } else {
                    Log.info("Request not reflected retry after 10 sec. Try#" + counter);
                    sleep(10000);
                    time = time + 10;
                    if (time >= 80) {
                        time = 0;
                        Log.info("Request not reflected after 2 min");
                        ExecuteBatchMessageProcessor.ExecuteBatchWithLastLoginUser();
                    }

                }
            } else {
                TestBase.logTestStepPassOrFail(false, "Application response fail- Request was not received after 8 min");
                clickFlag = true;
                break;
            }
        }
        while (clickFlag == false);

    }

    public static void setFocus(By by) {
        executeJavaScript("arguments[0].focus();", $(by).waitUntil(appear, DEFAULT_WAIT).getWrappedElement());
    }

    public static String getDocumentTypeToUpload(String fileType) {
        String FS = File.separator;
        String filePath = getProjectDirectory() + FS + "src" + FS + "test" + FS + "resources" + FS + "DocumentTypes";

        switch (fileType) {
            case ".txt":
                filePath = filePath + FS + "textDoc.txt";
                break;
            case ".jpg":
                filePath = filePath + FS + "jpegImage.jpeg";
                break;
            case ".jpeg":
                filePath = filePath + FS + "jpegImage.jpeg";
                break;
            case ".xml":
                filePath = filePath + FS + "xmlFile.xml";
                break;
            default:
                filePath = filePath + FS + "textDoc.txt";
                break;
        }
        return filePath;

    }

    public static void verifyUserCredentials() throws Exception {
        LoginOut objLoginOut = new LoginOut();
        objLoginOut.loginAs(LoginOutTestData.Requestor_Admin);
        objLoginOut.loginAs(LoginOutTestData.Responder_Admin);
        objLoginOut.loginAs(LoginOutTestData.Laboratory_Admin);
        objLoginOut.loginAs(LoginOutTestData.Requestor_SPU);
        objLoginOut.loginAs(LoginOutTestData.Responder_SPU);
        objLoginOut.loginAs(LoginOutTestData.Laboratory_SPU);
    }

    public static void verifyRequestIsDisplayedInICIXTask(String requestName) throws Exception {
        sleep(1000);
        {
            int counter = 0;
            while (counter < 3) {
                if ($(By.xpath("//*[contains(@class,'modal-body')]")).is(not(visible))) {
                    clickAppLauncher();
                    counter++;
                } else {
                    break;
                }
            }
        }
        TestBase.logTestStep("Open ICIX Task List");
        clickAppLauncherItem(AppLauncherItems.ICIXTASKS);
        waitForPageLoadToComplete();
        sleep(12000);
        logScreenshot("ICIXTask");
        // $(".dotsSpinner").waitUntil(Condition.disappear, DEFAULT_WAIT);
        switchToActiveFrame();
        sleep(5000);
        //Load ICIX Tasks
        $$("tr").shouldHave(CollectionCondition.sizeGreaterThan(0));
        String taskName = "NA";
        TestBase.logTestStep("Retrieve ICIX Task");
        logScreenshot("ICIXTaskList");
        logTestStep("Click on New List View page");
        $(By.partialLinkText("New List View")).waitUntil(appear, DEFAULT_WAIT).click();
        sleep(5000);
        $(By.id("fName")).waitUntil(visible, DEFAULT_WAIT).setValue("AutoTaskList" + " " + GetTimeStamp());
        sleep(3000);
        logTestStep("Select status as open");
        SelectDropdownText($x("//label[contains(text(),'Status')]/following::select[1]"), "Open");
        sleep(2000);
        $(By.xpath(".//*[text()='Save']")).click();
        sleep(13000);
        for (SelenideElement elm : $$x("//tr/td[1]/span[contains(@class,'slds-text-body--regular')]")) {
            if (elm.getText().contains(requestName)) {
                Log.info("New task found");
                taskName = elm.getText();
                sleep(4000);
                elm.click();
                sleep(4000);
                //    assertText(taskName,requestName);
                break;
            }
        }
        System.out.println("ReqName " + requestName);
        //Assert.assertTrue(requestName.equalsIgnoreCase(taskName),"Task not found in list");
        Assert.assertTrue(taskName.contains(requestName), "Task not found in list");
        TestBase.logTestStep("Task is available in task list:" + taskName);

    }

    public static void verifyRequestIsDisplayedInICIXTask(String requestName, String strTaskFilterName) throws Exception {
        sleep(1000);
        TestBase.logTestStep("Verify Request is displayed in ICIX task");
        {
            int counter = 0;
            while (counter < 3) {
                if ($(By.xpath("//*[contains(@class,'modal-body')]")).is(not(visible))) {
                    clickAppLauncher();
                    counter++;
                } else {
                    break;
                }
            }
        }
        TestBase.logTestStep("Open ICIX Task List");
        clickAppLauncherItem(AppLauncherItems.ICIXTASKS);
        waitForPageLoadToComplete();
        sleep(12000);
        logScreenshot("ICIXTask");
        // $(".dotsSpinner").waitUntil(Condition.disappear, DEFAULT_WAIT);
        switchToActiveFrame();
        sleep(5000);
        //Load ICIX Tasks
        $$("tr").shouldHave(CollectionCondition.sizeGreaterThan(0));
        String taskName = "NA";
        TestBase.logTestStep("Retrieve ICIX Task");
        logScreenshot("ICIXTaskList");
        logTestStep("Click on New List View page");
        $(By.partialLinkText("New List View")).waitUntil(appear, DEFAULT_WAIT).click();
        sleep(5000);

        $(By.id("fName")).waitUntil(visible, DEFAULT_WAIT).setValue(strTaskFilterName);
        sleep(3000);
        logTestStep("Select status as open");
        SelectDropdownText($x("//label[contains(text(),'Status')]/following::select[1]"), "Open");
        sleep(2000);
        $$x("//button[contains(@class,'slds-button--brand')]/span[text()='Save']").filter(Condition.visible).last().click();
        sleep(18000);
        for (SelenideElement elm : $$x("//tr/td[1]/span[contains(@class,'slds-text-body--regular')]")) {
            elm.scrollIntoView(true);
            if (elm.getText().contains(requestName)) {
                Log.info("New task found");
                taskName = elm.getText();
                HighlightElement(elm);
                sleep(4000);
                elm.click();
                $(".task-right .block.bottomborder .titles").waitUntil(matchText(requestName), DEFAULT_WAIT);
                sleep(4000);
                break;
            }
        }
        System.out.println("ReqName " + requestName);
        Assert.assertTrue(taskName.contains(requestName), "Search Task in list");
        TestBase.logTestStep("Task is available in task list: " + taskName);
    }//End

    public static String GetOSName() {
        return System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    }

    public static boolean isWindows() {
        return (GetOSName().indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (GetOSName().indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (GetOSName().indexOf("nix") >= 0 || GetOSName().indexOf("nux") >= 0 || GetOSName().indexOf("aix") > 0);
    }

    public static boolean isLinux() {
        return GetOSName().indexOf("nux") >= 0;
    }

    public static void KillChromeDriverProcessForWindows() {
        if (isWindows()) {
            try {
                for (Integer pid : GetChromeProcessesForWindow()) {
                    Log.info("Kill Chrome Process Id#" + pid);
                    Runtime.getRuntime().exec("taskkill /F /PID " + pid);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }//End method

    public static Set<Integer> GetChromeProcessesForWindow() {
        Set<Integer> winChromeProcess = new HashSet<>();
        String out;
        Process p = null;
        if (Common.isWindows()) {
            try {
                p = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq chromedriver.exe*\"");
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((out = input.readLine()) != null) {
                    String[] items = StringUtils.split(out, " ");
                    if (items.length > 1 && StringUtils.isNumeric(items[1])) {
                        winChromeProcess.add(NumberUtils.toInt(items[1]));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//End if
        return winChromeProcess;
    }

    public static String GetProjectNameFromMavenPOM() {
        String mavenProjectName = "";
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            mavenProjectName = model.getGroupId();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return mavenProjectName;
    }

    /**
     * <h1>Switch to default window</h1>
     */
    public static void switchToDefaultWindow() {
        WebDriverRunner.getWebDriver().switchTo().window(GetDefaultWindow());
    }

    public static void Search(String moduleName, String KeywordToSearch) throws Exception {
        long startTime = System.currentTimeMillis();

        Log.info(String.format("Search '%s' from module '%s'", KeywordToSearch, moduleName));
        try {
            Thread.sleep(6000);
            WebElement globalSearchBox = Common.FindAnElement(GlobalRepo.txtGlobalSrc); //WebDriverRunner.getWebDriver().findElement(GlobalRepo.txtGlobalSrc);
            globalSearchBox.click();
            Log.info("Global search text box clicked.");
            globalSearchBox.sendKeys(KeywordToSearch);
            sleep(1000);
            $(globalSearchBox).pressEnter();
            sleep(2000);

            if (!$(GlobalRepo.lnkShowMore).text().equalsIgnoreCase("Show Less")) {
                ClickElement(GlobalRepo.lnkShowMore, "Show More");
                sleep(2000);
            }//end if

            //Load menu items
            $$(By.xpath(".//*[@id='allItemsList']//li/a")).size();

            switch (moduleName) {
                case "Requests":
                    //  WaitUntilResultsPresent1("Requests", KeywordToSearch);
                    ClickGlobalSearchItem("Requests");
                    Log.info("Request tab clicked");
                    break;

                case "Workflows":
                    // WaitUntilResultsPresent1("Workflows", KeywordToSearch);
                    ClickGlobalSearchItem("Workflows");
                    Log.info("Workflow tab clicked");
                    break;

                case "Trading Partner Groups":
                    //WaitUntilResultsPresent1("Trading Partner Groups", KeywordToSearch);
                    ClickGlobalSearchItem("Trading Partner Groups");
                    Log.info("Trading Partner Groups tab clicked");
                    break;
                case "ICIX Products":
                    // WaitUntilResultsPresent1("ICIX Products", KeywordToSearch);
                    ClickGlobalSearchItem("ICIX Products");
                    Log.info("ICIX Product tab clicked");
                    break;
                case "Container Templates":
                    // WaitUntilResultsPresent1("Container Templates", KeywordToSearch);
                    ClickGlobalSearchItem("Container Templates");
                    Log.info("Container Templates tab clicked");
                    break;
                case "Product Groups":
                    //WaitUntilResultsPresent1("Product Groups", KeywordToSearch);
                    ClickGlobalSearchItem("Product Groups");
                    Log.info("Product Groups tab clicked");
                    break;

                default:
                    Log.info("Invalid Search Tab");
                    break;
            }
            long endTime = System.currentTimeMillis();
            double seconds = (endTime - startTime) / 1000.0;
            TestBase.logTestStep("Total time taken to search : " + seconds + "sec");
            sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static boolean isAllTrueInList(List<Boolean> listBools) {
        List<Boolean> listFalse = listBools.stream().filter(res -> Boolean.FALSE.equals(res)).collect(Collectors.toList());
        return listFalse.size() == 0;
    }

    public static String GetToastMessage() {
        String message = "";
        try {
            message = $$(".toastMessage").filter(visible).first().innerText();
            $(".toastMessage").waitUntil(disappear, DEFAULT_WAIT);
        } catch (Exception ex) {
            Log.info("Toast Message Not Found");
        }
        return message;
    }

    /**
     * <h1>Convert Date format from "May 17th, 2018" to "5/17/2018"</h1>
     *
     * @param strDate
     * @return
     */
    //This code is used for convert following date format "May 17th, 2018" to "5/17/2018"
    public static String GetConvertedDate(String strDate) {
        String convertedDate = "";
        try {
            String actualDate = strDate;
            String result = actualDate.replaceAll("[-+.^:,]", "");

            String[] dateArray = result.split(" ");
            String month = dateArray[0];
            String day = dateArray[1].replaceAll("[^0-9]", "");
            String year = dateArray[2];
            strDate = month + "/" + day + "/" + year;
            Date dd = new SimpleDateFormat("MMMM/dd/yyyy").parse(strDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dd);
            month = "" + (calendar.get(Calendar.MONTH) + 1);
            convertedDate = month + "/" + day + "/" + year;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }//End Method

    /**
     * <h1>Click Element Using pixel points<h1/>
     * <p>Purpose: This method is used To click element using pixel points  </p>
     *
     * @throws Exception
     */

    public void clickElementPoint(By by) throws Exception {
        WaitTool.waitForElementToBeClickable(by);
        try {
            JavascriptExecutor jse = (JavascriptExecutor) getWebDriver();
            jse.executeScript("window.scrollTo(" + getWebDriver().findElement(by).getLocation().x + ",0)");
            Thread.sleep(1000);
            getWebDriver().findElement(by).click();
        } catch (Exception e) {
            Log.error("Exception thrown: " + e.getMessage());
        }
    }

    public enum SearchModule {
        REQUESTS("Requests"), WORKFLOWS("Workflows"), TP_GROUPS("Trading Partner Groups"), ICIXProducts("ICIX Products"), ContainerTemplates("Container Templates");
        String value;

        private SearchModule(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }


    public static void verifyRequestAndWorkflowStatus_MultipleRequests(String RequestStatus, String WorkflowStatus, String requestName) throws Exception {
        TestBase.logTestStep("Verify workflow and request status for multiple requests");
        Common.OpenMultipleRequestsInNewTabs(requestName);
        String defaultWindow = WebDriverRunner.getWebDriver().getWindowHandle();
        for (String curWin : WebDriverRunner.getWebDriver().getWindowHandles()) {
            //Check if this is not a default window
            if (!curWin.equals(defaultWindow)) {
                WebDriverRunner.getWebDriver().switchTo().window(curWin);
                sleep(1000);
                //Now submit 3 actor form
                Request obtRequest = new Request();
                TestBase.logTestStep(String.format("Verify |Request status = %s|Workflow status = %s for request name = %s", RequestStatus, WorkflowStatus, requestName));
                Common.assertText(RequestRepo.txtRequestStatus, RequestStatus);
                PageTabs.OpenTab_Related();
                $x("//img[@title='Workflows']").waitUntil(visible, DEFAULT_WAIT);
                SelenideElement articleElement = $x("//img[@title='Workflows']/ancestor::article[1]").waitUntil(visible, DEFAULT_WAIT);
                String workflowStatus = articleElement.$$x("//table/tbody/tr/td[2]/*").filter(visible).last().text();
                Common.assertText(RequestRepo.workflowStatus, WorkflowStatus);
            }//End if
        }//End loop

        CloseExtraWindows();
    }

    public static void OpenMultipleRequestsInNewTabs(String RequestName) throws Exception {
        Common.GlobalSearchs(AppLauncherItems.REQUESTS, RequestName);
        String reqLinkXp = String.format("//a[contains(text(),'%s')]", RequestName.trim());
        ElementsCollection requests = $$(By.xpath(reqLinkXp)).filter(Condition.visible);
        int size = requests.size();
        Log.info("Total request link # " + size);
        for (int k = 0; k < size; k++) {
            Log.info("Opening Tab: " + k);
            String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, Keys.RETURN);
            $$(By.xpath(reqLinkXp)).filter(Condition.visible).get(k).sendKeys(selectLinkOpeninNewTab);
            sleep(4000);

            //This code is used to reload the links so that they can open in new tab
            if (!((k + 1) >= size))
                Common.GlobalSearchs(AppLauncherItems.REQUESTS, RequestName);
        }

        Log.info("Total browser windows are now:# " + WebDriverRunner.getWebDriver().getWindowHandles().size());

    }//End of function

    public static String GetTextFromPdfFile(String docFilePath) {
        String lines = "";
        try (PDDocument document = PDDocument.load(new File(docFilePath))) {
            document.getClass();
            if (!document.isEncrypted()) {

                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);

                PDFTextStripper tStripper = new PDFTextStripper();

                String pdfFileInText = tStripper.getText(document);

                // split by whitespace
                pdfFileInText = pdfFileInText.replaceAll("[\\t\\n\\r]+", " ");
                lines = pdfFileInText.replace("  ", " ");
            }


        } catch (Exception ex) {
            TestBase.logTestStepFail("Error in coc reading");
            TestBase.logTestStepFail("Error details -> " + ex.getMessage());
        }

        return lines;
    }//End of function

    public static void DeleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            Log.error("Error in file delete >" + filePath);
            e.printStackTrace();
        }
    }

    public static String convertMillisToTime(long millis) {
        String convertedTime = String.format("%d hour(s), %d minute(s), and %d second(s)",
                millis / (1000 * 60 * 60), (millis % (1000 * 60 * 60)) / (1000 * 60), ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        return convertedTime;
    }

    public static void setFocusUsingJS(WebElement elm) throws Exception {
        executeJavaScript("arguments[0].focus();", elm);
    }

    public static void scrollUp() throws Exception {
        logTestStep("Scroll up");
        JavascriptExecutor jse = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        jse.executeScript("window.scrollBy(0,-500)", "");
        sleep(2000);
    }

    public static void scrollDown(String size) throws Exception {
        logTestStep("Scroll up");
        JavascriptExecutor jse = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        jse.executeScript("window.scrollBy(0," + size + ")", "");
        sleep(2000);
    }


    public static void clickAppLauncherItem(String Item) {
        Log.info("Click Item: " + Item);
        $$(".oneAppLauncherItem a").filter(visible).find(exactText(Item));
    }

    public static String GetSuiteName() {
        String suiteName = "_";
        if (System.getProperty("app.suite") != null) {
            if (System.getProperty("app.suite").equalsIgnoreCase("full") ||
                    System.getProperty("app.suite").equalsIgnoreCase("high")) {
                suiteName = System.getProperty("app.suite");
            }
        }
        return suiteName;
    }

    public static void ItemIsVisible(String Item) throws Exception {
        String xpath = String.format("//li[contains(@class,'oneAppLauncherItem')]//a[@title='%s']", Item);
        if (WebDriverRunner.getWebDriver().getPageSource().contains(Item)) {
            TestBase.logTestStepPass(Item + " link is not Hidden for SPU Users");
        } else {
            TestBase.logTestStepPass(Item + " link is Hidden for SPU Users");
        }
    }

    public static void DisplayAllItems() {
        $x("//*[@ng-change='vm.changeSelectedItem()']").click();
        $x("//*[@label='100']").click();
        Selenide.sleep(3000);
        SelenideElement ShowMore = $x("//*[text()='Load next 100 entries']");
        do {
            Selenide.sleep(1000);
            $x("//*[text()='Load next 100 entries']").click();
        }
        while (ShowMore.isDisplayed());
    }


    public static String getToastMessage(){
        String toastMessage ="";
        try {
            Log.warn("Getting Toast message...");
            boolean ifError=$("div.slds-notify--toast").attr("class").contains("error");
            toastMessage =$(".slds-notify--toast .slds-notify__content").waitUntil(visible,DEFAULT_WAIT).text();
            if(ifError){
                logInfoStepColored(COLOR.RED,"There is an error on toast message:");
                logTestStepFail(toastMessage);
            }else {
                logTestStepPass(toastMessage);
            }
            logScreenshot("Form message");
            Log.info("Mesage is: " +toastMessage);
            $(".slds-notify--toast .slds-notify__content").waitUntil(hidden,DEFAULT_WAIT);
        } catch (Throwable e) {
            Log.warn("Unanle to get Toast message");
            Log.warn("Reason: " +e.getMessage());
        }
        return toastMessage;
    }

    public static void saveRequest_7498(String request) {
        //String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "Requests.properties";
        String filePath = "src/test/resources/Requests.properties";
        try {
            Properties prop = loadProperties(filePath);
            FileOutputStream fos = new FileOutputStream(filePath);
            prop.setProperty("QA_7498", request);
            prop.store(fos, "Save request data");
            fos.close();
        } catch (Exception ex) {
            Log.error("Problem in Saving Request");
            ex.printStackTrace();
        }
    }//End pf function


    public static void saveRequest_7424(String request) {
        //String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "Requests.properties";
        String filePath = "src/test/resources/Requests.properties";
        try {
            Properties prop = loadProperties(filePath);
            FileOutputStream fos = new FileOutputStream(filePath);
            prop.setProperty("QA_7424", request);
            prop.store(fos, "Save request data");
            fos.close();
        } catch (Exception ex) {
            Log.error("Problem in Saving Request");
            ex.printStackTrace();
        }
    }//End pf function


    public static String GetStoredRequestData(String key) {
        String value = "";
        String filePath = "src/test/resources/Requests.properties";
        try {
            value = (String) loadProperties(filePath).get(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }//End function

    public static void saveRequest_7424_GroupID(String request) {
        //String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "Requests.properties";
        String filePath = "src/test/resources/Requests.properties";
        try {
            Properties prop = loadProperties(filePath);
            FileOutputStream fos = new FileOutputStream(filePath);
            prop.setProperty("QA_7424_GroupID", request);
            prop.store(fos, "Save request data");
            fos.close();
        } catch (Exception ex) {
            Log.error("Problem in Saving Request");
            ex.printStackTrace();
        }
    }

    public static void saveRequest_7498_GroupID(String request) {
        //String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "Requests.properties";
        String filePath = "src/test/resources/Requests.properties";
        try {
            Properties prop = loadProperties(filePath);
            FileOutputStream fos = new FileOutputStream(filePath);
            prop.setProperty("QA_7498_GroupID", request);
            prop.store(fos, "Save request data");
            fos.close();
        } catch (Exception ex) {
            Log.error("Problem in Saving Request");
            ex.printStackTrace();
        }
    }

    private static Properties loadProperties(String propsFilePath) throws IOException {
        Properties props = null;
        try (FileInputStream fis = new FileInputStream(propsFilePath)) {
            props = new Properties();
            props.load(fis);
            fis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return props;
    }//End of loadProperties


    public static SelenideElement getLazyloadTable() {
        return $$(".slds-table.lazy-load-table").filter(visible).first();
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
        return prop.getProperty(key)
                ;
    }

    public static String captureScreenForReport_Base64() {
        TakesScreenshot oScn = (TakesScreenshot) getWebDriver();
        return oScn.getScreenshotAs(OutputType.BASE64);
    }

}//End class

