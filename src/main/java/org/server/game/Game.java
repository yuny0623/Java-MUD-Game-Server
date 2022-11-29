package org.server.game;

import org.server.dto.CommandDto;
import org.server.redistemplate.RedisTemplate;

public final class Game extends Thread{
    private static Game instance;
    public static RedisTemplate redisTemplate;

    private Game(){
        redisTemplate = RedisTemplate.getInstance();
        System.out.println("Game created.");
    }

    public static Game getInstance(){
        if(instance == null){
            instance = new Game();
        }
        return instance;
    }

    public synchronized String play(CommandDto commandDto){
        String nickname = commandDto.getNickname();
        String firstCommand = commandDto.getCommand().split(" ")[0];

        String result = null;
        switch (firstCommand){
            case "move":
                 break;
            case "attack":
                break;
        }

        return result;
    }

    public synchronized void startBot(String nickname){
        redisTemplate.activateBot(nickname);
    }

    public synchronized void exitBot(String nickname){
        redisTemplate.deactivateBot(nickname);
    }
}


