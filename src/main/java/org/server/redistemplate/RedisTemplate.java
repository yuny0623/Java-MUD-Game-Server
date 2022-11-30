package org.server.redistemplate;

import org.server.game.Game;
import org.server.game.monster.Monster;
import org.server.mainserver.MainServer;
import org.server.utils.ServerConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.*;

public final class RedisTemplate {
    private static JedisPool pool = new JedisPool(ServerConfig.JEDIS_DEFAULT_IP, Protocol.DEFAULT_PORT);
    private static Jedis jedis = pool.getResource();;
    private static MainServer mainServer;
    private static Map<String, Thread> botList = new HashMap<>();
    private static Game game = Game.getInstance();;

    public RedisTemplate(){

    }

    public static synchronized String createUser(String nickname){
        // random 한 위치에 생성
        int x = (int) (Math.random() * (29 - 0) + 0) + 0;
        int y = (int) (Math.random() * (29 - 0) + 0) + 0;

        jedis.sadd("nickname", nickname);                   // user nickname
        jedis.sadd(nickname + ":hp", "30");         // user hp
        jedis.sadd(nickname + ":str", "3");
        jedis.sadd(nickname + ":x_pos", String.valueOf(x));     // first position
        jedis.sadd(nickname + ":y_pos", String.valueOf(y));     // first position

        return "[Create User] " + "[nickname: " + nickname + ", hp:30, str:3, x_pos: "+x+", y_pos: "+y+"]";
    }

    public static synchronized String move(String nickname, int x, int y){
        String foundXPos = jedis.get(nickname + ":x_pos");
        String foundYPos = jedis.get(nickname + ":y_pos");
        String toX = String.valueOf(x + Integer.parseInt(foundXPos));
        String toY = String.valueOf(y + Integer.parseInt(foundYPos));
        jedis.set(nickname + ":x_pos", toX);
        jedis.set(nickname + ":y_pos", toY);
        return nickname + " move from [" + foundXPos + ", " + foundYPos + "] to " + "[" + toX + ", " + toY + "]";
    }

    public static synchronized String userAttack(String nickname){
        /*
            attack logic
         */
        return nickname + " attack.";
    }

    public static synchronized void userAttacked(String nickname, int monsterStr){
        int hp = Integer.parseInt(jedis.get(nickname +":hp"));
        jedis.sadd(String.valueOf(hp - monsterStr));
    }

    public static synchronized String showMonsters(){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < game.monsterList.size(); i++){
            Monster monster = game.monsterList.get(i);
            sb.append("monster " + monster.getX() + " " + monster.getY() + "\n");
        }
        return sb.toString();
    }

    public static synchronized String showUsers(){
        Set<String> members = jedis.smembers("nickname");
        List<String> list = new ArrayList<>(members);
        StringBuffer sb = new StringBuffer();

        for(String member : list){
            System.out.println("member: " + member);
            String xPos = jedis.get(member + ":x_pos");
            String yPos = jedis.get(member + ":y_pos");
            sb.append(member + " " + xPos + " " + yPos + "\n");
        }
        return sb.toString();
    }

    public static synchronized boolean isUsers(){
        Set<String> members = jedis.smembers("nickname");
        if(members.size() == 0){
            return false;
        }
        return true;
    }

    public static synchronized String chat(String from, String to, String content){
        mainServer.sendMessage(from, to, content);
        return from + " send message.";
    }
    
    public synchronized void setMainServer(MainServer mainServer) {
        this.mainServer = mainServer;
    }
}
