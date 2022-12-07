package org.server.redistemplate;

import org.server.game.monster.Monster;
import org.server.game.monster.MonsterManager;
import org.server.mainserver.MainServer;
import org.server.utils.ServerConfig;
import redis.clients.jedis.*;

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
        jedis.expire(nickname, 5 * 60);
        return "[Create User] " + "[nickname: " + nickname + ", hp:30, str:3, x_pos: " + x + ", y_pos: " + y + "]";
    }

    public static synchronized String myInfo(String nickname){
        String result = "";
        if(!isValidUser(nickname)){
            return result;
        }
        StringBuffer sb = new StringBuffer();
        String userNickname = jedis.hget(nickname, "user_nickname");
        String hp = jedis.hget(nickname, "hp");
        String str = jedis.hget(nickname, "str");
        String xPos = jedis.hget(nickname, "x_pos");
        String yPos = jedis.hget(nickname, "y_pos");
        String hpPotion = jedis.hget(nickname, "hp_potion");
        String strPotion = jedis.hget(nickname, "str_potion");
        sb.append("userNickname: " + userNickname +"\n");
        sb.append("hp: " + hp +"\n");
        sb.append("str: " + str +"\n");
        sb.append("xPos: " + xPos +"\n");
        sb.append("yPos: " + yPos +"\n");
        sb.append("hpPotion: " + hpPotion +"\n");
        sb.append("strPotion: " + strPotion +"\n");
        return sb.toString();
    }

    public static synchronized void renewalLogin(String nickname){
        jedis.expire(nickname, 5 * 60);
    }

    public static synchronized String move(String nickname, int x, int y){
        if(!isValidUser(nickname)){
            return nickname + " is Invalid User.";
        }

        if(x < -3){
            x = -3;
        }else if(x > 3){
            x = 3;
        }
        if(y < -3){
            y = -3;
        }else if(y > 3){
            y = 3;
        }

        int[] pos = getPosition(nickname);

        int toX = 0;
        int toY = 0;

        toX = x + pos[0];
        toY = y + pos[1];

        if(toX < 0){
            toX = 0;
        }else if(toX > 29){
            toY = 29;
        }
        if(toY < 0){
            toX = 0;
        }else if(toY > 29){
            toY = 29;
        }

        jedis.hset(nickname, "x_pos", String.valueOf(toX));
        jedis.hset(nickname, "y_pos", String.valueOf(toY));
        return nickname + " move to " + "[" + toX + ", " + toY + "]";
    }

    public static synchronized String userAttack(String nickname){
        if(!isValidUser(nickname)){
            return nickname + " is Invalid User.";
        }
        int str = Integer.parseInt(jedis.hget(nickname, "str"));
        int extraStr = RedisTemplate.getExtraStr(nickname);
        int curr_x = Integer.parseInt(jedis.hget(nickname ,"x_pos"));
        int curr_y = Integer.parseInt(jedis.hget(nickname ,"y_pos"));
        if(!checkMonsterExist()){
            return nickname + " attack miss. No Monster exist.";
        }
        String monsters = RedisTemplate.showMonstersServer();
        int kills = 0;
        int gainHpPotion = 0;
        int gainStrPotion = 0;
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
                            int[] reward = getReward(nickname, monsterId);
                            monster.interrupt();
                            kills ++;
                            gainHpPotion += reward[0];
                            gainStrPotion += reward[1];
                        }
                    }
                }
            }
        }
        return nickname + " attacked a Monster with power of "
                + str + extraStr + " and kills "
                + kills + " Monsters and gain "
                + gainHpPotion +" hp potion, "
                + gainStrPotion + " str potion.";
    }

    public static synchronized void userAttacked(String nickname, int monsterStr){
        if(!isValidUser(nickname)){
           return;
        }
        int hp = Integer.parseInt(jedis.hget(nickname ,"hp"));
        if(hp - monsterStr <= 0){
            jedis.hset(nickname, "hp", String.valueOf(0));
            System.out.println(nickname + " was killed by a monster!");
            jedis.setex("dead_user:"+nickname, 5 * 60, nickname);
            MainServer.mainServer.sendMessage("Monster", nickname, "You die!");
        }else {
            jedis.hset(nickname ,"hp", String.valueOf(hp - monsterStr));
        }
    }

    public static synchronized boolean isDead(String nickname){
        return (jedis.get("dead_user:" + nickname) != null);
    }

    public static synchronized String showMonstersServer(){
        StringBuffer sb = new StringBuffer();
        for(String monsterId:  MonsterManager.monsterMap.keySet()){
            Monster monster = MonsterManager.monsterMap.get(monsterId);
            sb.append(monsterId + " " + monster.getX() + " " + monster.getY() + "\n");
        }
        return sb.toString();
    }

    public static synchronized String showMonsters(){
        StringBuffer sb = new StringBuffer();
        for(String monsterId:  MonsterManager.monsterMap.keySet()){
            Monster monster = MonsterManager.monsterMap.get(monsterId);
            sb.append("Monster " + monster.getX() + " " + monster.getY() + "\n");
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
            if(!isValidUser(memberNickname)){
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
        if(!isValidUser(from)){
            return to + " is Invalid User.";
        }
        if(!isValidUser(to)){
            return to + " is Invalid User.";
        }
        mainServer.sendMessage(from, to, content);
        return from + " send message.";
    }

    public synchronized void setMainServer(MainServer mainServer) {
        this.mainServer = mainServer;
    }

    public static synchronized int[] getPosition(String nickname){
        if(!isValidUser(nickname)){
            int[] pos = new int[2];
            pos[0] = 0;
            pos[1] = 0;
            return pos;
        }
        int x = Integer.parseInt(jedis.hget(nickname, "x_pos"));
        int y = Integer.parseInt(jedis.hget(nickname, "y_pos"));
        int[] pos = new int[2];
        pos[0] = x;
        pos[1] = y;
        return pos;
    }

    public static synchronized int[] getReward(String monsterId, String nickname){
        if(!isValidUser(nickname)){
            return new int[] {0, 0};
        }
        Monster monster = MonsterManager.monsterMap.get(monsterId);
        if(monster == null) {
            return new int[] {0, 0};
        }
        int monsterHpPotion = monster.getHpPotion();
        int monsterStrPotion = monster.getStrPotion();
        jedis.hincrBy(nickname, "hp_potion", monsterHpPotion);
        jedis.hincrBy(nickname, "str_potion", monsterStrPotion);
        MonsterManager.monsterMap.remove(monsterId);
        return new int[] {monsterHpPotion, monsterStrPotion};
    }

    public static synchronized int getExtraStr(String nickname){
        if(!isValidUser(nickname)){
            return 0;
        }
        String extraStr = jedis.get(nickname + ":extra_str");
        if(extraStr == null){
           return 0;
        }else {
            return Integer.parseInt(extraStr);
        }
    }

    public static synchronized boolean useHpPotion(String nickname){
        if(!isValidUser(nickname)){
            return false;
        }
        int hpPotion = Integer.parseInt(jedis.hget(nickname ,"hp_potion"));
        if(hpPotion > 0){
            jedis.hincrBy(nickname, "hp", 10);
            jedis.hincrBy(nickname, "hp_potion", -1);
            return true;
        }else{
            return false;
        }
    }

    public static synchronized boolean useStrPotion(String nickname){
        if(!isValidUser(nickname)){
            return false;
        }
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
        jedis.hincrBy(nickname, "str_potion", -1);
        return true;
    }

    public static synchronized int getUserHpPotion(String nickname){
        if(!isValidUser(nickname)){
            return 0;
        }
        return Integer.parseInt(jedis.hget(nickname, "hp_potion"));
    }

    public static synchronized int getUserStrPotion(String nickname){
        if(!isValidUser(nickname)){
            return 0;
        }
        return  Integer.parseInt(jedis.hget(nickname, "str_potion"));
    }

    public static synchronized void serverReset(){
        jedis.flushAll();
    }

    public static synchronized boolean isValidUser(String nickname){
        if(isContains("nicknames", nickname)){
            if(jedis.hget(nickname, "user_nickname") != null){
                return true;
            }else{
                jedis.srem("nicknames", nickname);
                return false;
            }
        }else {
            return false;
        }
    }

    public static synchronized boolean isContains(String setKey, String target){
        ScanParams scanParams = new ScanParams().count(100);
        String cur = ScanParams.SCAN_POINTER_START;
        boolean cycleIsFinished = false;
        boolean isContains = false;
        while(!cycleIsFinished){
            ScanResult<String> scanResult = jedis.sscan(setKey, cur, scanParams);
            List<String> resultList = scanResult.getResult();

            for(int i = 0; i < resultList.size(); i++){
                if(resultList.get(i).equals(target)){
                    isContains = true;
                    break;
                }
            }
            cur = scanResult.getStringCursor();
            if(cur.equals("0")){
                cycleIsFinished = true;
            }
        }
        return isContains;
    }
}
