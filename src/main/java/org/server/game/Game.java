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
        System.out.println("[Game] Game Start.\n");
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
                 result = RedisTemplate.move(nickname, x, y);
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
            case "potion":
                String item = commands[1];
                if(item.equals("hp")){
                    boolean isUse = RedisTemplate.useHpPotion(nickname);
                    if(isUse){
                        result = nickname + " recover 10 hp.";
                    }else{
                        result = nickname + " No hp potion left.";
                    }
                    break;
                }else if(item.equals("str")){
                    boolean isUse = RedisTemplate.useStrPotion(nickname);
                    if(isUse){
                        result = nickname + " increase 3 str.";
                    }else{
                        result = nickname + " No Str potion left";
                    }
                    break;
                }
                break;
            }
        return result;
    }
}


