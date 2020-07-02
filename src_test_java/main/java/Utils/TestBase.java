package Utils;

import com.relevantcodes.extentreports.LogStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.io.IOException;

import static Utils.Common.GetUserData;
import static Utils.ReportUtil.getTest;

@Listeners({TestEvents.class, SuiteEvents.class})

public class TestBase {

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.SSLConfig.sslConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

    public class HasbroTestBase {

        public static Logger logger = Logger.getLogger(HasbroTestBase.class);

        private static String MAC_DRIVER = "/chromedriver-mac";
        private static String LINUX_DRIVER = "/chromedriver-linux";
        private static String WINDOWS_DRIVER = "/chromedriver.exe";
        private static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

        public static WebDriver driver = null;
        public static String webdriverURL = null;

        // system properties
        public static String siteName = null;
        public static String userid = null;
        public static String pass = null;
        public static String type = null;
        public static String testNGFileName = null;


        // config.properties file
        public static String wtime = null;
        public static int waitTime = 0;
        public static String threadSleep = null;
        public static int thSleep = 0;
        private String webDriverType = null;

        /**
         * Create constructor
         */
        public HasbroTestBase() {

        /**
         * Method for browser setup
         */
        @Parameters("browser")
        @BeforeClass
        public WebDriver browserSetup(String browser) throws Exception {

            logger.info("BeforeTest Browser SetUp: " + browser);
            logger.info("webDriverType = " + webDriverType);

            if (webDriverType != null && webDriverType.equals("REMOTE")) {
                setupRemoteDriver(browser);
                logger.info("Set up Remote WebDriver " + driver);
            } else {
                setupLocalDriver(browser);
                logger.info("Set up local WebDriver " + driver);
            }

            return driver;
        }

        /**
         * Method for Local Driver setup
         */
        public void setupLocalDriver(String browser) throws Exception {

            if (browser.equalsIgnoreCase("firefox")) {

                FirefoxProfile ffProfile = new FirefoxProfile();
                ffProfile.setAssumeUntrustedCertificateIssuer(false);

                //Delete the files in the directory
                File dir = new File("src/main/resources/services_export");
                if (dir.exists()) {
                    File[] dirContents = dir.listFiles();
                    for (int i = 0; i < dirContents.length; i++) {
                        dirContents[i].delete();
                    }
                } else {
                    dir.mkdirs();
                }

                String fileSep = System.getProperty("file.separator");
                String s = "src/main/resources/services_export".replace("/", fileSep);
                //String s = "src/main/resources".replace("/", fileSep);
                String filePathToUse = new File(s).getAbsolutePath();
                logger.info("filePathToUse: " + filePathToUse);

                ffProfile.setPreference("browser.download.folderList", 2);
                ffProfile.setPreference("browser.download.manager.showWhenStarting", false);
                ffProfile.setPreference("browser.download.dir", filePathToUse);
                ffProfile.setPreference("browser.download.downloadDir", filePathToUse);
                ffProfile.setPreference("browser.download.defaultFolder", filePathToUse);
                ffProfile.setPreference("browser.helperApps.alwaysAsk.force", false);
                ffProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv");
                ffProfile.setPreference("browser.download.manager.closeWhenDone", false);
                ffProfile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
                ffProfile.setPreference("browser.helperApps.neverAsk.openFile", "text/csv");
                ffProfile.setPreference("browser.download.manager.showAlertOnComplete", false);
                // ffProfile.setPreference("pdfjs.disabled", true);

                DesiredCapabilities dc = DesiredCapabilities.firefox();
                dc.setCapability(FirefoxDriver.PROFILE, ffProfile);
                dc.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
                        UnexpectedAlertBehaviour.ACCEPT);
                ffProfile.setPreference("xpinstall.signatures.required", false);
                driver = new FirefoxDriver(dc);

            }
            else if (browser.equalsIgnoreCase("chrome")) {

                if (System.getProperty("os.name").contains("Mac")) {
                    File cDriver = new File(OnitTestBase.class.getResource(
                            MAC_DRIVER).getFile());

                    // Is it executable
                    if (!cDriver.canExecute()) {
                        cDriver.setExecutable(true);
                    }
                    System.setProperty("webdriver.chrome.driver",
                            OnitTestBase.class.getResource(MAC_DRIVER).getFile());

                } else if (System.getProperty("os.name").contains("Linux")) {
                    File cDriver = new File(OnitTestBase.class.getResource(
                            LINUX_DRIVER).getFile());

                    // Is it executable
                    if (!cDriver.canExecute()) {
                        cDriver.setExecutable(true);
                    }
                    System.setProperty("webdriver.chrome.driver",
                            OnitTestBase.class.getResource(LINUX_DRIVER).getFile());
                } else {
                    System.setProperty("webdriver.chrome.driver",
                            OnitTestBase.class.getResource(WINDOWS_DRIVER)
                                    .getFile());
                }

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                driver = new ChromeDriver(options);

            }

            driver.manage().timeouts().implicitlyWait(waitTime + 4, TimeUnit.SECONDS);

            // Navigate to url
            driver.navigate().to(siteName);
            driver.manage().window().maximize();
            Thread.sleep(5000);
        }

