package org.server.game.monster;

import org.server.redistemplate.RedisTemplate;

public class Monster extends Thread{
    private int hp;
    private int str;
    private int hpPotion;
    private int strPotion;
    private int x;
    private int y;

    private int[] dx = {-1, 0, 1, 1, 1, 0, -1, -1, 0};
    private int[] dy = {-1, -1, -1, 0, 1, 1, 1, 0, 0};

    public Monster(){
        this.hp = (int) (Math.random() * (10 - 5 + 1) + 5);
        this.str=  (int) (Math.random() * (5 - 3 + 1) + 3);

        this.hpPotion = (int) (Math.random() * (10 - 5 + 1) + 5);
        this.strPotion = (int) (Math.random() * (10 - 5 + 1) + 5);

        this.x = (int) (Math.random() * (29 - 0 + 1) + 0);
        this.y = (int) (Math.random() * (29 - 0 + 1) + 0);
    }

    public void attacked(int str){
        this.hp -= str;
    }

    public void move(){
        int xx = (int) (Math.random() * (1 - (-1) + 1)) + (-1);
        int yy = (int) (Math.random() * (1 - (-1) + 1)) + (-1);

        if((0 <= this.x + xx) && (this.x + xx < 30) && (0 <= this.y + yy) && (this.y + yy < 30)){
            this.x += xx;
            this.y += yy;
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
        int attack = 0;
        while(true){
            ++attack;
            try {
                Thread.sleep( 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if((attack / 5) == 1){
                System.out.println("Monster Attacks.");
                attack = 0;
                if(!RedisTemplate.checkUserExist()){
                    System.out.println("No Users Exist.");
                    continue;
                }
                for(int i = 0; i < 9; i++) {
                    int x = dx[i];
                    int y = dy[i];
                    int attackX = this.getX() + x;
                    int attackY = this.getY() + y;
                    String users = RedisTemplate.showUsers();
                    if ((0 <= attackX) && (attackX < 30) && (0 <= attackY) && (attackY < 30)) {
                        String[] userRow = users.split("\n");
                        for (String row : userRow) {
                            String[] vals = row.split(" ");
                            String nickname = vals[0];
                            if(RedisTemplate.isDead(nickname)){
                                continue;
                            }
                            int userX = Integer.parseInt(vals[1]);
                            int userY = Integer.parseInt(vals[2]);
                            if (userX == attackX && userY == attackY) {
                                System.out.println("[" + nickname + "] " + "User Attacked " + " -" + this.getStr());
                                RedisTemplate.userAttacked(nickname, this.getStr());
                            }
                        }
                    }
                }
            }
//            int originX = getX();
//            int originY = getY();
//            this.move();
//            int movedX = getX();
//            int movedY = getY();
//            System.out.println("[monster] moving... from [" + originX + "," + originY + "]  to [" + movedX + "," + movedY + "]");
        }
    }
}
