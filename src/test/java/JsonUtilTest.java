import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.server.utils.JsonUtil;

public class JsonUtilTest {
    @Test
    @DisplayName("json 파싱 테스트")
    public void parseJsonTest(){
        // given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command", "move");
        jsonObject.put("x", "1");
        jsonObject.put("y", "2");
        String json = jsonObject.toJSONString();

        // when
        String message = JsonUtil.parseJson(json);

        // then
        Assert.assertEquals(message, "move 1 2");
    }

    @Test
    @DisplayName("json 생성 테스트")
    public void generateJsonTest(){
        // given
        String plainText = "This is test.";

        // when
        String json = JsonUtil.generateJson(plainText);

        // then
        Assert.assertEquals(json, "{\"Notice\":\"This is test.\"}");
    }
}
