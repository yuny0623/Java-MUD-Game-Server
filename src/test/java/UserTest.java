import org.junit.After;
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

    @After
    public void tearDown(){
        jedis.flushAll();
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

    @Test
    @DisplayName("유저 str 포션 사용 시 공격력 증가 테스트")
    public void user_str_potion_test(){
        // given
        String nickname = "maven";
        RedisTemplate.createUser(nickname);
        int originStr = Integer.parseInt(jedis.get(nickname+":str"));

        // when
        boolean isMember = jedis.sismember("nicknames", nickname);
        boolean isUse = RedisTemplate.useStrPotion(nickname);
        int userStr = Integer.parseInt(jedis.get(nickname+":str"));
        int userExtraStr = Integer.parseInt(jedis.get(nickname+":extra_str"));

        // then
        Assert.assertTrue(isMember);
        Assert.assertTrue(isUse);
        Assert.assertEquals(originStr + 3, userStr + userExtraStr);
    }

    @Test
    @DisplayName("extraStr의 expire Time 테스트")
    public void extraStr_expire_time_test(){
        // given
        String nickname = "gradle";
        RedisTemplate.createUser(nickname);
        int originStr = Integer.parseInt(jedis.get(nickname+":str"));

        // when
        jedis.setex(nickname+":extra_str", 1, "3");
        int increasedExtraStr = RedisTemplate.getExtraStr(nickname);
        try {
            Thread.sleep(2 * 1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        int timedOutExtraStr = RedisTemplate.getExtraStr(nickname);

        // then
        Assert.assertEquals(originStr + 3, originStr + increasedExtraStr);
        Assert.assertEquals(0, timedOutExtraStr);
    }

    @Test
    @DisplayName("사용자가 정보가 지정된 시간 동안 redis에 정보 저장되는지 테스트")
    public void check_if_user_info_is_stored_within_five_minutes(){
        // given
        String nickname = "hibernate";
        String x = String.valueOf((int) (Math.random() * (29 - 0) + 0) + 0);
        String y = String.valueOf((int) (Math.random() * (29 - 0) + 0) + 0);
        jedis.sadd("nicknames", nickname);                                   // user nickname
        jedis.setex(nickname + ":hp",1, "30");              // user hp
        jedis.setex(nickname + ":str",1, "3");
        jedis.setex(nickname + ":x_pos",1, x);                      // first position
        jedis.setex(nickname + ":y_pos",1, y);                      // first position
        jedis.setex(nickname + ":hp_potion",1, "1");
        jedis.setex(nickname + ":str_potion",1, "1");

        // when
        String hp = jedis.get(nickname+":hp");
        String str = jedis.get(nickname+":str");
        String x_pos = jedis.get(nickname+":x_pos");
        String y_pos = jedis.get(nickname+":y_pos");
        String hp_potion = jedis.get(nickname+":hp_potion");
        String str_potion = jedis.get(nickname+":str_potion");

        try {
            Thread.sleep(2 * 1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        String timedOut_hp = jedis.get(nickname+":hp");
        String timedOut_str = jedis.get(nickname+":str");
        String timedOut_x_pos = jedis.get(nickname+":x_pos");
        String timedOut_y_pos = jedis.get(nickname+":y_pos");
        String timedOut_hp_potion = jedis.get(nickname+":hp_potion");
        String timedOut_str_potion = jedis.get(nickname+":str_potion");

        // then
        Assert.assertEquals(hp, "30");
        Assert.assertEquals(str, "3");
        Assert.assertEquals(x_pos, x);
        Assert.assertEquals(y_pos, y);
        Assert.assertEquals(hp_potion, "1");
        Assert.assertEquals(str_potion, "1");

        Assert.assertNull(timedOut_hp);
        Assert.assertNull(timedOut_str);
        Assert.assertNull(timedOut_x_pos);
        Assert.assertNull(timedOut_y_pos);
        Assert.assertNull(timedOut_hp_potion);
        Assert.assertNull(timedOut_str_potion);
    }
}
