package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

public class NewFormPage {

    //-----------Screen: Form Menu bar-----------------------------------------
    @FindBy(xpath = "//td[@id='j_id0:form:tabContainer_lbl']")
    WebElement menuTabContainer;
    @FindBy(xpath = "//td[contains(@id,'j_id0:form:tabLayout_lbl')]")
    WebElement menuTabLayout;
    @FindBy(xpath = "//td[@id='j_id0:form:tabContainer_lbl']")
    WebElement menuTabTabs;
    @FindBy(xpath = "//td[@id='j_id0:form:tabSections_lbl']")
    WebElement menuTabSection;
    @FindBy(xpath = "//td[contains(@id,'j_id0:form:tabQuestions_lbl')]")
    WebElement menuTabLinkedQuestions;
    @FindBy(xpath = "//input[@name='j_id0:form:buttonSave']")
    WebElement btnSave;
    @FindBy(xpath = "//input[contains(@name,'j_id0:form:buttonSaveAndClone')]")
    WebElement btnSaveClose;
    @FindBy(xpath = "//input[@name='j_id0:form:buttonSave']")
    WebElement btnCancel;

    //===========END-Form Menu bar==============================================

    //-----------Screen: Form Top Buttons-----------------------------------------

    @FindBy(xpath = "//input[@name='j_id0:form:buttonSave']")
    WebElement btnSaveTab;
    @FindBy(xpath = "//input[@value='PREVIEW']")
    WebElement btnPreview;
    @FindBy(css=("input[id$='buttonSave'][type='submit']"))
    WebElement btnSaveAll;
    @FindBy(xpath = "//input[@value='PUBLISH']")
    WebElement btnPublish;
    @FindBy(css=("input[id$='buttonUnpublish'][type='submit']"))
    WebElement btnUnPublishForm;
    @FindBy(xpath = "//input[@value='PREVIEW AS USER']")
    WebElement btnPreviewAsUser;
    @FindBy(xpath = "//input[contains(@value,'CANCEL')]")
    WebElement bntCancel;

    //===========END-Form Top Buttons================================================

    //-----------Screen:Create new container -----------------------------------------

    @FindBy(xpath = "//input[@name='j_id0:form:containerBlock:containerNew:inputContainerName']")
    WebElement txtContainerName;
    @FindBy(xpath = "//select[contains(@name,'j_id0:form:containerBlock:containerNew:inputContainerType')]")
    WebElement drpType;
    @FindBy(xpath = "//select[@name='j_id0:form:containerBlock:containerNew:inputContainerContainerType']")
    WebElement drpContainerType;
    @FindBy(css=("input[name$='createContainer'][type='submit']"))
    WebElement btnCreate;
    @FindBy(xpath = "//input[@name='j_id0:form:containerBlock:containerNew:inputContainerEffectiveDate']")
    WebElement effectiveDate;
    @FindBy(xpath = "//input[@class='btn'][contains(@id,'id0:form:containerBlock:deleteContainer')]")
    WebElement btnDeleteContainer;
    //===========END-Screen:Create new container======================================

    //-----------Screen: Create new layout-----------------------------------------

    @FindBy(xpath = "//input[contains(@name,'j_id0:form:layoutBlock:layoutNew:inputLayoutName')]")
    WebElement txtLayoutName;
    @FindBy(xpath = "//select[contains(@name,'j_id0:form:layoutBlock:layoutNew:inputLayoutUiType')]")
    WebElement drpLayoutType;
    @FindBy(xpath = "//input[@class='btn'][contains(@id,'id0:form:layoutBlock:createLayout')]")
    WebElement btnCreateLayout;
    @FindBy(xpath = "//input[@class='btn'][contains(@id,'id0:form:layoutBlock:deleteLayout')]")
    WebElement btnDeleteLayout;

    //===========END-Screen: Create new layout======================================

    //------------------Screen: Tabs---------------------------------------------------

    @FindBy(xpath = "//input[@class='btn'][contains(@id,'id0:form:createTab')]")
    WebElement btnCreateTab;
    @FindBy(xpath = "//input[contains(@name,'id51')]")
    WebElement tblTab;
    @FindBy(xpath = "//input[contains(@name,'id55')]")
    WebElement tblTabRows;

    //============END Tabs================================================================


    //------------------Screen: Sections---------------------------------------------------
    @FindBy(xpath = "//td[@id='j_id0:form:tabSections_lbl']")
    WebElement sectionTab;
    @FindBy(xpath = "//input[@class='btn'][contains(@id,'id0:form:createSection')]")
    WebElement addSectionButton;
    @FindBy(xpath = "//input[contains(@name,'id78')]")
    WebElement sectionName;
    @FindBy(xpath = "//input[contains(@name,'id78')]")
    WebElement orderSection;
    @FindBy(xpath = "//select[contains(@id,'id89')]")
    WebElement displayStyle;
    @FindBy(xpath = "//input[@value='SAVE']")
    WebElement btnSaveSection;
    @FindBy(xpath = "//div[contains(@class,'messageText')]")
    WebElement successMessage;
    @FindBy(xpath = "//input[@class='btn'][contains(@id,'id0:form:sectionBlock:sectionSection:sectionTable:0:deleteSection')]")
    WebElement btndeleteSection;
    //============END Section================================================================

    //~~~~~~~~~~~Section: Answer Options~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static By btnCreateAnswerOption= cssSelector("input[type='submit'][id$='createAnswerOption']");
    public static By tblanswerOptionRows = cssSelector("[id$='answerOptionTable'] .dataRow");

