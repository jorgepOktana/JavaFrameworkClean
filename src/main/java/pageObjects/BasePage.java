package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static pageConstants.BasePageConstants.*;


public class BasePage {

	protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

  
	public WebElement test() {

		String foo=  PAGE_LOOK_UP_SEARCH_FRAME;

    	return null;
	}

	public void waitForElementToLoad(By locator){
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.elementToBeClickable(locator));
	}
}
