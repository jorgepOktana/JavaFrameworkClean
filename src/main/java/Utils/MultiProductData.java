package Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MultiProductData {
    @SerializedName("three_actor_tests")
    @Expose
    private List<ThreeActorTest> threeActorTests = new ArrayList<ThreeActorTest>();

    /**
     * No args constructor for use in serialization
     *
     */
    public MultiProductData() {
    }

    /**
     *
     * @param threeActorTests
     */
    public MultiProductData(List<ThreeActorTest> threeActorTests) {
        super();
        this.threeActorTests = threeActorTests;
    }

    public List<ThreeActorTest> getThreeActorTests() {
        return threeActorTests;
    }

    public void setThreeActorTests(List<ThreeActorTest> threeActorTests) {
        this.threeActorTests = threeActorTests;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("threeActorTests", threeActorTests).toString();
    }

public void saveData(){
    FileWriter writer = null;
    String jsonFolderPath = "src/test/resources/JSON";
    try {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonToWrite =gson.toJson(this).toString();
        writer = new FileWriter(jsonFolderPath+"/gen.json");
        writer.write(jsonToWrite);
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public ThreeActorTest getTestData(String testId){
    ThreeActorTest threeActorTestData = new ThreeActorTest();
    Predicate<ThreeActorTest> filter = testdata->testdata.getId().equalsIgnoreCase(testId);
    List<ThreeActorTest> finalFilterList=this.getThreeActorTests().stream().filter(filter).collect(Collectors.toList());
    if(finalFilterList.size()>0){
        threeActorTestData =finalFilterList.get(0);
    }
    return threeActorTestData;

}

    public void getDataFromJson(){
        String jsonFolderPath = "src/test/resources/JSON";
        String dataFile =jsonFolderPath+"/"+"data.json";
        Gson gson = new Gson();
        FileWriter writer = null;

        try {
            JsonReader reader = new JsonReader(new FileReader(dataFile));
            MultiProductData data =gson.fromJson(reader,MultiProductData.class);
            this.setThreeActorTests(data.getThreeActorTests());
            this.getThreeActorTests().get(0).setId("QA_JSOn");
            gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonToWrite =gson.toJson(data).toString();

            writer = new FileWriter(jsonFolderPath+"/gen.json");
            writer.write(jsonToWrite);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) {
        MultiProductData data = new MultiProductData();
        data.getDataFromJson();
        System.out.println("Data size: " +data.getThreeActorTests().size());
        ThreeActorTest threeActorTestData = data.getTestData("QA_0");
        threeActorTestData.setChildProduct("I have changed");
        data.saveData();

        System.out.println(threeActorTestData.toString());

//        for (int i = 0; i < data.getThreeActorTests().size(); i++) {
//            if (data.getThreeActorTests().get(i).getId().equalsIgnoreCase("QA_2")){
//                System.out.println("Data fond");
//                data.getThreeActorTests().get(i).setChildProduct("Auto Test Child");
//                data.getThreeActorTests().get(i).setRequest("This is updated data");
//                data.saveData();
//                break;
//            }
//
//
//        }

    }

}
