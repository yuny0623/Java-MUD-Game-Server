package org.server.game;

import org.server.dto.CommandDto;

public final class Game {
    private static Game instance;

    private Game(){
        System.out.println("Game created.");
    }

    public static Game getInstance(){
        if(instance == null){
            instance = new Game();
        }
        return instance;
    }

    public synchronized void play(CommandDto commandDto){
        /*
            actual play logic, call redisTemplate method.
         */
        String nickname = commandDto.getNickname();
        String command = commandDto.getCommand();

        switch (command){
            case "move":
                 break;
            case "attack":
                break;
        }
    }
}


