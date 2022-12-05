import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.w3c.dom.ls.LSException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JedisTest {
    JedisPool pool;
    Jedis jedis;

    @Before
    public void jedis_setup(){
        pool = new JedisPool("127.0.0.1", Protocol.DEFAULT_PORT);
        jedis = pool.getResource();
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
        Assert.assertEquals(task, "secondTask");
    }

    @Test
    @DisplayName("jedis set 테스트")
    public void jedis_sets_test(){
        // given
        jedis.sadd("nicknames", "nickname#1");
        jedis.sadd("nicknames", "nickname#2");
        jedis.sadd("nicknames", "nickname#3");

        // when
        Set<String> nicknames = jedis.smembers("nicknames");
        boolean exists = jedis.sismember("nicknames", "nickname#1");

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
        String result = jedis.flushAll();
        Set<String> keys = jedis.keys("*");
        List<String> keyList = new ArrayList<>();

        // when
        for(String key: keys){
            keyList.add(key);
        }

        // then
        Assert.assertEquals(result, "OK");
        Assert.assertTrue(keyList.size() == 0);
    }

    @Test
    public void get_non_exist_key_test(){
        // given
        jedis.set("test_key", "test_value");

        // when
        String result = jedis.get("invalid_test_key");

        // then
        Assert.assertTrue(result == null);
    }
}
