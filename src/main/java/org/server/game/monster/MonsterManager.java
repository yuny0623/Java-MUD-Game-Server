package org.server.game.monster;

import org.server.game.Game;

import java.util.ArrayList;
import java.util.List;

public class MonsterManager extends Thread{
    public static List<Monster> monsterList = new ArrayList<>();
    public MonsterManager(){
        System.out.println("Start MonsterManager.\n");
        for(int i = 0; i < 10; i++){
            System.out.println("New Monster created.");
            Monster monster = new Monster();
            monsterList.add(monster);
            monster.start();
        }
    }

    public static List<Monster> getMonsterList(){
        return monsterList;
    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(monsterList.size() < 10){
                int diff = 10 - monsterList.size();
                for(int i = 0; i < diff; i++){
                    System.out.println("New Monster Created.");
                    Monster monster = new Monster();
                    monsterList.add(new Monster());
                    monster.start();
                }
            }
        }
    }
}
