package pageObjects;

import TestData.GlobalTestData;
import TestData.Users;
import Utils.Log;
import Utils.WaitTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import static Utils.Common.*;

public class BasePage {

	protected static String Requestor = "Requestor_Admin";
	protected static String Responder = "Responder_Admin";
	protected static String Laboratory = "Laboratory_Admin";

	public void navigateToPage(String pageURL){
		driver.get(pageURL);
	}

	/**
	 * <h1>Login to Application<h1/>
	 * <p>Purpose: This method is used to login to application with Requestor_Admin,Responder_Admin and Laboratory_Admin </p>
	 *
	 * @param actor
	 * @throws Exception
	 */
	private boolean isLogin = false;
	private String currentLoginUser ="NA";
	public Users.TYPE lastLoginUser;

	public void loginAs(String actor) throws Exception {
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
				GlobalTestData.RequesterUrl = GetCurrentUrl();
				break;

			case "Responder_Admin":
				LoginUser(GlobalTestData.Responder_Admin.getUserId(), GlobalTestData.Responder_Admin.getPassword());
				this.isLogin = true;
				setCurrentLoginUser(actor);
				SwitchToLightiningView();
				Log.info("Responder_Admin logged in successfully.");
				GlobalTestData.ResponderUrl = GetCurrentUrl();
				break;

			case "Laboratory_Admin":
				LoginUser(GlobalTestData.Lab_Admin.getUserId(), GlobalTestData.Lab_Admin.getPassword());
				this.isLogin = true;
				setCurrentLoginUser(actor);
				SwitchToLightiningView();
				Log.info("Laboratory_Admin logged in successfully.");
				GlobalTestData.LabUrl = GetCurrentUrl();
				break;
			case "Requestor_SPU":
				LoginUser(GlobalTestData.Requester_SPU.getUserId(), GlobalTestData.Requester_SPU.getPassword());
				this.isLogin = true;
				setCurrentLoginUser(actor);
				SwitchToLightiningView();
				Log.info("Requestor_SPU logged in successfully.");
				GlobalTestData.RequesterUrl = GetCurrentUrl();
				break;

			case "Responder_SPU":
				LoginUser(GlobalTestData.Responder_SPU.getUserId(), GlobalTestData.Responder_SPU.getPassword());
				this.isLogin = true;
				setCurrentLoginUser(actor);
				SwitchToLightiningView();
				Log.info("Responder_SPU logged in successfully.");
				GlobalTestData.ResponderUrl = GetCurrentUrl();
				break;

			case "Laboratory_SPU":
				LoginUser(GlobalTestData.Lab_SPU.getUserId(), GlobalTestData.Lab_SPU.getPassword());
				this.isLogin = true;
				setCurrentLoginUser(actor);
				SwitchToLightiningView();
				Log.info("Laboratory_SPU logged in successfully.");
				GlobalTestData.LabUrl = GetCurrentUrl();
				break;
			default:
				Assert.assertTrue(false, "Not a valid user type.");
		}
	}

	/**
	 * <h1>Logout from Application<h1/>
	 * <p>Purpose: This method is used to logout from</p>
	 *
	 * @throws Exception
	 */
	public void logout() {

		Log.info("Logging out the user");
		try {
			LogoutUser();
			this.isLogin = false;
			this.setCurrentLoginUser("NA");
			waitForPageLoadToComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		changeUrl();
	}

	/**
	 * <h1>Change URL<h1/>
	 * <p>Purpose: This method is used to change url</p>
	 *
	 * @throws Exception
	 */
	public void changeUrl() {
		url();
	}

	private static Logger log = LogManager.getLogger(BasePage.class);

	protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }


	public void waitForElementToLoad(By locator){
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.elementToBeClickable(locator));
	}
	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean login) {
		isLogin = login;
	}

	public String getCurrentLoginUser() {
		return currentLoginUser;
	}

	public void setCurrentLoginUser(String currentLoginUser) {
		this.currentLoginUser = currentLoginUser;
	}

	public static void waitForPageLoadToComplete() {
		try {
			WaitTool.waitForPageLoadToComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
