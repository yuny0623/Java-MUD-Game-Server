import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.server.utils.GameUtil;
import org.server.utils.JedisUtil;
import org.server.config.ServerConfig;
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
    @DisplayName("유저 생성 테스트")
    public void user_creation_test(){
        // given
        String nickname = "tony";
        JedisUtil.createUser(nickname);

        // when
        String foundNickname = jedis.hget(nickname, "user_nickname");
        String hp = jedis.hget(nickname, "hp");
        String str = jedis.hget(nickname, "str");
        String xPos = jedis.hget(nickname, "x_pos");
        String yPos = jedis.hget(nickname, "y_pos");
        String hpPotion = jedis.hget(nickname, "hp_potion");
        String strPotion = jedis.hget(nickname, "str_potion");

        // then
        Assert.assertEquals(nickname, foundNickname);
        Assert.assertNotNull(hp);
        Assert.assertNotNull(str);
        Assert.assertNotNull(xPos);
        Assert.assertNotNull(yPos);
        Assert.assertNotNull(hpPotion);
        Assert.assertNotNull(strPotion);
    }

    @Test
    @DisplayName("유저 hp 포션 사용 시 체력 회복 테스트")
    public void user_hp_potion_test(){
        // given
        String nickname = "bruce";
        JedisUtil.createUser(nickname);
        int originUserHp = Integer.parseInt(jedis.hget(nickname, "hp"));

        // when
        boolean isMember = jedis.sismember("nicknames", nickname);
        boolean isUse = JedisUtil.useHpPotion(nickname);
        int userHp = Integer.parseInt(jedis.hget(nickname, "hp"));

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
        JedisUtil.createUser(nickname);
        int originStr = Integer.parseInt(jedis.hget(nickname, "str"));

        // when
        boolean isMember = jedis.sismember("nicknames", nickname);
        boolean isUse = JedisUtil.useStrPotion(nickname);
        int userStr = Integer.parseInt(jedis.hget(nickname, "str"));
        int userExtraStr = Integer.parseInt(jedis.get(nickname+":extra_str"));

        // then
        Assert.assertTrue(isMember);
        Assert.assertTrue(isUse);
        Assert.assertEquals(originStr + 3, userStr + userExtraStr);
    }

    @Test
    @DisplayName("유저 str 포션 사용 시 일정 시간 이후에 공격력 감소 테스트")
    public void user_str_potion_decrease_in_specific_time_test(){
        // given
        String nickname = "eugine";
        JedisUtil.createUser(nickname);
        int originStr = Integer.parseInt(jedis.hget(nickname,  "str"));

        // when
        boolean isMember = jedis.sismember("nicknames", nickname);
        boolean isUse = JedisUtil.useStrPotion(nickname);
        int userStr = Integer.parseInt(jedis.hget(nickname, "str"));
        int userExtraStr = Integer.parseInt(jedis.get(nickname + ":extra_str"));
        try {
            Thread.sleep(61 * 1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        int timedOutUserExtraStr = JedisUtil.getExtraStr(nickname);

        // then
        Assert.assertTrue(isMember);
        Assert.assertTrue(isUse);
        Assert.assertEquals(originStr, userStr);
        Assert.assertEquals(3, userExtraStr);
        Assert.assertEquals(0, timedOutUserExtraStr);
    }

    @Test
    @DisplayName("extraStr의 expire Time 테스트")
    public void extraStr_expire_time_test(){
        // given
        String nickname = "gradle";
        JedisUtil.createUser(nickname);
        int originStr = Integer.parseInt(jedis.hget(nickname, "str"));

        // when
        jedis.setex(nickname+":extra_str", 1, "3");
        int increasedExtraStr = JedisUtil.getExtraStr(nickname);
        try {
            Thread.sleep(2 * 1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        int timedOutExtraStr = JedisUtil.getExtraStr(nickname);

        // then
        Assert.assertEquals(originStr + 3, originStr + increasedExtraStr);
        Assert.assertEquals(0, timedOutExtraStr);
    }

    @Test
    @DisplayName("사용자가 정보가 지정된 시간 동안 redis에 정보 저장되는지 테스트")
    public void check_if_user_info_is_stored_within_five_minutes(){
        // given
        String nickname = "hibernate";
        int x = GameUtil.generateRandomNumber(0, 29);
        int y = GameUtil.generateRandomNumber(0, 29);
        jedis.sadd("nicknames", nickname);
        jedis.hset(nickname, "user_nickname", nickname);
        jedis.hset(nickname, "hp", "30");
        jedis.hset(nickname, "str", "3");
        jedis.hset(nickname, "x_pos", String.valueOf(x));
        jedis.hset(nickname, "y_pos", String.valueOf(y));
        jedis.hset(nickname, "hp_potion", "1");
        jedis.hset(nickname, "str_potion", "1");
        jedis.expire(nickname, 1);

        // when
        String hp = jedis.hget(nickname, "hp");
        String str = jedis.hget(nickname, "str");
        String x_pos = jedis.hget(nickname, "x_pos");
        String y_pos = jedis.hget(nickname, "y_pos");
        String hp_potion = jedis.hget(nickname, "hp_potion");
        String str_potion = jedis.hget(nickname, "str_potion");

        try {
            Thread.sleep(2 * 1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        String timedOut_hp = jedis.hget(nickname, "hp");
        String timedOut_str = jedis.hget(nickname, "str");
        String timedOut_x_pos = jedis.hget(nickname, "x_pos");
        String timedOut_y_pos = jedis.hget(nickname, "y_pos");
        String timedOut_hp_potion = jedis.hget(nickname, "hp_potion");
        String timedOut_str_potion = jedis.hget(nickname, "str_potion");

        // then
        Assert.assertEquals(hp, "30");
        Assert.assertEquals(str, "3");
        Assert.assertEquals(x_pos, String.valueOf(x));
        Assert.assertEquals(y_pos, String.valueOf(y));
        Assert.assertEquals(hp_potion, "1");
        Assert.assertEquals(str_potion, "1");

        Assert.assertNull(timedOut_hp);
        Assert.assertNull(timedOut_str);
        Assert.assertNull(timedOut_x_pos);
        Assert.assertNull(timedOut_y_pos);
        Assert.assertNull(timedOut_hp_potion);
        Assert.assertNull(timedOut_str_potion);
    }

    @Test
    @DisplayName("isValidUser 메소드 테스트")
    public void isValidUser_method_test(){
        // given
        String nickname = "gradle";
        JedisUtil.createUser(nickname);

        // when
        boolean isValid = JedisUtil.isValidUser(nickname);

        // then
        Assert.assertTrue(isValid);
    }
}
