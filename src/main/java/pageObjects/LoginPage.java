package pageObjects;

import TestData.GlobalTestData;
import TestData.Users;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import Utils.Log;
import org.openqa.selenium.support.FindBy;


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
	public Users.TYPE lastLoginUser;

	/**
	 * <h1>Login User <h1/>
	 * <p>Purpose:This method is used for login to application</p>
	 *
	 * @param UserName,Password
	 * @throws Exception
	 */
	public void LoginUser(String UserName, String Password) throws Exception {
		enterText(txtUsername, UserName);
		enterText(txtPassword, Password);
		clickButton(btnLogin);
		sleepSeconds(5);
		boolean isLogin = !isElementPresent("id", "username");
		//Verify if Change password screen appear
		if (isLogin) {
			if (isElementPresent("id", "newpassword")){
				Log.error("Salesforce message: " + header.getText());
				Log.info("Login Failed> SALESFORCE CHANGE PASSWORD WINDOW APPEARED");
			}
		}
		if (!isLogin) {
			Log.info("Login Failed");
		}
//		getMainWindow();
//		waitForPageLoad();
	}

	public void loginAs(Users.TYPE userType) throws Exception {
		if (userType.equals(this.lastLoginUser)) {
			Log.info(userType + " user is already logged in");
		} else {
			if (this.isLogin) {
				this.logout();
			}
			switch(userType) {
				case Requester_Admin:
					driver.navigate().to(Users.Requester_Admin.getUrl());
//					waitForPageLoad();
					LoginUser(Users.Requester_Admin.getUserId(), Users.Requester_Admin.getPassword());
//					switchToLightning();
					Log.info(userType.name() + " logged in successfully.");
					GlobalTestData.RequesterUrl = GetCurrentUrl();
					this.isLogin = true;
					this.lastLoginUser = userType;
					break;
				case Requester_SPU:
					driver.navigate().to(Users.Requester_Admin.getUrl());
					LoginUser(Users.Requester_SPU.getUserId(), Users.Requester_SPU.getPassword());
					switchToLightning();
					Log.info(userType.name() + " logged in successfully.");
					GlobalTestData.RequesterUrl = GetCurrentUrl();
					this.isLogin = true;
					this.lastLoginUser = userType;
					break;

				case QE:
					driver.navigate().to(Users.Requester_Admin.getUrl());
					LoginUser(Users.QE.getUserId(), Users.QE.getPassword());
					switchToLightning();
					Log.info(userType.name() + " logged in successfully.");
					GlobalTestData.RequesterUrl = GetCurrentUrl();
					this.isLogin = true;
					this.lastLoginUser = userType;
					break;
				case Factory:
					driver.navigate().to(Users.Requester_Admin.getUrl());
					LoginUser(Users.Integration.getUserId(), Users.Integration.getPassword());
					switchToLightning();
					Log.info(userType.name() + " logged in successfully.");
					GlobalTestData.RequesterUrl = GetCurrentUrl();
					this.isLogin = true;
					this.lastLoginUser = userType;
					break;
				case Integration:
					driver.navigate().to(Users.Requester_Admin.getUrl());
					LoginUser(Users.Factory.getUserId(), Users.Factory.getPassword());
					switchToLightning();
					Log.info(userType.name() + " logged in successfully.");
					GlobalTestData.RequesterUrl = GetCurrentUrl();
					this.isLogin = true;
					this.lastLoginUser = userType;
					break;
				case Responder_Admin:
					driver.navigate().to(Users.Responder_Admin.getUrl());
					LoginUser(Users.Responder_Admin.getUserId(), Users.Responder_Admin.getPassword());
					switchToLightning();
					Log.info(userType.name() + " logged in successfully.");
					GlobalTestData.RequesterUrl = GetCurrentUrl();
					this.isLogin = true;
					this.lastLoginUser = userType;
					break;
				case Responder_SPU:
					driver.navigate().to(Users.Responder_Admin.getUrl());
					LoginUser(Users.Responder_SPU.getUserId(), Users.Responder_SPU.getPassword());
					switchToLightning();
					Log.info(userType.name() + " logged in successfully.");
					GlobalTestData.RequesterUrl = GetCurrentUrl();
					this.isLogin = true;
					this.lastLoginUser = userType;
					break;

				case Lab_Admin:
					driver.navigate().to(Users.Lab_Admin.getUrl());
					LoginUser(Users.Lab_Admin.getUserId(), Users.Lab_Admin.getPassword());
					switchToLightning();
					Log.info(userType.name() + " logged in successfully.");
					GlobalTestData.RequesterUrl = GetCurrentUrl();
					this.isLogin = true;
					this.lastLoginUser = userType;
					break;
				case Lab_SPU:
					driver.navigate().to(Users.Lab_Admin.getUrl());
					LoginUser(Users.Lab_SPU.getUserId(), Users.Lab_SPU.getPassword());
					switchToLightning();
					Log.info(userType.name() + " logged in successfully.");
					GlobalTestData.RequesterUrl = GetCurrentUrl();
					this.isLogin = true;
					this.lastLoginUser = userType;
					break;
				default:
					Log.error("Login failed. Please define user > " + userType.name());
			}

		}
	}

}

