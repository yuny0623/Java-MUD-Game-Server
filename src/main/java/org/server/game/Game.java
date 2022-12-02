package org.server.game;

import org.server.dto.CommandDto;
import org.server.game.monster.Monster;
import org.server.game.monster.MonsterManager;
import org.server.redistemplate.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

public final class Game{
    private static Game instance;
    public MonsterManager monsterManager;

    private Game(){
        System.out.println("Game Start.\n");
        monsterManager = new MonsterManager();
        monsterManager.start();
    }

    public static Game getInstance(){
        if(instance == null){
            instance = new Game();
        }
        return instance;
    }

    public synchronized String play(CommandDto commandDto){
        String nickname = commandDto.getNickname();
        String[] commands = commandDto.getCommand().split(" ");
        String result = null;

        switch (commands[0]){
            case "move":
                 int x = Integer.parseInt(commands[1]);
                 int y = Integer.parseInt(commands[2]);
                 int[] pos = RedisTemplate.getPosition(nickname);
                 int fixedX = 0;
                 int fixedY = 0;
                 if(x + pos[0] < 0){
                     fixedX = 0;
                 }else if(x + pos[0] > 29){
                     fixedX = 29;
                 }
                 if(y + pos[1] < 0){
                     fixedY = 0;
                 }else if(y + pos[1] > 29){
                     fixedY = 29;
                 }
                 result = RedisTemplate.move(nickname, fixedX, fixedY);
                 break;
            case "attack":
                result = RedisTemplate.userAttack(nickname);
                break;
            case "monsters":
                result = RedisTemplate.showMonsters();
                break;
            case "users":
                result = RedisTemplate.showUsers();
                break;
            case "chat":
                String to = commands[1];
                String content = commands[2];
                result = RedisTemplate.chat(nickname, to, content);
                break;
        }
        return result;
    }
}


