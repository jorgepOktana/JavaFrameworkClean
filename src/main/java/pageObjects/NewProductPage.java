package pageObjects;

import Utils.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
// IN PROGRESS
public class NewProductPage extends BasePage {

    /**
         * This NewProductPage seems to be inside of an iframe so before acccesing the
     * elements we probably need to move to the iframe (iframe is on BasePage)
     */

    public NewProductPage() {
        super();
    }

    @FindBy(xpath = "(//input[contains(@type,'text')])[1]")
    WebElement txtTP;

    @FindBy(xpath = "//button[contains(.,'Search')]")
    WebElement btnSearchProduct;

    @FindBy(xpath = "//span[contains(.,'Create Product')]")
    WebElement btnCreateProduct;

    @FindBy(xpath = "//input[contains(@ng-model,'vm.main.product.name')]")
    WebElement txtProductName;

    @FindBy(xpath = "//select[@ng-model='id.key']")
    WebElement idKeys;

    @FindBy(xpath = "(//span[contains(.,'Next')])[2]")
    WebElement btnNext;

    @FindBy(xpath = "//input[@placeholder='Trading Partner']")
    WebElement txtTradPartner;

    //IN PROGRESS





}