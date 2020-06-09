package TestData;

import com.codoid.products.fillo.Recordset;
import Utils.ExcelUtil;
import Utils.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class ThreeActorTestData {
    public static Map<LabKey, String> labResultsMap = new HashMap<>();
    private String Lab, QuantitativeValue, QualitativeValue, Zip, State, City, Address, Country, Attention, CompanyName, TestResultValue, DefectiveSize;

    public static Map<String,String> requestGroupId = new HashMap<>();

    public String getDefectiveSize() {
        return DefectiveSize;
    }

    public void setDefectiveSize(String defectiveSize) {
        DefectiveSize = defectiveSize;
    }

    public String getTestResultValue() {
        return TestResultValue;
    }

    public void setTestResultValue(String testResultValue) {
        TestResultValue = testResultValue;
    }

    public String getQualitativeValue() {
        return QualitativeValue;
    }

    public void setQualitativeValue(String qualitativeValue) {
        QualitativeValue = qualitativeValue;
    }

    public static void SaveThreeActorData(ThreeActorTestData.ThreeActorKeys key, String value) {
        String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "ThreeActorData.properties";
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            Properties prop = new Properties();
            prop.load(inputStream);
            inputStream.close();

            FileOutputStream fos = new FileOutputStream(filePath);
            prop.setProperty(key.name(), value);
            prop.store(fos, null);
            fos.close();

        } catch (Exception ex) {
            Log.error("Problem in Three actor save data ");
            ex.printStackTrace();
        }
    }

    //Save A form into file
    public static String GetThreeActorData(ThreeActorTestData.ThreeActorKeys key) {
        Properties prop = new Properties();
        String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "ThreeActorData.properties";
        String result = "NA";
        try (FileInputStream fis = new FileInputStream(filePath)) {
            prop.load(fis);
            //Check if Key is available
            if (prop.containsKey(key.name())) {
                result = prop.getProperty(key.name());
            } else
                result = "NA";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "NA";
        }
        //Check if result has empty string
        if (result.isEmpty()) {
            result = "NA";
        }
        return result;
    }//End save form

    public static void ResetThreeActorData() {
        SaveThreeActorData(ThreeActorKeys.IsProductCofigured, "NO");
        SaveThreeActorData(ThreeActorKeys.IsProductTestFormConfigured, "NO");
        SaveThreeActorData(ThreeActorKeys.ProductTestForm, "NA");
        SaveThreeActorData(ThreeActorKeys.ProductTestManager, "NA");
        SaveThreeActorData(ThreeActorKeys.ProductName, "NA");
        SaveThreeActorData(ThreeActorKeys.CorrectiveActionForm, "NA");
        //This line is used to instruct system clean Three actor test data
        SaveThreeActorData(ThreeActorKeys.CleanThreeActorData, "Yes");
    }

    public String getLab() {
        return Lab;
    }

    public void setLab(String lab) {
        Lab = lab;
    }

    public String getQuantitativeValue() {
        return QuantitativeValue;
    }

    public void setQuantitativeValue(String quantitativeValue) {
        QuantitativeValue = quantitativeValue;
    }

    public String getZip() {
        return Zip;
    }

    public void setZip(String zip) {
        Zip = zip;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getAttention() {
        return Attention;
    }

    public void setAttention(String attention) {
        Attention = attention;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    @Override
    public String toString() {
        return "ThreeActorTestData{" +
                "Lab='" + Lab + '\'' +
                ", QuantitativeValue='" + QuantitativeValue + '\'' +
                ", QualitativeValue='" + QualitativeValue + '\'' +
                ", Zip='" + Zip + '\'' +
                ", State='" + State + '\'' +
                ", City='" + City + '\'' +
                ", Address='" + Address + '\'' +
                ", Country='" + Country + '\'' +
                ", Attention='" + Attention + '\'' +
                ", CompanyName='" + CompanyName + '\'' +
                ", TestResultValue='" + TestResultValue + '\'' +
                ", DefectiveSize='" + DefectiveSize + '\'' +
                '}';
    }

    public ThreeActorTestData GetData(String testId) throws Exception {
        Recordset rs = ExcelUtil.GetTestData("ThreeActor", testId);
        while (rs.next()) {
            //Lab_Admin name
            this.setLab(rs.getField("Lab name"));
            //Test Result value
            this.setQuantitativeValue(rs.getField("QuantitativeValue"));
            this.setQualitativeValue(rs.getField("QualitativeValue"));
            //Zip Code
            this.setZip(rs.getField("Zip"));
            //State Code
            this.setState(rs.getField("State"));
            //City
            this.setCity(rs.getField("City"));
            //Address
            this.setAddress(rs.getField("Address"));
            //Country
            this.setCountry(rs.getField("Country"));
            //Attention
            this.setAttention(rs.getField("Attention"));
            //CompanyName
            this.setCompanyName(rs.getField("CompanyName"));

            this.setTestResultValue(rs.getField("TestResultValue"));

            this.setDefectiveSize(rs.getField("DefectiveSize"));

        }//End while
        rs.close();
        return this;
    }


    public static enum LabKey {
        BrandLimitPF,
        BrandLimitValue,
        RegLimitPF,
        RegLimitValue,
        CorrectiveActionReq,
        BrandTestClassName,
        BrandTestClassResult,
        RegulatoryTestClassName,
        RegulatoryTestClassResult,
        TestResultValue,
        TestResultBrand,
        TestResultReg,
        TestReportNo;

    }
    public enum ThreeActorKeys {
        ProductTestManager,ProductTestForm, ProductName, IsProductCofigured,
        IsProductTestFormConfigured, CleanThreeActorData,
        RequesterName, CorrectiveActionForm,ProductTestForm_NonCertificate}
}
