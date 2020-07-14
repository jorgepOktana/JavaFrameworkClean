package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

public class ProductSearchPage {

    //-----------Search by Trading Partner-----------------------------------------
    @FindBy(xpath = "//span[contains(.,'Product Search')]")
    WebElement productSearch;
    @FindBy(xpath = "//button[contains(.,'Search by Trading Partner')]")
    WebElement btnTP;
    @FindBy(xpath = "(//select[contains(@class,'slds-select')])[1]")
    WebElement tpType;
    @FindBy(xpath = "(//select[contains(@class,'slds-select')])[2]")
    WebElement facilityName;
    @FindBy(xpath = "(//button[contains(.,'Search')])[4]")
    WebElement btnSearch;

    //===========END Trading Partnerr==============================================

    //-----------Search by Product Name-----------------------------------------

    @FindBy(xpath = "//button[contains(.,'Search by Product Name')]")
    WebElement btnProductName;
    @FindBy(xpath = "(//button[contains(.,'Search')])[4]")
    WebElement btnTpType;
    @FindBy(xpath = "//input[contains(@placeholder,'Search your Products Here')]")
    WebElement prodSearchBox;
    @FindBy(xpath = "//button[contains(@title,'Search')]")
    WebElement btnSearchProd;
    @FindBy(xpath = "//button[contains(@title,'Close')]")
    WebElement btnClose;

    //===========END-Product Name================================================

    //-----------Search by Attributes -----------------------------------------
    @FindBy(xpath = "//button[contains(.,'Search by Attributes')]")
    WebElement btnAttributes;
    @FindBy(xpath = "(//select[contains(@class,'slds-select')])[4]")
    WebElement selectTpType;
    @FindBy(xpath = "(//select[contains(@class,'slds-select')])[5]")
    WebElement selectAttribute;
    @FindBy(xpath = "//input[contains(@placeholder,'Search your Attribute')]")
    WebElement btnsearchAtt;
    @FindBy(xpath = "(//button[contains(@title,'Search Products')])[3]")
    WebElement btnAttributesSearch;

    //===========END-Search by Attributes======================================

    //-----------Mass QE Assignment -----------------------------------------
    @FindBy(xpath = "//button[contains(.,'Mass QE Assignment')]")
    WebElement btnMassQE;
    @FindBy(xpath = "(//select[contains(@class,'slds-select')])[6]")
    WebElement facility;
    @FindBy(xpath = "(//select[contains(@class,'slds-select')])[7]")
    WebElement attribute;
    @FindBy(xpath = "(//button[@title='Search Products'])[4]")
    WebElement search;
    @FindBy(xpath = "//input[@placeholder='Search your Global SKU']")
    WebElement globalSKU;
    @FindBy(xpath = "//input[@placeholder='Search your Parent Number']")
    WebElement parentNumber;
    @FindBy(xpath = "(//select[contains(@class,'slds-select')])[8]")
    WebElement resQE;
    @FindBy(xpath = "//button[contains(.,'Add/Update Responsible QE')]")
    WebElement addQE;

    //===========END-Search by Attributes======================================

}