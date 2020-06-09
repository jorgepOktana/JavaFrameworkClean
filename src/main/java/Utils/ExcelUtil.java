package Utils;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import icix.Start.Start;
import icix.TestData.Users;
import icix.TestData.UsersTestData;

import java.io.File;
import java.util.*;

import static icix.TestData.GlobalTestData.testCoverageMap;

/**
 * @author smartData
 * <h1>Excel Utils</h1>
 * <p>Purpose: This class is used to perform get test data from excel sheet</p>
 * It is used to get the test data which is required to execute test case form excel sheet
 */

public class ExcelUtil {
    static Fillo fillo = new Fillo();

    /**
     * <h1>Get Data form Excel Sheet <h1/>
     * <p>Purpose: This method is used to get test date for execute test cases form excel file after passing
     * module name as sheet name and testId name as testcaseid as parameter
     * </p>
     *
     * @param module,testId
     * @throws Exception
     */
    public synchronized static Recordset GetTestData(String module, String testId) throws Exception {
        try {
            String FS = File.separator;
            String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "TestData" + FS + "TestData.xls";
            File f = new File(testDataFilePath);
            Connection connection = fillo.getConnection(f.getAbsolutePath());
            String strQuery = String.format("Select * from %s Where TestID='%s'", module, testId);
            Recordset recordset = connection.executeQuery(strQuery);
            connection.close();
            return recordset;
        } catch (FilloException e) {
            Log.info("Error in test data reading");
            //Throw the exception To fail the test
            e.printStackTrace();
            throw new Exception(String.format("Test data not found for Module: '%s' and TestId: '%s'", module, testId));
        }
    }//End function

    /**
     * <h1>Get Data form Excel Sheet <h1/>
     * <p>Purpose: This method is used to get test date for execute test cases form excel file after passing
     * module name as sheet name and testId name as testcaseid as parameter
     * </p>
     *
     * @param testId
     * @throws Exception
     */

    /**
     * <h1>Get Browser Data form Excel Sheet <h1/>
     * <p>Purpose: This method is used to get test data.
     * </p>
     *
     * @throws Exception
     */
    public synchronized static void GetBrowser() throws Exception {
        try {
            String FS = File.separator;
            String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "TestData" + FS + "UserData.xls";
            File f = new File(testDataFilePath);
            Connection connection = fillo.getConnection(f.getAbsolutePath());

            String strQuery = String.format("Select * from Browser");
            Recordset recordset = connection.executeQuery(strQuery);
            while (recordset.next()) {
                Start.currentBrowser = recordset.getField("Browser");
            }
            recordset.close();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.info("Error in user test data reading");
            throw new Exception(String.format("Browser Test data not found ->" + ex.getMessage()));
        }
    }

    /**
     * <h1>Get Login Credentials <h1/>
     * <p>Purpose: This method is used to get get username, password, TP Name and required data to run scripts
     * form Excel sheet (UserData.xlsx)
     * <p>
     * </p>
     *
     * @param
     * @throws Exception
     */

    public synchronized static void GetUserData() throws Exception {
        try {
            String FS = File.separator;
            String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "TestData" + FS + "UserData.xls";
            File f = new File(testDataFilePath);
            Connection connection = fillo.getConnection(f.getAbsolutePath());
            String strQuery = String.format("Select * from Admin_Users");
            Recordset recordset = connection.executeQuery(strQuery);

            while (recordset.next()) {
                UsersTestData usersTestData = new UsersTestData();
                //Get data from Excel and Set for Requester_Admin object
                usersTestData.setUserId(recordset.getField("UserName"));
                usersTestData.setPassword(recordset.getField("Password"));
                usersTestData.setName(recordset.getField("Name"));
                usersTestData.setIcixId(recordset.getField("IcixID"));
                usersTestData.setTPRelationshipTag(recordset.getField("TPRelationshipTag"));
                usersTestData.setTPRelationshipStatus(recordset.getField("TPRelationshipStatus"));
                usersTestData.setTPRelationshipType(recordset.getField("TPRelationshipType"));
                usersTestData.setUrl(recordset.getField("Url"));

                Users.adminUsersTestDataMap.put(recordset.getField("UserType"), usersTestData);
            }
            recordset.close();
            strQuery = String.format("Select * from Users");
            recordset = connection.executeQuery(strQuery);

            while (recordset.next()) {
                UsersTestData usersTestData = new UsersTestData();
                usersTestData.setUserId(recordset.getField("UserName"));
                usersTestData.setPassword(recordset.getField("Password"));

                Users.spuUsersTestDataMap.put(recordset.getField("UserType"), usersTestData);
            }
            recordset.close();
            connection.close();
        } catch (FilloException e) {
            Log.info("Error in user test data reading");
            throw new Exception(String.format("User Test data not found ->" + e.getMessage()));
        }
    }//End of function

