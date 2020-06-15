package Utils;


import com.codeborne.selenide.Selenide;
import com.force.api.ApiConfig;
import com.force.api.ApiSession;
import com.force.api.ForceApi;
import com.google.gson.*;
import icix.TestData.GlobalTestData;
import icix.TestData.ThreeActorTestData;
import icix.TestData.Users;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.util.*;

import static icix.Utils.Common.GetUserData;

/**
 * <h1>ExecuteBatchMessageProcessor</h1>
 * <p>Purpose: This class is used to execute Salesforce tooling api codes (CRUD) operations, Getting token</p>
 * It is used to handle batch process
 */

public class ExecuteBatchMessageProcessor {
    private static USER lastLoginUser = USER.RESPONDER;
    public static String ORG_NAMESPACE = "";
    private static String userName;
    private static String password;
    private static String consumerKey = GetUserData("ConsumerKey");
    private static String consumerSecret = GetUserData("ConsumerSecret");
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
        ExecuteBatchMessageProcessor.userName =user;
        ExecuteBatchMessageProcessor.password =pwd;
    }


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
     * <h1>Execute batch message processor with id</h1>
     * <p> We can ICIX batch processor to reflect requests faster
     */

    public static boolean executedMessageProcessorWithMessageId(String accessToken, String id) {
        String query =
                String.format("Set<String> ids = new Set<String>();" +
                        "ids.add('%s');" +
                        "ICIX_v1.MessageProcessorBatch msg = new ICIX_v1.MessageProcessorBatch(null,ids,null);" +
                        "Database.executeBatch(msg, 1);", id);
        if (!ORG_NAMESPACE.contains("ICIX_V1")) {
            query = query.replaceAll("ICIX_v1.", "");
        }

        return executeSalesforceToolingQuery(accessToken, query);
    }

    private static boolean executedMessageProcessor(String accessToken) {
        String query = "Database.executeBatch(new ICIX_v1.MessageProcessorBatch());";
        if (isOldBatch) {
            query = "Database.executeBatch(new ICIX_v1.MessageProcessor());";
        }

        if (!ORG_NAMESPACE.contains("ICIX_V1")) {
            query = query.replaceAll("ICIX_v1", ORG_NAMESPACE);
        }
        boolean result = executeSalesforceToolingQuery(accessToken, query);
        return result;
    }//end method



    public static void DeleteProducts_PP_Relationship_ContainsText(String text,String oldDate) {
        //Old_Date format  = "2019-04-10";
        //String query="SELECT Name FROM ICIX_V1__PP_Relationship__c where name like '%"+text+"%' and And CreatedDate < " + oldDate + "T00:00:00Z";
        try {
            Log.info("Delete PP relationship contains text: " +text +" and older than date " +oldDate);
            String query = "List<ICIX_V1__PP_Relationship__c> productsToDelete = new List<ICIX_V1__PP_Relationship__c>();" +
                    "productsToDelete = [SELECT Id, Name, ICIX_V1__Product1_Name__c, ICIX_V1__Product1__c, ICIX_V1__Product2_Name__c, ICIX_V1__Product2__c FROM ICIX_V1__PP_Relationship__c where name like '%"+text+"%' AND CreatedDate < "+oldDate+ "T00:00:00Z];"+
                    " database.delete(productsToDelete);";

            boolean result = executeSalesforceToolingQuery(getSalesforceAccessToken(), query);
            if (result) {
                Log.info("Products deleted - PASS");
            } else {
                Log.info("Products deleted - FAIL");
            }
        } catch (Exception e) {
            Log.info("Products deleted - FAIL");
        }


    }

    private static void DeleteProducts(String accessToken, List<String> ProductList) {
        if (!ProductList.isEmpty()) {
            Log.info("Total number of ICIX Products to be deleted #" + ProductList.size());
            Log.info("ICIX Products : " + ProductList);

            try {
                Map<Integer, List<String>> batchesMap = ExecuteBatchMessageProcessor.CreateDataDeleteBatches(5, ProductList);
                //Process deletion using batch
                for (Integer key : batchesMap.keySet()) {

                    String query = "List<ICIX_V1__ICIX_Product__c> productsToDelete = new List<ICIX_V1__ICIX_Product__c>();" +
                            "productsToDelete = [Select id, name From ICIX_V1__ICIX_Product__c where Name in (" + convertList(batchesMap.get(key)) + ")];" +
                            " database.delete(productsToDelete);";
                    boolean result = executeSalesforceToolingQuery(accessToken, query);
                    if (result) {
                        Log.info("Products deleted - PASS");
                    } else {
                        Log.info("Products deleted - FAIL");
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }//End if
    }//End method

    private static void DeleteTradingPartnerGroup(String accessToken, List<String> TPGroupList) {
        if (!TPGroupList.isEmpty()) {
            try {
                Log.info("Total number of TP Groups to be deleted # " + TPGroupList.size());
                Log.info("TP Groups:" + TPGroupList);
                Map<Integer, List<String>> batchesMap = ExecuteBatchMessageProcessor.CreateDataDeleteBatches(5, TPGroupList);
                //Process deletion using batch
                for (Integer key : batchesMap.keySet()) {
                    String query = "List<ICIX_V1__Partner_Group__c> tpGroupListtoDelete = new List<ICIX_V1__Partner_Group__c>();" +
                            "tpGroupListtoDelete = [select Id,name from ICIX_V1__Partner_Group__c Where Name in (" + convertList(batchesMap.get(key)) + ")];" +
                            " database.delete(tpGroupListtoDelete);";
                    boolean result = executeSalesforceToolingQuery(accessToken, query);
                    if (result) {
                        Log.info("TP Group deleted - PASS");
                    } else {
                        Log.info("TP Group deleted - FAIL");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//End if
    }//End function

    private static void DeleteProductGroup(String accessToken, List<String> ProductGroupList) {
        if (!ProductGroupList.isEmpty()) {
            try {
                Log.info("Total number of Product Groups to be deleted # " + ProductGroupList.size());
                Log.info("Product Groups:" + ProductGroupList);

                Map<Integer, List<String>> batchesMap = ExecuteBatchMessageProcessor.CreateDataDeleteBatches(5, ProductGroupList);
                //Process deletion using batch
                for (Integer key : batchesMap.keySet()) {
                    String query = "List<ICIX_V1__Product_Group__c> productGroupToDelete = new List<ICIX_V1__Product_Group__c>();" +
                            "productGroupToDelete = [Select id, name From ICIX_V1__Product_Group__c where Name in(" + convertList(batchesMap.get(key)) + ")];" +
                            " database.delete(productGroupToDelete); ";
                    boolean result = executeSalesforceToolingQuery(accessToken, query);
                    if (result) {
                        Log.info("Product Group deleted - PASS");
                    } else {
                        Log.info("Product Group - FAIL");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//End if
    }//End function

    private static void DeleteForms(String accessToken, List<String> FormsList) {

        if (!FormsList.isEmpty()) {
            try {
                Log.info("Total number of forms to be deleted # " + FormsList.size());
                Log.info("Forms : " + FormsList);

                Map<Integer, List<String>> batchesMap = ExecuteBatchMessageProcessor.CreateDataDeleteBatches(5, FormsList);
                //Process deletion using batch
                for (Integer key : batchesMap.keySet()) {
                    String query = "Map<id, ICIX_V1__Container_Template__c> toDeleteForms = new Map<id, ICIX_V1__Container_Template__c> ([select id,name from ICIX_V1__Container_Template__c where name IN(" + convertList(batchesMap.get(key)) + ")]);" +
                            " List<String> containerTemplateIds = new list<String>();" +
                            " for(String s:toDeleteForms.keyset())" +
                            "     containerTemplateIds.add(s);" +
                            " List<ICIX_V1__Container__c> listContainer =[SELECT Id FROM ICIX_V1__Container__c Where ICIX_V1__Container_Template__c in: containerTemplateIds];" +
                            " List<String> containerIds = new List<String>();" +
                            " for(ICIX_V1__Container__c objContainer : listContainer)" +
                            " {" +
                            "     containerIds.add(objContainer.Id);" +
                            " } " +
                            " List<ICIX_V1__Layout__c> listLayout = [SELECT Id FROM ICIX_V1__Layout__c Where ICIX_V1__Container_Template_Global_Id__c in: containerTemplateIds or ICIX_V1__Parent_Form__c in: containerTemplateIds]; " +
                            " List<String> layoutIds = new List<String>(); " +
                            " for(ICIX_V1__Layout__c objLayout : listLayout)" +
                            " {     " +
                            "     layoutIds.add(objLayout.Id);" +
                            " }" +
                            " List<ICIX_V1__Tab__c> listTab = [SELECT Id FROM ICIX_V1__Tab__c Where ICIX_V1__Layout_Global_Id__c in: layoutIds or ICIX_V1__Layout_Global_Id__c =: '' or ICIX_V1__ParentLayout__c in: layoutIds or ICIX_V1__ParentLayout__c =:''];" +
                            " List<String> tabIds = new List<String>(); " +
                            " for(ICIX_V1__Tab__c objTab : listTab)" +
                            " {     " +
                            "     tabIds.add(objTab.Id);" +
                            " }         " +
                            " List<ICIX_V1__Section__c> listSection = [SELECT Id FROM ICIX_V1__Section__c Where ICIX_V1__Tab_Global_Id__c in: tabIds or ICIX_V1__Parent_Tab__c in: tabIds];" +
                            " List<String> sectionIds = new List<String>(); " +
                            " for(ICIX_V1__Section__c objSection : listSection )" +
                            " {     " +
                            "     sectionIds.add(objSection.Id);" +
                            " }         " +
                            " List<ICIX_V1__Question__c> listQuestion = [SELECT Id FROM ICIX_V1__Question__c Where ICIX_V1__Form__c in: containerTemplateIds];" +
                            " List<String> questionIds = new List<String>(); " +
                            " for(ICIX_V1__Question__c objQuestion : listQuestion)" +
                            " {     " +
                            "     questionIds.add(objQuestion.Id);" +
                            " }" +
                            " List<ICIX_V1__Element__c> listElement = [SELECT Id FROM ICIX_V1__Element__c Where ICIX_V1__ParentSection__c in: sectionIds or ICIX_V1__ParentSection__c =: '' or ICIX_V1__Section_Global_Id__c in: sectionIds or ICIX_V1__Section_Global_Id__c =: ''];" +
                            " List<String> elementIds = new List<String>(); " +
                            " for(ICIX_V1__Element__c objElement : listElement)" +
                            " {     " +
                            "     elementIds.add(objElement.Id);" +
                            " }" +
                            " List<ICIX_V1__Container_Answer__c> listContainerAnswer = [SELECT Id FROM ICIX_V1__Container_Answer__c Where ICIX_V1__Question__c in: questionIds];" +
                            " List<String> containerAnswerIds = new List<String>(); " +
                            " for(ICIX_V1__Container_Answer__c objContainerAnswer : listContainerAnswer)" +
                            " {     " +
                            "     containerAnswerIds.add(objContainerAnswer.Id);" +
                            " }" +
                            " List<ICIX_V1__Answer_Option__c> listAnswerOption =[SELECT Id FROM ICIX_V1__Answer_Option__c Where ICIX_V1__Question__c in: questionIds];" +
                            " List<String> answerOptionIds = new List<String>(); " +
                            " for(ICIX_V1__Answer_Option__c objAnswerOption : listAnswerOption)" +
                            " {     " +
                            "     answerOptionIds.add(objAnswerOption.Id);" +
                            " } " +
                            "			" +
                            " List<ICIX_V1__workflow_instance__c> listWorkflowInstance = [select id, ICIX_V1__Request__c from ICIX_V1__workflow_instance__c Where ICIX_V1__Container__c in: containerIds];" +
                            " List<String> workflowInstanceIds = new List<String>(); " +
                            " List<String> requestIds = new List<String>(); " +
                            " for(ICIX_V1__workflow_instance__c objWorkflowInstance : listWorkflowInstance)" +
                            " {     " +
                            "     workflowInstanceIds.add(objWorkflowInstance.Id);" +
                            "     requestIds.add(objWorkflowInstance.ICIX_V1__Request__c);" +
                            " }" +
                            " List<ICIX_V1__workflow_step_instance__c> listWorkflowStepInstance =[select id from ICIX_V1__workflow_step_instance__c Where ICIX_V1__Workflow_Instance__c in:workflowInstanceIds];" +
                            " List<ICIX_V1__Recipient__c> listRecipients =[select id, Name from ICIX_V1__Recipient__c Where ICIX_V1__Workflow_Instance__c in:workflowInstanceIds];" +
                            " List<String> workflowStepInstanceIds = new List<String>();" +
                            " List<String> RecipientIds = new List<String>(); " +
                            " for(ICIX_V1__Recipient__c objRecipient:listRecipients)" +
                            " {" +
                            "     RecipientIds.add(objRecipient.Id);" +
                            " }" +
                            " for(ICIX_V1__workflow_step_instance__c objWorkflowStepInstance : listWorkflowStepInstance)" +
                            " {     " +
                            "     workflowStepInstanceIds.add(objWorkflowStepInstance.Id);    " +
                            " } " +
                            "List<ICIX_V1__Requirement_Instance__c> listRequirementInstance = new List<ICIX_V1__Requirement_Instance__c>();" +
                            " listRequirementInstance =[SELECT Id FROM ICIX_V1__Requirement_Instance__c Where ICIX_V1__Workflow__c in: workflowInstanceIds];" +
                            "   List<ICIX_V1__Request__c> listRequestDelete = new List<ICIX_V1__Request__c>();" +
                            " listRequestDelete = [Select id from ICIX_V1__Request__c Where Id in:requestIds];" +
                            "   List<ICIX_V1__Container_Template__c> listContainerTemplateDelete= new List<ICIX_V1__Container_Template__c>();" +
                            " listContainerTemplateDelete = [select Id from ICIX_V1__Container_Template__c Where Id in: containerTemplateIds];" +
                            " delete listAnswerOption;" +
                            " delete listContainerAnswer;           " +
                            " delete listElement;" +
                            " delete listQuestion;   " +
                            " delete listSection;           " +
                            " delete listTab; " +
                            " delete listLayout;           " +
                            " delete listContainer; " +
                            " delete listRecipients;" +
                            " delete listRequirementInstance;           " +
                            " delete listWorkflowStepInstance; " +
                            " delete listWorkflowInstance;         " +
                            " delete listRequestDelete;    " +
                            " delete listContainerTemplateDelete;" +
                            "       ";
                    boolean result = executeSalesforceToolingQuery(accessToken, query);
                    if (result) {
                        Log.info("Forms deleted - PASS");
                    } else {
                        Log.info("Forms deleted - FAIL");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//end if

    }//End method

    private static void DeleteRequests(String accessToken, List<String> RequestsList) {

        if (!RequestsList.isEmpty()) {
            try {
                Log.info("Total number of requests to be deleted #" + RequestsList.size());
                Log.info("Requests: " + RequestsList);

                Map<Integer, List<String>> batchesMap = ExecuteBatchMessageProcessor.CreateDataDeleteBatches(5, RequestsList);
                //Process deletion using batch
                for (Integer key : batchesMap.keySet()) {

                    String query = "  try{   " +
                            " List<ICIX_V1__workflow_instance__c> listWorkflowInstance = [select id, ICIX_V1__Request__c, ICIX_V1__Container__c from ICIX_V1__workflow_instance__c Where Name in(" + convertList(batchesMap.get(key)) + ")];" +
                            " List<String> workflowInstanceIds = new List<String>(); " +
                            " List<String> requestIds = new List<String>(); " +
                            " List<String> containerInstanceIds = new List<String>();" +
                            " for(ICIX_V1__workflow_instance__c objWorkflowInstance : listWorkflowInstance)" +
                            " {     " +
                            "     workflowInstanceIds.add(objWorkflowInstance.Id);" +
                            "     requestIds.add(objWorkflowInstance.ICIX_V1__Request__c);" +
                            "     containerInstanceIds.add(objWorkflowInstance.ICIX_V1__Container__c);" +
                            " }" +
                            " System.debug('I am here 13 workflowInstanceIds ' + workflowInstanceIds.size());" +
                            " " +
                            " List<ICIX_V1__workflow_step_instance__c> listWorkflowStepInstance =[select id from ICIX_V1__workflow_step_instance__c Where ICIX_V1__Workflow_Instance__c in:workflowInstanceIds];" +
                            " List<ICIX_V1__Recipient__c> listRecipients =[select id, Name from ICIX_V1__Recipient__c Where ICIX_V1__Workflow_Instance__c in:workflowInstanceIds];" +
                            " List<String> workflowStepInstanceIds = new List<String>();" +
                            " List<String> RecipientIds = new List<String>();  " +
                            " for(ICIX_V1__Recipient__c objRecipient:listRecipients)" +
                            " {" +
                            "     RecipientIds.add(objRecipient.Id);" +
                            " }" +
                            " for(ICIX_V1__workflow_step_instance__c objWorkflowStepInstance : listWorkflowStepInstance)" +
                            " {     " +
                            "     workflowStepInstanceIds.add(objWorkflowStepInstance.Id);    " +
                            " }" +
                            " System.debug('I am here 15 workflowStepInstanceIds ' + workflowStepInstanceIds.size());" +
                            " List<ICIX_V1__Recipient__c> listRecipientDelete = new List<ICIX_V1__Recipient__c>();" +
                            " listRecipientDelete = [select id from ICIX_V1__Recipient__c Where Id in: RecipientIds];" +
                            "      		Database.delete(listRecipientDelete);" +
                            " List<ICIX_V1__Requirement_Instance__c> listRequirementInstance = new List<ICIX_V1__Requirement_Instance__c>();" +
                            " listRequirementInstance =[SELECT Id FROM ICIX_V1__Requirement_Instance__c Where ICIX_V1__Workflow__c in: workflowInstanceIds];" +
                            " Database.delete(listRequirementInstance);" +
                            " List<ICIX_V1__workflow_step_instance__c> listWSIDelete = new List<ICIX_V1__workflow_step_instance__c>();" +
                            " listWSIDelete = [select id from ICIX_V1__workflow_step_instance__c Where Id in: workflowStepInstanceIds];" +
                            " Database.delete(listWSIDelete);" +
                            " List<ICIX_V1__workflow_instance__c> listWSDelete = new List<ICIX_V1__workflow_instance__c>();" +
                            " listWSDelete = [select id from ICIX_V1__workflow_instance__c Where Id in: workflowInstanceIds];" +
                            " Database.delete(listWSDelete);           " +
                            " List<ICIX_V1__Request__c> listRequestDelete = new List<ICIX_V1__Request__c>();" +
                            " listRequestDelete = [Select id from ICIX_V1__Request__c Where Id in:requestIds];" +
                            " Database.delete(listRequestDelete);  " +
                            " List<ICIX_V1__Container__c> listContainerInstanceToDelete= new List<ICIX_V1__Container__c>();" +
                            " listContainerInstanceToDelete = [select Id from ICIX_V1__Container__c Where Id in: containerInstanceIds];" +
                            " Database.delete(listContainerInstanceToDelete); " +
                            " String strMessage = 'sucess.! selected forms are deleted';" +
                            " system.debug('$$$$$ strMessage ='+strMessage);" +
                            "        }" +
                            "        catch (Exception ex)" +
                            "        { " +
                            " system.debug('$$$$ Error :'+ex.getMessage()+' $$$ stack trace::'+ex.getStackTraceString());" +
                            "        }" +
                            "        ";

                    boolean result = executeSalesforceToolingQuery(accessToken, query);
                    if (result) {
                        Log.info("Requests List deleted - PASS");
                    } else {
                        Log.info("Requests List deleted - FAIL");
                    }
                }//End loop

            } catch (Exception e) {
                e.printStackTrace();
            }
        }//end if
    }//End method

    private static void Delete3ActorData(String accessToken, List<String> ThreeActorFormList) {

        if (!ThreeActorFormList.isEmpty()) {
            try {
                Log.info("Total number of 3 actor forms to be deleted # " + ThreeActorFormList.size());
                Log.info("Forms : " + ThreeActorFormList);

                Map<Integer, List<String>> batchesMap = ExecuteBatchMessageProcessor.CreateDataDeleteBatches(5, ThreeActorFormList);
                //Process deletion using batch
                for (Integer key : batchesMap.keySet()) {
                    String query = "Map<id,ICIX_V1__Container_Template__c> toDeleteForms = new Map<id, ICIX_V1__Container_Template__c> ([select id,name, ICIX_V1__Testing_Program__c from ICIX_V1__Container_Template__c where name IN(" + convertList(batchesMap.get(key)) + ")]);" +
                            " List<String> containerTemplateIds = new list<String>();" +
                            " List<String> TestingProgramIds = new list<String>();" +
                            " List<String> ProductIds = new list<String>();" +
                            " for(String s:toDeleteForms.keyset()){" +
                            "    ICIX_V1__Container_Template__c ct = toDeleteForms.get(s);" +
                            "    containerTemplateIds.add(ct.Id);" +
                            "    TestingProgramIds.add(ct.ICIX_V1__Testing_Program__c);" +
                            " }" +
                            " List<ICIX_V1__Container__c> listContainer =[SELECT Id FROM ICIX_V1__Container__c Where ICIX_V1__Container_Template__c in: containerTemplateIds];" +
                            " List<String> containerIds = new List<String>();" +
                            " for( ICIX_V1__Container__c objContainer : listContainer)" +
                            " {" +
                            "    containerIds.add(objContainer.Id);" +
                            " }            " +
                            " List< ICIX_V1__Layout__c> listLayout = [SELECT Id FROM ICIX_V1__Layout__c Where ICIX_V1__Container_Template_Global_Id__c in: containerTemplateIds or ICIX_V1__Parent_Form__c in: containerTemplateIds];            " +
                            " List<String> layoutIds = new List<String>(); " +
                            " for( ICIX_V1__Layout__c objLayout : listLayout)" +
                            " {                " +
                            "    layoutIds.add(objLayout.Id);" +
                            " }" +
                            " List< ICIX_V1__Tab__c> listTab = [SELECT Id FROM ICIX_V1__Tab__c Where ICIX_V1__Layout_Global_Id__c in: layoutIds or ICIX_V1__Layout_Global_Id__c =: '' or ICIX_V1__ParentLayout__c in: layoutIds or ICIX_V1__ParentLayout__c =:''];" +
                            " List<String> tabIds = new List<String>(); " +
                            " for( ICIX_V1__Tab__c objTab : listTab)" +
                            " {                " +
                            "    tabIds.add(objTab.Id);" +
                            " }         " +
                            " List<ICIX_V1__Section__c> listSection = [SELECT Id FROM ICIX_V1__Section__c Where ICIX_V1__Tab_Global_Id__c in: tabIds or ICIX_V1__Parent_Tab__c in: tabIds];" +
                            " List<String> sectionIds = new List<String>(); " +
                            " for(ICIX_V1__Section__c objSection : listSection )" +
                            " {                " +
                            "    sectionIds.add(objSection.Id);" +
                            " }         " +
                            " List<ICIX_V1__Question__c> listQuestion = [SELECT Id FROM ICIX_V1__Question__c Where ICIX_V1__Form__c in: containerTemplateIds];" +
                            " List<String> questionIds = new List<String>(); " +
                            " for(ICIX_V1__Question__c objQuestion : listQuestion)" +
                            " {                " +
                            "    questionIds.add(objQuestion.Id);" +
                            " }" +
                            " List<ICIX_V1__Element__c> listElement = [SELECT Id FROM ICIX_V1__Element__c Where ICIX_V1__ParentSection__c in: sectionIds or ICIX_V1__ParentSection__c =: '' or ICIX_V1__Section_Global_Id__c in: sectionIds or ICIX_V1__Section_Global_Id__c =: ''];" +
                            " List<String> elementIds = new List<String>(); " +
                            " for(ICIX_V1__Element__c objElement : listElement)" +
                            " {                " +
                            "    elementIds.add(objElement.Id);" +
                            " }" +
                            " List<ICIX_V1__Container_Answer__c> listContainerAnswer = [SELECT Id FROM ICIX_V1__Container_Answer__c Where ICIX_V1__Question__c in: questionIds];" +
                            " List<String> containerAnswerIds = new List<String>(); " +
                            " for(ICIX_V1__Container_Answer__c objContainerAnswer : listContainerAnswer)" +
                            " {                " +
                            "    containerAnswerIds.add(objContainerAnswer.Id);" +
                            " }" +
                            " List<ICIX_V1__Answer_Option__c> listAnswerOption =[SELECT Id FROM ICIX_V1__Answer_Option__c Where ICIX_V1__Question__c in: questionIds];" +
                            " List<String> answerOptionIds = new List<String>(); " +
                            " for(ICIX_V1__Answer_Option__c objAnswerOption : listAnswerOption)" +
                            " {                " +
                            "    answerOptionIds.add(objAnswerOption.Id);" +
                            " } " +
                            " List<ICIX_V1__workflow_instance__c> listWorkflowInstance = [select id, ICIX_V1__Request__c from ICIX_V1__workflow_instance__c Where ICIX_V1__Container__c in: containerIds];" +
                            " List<String> workflowInstanceIds = new List<String>(); " +
                            " List<String> requestIds = new List<String>(); " +
                            " for(ICIX_V1__workflow_instance__c objWorkflowInstance : listWorkflowInstance)" +
                            " {                " +
                            "    workflowInstanceIds.add(objWorkflowInstance.Id);" +
                            "    requestIds.add(objWorkflowInstance.ICIX_V1__Request__c);" +
                            " }" +
                            " List<ICIX_V1__workflow_step_instance__c> listWorkflowStepInstance =[select id from ICIX_V1__workflow_step_instance__c Where ICIX_V1__Workflow_Instance__c in:workflowInstanceIds];" +
                            " List<ICIX_V1__Recipient__c> listRecipients =[select id, Name from ICIX_V1__Recipient__c Where ICIX_V1__Workflow_Instance__c in:workflowInstanceIds];" +
                            " List<String> workflowStepInstanceIds = new List<String>();" +
                            " List<String> RecipientIds = new List<String>(); " +
                            " for(ICIX_V1__Recipient__c objRecipient:listRecipients)" +
                            " {" +
                            "    RecipientIds.add(objRecipient.Id);" +
                            " }" +
                            " for(ICIX_V1__workflow_step_instance__c objWorkflowStepInstance : listWorkflowStepInstance)" +
                            " {                " +
                            "    workflowStepInstanceIds.add(objWorkflowStepInstance.Id);               " +
                            " }            " +
                            " List<ICIX_V1__Requirement_Instance__c> listRequirementInstance = new List<ICIX_V1__Requirement_Instance__c>();" +
                            " listRequirementInstance =[SELECT Id FROM ICIX_V1__Requirement_Instance__c Where ICIX_V1__Recipient__c in: RecipientIds];" +
                            " List<ICIX_V1__Request__c> listRequestDelete = new List<ICIX_V1__Request__c>();" +
                            " listRequestDelete = [Select id, ICIX_V1__ICIX_Product__c from ICIX_V1__Request__c Where Id in:requestIds];" +
                            " for(ICIX_V1__Request__c reqList: listRequestDelete)" +
                            " {" +
                            "    ProductIds.add(reqList.ICIX_V1__ICIX_Product__c);" +
                            " }" +
                            " List<ICIX_V1__Container_Template__c> listContainerTemplateDelete= new List<ICIX_V1__Container_Template__c>();" +
                            " listContainerTemplateDelete = [select Id from ICIX_V1__Container_Template__c Where Id in: containerTemplateIds];" +
                            " List<ICIX_V1__ICIX_Product__c> listTodeleteProduct= new List<ICIX_V1__ICIX_Product__c>();" +
                            " listTodeleteProduct = [Select id from ICIX_V1__ICIX_Product__c Where Id in:ProductIds] ;" +
                            " List<ICIX_V1__Testing_Program__c> ListtodeleteTestingProgram= new List<ICIX_V1__Testing_Program__c>();" +
                            " ListtodeleteTestingProgram = [Select id from ICIX_V1__Testing_Program__c where id in:TestingProgramIds];" +
                            " delete listAnswerOption;" +
                            " delete listContainerAnswer;" +
                            " delete listElement;" +
                            " delete listQuestion;" +
                            " delete listSection;" +
                            " delete listTab;" +
                            " delete listLayout;" +
                            " delete listContainer;" +
                            " delete listRecipients;" +
                            " delete listRequirementInstance;" +
                            " delete listWorkflowStepInstance;" +
                            " delete listWorkflowInstance;" +
                            " delete listRequestDelete;" +
                            " delete listContainerTemplateDelete;" +
                            " delete ListtodeleteTestingProgram;" +
                            " delete listTodeleteProduct;";
                    boolean result = executeSalesforceToolingQuery(accessToken, query);
                    if (result) {
                        Log.info("Three actor Forms deleted - PASS");
                    } else {
                        Log.info("Three actor Forms deleted - FAIL");
                    }
                }//end loop


            } catch (Exception e) {
                e.printStackTrace();
            }
        }//end if
    }//End method

    private static void DeleteTestingProgram_Updated(String accessToken, List<String> TestingProgramList) {
        if (!TestingProgramList.isEmpty()) {
            try {

                //Before deleting Testing programm First delete PT forms
                //Get Ids from Testing of programs
                String strQueryTestingProgramms="SELECT Id, Name FROM ICIX_V1__Testing_Program__c Where Name IN("+convertList(TestingProgramList)+")";
                List<Map> testingProgrammsIds=executeQuery(strQueryTestingProgramms);
                List<String> testingProgrammsids = new ArrayList<>();
                for(Map map:testingProgrammsIds){
                    testingProgrammsids.add(map.get("Id").toString());
                }

                //SELECT Id, Name, Set2OrgCStgRK__Testing_Program__c FROM Set2OrgCStgRK__Container_Template__c Where Set2OrgCStgRK__Testing_Program__c = 'a1N46000002OfisEAC'
                String strQueryContainerTemplatesName ="SELECT Id, Name, ICIX_V1__Testing_Program__c FROM ICIX_V1__Container_Template__c Where ICIX_V1__Testing_Program__c IN("+convertList(testingProgrammsids)+")";
                List<Map> containerTemplates=executeQuery(strQueryContainerTemplatesName);
                List<String> lstContainerTemplates = new ArrayList<>();
                for(Map map:containerTemplates){
                    lstContainerTemplates.add(map.get("Name").toString());
                }
                //Remove Testing form that is still in use for 3 actor test data
                //Three actor cleanup
                if (!ThreeActorTestData.GetThreeActorData(ThreeActorTestData.ThreeActorKeys.CleanThreeActorData).equalsIgnoreCase("Yes")) {
                    lstContainerTemplates.remove(ThreeActorTestData.GetThreeActorData(ThreeActorTestData.ThreeActorKeys.CorrectiveActionForm));
                    lstContainerTemplates.remove(ThreeActorTestData.GetThreeActorData(ThreeActorTestData.ThreeActorKeys.ProductTestForm));
                }


                Delete3ActorData(accessToken,lstContainerTemplates);

                Log.info("DeleteTestingProgram method - START ");
                Log.info("Total number of Testing Programs to be deleted #" + TestingProgramList.size());
                Log.info("Testing Programs : " + TestingProgramList);
                Map<Integer, List<String>> batchesMap = ExecuteBatchMessageProcessor.CreateDataDeleteBatches(1, TestingProgramList);
                //Process deletion using batch
                for (Integer key : batchesMap.keySet()) {

                    String strQuery ="List<ICIX_V1__Testing_Program__c> listTestingProgram = new List<ICIX_V1__Testing_Program__c>();" +
                            "listTestingProgram = [SELECT Id, Name FROM ICIX_V1__Testing_Program__c WHERE Name IN ("+ convertList(batchesMap.get(key)) + ")];" +
                            "Set<Id> TestingProgramIds = new Set<Id>();" +
                            "for(ICIX_V1__Testing_Program__c tsp:listTestingProgram)" +
                            "TestingProgramIds.add(tsp.Id);" +
                            "" +
                            "List<ICIX_V1__Preferred_Lab__c> lstPreferredLabs = [SELECT Id, Name, ICIX_V1__Testing_Program__c FROM ICIX_V1__Preferred_Lab__c Where ICIX_V1__Testing_Program__c =: TestingProgramIds];" +
                            "List<ICIX_V1__Testing_Program_Certificate__c> lstTestingProgramCertificates = [SELECT Id, Name, ICIX_V1__Testing_Program__c FROM ICIX_V1__Testing_Program_Certificate__c Where ICIX_V1__Testing_Program__c =: TestingProgramIds];" +
                            "List<ICIX_V1__Product_Test_Category__c> lstProductTestCategories = [SELECT Id, Name, ICIX_V1__Testing_Program__c FROM ICIX_V1__Product_Test_Category__c Where ICIX_V1__Testing_Program__c =: TestingProgramIds];" +
                            "" +
                            "List<ICIX_V1__Product_Test__c> lstProductTest = new List<ICIX_V1__Product_Test__c>();" +
                            "List<ICIX_V1__Product_Test_Limit__c> lstLimits = new List<ICIX_V1__Product_Test_Limit__c>();" +
                            "List<ICIX_V1__Product_Test_Rule__c> lstProductTestRule = new List<ICIX_V1__Product_Test_Rule__c>();" +
                            "List<ICIX_V1__Product_Test_Group__c> lstProductTestGroups = new List<ICIX_V1__Product_Test_Group__c>();" +
                            "List<ICIX_V1__Testing_Program_Product_Category__c> lstTestingProgramProductCategories = new List<ICIX_V1__Testing_Program_Product_Category__c>();" +
                            "List<ICIX_V1__Product_Test_Category_Rule__c> lstProductTestCategoryRule = new List<ICIX_V1__Product_Test_Category_Rule__c>();" +
                            "" +
                            "Set<Id> productTestCategoryIds = new Set<Id>();" +
                            "if(lstProductTestCategories.size() > 0) {" +
                            "" +
                            "    for(ICIX_V1__Product_Test_Category__c objProductTestCategories : lstProductTestCategories)" +
                            "         productTestCategoryIds.add(objProductTestCategories.Id);" +
                            "    " +
                            "    lstProductTest = [SELECT Id, Name, ICIX_V1__Product_Test_Category__c FROM ICIX_V1__Product_Test__c Where ICIX_V1__Product_Test_Category__c in: productTestCategoryIds];" +
                            "    if(lstProductTest.size() > 0){" +
                            "  Set<Id> productTestIds = new Set<Id>();" +
                            "      for(ICIX_V1__Product_Test__c objProductTest : lstProductTest)" +
                            "  productTestIds.add(objProductTest.Id);" +
                            "        " +
                            "      lstLimits = [SELECT Id, Name, ICIX_V1__Product_Test__c FROM ICIX_V1__Product_Test_Limit__c Where ICIX_V1__Product_Test__c in: productTestIds];" +
                            "      lstProductTestRule = [SELECT Id, Name, ICIX_V1__Product_Test__c FROM ICIX_V1__Product_Test_Rule__c Where ICIX_V1__Product_Test__c in: productTestIds];" +
                            "    }" +
                            "    lstProductTestGroups = [SELECT Id, Name, ICIX_V1__Product_Test_Category__c FROM ICIX_V1__Product_Test_Group__c Where ICIX_V1__Product_Test_Category__c in: productTestCategoryIds];" +
                            "    lstTestingProgramProductCategories = [SELECT Id, Name, ICIX_V1__Product_Test_Category__c FROM ICIX_V1__Testing_Program_Product_Category__c Where ICIX_V1__Product_Test_Category__c in: productTestCategoryIds];" +
                            "    " +
                            "    lstProductTestCategoryRule = [SELECT Id, Name, ICIX_V1__Product_Test_Category__c FROM ICIX_V1__Product_Test_Category_Rule__c Where ICIX_V1__Product_Test_Category__c in: productTestCategoryIds];" +
                            "               " +
                            "}" +
                            "" +
                            "List<ICIX_V1__Testing_Program_Product__c> lstTestingProgramProducts = [SELECT Id, Name, ICIX_V1__Testing_Program__c FROM ICIX_V1__Testing_Program_Product__c Where ICIX_V1__Testing_Program__c =: TestingProgramIds];" +
                            "" +
                            "if(lstTestingProgramProducts.size() > 0)Delete lstTestingProgramProducts;" +
                            "if(lstTestingProgramProductCategories.size() > 0)Delete lstTestingProgramProductCategories;" +
                            "if(lstProductTestGroups.size() > 0)Delete lstProductTestGroups;" +
                            "if(lstProductTestRule.size() > 0)Delete lstProductTestRule;" +
                            "if(lstLimits.size() > 0)Delete lstLimits;" +
                            "if(lstProductTest.size() > 0)Delete lstProductTest;" +
                            "if(lstProductTestCategoryRule.size() > 0)Delete lstProductTestCategoryRule;" +
                            "" +
                            "for(ICIX_V1__Product_Test_Category__c objProductTestCategory : lstProductTestCategories)" +
                            "    objProductTestCategory.ICIX_V1__Testing_Program__c = null;        " +
                            "update lstProductTestCategories;" +
                            "" +
                            "List<ICIX_V1__Product_Test_Category__c> lstProductTestCategoriesToDelete = [SELECT Id, Name, ICIX_V1__Testing_Program__c FROM ICIX_V1__Product_Test_Category__c Where Id in: productTestCategoryIds];" +
                            "if(lstProductTestCategories.size() > 0)Delete lstProductTestCategoriesToDelete;" +
                            "" +
                            "if(lstTestingProgramCertificates.size() > 0)Delete lstTestingProgramCertificates;" +
                            "if(lstPreferredLabs.size() > 0)Delete lstPreferredLabs;" +
                            "if(listTestingProgram.size() > 0)Delete listTestingProgram;";

                    boolean result = executeSalesforceToolingQuery(accessToken, strQuery);
                    if (result) {
                        Log.info("Testing Programs deleted - PASS");
                    } else {
                        Log.info("Testing Programs deleted - FAIL");
                    }

                }//end loop


            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.info("DeleteTestingProgram method - END ");
        }//End if
    }//End function



    private static void DeleteTestingProgram(String accessToken, List<String> TestingProgramList) {
        if (!TestingProgramList.isEmpty()) {
            try {
                Log.info("DeleteTestingProgram method - START ");
                Log.info("Total number of Testing Programs to be deleted #" + TestingProgramList.size());
                Log.info("Testing Programs : " + TestingProgramList);
                Map<Integer, List<String>> batchesMap = ExecuteBatchMessageProcessor.CreateDataDeleteBatches(5, TestingProgramList);
                //Process deletion using batch
                for (Integer key : batchesMap.keySet()) {

                    String query = "List<ICIX_V1__Testing_Program__c> listTestingProgram = new List<ICIX_V1__Testing_Program__c>();" +
                            "listTestingProgram = [SELECT Id, Name FROM ICIX_V1__Testing_Program__c WHERE Name IN (" + convertList(batchesMap.get(key)) + ")];" +
                            "List<ICIX_V1__Product_Test_Category__c> listProductTestCategory = new List<ICIX_V1__Product_Test_Category__c>();" +
                            "List<ICIX_V1__Product_Test__c> listProductTest = new List<ICIX_V1__Product_Test__c>();" +
                            "List<ICIX_V1__Product_Test_Limit__c> listProductTestLimit = new List<ICIX_V1__Product_Test_Limit__c>();" +
                            "Set<String> testingProgramIdSet = new Set<String>();" +
                            "Set<String> productTestCategoryIdSet = new Set<String>();" +
                            "Set<String> productTestIdSet = new Set<String>();" +
                            "List<ICIX_V1__Preferred_Lab__c> listPreferredLab = new List<ICIX_V1__Preferred_Lab__c>();" +
                            "List<ICIX_V1__User_Notification_Preference__c> listUserNotiPref = new List<ICIX_V1__User_Notification_Preference__c>();" +
                            "for(ICIX_V1__Testing_Program__c testingProg : listTestingProgram){" +
                            "testingProgramIdSet.Add(testingProg.Id);" +
                            "}" +

                            "if(listTestingProgram.size() > 0){" +
                            "listProductTestCategory = [SELECT Id, Name, ICIX_V1__Testing_Program__c FROM ICIX_V1__Product_Test_Category__c WHERE ICIX_V1__Testing_Program__c IN :   testingProgramIdSet];" +
                            "listPreferredLab = [SELECT Id, Name, ICIX_V1__Testing_Program__c FROM ICIX_V1__Preferred_Lab__c WHERE ICIX_V1__Testing_Program__c IN : testingProgramIdSet];" +
                            "listUserNotiPref = [SELECT Id, Name, ICIX_V1__Testing_Program__c FROM ICIX_V1__User_Notification_Preference__c WHERE ICIX_V1__Testing_Program__c IN : testingProgramIdSet];" +

                            "System.debug('listProductTestCategory size => ' + listProductTestCategory.size());" +
                            "System.debug('listPreferredLab size => ' + listPreferredLab.size());" +
                            "System.debug('listUserNotiPref size => ' + listUserNotiPref.size());" +
                            "}" +
                            "if(listProductTestCategory.size() > 0){" +
                            "for(ICIX_V1__Product_Test_Category__c productTestCat : listProductTestCategory){" +
                            "productTestCategoryIdSet.Add(productTestCat.Id);" +
                            "}" +
                            "}" +
                            "listProductTest = [SELECT Id, Name, ICIX_V1__Product_Test_Category__c FROM ICIX_V1__Product_Test__c WHERE ICIX_V1__Product_Test_Category__c IN : productTestCategoryIdSet];" +
                            "System.debug('listProductTest size => ' + listProductTest.size());" +
                            "if(listProductTestCategory.size() > 0){" +
                            "for(ICIX_V1__Product_Test__c productTest : listProductTest){" +
                            "productTestIdSet.Add(productTest.Id);" +
                            "}" +
                            "}" +
                            "listProductTestLimit = [SELECT Id, Name, CreatedDate, ICIX_V1__Product_Test__c FROM ICIX_V1__Product_Test_Limit__c WHERE ICIX_V1__Product_Test__c IN : productTestIdSet];" +
                            "System.debug('listProductTestLimit size => ' + listProductTestLimit.size());" +

                            "delete listPreferredLab;" +
                            "delete listUserNotiPref;" +
                            "delete listProductTestLimit;" +
                            "delete listProductTest;" +
                            "delete listProductTestCategory;" +
                            "delete listTestingProgram;";

                    boolean result = executeSalesforceToolingQuery(accessToken, query);
                    if (result) {
                        Log.info("Testing Programs deleted - PASS");
                    } else {
                        Log.info("Testing Programs deleted - FAIL");
                    }

                }//end loop


            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.info("DeleteTestingProgram method - END ");
        }//End if
    }//End function

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


    public static void ExecuteBatch(USER user) {
        Log.info("Executing batch for user: " + user.toString());
        boolean result = false;
        lastLoginUser = user;
        switch (user) {
            case REQUESTER:
                userName = Users.Requester_Admin.getUserId();
                password = Users.Requester_Admin.getPassword();
                break;
            case RESPONDER:
                userName = Users.Responder_Admin.getUserId();
                password = Users.Responder_Admin.getPassword();
                break;
            case LAB:
                userName = Users.Lab_Admin.getUserId();
                password = Users.Lab_Admin.getPassword();
                break;
        }//End switch
        result = executedMessageProcessor(getSalesforceAccessToken());
        if (result) {
            Log.info("Batch executed - PASS");
        } else {
            Log.info("Batch executed - FAIL");
        }
    }//End function

    public static void ExecuteBatch(USER user, String messageId) {
        Log.info("Executing batch for user: " + user.toString());
        boolean result = false;
        lastLoginUser = user;
        switch (user) {
            case REQUESTER:
                userName = Users.Requester_Admin.getUserId();
                password = Users.Requester_Admin.getPassword();
                break;
            case RESPONDER:
                userName = Users.Responder_Admin.getUserId();
                password = Users.Responder_Admin.getPassword();
                break;
            case LAB:
                userName = Users.Lab_Admin.getUserId();
                password = Users.Lab_Admin.getPassword();
                break;
        }//End switch
        String token = getSalesforceAccessToken();
        result = executedMessageProcessorWithMessageId(token, messageId);
        if (result) {
            Log.info("Batch executed - PASS");
        } else {
            Log.info("Batch executed - FAIL");
        }
    }//End function


    /**
     * <h1>ExecuteBatchWithLastLoginUser</h1>
     */

    public static void ExecuteBatchWithLastLoginUser() {
        Log.info("Executing batch for last login user...");
        ExecuteBatch(lastLoginUser);
    }//End method

    public static void ExecuteBatchWithLastLoginUser(String messageId) {
        Log.info("Executing batch for last login user. With given message id: " + messageId);
        ExecuteBatch(lastLoginUser, messageId);
    }//End method

    public static void CleanUpOrgs() {
        CleanUpOrgsRequester();
        CleanUpOrgsResponder();
        CleanUpOrgslab();
    }//End method


    public static void cleanUpAutomationData() {
        getSalesforceAccessToken();

        DeleteTradingPartnerGroup(currentSession.getAccessToken(), new ArrayList<String>(GlobalTestData.TPGroupsToDelete));
        //Delete product group
        DeleteProductGroup(currentSession.getAccessToken(), new ArrayList<String>(GlobalTestData.ProductGroupsToDelete));
        //Delete requests
        DeleteRequests(currentSession.getAccessToken(), new ArrayList<String>(GlobalTestData.RequestsToDelete));
        //Delete forms
        DeleteForms(currentSession.getAccessToken(), new ArrayList<String>(GlobalTestData.FormBuildersFormNamesToDelete));
        // DeleteAttribute
        DeleteAttribute(currentSession.getAccessToken(), new ArrayList<String>(GlobalTestData.AttributeToDelete));

        //Combine both forms
        GlobalTestData.ProductTestFormToDelete.addAll(GlobalTestData.CorrectiveActionFormToDelete);

        //Three actor cleanup
        if (!ThreeActorTestData.GetThreeActorData(ThreeActorTestData.ThreeActorKeys.CleanThreeActorData).equalsIgnoreCase("Yes")) {
            GlobalTestData.ProductsToDelete.remove(ThreeActorTestData.GetThreeActorData(ThreeActorTestData.ThreeActorKeys.ProductName));
            GlobalTestData.ProductTestManagerToDelete.remove(ThreeActorTestData.GetThreeActorData(ThreeActorTestData.ThreeActorKeys.ProductTestManager));
            GlobalTestData.ProductTestFormToDelete.remove(ThreeActorTestData.GetThreeActorData(ThreeActorTestData.ThreeActorKeys.CorrectiveActionForm));
            GlobalTestData.ProductTestFormToDelete.remove(ThreeActorTestData.GetThreeActorData(ThreeActorTestData.ThreeActorKeys.ProductTestForm));
        }

        //Delete Products
        DeleteProducts(currentSession.getAccessToken(), new ArrayList<String>(GlobalTestData.ProductsToDelete));

        //DeleteTestingProgram(token, new ArrayList<String>(GlobalTestData.ProductTestManagerToDelete));
        DeleteDocuments(currentSession.getAccessToken(), new ArrayList<String>(GlobalTestData.DocumentToDelete));
        Delete3ActorData(currentSession.getAccessToken(), new ArrayList<String>(GlobalTestData.ProductTestFormToDelete));
        DeleteTestingProgram_Updated(currentSession.getAccessToken(), new ArrayList<String>(GlobalTestData.ProductTestManagerToDelete));

    }

    public static void CleanUpOrgsRequester() {
        //Clean up Requester_Admin
        userName = Users.Requester_Admin.getUserId();
        password = Users.Requester_Admin.getPassword();
        Log.info("Clean up Requester_Admin - Start");
        //Delete TP Groups
        cleanUpAutomationData();
        Log.info("Clean up Requester_Admin - Finished");
    }//End method

    public static void CleanUpOrgsResponder() {
        //Clean up Requester_Admin
        userName = Users.Responder_Admin.getUserId();
        password = Users.Responder_Admin.getPassword();
        Log.info("Clean up Responder_Admin - Start");
        cleanUpAutomationData();

        Log.info("Clean up Responder_Admin - Finished");
    }//End method

    public static void CleanUpOrgslab() {
        //Clean up Requester_Admin
        userName = Users.Lab_Admin.getUserId();
        password = Users.Lab_Admin.getPassword();
        Log.info("Clean up LAB - Start");
        cleanUpAutomationData();
        Log.info("Clean up LAB - Finished");

    }//End method

    private static String convertList(List<String> lst) {
        String strList = "";
        for (String S : lst) {
            String tmp = String.format("'%s',", S);
            strList = strList + tmp;
        }
        strList = strList.substring(0, strList.length() - 1);
        return strList;
    }//End method

    public enum USER {REQUESTER, RESPONDER, LAB}

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
    }//End of function

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

    /**
     * <h1>Create form using Tooling API</h1>
     * <p>Purpose: This method will create ICIX form</p>
     * <p>Form detail: 1 Tab > 1 Section > 3 Linked questions(2 Text type and 1 Upload type )</p>
     *
     * @param formName
     * @return
     */
    public static boolean CreateFormUsingScript(String formName) {
        String query = "ICIX_V1__Container_Template__c containerTemplateObj = new ICIX_V1__Container_Template__c(); " +
                "                  containerTemplateObj.Name = '" + formName + "'; " +
                "                        containerTemplateObj.ICIX_V1__Type__c = 'Form'; " +
                "                        containerTemplateObj.ICIX_V1__Container_Type__c = 'Single Form'; " +
                "                        containerTemplateObj.ICIX_V1__Version__c = 1; " +
                "                        containerTemplateObj.ICIX_V1__Active__c = true; " +
                "                        containerTemplateObj.ICIX_V1__Status__c = 'Published'; " +
                "                        containerTemplateObj.ICIX_V1__Workflow_Definition_Global_Id__c = 'WD2'; " +
                "                        insert containerTemplateObj;" +
                "                String formId = containerTemplateObj.Id; " +
                "                Integer    CONTAINERS_COUNT = 1; " +
                "                Integer           TAB_COUNT = 1; " +
                "                Integer       SECTION_COUNT = 1; " +
                "                Integer      QUESTION_COUNT = 1; " +
                "                Integer      QUESTION_UPLOAD_COUNT = 1; " +
                "                List<ICIX_V1__Layout__c>                layouts = new List<ICIX_V1__Layout__c>(); " +
                "                List<ICIX_V1__Tab__c>                      tabs = new List<ICIX_V1__Tab__c>(); " +
                "                List<ICIX_V1__Section__c>              sections = new List<ICIX_V1__Section__c>(); " +
                "                List<ICIX_V1__Answer_Option__c>   answerOptions = new List<ICIX_V1__Answer_Option__c>(); " +
                "                layouts = [SELECT Id FROM ICIX_V1__Layout__c WHERE ICIX_V1__Parent_Form__c =: formId ]; " +
                "                if(layouts.size() == 0) { " +
                "                 ICIX_V1__Layout__c tempLayout =  " +
                "                                    new ICIX_V1__Layout__c(Name = 'Layout',  " +
                "                                        ICIX_V1__Parent_Form__c = formId,  " +
                "                                           ICIX_V1__Is_Valid__c = true,  " +
                "                                            ICIX_V1__UI_Type__c = 'desktop'); " +
                "                 layouts.add(tempLayout); " +
                "                    database.insert(layouts); " +
                "                } " +
                "                " +
                "                for(ICIX_V1__Layout__c layout: layouts) { " +
                "                    for(Integer i = 1; i < TAB_COUNT + 1; i++) { " +
                "                        ICIX_V1__Tab__c tempTab =  " +
                "                            new ICIX_V1__Tab__c(Name = 'Tab '+i, " +
                "                                       ICIX_V1__Disabled__c = false, " +
                "                                       ICIX_V1__ParentLayout__c = layout.Id, " +
                "                                       ICIX_V1__Order__c = i);  " +
                "                        tabs.add(tempTab); " +
                "                    } " +
                "                } " +
                "                database.insert(tabs); " +
                "                " +
                "                for(ICIX_V1__Tab__c tab: tabs) { " +
                "                    for(Integer i = 1; i < SECTION_COUNT + 1; i++) { " +
                "                        ICIX_V1__Section__c tempSection =  " +
                "                            new ICIX_V1__Section__c(Name = tab.Name+' Section ' + i, " +
                "                                           ICIX_V1__Order__c = i, " +
                "                                           ICIX_V1__Parent_Tab__c = tab.Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        sections.add(tempSection); " +
                "                    }        " +
                "                } " +
                "                database.insert(sections); " +
                "                for(Integer k = 0; k < sections.size(); k++) { " +
                "                    list<ICIX_V1__Question__c> questions = new list<ICIX_V1__Question__c>(); " +
                "                     " +
                "                    for(Integer i = 0; i <= QUESTION_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Question-'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'text', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'Question-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +
                "                  for(Integer i = 0; i <= QUESTION_UPLOAD_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Upload document -'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'upload', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'Upload document-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +
                        "                  for(Integer i = 0; i < QUESTION_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Long Text- -'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'long text', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'Long Text--'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +

                "                     " +
                "                    insert questions; " +
                "                     " +
                "                    list<ICIX_V1__Element__c> elements = new list<ICIX_V1__Element__c>(); " +
                "                    for(Integer i = 0; i < questions.size(); i++) { " +
                "                        " +
                "                        ICIX_V1__Element__c tempElement =  " +
                "                            new ICIX_V1__Element__c(Name = questions[i].Name +' Element ',  " +
                "                                           ICIX_V1__Columns__c = '6;6;12', " +
                "                                           ICIX_V1__Order__c = i + 1, " +
                "                                           ICIX_V1__LinkedQuestion__c = questions[i].Id, " +
                "                                           ICIX_V1__ParentSection__c = sections[k].Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        elements.add(tempElement);  " +
                "                    } " +
                "                    insert elements; " +
                "                } " +
                "                database.insert(answerOptions);";

        boolean result = false;
        userName = Users.Requester_Admin.getUserId();
        password = Users.Requester_Admin.getPassword();
        try {
            String accessToken = getSalesforceAccessToken();
            result = executeSalesforceToolingQuery(accessToken, query);
            if (result) {
                Log.info("Form created- PASS");
                GlobalTestData.FormBuildersFormNamesToDelete.add(formName);
            } else {
                Log.info("Form created - FAIL");
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    public static boolean CreateFormUsingScript_NPanel(String formName) {

        String query = "ICIX_V1__Container_Template__c containerTemplateObj = new ICIX_V1__Container_Template__c(); " +
                "                  containerTemplateObj.Name = '" + formName + "'; " +
                "                        containerTemplateObj.ICIX_V1__Type__c = 'Form'; " +
                "                        containerTemplateObj.ICIX_V1__Container_Type__c = 'Single Form'; " +
                "                        containerTemplateObj.ICIX_V1__Version__c = 1; " +
                "                        containerTemplateObj.ICIX_V1__Active__c = true; " +
                "                        containerTemplateObj.ICIX_V1__Status__c = 'Published'; " +
                "                        containerTemplateObj.ICIX_V1__Workflow_Definition_Global_Id__c = 'WD2'; " +
                "                        insert containerTemplateObj;" +
                "                String formId = containerTemplateObj.Id; " +
                "                Integer    CONTAINERS_COUNT = 1; " +
                "                Integer           TAB_COUNT = 1; " +
                "                Integer       SECTION_COUNT = 1; " +
                "                Integer      QUESTION_COUNT = -1; " +
                "                Integer      NPANEL_COUNT = 1; " +
                "                List<ICIX_V1__Layout__c>                layouts = new List<ICIX_V1__Layout__c>(); " +
                "                List<ICIX_V1__Tab__c>                      tabs = new List<ICIX_V1__Tab__c>(); " +
                "                List<ICIX_V1__Section__c>              sections = new List<ICIX_V1__Section__c>(); " +
                "                List<ICIX_V1__Answer_Option__c>   answerOptions = new List<ICIX_V1__Answer_Option__c>(); " +
                "                layouts = [SELECT Id FROM ICIX_V1__Layout__c WHERE ICIX_V1__Parent_Form__c =: formId ]; " +
                "                if(layouts.size() == 0) { " +
                "                 ICIX_V1__Layout__c tempLayout =  " +
                "                                    new ICIX_V1__Layout__c(Name = 'Layout',  " +
                "                                        ICIX_V1__Parent_Form__c = formId,  " +
                "                                           ICIX_V1__Is_Valid__c = true,  " +
                "                                            ICIX_V1__UI_Type__c = 'desktop'); " +
                "                 layouts.add(tempLayout); " +
                "                    database.insert(layouts); " +
                "                } " +
                "                " +
                "                for(ICIX_V1__Layout__c layout: layouts) { " +
                "                    for(Integer i = 1; i < TAB_COUNT + 1; i++) { " +
                "                        ICIX_V1__Tab__c tempTab =  " +
                "                            new ICIX_V1__Tab__c(Name = 'Tab '+i, " +
                "                                       ICIX_V1__Disabled__c = false, " +
                "                                       ICIX_V1__ParentLayout__c = layout.Id, " +
                "                                       ICIX_V1__Order__c = i);  " +
                "                        tabs.add(tempTab); " +
                "                    } " +
                "                } " +
                "                database.insert(tabs); " +
                "                " +
                "                for(ICIX_V1__Tab__c tab: tabs) { " +
                "                    for(Integer i = 1; i < SECTION_COUNT + 1; i++) { " +
                "                        ICIX_V1__Section__c tempSection =  " +
                "                            new ICIX_V1__Section__c(Name = tab.Name+' Section ' + i, " +
                "                                           ICIX_V1__Order__c = i, " +
                "                                           ICIX_V1__Parent_Tab__c = tab.Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        sections.add(tempSection); " +
                "                    }        " +
                "                } " +
                "                database.insert(sections); " +
                "                for(Integer k = 0; k < sections.size(); k++) { " +
                "                    list<ICIX_V1__Question__c> questions = new list<ICIX_V1__Question__c>(); " +
                "                     " +
                "                    for(Integer i = 0; i <= QUESTION_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Question-'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'text', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'Question-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +
                "                  for(Integer i = 0; i <= NPANEL_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'NPanel -'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'nutritional', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'NPanel-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +

                "                     " +
                "                    insert questions; " +
                "                     " +
                "                    list<ICIX_V1__Element__c> elements = new list<ICIX_V1__Element__c>(); " +
                "                    for(Integer i = 0; i < questions.size(); i++) { " +
                "                        " +
                "                        ICIX_V1__Element__c tempElement =  " +
                "                            new ICIX_V1__Element__c(Name = questions[i].Name +' Element ',  " +
                "                                           ICIX_V1__Columns__c = '6;6;12', " +
                "                                           ICIX_V1__Order__c = i + 1, " +
                "                                           ICIX_V1__LinkedQuestion__c = questions[i].Id, " +
                "                                           ICIX_V1__ParentSection__c = sections[k].Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        elements.add(tempElement);  " +
                "                    } " +
                "                    insert elements; " +
                "                } " +
                "                database.insert(answerOptions);";

        boolean result = false;
        userName = Users.Requester_Admin.getUserId();
        password = Users.Requester_Admin.getPassword();
        try {
            result = executeSalesforceToolingQuery(getSalesforceAccessToken(), query);
            if (result) {
                Log.info("Form created- PASS");
                GlobalTestData.FormBuildersFormNamesToDelete.add(formName);
            } else {
                Log.info("Form created - FAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }//End method


    public static boolean CreateFormWithDifferentTypeUsingScript(String formName, String type) {

        String query = "ICIX_V1__Container_Template__c containerTemplateObj = new ICIX_V1__Container_Template__c(); " +
                "                  containerTemplateObj.Name = '" + formName + "'; " +
                "                        containerTemplateObj.ICIX_V1__Type__c = '" + type + "'; " +
                "                        containerTemplateObj.ICIX_V1__Container_Type__c = 'Single Form'; " +
                "                        containerTemplateObj.ICIX_V1__Version__c = 1; " +
                "                        containerTemplateObj.ICIX_V1__Active__c = true; " +
                "                        containerTemplateObj.ICIX_V1__Status__c = 'Published'; " +
                "                        containerTemplateObj.ICIX_V1__Workflow_Definition_Global_Id__c = 'WD2'; " +
                "                        insert containerTemplateObj;" +
                "                String formId = containerTemplateObj.Id; " +
                "                Integer    CONTAINERS_COUNT = 1; " +
                "                Integer           TAB_COUNT = 1; " +
                "                Integer       SECTION_COUNT = 1; " +
                "                Integer      QUESTION_COUNT = 1; " +
                "                List<ICIX_V1__Layout__c>                layouts = new List<ICIX_V1__Layout__c>(); " +
                "                List<ICIX_V1__Tab__c>                      tabs = new List<ICIX_V1__Tab__c>(); " +
                "                List<ICIX_V1__Section__c>              sections = new List<ICIX_V1__Section__c>(); " +
                "                List<ICIX_V1__Answer_Option__c>   answerOptions = new List<ICIX_V1__Answer_Option__c>(); " +
                "                layouts = [SELECT Id FROM ICIX_V1__Layout__c WHERE ICIX_V1__Parent_Form__c =: formId ]; " +
                "                if(layouts.size() == 0) { " +
                "                 ICIX_V1__Layout__c tempLayout =  " +
                "                                    new ICIX_V1__Layout__c(Name = 'Layout',  " +
                "                                        ICIX_V1__Parent_Form__c = formId,  " +
                "                                           ICIX_V1__Is_Valid__c = true,  " +
                "                                            ICIX_V1__UI_Type__c = 'desktop'); " +
                "                 layouts.add(tempLayout); " +
                "                    database.insert(layouts); " +
                "                } " +
                "                " +
                "                for(ICIX_V1__Layout__c layout: layouts) { " +
                "                    for(Integer i = 1; i < TAB_COUNT + 1; i++) { " +
                "                        ICIX_V1__Tab__c tempTab =  " +
                "                            new ICIX_V1__Tab__c(Name = 'Tab '+i, " +
                "                                       ICIX_V1__Disabled__c = false, " +
                "                                       ICIX_V1__ParentLayout__c = layout.Id, " +
                "                                       ICIX_V1__Order__c = i);  " +
                "                        tabs.add(tempTab); " +
                "                    } " +
                "                } " +
                "                database.insert(tabs); " +
                "                " +
                "                for(ICIX_V1__Tab__c tab: tabs) { " +
                "                    for(Integer i = 1; i < SECTION_COUNT + 1; i++) { " +
                "                        ICIX_V1__Section__c tempSection =  " +
                "                            new ICIX_V1__Section__c(Name = tab.Name+' Section ' + i, " +
                "                                           ICIX_V1__Order__c = i, " +
                "                                           ICIX_V1__Parent_Tab__c = tab.Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        sections.add(tempSection); " +
                "                    }        " +
                "                } " +
                "                database.insert(sections); " +
                "                for(Integer k = 0; k < sections.size(); k++) { " +
                "                    list<ICIX_V1__Question__c> questions = new list<ICIX_V1__Question__c>(); " +
                "                     " +
                "                    for(Integer i = 1; i <= QUESTION_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Question-'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'text', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'Question-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +
                "                     " +
                "                    insert questions; " +
                "                     " +
                "                    list<ICIX_V1__Element__c> elements = new list<ICIX_V1__Element__c>(); " +
                "                    for(Integer i = 0; i < questions.size(); i++) { " +
                "                        " +
                "                        ICIX_V1__Element__c tempElement =  " +
                "                            new ICIX_V1__Element__c(Name = questions[i].Name +' Element ',  " +
                "                                           ICIX_V1__Columns__c = '6;6;12', " +
                "                                           ICIX_V1__Order__c = i + 1, " +
                "                                           ICIX_V1__LinkedQuestion__c = questions[i].Id, " +
                "                                           ICIX_V1__ParentSection__c = sections[k].Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        elements.add(tempElement);  " +
                "                    } " +
                "                    insert elements; " +
                "                } " +
                "                database.insert(answerOptions);";

        boolean result = false;
        userName = Users.Requester_Admin.getUserId();
        password = Users.Requester_Admin.getPassword();
        try {
            result = executeSalesforceToolingQuery(getSalesforceAccessToken(), query);
            if (result) {
                Log.info("Form created- PASS");
                GlobalTestData.FormBuildersFormNamesToDelete.add(formName);
            } else {
                Log.info("Form created - FAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }//End method

    public static boolean CreateFormWithMultipleTabUsingScript(String formName) {
        String query = "ICIX_V1__Container_Template__c containerTemplateObj = new ICIX_V1__Container_Template__c(); " +
                "                  containerTemplateObj.Name = '" + formName + "'; " +
                "                        containerTemplateObj.ICIX_V1__Type__c = 'Form'; " +
                "                        containerTemplateObj.ICIX_V1__Container_Type__c = 'Single Form'; " +
                "                        containerTemplateObj.ICIX_V1__Version__c = 1; " +
                "                        containerTemplateObj.ICIX_V1__Active__c = true; " +
                "                        containerTemplateObj.ICIX_V1__Status__c = 'Published'; " +
                "                        containerTemplateObj.ICIX_V1__Workflow_Definition_Global_Id__c = 'WD2'; " +
                "                        insert containerTemplateObj;" +
                "                String formId = containerTemplateObj.Id; " +
                "                Integer    CONTAINERS_COUNT = 1; " +
                "                Integer           TAB_COUNT = 2; " +
                "                Integer       SECTION_COUNT = 1; " +
                "                Integer      QUESTION_COUNT = 1; " +
                "                Integer      QUESTION_UPLOAD_COUNT = 1; " +
                "                List<ICIX_V1__Layout__c>                layouts = new List<ICIX_V1__Layout__c>(); " +
                "                List<ICIX_V1__Tab__c>                      tabs = new List<ICIX_V1__Tab__c>(); " +
                "                List<ICIX_V1__Section__c>              sections = new List<ICIX_V1__Section__c>(); " +
                "                List<ICIX_V1__Answer_Option__c>   answerOptions = new List<ICIX_V1__Answer_Option__c>(); " +
                "                layouts = [SELECT Id FROM ICIX_V1__Layout__c WHERE ICIX_V1__Parent_Form__c =: formId ]; " +
                "                if(layouts.size() == 0) { " +
                "                 ICIX_V1__Layout__c tempLayout =  " +
                "                                    new ICIX_V1__Layout__c(Name = 'Layout',  " +
                "                                        ICIX_V1__Parent_Form__c = formId,  " +
                "                                           ICIX_V1__Is_Valid__c = true,  " +
                "                                            ICIX_V1__UI_Type__c = 'desktop'); " +
                "                 layouts.add(tempLayout); " +
                "                    database.insert(layouts); " +
                "                } " +
                "                " +
                "                for(ICIX_V1__Layout__c layout: layouts) { " +
                "                    for(Integer i = 0; i < TAB_COUNT; i++) { " +
                "                        ICIX_V1__Tab__c tempTab =  " +
                "                            new ICIX_V1__Tab__c(Name = 'Tab '+i, " +
                "                                       ICIX_V1__Disabled__c = false, " +
                "                                       ICIX_V1__ParentLayout__c = layout.Id, " +
                "                                       ICIX_V1__Order__c = i);  " +
                "                        tabs.add(tempTab); " +
                "                    } " +
                "                } " +
                "                database.insert(tabs); " +
                "                " +
                "                for(ICIX_V1__Tab__c tab: tabs) { " +
                "                    for(Integer i = 0; i < SECTION_COUNT; i++) { " +
                "                        ICIX_V1__Section__c tempSection =  " +
                "                            new ICIX_V1__Section__c(Name = tab.Name+' Section ' + i, " +
                "                                           ICIX_V1__Order__c = i, " +
                "                                           ICIX_V1__Parent_Tab__c = tab.Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        sections.add(tempSection); " +
                "                    }        " +
                "                } " +
                "                database.insert(sections); " +
                "                for(Integer k = 0; k < sections.size(); k++) { " +
                "                    list<ICIX_V1__Question__c> questions = new list<ICIX_V1__Question__c>(); " +
                "                     " +
                "                    for(Integer i = 0; i <= QUESTION_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Question-'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'text', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'Question-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +
                "                  for(Integer i = 0; i <= QUESTION_UPLOAD_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Upload document -'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'upload', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'Upload document-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +
                "                    insert questions; " +
                "                     " +
                "                    list<ICIX_V1__Element__c> elements = new list<ICIX_V1__Element__c>(); " +
                "                    for(Integer i = 0; i < questions.size(); i++) { " +
                "                        " +
                "                        ICIX_V1__Element__c tempElement =  " +
                "                            new ICIX_V1__Element__c(Name = questions[i].Name +' Element ',  " +
                "                                           ICIX_V1__Columns__c = '6;6;12', " +
                "                                           ICIX_V1__Order__c = i + 1, " +
                "                                           ICIX_V1__LinkedQuestion__c = questions[i].Id, " +
                "                                           ICIX_V1__ParentSection__c = sections[k].Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        elements.add(tempElement);  " +
                "                    } " +
                "                    insert elements; " +
                "                } " +
                "                database.insert(answerOptions);";

        boolean result = false;
        userName = Users.Requester_Admin.getUserId();
        password = Users.Requester_Admin.getPassword();
        try {
            String accessToken = getSalesforceAccessToken();
            result = executeSalesforceToolingQuery(accessToken, query);
            if (result) {
                Log.info("Form created- PASS");
                GlobalTestData.FormBuildersFormNamesToDelete.add(formName);
            } else {
                Log.info("Form created - FAIL");
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    public static Map<Integer, List<String>> CreateDataDeleteBatches(int batchSize, List<String> dataList) {
        Map<Integer, List<String>> batchesMap = new HashMap<>();
        int mapCounter = 0;
        if (!dataList.isEmpty()) {

            System.out.println("List size to process: " + dataList.size());
            System.out.println("List data to process: " + dataList.toString());
            int maxElementToPop = batchSize;
            Queue<String> allDataQueue = new LinkedList<>();
            for (String data : dataList) {
                allDataQueue.add(data);
            }
            int end = allDataQueue.size();

            for (int start = 0; start <= end; start++) {
                Queue<String> subDataQueue = new LinkedList<>();

                //if more than 3 values then take 3 element from queue
                if (end > maxElementToPop) {
                    //Add data into sub queue
                    for (int i = 0; i < maxElementToPop; i++) {
                        subDataQueue.add(((LinkedList<String>) allDataQueue).pop());
                    }

                    //updated the loop end counter after removing data from allDataQueue
                    end = allDataQueue.size();
                    //Reset start to 0 in order to process
                    start = 0;

                } else {

                    int j = allDataQueue.size();
                    for (int i = 0; i < j; i++) {
                        subDataQueue.add(((LinkedList<String>) allDataQueue).pop());
                    }//end loop

                    //updated the loop end counter after removing users from allUsersQueue
                    end = allDataQueue.size();

                }

                //Now create a delete list based on the sub queue and make it empty to receive new list
                List<String> subList = new ArrayList<>();
                int qSize = subDataQueue.size();
                for (int q = 0; q < qSize; q++) {
                    String singleElement = ((LinkedList<String>) subDataQueue).pop();
                    subList.add(singleElement);
                }
                //Add  sub list to map here
                mapCounter++;
                batchesMap.put(mapCounter, subList);
            }//List of entire data


        }//end outer if
        Log.info("Total batches# " + batchesMap.size());
        return batchesMap;
    }


    public static void DeleteDocuments(String accessToken, List<String> dataList) {
        if (!dataList.isEmpty()) {
            try {
                Log.info("Total number of Documents to be deleted #" + dataList.size());
                Log.info("Documents : " + dataList);
                Map<Integer, List<String>> batchesMap = ExecuteBatchMessageProcessor.CreateDataDeleteBatches(5, dataList);
                //Process deletion using batch
                for (Integer key : batchesMap.keySet()) {

                    String query = "List<ICIX_V1__ICIX_Document__c> listDoc = new List<ICIX_V1__ICIX_Document__c>();" +
                            "List<ContentDistribution> contentDistributionList = new List<ContentDistribution>();" +
                            "Set<String> contentDistributionNames = new Set<String>();" +
                            "Set<String> contentDocumentIdSet = new Set<String>();" +
                            "Set<String> containerAnswerIdSet = new Set<String>();" +
                            "Set<String> containerInstanceIdSet = new Set<String>();" +
                            "listDoc = [SELECT Id, Name,ICIX_V1__Override__c, CreatedDate, ICIX_V1__Container_Answer__c, ICIX_V1__Container_Instance__c FROM ICIX_V1__ICIX_Document__c where Name IN (" + convertList(batchesMap.get(key)) + ")];" +
                            "System.debug('listDoc => ' + listDoc.size());" +
                            "for(ICIX_V1__ICIX_Document__c icixDoc : listDoc){" +
                            "    System.debug('icixDoc = > ' + icixDoc);" +
                            "    icixDoc.ICIX_V1__Override__c = true;" +
                            "	icixDoc.ICIX_V1__IsDelete__c = true;" +
                            "	contentDistributionNames.Add(icixDoc.Name);" +
                            "   containerAnswerIdSet.Add(icixDoc.ICIX_V1__Container_Answer__c);" +
                            "containerInstanceIdSet.Add(icixDoc.ICIX_V1__Container_Instance__c);" +
                            "}" +
                            "contentDistributionList = [select Id, CreatedDate, Name, ContentVersionId, ContentDocumentId from ContentDistribution Where ContentDistribution.Name IN : contentDistributionNames];" +
                            "System.debug('contentDistributionList => ' + contentDistributionList.size());" +
                            "for(ContentDistribution contentDist : contentDistributionList){" +
                            "      contentDocumentIdSet.Add(contentDist.ContentDocumentId);" +
                            "}" +
                            "if(listDoc.size() > 0){" +
                            "    System.debug('containerAnswerIdSet => ' + containerAnswerIdSet.size());" +
                            "	System.debug('containerInstanceIdSet => ' + containerInstanceIdSet.size());" +
                            "   for (string setElement : containerAnswerIdSet) {        " +
                            "    	System.debug('containerAnswerIdSet => ' + setElement);" +
                            "	}" +
                            "    for (string setElement : containerInstanceIdSet) {        " +
                            "    	System.debug('containerInstanceIdSet => ' + setElement);" +
                            "	}    " +
                            "    List<ICIX_V1__Container_Answer__c> lstContainerAns = new List<ICIX_V1__Container_Answer__c>();" +
                            "    List<ICIX_V1__Container__c> lstContainerInstance = new List<ICIX_V1__Container__c>();    " +
                            "    lstContainerInstance = [SELECT Id FROM ICIX_V1__Container__c Where Id IN : containerInstanceIdSet ];        " +
                            "    lstContainerAns = [SELECT Id FROM ICIX_V1__Container_Answer__c Where Id IN : containerAnswerIdSet ];" +
                            "    delete [select Id, Title from ContentDocument Where Id IN : contentDocumentIdSet ];	" +
                            "	delete [select Id, Name from ContentDistribution Where ContentDistribution.Name IN : contentDistributionNames ];    " +
                            "    System.debug('lstContainerAns ' + lstContainerAns.size());" +
                            "    System.debug('lstContainerInstance ' + lstContainerInstance.size());" +
                            "    delete [SELECT Id FROM ICIX_V1__Container_Answer__c Where Id IN : containerAnswerIdSet ];" +
                            "	delete [SELECT Id FROM ICIX_V1__Container__c Where Id IN : containerInstanceIdSet ];" +
                            "	update listDoc;	" +
                            "}";

                    boolean result = executeSalesforceToolingQuery(accessToken, query);
                    if (result) {
                        Log.info("Documents deleted - PASS");

                    } else {
                        Log.info("Documents deleted - FAIL");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//End if
    }//End function

    public static boolean CreateFormWithTableStyleUsingScript(String formName) {
        String query = "ICIX_V1__Container_Template__c containerTemplateObj = new ICIX_V1__Container_Template__c(); " +
                "                  containerTemplateObj.Name = '" + formName + "'; " +
                "                        containerTemplateObj.ICIX_V1__Type__c = 'Form'; " +
                "                        containerTemplateObj.ICIX_V1__Container_Type__c = 'Single Form'; " +
                "                        containerTemplateObj.ICIX_V1__Version__c = 1; " +
                "                        containerTemplateObj.ICIX_V1__Active__c = true; " +
                "                        containerTemplateObj.ICIX_V1__Status__c = 'Published'; " +
                "                        containerTemplateObj.ICIX_V1__Workflow_Definition_Global_Id__c = 'WD2'; " +
                "                        insert containerTemplateObj;" +
                "                String formId = containerTemplateObj.Id; " +
                "                Integer    CONTAINERS_COUNT = 1; " +
                "                Integer           TAB_COUNT = 1; " +
                "                Integer       SECTION_COUNT = 1; " +
                "                Integer      QUESTION_COUNT = 1; " +
                "                Integer      QUESTION_UPLOAD_COUNT = 1; " +
                "                List<ICIX_V1__Layout__c>                layouts = new List<ICIX_V1__Layout__c>(); " +
                "                List<ICIX_V1__Tab__c>                      tabs = new List<ICIX_V1__Tab__c>(); " +
                "                List<ICIX_V1__Section__c>              sections = new List<ICIX_V1__Section__c>(); " +
                "                List<ICIX_V1__Answer_Option__c>   answerOptions = new List<ICIX_V1__Answer_Option__c>(); " +
                "                layouts = [SELECT Id FROM ICIX_V1__Layout__c WHERE ICIX_V1__Parent_Form__c =: formId ]; " +
                "                if(layouts.size() == 0) { " +
                "                 ICIX_V1__Layout__c tempLayout =  " +
                "                                    new ICIX_V1__Layout__c(Name = 'Layout',  " +
                "                                        ICIX_V1__Parent_Form__c = formId,  " +
                "                                           ICIX_V1__Is_Valid__c = true,  " +
                "                                            ICIX_V1__UI_Type__c = 'desktop'); " +
                "                 layouts.add(tempLayout); " +
                "                    database.insert(layouts); " +
                "                } " +
                "                " +
                "                for(ICIX_V1__Layout__c layout: layouts) { " +
                "                    for(Integer i = 0; i < TAB_COUNT; i++) { " +
                "                        ICIX_V1__Tab__c tempTab =  " +
                "                            new ICIX_V1__Tab__c(Name = 'Tab '+i, " +
                "                                       ICIX_V1__Disabled__c = false, " +
                "                                       ICIX_V1__ParentLayout__c = layout.Id, " +
                "                                       ICIX_V1__Order__c = i);  " +
                "                        tabs.add(tempTab); " +
                "                    } " +
                "                } " +
                "                database.insert(tabs); " +
                "                " +
                "                for(ICIX_V1__Tab__c tab: tabs) { " +
                "                    for(Integer i = 0; i < SECTION_COUNT; i++) { " +
                "                        ICIX_V1__Section__c tempSection =  " +
                "                            new ICIX_V1__Section__c(Name = tab.Name+' Section ' + i, " +
                "                                           ICIX_V1__Order__c = i, " +
                "                                           ICIX_V1__Parent_Tab__c = tab.Id, " +
                "                                           ICIX_V1__Style__c = 'Table', " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        sections.add(tempSection); " +
                "                    }        " +
                "                } " +
                "                database.insert(sections); " +
                "                for(Integer k = 0; k < sections.size(); k++) { " +
                "                    list<ICIX_V1__Question__c> questions = new list<ICIX_V1__Question__c>(); " +
                "                     " +
                "                    for(Integer i = 1; i <= QUESTION_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Question-'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'text', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'Question-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +
                "                  for(Integer i = 1; i <= QUESTION_UPLOAD_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Upload document -'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'upload', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'Upload document-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +

                "                     " +
                "                    insert questions; " +
                "                     " +
                "                    list<ICIX_V1__Element__c> elements = new list<ICIX_V1__Element__c>(); " +
                "                    for(Integer i = 0; i < questions.size(); i++) { " +
                "                        " +
                "                        ICIX_V1__Element__c tempElement =  " +
                "                            new ICIX_V1__Element__c(Name = questions[i].Name +' Element ',  " +
                "                                           ICIX_V1__Columns__c = '6;6;12', " +
                "                                           ICIX_V1__Order__c = i + 1, " +
                "                                           ICIX_V1__LinkedQuestion__c = questions[i].Id, " +
                "                                           ICIX_V1__ParentSection__c = sections[k].Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        elements.add(tempElement);  " +
                "                    } " +
                "                    insert elements; " +
                "                } " +
                "                database.insert(answerOptions);";

        boolean result = false;
        userName = Users.Requester_Admin.getUserId();
        password = Users.Requester_Admin.getPassword();
        try {
            result = executeSalesforceToolingQuery(getSalesforceAccessToken(), query);
            if (result) {
                Log.info("Form created- PASS");
                GlobalTestData.FormBuildersFormNamesToDelete.add(formName);
            } else {
                Log.info("Form created - FAIL");
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }//End method


    /**
     * <h1> This method will create form with mandatory fields </h1>
     *
     * @param formName
     * @return
     */
    public static boolean CreateFormWithRequiredFieldsUsingScript(String formName) {

        String query = "ICIX_V1__Container_Template__c containerTemplateObj = new ICIX_V1__Container_Template__c(); " +
                "                  containerTemplateObj.Name = '" + formName + "'; " +
                "                        containerTemplateObj.ICIX_V1__Type__c = 'Form'; " +
                "                        containerTemplateObj.ICIX_V1__Container_Type__c = 'Single Form'; " +
                "                        containerTemplateObj.ICIX_V1__Version__c = 1; " +
                "                        containerTemplateObj.ICIX_V1__Active__c = true; " +
                "                        containerTemplateObj.ICIX_V1__Status__c = 'Published'; " +
                "                        containerTemplateObj.ICIX_V1__Workflow_Definition_Global_Id__c = 'WD2'; " +
                "                        insert containerTemplateObj;" +
                "                String formId = containerTemplateObj.Id; " +
                "                Integer    CONTAINERS_COUNT = 1; " +
                "                Integer           TAB_COUNT = 1; " +
                "                Integer       SECTION_COUNT = 1; " +
                "                Integer      QUESTION_COUNT = 2; " +
                "                Integer      QUESTION_UPLOAD_COUNT = 1; " +
                "                List<ICIX_V1__Layout__c>                layouts = new List<ICIX_V1__Layout__c>(); " +
                "                List<ICIX_V1__Tab__c>                      tabs = new List<ICIX_V1__Tab__c>(); " +
                "                List<ICIX_V1__Section__c>              sections = new List<ICIX_V1__Section__c>(); " +
                "                List<ICIX_V1__Answer_Option__c>   answerOptions = new List<ICIX_V1__Answer_Option__c>(); " +
                "                layouts = [SELECT Id FROM ICIX_V1__Layout__c WHERE ICIX_V1__Parent_Form__c =: formId ]; " +
                "                if(layouts.size() == 0) { " +
                "                 ICIX_V1__Layout__c tempLayout =  " +
                "                                    new ICIX_V1__Layout__c(Name = 'Layout',  " +
                "                                        ICIX_V1__Parent_Form__c = formId,  " +
                "                                           ICIX_V1__Is_Valid__c = true,  " +
                "                                            ICIX_V1__UI_Type__c = 'desktop'); " +
                "                 layouts.add(tempLayout); " +
                "                    database.insert(layouts); " +
                "                } " +
                "                " +
                "                for(ICIX_V1__Layout__c layout: layouts) { " +
                "                    for(Integer i = 0; i < TAB_COUNT; i++) { " +
                "                        ICIX_V1__Tab__c tempTab =  " +
                "                            new ICIX_V1__Tab__c(Name = 'Tab '+i, " +
                "                                       ICIX_V1__Disabled__c = false, " +
                "                                       ICIX_V1__ParentLayout__c = layout.Id, " +
                "                                       ICIX_V1__Order__c = i);  " +
                "                        tabs.add(tempTab); " +
                "                    } " +
                "                } " +
                "                database.insert(tabs); " +
                "                " +
                "                for(ICIX_V1__Tab__c tab: tabs) { " +
                "                    for(Integer i = 0; i < SECTION_COUNT; i++) { " +
                "                        ICIX_V1__Section__c tempSection =  " +
                "                            new ICIX_V1__Section__c(Name = tab.Name+' Section ' + i, " +
                "                                           ICIX_V1__Order__c = i, " +
                "                                           ICIX_V1__Parent_Tab__c = tab.Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        sections.add(tempSection); " +
                "                    }        " +
                "                } " +
                "                database.insert(sections); " +
                "                for(Integer k = 0; k < sections.size(); k++) { " +
                "                    list<ICIX_V1__Question__c> questions = new list<ICIX_V1__Question__c>(); " +
                "                     " +
                "                    for(Integer i = 1; i <= QUESTION_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Question-'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'text', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Response_Required__c = true, " +
                "                                            ICIX_V1__Question_Text__c = 'Question-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +
                "                  for(Integer i = 1; i <= QUESTION_UPLOAD_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Upload document -'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'upload', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Response_Required__c = true, " +
                "                                            ICIX_V1__Question_Text__c = 'Upload document-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +

                "                     " +
                "                    insert questions; " +
                "                     " +
                "                    list<ICIX_V1__Element__c> elements = new list<ICIX_V1__Element__c>(); " +
                "                    for(Integer i = 0; i < questions.size(); i++) { " +
                "                        " +
                "                        ICIX_V1__Element__c tempElement =  " +
                "                            new ICIX_V1__Element__c(Name = questions[i].Name +' Element ',  " +
                "                                           ICIX_V1__Columns__c = '6;6;12', " +
                "                                           ICIX_V1__Order__c = i + 1, " +
                "                                           ICIX_V1__LinkedQuestion__c = questions[i].Id, " +
                "                                           ICIX_V1__ParentSection__c = sections[k].Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        elements.add(tempElement);  " +
                "                    } " +
                "                    insert elements; " +
                "                } " +
                "                database.insert(answerOptions);";

        boolean result = false;
        userName = GlobalTestData.Requester_Admin.getUserId();
        password = GlobalTestData.Requester_Admin.getPassword();
        try {
            result = executeSalesforceToolingQuery(getSalesforceAccessToken(), query);
            if (result) {
                Log.info("Form created- PASS");
                GlobalTestData.FormBuildersFormNamesToDelete.add(formName);
            } else {
                Log.info("Form created - FAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }//End method

    public static void ExecuteBatch_ProductTestCertificateGeneration() {
        Log.info("Executing batch for COC generation");
        boolean result = false;
        userName = GlobalTestData.Requester_Admin.getUserId();
        password = GlobalTestData.Requester_Admin.getPassword();
        result = ProductTestCertificateGenerationBatch(getSalesforceAccessToken());
        if (result) {
            Log.info("Batch executed - PASS");
        } else {
            Log.info("Batch executed - FAIL");
        }
    }//End function

    private static boolean ProductTestCertificateGenerationBatch(String accessToken) {
        try {
            //String query = "Database.executeBatch(new ICIX_v1.ProductTestCertificateGenerationBatch());";
            // New batch for package v2.116
            String query = "Database.executeBatch(new ICIX_V1.ProductTestCertificateGenerationBatch(''));";
            boolean result = executeSalesforceToolingQuery(accessToken, query);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * <h1>GetRequestsCountUsingAPI</h1>
     * <p>This method will return all the request count that contains a given text</p>
     */
    public static int GetRequestsCountUsingAPI(String accessToken, String strRequestNameContains) {
        int requestRecords = -1;
        try {
            long startTime = System.currentTimeMillis();
            String query = "SELECT Count() FROM ICIX_V1__Request__c WHERE Name Like '%" + strRequestNameContains + "%'";
            String oauthLoginResponse = callSalesforceRestApi(accessToken, query);
            Gson gson = new Gson();
            RequestsDetails requestsDetails = gson.fromJson(oauthLoginResponse, RequestsDetails.class);
            Log.info("***Execution finished with result: " + requestsDetails.done);
            requestRecords = requestsDetails.totalSize;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.error(ex.getMessage());
        }
        return requestRecords;
    }


    //This inner class is used to get Records count of Request using rest api
    private class RequestsDetails {
        int totalSize;
        boolean done;

        public int getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(int totalSize) {
            this.totalSize = totalSize;
        }

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }
    }

    public static boolean CreateFormUsingScriptWithDateField(String formName) {
        String query = "ICIX_V1__Container_Template__c containerTemplateObj = new ICIX_V1__Container_Template__c(); " +
                "                  containerTemplateObj.Name = '" + formName + "'; " +
                "                        containerTemplateObj.ICIX_V1__Type__c = 'Form'; " +
                "                        containerTemplateObj.ICIX_V1__Container_Type__c = 'Single Form'; " +
                "                        containerTemplateObj.ICIX_V1__Version__c = 1; " +
                "                        containerTemplateObj.ICIX_V1__Active__c = true; " +
                "                        containerTemplateObj.ICIX_V1__Status__c = 'Published'; " +
                "                        containerTemplateObj.ICIX_V1__Workflow_Definition_Global_Id__c = 'WD2'; " +
                "                        insert containerTemplateObj;" +
                "                String formId = containerTemplateObj.Id; " +
                "                Integer    CONTAINERS_COUNT = 1; " +
                "                Integer           TAB_COUNT = 1; " +
                "                Integer       SECTION_COUNT = 1; " +
                "                Integer      QUESTION_COUNT = 1; " +
                "                Integer      QUESTION_UPLOAD_COUNT = 1; " +
                "                List<ICIX_V1__Layout__c>                layouts = new List<ICIX_V1__Layout__c>(); " +
                "                List<ICIX_V1__Tab__c>                      tabs = new List<ICIX_V1__Tab__c>(); " +
                "                List<ICIX_V1__Section__c>              sections = new List<ICIX_V1__Section__c>(); " +
                "                List<ICIX_V1__Answer_Option__c>   answerOptions = new List<ICIX_V1__Answer_Option__c>(); " +
                "                layouts = [SELECT Id FROM ICIX_V1__Layout__c WHERE ICIX_V1__Parent_Form__c =: formId ]; " +
                "                if(layouts.size() == 0) { " +
                "                 ICIX_V1__Layout__c tempLayout =  " +
                "                                    new ICIX_V1__Layout__c(Name = 'Layout',  " +
                "                                        ICIX_V1__Parent_Form__c = formId,  " +
                "                                           ICIX_V1__Is_Valid__c = true,  " +
                "                                            ICIX_V1__UI_Type__c = 'desktop'); " +
                "                 layouts.add(tempLayout); " +
                "                    database.insert(layouts); " +
                "                } " +
                "                " +
                "                for(ICIX_V1__Layout__c layout: layouts) { " +
                "                    for(Integer i = 1; i < TAB_COUNT + 1; i++) { " +
                "                        ICIX_V1__Tab__c tempTab =  " +
                "                            new ICIX_V1__Tab__c(Name = 'Tab '+i, " +
                "                                       ICIX_V1__Disabled__c = false, " +
                "                                       ICIX_V1__ParentLayout__c = layout.Id, " +
                "                                       ICIX_V1__Order__c = i);  " +
                "                        tabs.add(tempTab); " +
                "                    } " +
                "                } " +
                "                database.insert(tabs); " +
                "                " +
                "                for(ICIX_V1__Tab__c tab: tabs) { " +
                "                    for(Integer i = 1; i < SECTION_COUNT + 1; i++) { " +
                "                        ICIX_V1__Section__c tempSection =  " +
                "                            new ICIX_V1__Section__c(Name = tab.Name+' Section ' + i, " +
                "                                           ICIX_V1__Order__c = i, " +
                "                                           ICIX_V1__Parent_Tab__c = tab.Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        sections.add(tempSection); " +
                "                    }        " +
                "                } " +
                "                database.insert(sections); " +
                "                for(Integer k = 0; k < sections.size(); k++) { " +
                "                    list<ICIX_V1__Question__c> questions = new list<ICIX_V1__Question__c>(); " +
                "                     " +
                "                    for(Integer i = 0; i <= QUESTION_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Question-'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'text', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'Question-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +
                "                  for(Integer i = 0; i <= QUESTION_UPLOAD_COUNT; i++) { " +
                "                        ICIX_V1__Question__c question =  " +
                "                            new ICIX_V1__Question__c(Name = 'Upload document -'+i+' text ',  " +
                "                                            ICIX_V1__Answer_Type__c = 'upload', " +
                "                                            ICIX_V1__Form__c = formId, " +
                "                                            ICIX_V1__Question_Text__c = 'Upload document-'+i+'?'); " +
                "                        questions.add(question);             " +
                "                    } " +

                "                for(Integer i = 1; i <= QUESTION_UPLOAD_COUNT; i++) { " +
                "                   ICIX_V1__Question__c question =  " +
                "                        new ICIX_V1__Question__c(Name = 'date-'+i+' text ',  " +
                "                                        ICIX_V1__Answer_Type__c = 'date', " +
                "                                        ICIX_V1__Form__c = formId, " +
                "                                        ICIX_V1__Question_Text__c = 'date-'+i+'?'); " +
                "                    questions.add(question);             " +
                "                } " +
                "                    insert questions; " +
                "                     " +
                "                    list<ICIX_V1__Element__c> elements = new list<ICIX_V1__Element__c>(); " +
                "                    for(Integer i = 0; i < questions.size(); i++) { " +
                "                        " +
                "                        ICIX_V1__Element__c tempElement =  " +
                "                            new ICIX_V1__Element__c(Name = questions[i].Name +' Element ',  " +
                "                                           ICIX_V1__Columns__c = '6;6;12', " +
                "                                           ICIX_V1__Order__c = i + 1, " +
                "                                           ICIX_V1__LinkedQuestion__c = questions[i].Id, " +
                "                                           ICIX_V1__ParentSection__c = sections[k].Id, " +
                "                                           ICIX_V1__Visible__c = true); " +
                "                        elements.add(tempElement);  " +
                "                    } " +
                "                    insert elements; " +
                "                } " +
                "                database.insert(answerOptions);";

        boolean result = false;
        userName = GlobalTestData.Requester_Admin.getUserId();
        password = GlobalTestData.Requester_Admin.getPassword();
        try {
            String accessToken = getSalesforceAccessToken();
            result = executeSalesforceToolingQuery(accessToken, query);
            if (result) {
                Log.info("Form created- PASS");
                GlobalTestData.FormBuildersFormNamesToDelete.add(formName);
            } else {
                Log.info("Form created - FAIL");
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    public static List<Map> executeQuery(String query) {
        ForceApi forceApi = getForceApi(userName, password);
        if (forceApi == null)
            return Collections.emptyList();
        Selenide.sleep(8000);
        if (!ORG_NAMESPACE.equalsIgnoreCase("ICIX_V1")) {
            Selenide.sleep(8000);
            query = query.replaceAll("ICIX_V1__", ORG_NAMESPACE + "__");
        }

        Log.info("***Executing Salesforce REST query: " +query);
        Selenide.sleep(8000);
        return forceApi.query(query).getRecords();

    }

    public static String callSalesforceRestApi(String accessToken, String query) {
        String restResponse = "";
        try {
            //Encode query
            query = URLEncoder.encode(query, "UTF-8");
            if (!ORG_NAMESPACE.equalsIgnoreCase("ICIX_V1")) {
                query = query.replaceAll("ICIX_V1__", ORG_NAMESPACE + "__");
            }
            long startTime = System.currentTimeMillis();
            Log.info("***Execute Salesforce REST API query****");
            String endPoint = CurrentInstanceUrl + "/services/data/" + salesforceRestApiVersion + "/query?q=" + query;
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(endPoint);
            get.addHeader("Authorization", "Bearer " + accessToken);
            HttpResponse response = httpClient.execute(get);
            long endTime = System.currentTimeMillis();
            int code = response.getStatusLine().getStatusCode();
            Log.info("REST API status: " + code);
            if (code == 200) {
                Log.info("Http Request status is success");
                restResponse = EntityUtils.toString(response.getEntity());
                Log.info("Total time taken(sec): " + ((endTime - startTime) / 1000));
            } else {
                Log.error("API status for Getting namespace is not valid : " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception ex) {
            Log.error("---------------Problem in call salesforce rest api------------------------");
            Log.error(ex.getMessage());
        }
        return restResponse;
    }

    public static String GetProductTestForm_Latest_WithAPI() {

        userName = GlobalTestData.Requester_Admin.getUserId();
        password = GlobalTestData.Requester_Admin.getPassword();
        String query = "SELECT Name FROM ICIX_V1__Container_Template__c WHERE Name Like '%Product Test Form%' ORDER BY CreatedDate DESC LIMIT 1";
        List<Map> records = executeQuery(query);
        if (!records.isEmpty())
            return records.get(0).get("Name").toString();
        else
            return "null";
    }

    public static String GetCorrectiveActionForm_Latest_WithAPI() {
        userName = GlobalTestData.Requester_Admin.getUserId();
        password = GlobalTestData.Requester_Admin.getPassword();
        String query = "SELECT Name FROM ICIX_V1__Container_Template__c WHERE Name Like '%Product Test Corrective Action Form%' ORDER BY CreatedDate DESC LIMIT 1";
        List<Map> records = executeQuery(query);
        if (!records.isEmpty())
            return records.get(0).get("Name").toString();
        else
            return "null";
    }


    public static List<String> getTestingProgrammesNames_ContainsText(String testingProgramName) {
        List<String> testingPrograms = new ArrayList<>();
        String query = "SELECT Name FROM ICIX_V1__Testing_Program__c Where Name Like '%" + testingProgramName + "%'";
        try {
            userName = GlobalTestData.Requester_Admin.getUserId();
            password = GlobalTestData.Requester_Admin.getPassword();
            String accessToken = getSalesforceAccessToken();
            String strResponse = callSalesforceRestApi(accessToken, query);
            JsonArray jsonArray = getRecordsFromResponse(strResponse);
            if (jsonArray.size() > 0) {
                Iterator<JsonElement> it = jsonArray.iterator();
                while (it.hasNext()) {
                    testingPrograms.add(
                            it.next().getAsJsonObject().get("Name").getAsString().replaceAll("\"", "")
                    );
                }
            }
            return testingPrograms;
        } catch (Exception ex) {
            Log.info("There was a problem in calling salesforce rest api using query :" + query);

        }
        return Collections.emptyList();
    }


    private class ProductTestForm extends RequestsDetails {

        List<Records> records = new ArrayList<>();

        public List<Records> getRecords() {
            return records;
        }

        public void setRecords(List<Records> records) {
            this.records = records;
        }

        public class Records {
            String Name;

            public String getName() {
                return Name;
            }

            public void setName(String name) {
                Name = name;
            }
        }
    }

    private class Namespace extends RequestsDetails {
        List<Records> records = new ArrayList<>();

        public List<Records> getRecords() {
            return records;
        }

        public void setRecords(List<Records> records) {
            this.records = records;
        }

        public class Records {
            String NamespacePrefix;

            public String getNamespacePrefix() {
                return NamespacePrefix;
            }

            public void setNamespacePrefix(String name) {
                NamespacePrefix = name;
            }
        }
    }

    public static void cleanDataForOrg_AutomationFormsByOlderDays(USER user, int subtractDays) {
        String date = getSubtractcByDaysDateForDataDeletion(subtractDays);

        String query = String.format("Datetime DD = Datetime.valueOf('%s 00:00:00');", date) +
                "DELETE [SELECT Id FROM ICIX_V1__Container_Answer__c WHERE CreatedDate <=: DD LIMIT 10000];" +
                "List<ICIX_V1__Container_Template__c> containerTemplateList = new List<ICIX_V1__Container_Template__c>();" +
                "containerTemplateList = [select id,name from ICIX_V1__Container_Template__c where CreatedDate <=: DD Limit 10];" +
                "Set<String> containerTemplateIdSet = new Set<String>();" +
                "for(ICIX_V1__Container_Template__c contianerTemplate : containerTemplateList){" +
                " containerTemplateIdSet.add(contianerTemplate.id);" +
                "}" +
                "if(containerTemplateIdSet.size() > 0){" +
                " delete [Select id FROM ICIX_V1__Container_Answer__c where ICIX_V1__Container_Instance__r.ICIX_V1__Container_Template__c in: containerTemplateIdSet];" +
                " delete [SELECT Id FROM ICIX_V1__Container__c Where ICIX_V1__Container_Template__c in: containerTemplateIdSet];" +
                " delete [Select id FROM ICIX_V1__Question__c where ICIX_V1__Form__c in :containerTemplateIdSet];" +
                " delete [Select id FROM ICIX_V1__Element__c where ICIX_V1__ParentSection__r.ICIX_V1__Parent_Tab__r.ICIX_V1__ParentLayout__r.ICIX_V1__Parent_Form__c in : containerTemplateIdSet];" +
                " delete [Select id FROM ICIX_V1__Section__c where ICIX_V1__Parent_Tab__r.ICIX_V1__ParentLayout__r.ICIX_V1__Parent_Form__c in : containerTemplateIdSet];" +
                " delete [Select id FROM ICIX_V1__Tab__c where ICIX_V1__ParentLayout__r.ICIX_V1__Parent_Form__c in : containerTemplateIdSet];" +
                " delete [SELECT Id FROM ICIX_V1__Layout__c Where ICIX_V1__Parent_Form__c in: containerTemplateIdSet];" +
                " delete [Select id from ICIX_V1__Recipient__c where ICIX_V1__Request__r.ICIX_V1__Container_Template__c IN :containerTemplateIdSet];" +
                " delete [Select id from ICIX_V1__Workflow_Step_Instance__c where ICIX_V1__Workflow_Instance__r.ICIX_V1__Request__r.ICIX_V1__Container_Template__c IN :containerTemplateIdSet];" +
                " delete [Select id from ICIX_V1__Requirement_Instance__c where ICIX_V1__Workflow__r.ICIX_V1__Request__r.ICIX_V1__Container_Template__c IN :containerTemplateIdSet];" +
                " delete [Select id from ICIX_V1__Workflow_Instance__c where ICIX_V1__Request__r.ICIX_V1__Container_Template__c IN :containerTemplateIdSet];" +
                " delete [Select id from ICIX_V1__Request__c where ICIX_V1__Container_Template__c IN :containerTemplateIdSet];" +
                " delete containerTemplateList;" + "}";

        switch (user) {
            case REQUESTER:
                userName = GlobalTestData.Requester_Admin.getUserId();
                password = GlobalTestData.Requester_Admin.getPassword();

                break;
            case RESPONDER:
                userName = GlobalTestData.Responder_Admin.getUserId();
                password = GlobalTestData.Responder_Admin.getPassword();

                break;
            case LAB:
                userName = GlobalTestData.Lab_Admin.getUserId();
                password = GlobalTestData.Lab_Admin.getPassword();

                break;
        }//End switch

        String token = getSalesforceAccessToken();
        executeSalesforceToolingQuery(token, query);
    }

    public static void cleanDataForOrg_AutomationRequestsByOlderDays(USER user, int subtractDays) {
        String date = getSubtractcByDaysDateForDataDeletion(subtractDays);
        String query = "  try{    " +
                String.format("Datetime DD = Datetime.valueOf('%s 00:00:00');", date) +
                "   List<ICIX_V1__workflow_instance__c> listWorkflowInstance = [select id, ICIX_V1__Request__c,ICIX_V1__Container__c from ICIX_V1__workflow_instance__c Where CreatedDate <=: DD Limit 50]; " +
                "   List<String> workflowInstanceIds = new List<String>();  " +
                "   List<String> requestIds = new List<String>();  " +
                "   List<String> containerInstanceIds = new List<String>(); " +
                "   for(ICIX_V1__workflow_instance__c objWorkflowInstance : listWorkflowInstance) " +
                "   {      " +
                "       workflowInstanceIds.add(objWorkflowInstance.Id); " +
                "       requestIds.add(objWorkflowInstance.ICIX_V1__Request__c); " +
                "       containerInstanceIds.add(objWorkflowInstance.ICIX_V1__Container__c); " +
                "   } " +
                "   System.debug('I am here 13 workflowInstanceIds ' + workflowInstanceIds.size()); " +
                "   List<ICIX_V1__workflow_step_instance__c> listWorkflowStepInstance =[select id from ICIX_V1__workflow_step_instance__c Where ICIX_V1__Workflow_Instance__c in:workflowInstanceIds]; " +
                "   List<ICIX_V1__Recipient__c> listRecipients =[select id, Name from ICIX_V1__Recipient__c Where ICIX_V1__Workflow_Instance__c in:workflowInstanceIds]; " +
                "   List<String> workflowStepInstanceIds = new List<String>(); " +
                "   List<String> RecipientIds = new List<String>();   " +
                "   for(ICIX_V1__Recipient__c objRecipient:listRecipients) " +
                "   { " +
                "       RecipientIds.add(objRecipient.Id); " +
                "   } " +
                "   for(ICIX_V1__workflow_step_instance__c objWorkflowStepInstance : listWorkflowStepInstance) " +
                "   {      " +
                "       workflowStepInstanceIds.add(objWorkflowStepInstance.Id);     " +
                "   } " +
                "   System.debug('I am here 15 workflowStepInstanceIds '  +workflowStepInstanceIds.size()); " +
                "    " +
                "   List<ICIX_V1__Recipient__c> listRecipientDelete = new List<ICIX_V1__Recipient__c>(); " +
                "   listRecipientDelete = [select id from ICIX_V1__Recipient__c Where Id in: RecipientIds]; " +
                "   data.remove(listRecipientDelete); " +
                "   List<ICIX_V1__Requirement_Instance__c> listRequirementInstance = new List<ICIX_V1__Requirement_Instance__c>(); " +
                "   listRequirementInstance =[SELECT Id FROM ICIX_V1__Requirement_Instance__c Where ICIX_V1__Workflow__c in: workflowInstanceIds]; " +
                "   data.remove(listRequirementInstance); " +
                "   List<ICIX_V1__workflow_step_instance__c> listWSIDelete = new List<ICIX_V1__workflow_step_instance__c>(); " +
                "   listWSIDelete = [select id from ICIX_V1__workflow_step_instance__c Where Id in: workflowStepInstanceIds]; " +
                "   data.remove(listWSIDelete); " +
                "   List<ICIX_V1__workflow_instance__c> listWSDelete = new List<ICIX_V1__workflow_instance__c>(); " +
                "   listWSDelete = [select id from ICIX_V1__workflow_instance__c Where Id in: workflowInstanceIds]; " +
                "   data.remove(listWSDelete); " +
                "   List<ICIX_V1__Request__c> listRequestDelete = new List<ICIX_V1__Request__c>(); " +
                "   listRequestDelete = [Select id from ICIX_V1__Request__c Where Id in:requestIds]; " +
                "   data.remove(listRequestDelete);   " +
                "   List<ICIX_V1__Container__c> listContainerInstanceToDelete= new List<ICIX_V1__Container__c>(); " +
                "   listContainerInstanceToDelete = [select Id from ICIX_V1__Container__c Where Id in: containerInstanceIds]; " +
                "   data.remove(listContainerInstanceToDelete);  " +
                "      system.debug(listContainerInstanceToDelete);" +
                "   String strMessage = 'sucess.! selected forms are deleted'; " +
                "      system.debug('Deleted :' +listWorkflowInstance.size());" +
                "   system.debug('$$$$$ strMessage ='+strMessage); " +
                "          } " +
                "          catch (Exception ex) " +
                "          {  " +
                "   system.debug('$$$$ Error :'+ex.getMessage()+' $$$ stack trace::'+ex.getStackTraceString()); " +
                "            }";

        switch (user) {
            case REQUESTER:
                userName = GlobalTestData.Requester_Admin.getUserId();
                password = GlobalTestData.Requester_Admin.getPassword();

                break;
            case RESPONDER:
                userName = GlobalTestData.Responder_Admin.getUserId();
                password = GlobalTestData.Responder_Admin.getPassword();

                break;
            case LAB:
                userName = GlobalTestData.Lab_Admin.getUserId();
                password = GlobalTestData.Lab_Admin.getPassword();

                break;
        }//End switch

        String token = getSalesforceAccessToken();
        executeSalesforceToolingQuery(token, query);
    }


    public static void cleanDataForOrg_ICIXProductsByOlderDays(USER user, int subtractDays) {
        String date = getSubtractcByDaysDateForDataDeletion(subtractDays);
        String query = String.format("Datetime DD = Datetime.valueOf('%s 00:00:00');", date) +
                "integer lmt = 10;" +
                "delete [ select Id from ICIX_V1__UP_Relationship_Permission__c WHERE CreatedDate <=: DD limit : lmt];" +
                "delete [ select Id from ICIX_V1__Product_Non_Universal_Id__c  WHERE CreatedDate <=: DD limit : lmt];" +
                "delete [ select Id from ICIX_V1__Product_Universal_Id__c  WHERE CreatedDate <=: DD limit : lmt];" +
                "delete [ select id from ICIX_V1__PP_Relationship__c  WHERE CreatedDate <=: DD limit : lmt];" +
                "delete [ select id from ICIX_V1__UP_Relationship_Attribute__c  WHERE CreatedDate <=: DD limit : lmt];" +
                "delete [ select id from ICIX_V1__PP_Relationship_Attribute__c  WHERE CreatedDate <=: DD limit : lmt];" +
                "delete [ select Id from ICIX_V1__UP_Relationship__c  WHERE CreatedDate <=: DD limit : lmt];" +
                "" +
                "delete [Select Id from ICIX_V1__Testing_Program_Product_Category__c WHERE CreatedDate <=: DD limit : lmt];" +
                "delete [Select Id from ICIX_V1__Testing_Program_Product__c WHERE CreatedDate <=: DD limit : lmt];" +
                "list<ICIX_Product__c> DeleteList = [select Name from ICIX_V1__ICIX_Product__c  WHERE CreatedDate <=: DD limit : lmt];" +
                "delete DeleteList;";

        switch (user) {
            case REQUESTER:
                userName = GlobalTestData.Requester_Admin.getUserId();
                password = GlobalTestData.Requester_Admin.getPassword();

                break;
            case RESPONDER:
                userName = GlobalTestData.Responder_Admin.getUserId();
                password = GlobalTestData.Responder_Admin.getPassword();

                break;
            case LAB:
                userName = GlobalTestData.Lab_Admin.getUserId();
                password = GlobalTestData.Lab_Admin.getPassword();

                break;
        }//End switch

        String token = getSalesforceAccessToken();
        executeSalesforceToolingQuery(token, query);
    }

    public static void cleanDataForOrg_ICIXLogsByOlderDays(USER user, int subtractDays) {
        String date = getSubtractcByDaysDateForDataDeletion(subtractDays);
        String query = String.format("Datetime DD = Datetime.valueOf('%s 00:00:00');", date) +
                "DELETE [SELECT Id FROM ICIX_V1__Log__c WHERE CreatedDate <=: DD LIMIT 10000 ];";

        switch (user) {
            case REQUESTER:
                userName = GlobalTestData.Requester_Admin.getUserId();
                password = GlobalTestData.Requester_Admin.getPassword();

                break;
            case RESPONDER:
                userName = GlobalTestData.Responder_Admin.getUserId();
                password = GlobalTestData.Responder_Admin.getPassword();

                break;
            case LAB:
                userName = GlobalTestData.Lab_Admin.getUserId();
                password = GlobalTestData.Lab_Admin.getPassword();

                break;
        }//End switch

        String token = getSalesforceAccessToken();
        executeSalesforceToolingQuery(token, query);
    }

    public static String getSubtractcByDaysDateForDataDeletion(int dy) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -dy);
        int mth = (cal.get(Calendar.MONTH) + 1);
        String month;
        if (mth < 10) {
            month = "0" + mth;
        } else month = "" + mth;

        int d = (cal.get(Calendar.DATE));
        String day;
        if (d < 10) {
            day = "0" + d;
        } else day = "" + d;
        return cal.get(Calendar.YEAR) + "-" + month + "-" + day;
    }

    //New in Batch 08/03/2019. This code is not yet merged into other branches
    public static String getAccountId(USER user, String accountName) {
        switch (user) {
            case REQUESTER:
                userName = GlobalTestData.Requester_Admin.getUserId();
                password = GlobalTestData.Requester_Admin.getPassword();

                break;
            case RESPONDER:
                userName = GlobalTestData.Responder_Admin.getUserId();
                password = GlobalTestData.Responder_Admin.getPassword();

                break;
            case LAB:
                userName = GlobalTestData.Lab_Admin.getUserId();
                password = GlobalTestData.Lab_Admin.getPassword();

                break;
        }//End switch

        String token = getSalesforceAccessToken();
        String query = "SELECT Id FROM Account Where Name=%" + accountName + "%";
        String strResponse = callSalesforceRestApi(token, query);
        JsonParser jp = new JsonParser();
        JsonObject jsonObject =
                jp.parse(strResponse).getAsJsonObject();

        return jsonObject.get("records")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("Id")
                .toString()
                .replaceAll("\"", "");


    }

    public static String getProductId(USER user, String productName) {
        switch (user) {
            case REQUESTER:
                userName = GlobalTestData.Requester_Admin.getUserId();
                password = GlobalTestData.Requester_Admin.getPassword();

                break;
            case RESPONDER:
                userName = GlobalTestData.Responder_Admin.getUserId();
                password = GlobalTestData.Responder_Admin.getPassword();

                break;
            case LAB:
                userName = GlobalTestData.Lab_Admin.getUserId();
                password = GlobalTestData.Lab_Admin.getPassword();

                break;
        }//End switch

        String token = getSalesforceAccessToken();
        String productClass = "ICIX_V1__ICIX_Product__c";
        if (!ORG_NAMESPACE.equalsIgnoreCase("ICIX_V1"))
            productClass = productClass.replaceAll("ICIX_V1__", ORG_NAMESPACE + "__");

        String query = "SELECT Id FROM " + productClass + " Where Name='" + productName + "'";
        String strResponse = callSalesforceRestApi(token, query);
        JsonParser jp = new JsonParser();
        JsonObject jsonObject =
                jp.parse(strResponse).getAsJsonObject();

        return jsonObject.get("records")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("Id")
                .toString()
                .replaceAll("\"", "");
    }

    public static JsonArray getRecordsFromResponse(String response) {

        JsonArray jsonArray = new JsonArray();
        try {
            JsonParser jsonParser = new JsonParser();
            if (jsonParser.parse(response).isJsonObject()) {
                JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
                Boolean done = Boolean.parseBoolean(jsonObject.get("done").getAsString().replaceAll("\"", ""));
                if (done) {
                    jsonArray = jsonObject.get("records")
                            .getAsJsonArray();
                } else {
                    Log.error("Response does not have valid results");
                }

            } else {
                Log.error("Response is not a valid json :" + response);
            }
        } catch (Exception ex) {
            Log.error(ex.getMessage());
        }
        return jsonArray;
    }

    private static void DeleteAttribute(String accessToken, List<String> AttributeToDelete) {

        if (!AttributeToDelete.isEmpty()) {
            try {
                Log.info("Total number of requests to be deleted #" + AttributeToDelete.size());
                Log.info("Attribute: " + AttributeToDelete);

                String query = "  try{   " +
                        "        delete [SELECT Id, Name, List__c, Picklist_Selection__c FROM" +
                        "        Attribute__c where Name in(" + convertList(AttributeToDelete) + ")];" +
                        "        }" +
                        "        catch (Exception ex)" +
                        "        { " +
                        "           system.debug('$$$$ Error :'+ex.getMessage()+' $$$ stack trace::'+ex.getStackTraceString());" +
                        "          }";

                boolean result = executeSalesforceToolingQuery(accessToken, query);
                if (result) {
                    Log.info("Forms related to Requests List deleted - PASS");
                } else {
                    Log.info("Forms related to Requests List deleted - FAIL");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }//end if

    }//End method
}//End class
