import org.junit.Assert;
import org.junit.Test;
import org.server.redistemplate.RedisTemplate;

public class RedisTemplateTest {

    @Test
    public void redisTemplateGenerateTest(){
        // given
        String nickname = "tony";
        RedisTemplate.createUser(nickname);

        // when
        boolean isUser = RedisTemplate.isUsers();

        // then
        Assert.assertTrue(isUser);
    }
}