    //~~~~~~~~~~~END: Section: Answer Options~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //~~~~~~~~~~~Section: Element fields~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static By chbxInternalOnly =cssSelector("input[id$='inputElementIsInternalOnly'][type='checkbox']");
    //~~~~~~~~~~~END Section: Element fields~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //~~~~~~~~~~~Section: READONLY~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static By chbxReadonlyAll = cssSelector("input[id$='inputElementReadOnly'][type='checkbox']");
    public static By chbxReadonlyRes = cssSelector("input[id$='inputNewQuestionReadOnlyRes'][type='checkbox']");
    public static By chbxReadonlyReq = cssSelector("input[id$='inputNewQuestionReadOnlyReq'][type='checkbox']");
    public static By chbxReadonlyVer = cssSelector("input[id$='inputNewQuestionReadOnlyVer'][type='checkbox']");
    //~~~~~~~~~~~END: Section: READONLY~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //~~~~~~~~~~~Section: Keep Editable~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static By chbxEditableReq = cssSelector("input[id$='inputNewQuestionEditableReq'][type='checkbox']");
    public static By chbxEditableRes = cssSelector("input[id$='inputNewQuestionEditableRes'][type='checkbox']");
    public static By chbxEditableVer = cssSelector("input[id$='inputNewQuestionEditableVer'][type='checkbox']");
    //~~~~~~~~~~~END Section: Keep Editable~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //~~~~~~~~~~~~~~Section: Format~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static By chbxFormatBold = cssSelector("input[id$='inputLabelFormatBold'][type='checkbox']");
    public static By chbxFormatItalic = cssSelector("input[id$='inputLabelFormatItalic'][type='checkbox']");
    public static By chbxFormatUnderline = cssSelector("input[id$='inputLabelFormatUnderline'][type='checkbox']");
    //~~~~~~~~~~~~~~END - Section: Format~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //~~~~~~~~~~~Section: Question fields~~~~~~~~~~~~~~~~~~
    public static By txtQstName = cssSelector("input[id$='inputQuestionName']");
    public static By txtAreaAns = cssSelector("textarea[id$='inputQuestionQuestionText']");
    public static By answerType = cssSelector("select[id$='inputQuestionAnswerType']");
    public static By optionListName = cssSelector("select[id$='inputQuestionOptionListName']");
    public static By inputParentQuestions = cssSelector("input[id$='targetId'][type='text']");
    public static By drpQuestionDependencyAction = cssSelector("select[id$='inputQuestionDependencyAction']");
    public static By txtAreaFormula = cssSelector("textarea[id$='inputQuestionFormula']");
    public static By drpDependentAnswer = cssSelector("div[id$='newQuestionBlock'] tr:nth-of-type(5) td:nth-of-type(2) select");
    public static By chbxResponseRequired = cssSelector("input[id$='inputQuestionResponseRequired'][type='checkbox']");
    public static By chbxAdvanceSection = cssSelector("input[id$='inputIsAdvancedMode'][type='checkbox']");
    public static By chbxDoNotRequireExpDate = cssSelector("input[id$='inputRequiredUpload'][type='checkbox']");
    //~~~~~~~~~~~~~~~~~~~~~~Advance Section~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static By inputMinScale = cssSelector("input[id$='inputQuestionMinScale'][type='text']");
    public static By inputMaxScale = cssSelector("input[id$='inputQuestionMaxScale'][type='text']");
    //~~~~~~~~~~~~~~~~~~~~~~END - Advance Section~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static By btnAddNewQuestion = cssSelector("input[id$='addNewLinkedQuestion'][type='submit']");
    public static By btnSaveAfterEdit=cssSelector("input[type='submit'][id$='editNewLinkedQuestion']");
    //~~~~~~~~~~~END - Section: Question fields~~~~~~~~~~~~~~~~~~


    //============END - Screen Linked questions================================================================


    //-------------MISC--------------------------------------------------------------------
    public static By loaderImage = cssSelector("#loading");
    public static By msgSaveData = cssSelector("#messageBlock .messageText");
    public static By loaderImage_New_FormListPage =cssSelector("img[alt='Loading...']");
    //===============END MISC==============================================================

    //==========================START- GENERATE SERVICE SECTION=============================================//
    public static By headingOfGenerateServiceSection = By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Account & Submission Information' ])[1]");
    public static By btnGenerateServiceSection = By.xpath("//input[@value='generate service section']");

    //==========================END - GENERATE SERVICE SECTION==============================================//

    public static By placeHolderField = By.cssSelector("input[id$='inputQuestionPlaceHolderText'][type='text']");


   //==================================== Locator Related to Download Form As PDF =========


    public static By savePdf = xpath("//*[@id=\"buttons-left\"]/button[2]");
    public static By Save_in_Library = xpath("//*[@id=\"origin-main\"]/edit-dialog[2]/div[2]/div/div[3]/button[2]/span");
    public static By CloseButton = xpath("//*[@id=\"origin-main\"]/edit-dialog[3]/div[2]/div/div[3]/button[1]/span");
    public static By DownloadButton = cssSelector(".slds-modal__footer button[variable='pdfSaveDialog_action_1']");
    public static By ClosePopButton = xpath("//*[@id=\"origin-main\"]/edit-dialog[2]/div[2]/div/div[3]/button[1]");

    //=================================== Locators for Preview form===================================//
    public static By chkBox = By.xpath("//span[@class='slds-checkbox--faux clickable']");
    public static By txtArea = By.xpath("//textarea[@ng-maxlength='5000']");
    public static By getDatapopup = By.xpath("//button[@text='Get Data']");

}
