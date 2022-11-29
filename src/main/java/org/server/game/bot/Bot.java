package org.server.game.bot;

import java.util.Random;

public class Bot extends Thread{

    public String nickname;

    public Bot(String nickname){
        this.nickname = nickname;
    }

    @Override
    public void run(){
        while(true) {
            try {
                Thread.sleep(1000);
                String action = randomAction();
                /*
                    actual game play
                 */
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String randomAction(){
        Random random = new Random();
        int x;
        int y;
        String action = null;
        int randomCommand = random.nextInt(2);
        switch(randomCommand){
            case 0:
                x = random.nextInt(30);
                y = random.nextInt(30);
                action = "move " + x + " " + y;
                break;
            case 1:
                action = "attack";
                break;
        }
        return action;
    }
}
