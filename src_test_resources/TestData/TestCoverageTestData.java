package TestData;

public class TestCoverageTestData {
    private String TestCaseID, CoverageID;

    public String getTestCaseID() {
        return TestCaseID;
    }

    public void setTestCaseID(String testCaseID) {
        TestCaseID = testCaseID;
    }

    public String getCoverageID() {
        return CoverageID;
    }

    public void setCoverageID(String coverageID) {
        CoverageID = coverageID;
    }

    @Override
    public String toString() {
        return "TestCoverageTestData{" +
                "TestCaseID='" + TestCaseID + '\'' +
                ", CoverageID='" + CoverageID + '\'' +
                '}';
    }

    /*public void GetData(String testId, String coverageID) throws Exception
    {
        Recordset rs = TestCoverage.GetTestData("CovergaeData", testId, coverageID);
        while (rs.next()) {
            this.setTestCaseID(rs.getField("TestCaseID"));

            this.setCoverageID(rs.getField("CoverageID"));

        }//End while
        rs.close();
    }*/
}
