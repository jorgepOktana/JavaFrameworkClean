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
    private String RequestName, RequestType, TpOrPrdctName, DocCategory, DocName, Comment, ApproveOrRejectComments, WorkflowStatus;

    public RequestTestData(WebDriver driver) {
        super(driver);
    }

    public String getRequestName() {
        return RequestName;
    }


    public void setRequestName(String requestName) {
        RequestName = requestName;
    }

    public String getRequestType() {
        return RequestType;
    }

    public void setRequestType(String requestType) {
        RequestType = requestType;
    }

    public String getTpOrPrdctName() {
        return TpOrPrdctName;
    }

    public void setTpOrPrdctName(String tpOrPrdctName) {
        TpOrPrdctName = tpOrPrdctName;
    }

    public String getDocCategory() {
        return DocCategory;
    }

    public void setDocCategory(String docCategory) {
        DocCategory = docCategory;
    }

    public String getDocName() {
        return DocName;
    }

    public void setDocName(String docName) {
        DocName = docName;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getApproveOrRejectComments() {
        return ApproveOrRejectComments;
    }

    public void setApproveOrRejectComments(String approveOrRejectComments) {
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

    public void GetData(String testId) throws Exception {
        Recordset rs = GetTestData("Request", testId);
        Date d = new Date(System.currentTimeMillis());
        while (rs.next()) {
            //Request name
            this.setRequestName(rs.getField("Request name") + " " + GetTimeStamp());

            //Request Type
            this.setRequestType(rs.getField("Request Type"));

            //TP name Or Product name
            this.setTpOrPrdctName(rs.getField("TP name Or Product name"));

            //Document category
            this.setDocCategory(rs.getField("Document category"));

            //Document name
            this.setDocName(rs.getField("Document name"));

            //Comment
            this.setComment(rs.getField("Comment"));
            //ApproveOrRejectCommentsComment
            this.setApproveOrRejectComments(rs.getField("ApproveOrRejectComments"));
        }//End while
        rs.close();


    }

    public enum dateKey{
        DATEATREQUESTER,DATEATRESPONDER;
    }


}
