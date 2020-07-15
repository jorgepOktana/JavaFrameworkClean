package pageObjects;

import TestData.Users;
import Utils.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

public class CertificationTestingForm extends BasePage {

    @FindBy(xpath = "//a[@role='tab']")
    public List<WebElement> certificationTabs;

    @FindBy(xpath = "//select[@ng-model='model[options.key]']")
    public WebElement testingMilestonePicklist;

    @FindBy(xpath = "//option[@ng-repeat='option in to.options track by option.id']")
    public List<WebElement> testingMilestoneOptions;

    @FindBy(xpath = "//input[contains(@class,'slds-input ng-pristine ng-untouched ng-scope')]")
    public WebElement sampleSize;

    @FindBy(xpath = "//input[contains(@class,'slds-input ng-pristine ng-valid')]")
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

    @FindBy(xpath = "(//input[@type='number'])[2]")
    public WebElement testedLowerAge;

    @FindBy(xpath = "(//input[@type='number'])[3]")
    public WebElement testedUpperAge;

    @FindBy(xpath = "(//input[@type='text'])[3]")
    public WebElement otherTestedAge;

    @FindBy(xpath = "//select[contains(@id,'category')]")
    public WebElement testCategoryPicklist;

    @FindBy(xpath = "//select[contains(@id,'category')]//option")
    public List<WebElement> testCategoryOptions;

    @FindBy(xpath = "//select[contains(@id,'class')]")
    public WebElement testClassPicklist;

    @FindBy(xpath = "//select[contains(@id,'class')]//option")
    public List<WebElement> testClassOptions;

    @FindBy(xpath = "//select[contains(@id,'test')]")
    public WebElement testNamePicklist;

    @FindBy(xpath = "//select[contains(@id,'test')]//option")
    public List<WebElement> testNameOptions;

    @FindBy(xpath = "(//span[contains(.,'Add')])[4]")
    public WebElement addTestButton;

    @FindBy(xpath = "(//button[contains(.,'Previous')])[1]")
    public WebElement previousButton;

    public CertificationTestingForm() {
        super();
    }

    public void setTestingMilestone(String milestone){
        clickElement(testingMilestonePicklist, false, false );
        waitForElemToBeClickable(testingMilestoneOptions.get(0));
        clickFirstContainsText(testingMilestoneOptions, milestone);
    }

    public void setSampleSize(String size){
        waitForElemToBeClickable(sampleSize);
        clickElement(sampleSize, false, false);
        enterText(sampleSize, size);
    }

    public void setSampleDescription(String description) {
        clickElement(sampleDescription, false, true);
        enterText(sampleDescription, description);
    }

    public void setTestingLab(String lab) {
        waitUntilDisplayed(testingLabPicklist);
        clickElement(testingLabPicklist, false, true);
        clickFirstContainsText(testingLabOptions, lab);
    }

    public void goToTab(String tabName){
        waitUntilDisplayed(certificationTabs.get(0));
        clickFirstMatchingText(certificationTabs, tabName);
    }

    public void goToTestRequestOverviewtab(){
        goToTab(formRequestOverviewTab);
    }
    public void goToTestResultTab() {
        goToTab(formResultsTab);
    }

    public void setCostCenter(String costCenter) {
        waitUntilDisplayed(costCenterPicklist);
        clickButton(costCenterPicklist);
        waitUntilDisplayed(costCenterOptions.get(0));
        clickFirstContainsText(costCenterOptions, costCenter);
//        clickFirstMatchingText(costCenterOptions, costCenter);
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
        clickFirstContainsText(testCategoryOptions, category);
        sleepSeconds(5);
    }

    public void setTestClass(String testClass) {
        waitUntilDisplayed(testClassPicklist);
        clickButton(testClassPicklist);
        waitUntilDisplayed(testClassOptions.get(0));
        clickFirstMatchingText(testClassOptions, testClass);
        sleepSeconds(5);
    }

    public void setTestName(String testName) {
        waitUntilDisplayed(testNamePicklist);
        clickButton(testNamePicklist);
        waitUntilDisplayed(testNameOptions.get(0));
        clickFirstMatchingText(testNameOptions, testName);
        sleepSeconds(5);
    }

    public void setMultipleTest(ArrayList<certificationTests> multipleTests){
        for (certificationTests test: multipleTests) {
                addTestToForm(test.getTestCategory(), test.getTestClass(), test.getTestName());
            }
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

    public void fillCertificationTestingProgram() {
        setTestingMilestone("QN - Others");
        setSampleSize("AutoQA");
        setTestingLab("KUl_LAB01");
        clickNextButton();
        waitForPageLoad();
        setCostCenter("Other - N/A");
        setTestCategory("AU");
        clickAddButton();
        setTestCategory("CA");
        clickAddButton();
        sleepSeconds(2);
        clickSaveButton();
        sleepSeconds(2);
    }

}
