import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.server.config.ServerConfig;

public class RESTServerTest {

    @Test
    @DisplayName("http response 생성 테스트")
    public void http_response_generation_test(){
        // given
        String httpStatusCode = "200 OK";
        String data = "this is test";
        String httpResponse = ServerConfig.HTTP_VERSION + " " + httpStatusCode + ServerConfig.HTTP_NEW_LINE + data;

        // when
        String example = "HTTP/1.1 200 OK\r\n" +
                "\r\n" +
                "this is test";

        // then
        Assert.assertEquals(example, httpResponse);
    }
}
