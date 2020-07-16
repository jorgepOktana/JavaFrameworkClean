package Utils;

import com.force.api.ApiConfig;
import com.force.api.ApiSession;
import com.force.api.ForceApi;
import com.google.gson.*;
import TestData.GlobalTestData;
import TestData.ThreeActorTestData;
import TestData.Users;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import static Utils.Utils.getUserData;

public class RestAPISF {

    public enum USER {REQUESTER, RESPONDER, LAB}

    private static USER lastLoginUser = USER.RESPONDER;
    public static String ORG_NAMESPACE = "";
    private static String userName;
    private static String password;
    private static String consumerKey = getUserData("ConsumerKey");
    private static String consumerSecret = getUserData("ConsumerSecret");
    private static String CurrentInstanceUrl = "";
    private static SalesForceOauthObject salesForceOauthObject;
    public static String SF_Token_TargetUrl = "";
    public static String salesforceRestApiVersion = "v45.0";
    public static boolean isOldBatch = false;
    public  static ApiSession currentSession;

    public enum SF_TOKEN_ENVIRONMENT {LOGIN, TEST}

    //This block will execute automatically and set Salesforce Default Token Environment to login
    static {
        setSF_Token_Environment(SF_TOKEN_ENVIRONMENT.TEST);
    }//end static block

    public static void setUserData(String user, String pwd){
        RestAPISF.userName =user;
        RestAPISF.password =pwd;
    }

    /**
     * <H1>getForceApi</h1>
     * <p>This method get the session and return ForceApi where you can get token and instance url and run a query</p>
     *
     * @return ForceApi
     */
    public static ForceApi getForceApi(String userId, String pwd) {
        ForceApi forceApi = null;
        try {
            Log.info("**************Getting salesforce session**************");
            forceApi = new ForceApi(new ApiConfig()
                    .setUsername(userId)
                    .setPassword(pwd)
                    .setClientId(consumerKey)
                    .setClientSecret(consumerSecret)
                    .setLoginEndpoint(SF_Token_TargetUrl)
                    .setApiVersionString(salesforceRestApiVersion)
            );
            currentSession = forceApi.getSession();
            CurrentInstanceUrl = currentSession.getApiEndpoint();
            Log.info("Instance Url : " + CurrentInstanceUrl);
            Log.info("**************Session retrieved successfully**************");

            Log.info("**************Get ORG Namespace****************************");
            String nameSpaceQuery = "SELECT NamespacePrefix FROM ApexClass Where Namespaceprefix !=null And name='CreateRequestController'";
            List<Map> map = forceApi.query(nameSpaceQuery).getRecords();
            if (!map.isEmpty())
            {
                ORG_NAMESPACE = map.get(0).get("NamespacePrefix").toString();
                Log.info("ORG NamespacePrefix : " + ORG_NAMESPACE);
            }else {
                Log.error("FAILED to get ORG NamespacePrefix");
            }
        } catch (Exception ex) {
            Log.error("There is problem while getting access token");
            Log.error(" " + ex.getMessage());

        }
        return forceApi;
    }

    public static String getSalesforceAccessToken(String userId, String pwd) {
        getForceApi(userId,pwd);
        String token="";
        try {
            if(currentSession.getAccessToken()!=null)
                token= currentSession.getAccessToken();
            else
                token= "null";
        } catch (Exception e) {
            token="null";
        }
        return token;
    }//End method


    public static String getSalesforceAccessToken() {
        return getSalesforceAccessToken(userName,password);
    }//End method


    /**
     * <h1>Set Salesforce Environment</h1>
     * <p> We can set salesforce environment like Login or Test
     */
    public static void setSF_Token_Environment(SF_TOKEN_ENVIRONMENT SFENV) {
        switch (SFENV) {
            case TEST:
                Log.info("Selected SF TOKEN ENVIRONMENT = TEST");
                SF_Token_TargetUrl = "https://test.salesforce.com";
                break;

            default:
                Log.info("Selected SF TOKEN ENVIRONMENT = LOGIN");
                SF_Token_TargetUrl = "https://login.salesforce.com";
                break;
        }
    }//end method

