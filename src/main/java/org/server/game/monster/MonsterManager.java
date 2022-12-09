package org.server.game.monster;

import org.server.game.Game;
import org.server.utils.GameUtil;

import java.util.*;

public class MonsterManager extends Thread{
    public static Map<String, Monster> monsterMap = new HashMap<>();

    public MonsterManager(){
        System.out.println("[MonsterFactory] Start MonsterManager.\n");
        generateMonsters();
        System.out.println("[MonsterFactory] Create 10 Monsters.");
    }

    public void generateMonsters(){
        for(int i = 0; i < 10; i++){
            String monsterName = GameUtil.generateRandomString();
            Monster monster = new Monster(monsterName);
            this.monsterMap.put(monsterName, monster);
            monster.start();
        }
    }

    public void generateNewMonsters(){
        int diff;
        if(this.monsterMap.size() < 10){
            diff = 10 - this.monsterMap.size();
            for(int i = 0; i < diff; i++){
                String monsterName = GameUtil.generateRandomString();
                Monster monster = new Monster(monsterName);
                this.monsterMap.put(monsterName, monster);
                monster.start();
            }
            System.out.printf("[MonsterFactory] Generate new %d Monsters.\n", diff);
        }
    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            generateNewMonsters();
        }
    }
}
