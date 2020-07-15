package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

public class IcixProductsPage {

    //-----------Icix tasks-----------------------------------------

    @FindBy(xpath = "//button[contains(.,'Open Form')]")
    WebElement btnOpenForm;
    @FindBy(xpath = "(//span[contains(.,'Reject')])[2]")
    WebElement btnReject;
    FindBy(xpath = "(//td[@class=slds-table lazy-load-table ng-scope]")
    WebElement table;
    @FindBy(xpath = "//svg[contains(@class,'icon')]")
    WebElement icon;

    //===========END Icix tasks==============================================

}