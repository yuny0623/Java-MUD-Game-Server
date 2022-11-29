package org.server.game;

public class MonsterAttacker extends Thread{

    public Game game;

    public MonsterAttacker(){
        this.game = Game.getInstance();
    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // 5초에 한번씩 주변을 공격함.
            
        }
    }
}
