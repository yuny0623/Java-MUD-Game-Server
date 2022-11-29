package org.server.redistemplate;

import org.server.utils.ServerConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;
import java.util.List;
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

    public synchronized String createUser(String nickname){
        jedis.sadd("nickname", nickname);                   // user nickname
        jedis.sadd(nickname+":hp", "");         // user hp
        jedis.sadd(nickname + ":str", "0");
        jedis.sadd(nickname+":x_pos", "0");     // first position
        jedis.sadd(nickname+":y_pos", "0");     // first position

        return "[Create User] " + nickname
    }

    public synchronized String move(String nickname, int x, int y){
        String foundXPos = jedis.get(nickname + ":x_pos");
        String foundYPos = jedis.get(nickname + ":y_pos");
        String toX = String.valueOf(x + Integer.parseInt(foundXPos));
        String toY = String.valueOf(y + Integer.parseInt(foundYPos));
        jedis.set(nickname + ":x_pos", toX);
        jedis.set(nickname + ":y_pos", toY);
        return nickname + " move from [" + foundXPos + ", " + foundYPos + "] to " + "[" + toX + ", " + toY + "]";
    }

    public synchronized String attack(String nickname){
        /*
            attack logic
         */
        return nickname + " attacked.";
    }

    public synchronized void showMonsters(){

    }

    public synchronized String showUsers(){
        Set<String> members = jedis.smembers("nickname");
        List<String> list = new ArrayList<>(members);
        StringBuffer sb = new StringBuffer();
        for(String member : list){
            sb.append(member + "\n");
        }
        return sb.toString();
    }

    public synchronized void chat(String from, String to, String content){

    }

    public synchronized void activateBot(){

    }

    public synchronized void deactivateBot(){

    }
}
