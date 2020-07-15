package TestData;

import Utils.Log;
import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import pageObjects.BasePage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductTestManagerTestData extends BasePage {
    List<Category> categoryLits = new ArrayList<>();
    List<LimitValues> limitValuesList = new ArrayList<>();
    List<TestSubstance> testSubstanceList = new ArrayList<>();
    List<CustomCerificate> customCerificates = new ArrayList<>();

    private String ProductTestManagerName = "";
    private String CerfificateName = "";
    private String AllowBulkAction;
    private String ValidationPeriod;
    private String Retest, Days;
    private String LabName = "";
    private String CategoryName = "";
    private String TestClass = "";
    private String TestName = "";
    private String TestType = "";
    private String TestMethodName = "";
    private String Operator = "";
    private String Value = "";
    private String Measure = "";
    private String Message = "";
    private String CreateCustomCertificate;

    public ProductTestManagerTestData() {
        super();
    }

    @Override
    public String toString() {
        return "ProductTestManagerTestData{" +
                "categoryLits=" + categoryLits +
                ", limitValuesList=" + limitValuesList +
                ", testSubstanceList=" + testSubstanceList +
                ", customCerificates=" + customCerificates +
                ", ProductTestManagerName='" + ProductTestManagerName + '\'' +
                ", CerfificateName='" + CerfificateName + '\'' +
                ", AllowBulkAction='" + AllowBulkAction + '\'' +
                ", ValidationPeriod='" + ValidationPeriod + '\'' +
                ", Retest='" + Retest + '\'' +
                ", Days='" + Days + '\'' +
                ", LabName='" + LabName + '\'' +
                ", CategoryName='" + CategoryName + '\'' +
                ", TestClass='" + TestClass + '\'' +
                ", TestName='" + TestName + '\'' +
                ", TestType='" + TestType + '\'' +
                ", TestMethodName='" + TestMethodName + '\'' +
                ", Operator='" + Operator + '\'' +
                ", Value='" + Value + '\'' +
                ", Measure='" + Measure + '\'' +
                ", Message='" + Message + '\'' +
                ", CreateCustomCertificate='" + CreateCustomCertificate + '\'' +
                '}';
    }

    public List<CustomCerificate> getCustomCerificates() {
        return customCerificates;
    }

    public void setCustomCerificates(List<CustomCerificate> customCerificates) {
        this.customCerificates = customCerificates;
    }

    public String getCreateCustomCertificate() {
        return CreateCustomCertificate;
    }

    public void setCreateCustomCertificate(String createCustomCertificate) {
        CreateCustomCertificate = createCustomCertificate;
    }

    public List<Category> getCategoryLits() {
        return categoryLits;
    }

    public void setCategoryLits(List<Category> categoryLits) {
        this.categoryLits = categoryLits;
    }

    public List<LimitValues> getLimitValuesList() {
        return this.limitValuesList;
    }

    public void setLimitValuesList(List<LimitValues> limitValuesList) {
        this.limitValuesList = limitValuesList;
    }

    public List<TestSubstance> getTestSubstanceList() {
        return this.testSubstanceList;
    }

    public void setTestSubstanceList(List<TestSubstance> testSubstanceList) {
        this.testSubstanceList = testSubstanceList;
    }

    public String getValidationPeriod() {
        return this.ValidationPeriod;
    }

    public void setValidationPeriod(String validationPeriod) {
        this.ValidationPeriod = validationPeriod;
    }

    public String getRetest() {
        return this.Retest;
    }

    public void setRetest(String retest) {
        this.Retest = retest;
    }

    public String getDays() {
        return this.Days;
    }

    public void setDays(String days) {
        this.Days = days;
    }

    public String getAllowBulkAction() {
        return this.AllowBulkAction;
    }

    public void setAllowBulkAction(String allowBulkAction) {
        this.AllowBulkAction = allowBulkAction;
    }

    public String getProductTestManagerName() {
        return this.ProductTestManagerName;
    }

    public void setProductTestManagerName(String productTestManagerName) {
        this.ProductTestManagerName = productTestManagerName;
    }

    public String getCerfificateName() {
        return this.CerfificateName;
    }

    public void setCerfificateName(String cerfificateName) {
        this.CerfificateName = cerfificateName;
    }

    public String getLabName() {
        return this.LabName;
    }

    public void setLabName(String labName) {
        this.LabName = labName;
    }

    public String getCategoryName() {
        return this.CategoryName;
    }

    public void setCategoryName(String categoryName) {
        this.CategoryName = categoryName;
    }

    public String getTestClass() {
        return this.TestClass;
    }

    public void setTestClass(String testClass) {
        this.TestClass = testClass;
    }

    public String getTestName() {
        return this.TestName;
    }

    public void setTestName(String testName) {
        this.TestName = testName;
    }

    public String getTestType() {
        return this.TestType;
    }

    public void setTestType(String testType) {
        this.TestType = testType;
    }

    public String getTestMethodName() {
        return this.TestMethodName;
    }

    public void setTestMethodName(String testMethodName) {
        this.TestMethodName = testMethodName;
    }

    public String getOperator() {
        return this.Operator;
    }

    public void setOperator(String operator) {
        this.Operator = operator;
    }

    public String getValue() {
        return this.Value;
    }

    public void setValue(String value) {
        this.Value = value;
    }

    public String getMeasure() {
        return this.Measure;
    }

    public void setMeasure(String measure) {
        this.Measure = measure;
    }

    public String getMessage() {
        return this.Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public void GetData(String testId) throws Exception {
        Recordset rs = GetTestData("ProductTestManager", testId);
        Date d = new Date(System.currentTimeMillis());
        while (rs.next()) {
            this.setProductTestManagerName(rs.getField("ProductTestManagerName") + " " + d);
            this.setCerfificateName(rs.getField("CerfificateName"));
            this.setLabName(rs.getField("LabName"));
            this.setCategoryName(rs.getField("CategoryName") + " " + d);
            this.setTestClass(rs.getField("TestClass"));
            this.setTestName(rs.getField("TestName"));
            this.setTestType(rs.getField("TestType"));
            this.setTestType(rs.getField("TestMethodName"));
            this.setOperator(rs.getField("Operator"));
            this.setValue(rs.getField("Value"));
            this.setMeasure(rs.getField("Measure"));
            this.setMessage(rs.getField("Message"));
        }//End while
        rs.close();

    }

    /**
     * <h1>Get Test Program test data</h1>
     * <p>Purpose: Extract all data for given test id from excel file</p>
     */

    public ProductTestManagerTestData GetData_Dynamic(String testId) throws Exception {
        Fillo fillo = new Fillo();
        ProductTestManagerTestData productTestManagerTestData = new ProductTestManagerTestData();
        try {
            String FS = File.separator;
            String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "TestData" + FS + "TestProgram_TestData.xlsx";
            File f = new File(testDataFilePath);
            Connection connection = fillo.getConnection(f.getAbsolutePath());

            //***********************Get TestProgram Data******************************************************************
            String strQuery = String.format("Select * from %s Where TestID='%s'", "TestProgram", testId.trim());
            Recordset recordset = connection.executeQuery(strQuery);

            //If there is no record for given test id then stop and throw error
            if (recordset.getCount() < 1) {
                throw new Exception("No data found for TestProgram using TestID:" + testId);
            }

            //***********************Get TestProgram Data******************************************************************
            while (recordset.next()) {
                productTestManagerTestData.setProductTestManagerName(recordset.getField("ProductTestManagerName") + " " + getTimeStamp());
                productTestManagerTestData.setCerfificateName(recordset.getField("CerfificateName"));
                productTestManagerTestData.setAllowBulkAction(recordset.getField("AllowBulkAction"));
                productTestManagerTestData.setValidationPeriod(recordset.getField("ValidationPeriod"));
                productTestManagerTestData.setRetest(recordset.getField("Retest"));
                productTestManagerTestData.setDays(recordset.getField("Days"));
                productTestManagerTestData.setLabName(recordset.getField("LabName"));
                productTestManagerTestData.setCreateCustomCertificate(recordset.getField("CreateCustomCertificate"));
            }
            //Close recordset for Form sheet
            recordset.close();
            //===========================End TestProgram Block===============================================================

            //Get Custom Certificate
            if(productTestManagerTestData.getCreateCustomCertificate().equalsIgnoreCase("Yes")){
                strQuery = String.format("Select * from %s Where TestID='%s'", "CustomCertificates", testId.trim());
                recordset = connection.executeQuery(strQuery);
                if (recordset.getCount() < 1) {
                    throw new Exception("No data found for CustomCertificates using TestID:" + testId);
                }
                //Process Recordset for Tab

                List<CustomCerificate> customCerificates = new ArrayList<>();
                while (recordset.next()) {
                    CustomCerificate customCerificate = new CustomCerificate();
                    customCerificate.setTestId(testId);
                    customCerificate.setCertificateName(recordset.getField("CertificateName")+ " "+ getTimeStamp());
                    customCerificate.setTemplatePage(recordset.getField("TemplatePage"));
                    customCerificate.setCertificateTitle(recordset.getField("CertificateTitle")+ " "+ getTimeStamp());
                    customCerificate.setContactName(recordset.getField("ContactName"));
                    customCerificate.setContactPhone(recordset.getField("ContactPhone"));
                    customCerificate.setContactEmail(recordset.getField("ContactEmail"));
                    customCerificate.setSignature1(recordset.getField("Signature1"));
                    customCerificate.setLegalStatement(recordset.getField("LegalStatement"));

                    //Add object to List
                    customCerificates.add(customCerificate);
                }
                //Update List
                productTestManagerTestData.setCustomCerificates(customCerificates);
                //Close recordset for Form sheet
                recordset.close();
            }//end if CustomCertificate



            //***********************Get Category Data******************************************************************
            strQuery = String.format("Select * from %s Where TestID='%s'", "Category", testId.trim());
            try {
                recordset = connection.executeQuery(strQuery);
            } catch (FilloException e) {
                Log.info("There is no data defined for Category using TestID:" + testId );
                return productTestManagerTestData;
            }
            if (recordset.getCount() < 1) {

                return productTestManagerTestData;
            }

            //Proceed further only if any single category found

            //Process Recordset for Tab
            List<Category> categories = new ArrayList<>();
            while (recordset.next()) {
                Category category = new Category();
                category.setCategoryName(recordset.getField("CategoryName"));
                categories.add(category);
            }
            productTestManagerTestData.setCategoryLits(categories);
            //Close recordset for Form sheet
            recordset.close();
            //===========================End Category Block===============================================================


            //***********************Get TestSubstance Data******************************************************************
            strQuery = String.format("Select * from %s Where TestID='%s'", "TestSubstance", testId.trim());
            recordset = connection.executeQuery(strQuery);
            if (recordset.getCount() < 1) {
                throw new Exception("No data found for TestSubstance using TestID:" + testId);
            }
            List<TestSubstance> testSubstances = new ArrayList<>();
            while (recordset.next()) {
                TestSubstance testSubstance = new TestSubstance();
                testSubstance.setCategoryName(recordset.getField("CategoryName"));
                testSubstance.setTestClass(recordset.getField("TestClass"));
                testSubstance.setTestName(recordset.getField("TestName"));
                testSubstance.setTestType(recordset.getField("TestType"));
                testSubstance.setTestMethodName(recordset.getField("TestMethodName"));
                testSubstances.add(testSubstance);
            }//End while
            productTestManagerTestData.setTestSubstanceList(testSubstances);
            recordset.close();
            //===========================End TestSubstance Block===============================================================


            //***********************Get LimitValues Data******************************************************************
            strQuery = String.format("Select * from %s Where TestID='%s'", "LimitValues", testId.trim());
            recordset = connection.executeQuery(strQuery);
            if (recordset.getCount() < 1) {
                Log.info("No data found for Test program's category with id " +testId);
            }
            List<LimitValues> limitValues = new ArrayList<>();
            while (recordset.next()) {
                LimitValues limitValue = new LimitValues();
                limitValue.setCategoryName(recordset.getField("CategoryName"));
                limitValue.setTestClass(recordset.getField("TestClass"));
                limitValue.setRegulatoryOrBrand(recordset.getField("RegulatoryOrBrand"));
                limitValue.setOperator(recordset.getField("Operator"));
                limitValue.setValue(recordset.getField("Value"));
                limitValue.setMeasure(recordset.getField("Measure"));
                limitValue.setMessage(recordset.getField("Message"));
                limitValues.add(limitValue);
            }
            productTestManagerTestData.setLimitValuesList(limitValues);
            //Close recordset for LimitValues sheet
            recordset.close();
            //===========================End TestProgram Block===============================================================
            connection.close();
            return productTestManagerTestData;
        } catch (Exception ex) {
            Log.error("Error in test data reading");
            //Throw the exception To fail the test
            throw new Exception(String.format("Test data not found for TestId: '%s'", testId));
        }

    }

    public class Category {
        String CategoryName;

        public Category() {
        }

        public String getCategoryName() {
            return CategoryName;
        }

        public void setCategoryName(String categoryName) {
            CategoryName = categoryName;
        }

        @Override
        public String toString() {
            return "Category{" +
                    "CategoryName='" + CategoryName + '\'' +
                    '}';
        }
    }//End class

    public class TestSubstance {
        String CategoryName, TestClass, TestName, TestType, TestMethodName;

        public String getCategoryName() {
            return CategoryName;
        }

        public void setCategoryName(String categoryName) {
            CategoryName = categoryName;
        }

        public String getTestClass() {
            return TestClass;
        }

        public void setTestClass(String testClass) {
            TestClass = testClass;
        }

        public String getTestName() {
            return TestName;
        }

        public void setTestName(String testName) {
            TestName = testName;
        }

        public String getTestType() {
            return TestType;
        }

        public void setTestType(String testType) {
            TestType = testType;
        }

        public String getTestMethodName() {
            return TestMethodName;
        }

        public void setTestMethodName(String testMethodName) {
            TestMethodName = testMethodName;
        }

        @Override
        public String toString() {
            return "TestSubstance{" +
                    "CategoryName='" + CategoryName + '\'' +
                    ", TestClass='" + TestClass + '\'' +
                    ", TestName='" + TestName + '\'' +
                    ", TestType='" + TestType + '\'' +
                    ", TestMethodName='" + TestMethodName + '\'' +
                    '}';
        }
    }//End class

    public class LimitValues {
        String CategoryName, TestClass, RegulatoryOrBrand, Operator, Value, Measure, Message;

        public void LimitValues() {
        }

        public String getTestClass() {
            return TestClass;
        }

        public void setTestClass(String testClass) {
            TestClass = testClass;
        }

        public String getCategoryName() {
            return CategoryName;
        }

        public void setCategoryName(String categoryName) {
            CategoryName = categoryName;
        }

        public String getRegulatoryOrBrand() {
            return RegulatoryOrBrand;
        }

        public void setRegulatoryOrBrand(String regulatoryOrBrand) {
            RegulatoryOrBrand = regulatoryOrBrand;
        }

        public String getOperator() {
            return Operator;
        }

        public void setOperator(String operator) {
            Operator = operator;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String value) {
            Value = value;
        }

        public String getMeasure() {
            return Measure;
        }

        public void setMeasure(String measure) {
            Measure = measure;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }

        @Override
        public String toString() {
            return "LimitValues{" +
                    "CategoryName='" + CategoryName + '\'' +
                    ", TestClass='" + TestClass + '\'' +
                    ", RegulatoryOrBrand='" + RegulatoryOrBrand + '\'' +
                    ", Operator='" + Operator + '\'' +
                    ", Value='" + Value + '\'' +
                    ", Measure='" + Measure + '\'' +
                    ", Message='" + Message + '\'' +
                    '}';
        }
    }

    public class CustomCerificate{
        String TestId,CertificateName,	TemplatePage,	CertificateTitle,	ContactName,	ContactEmail,	ContactPhone,	Signature1,	LegalStatement;

        public String getTestId() {
            return TestId;
        }

        public void setTestId(String testId) {
            TestId = testId;
        }

        public String getCertificateName() {
            return CertificateName;
        }

        public void setCertificateName(String certificateName) {
            CertificateName = certificateName;
        }

        public String getTemplatePage() {
            return TemplatePage;
        }

        public void setTemplatePage(String templatePage) {
            TemplatePage = templatePage;
        }

        public String getCertificateTitle() {
            return CertificateTitle;
        }

        public void setCertificateTitle(String certificateTitle) {
            CertificateTitle = certificateTitle;
        }

        public String getContactName() {
            return ContactName;
        }

        public void setContactName(String contactName) {
            ContactName = contactName;
        }

        public String getContactEmail() {
            return ContactEmail;
        }

        public void setContactEmail(String contactEmail) {
            ContactEmail = contactEmail;
        }

        public String getContactPhone() {
            return ContactPhone;
        }

        public void setContactPhone(String contactPhone) {
            ContactPhone = contactPhone;
        }

        public String getSignature1() {
            return Signature1;
        }

        public void setSignature1(String signature1) {
            Signature1 = signature1;
        }

        public String getLegalStatement() {
            return LegalStatement;
        }

        public void setLegalStatement(String legalStatement) {
            LegalStatement = legalStatement;
        }

        @Override
        public String toString() {
            return "CustomCerificate{" +
                    "TestId='" + TestId + '\'' +
                    ", CertificateName='" + CertificateName + '\'' +
                    ", TemplatePage='" + TemplatePage + '\'' +
                    ", CertificateTitle='" + CertificateTitle + '\'' +
                    ", ContactName='" + ContactName + '\'' +
                    ", ContactEmail='" + ContactEmail + '\'' +
                    ", ContactPhone='" + ContactPhone + '\'' +
                    ", Signature1='" + Signature1 + '\'' +
                    ", LegalStatement='" + LegalStatement + '\'' +
                    '}';
        }
    }//End CustomCertificate
}
