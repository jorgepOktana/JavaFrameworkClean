import Utils.Utils;
import org.testng.annotations.BeforeClass;
import pageObjects.*;

public class TestBase extends Utils {

    LoginPage loginPage = new LoginPage(driver);
    HomePage homePage = new HomePage(driver);
    ListRequestsPage listRequestsPage = new ListRequestsPage(driver);
    NewRequestPage newRequestPage = new NewRequestPage(driver);
    SelectDocumentsPage selectDocumentsPage = new SelectDocumentsPage(driver);

}
