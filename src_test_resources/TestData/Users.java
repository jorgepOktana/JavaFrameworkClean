package TestData;

import org.openqa.selenium.WebDriver;
import pageObjects.BasePage;

import java.util.HashMap;
import java.util.Map;

public class Users extends BasePage {

    public Users(WebDriver driver) {
        super(driver);
    }

    //All user almost similar as defined in excel
    public enum TYPE {
        Requester_Admin, Requester_SPU,
        Responder_Admin, Responder_SPU,
        Lab_Admin, Lab_SPU,QE, Factory,Integration
    }


    public static Map<String, UsersTestData> adminUsersTestDataMap = new HashMap<>();
    public static Map<String, UsersTestData> spuUsersTestDataMap = new HashMap<>();

    public static UsersTestData Requester_Admin, Requester_SPU,
            Responder_Admin, Responder_SPU,
            Lab_Admin,Lab_SPU,
            QE,Factory,Integration;


    static {
        try {
            //Get data from excel
            GetUserData();
            //Set data for each user
            Requester_Admin = adminUsersTestDataMap.get("Requester");
            Responder_Admin = adminUsersTestDataMap.get("Responder");
            Lab_Admin= adminUsersTestDataMap.get("Lab");

            Requester_SPU = spuUsersTestDataMap.get("Requester");
            Responder_SPU = spuUsersTestDataMap.get("Responder");
            Lab_SPU = spuUsersTestDataMap.get("Lab");
            QE= spuUsersTestDataMap.get(TYPE.QE.name());
            Factory= spuUsersTestDataMap.get(TYPE.Factory.name());
            Integration= spuUsersTestDataMap.get(TYPE.Integration.name());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
