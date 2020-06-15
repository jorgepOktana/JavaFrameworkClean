package Utils;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import icix.TestData.TestCoverageTestData;

import java.io.File;

public class TestCoverage {
    static Fillo fillo = new Fillo();
    TestCoverageTestData objTCTD = new TestCoverageTestData();

    public static Recordset GetTestData(String module, String testId, String[] coverageID, int count) throws Exception{
        try {
            String FS = File.separator;
            String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "TestCoverage" + FS + "TestCoverage.xls";
            File f = new File(testDataFilePath);
            Connection connection = fillo.getConnection(f.getAbsolutePath());

            String [] s2 = coverageID[0].split( " " );
            for (int i=0;i<count; i++) {
                System.out.println("List elements are" + s2);
            }

            String strQuery = String.format("Select * from %s Where TestID='%s'", module, testId, coverageID);
            Recordset recordset = connection.executeQuery(strQuery);
            connection.close();
            return recordset;
        } catch (FilloException e) {
            Log.info("Error in test data reading");
            e.printStackTrace();
            throw new Exception(String.format("Test data not found for Module: '%s' and TestId: '%s'", module, testId));
        }

    }

    /*public void GetTestCaseID(String module, String testId, String coverageID) throws Exception
    {
           this.GetTestData(module,testId, coverageID);
           String str = coverageID;
           List<String> eleList = Arrays.asList(str.split(","));
           System.out.println("List elements are" + eleList);


    }*/
}