    /**
     * <h1>Get Test Suite</h1>
     * <p>Purpose: This method will read the TestSelection excel file and return the enabled modules and tests.
     * </p>
     *
     * @return List<TestConfig>
     * @throws Exception
     */
    public static List<TestConfig> GetTestSuite() throws Exception {
        //Create A TestConfig object to Hold the list that will be returned
        List<TestConfig> lstTestConfig = new ArrayList<>();

        //Map for holding enabled modules
        List<Map<String, String>> lstModules = new ArrayList<>();
        try {
            String suiteName = Common.GetSuiteName();

            if(suiteName.equalsIgnoreCase("Sanity")){
                Log.info("***************Selected Default Sanity suite for execution**************");
            }else {
                if(suiteName.equalsIgnoreCase("Full"))
                    Log.info(String.format("***************Selected %s regression suite for execution**************",suiteName));

                if(suiteName.equalsIgnoreCase("High"))
                    Log.info(String.format("***************Selected %s highest suite for execution**************",suiteName));
            }

            String FS = File.separator;
            String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "SuiteControl" + FS + suiteName + "_TestSelection.xlsx";
            File f = new File(testDataFilePath);
            Connection connection = fillo.getConnection(f.getAbsolutePath());

            //Get all enabled moduled from Sheet SuiteControl
            String strQuery = String.format("Select * from SuiteControl Where RunMode='Yes'");
            Recordset recordset;
            try {
                recordset = connection.executeQuery(strQuery);
                while (recordset.next()) {
                    Map<String, String> module = new HashMap<>();
                    module.put("Module", recordset.getField("Module"));
                    module.put("Parallel", recordset.getField("Parallel"));
                    module.put("ParallelCount", recordset.getField("ParallelCount"));
                    lstModules.add(module);
                }
//            System.out.println("=============================");
//            System.out.println("Total Enabled modules: " +lstModules.size());
//            System.out.println("=============================");
                recordset.close();

            } catch (Exception ex) {
                System.err.println("There is No module enabled to run.");
            }


            //Find enabled test from the Modules
            for (Map<String, String> module : lstModules) {
                //Hold Single Test Configuration
                TestConfig testConfig = new TestConfig();
                testConfig.setModuleName(module.get("Module"));
                testConfig.setParallel(module.get("Parallel"));
                testConfig.setParallelCount(module.get("ParallelCount"));

                //Select all the enabled test from current module
                strQuery = String.format("Select * from %s Where RunMode='Yes'", module.get("Module"));
                //List to hold All Enabled test for current module
                List<String> lstTestToRun = new ArrayList<>();
                try {
                    recordset = connection.executeQuery(strQuery);
                    while (recordset.next()) {
                        //System.out.println("|"+recordset.getField("TestID"));
                        lstTestToRun.add(recordset.getField("TestID"));
                    }//End while
                    //Close the recordset
                    recordset.close();
                } catch (Exception ex) {
                }

                //System.out.println(module.get("Module") +" - Total Enabled Test: " +recordset.getCount() );

                //Add Tests list for current module into testConfig object
                testConfig.setTestsList(lstTestToRun);

                //Add Single Test Configuration into List
                lstTestConfig.add(testConfig);


            }//End for
            //Close connection with file
            connection.close();

            //Return List of TestConfig object
            return lstTestConfig;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Test Suite configuration failed");
        }//End catch

    }//End function > GetTestSuite


