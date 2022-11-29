package org.server.game.monster;

import org.server.game.Game;

public class MonsterManager extends Thread{

    public Game game;


    public MonsterManager(){
        game = Game.getInstance();
    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 1초에 한번씩 monster 들이 움직임
            for(int i = 0; i < game.monsterList.size(); i++){
                Monster monster = game.monsterList.get(i);
                monster.move();
            }
        }
    }
}
