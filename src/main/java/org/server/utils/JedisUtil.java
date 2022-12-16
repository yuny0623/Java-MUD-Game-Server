package org.server.utils;

import org.server.config.ServerConfig;
import org.server.game.monster.Monster;
import org.server.game.monster.MonsterManager;
import org.server.server.Server;
import redis.clients.jedis.*;

import java.util.*;

public final class JedisUtil {
    private static final JedisPool pool = new JedisPool(ServerConfig.JEDIS_DEFAULT_IP, Protocol.DEFAULT_PORT);
    private static final Jedis jedis = pool.getResource();
    private static Server server;

    public JedisUtil(){

    }

    public static synchronized String createUser(String nickname){
        int x = GameUtil.generateRandomNumber(0, 29);
        int y = GameUtil.generateRandomNumber(0, 29);
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

    public static synchronized String getMyInfo(String nickname){
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
        renewalLogin(nickname);

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

        int[] pos = getUserXYPosition(nickname);

        int toX = 0;
        int toY = 0;

        toX = x + pos[0];
        toY = y + pos[1];

        if(toX < 0){
            toX = 0;
        }else if(toX > 29){
            toX = 29;
        }
        if(toY < 0){
            toY = 0;
        }else if(toY > 29){
            toY = 29;
        }

        jedis.hset(nickname, "x_pos", String.valueOf(toX));
        jedis.hset(nickname, "y_pos", String.valueOf(toY));
        return nickname + " move to " + "[" + toX + ", " + toY + "]";
    }

    public static synchronized String attackMonster(String nickname){
        if(!isValidUser(nickname)){
            return nickname + " is Invalid User.";
        }
        renewalLogin(nickname);
        int str = getUserStr(nickname);
        int extraStr = JedisUtil.getExtraStr(nickname);
        int userX = getUserXPosition(nickname);
        int userY = getUserYPosition(nickname);
        if(!isMonsterExist()){
            return nickname + " attack miss. No Monster exist.";
        }
        String monsters = JedisUtil.showMonstersServer();
        String[] monsterRow = monsters.split("\n");
        int kills = 0;
        int gainHpPotion = 0;
        int gainStrPotion = 0;
        for(int i = 0; i < 9; i++){
            int attackX = ServerConfig.DX[i] + userX;
            int attackY = ServerConfig.DY[i] + userY;
            if(0 <= attackX && attackX < 30 && 0 <= attackY && attackY < 30){
                for(String row: monsterRow){
                    String[] val = row.split(" ");
                    String monsterId = val[0];
                    int monsterX = Integer.parseInt(val[1]);
                    int monsterY = Integer.parseInt(val[2]);
                    if(monsterX == attackX && monsterY == attackY){
                        Monster monster = MonsterManager.monsterMap.get(monsterId);
                        boolean isDead = monster.receiveDamage(str + extraStr);
                        if(isDead) {
                            int[] reward = getReward(monsterId, nickname);
                            kills ++;
                            gainHpPotion += reward[0];
                            gainStrPotion += reward[1];
                            monster.interrupt();
                            return nickname + " attack a Monster with power of "
                                    + (str + extraStr) + " and kills "
                                    + kills + " Monsters and gain "
                                    + gainHpPotion +" hp potion, "
                                    + gainStrPotion + " str potion.";
                        }
                    }
                }
            }
        }
        return nickname + " attack a Monster with power of " + (str + extraStr) +".";
    }

    public static synchronized void attackUser(String nickname, int monsterStr){
        if(!isValidUser(nickname)){
           return;
        }
        renewalLogin(nickname);
        int hp = Integer.parseInt(jedis.hget(nickname ,"hp"));
        if(hp - monsterStr <= 0){
            jedis.hset(nickname, "hp", String.valueOf(0));
            System.out.println("[Monster]" + nickname + " is killed by a monster!");
            jedis.setex("dead_user:" + nickname, 1 * 60, nickname);
            Server.server.sendMessage("Monster", nickname, "You die!");
        }else {
            jedis.hset(nickname ,"hp", String.valueOf(hp - monsterStr));
            Server.server.sendMessage("Monster", nickname, "You are attacking by Monster!");
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
        Set<String> monsterIdSet = MonsterManager.monsterMap.keySet();
        List<String> monsterIdList = new ArrayList<>(monsterIdSet);
        for(int i = monsterIdList.size() - 1; i >= 0; i --){
            Monster monster = MonsterManager.monsterMap.get(monsterIdList.get(i));
            if(monster.getHp() <= 0){
                if(monster.isAlive()){
                    monster.interrupt();
                }
                MonsterManager.monsterMap.remove(monsterIdList.get(i));
                continue;
            }
            sb.append("Monster " + monster.getX() + " " + monster.getY() + "\n");
        }
        return sb.toString();
    }

    public static synchronized String showUsers(){
        Set<String> members = jedis.smembers("nicknames");
        StringBuffer sb = new StringBuffer();
        for(String memberNickname : members){
            if(isDead(memberNickname)) {
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

    public static synchronized boolean isUserExist(){
        Set<String> members = jedis.smembers("nicknames");
        return members.size() != 0;
    }

    public static synchronized boolean isMonsterExist(){
        return MonsterManager.monsterMap.size() != 0;
    }

    public static synchronized String chat(String from, String to, String content){
        if(!isValidUser(from)){
            return from + " is Invalid User.";
        }
        if(!isValidUser(to)){
            return to + " is Invalid User.";
        }
        renewalLogin(from);
        server.sendMessage(from, to, content);
        return from + " send message.";
    }

    public synchronized void setMainServer(Server server) {
        this.server = server;
    }

    public static synchronized int[] getUserXYPosition(String nickname){
        if(!isValidUser(nickname)){
            return new int[] {0, 0};
        }
        return new int[] {getUserXPosition(nickname), getUserYPosition(nickname)};
    }

    public static synchronized int getUserXPosition(String nickname){
        return Integer.parseInt(jedis.hget(nickname, "x_pos"));
    }

    public static synchronized int getUserYPosition(String nickname){
        return Integer.parseInt(jedis.hget(nickname, "y_pos"));
    }

    public static synchronized int getUserStr(String nickname){
        return Integer.parseInt(jedis.hget(nickname, "str"));
    }

    public static synchronized int[] getReward(String monsterId, String nickname){
        renewalLogin(nickname);
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
        renewalLogin(nickname);
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
        renewalLogin(nickname);
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
        renewalLogin(nickname);
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

    public static synchronized void clearRedis(){
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
