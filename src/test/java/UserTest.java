import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.server.redistemplate.RedisTemplate;
import org.server.utils.ServerConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

public class UserTest {

    Jedis jedis;
    JedisPool pool;

    @Before
    public void setUp(){
        pool = new JedisPool(ServerConfig.JEDIS_DEFAULT_IP, Protocol.DEFAULT_PORT);
        jedis = pool.getResource();
    }

    @Test
    @DisplayName("유저 hp 포션 사용 시 체력 회복 테스트")
    public void user_hp_potion_test(){
        // given
        String nickname = "bruce";
        RedisTemplate.createUser(nickname);
        int originUserHp = Integer.parseInt(jedis.get(nickname+":hp"));

        // when
        boolean isMember = jedis.sismember("nicknames", nickname);
        boolean isUse = RedisTemplate.useHpPotion(nickname);
        int userHp = Integer.parseInt(jedis.get(nickname+":hp"));

        // then
        Assert.assertTrue(isMember);
        Assert.assertTrue(isUse);
        Assert.assertEquals(userHp, originUserHp + 10);
    }
}