import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.w3c.dom.ls.LSException;
import redis.clients.jedis.*;

import java.util.*;

public class JedisTest {
    JedisPool pool;
    Jedis jedis;

    @Before
    public void jedis_setup(){
        pool = new JedisPool("127.0.0.1", Protocol.DEFAULT_PORT);
        jedis = pool.getResource();
    }

    @After
    public void tearDown(){
        jedis.flushAll();
    }

    @Test
    @DisplayName("jedis 연결 테스트")
    public void jedis_connection_test(){
        // given
        jedis.set("test", "hello world");
        jedis.setex("test1", 60, "hello world expire");

        // when
        var data = jedis.get("test");
        var data2 = jedis.get("test1");

        // then
        Assert.assertEquals(data, "hello world");
        Assert.assertEquals(data2, "hello world expire");
    }

    @Test
    @DisplayName("jedis list 테스트")
    public void jedis_lists_test(){
        // given
        jedis.lpush("queue#tasks", "firstTask");
        jedis.lpush("queue#tasks", "secondTask");

        // when
        String task = jedis.rpop("queue#tasks");

        // then
        Assert.assertEquals(task, "firstTask");
    }

    @Test
    @DisplayName("jedis set 테스트")
    public void jedis_sets_test(){
        // given
        jedis.sadd("nicknames", "tony");
        jedis.sadd("nicknames", "gradle");
        jedis.sadd("nicknames", "maven");

        // when
        Set<String> nicknames = jedis.smembers("nicknames");
        boolean exists = jedis.sismember("nicknames", "gradle");

        // then
        Assert.assertTrue(exists);
    }

    @Test
    @DisplayName("jedis hash 테스트")
    public void jedis_hashes_test(){
        // given
        jedis.hset("user#1", "name", "Peter");
        jedis.hset("user#1", "job", "politician");

        // when
        String name = jedis.hget("user#1", "name");
        Map<String, String> fields = jedis.hgetAll("user#1");
        String job = fields.get("job");

        // then
        Assert.assertEquals(name, "Peter");
        Assert.assertEquals(job, "politician");
    }

    @Test
    @DisplayName("jedis 모든 키 확인 테스트")
    public void jedis_all_keys_test(){
        // given
        jedis.set("name", "tony");
        Set<String> keys = jedis.keys("*");
        List<String> keyList = new ArrayList<>();

        // when
        for(String key: keys){
            keyList.add(key);
        }

        // then
        Assert.assertTrue(keyList.size()!=0);
    }

    @Test
    @DisplayName("jedis 모든 키 지우기 테스트")
    public void jedis_flush_keys_test(){
        // given
        jedis.set("name", "tony");
        List<String> keyList = new ArrayList<>();

        // when
        String result = jedis.flushAll();
        Set<String> keys = jedis.keys("*");
        for(String key: keys){
            keyList.add(key);
        }

        // then
        Assert.assertEquals(result, "OK");
        Assert.assertTrue(keyList.size() == 0);
    }

    @Test
    @DisplayName("Jedis에서 존재하지 않는 키로 탐색하기 테스트")
    public void get_non_exist_key_test(){
        // given
        jedis.set("test_key", "test_value");

        // when
        String result = jedis.get("invalid_test_key");

        // then
        Assert.assertTrue(result == null);
    }

    @Test
    @DisplayName("hset 동작 테스트")
    public void hset_test(){
        // given
        String nickname = "Lee";
        jedis.hset("user:" + nickname, "nickname", nickname);
        jedis.hset("user:" + nickname, "x_pos","1");
        jedis.hset("user:" + nickname, "y_pos","2");

        // when
        String foundNickname = jedis.hget("user:Lee", "nickname");
        String foundXPos = jedis.hget("user:Lee","x_pos");
        String foundYPos = jedis.hget("user:Lee", "y_pos");

        // then
        Assert.assertEquals(nickname, foundNickname);
        Assert.assertEquals("1", foundXPos);
        Assert.assertEquals("2", foundYPos);
    }

    @Test
    @DisplayName("Jedis의 hset expire 동작 테스트")
    public void hset_expire_test(){
        // given
        String nickname = "Lee";
        jedis.hset("user:" + nickname, "nickname", nickname);
        jedis.hset("user:" + nickname, "x_pos","1");
        jedis.hset("user:" + nickname, "y_pos","2");
        jedis.expire("user:Lee", 1);

        // when
        try {
            Thread.sleep(2 * 1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        String foundNickname = jedis.hget("user:Lee", "nickname");
        String foundXPos = jedis.hget("user:Lee","x_pos");
        String foundYPos = jedis.hget("user:Lee", "y_pos");

        // then
        Assert.assertNull(foundXPos);
        Assert.assertNull(foundYPos);
        Assert.assertNull(foundNickname);
    }

    @Test
    @DisplayName("Jedis의 hgetAll 테스트")
    public void hgetAll_test(){
        // given
        String nickname = "Lee";
        jedis.hset("user:" + nickname, "nickname", nickname);
        jedis.hset("user:" + nickname, "x_pos","1");
        jedis.hset("user:" + nickname, "y_pos","2");

        // when
        Map<String, String> result = jedis.hgetAll("user:"+nickname);
        String[] arr = new String[] {nickname, "1", "2"};
        int i = 0;

        // then
        for(String key: result.keySet()){
            Assert.assertEquals(result.get(key), arr[i++]);
        }
    }
    
    @Test
    @DisplayName("Jedis의 hincrBy에 음수를 전달했을 경우 테스트")
    public void give_negative_number_to_hincrBy_test(){
        // given
        String nickname = "tomson";
        jedis.hset(nickname, "hp", "30");
        int originHp = Integer.parseInt(jedis.hget(nickname, "hp"));

        // when
        jedis.hincrBy(nickname, "hp", 10);
        int increasedHp = Integer.parseInt(jedis.hget(nickname, "hp"));
        jedis.hincrBy(nickname, "hp", -10);
        int decreasedHp = Integer.parseInt(jedis.hget(nickname, "hp"));

        // then
        Assert.assertEquals(originHp + 10, increasedHp);
        Assert.assertEquals(increasedHp - 10, decreasedHp);
    }

    @Test
    @DisplayName("Jedis sscan 기능 테스트")
    public void sscan_test(){
        // given
        String setKey = "nicknames";
        String target = UUID.randomUUID().toString();
        for(int i = 0; i < 999; i++){
            jedis.sadd(setKey, UUID.randomUUID().toString());
        }
        jedis.sadd(setKey, target);

        // when
        String key = "nicknames";
        ScanParams scanParams = new ScanParams().count(100);
        String cur = ScanParams.SCAN_POINTER_START;
        boolean cycleIsFinished = false;
        boolean isContains = false;
        while(!cycleIsFinished){
            ScanResult<String> scanResult = jedis.sscan(key, cur, scanParams);
            List<String> resultList = scanResult.getResult();

            for(int i = 0; i < resultList.size(); i++){
                if(resultList.get(i).equals(target)){
                    isContains = true;
                    break;
                }
            }
            cur = scanResult.getStringCursor();
            if(cur.equals("0")){
                cycleIsFinished = true;
            }
        }

        // then
        Assert.assertTrue(isContains);
    }
}
