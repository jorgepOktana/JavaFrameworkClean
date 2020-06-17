import TestData.Users;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageObjects.HomePage;
import pageObjects.LoginPage;

public class LoginTest {

    String baseUrl = "https://login.salesforce.com";

    WebDriver driver = new ChromeDriver();
    LoginPage loginPage = new LoginPage(driver);
    HomePage homePage = new HomePage(driver);

    @Test
    public void loginTest() throws Exception {
        loginPage.loginAs(Users.TYPE.Requester_Admin);
        Assert.assertTrue(homePage.isCurrentPage());
        driver.close();
    }

}
