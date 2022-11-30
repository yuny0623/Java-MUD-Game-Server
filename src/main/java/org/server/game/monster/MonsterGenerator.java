package org.server.game.monster;

import org.server.game.Game;

public class MonsterGenerator extends Thread{
    public Game game;

    public MonsterGenerator(Game game){
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
            if(game.monsterList.size() < 10){
                int diff = 10 - game.monsterList.size();
                for(int i = 0; i < diff; i++){
                    game.monsterList.add(new Monster());
                }
            }
        }
    }
}
