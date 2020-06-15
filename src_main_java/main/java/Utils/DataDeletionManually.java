package Utils;


import icix.TestData.GlobalTestData;

import java.util.List;
import java.util.Map;

public class DataDeletionManually {
    //Do not change format update year-month-day
    static String old_date = ExecuteBatchMessageProcessor.getSubtractcByDaysDateForDataDeletion(20);

    public static void main(String[] args) throws Exception {
        //Get users data
        ExcelUtil.GetUserData();
        //Set org user to delete data
        DataDeleteForUser(GlobalTestData.Requester_Admin.getUserId(), GlobalTestData.Requester_Admin.getPassword());

        DataDeleteForUser(GlobalTestData.Responder_Admin.getUserId(), GlobalTestData.Responder_Admin.getPassword());

        DataDeleteForUser(GlobalTestData.Lab_Admin.getUserId(), GlobalTestData.Lab_Admin.getPassword());

    }

    static void DataDeleteForUser(String userId, String password) {
        //Set user
        ExecuteBatchMessageProcessor.setUserData(userId, password);

        ExecuteBatchMessageProcessor.DeleteProducts_PP_Relationship_ContainsText("REL with-Auto_Test", old_date);
        getRequestsToDelete();
        getFormsToDelete();
        //Get products to delete
        getProductsToDelete();
        //Get Test managers to delete
        getProductTestManagersToDelete();
        //Get product test forms to delete
        getProductTestFormToDelete();
        //Finally delete collected data
        getProductTestManagersToDelete();
        ExecuteBatchMessageProcessor.cleanUpAutomationData();
    }


    static void getProductTestManagersToDelete() {
        String strQryTestingPrograms = "SELECT Name FROM ICIX_V1__Testing_Program__c where name like '%Auto%' And CreatedDate < " + old_date + "T00:00:00Z";
        List<Map> testingPrograms = ExecuteBatchMessageProcessor.executeQuery(strQryTestingPrograms);
        System.out.println("Testing programs: " + testingPrograms.size());
        testingPrograms.forEach(
                map ->
                        GlobalTestData.ProductTestManagerToDelete.add(map.get("Name").toString())
        );
    }

    static void getProductsToDelete() {
        String strQryProducts = "SELECT Name FROM ICIX_V1__ICIX_Product__c WHERE Name like '%Auto%' And CreatedDate < " + old_date + "T00:00:00Z order by name desc";
        List<Map> products = ExecuteBatchMessageProcessor.executeQuery(strQryProducts);
        System.out.println("Products to delete: " + products.size());
        products.forEach(
                map ->
                        GlobalTestData.ProductsToDelete.add(map.get("Name").toString())
        );
    }

    static void getProductTestFormToDelete() {
        String strQryProductTestForms = "SELECT Name FROM ICIX_V1__Container_Template__c WHERE Name like '%Product Test%' And CreatedDate < " + old_date + "T00:00:00Z order by name desc";
        List<Map> productTestForms = ExecuteBatchMessageProcessor.executeQuery(strQryProductTestForms);
        System.out.println("Product test forms: " + productTestForms.size());
        productTestForms.forEach(
                map ->
                        GlobalTestData.ProductTestFormToDelete.add(map.get("Name").toString())
        );
    }


    static void getRequestsToDelete() {
        String strQryProductTestForms = "SELECT Name FROM ICIX_V1__Request__c WHERE Name like '%Auto%' And CreatedDate < " + old_date + "T00:00:00Z";
        List<Map> requests = ExecuteBatchMessageProcessor.executeQuery(strQryProductTestForms);
        System.out.println("Product test forms: " + requests.size());
        requests.forEach(
                map ->
                        GlobalTestData.RequestsToDelete.add(map.get("Name").toString())
        );
    }

    static void getFormsToDelete() {
        String strQryProductTestForms = "SELECT Name FROM ICIX_V1__Container_Template__c WHERE Name like '%Auto%' And CreatedDate < " + old_date + "T00:00:00Z";
        List<Map> forms = ExecuteBatchMessageProcessor.executeQuery(strQryProductTestForms);
        System.out.println("Product test forms: " + forms.size());
        forms.forEach(
                map ->
                        GlobalTestData.FormBuildersFormNamesToDelete.add(map.get("Name").toString())
        );
    }

}
