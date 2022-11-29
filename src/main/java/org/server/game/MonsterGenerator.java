package org.server.game;

public class MonsterGenerator extends Thread{
    public Game game;

    public MonsterGenerator(){
        this.game = Game.getInstance();
    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
