package org.server.game.monster;

import org.server.game.Game;

import java.util.*;

public class MonsterManager extends Thread{
    public static Map<String, Monster> monsterMap = new HashMap<>();

    public MonsterManager(){
        System.out.println("Start MonsterManager.\n");
        for(int i = 0; i < 10; i++){
            System.out.println("New Monster created.");
            Monster monster = new Monster();
            this.monsterMap.put(UUID.randomUUID().toString(), monster);
            monster.start();
        }
    }

    public Map<String, Monster> getMonsterMap(){
        return this.monsterMap;
    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(this.monsterMap.size() < 10){
                int diff = 10 - this.monsterMap.size();
                for(int i = 0; i < diff; i++){
                    System.out.println("New Monster Created.");
                    Monster monster = new Monster();
                    this.monsterMap.put(UUID.randomUUID().toString(), new Monster());
                    monster.start();
                }
            }
        }
    }
}
