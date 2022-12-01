package org.server.game.monster;

import java.util.Random;

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

        if(0 <= this.x + xx && this.x + xx < 30 && 0 <= this.y + yy && this.y + yy < 30){
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
            attack ++;
            try {
                Thread.sleep( 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if((int) (attack / 5) == 1){
                attack = 0;
                for(int i = 0; i < 9; i++){
                    int x = dx[i];
                    int y = dy[i];
                    int xx = getX() + x;
                    int yy = getY() + y;
                    if(0<= xx && xx < 30 && 0<= yy && yy < 30){
                        /*
                            check some user is in this range
                         */
                    }
                }
                continue;
            }
            int originX = getX();
            int originY = getY();
            this.move();
            int movedX = getX();
            int movedY = getY();
            System.out.println("[monster] moving... from [" + originX + "," + originY + "]  to [" + movedX + "," + movedY + "]");
        }
    }
}