        /**
         * Method for Remote Driver setup
         */
        @SuppressWarnings("static-access")
        public void setupRemoteDriver(String browser) throws Exception {

            if (browser == null || browser.equalsIgnoreCase("Chrome")) {

                logger.info("Setting up Remote Webdriver for Chrome " + webdriverURL);

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");

                DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
                desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
                driver = new RemoteWebDriver(new URL(webdriverURL), desiredCapabilities);

                // Navigate to url
                driver.navigate().to(siteName);
                Thread.sleep(5000);
            }
            else if (browser.equalsIgnoreCase("IE")) {
                driver = new RemoteWebDriver(new URL(webdriverURL), DesiredCapabilities.internetExplorer());
            }
            else if (browser.equalsIgnoreCase("firefox")) {
                FirefoxProfile ffProfile = new FirefoxProfile();
                ffProfile.setAssumeUntrustedCertificateIssuer(false);
            }

            // Navigate to url
            driver.navigate().to(siteName);
            driver.manage().window().maximize();
            Thread.sleep(5000);
        }

        /* Method to set System properties
         * @throws IOException
         */
        public void loadSystemProperties() throws IOException {
            logger.info("===== Loading System properties.. =====");
            //type = System.getProperty("WebDriver.type");

            //Get TestNG File THIS TO BE CONFIGURED
           // testNGFileName = System.getProperty("suiteXmlFile");

            // navigate url
            //siteName = System.getProperty("Hasbro.SiteName");
            //userid = System.getProperty("Hasbro.User");
            //pass = System.getProperty("Hasbro.Password");

        }

        /**
         * Method to read data from Config properties file
         *
         * @throws IOException
         */
        public void loadConfigProperties() throws IOException {
            logger.info("Loading Config.properties..");
            InputStream confobj = this.getClass().getResourceAsStream(
                    "/Config.properties");
            Properties propObj1 = new Properties();
            propObj1.load(confobj);

            if (siteName == null) {
                siteName = propObj1.getProperty("hasbroSiteName");
            }

            wtime = propObj1.getProperty("waittime");
            waitTime = Integer.parseInt(wtime);

            threadSleep = propObj1.getProperty("threadsleep");
            thSleep = Integer.parseInt(threadSleep);

            webdriverURL = propObj1.getProperty("webdriver.host");

        }


        /**
         * Method to take screenshots on failure
         */
        @AfterMethod(alwaysRun = true)
        public void catchFailurescreenshot(ITestResult result)
                throws InterruptedException {

            // don't attempt to take a screen shot if the browser already died.
            if (driver == null) {
                return;
            }
            String dateSuffix = new SimpleDateFormat("MM_dd_yyyy_hh_mm_ss").format(
                    Calendar.getInstance().getTime()).toString();
            String methodName = result.getName();
            if (!result.isSuccess()) {
                // Taking screentshot of failed_screens...
                TakesScreenshot screenshot = (TakesScreenshot) driver;
                Assert.assertNotNull(screenshot, "Can't get TakesScreenshot!"
                        + screenshot);
                if (screenshot != null) {
                    File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
                    File destFile = new File("target/Screenshots" + File.separator
                            + methodName + "-" + dateSuffix + ".png");
                    try {
                        FileUtils.copyFile(srcFile, destFile);
                    } catch (IOException ioe) {
                        System.out
                                .println("Exception while creating the screenshot file!"
                                        + ioe.getMessage());
                    }
                } else
                    return;
                Thread.sleep(thSleep);
            }
        }

        public void sleep(long milliseconds) {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }

        /**
         * Method to close the Browser
         */
        @AfterClass
        public void closeBrowser() throws Exception {
            //don't attempt to close the browser if it already died.
            if (driver == null) {
                return;
            }
            driver.close();
            driver.quit();
        }

    }



}
