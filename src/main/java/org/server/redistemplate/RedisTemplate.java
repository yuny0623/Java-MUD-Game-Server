package org.server.redistemplate;

import org.server.game.Game;
import org.server.game.monster.Monster;
import org.server.game.monster.MonsterManager;
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
    private static Game game = Game.getInstance();

    private static int[] dx = {-1, 0, 1, 1, 1, 0, -1, -1, 0};
    private static int[] dy = {-1, -1, -1, 0, 1, 1, 1, 0, 0};

    public RedisTemplate(){

    }

    public static synchronized String createUser(String nickname){
        // random 한 위치에 생성
        int x = (int) (Math.random() * (29 - 0) + 0) + 0;
        int y = (int) (Math.random() * (29 - 0) + 0) + 0;

        jedis.sadd("nicknames", nickname);                   // user nickname
        jedis.set(nickname + ":hp", "30");                          // user hp
        jedis.set(nickname + ":str", "3");
        jedis.set(nickname + ":x_pos", String.valueOf(x));     // first position
        jedis.set(nickname + ":y_pos", String.valueOf(y));     // first position
        jedis.set(nickname + "hp_potion", "1");
        jedis.set(nickname + "str_potion", "1");
        return "[Create User] " + "[nickname: " + nickname + ", hp:30, str:3, x_pos: "+x+", y_pos: "+y+"]";
    }

    public static synchronized String move(String nickname, int x, int y){
        jedis.set(nickname + ":x_pos", String.valueOf(x));
        jedis.set(nickname + ":y_pos", String.valueOf(y));
        return nickname + " move to " + "[" + x + ", " + y + "]";
    }

    public static synchronized String userAttack(String nickname){
        int str = Integer.parseInt(jedis.get(nickname+":str"));
        int curr_x = Integer.parseInt(jedis.get(nickname + ":x_pos"));
        int curr_y = Integer.parseInt(jedis.get(nickname + ":y_pos"));
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
                        Monster monster = MonsterManager.monsterMap.get(monsterId) ;
                        boolean isDead = monster.attacked(str);
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
        int hp = Integer.parseInt(jedis.get(nickname +":hp"));
        if(hp - monsterStr <= 0){
            jedis.set(nickname+":hp", String.valueOf(0));
            System.out.println(nickname + " was killed by a monster!");
            jedis.sadd("dead_user", nickname);
        }else {
            jedis.set(nickname + ":hp", String.valueOf(hp - monsterStr));
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
        List<String> list = new ArrayList<>(members);
        StringBuffer sb = new StringBuffer();

        for(String member : list){
            if (isDead(member)) {
                continue;
            }
            String xPos = jedis.get(member + ":x_pos");
            String yPos = jedis.get(member + ":y_pos");
            sb.append(member + " " + xPos + " " + yPos + "\n");
        }
        return sb.toString();
    }

    public static synchronized boolean checkUserExist(){
        Set<String> members = jedis.smembers("nicknames");
        if(members.size() == 0){
            return false;
        }
        return true;
    }

    public static synchronized boolean checkMonsterExist(){
        if(MonsterManager.monsterMap.size() == 0){
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

    public static synchronized int[] getPosition(String nickname){
        int x = Integer.parseInt(jedis.get(nickname+":x_pos"));
        int y = Integer.parseInt(jedis.get(nickname+":y_pos"));
        int[] pos = new int[2];
        pos[0] = x;
        pos[1] = y;
        return pos;
    }

    public static synchronized void getReward(String monsterId, String nickname){
        Monster monster = MonsterManager.monsterMap.get(monsterId);
        int hpPotion = monster.getHpPotion();
        int strPotion = monster.getStrPotion();
        int userHpPotion = Integer.parseInt(jedis.get(nickname+":hp_potion"));
        int userStrPotion = Integer.parseInt(jedis.get(nickname+":str_potion"));
        jedis.set(nickname + ":hp_potion", String.valueOf(hpPotion + userHpPotion));
        jedis.set(nickname + ":str_potion", String.valueOf(strPotion + userStrPotion));
    }
}
