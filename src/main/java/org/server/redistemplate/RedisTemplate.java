package org.server.redistemplate;

import org.server.bot.Bot;
import org.server.game.Game;
import org.server.mainserver.MainServer;
import org.server.utils.ServerConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.*;

public final class RedisTemplate {
    JedisPool pool;
    Jedis jedis;

    private static RedisTemplate instance;
    private static MainServer mainServer;
    private static Map<String, Thread> botList;

    private static Game game;

    private RedisTemplate(){
        pool = new JedisPool(ServerConfig.JEDIS_DEFAULT_IP, Protocol.DEFAULT_PORT);
        jedis = pool.getResource();
        game = Game.getInstance();
        botList = new HashMap<>();
    }

    public static RedisTemplate getInstance(){
        if(instance == null){
            instance = new RedisTemplate();
        }
        return instance;
    }

    public synchronized String createUser(String nickname){
        jedis.sadd("nickname", nickname);                   // user nickname
        jedis.sadd(nickname + ":hp", "100");         // user hp
        jedis.sadd(nickname + ":str", "5");
        jedis.sadd(nickname + ":x_pos", "0");     // first position
        jedis.sadd(nickname + ":y_pos", "0");     // first position
        return "[Create User] " + "[nickname: " + nickname + ", hp:100, str:5, x_pos: 0, y_pos: 0]";
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
        return nickname + " attack.";
    }

    public synchronized String showMonsters(){
        return "";
    }

    public synchronized String showUsers(){
        Set<String> members = jedis.smembers("nickname");
        List<String> list = new ArrayList<>(members);
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for(String member : list){
            sb.append("[User-"+ i +"]" + member + "\n");
            i++;
        }
        return sb.toString();
    }

    public synchronized String chat(String from, String to, String content){
        mainServer.sendMessage(from, to, content);
        return from + " send message.";
    }

    public synchronized String activateBot(String nickname){
        Bot bot = new Bot(nickname);
        addBot(nickname, bot);
        bot.start();
        return nickname + " activate Bot mode.";
    }

    public synchronized String deactivateBot(String nickname){
        removeBot(nickname);
        return nickname + " deactivate Bot mode.";
    }

    public synchronized void setMainServer(MainServer mainServer) {
        this.mainServer = mainServer;
    }

    public synchronized void addBot(String nickname, Bot bot){
        botList.put(nickname, bot);
    }

    public synchronized void removeBot(String nickname){
        botList.remove(nickname);
    }
}
