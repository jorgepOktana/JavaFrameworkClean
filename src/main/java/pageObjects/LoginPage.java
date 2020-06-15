package pageObjects;

import TestData.GlobalTestData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import Utils.Log;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;


public class LoginPage extends BasePage{

	@FindBy(id = "username")
	public WebElement txtUsername;

	@FindBy(id = "password")
	public WebElement txtPassword;

	@FindBy(id = "Login")
	public WebElement btnLogin;

	@FindBy(id = "newpassword")
	public WebElement newPassword;

	@FindBy(id = "header")
	public WebElement header;

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public boolean isCurrentPage(){
		return txtUsername.isDisplayed();
	}

	public boolean isLogin = false;

	/**
	 * <h1>Login User <h1/>
	 * <p>Purpose:This method is used for login to application</p>
	 *
	 * @param UserName,Password
	 * @throws Exception
	 */
	public void LoginUser(String UserName, String Password) throws Exception {
		enterInTextBox(txtUsername, UserName);
		enterInTextBox(txtPassword, Password);
		btnLogin.click();
		waitForPageLoadToComplete();
		boolean isLogin = !txtUsername.isDisplayed();
		//Verify if Change password screen appear
		if (isLogin) {
			if (newPassword.isDisplayed()) {
				Log.error("Salesforce message: " + header.getText());
				Log.info("Login Failed> SALESFORCE CHANGE PASSWORD WINDOW APPEARED");
			}
		}
		if (!isLogin) {
			Log.info("Login Failed");
		}
		waitForPageLoadToComplete();
	}

	public void loginAs(String actor) throws Exception {
		driver.switchTo().defaultContent();

		if (this.isLogin()) {
			if(!this.getCurrentLoginUser().equals(actor))
			{
				logout();
			}else {
				Log.info( actor +" is already logged in");
				return;
			}
		}
		switch (actor) {
			case "Requestor_Admin":
				LoginUser(GlobalTestData.Requester_Admin.getUserId(), GlobalTestData.Requester_Admin.getPassword());
				this.isLogin = true;
				setCurrentLoginUser(actor);
				SwitchToLightiningView();
				Log.info("Requestor_Admin logged in successfully.");
				GlobalTestData.RequesterUrl = driver.getCurrentUrl();
				break;

			case "Responder_Admin":
				LoginUser(GlobalTestData.Responder_Admin.getUserId(), GlobalTestData.Responder_Admin.getPassword());
				this.isLogin = true;
				setCurrentLoginUser(actor);
				SwitchToLightiningView();
				Log.info("Responder_Admin logged in successfully.");
				GlobalTestData.ResponderUrl = driver.getCurrentUrl();
				break;

			case "Laboratory_Admin":
				LoginUser(GlobalTestData.Lab_Admin.getUserId(), GlobalTestData.Lab_Admin.getPassword());
				this.isLogin = true;
				setCurrentLoginUser(actor);
				SwitchToLightiningView();
				Log.info("Laboratory_Admin logged in successfully.");
				GlobalTestData.LabUrl = driver.getCurrentUrl();
				break;
			case "Requestor_SPU":
				LoginUser(GlobalTestData.Requester_SPU.getUserId(), GlobalTestData.Requester_SPU.getPassword());
				this.isLogin = true;
				setCurrentLoginUser(actor);
				SwitchToLightiningView();
				Log.info("Requestor_SPU logged in successfully.");
				GlobalTestData.RequesterUrl = driver.getCurrentUrl();
				break;

			case "Responder_SPU":
				LoginUser(GlobalTestData.Responder_SPU.getUserId(), GlobalTestData.Responder_SPU.getPassword());
				this.isLogin = true;
				setCurrentLoginUser(actor);
				SwitchToLightiningView();
				Log.info("Responder_SPU logged in successfully.");
				GlobalTestData.ResponderUrl = driver.getCurrentUrl();
				break;

			case "Laboratory_SPU":
				LoginUser(GlobalTestData.Lab_SPU.getUserId(), GlobalTestData.Lab_SPU.getPassword());
				this.isLogin = true;
				setCurrentLoginUser(actor);
				SwitchToLightiningView();
				Log.info("Laboratory_SPU logged in successfully.");
				GlobalTestData.LabUrl = driver.getCurrentUrl();
				break;
			default:
				Assert.assertTrue(false, "Not a valid user type.");
		}
	}

}

