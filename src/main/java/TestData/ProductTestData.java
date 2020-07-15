package TestData;

import com.codoid.products.fillo.Recordset;
import org.openqa.selenium.WebDriver;
import pageObjects.BasePage;

import java.util.Date;

public class ProductTestData extends BasePage {
    // public static String ProductName ="Product Testing"+date;
    public static String id = "UPC";
    // public static String ProductGroupTag="DigitalW"; // New Added
    public static String ProductGroupTag = GetUserData("ProductGroupTag");
    static Date d = new Date(System.currentTimeMillis());
    public static String prodGrpName = "Automation Product Group " + d;
    public static String prodEditGrpName = "Automation Product Editing Group " + d; // New Added

    private String TpNameToSearchProduct, UniversalID, ProductName, ProductIdType, IDText, ProductDescriptions, TPName,
            RelationshipStatus, RelationshipType, InternalProductIDsType, InternalProductIDsValue, RelationshipTag,
            PrivacySettings, RelationshipAttribute, RelationshipAttributeValuesForCountry, AttributeTypeFromFilter, EnterNewTagName;

    public ProductTestData(WebDriver driver) {
        super();
    }

    public String getTpNameToSearchProduct() {
        return TpNameToSearchProduct;
    }

    public void setTpNameToSearchProduct(String tpNameToSearchProduct) {
        TpNameToSearchProduct = tpNameToSearchProduct;
    }

    public String getEnterNewTagName() {
        return EnterNewTagName;
    }

    public void setEnterNewTagName(String enterNewTagName) {
        EnterNewTagName = enterNewTagName;
    }

    public String getUniversalID() {
        return UniversalID;
    }

    public void setUniversalID(String universalID) {
        UniversalID = universalID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductIdType() {
        return ProductIdType;
    }

    public void setProductIdType(String productIdType) {
        ProductIdType = productIdType;
    }

    public String getIDText() {
        return IDText;
    }

    public void setIDText(String IDText) {
        this.IDText = IDText;
    }

    public String getProductDescriptions() {
        return ProductDescriptions;
    }

    public void setProductDescriptions(String productDescriptions) {
        ProductDescriptions = productDescriptions;
    }

    public String getTPName() {
        return TPName;
    }

    public void setTPName(String TPName) {
        this.TPName = TPName;
    }

    public String getRelationshipType() {
        return RelationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        RelationshipType = relationshipType;
    }

    public String getInternalProductIDsType() {
        return InternalProductIDsType;
    }

    public void setInternalProductIDsType(String internalProductIDsType) {
        InternalProductIDsType = internalProductIDsType;
    }

    public String getInternalProductIDsValue() {
        return InternalProductIDsValue;
    }

    public void setInternalProductIDsValue(String internalProductIDsValue) {
        InternalProductIDsValue = internalProductIDsValue;
    }

    public String getPrivacySettings() {
        return PrivacySettings;
    }

    public void setPrivacySettings(String privacySettings) {
        PrivacySettings = privacySettings;
    }

    public String getRelationshipStatus() {
        return RelationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        RelationshipStatus = relationshipStatus;
    }

    public String getRelationshipTag() {
        return RelationshipTag;
    }

    public void setRelationshipTag(String relationshipTag) {
        RelationshipTag = relationshipTag;
    }

    // Created by Divya ================ START ================================//
    public String getRelationshipAttribute() {
        return RelationshipAttribute;
    }

    public void setRelationshipAttribute(String relationshipAttribute) {
        RelationshipAttribute = relationshipAttribute;
    }

    public String getRelationshipAttributeValuesForCountry() {
        return RelationshipAttributeValuesForCountry;
    }

    public void setRelationshipAttributeValuesForCountry(String relationshipAttributeValuesForCountry) {
        RelationshipAttributeValuesForCountry = relationshipAttributeValuesForCountry;
    }
    // Created by Divya ================ END ================================//


    public String getAttributeTypeFromFilter() {
        return AttributeTypeFromFilter;
    }

    public void setAttributeTypeFromFilter(String attributeTypeFromFilter) {
        AttributeTypeFromFilter = attributeTypeFromFilter;
    }

    @Override
    public String toString() {
        return "ProductTestData{" +
                "TpNameToSearchProduct='" + TpNameToSearchProduct + '\'' +
                ", UniversalID='" + UniversalID + '\'' +
                ", ProductName='" + ProductName + '\'' +
                ", ProductIdType='" + ProductIdType + '\'' +
                ", IDText='" + IDText + '\'' +
                ", ProductDescriptions='" + ProductDescriptions + '\'' +
                ", TPName='" + TPName + '\'' +
                ", RelationshipStatus='" + RelationshipStatus + '\'' +
                ", RelationshipType='" + RelationshipType + '\'' +
                ", InternalProductIDsType='" + InternalProductIDsType + '\'' +
                ", InternalProductIDsValue='" + InternalProductIDsValue + '\'' +
                ", RelationshipTag='" + RelationshipTag + '\'' +
                ", PrivacySettings='" + PrivacySettings + '\'' +
                ", RelationshipAttribute='" + RelationshipAttribute + '\'' +
                ", RelationshipAttributeValuesForCountry='" + RelationshipAttributeValuesForCountry + '\'' +
                ", AttributeTypeFromFilter='" + AttributeTypeFromFilter + '\'' +
                ", EnterNewTagName='" + EnterNewTagName + '\'' +
                '}';
    }

    public void GetData(String testId) throws Exception {
        Recordset rs = GetTestData("ProductSearch", testId);
        Date d = new Date(System.currentTimeMillis());
        while (rs.next()) {
            // TpNameToSearchProduct
            this.setTpNameToSearchProduct(rs.getField("TPNameToSearchProduct"));

            // UniversalID
            this.setUniversalID(rs.getField("UniversalID"));

            // ProductName
            this.setProductName(rs.getField("ProductName") + " " + getTimeStamp());

            // ProductIdType
            this.setProductIdType(rs.getField("ProductIdType"));

            // IDText
            this.setIDText(rs.getField("IDText"));

            // ProductDescriptions
            this.setProductDescriptions(rs.getField("ProductDescriptions"));
            // TPName
            this.setTPName(rs.getField("TPName"));
            // RelationshipStatus
            this.setRelationshipStatus(rs.getField("RelationshipStatus"));
            // RelationshipType
            this.setRelationshipType(rs.getField("RelationshipType"));
            // InternalProductIDsType
            this.setInternalProductIDsType(rs.getField("InternalProductIDsType"));
            // InternalProductIDsValue
            this.setInternalProductIDsValue(rs.getField("InternalProductIDsValue"));
            // Relationship Tag
            this.setRelationshipTag(rs.getField("RelationshipTag"));
            // PrivacySettings
            this.setPrivacySettings(rs.getField("PrivacySettings"));

            // Created by Divya ================ START ================================//
            // Attributes
            this.setRelationshipAttribute(rs.getField("RelationshipAttribute"));
            //Attribute value for Country
            this.setRelationshipAttributeValuesForCountry(rs.getField("RelationshipAttributeValuesForCountry"));
            // Created by Divya ================ END ================================//

            this.setAttributeTypeFromFilter(rs.getField("RelationshipAttributeType"));

            this.setEnterNewTagName(rs.getField("EnterNewTagName"));
        } // End while
        rs.close();

    }
}
