import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.server.game.monster.MonsterManager;
import org.server.utils.GameUtil;
import org.server.utils.JedisUtil;
import org.server.config.ServerConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;
import java.util.List;

public class JedisUtilTest {
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
    @DisplayName("jedis get 테스트")
    public void jedis_get_test(){
        // given
        String nickname = "monad";
        jedis.sadd("nicknames",  nickname);
        jedis.set(nickname + ":hp", "30");
        jedis.set(nickname + ":str", "3");

        // when
        String hp = jedis.get(nickname+":hp");
        String str = jedis.get(nickname + ":str");

        // then
        Assert.assertEquals(hp, "30");
        Assert.assertEquals(str, "3");
    }
    
    @Test
    @DisplayName("유저 생성 테스트")
    public void user_creation_test(){
        // given
        String nickname = "tony";
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
        jedis.expire(nickname, 5 * 60);

        // when
        boolean isMember = jedis.sismember("nicknames", "tony");
        String foundNickname = jedis.hget("tony", "user_nickname");
        String hp = jedis.hget("tony", "hp");
        String str = jedis.hget("tony", "str");
        String x_pos = jedis.hget("tony", "x_pos");
        String y_pos = jedis.hget("tony", "y_pos");
        String hp_potion = jedis.hget("tony", "hp_potion");
        String str_potion = jedis.hget("tony", "str_potion");

        // then
        Assert.assertTrue(isMember);
        Assert.assertEquals(nickname, foundNickname);
        Assert.assertEquals("30", hp);
        Assert.assertEquals("3", str);
        Assert.assertEquals(String.valueOf(x), x_pos);
        Assert.assertEquals(String.valueOf(y), y_pos);
        Assert.assertEquals("1", hp_potion);
        Assert.assertEquals("1", str_potion);
    }

    @Test
    @DisplayName("생성된 몬스터들이 제한된 범위 내에 존재하는지 테스트")
    public void created_monster_is_in_range_test(){
        // given
        MonsterManager monsterManager = new MonsterManager();
        monsterManager.start();
        String monsters = JedisUtil.showMonsters();
        String[] monsterRows = monsters.split("\n");
        List<Integer> posList = new ArrayList<>();

        // when
        for(int i = 0; i < monsterRows.length; i++){
            String[] vals = monsterRows[i].split(" ");
            posList.add(Integer.valueOf(vals[1]));
            posList.add(Integer.valueOf(vals[2]));
        }
        boolean isInRange = true;
        for(int i = 0; i < posList.size(); i++){
            if(posList.get(i) < 0 || posList.get(i) > 29){
                isInRange = false;
            }
        }

        // then
        Assert.assertTrue(isInRange);
    }
}
