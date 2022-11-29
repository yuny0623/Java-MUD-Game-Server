package org.server.redistemplate;

import org.server.utils.ServerConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;
import java.util.Set;

public final class RedisTemplate {
    JedisPool pool;
    Jedis jedis;

    private static RedisTemplate instance;

    private RedisTemplate(){
        pool = new JedisPool(ServerConfig.JEDIS_DEFAULT_IP, Protocol.DEFAULT_PORT);
        jedis = pool.getResource();
    }

    public static RedisTemplate getInstance(){
        if(instance == null){
            instance = new RedisTemplate();
        }
        return instance;
    }

    public synchronized void createUser(String nickname){
        jedis.sadd("nickname", nickname);                   // user nickname
        jedis.sadd(nickname+":hp", "");         // user hp
        jedis.sadd(nickname + ":str", "0");
        jedis.sadd(nickname+":x_pos", "0");     // first position
        jedis.sadd(nickname+":y_pos", "0");     // first position
    }

    public synchronized void move(String nickname, int x, int y){
        String foundXPos = jedis.get(nickname + ":x_pos");
        String foundYPos = jedis.get(nickname + ":y_pos");
        jedis.set(nickname + ":x_pos", String.valueOf(x + Integer.parseInt(foundXPos)));
        jedis.set(nickname + ":y_pos", String.valueOf(y + Integer.parseInt(foundYPos)));
    }

    public synchronized void attack(){

    }

    public synchronized void showMonsters(){

    }

    public synchronized Set<String> showUsers(){
        Set<String> members = jedis.smembers("nickname");
        return members;
    }

    public synchronized void chat(String from, String to, String content){

    }

    public synchronized void activateBot(){

    }

    public synchronized void deactivateBot(){

    }
}
