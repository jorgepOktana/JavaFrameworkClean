package TestData;

public class UsersTestData {
    private String UserId, Password, Name, IcixId, TPRelationshipTag, TPRelationshipStatus, TPRelationshipType,Url;


    public UsersTestData() {
        this.UserId = "";
        this.Password = "";
        this.Name = "";
        this.IcixId = "";
        this.TPRelationshipTag = "";
        this.TPRelationshipStatus = "";
        this.TPRelationshipType = "";
        this.Url = "";
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIcixId() {
        return IcixId;
    }

    public void setIcixId(String icixId) {
        IcixId = icixId;
    }

    public String getTPRelationshipTag() {
        return TPRelationshipTag;
    }

    public void setTPRelationshipTag(String TPRelationshipTag) {
        this.TPRelationshipTag = TPRelationshipTag;
    }

    public String getTPRelationshipStatus() {
        return TPRelationshipStatus;
    }

    public void setTPRelationshipStatus(String TPRelationshipStatus) {
        this.TPRelationshipStatus = TPRelationshipStatus;
    }

    public String getTPRelationshipType() {
        return TPRelationshipType;
    }

    public void setTPRelationshipType(String TPRelationshipType) {
        this.TPRelationshipType = TPRelationshipType;
    }

    @Override
    public String toString() {
        return "UsersTestData{" +
                "UserId='" + UserId + '\'' +
                ", Password='" + Password + '\'' +
                ", Name='" + Name + '\'' +
                ", IcixId='" + IcixId + '\'' +
                ", TPRelationshipTag='" + TPRelationshipTag + '\'' +
                ", TPRelationshipStatus='" + TPRelationshipStatus + '\'' +
                ", TPRelationshipType='" + TPRelationshipType + '\'' +
                ", Url='" + Url + '\'' +
                '}';
    }
}
