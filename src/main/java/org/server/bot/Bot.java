package org.server.bot;

import org.server.dto.CommandDto;
import org.server.game.Game;

import java.util.Random;

public class Bot extends Thread{

    public String nickname;
    public Game game;

    public Bot(String nickname){
        this.game = Game.getInstance();
        this.nickname = nickname;
    }

    @Override
    public void run(){
        while(true) {
            try {
                Thread.sleep(1000);
                String command = randomAction();
                CommandDto commandDto = new CommandDto(nickname, command);
                game.play(commandDto);
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
