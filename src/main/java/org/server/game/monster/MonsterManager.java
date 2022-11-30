package org.server.game.monster;

import org.server.game.Game;

public class MonsterManager extends Thread{

    public Game game;


    public MonsterManager(Game game){
        this.game = game;
    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 1초에 한번씩 monster 들이 움직임
            for(int i = 0; i < game.monsterList.size(); i++){
                Monster monster = game.monsterList.get(i);
                monster.move();
            }
        }
    }
}
