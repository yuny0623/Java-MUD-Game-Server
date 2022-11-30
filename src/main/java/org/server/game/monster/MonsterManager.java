package org.server.game.monster;

import org.server.game.Game;

public class MonsterManager extends Thread{

    public Game game;

    public MonsterManager(Game game){
        System.out.println("start MonsterManager.");
        this.game = game;
    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 3초에 한번씩 monster 들이 움직임
            for(int i = 0; i < game.monsterList.size(); i++){
                Monster monster = game.monsterList.get(i);
                int originX = monster.getX();
                int originY = monster.getY();
                monster.move();
                int movedX = monster.getX();
                int movedY = monster.getY();
                System.out.println("[monster-" + i + "] moving... from [" + originX + "," + originY + "]  to [" + movedX + "," + movedY + "]");
            }
            System.out.println();
        }
    }
}
