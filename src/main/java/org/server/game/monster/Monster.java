package org.server.game.monster;

import java.util.Random;

public class Monster {
    private int hp;
    private int str;
    private int hpPotion;
    private int strPotion;
    private int x;
    private int y;

    public Monster(){
        // (int) Math.random() * (최댓값-최소값+1) + 최소값
        this.hp = (int) (Math.random() * (10 - 5 + 1) + 5);
        this.str=  (int) (Math.random() * (5 - 3 + 1) + 3);

        this.hpPotion = (int) (Math.random() * (10 - 5 + 1) + 5);
        this.strPotion = (int) (Math.random() * (10 - 5 + 1) + 5);

        this.x = (int) (Math.random() * (29 - 0 + 1) + 0);
        this.y = (int) (Math.random() * (29 - 0 + 1) + 0);

        System.out.println("New Monster created.");
    }

    public void attacked(int str){
        this.hp -= str;
    }

    public void move(){
        int xx = (int) (Math.random() * (1 - (-1) + 1) + (-1));
        int yy = (int) (Math.random() * (1 - (-1) + 1) + (-1));

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
}
