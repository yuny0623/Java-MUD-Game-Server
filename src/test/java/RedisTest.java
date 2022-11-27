import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.Set;

public class RedisTest {

    JedisPool pool;
    Jedis jedis;

    @Before
    public void setup(){
        pool = new JedisPool("127.0.0.1", Protocol.DEFAULT_PORT);
        jedis = pool.getResource();
    }

    @Test
    public void Jedis_connection_test(){
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
    public void redis_lists_test(){
        // given
        jedis.lpush("queue#tasks", "firstTask");
        jedis.lpush("queue#tasks", "secondTask");

        // when
        String task = jedis.rpop("queue#tasks");

        // then
        Assert.assertEquals(task, "firstTask");
    }

    @Test
    public void redis_sets_test(){
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
}
