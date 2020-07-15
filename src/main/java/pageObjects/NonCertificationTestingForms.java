package pageObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class NonCertificationTestingForms extends BasePage{

    @FindBy(xpath = "//a[@role='tab']")
    public List<WebElement> certificationTabs;

    @FindBy(xpath = "//select[@ng-model='model[options.key]']")
    public WebElement testingMilestonePicklist;

    @FindBy(xpath = "//option[@ng-repeat='option in to.options track by option.id']")
    public List<WebElement> testingMilestoneOptions;

    @FindBy(xpath = "(//input[@ng-if='!$root.isReadOnly(options)'])[1]")
    public WebElement testRevisionPlan;

    @FindBy(xpath = "(//input[@ng-if='!$root.isReadOnly(options)'])[2]")
    public WebElement sampleSize;

    @FindBy(xpath = "(//input[@ng-if='!$root.isReadOnly(options)'])[3]")
    public WebElement sampleDescription;

    @FindBy(xpath = "//select[contains(@ng-if,'!vm.readOnly && vm.labs.length')]")
    public WebElement testingLabPicklist;

    @FindBy(xpath = "//select[contains(@ng-if,'!vm.readOnly && vm.labs.length')]//option")
    public List<WebElement> testingLabOptions;

    @FindBy(xpath = "//button[contains(.,'Print')]")
    public WebElement buttonPrint;

    @FindBy(xpath = "//button[contains(.,'PDF')]")
    public WebElement buttonPDF;

    @FindBy(xpath = "(//button[contains(.,'Close')])[1]")
    public WebElement buttonClose;

    @FindBy(xpath = "(//button[contains(.,'Save')])[1]")
    public WebElement buttonSave;

    @FindBy(xpath = "(//span[contains(.,'Next')])[2]")
    public WebElement buttonNext;

    @FindBy(xpath = "//select[@placeholder='Select the Cost Center']")
    public WebElement costCenterPicklist;

    @FindBy(xpath = "//select[@placeholder='Select the Cost Center']//option")
    public List<WebElement> costCenterOptions;

    @FindBy(xpath = "(//input[@type='number'])[1]")
    public WebElement testedLowerAge;

    @FindBy(xpath = "(//input[@type='number'])[2]")
    public WebElement testedUpperAge;

    @FindBy(xpath = "(//input[@type='text'])[4]")
    public WebElement otherTestedAge;

    @FindBy(xpath = "//textarea[@ng-if='!$root.isReadOnly(options)']")
    public WebElement overallQEComment;

    @FindBy(xpath = "//select[contains(@id,'category')]")
    public WebElement testCategoryPicklist;

    @FindBy(xpath = "//select[contains(@id,'category')]//option")
    public List<WebElement> testCategoryOptions;

//    @FindBy(xpath = "//select[contains(@id,'class')]")
    @FindBy(xpath = "//input[@type='text'][contains(@id,'class')]")
    public WebElement testClassPicklist;

//    @FindBy(xpath = "//select[contains(@id,'class')]//option")
//    public List<WebElement> testClassOptions;
    @FindBy(xpath = "//span[@role='option']")
    public WebElement testClassResult;

    @FindBy(xpath = "//select[contains(@id,'test')]")
    public WebElement testNamePicklist;

    @FindBy(xpath = "//select[contains(@id,'test')]//option")
    public List<WebElement> testNameOptions;

    @FindBy(xpath = "(//span[contains(.,'Add')])[4]")
    public WebElement addTestButton;

    @FindBy(xpath = "//select[@ng-model='row[field]']")
    public WebElement documentCategoryPicklist;

    @FindBy(xpath = "//option[@ng-repeat='option in col.templateOptions.options track by option.id']")
    public List<WebElement> documentCategoryOptions;

    @FindBy(xpath = "//span[contains(.,'Upload File')]")
    public WebElement uploadFileButton;

    @FindBy(xpath = "(//button[contains(.,'Previous')])[1]")
    public WebElement previousButton;

    @FindBy(xpath = "//label[contains(@class,'block')]")
    public WebElement uploadFromDesktopButton;

    public void setTestingMilestone(String milestone) {
        clickButton(testingMilestonePicklist);
        waitForElemToBeClickable(testingMilestoneOptions.get(0));
        clickFirstContainsText(testingMilestoneOptions, milestone);
    }

    public void setTestRevisionPlan(String revisionPlan){
//        clickElement(testRevisionPlan, false, false);
        enterText(testRevisionPlan, revisionPlan);
    }

    public void setSampleSize(String size){
        waitForElemToBeClickable(sampleSize);
//        clickElement(sampleSize, false, false);
        enterText(sampleSize, size);
    }

    public void setSampleDescription(String description) {
//        clickElement(sampleDescription, false, true);
        enterText(sampleDescription, description);
    }

    public void setTestingLab(String lab) {
        waitUntilDisplayed(testingLabPicklist);
        clickElement(testingLabPicklist, false, true);
        clickFirstContainsText(testingLabOptions, lab);
    }

    public void setCostCenter(String costCenter) {
        waitUntilDisplayed(costCenterPicklist);
        clickButton(costCenterPicklist);
        waitUntilDisplayed(costCenterOptions.get(0));
        clickFirstContainsText(costCenterOptions, costCenter);
    }

    public void addTestToForm(String category, String testClass, String testName) {
        setTestCategory(category);
        setTestClass(testClass);
        setTestName(testName);
        clickAddButton();
    }

    public void setTestCategory(String category){
        waitUntilDisplayed(testCategoryPicklist);
        clickButton(testCategoryPicklist);
        waitUntilDisplayed(testCategoryOptions.get(0));
        clickFirstMatchingText(testCategoryOptions, category);
        sleepSeconds(5);
    }

    /**
     * This method is not working as expected. testClassResult is not being selected.
     */
    public void setTestClass(String testClass) {
        enterText(testClassPicklist, testClass);
        clickButton(testClassResult);
//        waitUntilDisplayed(testClassOptions.get(0));
//        clickFirstMatchingText(testClassOptions, testClass);
        sleepSeconds(5);
    }

    public void setTestName(String testName) {
        waitUntilDisplayed(testNamePicklist);
        clickButton(testNamePicklist);
        waitUntilDisplayed(testNameOptions.get(0));
        clickFirstMatchingText(testNameOptions, testName);
        sleepSeconds(5);
    }

    public void setDocumentCategory(String documentCategory) {
        waitUntilDisplayed(documentCategoryPicklist);
        clickElement(documentCategoryPicklist, false, false);
        waitUntilDisplayed(documentCategoryOptions.get(0));
        clickFirstContainsText(documentCategoryOptions, documentCategory);
    }

    /**
     * This method is not working, need to find a way to do the upload file for non-certs
     */
    public void uploadFile() {
        clickButton(uploadFileButton);
        String fileToUpload = "/Users/jorgeperozo/Desktop/a1.png";
        waitUntilDisplayed(uploadFromDesktopButton);
        uploadFromDesktopButton.sendKeys(fileToUpload);
//        enterText(uploadFromDesktopButton, fileToUpload);
    }

    public void clickAddButton() {
        waitUntilEnabled(addTestButton);
        clickButton(addTestButton);
    }

    public void clickNextButton(){
        waitUntilEnabled(buttonNext);
        clickButton(buttonNext);
    }

    public void clickSaveButton(){
        waitUntilEnabled(buttonSave);
        clickButton(buttonSave);
    }

    public void fillNonCertNonQNCertification(){
        setTestingMilestone("VSP - Vendor Sample Pilot");
        setTestRevisionPlan("QA Revision Plan");
        setSampleSize("123");
        setSampleDescription("Sample Description");
        setTestingLab("Hasbro Factory-Lab");
        clickNextButton();
        setCostCenter("Other - N/A");
        setTestCategory("Transit Test");
        setTestClass("Package");
        clickAddButton();
//        setDocumentCategory("Test Plan Attachment");
//        uploadFile();
        sleepSeconds(2);
        clickSaveButton();
        sleepSeconds(2);
    }

    public void fillNonCertQNCertification(){
        setTestingMilestone("QN - First QN (new tool)");
        setTestRevisionPlan("QA Revision Plan");
        setSampleSize("123");
        setSampleDescription("Sample Description");
        setTestingLab("KUl_LAB01");
        clickNextButton();
        setCostCenter("Other - N/A");
        setTestCategory("Transit Test");
        setTestClass("Package");
        clickAddButton();
//        setDocumentCategory("Test Plan Attachment");
//        uploadFile();
        sleepSeconds(2);
        clickSaveButton();
        sleepSeconds(2);
    }
}
