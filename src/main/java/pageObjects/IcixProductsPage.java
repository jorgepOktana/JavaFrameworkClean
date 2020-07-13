package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

public class IcixProductsPage {

    //-----------New Product-----------------------------------------

    @FindBy(xpath = "//div[@title='New']")
    WebElement btnNew;
    @FindBy(xpath = "//input[contains(@class,'slds-input ng-pristine ng-valid ng-empty ng-valid-maxlength ng-touched')]")
    WebElement tpSearch;
    @FindBy(xpath = "//button[contains(.,'Search')]")
    WebElement btnSearch;
    @FindBy(xpath = "//span[contains(.,'Create Product')]")
    WebElement btnCreateProduct;
    @FindBy(xpath = "//input[@ng-model='vm.main.product.name']")
    WebElement prodName;
    @FindBy(xpath = "//select[@ng-model='id.key']")
    WebElement prodIdType;
    @FindBy(xpath = "(//button[contains(.,'Previous')])[1]")
    WebElement btnPrevious;
    @FindBy(xpath = "(//span[contains(.,'Cancel')])[3]")
    WebElement btnCancel;
    @FindBy(xpath = "(//button[contains(.,'Next')])[2]")
    WebElement btnNext;
    @FindBy(xpath = "//h2[contains(.,'Define ICIX Product Relationship')]")
    WebElement txtProdRelantionship;
    @FindBy(xpath = "//input[@placeholder='Trading Partner']")
    WebElement tpTextbox;
    @FindBy(xpath = "//select[@ng-options='status for status in vm.main.relationshipStatuses']")
    WebElement relStatus;
    @FindBy(xpath = "//select[@ng-options='type for type in vm.main.relationshipTypes']")
    WebElement relType;
    @FindBy(xpath = "//textarea[@ng-model='vm.main.selectedProductRelationship.comment']")
    WebElement comments;
    @FindBy(xpath = "//span[contains(.,'Save')]")
    WebElement btnSave;
    @FindBy(xpath = "(//span[contains(.,'Cancel')])[4]")
    WebElement btnClose;

    //===========END-Form Menu bar==============================================

}