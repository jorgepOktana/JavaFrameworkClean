import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;
import pageObjects.LoginPage;
import pageObjects.ProcessBuilderPage;
import pageObjects.SalesforceSetupPage;

public class ProcessBuilderTest {
    String username= System.getProperty("username", "local").trim();
    String password= System.getProperty("password", "local").trim();


    @Test
    public void navigateToProcessBuilder()  {
        WebDriver driver = doLogin();

    }

    private WebDriver doLogin(){
        WebDriver driver = new ChromeDriver();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToPage("https://maxitesting-dev-ed.lightning.force.com/lightning/setup/SetupOneHome/home");

        loginPage.getUsernameInput().sendKeys(username);
        loginPage.getPasswordInput().sendKeys(password);
        loginPage.getLoginButton().click();

        return driver;
    }

    private String getRandomName(){
        String random = "Workshop process"+Math.random();
        return random;
    }

    private void oldTest() throws InterruptedException{
        WebDriver driver = doLogin();

        //Navigation to process builder
        SalesforceSetupPage salesforceSetupPage = new SalesforceSetupPage(driver);
        salesforceSetupPage.getSearchBoxInput().click();
        salesforceSetupPage.getSearchBoxInput().sendKeys("Process Builder");
        salesforceSetupPage.getMarkedSearchResult().click();

        //Initiate creation of a new process
        ProcessBuilderPage processBuilderPage = new ProcessBuilderPage(driver);
        processBuilderPage.switchToProcessBuiderIframe();
        processBuilderPage.getNewButton().click();

        //Fill the name and when will it start
        processBuilderPage.getProcessNameInput().sendKeys(getRandomName());
        processBuilderPage.getProcessStartsWhenSelect().selectByVisibleText("A record changes");
        processBuilderPage.getProcessSaveButton().click();

        //Add object to the process builder
        processBuilderPage.getAddObjectButton().click();
        processBuilderPage.getWebElementByXpath("//span[text()='Object']/following-sibling::div/input").click();
        processBuilderPage.getWebElementByXpath("//span[text()='Object']/following-sibling::div/input").sendKeys("Order");
        Thread.sleep(5000);
        processBuilderPage.getWebElementByXpath("//span[text()='Object']/following-sibling::div/input").sendKeys(Keys.ARROW_DOWN);
        processBuilderPage.getWebElementByXpath("//span[text()='Object']/following-sibling::div/input").sendKeys(Keys.RETURN);
        processBuilderPage.getWebElementByXpath("//span[text()='Save']").click();
    }
}