    /**
     * <h1>Get Test Suite</h1>
     * <p>Purpose: This method will read the TestSelection excel file and return the enabled modules and tests.
     * </p>
     *
     * @return List<TestConfig>
     * @throws Exception
     */
    public static List<TestConfig> GetTestSuite(String filePath) throws Exception {
        //Create A TestConfig object to Hold the list that will be returned
        List<TestConfig> lstTestConfig = new ArrayList<>();

        //Map for holding enabled modules
        List<Map<String, String>> lstModules = new ArrayList<>();
        try {
            File f = new File(filePath);
            Connection connection = fillo.getConnection(f.getAbsolutePath());
            //Get all enabled moduled from Sheet SuiteControl
            String strQuery = String.format("Select * from SuiteControl Where RunMode='Yes'");
            Recordset recordset;
            try {
                recordset = connection.executeQuery(strQuery);
                while (recordset.next()) {
                    Map<String, String> module = new HashMap<>();
                    module.put("Module", recordset.getField("Module"));
                    module.put("Parallel", recordset.getField("Parallel"));
                    module.put("ParallelCount", recordset.getField("ParallelCount"));
                    lstModules.add(module);
                }
//            System.out.println("=============================");
//            System.out.println("Total Enabled modules: " +lstModules.size());
//            System.out.println("=============================");
                recordset.close();

            } catch (Exception ex) {
                System.err.println("There is No module enabled to run.");
            }


            //Find enabled test from the Modules
            for (Map<String, String> module : lstModules) {
                //Hold Single Test Configuration
                TestConfig testConfig = new TestConfig();
                testConfig.setModuleName(module.get("Module"));
                testConfig.setParallel(module.get("Parallel"));
                testConfig.setParallelCount(module.get("ParallelCount"));

                //Select all the enabled test from current module
                strQuery = String.format("Select * from %s Where RunMode='Yes'", module.get("Module"));
                //List to hold All Enabled test for current module
                List<String> lstTestToRun = new ArrayList<>();
                try {
                    recordset = connection.executeQuery(strQuery);
                    while (recordset.next()) {
                        //System.out.println("|"+recordset.getField("TestID"));
                        lstTestToRun.add(recordset.getField("TestID"));
                    }//End while
                    //Close the recordset
                    recordset.close();
                } catch (Exception ex) {
                }

                //System.out.println(module.get("Module") +" - Total Enabled Test: " +recordset.getCount() );

                //Add Tests list for current module into testConfig object
                testConfig.setTestsList(lstTestToRun);

                //Add Single Test Configuration into List
                lstTestConfig.add(testConfig);


            }//End for
            //Close connection with file
            connection.close();

            //Return List of TestConfig object
            return lstTestConfig;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Test Suite configuration failed");
        }//End catch

    }//End function > GetTestSuite

    public static void GetTestCoverageData() {
        try {

            String FS = File.separator;
            String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "TestCoverage" + FS + "TestCoverage.xls";
            File f = new File(testDataFilePath);
            Connection connection = fillo.getConnection(f.getAbsolutePath());

            //Get all enabled moduled from Sheet SuiteControl
            String strQuery = String.format("Select * from Coverage");
            Recordset recordset = connection.executeQuery(strQuery);

            while (recordset.next()) {
                String strKey = recordset.getField("TestCaseID");
                String strCoverages = recordset.getField("CoverageIDs");

                Set<String> coverageSet = new HashSet<>();
                if (!strCoverages.isEmpty()) {

                    for (String id : strCoverages.split(",")) {
                        coverageSet.add(id);
                    }

                }

                //Add Test Id and coverage ids into map
                if (!strKey.isEmpty()) {
                    testCoverageMap.put(strKey, coverageSet);

                }
            }
            recordset.close();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}//End class
