package org.server.game.monster;

import org.server.config.ServerConfig;
import org.server.utils.GameUtil;
import org.server.utils.JedisUtil;

public class Monster extends Thread{
    private int hp;
    private int str;
    private int hpPotion;
    private int strPotion;
    private int x;
    private int y;
    private String monsterName;

    public Monster(String monsterName){
        this.monsterName = monsterName;

        this.hp = GameUtil.generateRandomNumber(5, 10);
        this.str=  GameUtil.generateRandomNumber(3, 5);

        this.hpPotion = GameUtil.generateRandomNumber(5, 10);
        this.strPotion = GameUtil.generateRandomNumber(5, 10);

        this.x = GameUtil.generateRandomNumber(0, 29);
        this.y = GameUtil.generateRandomNumber(0, 29);
    }

    public boolean receiveDamage(int str){
        this.hp -= str;
        if(this.hp <= 0){
            return true;
        }
        return false;
    }

    public void move(){
        int randomX = GameUtil.generateRandomNumber(-1, 1);
        int randomY = GameUtil.generateRandomNumber(-1, 1);

        if((0 <= this.x + randomX) && (this.x + randomX < 30) && (0 <= this.y + randomY) && (this.y + randomY < 30)){
            this.x += randomX;
            this.y += randomY;
        }
    }

    public int getHp(){
        return this.hp;
    }

    public int getStr(){
        return this.str;
    }

    public int getHpPotion(){
        return this.hpPotion;
    }

    public int getStrPotion(){
        return this.strPotion;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    @Override
    public void run(){
        try {
            while (!this.interrupted()) {
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    System.out.printf("[Monster] Monster killed so terminate [%s].\n", this.getName());
                    throw e;
                }
                if (!JedisUtil.isUserExist()) {
                    continue;
                }
                String users = JedisUtil.showUsers();
                if (users.isEmpty() || users.isBlank()) {
                    continue;
                }
                for (int i = 0; i < 9; i++) {
                    int x = ServerConfig.DX[i];
                    int y = ServerConfig.DY[i];
                    int attackX = this.getX() + x;
                    int attackY = this.getY() + y;
                    if ((0 <= attackX) && (attackX < 30) && (0 <= attackY) && (attackY < 30)) {
                        String[] userRow = users.split("\n");
                        for (String row : userRow) {
                            String[] vals = row.split(" ");
                            String nickname = vals[0];
                            if (JedisUtil.isDead(nickname)) {
                                continue;
                            }
                            int userX = Integer.parseInt(vals[1]);
                            int userY = Integer.parseInt(vals[2]);
                            if (userX == attackX && userY == attackY) {
                                System.out.printf("[Monster] Monster attacks [%s] in [%d,%d].\n", nickname, userX, userY);
                                JedisUtil.attackUser(nickname, this.getStr());
                            }
                        }
                    }
                }
            }
        }catch(InterruptedException e){
            System.out.println("[Monster] " + e.getMessage());
        }
    }
}
