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

        public static String LOGIN_PAGE_TITLE = "Authentication";
        public static String HOME_PAGE_TITLE = "Home | OnIT";

        public static String HOME_PAGE_TITLE1 = "OnIT";
        public static String HOME_PAGE_TITLE2 = "The one stop for managing OnCue services.";

        public static String Channels_TITLE = "Channels";
        public static String cdnSelectors_TITLE = "CDN Selectors";

        public static String System_Update_TITLE = "System Update";
        public static String System_Update_PAGE_TITLE = "System Update | OnIT";

        public static String Service_TITLE = "Services";
        public static String Service_PAGE_TITLE = "Services | OnIT";

        public static String Service_Tags_TITLE = "Service Tags";
        public static String Service_Tags_PAGE_TITLE = "Service Tags | OnIT";

        public static String DR_Targets_TITLE = "DR Targets";

        public static String Catalog_Features_TITLE = "Catalog - Features";
        public static String Catalog_Series_TITLE = "Catalog - Series";
        public static String Catalog_MissingImages_TITLE = "Catalog - Missing Images";

        public static String RegistrationTokens_Title = "Registration Tokens";

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
        //Adding
        public static String fileDownloadPath = null;

        // config.properties file
        public static String wtime = null;
        public static int waitTime = 0;
        public static String threadSleep = null;
        public static int thSleep = 0;

        private static String intServicesClientId = null;
        private static String intServicesClientSecret = null;
        private static String intServicesURL = null;
        //private static String suServiceURL = null;

        //private static String mmsServiceTag = null;
        private static String mmsServiceType = "MMS";
        //private static String mmsServiceLocation = null;

        private String oncueServiceTag = null;
        private String metadataServiceTag = null;
        private String s2sURL = null;
        private String onitServiceTokenTag = null;
        private String SUServiceType = "SeamlessUpgrade";
        private String DRServiceType = "Inventory";
        private String MMSServiceType = "MMS";
        private String tokenServiceType = "Inventory";

        public static HashMap<String, String> tokensMap = null;

        //OnitHomePage onitHomePageobj = null;

        private static String trustStoreType = null;
        private static String trustStorePath = null;
        private static String trustStorePassword = null;
        private static String keyStoreType = null;
        private static String keyStorePath = null;
        private static String keyStorePassword = null;
        private String webDriverType = null;

        private static final String AUTHORIZATION = "Authorization";
        private static final String DATE_FIELD = "Date";
        private static final String CONTENT_MD5 = "Content-Md5";

        private org.apache.http.conn.ssl.SSLSocketFactory sslSocketFactory = null;

        /**
         * Create constructor
         */
        public OnitTestBase() {

            try {
                webDriverType = System.getProperty("webdriver.type");

                if (webDriverType != null && webDriverType.equals("REMOTE")) {
                    // Load S3 config properties file from Jenkins
                    String configPath = System.getProperty("S3.Config.Path");
                    logger.info("configPath = " + configPath);
                    if(configPath!=null && !configPath.equals("")) {
                        S3ConfigReader.loadProperties(configPath);
                    }
                }

                // Load System Properties
                loadSystemProperties();

                // Load Config Properties
                loadConfigProperties();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

            //setupSystemProperties();

            // set up SSL factory with keystore and trust store to be able to talk to 2-way SSL enabled services.
            sslSocketFactory = setupSSLFactory();

            //make sure we can talk to 2 way enabled services.
            //getJsonResponseforChannels("5c40ae09-6568-3884-99d6-a2ed60ab55c0", "CNNHD");
            //getJsonResponseFromMMS("5c40ae096568388499d6a2ed60ab55c0" );
            //getJsonResponseFromSU("IM4845170776960IWWIB");
            //getJsonResponseFromCatalogs("series","19774d861cb54bdcba836d636e5ff35d");

            //this.getJsonResponseforChannelMap("7531234");

            return driver;
        }

        /**
         * Method for Local Driver setup
         */
        public void setupLocalDriver(String browser) throws Exception {

            if (browser.equalsIgnoreCase("firefox")) {

                //ProfilesIni profile = new ProfilesIni();
                //FirefoxProfile ffProfile = profile.getProfile("default");
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
                dc.setCapability(FirefoxDriver.PROFILE, ffProfile); // new added
                //dc.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
                //UnexpectedAlertBehaviour.DISMISS);
                dc.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
                        UnexpectedAlertBehaviour.ACCEPT);
                ffProfile.setPreference("xpinstall.signatures.required", false);
                driver = new FirefoxDriver(dc);
                //driver.findElement(By.xpath(Constants.articleimagefile)).sendKeys(System.getProperty("user.dir")+"\\src\\test\\java\\com\\sforce\\DjcUIAutomation\\testdata\\"+imagename); //attach Image

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

            } else if (browser.equalsIgnoreCase("IE")) {
                System.setProperty("webdriver.ie.driver",
                        "C:\\Software\\InternetExplorer\\IEDriverServer.exe");
                DesiredCapabilities ieCapabilities = DesiredCapabilities
                        .internetExplorer();
                driver = new InternetExplorerDriver(ieCapabilities);
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
                //ProfilesIni profile = new ProfilesIni();
                // FirefoxProfile ffProfile = profile.getProfile("default");
                FirefoxProfile ffProfile = new FirefoxProfile();
                //ffProfile.setPreference("dom.max_script_run_time", 60);
                ffProfile.setAssumeUntrustedCertificateIssuer(false);

                //Delete the files in the directory else create a new directory
                File dir = new File("src/main/resources/services_export");
                if (dir.exists()) {
                    File[] dirContents = dir.listFiles();
                    for (int i = 0; i < dirContents.length; i++) {
                        dirContents[i].delete();
                    }
                } else {
                    dir.mkdirs();
                }

         /*   String fileSep = System.getProperty("file.separator");
            String s = "src/main/resources/services_export".replace("/", fileSep);
            //String s = "src/main/resources".replace("/", fileSep);
            String filePathToUse = new File(s).getAbsolutePath();
            logger.info("filePathToUse: " + filePathToUse);*/
                logger.info("system get property is: " +System.getProperty("fileDownloadPath"));

                //Set preferences to save the downloaded file to a specific location
                ffProfile.setPreference("browser.download.folderList", 2);
                ffProfile.setPreference("browser.download.manager.showWhenStarting", false);
                ffProfile.setPreference("browser.download.dir", System.getProperty("fileDownloadPath"));
                ffProfile.setPreference("browser.download.downloadDir", System.getProperty("fileDownloadPath"));
                ffProfile.setPreference("browser.download.defaultFolder", System.getProperty("fileDownloadPath"));
                ffProfile.setPreference("browser.helperApps.alwaysAsk.force", false);
                ffProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv");
                ffProfile.setPreference("browser.download.manager.closeWhenDone", false);
                ffProfile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
                ffProfile.setPreference("browser.helperApps.neverAsk.openFile", "text/csv");
                ffProfile.setPreference("browser.download.manager.showAlertOnComplete", false);
                ffProfile.setPreference("xpinstall.signatures.required", false);

                DesiredCapabilities caps = DesiredCapabilities.firefox();
                caps.setCapability(FirefoxDriver.PROFILE, ffProfile);
                caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                //caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
                //UnexpectedAlertBehaviour.ACCEPT);
                caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
                        UnexpectedAlertBehaviour.DISMISS);

                driver = new RemoteWebDriver(new URL(webdriverURL), caps.firefox());

                // Navigate to url
                driver.navigate().to(siteName);
                driver.manage().window().maximize();
                Thread.sleep(5000);
            }
        }

        /* Method to set System properties
         * @throws IOException
         */
        public void loadSystemProperties() throws IOException {
            logger.info("===== Loading System properties.. =====");
            //type = System.getProperty("WebDriver.type");

            //Get TestNG File
            testNGFileName = System.getProperty("suiteXmlFile");

            // navigate url
            siteName = System.getProperty("Onit.SiteName");
            userid = System.getProperty("Onit.User");
            pass = System.getProperty("Onit.Password");

            //Environment Tags
            oncueServiceTag = System.getProperty("Tag.oncueService");
            metadataServiceTag = System.getProperty("Tag.metadataService");

            //get Web Service info
            intServicesClientId = System.getProperty("WebService.IntServicesClientId");
            intServicesClientSecret = System.getProperty("WebService.IntServicesClientSecret");
            s2sURL = System.getProperty("WebService.S2SServiceURL");
            intServicesURL = System.getProperty("WebService.IntServicesURL");

            // get the key store and trust store info
            trustStoreType = System.getProperty("trustStoreType");
            trustStorePath = System.getProperty("trustStorePath");
            trustStorePassword = System.getProperty("trustStorePassword");
            keyStoreType = System.getProperty("keyStoreType");
            keyStorePath = System.getProperty("keyStorePath");
            keyStorePassword = System.getProperty("keyStorePassword");

            //webservices calls  ON or OFF
            webServicesCalls = System.getProperty("webservices.calls");

            //path to file to download when running from remote web driver
            fileDownloadPath = System.getProperty("fileDownloadPath");

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
                siteName = propObj1.getProperty("onitSiteName");
            }

            wtime = propObj1.getProperty("waittime");
            waitTime = Integer.parseInt(wtime);

            threadSleep = propObj1.getProperty("threadsleep");
            thSleep = Integer.parseInt(threadSleep);

            webdriverURL = propObj1.getProperty("webdriver.host");

            if (oncueServiceTag == null) {
                oncueServiceTag = propObj1.getProperty("OnCueServiceTag");
            }

            if (onitServiceTokenTag == null) {
                onitServiceTokenTag = propObj1.getProperty("OnitServiceTokenTag");
            }

            if (metadataServiceTag == null) {
                metadataServiceTag = propObj1.getProperty("MetadataServiceTag");
            }

            if (s2sURL == null) {
                s2sURL = propObj1.getProperty("S2SServiceURL");
            }

            if (intServicesURL == null) {
                intServicesURL = propObj1.getProperty("IntServicesURL");
            }

            logger.info("onitSiteName : " + siteName);
            logger.info("S2SServiceURL : " + s2sURL);
            logger.info("IntServicesURL : " + intServicesURL);
            logger.info("oncueServiceTag : " + oncueServiceTag);
            logger.info("metadataServiceTag : " + metadataServiceTag);

            confobj.close();
        }

        /**
         * Create the SSL factory with appropriate Key Store and Trust store and
         * configure Rest Assured to use that factory so that it can communicate
         * with 2-way SSL enabled services.
         */
        public org.apache.http.conn.ssl.SSLSocketFactory setupSSLFactory() {
            org.apache.http.conn.ssl.SSLSocketFactory sslSocketFactory = null;

            logger.info("Key store type " + keyStoreType);
            logger.info("Key store path " + keyStorePath);
            logger.info("Key store password " + keyStorePassword);
            logger.info("Trust store type " + trustStoreType);
            logger.info("Trust store path " + trustStorePath);
            logger.info("Trust store password " + trustStorePassword);

            try {
                KeyStore keystore = KeyStore.getInstance(keyStoreType);

                InputStream keystoreInput = new FileInputStream(keyStorePath);
                keystore.load(keystoreInput, keyStorePassword.toCharArray());
                logger.info("Keystore has " + keystore.size() + " keys");

                KeyStore truststore = KeyStore.getInstance(KeyStore
                        .getDefaultType());
                InputStream truststoreInput = new FileInputStream(trustStorePath);
                truststore.load(truststoreInput, trustStorePassword.toCharArray());
                logger.info("Truststore has " + truststore.size() + " keys");

                X509HostnameVerifier hostnameVerifier =
                        org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

                sslSocketFactory = new org.apache.http.conn.ssl.SSLSocketFactory(null,
                        keystore, keyStorePassword, truststore, null, hostnameVerifier);
                // sslSocketFactory.setHostnameVerifier(hostnameVerifier);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return sslSocketFactory;
        }

        public String getServiceLocation(String serviceType, String serviceTag) {
            logger.info("Getting Service location for " + serviceType);
            RestAssured.config = RestAssured.config().sslConfig(sslConfig().with().sslSocketFactory(sslSocketFactory).and().allowAllHostnames());

            Response response = null;
            String discovery_url = s2sURL + "/wstags/" + serviceTag + "/discovery";
            logger.info("S2S Service url " + s2sURL);
            logger.info("S2S service discovery url " + discovery_url);
            try {
                response = given().get(discovery_url);

            } catch (Exception ex) {
                System.out
                        .println("Failed to get response from S2S service "
                                + ex.getMessage());
                ex.printStackTrace();
            }
            logger.info("S2S Reponse code " + response.getStatusCode());
            JSONObject jsonTokens = (JSONObject) JSONValue.parse(response.getBody().asString());
            logger.info("JSON Response from S2S " + jsonTokens.toJSONString());
            JSONArray resultsArray = (JSONArray) jsonTokens.get("Services");

            JSONObject childJson = null;
            String service_type = "";

            Iterator itr = resultsArray.iterator();
            while (itr.hasNext()) {
                childJson = (JSONObject) itr.next();
                service_type = (String) childJson.get("serviceType");
                if (service_type != null && service_type.equals(serviceType)) {
                    break;
                }

            }
            String location = (String) childJson.get("serviceLocation");
            logger.info("service location for " + serviceType + "is " + location);
            return location;

        }

        // ======= Web Service call Base Methods ======= (Jayashri) //

        /**
         * Makes a channels call to MMS service passing in the entityId and returns
         * the json response.
         *
         * @param entityId entityId for the channel to query MMS service
         * @return Json response from MMS service
         */
        public Response getJsonResponseFromMMS(String entityId) {

            // String service_location = getServiceLocation(mmsServiceType);
            // System.out.println("service location from ONIT look up " +  service_location);
            String service_location = getServiceLocation(MMSServiceType, metadataServiceTag);
            if (service_location == null) {
                System.out.println("ONIT look up for " + mmsServiceType + " failed");
            }
            logger.info("MMS service location " + service_location);
            String url;

            if (service_location.endsWith("/")) {
                url = service_location + "channels/"
                        + stripOutDashesFromEntityId(entityId);
                logger.info("MMS url " + url);
            } else {
                url = service_location + "/channels/"
                        + stripOutDashesFromEntityId(entityId);
                logger.info("MMS url " + url);
            }


	/*	Response mmsResponse = given().config(
                config().sslConfig(
						sslConfig().with().sslSocketFactory(sslSocketFactory)
								.and().allowAllHostnames())).get(url); */
            RestAssured.config = RestAssured.config().sslConfig(sslConfig().with().sslSocketFactory(sslSocketFactory).and().allowAllHostnames());
            Response mmsResponse = given().get(url);

            logger.info("MMS Response  " + mmsResponse);
            logger.info("MMS status code  " + mmsResponse.getStatusCode());

		/*JSONObject jsonTokens = (JSONObject) JSONValue.parse(mmsResponse
                .getBody().asString());
		logger.info("JSON Response from MMS " + jsonTokens.toJSONString());
		return jsonTokens;
		*/
            return mmsResponse;

        }

        /**
         * Makes a  call to Seamless Upgrade service and returns the group details
         * for a particular groupId
         *
         * @param groupId groupId for the channel to query MMS service
         * @return Json response from MMS service
         */
        public JSONObject getJsonResponseFromSU(String groupId) {

            String su_location = getServiceLocation(SUServiceType, oncueServiceTag);
            logger.info("SU service location " + su_location);
            String url;

            if (su_location.endsWith("/")) {
                url = su_location + "groups/" + groupId;
                logger.info("SU url " + url);
            } else {
                url = su_location + "/groups/" + groupId;
                logger.info("SU url " + url);
            }
            RestAssured.config = RestAssured.config().sslConfig(sslConfig().with().sslSocketFactory(sslSocketFactory).and().allowAllHostnames());

            Response suResponse = given().get(url);

            logger.info("su Response  " + suResponse);
            logger.info("su status code  " + suResponse.getStatusCode());

            JSONObject jsonTokens = (JSONObject) JSONValue.parse(suResponse
                    .getBody().asString());
            logger.info("JSON Response from SU " + jsonTokens.toJSONString());
            return jsonTokens;

        }

        /**
         * Makes MMS call for DR Targets
         */
        public Response getJsonResponseFromDRTargets(String releaseType, String numberFlavor) {
            String dr_location = getServiceLocation(DRServiceType, oncueServiceTag);
            logger.info("DR service location " + dr_location);

            if (dr_location == null) {
                System.out.println("ONIT look up for " + DRServiceType + " failed");
            }
            String url;

            if (dr_location.endsWith("/")) {
                url = dr_location + "v2/artifact/stb-intel-client/" + releaseType + "/" + numberFlavor;
                logger.info("DR url " + url);
            } else {
                url = dr_location + "/v2/artifact/stb-intel-client/" + releaseType + "/" + numberFlavor;
                logger.info("DR url " + url);
            }

            RestAssured.config = RestAssured.config().sslConfig(sslConfig().with().sslSocketFactory(sslSocketFactory).and().allowAllHostnames());
            Response drResponse = given().get(url);

            logger.info("DR Response  " + drResponse);
            logger.info("DR status code  " + drResponse.getStatusCode());
            return drResponse;

        }


        /**
         * Makes MMS call for Registration Tokens
         */

        public Response getJsonResponseFromInventory(String family) {
            String url;
            String token_location = getServiceLocation(tokenServiceType, onitServiceTokenTag);
            logger.info("Token service location " + token_location);

            if (token_location == null) {
                System.out.println("ONIT look up for " + tokenServiceType + " failed");
            }
            if (token_location.endsWith("/")) {
                url = token_location + "v2/artifact/registration-token/oncue-mobile-app/" + family;
                logger.info("Token url " + url);
            } else {
                url = token_location + "/v2/artifact/registration-token/oncue-mobile-app/" + family;
                logger.info("Token url " + url);
            }

            RestAssured.config = RestAssured.config().sslConfig(sslConfig().with().sslSocketFactory(sslSocketFactory).and().allowAllHostnames());
            Response tokenResponse = given().get(url);

            logger.info("Token Response  " + tokenResponse);
            logger.info("Token status code  " + tokenResponse.getStatusCode());
            return tokenResponse;

        }

        /**
         * Make MMS call for Catalogs
         *
         * @param catalogType, catalogId
         *                     catalogType and catalogId for the catalog to query MMS service
         * @return Json response from MMS service
         */
        public Response getJsonResponseFromCatalogs(String catalogType, String catalogId) {

            String catalog_location = getServiceLocation(MMSServiceType, metadataServiceTag);
            logger.info("catalog service location " + catalog_location);
            if (catalog_location == null) {
                System.out.println("ONIT look up for " + mmsServiceType + " failed");
            }
            String url;

            if (catalog_location.endsWith("/")) {
                url = catalog_location + "curate/artwork/" + catalogType + "/" + catalogId;
                logger.info("catalog url " + url);
            } else {
                url = catalog_location + "/curate/artwork/" + catalogType + "/" + catalogId;
                logger.info("catalog url " + url);
            }

            RestAssured.config = RestAssured.config().sslConfig(sslConfig().with().sslSocketFactory(sslSocketFactory).and().allowAllHostnames());
            Response mmsResponse = given().get(url);

            logger.info("MMS Response  " + mmsResponse);
            logger.info("MMS status code  " + mmsResponse.getStatusCode());

        /*JSONObject jsonTokens = (JSONObject) JSONValue.parse(mmsResponse
                .getBody().asString());
        logger.info("JSON Response from MMS " + jsonTokens.toJSONString());
        return jsonTokens;
        */
            return mmsResponse;
        }

        /**
         * Make MMS call for Catalogs Metadata
         *
         * @param catalogType, catalogId
         *                     catalogType and catalogId for the catalog to query MMS service
         * @return Json response from MMS service
         */
        public Response getJsonResponseFromCatalogsMetadata(String catalogType, String catalogId) {

            String catalog_location = getServiceLocation(MMSServiceType, metadataServiceTag);
            logger.info("catalog service location " + catalog_location);
            if (catalog_location == null) {
                System.out.println("ONIT look up for " + mmsServiceType + " failed");
            }
            String url;

            if (catalog_location.endsWith("/")) {
                url = catalog_location + "curate/metadata/" + catalogType + "/" + catalogId;
                logger.info("catalog url " + url);
            } else {
                url = catalog_location + "/curate/metadata/" + catalogType + "/" + catalogId;
                logger.info("catalog url " + url);
            }

            RestAssured.config = RestAssured.config().sslConfig(sslConfig().with().sslSocketFactory(sslSocketFactory).and().allowAllHostnames());
            Response mmsResponse = given().get(url);

            logger.info("MMS Response  " + mmsResponse);
            logger.info("MMS status code  " + mmsResponse.getStatusCode());

        /*JSONObject jsonTokens = (JSONObject) JSONValue.parse(mmsResponse
                .getBody().asString());
        logger.info("JSON Response from MMS " + jsonTokens.toJSONString());
        return jsonTokens;
        */
            return mmsResponse;
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

        /*
         * Integration Services that talk to devices use HMAC authentication unlike
         * other services that talk to Discovery. This method generates a dynamic
         * HMAC token for the client Id and client secret and uses it as an
         * authentication key to talk to integration services. This method supports
         * "GET" calls only.
         *
         * @param user clientId that is unique per user
         *
         * @param secretKey shared key between client and server.
         *
         * @param service_url DNS entry of the service
         *
         * @param body body content for the message. This will be empty for GET
         * calls.
         *
         * @return return the Response object from Integration Services
         *
         * @throws IOException
         *
         * @throws NoSuchAlgorithmException
         */

        public Response makeHTTPCallUsingHMAC(String user, String secretKey,
                                              String service_url, String body) throws IOException,
                NoSuchAlgorithmException, RuntimeException {

            URL url = new URL(service_url);
            SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dt = sdf.format(new Date());

            // Assumes it is a GET request, hence send empty body. sends empty
            // content type as well.
            String hmac = getHmac(
                    secretKey,
                    (String.format("%s:%s:%s:%s:%s", "GET", "", dt, "",
                            url.getPath())));
            logger.info("HTTP Request to int-services ");
            logger.info("user " + user);
            logger.info("secret key " + secretKey);
            logger.info("hmac " + hmac);
            logger.info("Authorization " + getAuthHeader(user, hmac));
            logger.info("DATE " + dt);
            logger.info("PATH " + url.getPath());
            logger.info("url " + service_url);

            RestAssured.config = RestAssured.config().sslConfig(sslConfig().with().sslSocketFactory(sslSocketFactory).and().allowAllHostnames());

            Response response = given().headers(AUTHORIZATION,
                    getAuthHeader(user, hmac), DATE_FIELD, dt, CONTENT_MD5, "")
                    .get(service_url);

            logger.info("Int Services Response  " + response);
            logger.info("Int Services code  " + response.getStatusCode());
            return response;

        }

        public Response makeHTTPCallUsingoAUth(String service_url) throws IOException,
                NoSuchAlgorithmException, RuntimeException {

            URL url = new URL(service_url);
            GenerateOAuthTokens oAUth = new GenerateOAuthTokens();
            System.out.println("oAUth = " + oAUth);
            String token = oAUth.getClientToken();
            logger.info("token:" + token);
            RestAssured.config = RestAssured.config().sslConfig(sslConfig().with().sslSocketFactory(sslSocketFactory).and().allowAllHostnames());

            Response response = given().headers(AUTHORIZATION,
                    "Bearer " + token)
                    .get(service_url);

            logger.info("Int Services Response  " + response);
            logger.info("Int Services code  " + response.getStatusCode());
            return response;

        }

        /**
         * This method returns MD5 value of the input string. Currently not used as
         * all our calls are "GET"
         *
         * @param input body of PUT/POST/DELETE call.
         * @return MD5 value of the input
         * @throws NoSuchAlgorithmException
         */
        public String getMD5(String input) throws NoSuchAlgorithmException {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            return digest.digest().toString();
        }

        /**
         * Returns the dynamic HMAC token for the input string
         *
         * @param secret shared key between client and server.
         * @param data   input string
         * @return dynamic HMAC token for the input string
         */

        private String getHmac(String secret, String data) {

            Base64 base64Obj = new Base64();

            try {
                SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(),
                        "HmacSHA1");
                Mac mac = Mac.getInstance(("HmacSHA1"));
                mac.init(signingKey);
                byte[] rawHmac = mac.doFinal(data.getBytes());
                byte[] resultArray = base64Obj.encode(rawHmac);
                String result = new String(resultArray);
                return result;
            } catch (GeneralSecurityException e) {
                throw new IllegalArgumentException();
            }

        }

        /**
         * Formats the input token in the format expected by Integration services
         *
         * @param user clientId
         * @param hmac hmac token corresponding to the input string.
         * @return
         */
        private String getAuthHeader(String user, String hmac) {
            String result = String.format("INTL %s:%s", user, hmac);
            return result;
        }

        /**
         * Makes a channels call to Integration service passing in entityId and
         * returns the json response.
         *
         * @param entityId entityId for the channel
         * @return Json response from Integration Packages service
         */
        public Response getJsonResponseforChannels(String entityId, String version) {

            sslSocketFactory = setupSSLFactory();

            String url = intServicesURL + "/channel/" + entityId + "?version=" + version;
            Response response = null;
            System.out.println("intServicesURL: " + url);
            try {
           /* response = makeHTTPCallUsingHMAC(intServicesClientId,
                    intServicesClientSecret, url, "");*/
                response = makeHTTPCallUsingoAUth(url);

                System.out.println("RESPONSE:");
                response.getBody().prettyPrint();

            } catch (Exception e) {
                System.out
                        .println("Failed to get response from Integration service "
                                + e.getMessage());
                e.printStackTrace();
            }

		/*JSONObject jsonTokens = (JSONObject) JSONValue.parse(response.getBody()
                .asString());
		logger.info("JSON Response from Int Service " + jsonTokens.toJSONString());
		return jsonTokens;
		*/
            return response;
        }

        /**
         * Makes a channels call to Integration service passing in entityId and
         * returns the json response.
         *
         * @param entityId entityId for the channel
         * @return Json response from Integration Packages service
         */
        public Response getJsonResponseforOnCueChannels(String entityId) {

            sslSocketFactory = setupSSLFactory();

            String url = intServicesURL + "/channel/" + entityId;
            Response response = null;
            System.out.println("ONIT Service URL: " + url);
            try {
                response = makeHTTPCallUsingoAUth(url);

                System.out.println("response = " + response.getBody().prettyPrint());

            } catch (Exception e) {
                System.out.println("Failed to get response from Integration service " + e.getMessage());
                e.printStackTrace();
            }

            return response;
        }

        /**
         * Makes a channelMap call to Integration service passing in channelMap id
         * and returns the json response.
         *
         * @param channelMapId channelMapId for the channelMap
         * @return Json response from Integration Packages service
         */
        public Response getJsonResponseforChannelMap(String channelMapId) {

            String url = intServicesURL + "/channelMap/" + channelMapId;
            Response response = null;
            try {
                response = makeHTTPCallUsingHMAC(intServicesClientId,
                        intServicesClientSecret, url, "");
            } catch (Exception e) {
                System.out
                        .println("Failed to get response from Integration service "
                                + e.getMessage());
            }

            return response;

        }

        /**
         * This method strips out the dashes from entityId as MMS expects entityIds
         * without dashes.
         *
         * @param entityId entityId for the channel
         * @return entityId entityId without the dashes.
         */
        public String stripOutDashesFromEntityId(String entityId) {
            assert (entityId != null);
            String idwithoutDashes = entityId.replaceAll("[^a-zA-Z0-9]", "");
            logger.info("EntityId without dashes " + idwithoutDashes);
            return idwithoutDashes;
        }

        /**
         * Returns the version object with name matching version_name.
         *
         * @param jsonTokens Json object which includes response from MMS service.
         *                   key to look up value within a version object
         * @return JSONObject Jsonobject corresponding to version name.
         */
        @SuppressWarnings("rawtypes")
        public JSONObject getVersionObject(JSONObject jsonTokens,
                                           String version_name) {

            assert (jsonTokens != null);

            JSONArray resultsArray = (JSONArray) jsonTokens.get("versions");

            JSONObject childJson = null;
            String current_version = "";

            Iterator itr = resultsArray.iterator();
            while (itr.hasNext()) {
                childJson = (JSONObject) itr.next();
                current_version = (String) childJson.get("name");
                if (current_version != null && current_version.equals(version_name)) {
                    return childJson;
                }

            }
            return childJson;
        }

        /**
         * Locates the neighborhood object with name matching neighborhood_name.
         *
         * @param jsonTokens Json object which includes response from IntPackages service.
         * @return Jsonobject corresponding to version name.
         */
        @SuppressWarnings("rawtypes")
        public JSONObject getNeighborhoodObject(JSONObject jsonTokens,
                                                String neighborhood_name) {

            assert (jsonTokens != null);

            JSONArray resultsArray = (JSONArray) jsonTokens.get("neighborhoods");

            JSONObject childJson = null;
            String current_neighborhood = "";

            Iterator itr = resultsArray.iterator();
            while (itr.hasNext()) {
                childJson = (JSONObject) itr.next();
                current_neighborhood = (String) childJson.get("name");
                if (current_neighborhood != null
                        && current_neighborhood.equals(neighborhood_name)) {
                    return childJson;
                }

            }
            return childJson;
        }

        /**
         * Method to sanitizeJson
         *
         * @param parentJson , arrayName, parentElement, childElement
         * @return value
         */
        public String sanitizeJson(JSONObject parentJson, String arrayName, String parentElement, String childElement) {
            JSONArray resultsArray = (JSONArray) parentJson.get(arrayName);
            @SuppressWarnings("rawtypes")
            Iterator itr = resultsArray.iterator();
            JSONObject childJson = null;
            childJson = (JSONObject) itr.next();
            Set<String> keys = childJson.keySet();
            Iterator<String> a = keys.iterator();
            String key = null;
            Boolean keyResult = true;
            while (a.hasNext() && keyResult) {
                key = (String) a.next();
                if (!(key.equalsIgnoreCase(parentElement))) {
                    keyResult = true;
                } else {
                    keyResult = false;
                }
            }

            if (!(childElement == null)) {
                String value = (String) childJson.get(key).toString();
                JSONObject flavorObject = (JSONObject) JSONValue.parse(value);
                String returnValue = (String) flavorObject.get(childElement);
                return returnValue;
            } else {
                String returnValue = (String) childJson.get(key).toString();
                return returnValue;
            }

        }

        /**
         * Method to get keyvalue Json
         *
         * @param parentJson , arrayName, parentElement, childElement
         * @return array value
         */
        @SuppressWarnings("unchecked")
        public String[] keyvalueJson(JSONObject parentJson, String arrayName, String parentElement, String childElement) {
            String key = null;
            String value = null;
            String foundValue = null;
            String[] resultValue = new String[20];
            int i = 0;
            JSONArray resultsArray = (JSONArray) parentJson.get(arrayName);
            @SuppressWarnings("rawtypes")
            Iterator itr = resultsArray.iterator();
            JSONObject childJson = (JSONObject) itr.next();
            Set<String> keys = childJson.keySet();
            Iterator<String> a = keys.iterator();

            while (a.hasNext()) {
                key = (String) a.next();
                System.out.println("KEY : :" + key);
                if (key.equalsIgnoreCase(parentElement)) {
                    if (!(childElement == null)) {
                        value = (String) childJson.get(key).toString();
                        JSONObject flavorObject = (JSONObject) JSONValue.parse(value);
                        foundValue = (String) flavorObject.get(childElement);
                    } else {
                        foundValue = (String) childJson.get(key).toString();
                    }
                    System.out.println(" i :: " + i + "  foundvalue :: " + foundValue);
                    resultValue[i] = foundValue;
                    i++;
                }
            }
            return resultValue;
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
