package org.server.game.monster;

import org.server.game.Game;

import java.util.ArrayList;
import java.util.List;

public class MonsterManager extends Thread{
    public Game game;
    public List<Monster> monsterList;
    public MonsterManager(Game game){
        System.out.println("Start MonsterManager.\n");
        monsterList = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            System.out.println("New Monster created.");
            Monster monster = new Monster();
            monsterList.add(monster);
            monster.start();
        }
        System.out.println();
        this.game = game;
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
