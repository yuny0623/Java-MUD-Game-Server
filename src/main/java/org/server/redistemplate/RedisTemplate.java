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
        jedis.sadd("nickname", nickname);                  // user nickname
        jedis.sadd(nickname+":hp", String.valueOf(0));  // user hp
        jedis.sadd(nickname+":pos", "0,0");     // first position
    }

    public synchronized void move(String nickname, int x, int y){
        String foundPos = jedis.get(nickname + ":pos");

        String foundX = String.valueOf(foundPos.charAt(0));
        String foundY = String.valueOf(foundPos.charAt(2));

        jedis.set("nickname:pos", Integer.parseInt(foundX) + x + "," + Integer.parseInt(foundY) + y);

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
