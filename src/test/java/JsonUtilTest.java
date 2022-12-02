import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.server.utils.JsonUtil;

public class JsonUtilTest {
    @Test
    public void parseJsonTest(){

    }

    @Test
    public void generateJsonTest(){
        // given
        String plainText = "This is test.";

        // when
        String json = JsonUtil.generateJson(plainText);

        // then
        Assert.assertEquals(json, "{\"Notice\":\"This is test.\"}");
    }
}
