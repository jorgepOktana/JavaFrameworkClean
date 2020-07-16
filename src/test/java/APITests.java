import Utils.RestAPISF;
import org.testng.annotations.Test;

public class APITests extends TestBase{

    public void createAssortment(){
        RestAPISF.createAssortmentProduct("TestAssortment1");
    }

}
