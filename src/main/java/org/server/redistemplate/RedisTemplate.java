package org.server.redistemplate;

import org.server.game.monster.Monster;
import org.server.game.monster.MonsterManager;
import org.server.mainserver.MainServer;
import org.server.utils.ServerConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.*;

public final class RedisTemplate {
    private static final JedisPool pool = new JedisPool(ServerConfig.JEDIS_DEFAULT_IP, Protocol.DEFAULT_PORT);
    private static final Jedis jedis = pool.getResource();
    private static MainServer mainServer;

    private static final int[] dx = {-1, 0, 1, 1, 1, 0, -1, -1, 0};
    private static final int[] dy = {-1, -1, -1, 0, 1, 1, 1, 0, 0};

    public RedisTemplate(){

    }

    public static synchronized String createUser(String nickname){
        int x = (int) (Math.random() * (29 - 0) + 0) + 0;
        int y = (int) (Math.random() * (29 - 0) + 0) + 0;
        jedis.sadd("nicknames", nickname);
        jedis.hset(nickname, "user_nickname", nickname);
        jedis.hset(nickname, "hp", "30");
        jedis.hset(nickname, "str", "3");
        jedis.hset(nickname, "x_pos", String.valueOf(x));
        jedis.hset(nickname, "y_pos", String.valueOf(y));
        jedis.hset(nickname, "hp_potion", "1");
        jedis.hset(nickname, "str_potion", "1");
        jedis.expire(nickname, 5 * 60); // 로그인 5분간 유지
        return "[Create User] " + "[nickname: " + nickname + ", hp:30, str:3, x_pos: " + x + ", y_pos: " + y + "]";
    }

    public static synchronized String move(String nickname, int x, int y){
        jedis.hset(nickname, "x_pos", String.valueOf(x));
        jedis.hset(nickname, "y_pos", String.valueOf(y));
        int movedToX = Integer.parseInt(jedis.hget(nickname , "x_pos"));
        int movedToY = Integer.parseInt(jedis.hget(nickname , "y_pos"));
        return nickname + " move to " + "[" + movedToX + ", " + movedToY + "]";
    }

    public static synchronized String userAttack(String nickname){
        int str = Integer.parseInt(jedis.hget(nickname, "str"));
        int extraStr = RedisTemplate.getExtraStr(nickname);
        int curr_x = Integer.parseInt(jedis.hget(nickname ,"x_pos"));
        int curr_y = Integer.parseInt(jedis.hget(nickname ,"y_pos"));
        if(!checkMonsterExist()){
            return nickname + "attack miss. No Monster exist.";
        }
        String monsters = RedisTemplate.showMonsters();
        for(int i = 0; i < 9; i++){
            int attackX = dx[i] + curr_x;
            int attackY = dy[i] + curr_y;
            if(0 <= attackX && attackX < 30 && 0 <= attackY && attackY < 30){
                String[] monsterRow = monsters.split("\n");
                for(String row: monsterRow){
                    String[] val = row.split(" ");
                    String monsterId = val[0];
                    int monsterX = Integer.parseInt(val[1]);
                    int monsterY = Integer.parseInt(val[2]);
                    if(monsterX == attackX && monsterY == attackY){
                        Monster monster = MonsterManager.monsterMap.get(monsterId);
                        boolean isDead = monster.attacked(str + extraStr);
                        if(isDead) {
                            getReward(nickname, monsterId);
                            monster.interrupt();
                        }
                    }
                }
            }
        }
        return nickname + " attack success.";
    }

    public static synchronized void userAttacked(String nickname, int monsterStr){
        int hp = Integer.parseInt(jedis.hget(nickname ,"hp"));
        if(hp - monsterStr <= 0){
            jedis.hset(nickname, "hp", String.valueOf(0));
            System.out.println(nickname + " was killed by a monster!");
            jedis.sadd("dead_user", nickname);
        }else {
            jedis.hset(nickname ,"hp", String.valueOf(hp - monsterStr));
        }
    }

    public static synchronized boolean isDead(String nickname){
        return jedis.sismember("dead_user", nickname);
    }

