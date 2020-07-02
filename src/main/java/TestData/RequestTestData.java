package TestData;

import com.codoid.products.fillo.Recordset;
import org.openqa.selenium.WebDriver;
import pageObjects.BasePage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestTestData extends BasePage {


    public static String ProductName = "TestProduct_18Aug2017";
    static Date d = new Date(System.currentTimeMillis());
    public static String TPRequestName = "TP_AutoTest" + d;
     public  static  Map<dateKey,String> dateMap = new HashMap<>();


    //Get Testdata from excel file
    private static String RequestName, RequestType, TpOrPrdctName, DocCategory, DocName, Comment, ApproveOrRejectComments, WorkflowStatus;

    public RequestTestData(WebDriver driver) {
        super(driver);
    }

    public static String getRequestName() {
        return RequestName;
    }


    public static void setRequestName(String requestName) {
        RequestName = requestName;
    }

    public static String getRequestType() {
        return RequestType;
    }

    public static void setRequestType(String requestType) {
        RequestType = requestType;
    }

    public static String getTpOrPrdctName() {
        return TpOrPrdctName;
    }

    public static void setTpOrPrdctName(String tpOrPrdctName) {
        TpOrPrdctName = tpOrPrdctName;
    }

    public static String getDocCategory() {
        return DocCategory;
    }

    public static void setDocCategory(String docCategory) {
        DocCategory = docCategory;
    }

    public static String getDocName() {
        return DocName;
    }

    public static void setDocName(String docName) {
        DocName = docName;
    }

    public static String getComment() {
        return Comment;
    }

    public static void setComment(String comment) {
        Comment = comment;
    }

    public String getApproveOrRejectComments() {
        return ApproveOrRejectComments;
    }

    public static void setApproveOrRejectComments(String approveOrRejectComments) {
        ApproveOrRejectComments = approveOrRejectComments;
    }

    public String getWorkflowStatus() {
        return WorkflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus) {
        WorkflowStatus = workflowStatus;
    }

    @Override
    public String toString() {
        return "RequestTestData{" +
                "RequestName='" + RequestName + '\'' +
                ", RequestType='" + RequestType + '\'' +
                ", TpOrPrdctName='" + TpOrPrdctName + '\'' +
                ", DocCategory='" + DocCategory + '\'' +
                ", DocName='" + DocName + '\'' +
                ", Comment='" + Comment + '\'' +
                ", ApproveOrRejectComments='" + ApproveOrRejectComments + '\'' +
                '}';
    }

    public static void GetData(String testId) throws Exception {
        Recordset rs = GetTestData("Request", testId);
        Date d = new Date(System.currentTimeMillis());
        while (rs.next()) {
            //Request name
            setRequestName(rs.getField("Request name") + " " + GetTimeStamp());

            //Request Type
            setRequestType(rs.getField("Request Type"));

            //TP name Or Product name
            setTpOrPrdctName(rs.getField("TP name Or Product name"));

            //Document category
            setDocCategory(rs.getField("Document category"));

            //Document name
            setDocName(rs.getField("Document name"));

            //Comment
            setComment(rs.getField("Comment"));
            //ApproveOrRejectCommentsComment
            setApproveOrRejectComments(rs.getField("ApproveOrRejectComments"));
        }//End while
        rs.close();


    }

    public enum dateKey{
        DATEATREQUESTER,DATEATRESPONDER;
    }


}
