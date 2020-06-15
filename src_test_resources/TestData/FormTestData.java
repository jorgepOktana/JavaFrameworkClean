package TestData;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import Utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static Utils.Common.GetTimeStamp;

public class FormTestData {
    String TestId;
    String ContainerTemplateName, Type;
    List<Tab> Tabs = new ArrayList<>();
    List<Section> Sections = new ArrayList<>();
    List<LinkedQuestion> LinkedQuestions = new ArrayList<>();


    public String getTestId() {
        return TestId;
    }

    public void setTestId(String testId) {
        TestId = testId;
    }

    public String getContainerTemplateName() {
        return ContainerTemplateName;
    }

    public void setContainerTemplateName(String containerTemplateName) {
        ContainerTemplateName = containerTemplateName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public List<Tab> getTabs() {
        return Tabs;
    }

    public void setTabs(List<Tab> tabs) {
        Tabs = tabs;
    }

    public List<Section> getSections() {
        return Sections;
    }

    public void setSections(List<Section> sections) {
        Sections = sections;
    }

    public List<LinkedQuestion> getLinkedQuestions() {
        return LinkedQuestions;
    }

    public void setLinkedQuestions(List<LinkedQuestion> linkedQuestions) {
        LinkedQuestions = linkedQuestions;
    }

    @Override
    public String toString() {
        return "FormTestData{" +
                "TestId='" + TestId + '\'' +
                ", ContainerTemplateName='" + ContainerTemplateName + '\'' +
                ", Type='" + Type + '\'' +
                ", Tabs=" + Tabs +
                ", Sections=" + Sections +
                ", LinkedQuestions=" + LinkedQuestions +
                '}';
    }

    //Inner Classes

    //Get Test Data from Excel
    public FormTestData GetData(String testId) throws Exception {
        return this.GetFormTestData(testId);
    }

    private synchronized FormTestData GetFormTestData(String testId) throws Exception {
        Fillo fillo = new Fillo();
        try {
            FormTestData formTestData = new FormTestData();
            String FS = File.separator;
            String testDataFilePath = "src" + FS + "test" + FS + "resources" + FS + "src_test_resources/TestData" + FS + "FormBuilder_TestData.xlsx";
            File f = new File(testDataFilePath);
            Connection connection = fillo.getConnection(f.getAbsolutePath());

            //***********************Get Form Data******************************************************************
            String strQuery = String.format("Select * from %s Where TestID='%s'", "Forms", testId.trim());
            Recordset recordset = connection.executeQuery(strQuery);

            //If there is no record for given test id then stop and throw error
            if (recordset.getCount() < 1) {
                throw new Exception("No data found for TestID:" + testId);
            }
            //Process Recordset for Form
            while (recordset.next()) {
                //Get Form Data from Excel> "Form" sheet and set into object
                formTestData.setTestId(recordset.getField("TestID").trim());
                formTestData.setContainerTemplateName(recordset.getField("ContainerTemplateName").trim() + " " + GetTimeStamp());
                formTestData.setType(recordset.getField("Type").trim());
            }
            //Close recordset for Form sheet
            recordset.close();
            //Check if formTestData has fetched records from sheet Forms
           /* System.out.println("Test Data for Form TestID:" + formTestData.getTestId());
            System.out.println("Test Data for Form Container TemplateName:" + formTestData.getContainerTemplateName());
            System.out.println("Test Data for Form Type:" + formTestData.getType());*/
            //===========================End Form Block===============================================================


            //***********************Get Tabs Data******************************************************************
            strQuery = String.format("Select * from %s Where TestID='%s'", "Tabs", formTestData.getTestId());
            recordset = connection.executeQuery(strQuery);
            //If there is no record for given test id then stop and throw error
            if (recordset.getCount() < 1) {
                throw new Exception("No data found for Tabs with TestID:" + testId);
            }
            //Process Recordset for Tab
            List<Tab> lstTabs = new ArrayList<>();
            while (recordset.next()) {
                Tab tab = new Tab();
                tab.setTabName(recordset.getField("TabName").trim());
                tab.setDescription(recordset.getField("Description").trim());
                lstTabs.add(tab);
            }
            //Set All the Tabs in List
            formTestData.setTabs(lstTabs);
            recordset.close();
            //Check if formTestData has fetched records from sheet Tabs
            /*System.out.println("All Tabs Data");
            for (Tab t: formTestData.getTabs()){
                System.out.println(t.toString());
            }*/
            //===========================End Tab Block===============================================================


            //***********************Get Section Data******************************************************************
            //Get Recordset for Tab
            strQuery = String.format("Select * from %s Where TestID='%s'", "Sections", formTestData.getTestId());
            recordset = connection.executeQuery(strQuery);
            //If there is no record for given test id then stop and throw error
            if (recordset.getCount() < 1) {
                throw new Exception("No data found for Sections with TestID:" + testId);
            }
            //Process Recordset for Section
            List<Section> lstSections = new ArrayList<>();
            while (recordset.next()) {
                Section section = new Section();
                section.setSectionName(recordset.getField("SectionName").trim());
                section.setDisplayStyle(recordset.getField("DisplayStyle").trim());
                section.setTabName(recordset.getField("TabName").trim());
                section.setDescription(recordset.getField("Description").trim());
                lstSections.add(section);
            }
            //Set All the Sections in List
            formTestData.setSections(lstSections);
            recordset.close();
            //Check if formTestData has fetched records from sheet Sections
            /*System.out.println("All Section Data");
            for (Section s: formTestData.getSections()){
                System.out.println(s.toString());
            }*/
            //===========================End Section Block===============================================================


            //***********************Get LinkedQuestions Data******************************************************************
            //Get Recordset for Linked questions
            //Try is used to handle No record found error because Linked questions can be empty
            try {
                strQuery = String.format("Select * from %s Where TestID='%s'", "LinkedQuestions", formTestData.getTestId());
                recordset = connection.executeQuery(strQuery);
                //If there is no record for given test id then stop and throw error
                //NOTE* --> There is no need to check <1 Record for Linked Questions, Because Linked question can be 0

                //Process Recordset for Linked questions
                List<LinkedQuestion> lstLinkedQuestions = new ArrayList<>();
                while (recordset.next()) {
                    LinkedQuestion question = new LinkedQuestion();
                    question.setTabName(recordset.getField("TabName").trim());
                    question.setSectionName(recordset.getField("SectionName").trim());
                    question.setQuestionName(recordset.getField("QuestionName").trim());
                    question.setQuestionText(recordset.getField("QuestionText").trim());
                    question.setAnswerType(recordset.getField("AnswerType").trim());
                    question.setOptionListName(recordset.getField("OptionListName").trim());
                    question.setDependencyAction(recordset.getField("DependencyAction").trim());
                    question.setResponseRequired(recordset.getField("ResponseRequired").trim());
                    question.setReadOnlyFor(recordset.getField("ReadOnlyFor").trim());
                    question.setKeepEditable(recordset.getField("KeepEditableFor").trim());
                    question.setParentQuestions(recordset.getField("ParentQuestions").trim());
                    question.setFormulaOperator(recordset.getField("FormulaOperator").trim());
                    question.setDependentAnswer(recordset.getField("DependentAnswer").trim());
                    question.setIsBold(recordset.getField("FormatLabelBold").trim());
                    question.setIsItalic(recordset.getField("FormatLabelItalic").trim());
                    question.setIsUnderline(recordset.getField("FormatLabelUnderline").trim());
                    question.setAdvanceSection(recordset.getField("AdvanceSection").trim());
                    question.setInternalOnly(recordset.getField("InternalOnly").trim());
                    question.setDoNotRequireCheckBoxForUpload(recordset.getField("DoNotRequireCheckBoxForUpload").trim());
                    question.setPlaceHolderText(recordset.getField("PlaceHolderText").trim());
                    lstLinkedQuestions.add(question);
                }//End While
                //Set All the Sections in List
                formTestData.setLinkedQuestions(lstLinkedQuestions);
            } catch (Exception ex) {
                Log.info("No LinkedQuestions found for TestId: " + testId);
            }

            recordset.close();
            //Check if formTestData has fetched records from sheet Sections
            /*System.out.println("All LinkedQuestions Data");
            for (LinkedQuestion q: formTestData.getLinkedQuestions()){
                System.out.println(q.toString());
            }*/
            //===========================End LinkedQuestions Block===============================================================

            connection.close();
            return formTestData;
        } catch (FilloException e) {
            Log.error("Error in test data reading");
            //Throw the exception To fail the test
            e.printStackTrace();
            throw new Exception(String.format("Test data not found for TestId: '%s'", testId));
        }
    }//End function

    //Class to Hold Tab
    public static class Tab {
        String TabName, Description;
        public Tab(){this.TabName=""; this.Description="";}

        public String getTabName() {
            return TabName;
        }

        public void setTabName(String tabName) {
            TabName = tabName;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        @Override
        public String toString() {
            return "Tab{" +
                    "TabName='" + TabName + '\'' +
                    ", Description='" + Description + '\'' +
                    '}';
        }
    }//End class Tab

//End Inner Classes

    //class to Hold Tab
    public static class Section {
        String TabName;
        String SectionName, DisplayStyle, Description;

        public String getTabName() {
            return TabName;
        }

        public void setTabName(String tabName) {
            TabName = tabName;
        }

        public String getSectionName() {
            return SectionName;
        }

        public void setSectionName(String sectionName) {
            SectionName = sectionName;
        }

        public String getDisplayStyle() {
            return DisplayStyle;
        }

        public void setDisplayStyle(String displayStyle) {
            DisplayStyle = displayStyle;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        @Override
        public String toString() {
            return "Section{" +
                    "TabName='" + TabName + '\'' +
                    ", SectionName='" + SectionName + '\'' +
                    ", DisplayStyle='" + DisplayStyle + '\'' +
                    ", Description='" + Description + '\'' +
                    '}';
        }


    }//End class Section

    //class to Hold Tab
    public static class LinkedQuestion {
        String TabName;
        String SectionName;
        String QuestionName;
        String QuestionText;
        String AnswerType;
        String OptionListName;
        String DependencyAction;
        String DependentAnswer;
        String ResponseRequired;
        String ReadOnlyFor;
        String KeepEditable;
        String ParentQuestions;
        String isBold;
        String isItalic;
        String isUnderline;
        String advanceSection;
        String FormulaOperator;
        String InternalOnly;
        String DoNotRequireCheckBoxForUpload;
        String PlaceHolderText;

        public String getDoNotRequireCheckBoxForUpload() {
            return DoNotRequireCheckBoxForUpload;
        }

        public void setDoNotRequireCheckBoxForUpload(String doNotRequireCheckBoxForUpload) {
            DoNotRequireCheckBoxForUpload = doNotRequireCheckBoxForUpload;
        }

        public String getInternalOnly() {
            return InternalOnly;
        }

        public void setInternalOnly(String internalOnly) {
            InternalOnly = internalOnly;
        }

        public String getAdvanceSection() {
            return advanceSection;
        }

        public void setAdvanceSection(String advanceSection) {
            this.advanceSection = advanceSection;
        }

        public String getIsItalic() {
            return isItalic;
        }

        public void setIsItalic(String isItalic) {
            this.isItalic = isItalic;
        }

        public String getIsUnderline() {
            return isUnderline;
        }

        public void setIsUnderline(String isUnderline) {
            this.isUnderline = isUnderline;
        }

        public String getDependentAnswer() {
            return DependentAnswer;
        }

        public void setDependentAnswer(String dependentAnswer) {
            DependentAnswer = dependentAnswer;
        }

        public String getParentQuestions() {
            return ParentQuestions;
        }

        public void setParentQuestions(String parentQuestions) {
            ParentQuestions = parentQuestions;
        }

        public String getFormulaOperator() {
            return FormulaOperator;
        }

        public void setFormulaOperator(String formulaOperator) {
            FormulaOperator = formulaOperator;
        }

        public String getKeepEditable() {
            return KeepEditable;
        }

        public void setKeepEditable(String keepEditable) {
            KeepEditable = keepEditable;
        }

        public String getReadOnlyFor() {
            return ReadOnlyFor;
        }

        public void setReadOnlyFor(String readOnlyFor) {
            ReadOnlyFor = readOnlyFor;
        }


        public String getTabName() {
            return TabName;
        }

        public void setTabName(String tabName) {
            TabName = tabName;
        }

        public String getSectionName() {
            return SectionName;
        }

        public void setSectionName(String sectionName) {
            SectionName = sectionName;
        }

        public String getQuestionName() {
            return QuestionName;
        }

        public void setQuestionName(String questionName) {
            QuestionName = questionName;
        }

        public String getQuestionText() {
            return QuestionText;
        }

        public void setQuestionText(String questionText) {
            QuestionText = questionText;
        }

        public String getAnswerType() {
            return AnswerType;
        }

        public void setAnswerType(String answerType) {
            AnswerType = answerType;
        }

        public String getOptionListName() {
            return OptionListName;
        }

        public void setOptionListName(String optionListName) {
            OptionListName = optionListName;
        }

        public String getDependencyAction() {
            return DependencyAction;
        }

        public void setDependencyAction(String dependencyAction) {
            DependencyAction = dependencyAction;
        }

        public String getResponseRequired() {
            return ResponseRequired;
        }

        public void setResponseRequired(String responseRequired) {
            ResponseRequired = responseRequired;
        }

        public String getIsBold() {
            return isBold;
        }

        public void setIsBold(String isBold) {
            this.isBold = isBold;
        }

        public String getPlaceHolderText() {
            return PlaceHolderText;
        }

        public void setPlaceHolderText(String placeHolderText) {
            PlaceHolderText = placeHolderText;
        }

        @Override
        public String toString() {
            return "LinkedQuestion{" +
                    "TabName='" + TabName + '\'' +
                    ", SectionName='" + SectionName + '\'' +
                    ", QuestionName='" + QuestionName + '\'' +
                    ", QuestionText='" + QuestionText + '\'' +
                    ", AnswerType='" + AnswerType + '\'' +
                    ", OptionListName='" + OptionListName + '\'' +
                    ", DependencyAction='" + DependencyAction + '\'' +
                    ", DependentAnswer='" + DependentAnswer + '\'' +
                    ", ResponseRequired='" + ResponseRequired + '\'' +
                    ", ReadOnlyFor='" + ReadOnlyFor + '\'' +
                    ", KeepEditable='" + KeepEditable + '\'' +
                    ", ParentQuestions='" + ParentQuestions + '\'' +
                    ", isBold='" + isBold + '\'' +
                    ", isItalic='" + isItalic + '\'' +
                    ", isUnderline='" + isUnderline + '\'' +
                    ", advanceSection='" + advanceSection + '\'' +
                    ", FormulaOperator='" + FormulaOperator + '\'' +
                    ", InternalOnly='" + InternalOnly + '\'' +
                    ", DoNotRequireCheckBoxForUpload='" + DoNotRequireCheckBoxForUpload + '\'' +
                    ", PlaceHolderText='" + PlaceHolderText + '\'' +
                    '}';
        }
    }//End class LinkedQuestions


}//End class FormTestData
