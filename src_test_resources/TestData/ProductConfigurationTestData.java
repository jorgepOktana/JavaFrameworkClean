package TestData;


import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import Utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
* Purpose: This class will hold the product configuration test data
*
* */
public class ProductConfigurationTestData {
String   RowId,ProductType,ProductName,TestProgramName,TestCategories,TestingLevels,ProductRelationshipName,Type,Status,PPRAttributeName,Attribute,AttributeValue,
        TestsNameInCOC,SpecifyTestResults;
 List<ProductConfigurationTestData> childProducts = new ArrayList<>();


    public String getRowId() {
        return RowId;
    }

    public void setRowId(String rowId) {
        RowId = rowId;
    }

    public String getProductType() {
        return ProductType;
    }

    public void setProductType(String productType) {
        ProductType = productType;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getTestProgramName() {
        return TestProgramName;
    }

    public void setTestProgramName(String testProgramName) {
        TestProgramName = testProgramName;
    }

    public String getTestCategories() {
        return TestCategories;
    }

    public void setTestCategories(String testCategories) {
        TestCategories = testCategories;
    }

    public String getTestingLevels() {
        return TestingLevels;
    }

    public void setTestingLevels(String testingLevels) {
        TestingLevels = testingLevels;
    }

    public String getProductRelationshipName() {
        return ProductRelationshipName;
    }

    public void setProductRelationshipName(String productRelationshipName) {
        ProductRelationshipName = productRelationshipName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPPRAttributeName() {
        return PPRAttributeName;
    }

    public void setPPRAttributeName(String PPRAttributeName) {
        this.PPRAttributeName = PPRAttributeName;
    }

    public String getAttribute() {
        return Attribute;
    }

    public void setAttribute(String attribure) {
        Attribute = attribure;
    }

    public String getAttributeValue() {
        return AttributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        AttributeValue = attributeValue;
    }

    public String getTestsNameInCOC() {
        return TestsNameInCOC;
    }

    public void setTestsNameInCOC(String testsNameInCOC) {
        TestsNameInCOC = testsNameInCOC;
    }

    public List<ProductConfigurationTestData> getChildProducts() {
        return childProducts;
    }

    public void setChildProducts(List<ProductConfigurationTestData> childProducts) {
        this.childProducts = childProducts;
    }
    public void addChildProduct(ProductConfigurationTestData childProduct) {
        this.childProducts.add(childProduct);
    }
    public int getTotalChildProducts() {
        return  this.childProducts.size();
    }

    public String getSpecifyTestResults() {
        return SpecifyTestResults;
    }

    public void setSpecifyTestResults(String specifyTestResults) {
        SpecifyTestResults = specifyTestResults;
    }

    @Override
    public String toString() {
        return "ProductConfigurationTestData{" +
                "RowId='" + RowId + '\'' +
                ", ProductType='" + ProductType + '\'' +
                ", ProductName='" + ProductName + '\'' +
                ", TestProgramName='" + TestProgramName + '\'' +
                ", TestCategories='" + TestCategories + '\'' +
                ", TestingLevels='" + TestingLevels + '\'' +
                ", ProductRelationshipName='" + ProductRelationshipName + '\'' +
                ", Type='" + Type + '\'' +
                ", Status='" + Status + '\'' +
                ", PPRAttributeName='" + PPRAttributeName + '\'' +
                ", Attribute='" + Attribute + '\'' +
                ", AttributeValue='" + AttributeValue + '\'' +
                ", TestsNameInCOC='" + TestsNameInCOC + '\'' +
                ", SpecifyTestResults='" + SpecifyTestResults + '\'' +
                ", childProducts=" + childProducts +
                '}';
    }

    public void GetData(String rowId) throws Exception {
        this.setRowId(rowId);

        Fillo fillo = new Fillo();
        try {
            String FS = File.separator;
            String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "src_test_resources/TestData" + FS + "TestProgram_TestData.xlsx";
            File f = new File(testDataFilePath);
            Connection connection = fillo.getConnection(f.getAbsolutePath());

            //***********************Get TestProgram Data******************************************************************
            String strQuery = String.format("Select * from %s Where RowId='%s'", "ProductConfiguration", this.getRowId().trim());
            Recordset recordset = connection.executeQuery(strQuery);

            //If there is no record for given test id then stop and throw error
            if (recordset.getCount() < 1) {
                throw new Exception("No data found for TestProgram using TestID:" + this.getRowId().trim());
            }

            //***********************Get  Data******************************************************************
            while (recordset.next()) {
                            this.setProductType(recordset.getField("ProductType"));
                            this.setProductName(recordset.getField("ProductName"));
                            this.setTestProgramName(recordset.getField("TestProgramName"));
                            this.setTestCategories(recordset.getField("TestCategories"));
                            this.setTestingLevels(recordset.getField("TestingLevels"));
                            this.setProductRelationshipName(recordset.getField("ProductRelationshipName"));
                            this.setType(recordset.getField("Type"));
                            this.setStatus(recordset.getField("Status"));
                            this.setPPRAttributeName(recordset.getField("PPRAttributeName"));
                            this.setAttribute(recordset.getField("Attribute"));
                            this.setAttributeValue(recordset.getField("AttributeValue"));
                            this.setTestsNameInCOC(recordset.getField("TestsNameInCOC"));
                            this.setSpecifyTestResults(recordset.getField("SpecifyTestResults"));

            }
            //Close recordset for Form sheet
            recordset.close();
            connection.close();
        } catch (Exception ex) {
            Log.error("Error in test data reading: " +ex.getMessage());
            //Throw the exception To fail the test
            throw new Exception(String.format("Test data not found in sheet ProductConfiguration with RowId: '%s'", rowId));
        }

    }
}
