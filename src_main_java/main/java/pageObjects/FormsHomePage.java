package pageObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FormsHomePage {

    //-----------Screen: Forms home page -----------------------------------------
    @FindBy(xpath = "//span[contains(.,'New Form')]")
    WebElement btnNewForm;
    @FindBy(xpath="//input[@ng-model='vm.searchText']")
    WebElement inputSearchForm;
    @FindBy(xpath="//svg[contains(@class,'icon')]")
    WebElement btnSearchForm;
    @FindBy(xpath="(//span[contains(.,'Delete')])")
    WebElement btnDeleteForm;
    /*@FindBy(xpath="table[id*='dataPageBlock'] .dataRow")
    WebElement tblFormRowsList;
    @FindBy(xpath="table.slds-table.lazy-load-table tbody tr td:nth-of-type(2) a")
    WebElement lstFormNames;
    @FindBy(xpath="table.slds-table.lazy-load-table tbody tr")
    WebElement tblRowsSearchedForms;*/

    //===========END-Screen: Forms home page =======================================



}