    public static synchronized String showMonsters(){
        StringBuffer sb = new StringBuffer();
        for(String monsterId:  MonsterManager.monsterMap.keySet()){
            Monster monster = MonsterManager.monsterMap.get(monsterId);
            sb.append(monsterId + " " + monster.getX() + " " + monster.getY() + "\n");
        }
        return sb.toString();
    }

    public static synchronized String showUsers(){
        Set<String> members = jedis.smembers("nicknames");
        StringBuffer sb = new StringBuffer();
        for(String memberNickname : members){
            if (isDead(memberNickname)) {
                continue;
            }
            if(jedis.hget(memberNickname, "nickname") == null){
                jedis.srem("nicknames", memberNickname);
                continue;
            }
            String xPos = jedis.hget(memberNickname ,"x_pos");
            String yPos = jedis.hget(memberNickname ,"y_pos");
            sb.append(memberNickname + " " + xPos + " " + yPos + "\n");
        }
        return sb.toString();
    }

    public static synchronized boolean checkUserExist(){
        Set<String> members = jedis.smembers("nicknames");
        return members.size() != 0;
    }

    public static synchronized boolean checkMonsterExist(){
        return MonsterManager.monsterMap.size() != 0;
    }

    public static synchronized String chat(String from, String to, String content){
        mainServer.sendMessage(from, to, content);
        return from + " send message.";
    }

    public synchronized void setMainServer(MainServer mainServer) {
        this.mainServer = mainServer;
    }

    public static synchronized int[] getPosition(String nickname){
        int x = Integer.parseInt(jedis.hget(nickname, "x_pos"));
        int y = Integer.parseInt(jedis.hget(nickname, "y_pos"));
        int[] pos = new int[2];
        pos[0] = x;
        pos[1] = y;
        return pos;
    }

    public static synchronized void getReward(String monsterId, String nickname){
        Monster monster = MonsterManager.monsterMap.get(monsterId);
        if(monster == null) {
            return;
        }
        int hpPotion = monster.getHpPotion();
        int strPotion = monster.getStrPotion();
        int userHpPotion = Integer.parseInt(jedis.hget(nickname, "hp_potion"));
        int userStrPotion = Integer.parseInt(jedis.hget(nickname, "str_potion"));
        jedis.hset(nickname ,"hp_potion", String.valueOf(hpPotion + userHpPotion));
        jedis.hset(nickname ,"str_potion", String.valueOf(strPotion + userStrPotion));
    }

    public static synchronized boolean useHpPotion(String nickname){
        int hpPotion = Integer.parseInt(jedis.hget(nickname ,"hp_potion"));
        if(hpPotion > 0){
            int userHp = Integer.parseInt(jedis.hget(nickname ,"hp"));
            userHp += 10;
            hpPotion--;
            jedis.hset(nickname ,"hp_potion", String.valueOf(hpPotion));
            jedis.hset(nickname ,"hp", String.valueOf(userHp));
            return true;
        }else{
            return false;
        }
    }

    public static synchronized int getExtraStr(String nickname){
        String extraStr = jedis.get(nickname + ":extra_str");
        if(extraStr == null){
           return 0;
        }else {
            return Integer.parseInt(extraStr);
        }
    }

    public static synchronized boolean useStrPotion(String nickname){
        int strPotion = Integer.parseInt(jedis.hget(nickname ,"str_potion"));
        if(strPotion <= 0){
            return false;
        }
        String extraStr = jedis.get(nickname+":extra_str");
        if(extraStr == null){
            jedis.setex(nickname+":extra_str", 60, "3");
        }else{
            int foundExtraStr = Integer.parseInt(extraStr);
            foundExtraStr += 3;
            jedis.setex(nickname+":extra_str",60, String.valueOf(foundExtraStr));
        }
        strPotion--;
        jedis.hset(nickname, "str_potion", String.valueOf(strPotion));
        return true;
    }

    public static synchronized int getUserHpPotion(String nickname){
        return Integer.parseInt(jedis.hget(nickname, "hp_potion"));
    }

    public static synchronized int getUserStrPotion(String nickname){
        return  Integer.parseInt(jedis.hget(nickname, "str_potion"));
    }

    public static synchronized  void serverReset(){
        jedis.flushAll();
    }
}