    /**
     * @param accessToken String
     * @param query       String
     * @return boolean
     * @since 08/05/2018
     * <p>
     * <h1>Execute Salesforce tooling API query</h1>
     * <p>This method will take salesforce access token and execute given query on it and return true or false as result</p>
     */
    private static boolean executeSalesforceToolingQuery(String accessToken, String query) {
        boolean result = false;
        if (accessToken.equalsIgnoreCase("null")) {
            Log.info("Tooling api can not executed because token is null");
            return false;
        }
        try {
            query = query.replaceAll("ICIX_V1__", ORG_NAMESPACE + "__");

            long startTime = System.currentTimeMillis();
            Log.info("***Execute Salesforce Tooling API query****");
            String subQuery = URLEncoder.encode(query, "UTF-8");
            String endPoint = CurrentInstanceUrl + "/services/data/" + salesforceRestApiVersion + "/tooling/executeAnonymous/?anonymousBody=" + subQuery;
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(endPoint);
            get.addHeader("Authorization", "Bearer " + accessToken);
            HttpResponse response = httpClient.execute(get);
            long endTime = System.currentTimeMillis();

            int code = response.getStatusLine().getStatusCode();
            Log.info("Tooling API Status: " + code);
            if (code == 200) {
                String oauthLoginResponse = EntityUtils.toString(response.getEntity());
                Gson gson = new Gson();
                Log.info("oauthLoginResponse :: " + oauthLoginResponse);
                result = gson.fromJson(oauthLoginResponse, ExecuteAnonymousResult.class).success;
                Log.info("***Execution finished with result: " + result);
                Log.info("Total time taken(sec): " + ((endTime - startTime) / 1000));
            } else {
                Log.error("***Execution FAILED because HTTP status is  " + code);
            }
        } catch (Exception e) {
            Log.error(e.getMessage());
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public static boolean createRetailProduct(String retailName){
        String query = "{" +
                "       settings: {" +
                "        Command: AddProducts," +
                "        ApiParameters: {" +
                "            Products: [" +
                "                {" +
                "                    TradingPartnerIdType: Vendor," +
                "                    Status: Active," +
                "                    RelationshipType: Buy," +
                "                    ProductIdTypesAndValues: {" +
                "                        Item: 367VD19_RI" +
                "                    }," +
                "                    IcixProductName: Hasbro Retail Item API 1," +
                "                    Description: Hasbro Retail Item API 1 desc," +
                "                    Attributes: {" +
                "                        Lower Age: 6," +
                "                        Language Code: EU4," +
                "                        Shipping Unit Sold As: CONTENT," +
                "                        Upper Age: 98.5," +
                "                        Season: 2018," +
                "                        Product Type: RETAILITEM," +
                "                        Property: MIGHTY MUGGS," +
                "                        Property Code: MM," +
                "                        Licensor: MARVEL CHARACTERS INC.," +
                "                        Licensor Code: MAR," +
                "                        Family Brand: HASBRO," +
                "                        Family Brand Code: HAS," +
                "                        Class: TOYS," +
                "                        Brand: MARVEL CLASSIC," +
                "                        Brand Code: MVL," +
                "                        Super Category: ACTION FIGURES & ACCESSORIES," +
                "                        Super Category Code: ACFIG," +
                "                        Category: ACTION FIGURES," +
                "                        Category Code: ACTNF," +
                "                        Internal Classification: BOYS," +
                "                        Internal Class Code: BOYSG," +
                "                        Product Class Code: TOYGR," +
                "                        IP Sensitive: No," +
                "                        Shipping Unit Package Style: MASTER CARTON," +
                "                        Secondary Package Style: PDQ," +
                "                        Retail Package Style: OTHER," +
                "                        Product Status: Active," +
                "                        Global SKU: HAS-API-1" +
                "                    }" +
                "                }" +
                "            ]" +
                "        }" +
                "    }" +
                "}";
        boolean result = false;
        userName = Users.Requester_Admin.getUserId();
        password = Users.Requester_Admin.getPassword();
        try {
            String accessToken = getSalesforceAccessToken();
            result = executeSalesforceToolingQuery(accessToken, query);
            if (result) {
                Log.info("Product creation via API - PASS");
                GlobalTestData.ProductsToDelete.add(retailName);
            } else {
                Log.info("Product creation via API - FAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean createAssortmentProduct(String productName){
        String query = "ICIX_V1__ICIX_Product__c product = new ICIX_V1__ICIX_Product__c();" +
                "        product.Name = 'Product test from RestCall 2';" +
                "        product.ICIX_V1__Description__c = 'Product test from RestCall 2';" +
                "insert product;" +
                "ID prodID = product.Id;" +
                "UP_Relationship__c upr = new UP_Relationship__c();" +
                "        upr.Product__c = prodId;" +
                "        upr.Related_Account__c = '001f200001cBmyrAAC';" +
                "        upr.Name = 'Product2';" +
                "        upr.Type__c = 'Buy';" +
                "        upr.Status__c = 'Active';" +
                "        insert upr;" +
                "ICIX_V1__Product_Universal_Id__c universalId = new ICIX_V1__Product_Universal_Id__c();" +
                "universalId.ICIX_V1__ICIX_Product__c = prodId;" +
                "universalId.ICIX_V1__Id_Type__c = 'a0Sf200000MtGt8EAF';" +
                "universalId.ICIX_V1__Id_Value__c = '00003';" +
                "insert universalId;" +
                "ICIX_V1__Product_Universal_Id__c universalId = new ICIX_V1__Product_Universal_Id__c();" +
                "universalId.ICIX_V1__ICIX_Product__c = prodId;" +
                "universalId.ICIX_V1__Id_Type__c = 'a0Sf200000MtGt9EAF';" +
                "universalId.ICIX_V1__Id_Value__c = '00004';" +
                "insert universalId;";
        boolean result = false;
        userName = Users.Requester_Admin.getUserId();
        password = Users.Requester_Admin.getPassword();
        try {
            String accessToken = getSalesforceAccessToken();
            result = executeSalesforceToolingQuery(accessToken, query);
            if (result) {
                Log.info("Product creation via API - PASS");
                GlobalTestData.ProductsToDelete.add(productName);
            } else {
                Log.info("Product creation via API - FAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //Internal class to hold salesforce token information
    private class SalesForceOauthObject {
        String access_token;
        String instance_url;

        public SalesForceOauthObject() {
        }

        public String getAccess_token() {
            return access_token;
        }//End inner class

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getInstance_url() {
            return instance_url;
        }

        public void setInstance_url(String instance_url) {
            this.instance_url = instance_url;
        }
    }

    //Internal class to hold Execution Results
    private class ExecuteAnonymousResult {
        Integer column;
        String compileProblem;
        Boolean compiled;
        String exceptionMessage;
        String exceptionStackTrace;
        Integer line;
        Boolean success;
    }
}
