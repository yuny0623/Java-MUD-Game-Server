import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

public class RedisTest {

    @Test
    public void redis연동테스트(){
        // given
        var pool = new JedisPool("127.0.0.1", Protocol.DEFAULT_PORT);
        var jedis = pool.getResource();
        jedis.set("test", "hello world");
        jedis.setex("test1", 60, "hello world expire");

        // when
        var data = jedis.get("test");
        var data2 = jedis.get("test1");

        // then
        Assert.assertEquals(data, "hello world");
        Assert.assertEquals(data2, "hello world expire");
    }
}
