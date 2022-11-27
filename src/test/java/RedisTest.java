import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.Map;
import java.util.Set;

public class RedisTest {

    JedisPool pool;
    Jedis jedis;

    @Before
    public void jedis_setup(){
        pool = new JedisPool("127.0.0.1", Protocol.DEFAULT_PORT);
        jedis = pool.getResource();
    }

    @Test
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
    public void jedis_sorted_sets_test(){
        // given

        // when

        // then

    }
}
