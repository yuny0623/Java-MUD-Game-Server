import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.server.redistemplate.RedisTemplate;
import org.server.utils.ServerConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;
import java.util.List;

public class RedisTemplateTest {
    Jedis jedis;
    JedisPool pool;
    @Before
    public void setUp(){
        pool = new JedisPool(ServerConfig.JEDIS_DEFAULT_IP, Protocol.DEFAULT_PORT);
        jedis = pool.getResource();
    }

    @Test
    public void redisTemplateGetTest(){
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
    public void userCreationTest(){
        // given
        String nickname = "jenifer";
        int x = (int) (Math.random() * (29 - 0) + 0) + 0;
        int y = (int) (Math.random() * (29 - 0) + 0) + 0;
        jedis.sadd("nicknames", nickname);
        jedis.set(nickname + ":hp", "30");
        jedis.set(nickname + ":str", "3");
        jedis.set(nickname + ":x", String.valueOf(x));
        jedis.set(nickname + ":y", String.valueOf(y));

        // when
        boolean isIn = jedis.sismember("nicknames", nickname);
        String hp = jedis.get(nickname + ":hp");
        String str = jedis.get(nickname + ":str");
        String foundX = jedis.get(nickname + ":x");
        String foundY = jedis.get(nickname + ":y");

        // then
        Assert.assertTrue(isIn);
        Assert.assertEquals("30", hp);
        Assert.assertEquals("3", str);
        Assert.assertEquals(String.valueOf(x), foundX);
        Assert.assertEquals(String.valueOf(y), foundY);
    }

    @Test
    public void showMonstersTest(){
        // given
        String monsters = RedisTemplate.showMonsters();
        String[] monsterRows = monsters.split("\n");
        String[] vals=  null;
        List<Integer> posList = new ArrayList<>();

        // when
        for(int i = 0; i < monsterRows.length; i++){
            vals = monsterRows[i].split(" ");
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
        Assert.assertNotNull(vals);
        Assert.assertTrue(isInRange);
    }
}
