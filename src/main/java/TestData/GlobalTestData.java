package TestData;

import org.openqa.selenium.WebDriver;
import pageObjects.BasePage;

import java.util.*;

public class GlobalTestData extends BasePage {
    public static Set<String> DocumentToDelete = new HashSet<String>();
    public static String TPName = "";
    public static String ProductGroupName = "";
    public static String ParentProductName = "";
    public static String PGroupName = "";
    public static String FormName = "";
    public static String docRequestName = "";
    public static String FormNameForProductReq = "";
    public static String ProductName = "";
    public static String FirstProductReqName = "";
    public static String parentQuestionId = "";
    public static String correctiveActionRequestName = "";
    public static String productTestFailRequestName = "";
    public static String productTestPassRequestName = "";
    public static List<String> testCategory=  new ArrayList<>();

    //Cleanup Unique Set
    //Convert Set to List   List <String> RequestsFormNamesList = new ArrayList <String> (RequestsFormNames);
    public static Set<String> RequestsToDelete = new HashSet<String>();
    public static Set<String> ProductGroupsToDelete = new HashSet<String>();
    public static Set<String> TPGroupsToDelete = new HashSet<String>();
    public static Set<String> ProductsToDelete = new HashSet<String>();
    public static Set<String> FormBuildersFormNamesToDelete = new HashSet<String>();
    public static Set<String> ProductTestFormToDelete = new HashSet<String>();
    public static Set<String> ProductTestManagerToDelete = new HashSet<String>();
    public static Set<String> CorrectiveActionFormToDelete = new HashSet<String>();
    public static Set<String> AttributeToDelete = new HashSet<String>();
    public static String RequesterUrl = "";
    public static String ResponderUrl = "";
    public static String LabUrl = "";

    //Requester_Admin/Responder_Admin/Lab_Admin Object to Hold All information stored in Excel
    //Like Credentials
    public static UsersTestData Requester_Admin = new UsersTestData();
    public static UsersTestData Responder_Admin = new UsersTestData();
    public static UsersTestData Lab_Admin = new UsersTestData();

    public static UsersTestData Requester_SPU = new UsersTestData();
    public static UsersTestData Responder_SPU = new UsersTestData();
    public static UsersTestData Lab_SPU = new UsersTestData();
    public static String requestName = "";
    public static String documentName = "";
    //This variable to used to maintain default window
    public static String TPNameFroDocShare = "";
    public static String CopedFormName = "";
    public static String RetestRequestName = "";
    static Date d = new Date(System.currentTimeMillis());
    public static String tagName = "Tag " + d;

    public static String parentProductNameForQA_6005 = "";
    public static String childProductNameForQA_6005 = "";

    public static Map<String, Set<String>> testCoverageMap = new HashMap<>();

    public GlobalTestData(WebDriver driver) {
        super();
    }

//    public static Set<String> getCoverageIds(String key) {
//        if (testCoverageMap.containsKey(key)){
//            testCoverageMap.get(key).removeAll(ReportUtil.started_tests);
//            return testCoverageMap.get(key);
//        }
//        return Collections.EMPTY_SET;
//    }

}
