package pageObjects;

import org.jsoup.Connection;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

public class IcixTasksPage extends BasePage {

    //-----------Icix tasks-----------------------------------------

    @FindBy(xpath = "//button[contains(.,'Open Form')]")
    WebElement btnOpenForm;

    @FindBy(xpath = "(//span[contains(.,'Reject')])[2]")
    WebElement btnReject;

   @FindBy(xpath = "(//table[@class=slds-table lazy-load-table ng-scope]")
    WebElement table;

    @FindBy(xpath = "//svg[contains(@class,'icon')]")
    WebElement icon;

    @FindBy(xpath = "//input[@placeholder='Search this List...']")
    WebElement txtSearchList;


    //===========END Icix tasks==============================================
    //Method to search the task with the Related Product Parent Request from request
    public void searchByTasksID (String currentTaskID){
        txtSearchList.sendKeys(currentTaskID);
        icon.click();
    }

    public List<String> listOfRequest (){
        List<WebElement> rows = table.findElements(xpath(".//tr"));
        return rows.stream().map(row -> {
            List<WebElement> cols = row.findElements(xpath("//td[@ng-repeat='col in vm.columns']"));
            WebElement cell = cols.get(3).findElement(By.tagName("a"));
            return cell.getText();
            }).collect(Collectors.toList());
    }
}