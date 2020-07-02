import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;
import pageObjects.LoginPage;

public class ProcessBuilderTest {
    String username= System.getProperty("username", "local").trim();
    String password= System.getProperty("password", "local").trim();
    String sandBoxURL= System.getProperty("sandboxURL", "local").trim();

    public static String Requestor_Admin = "Requestor_Admin";
    public static String Responder_Admin = "Responder_Admin";
    public static String Laboratory_Admin = "Laboratory_Admin";
    public static String Requestor_SPU = "Requestor_SPU";
    public static String Responder_SPU = "Responder_SPU";
    public static String Laboratory_SPU = "Laboratory_SPU";

    public static String baseUrl = "https://login.salesforce.com";

    @Test
    public void navigateToProcessBuilder() throws Exception {
        WebDriver driver = doLogin();

    }

    private WebDriver doLogin() throws Exception {
        WebDriver driver = new ChromeDriver();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToPage(baseUrl);
//        loginPage.loginAs(Requestor_Admin);
        loginPage.LoginUser("username", "password");
        return driver;
    }

    private String getRandomName(){
        String random = "Workshop process"+Math.random();
        return random;
    }

}
