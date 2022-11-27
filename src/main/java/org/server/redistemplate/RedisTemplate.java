package org.server.redistemplate;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;

public final class RedisTemplate {
    JedisPool pool;
    Jedis jedis;

    private static RedisTemplate instance;

    private RedisTemplate(){
        pool = new JedisPool("127.0.0.1", Protocol.DEFAULT_PORT);
        jedis = pool.getResource();
    }

    public static RedisTemplate getInstance(){
        if(instance == null){
            instance = new RedisTemplate();
        }
        return instance;
    }

    public synchronized void createUser(String nickname, String info){
//        jedis.setex(nickname, 300, );
//        jedis.setex(nickname +"#" + "hp", 300, );
//        jedis.setex(nickname + "#" + "row" + "#" + "col", 300, );
//        jedis.setex(nickname, 300, );
//        jedis.setex(nickname, 300, );
//        jedis.setex(nickname, 300, );
    }

    public synchronized void move(){

    }

    public synchronized void attack(){

    }

    public synchronized void showMonsters(){

    }

    public synchronized ArrayList<String> showUsers(){
        return new ArrayList<>();
    }

    public synchronized void chat(){

    }

    public synchronized void activateBot(){

    }

    public synchronized void deactivateBot(){

    }
}
