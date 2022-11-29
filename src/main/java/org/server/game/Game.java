package org.server.game;

import org.server.dto.CommandDto;
import org.server.redistemplate.RedisTemplate;

import java.util.List;

public final class Game{
    private static Game instance;
    public static RedisTemplate redisTemplate;

    public List<Monster> monsterList;

    public MonsterGenerator monsterGenerator;
    public MonsterManager monsterManager;
    public MonsterAttacker monsterAttacker;

    private Game(){
        redisTemplate = RedisTemplate.getInstance();
        for(int i = 0; i < 10; i++){
            monsterList.add(new Monster());
        }

        monsterGenerator = new MonsterGenerator();
        monsterManager = new MonsterManager();
        monsterAttacker = new MonsterAttacker();

        monsterGenerator.start();
        monsterManager.start();
        monsterAttacker.start();

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
        String[] commands = commandDto.getCommand().split(" ");
        String result = null;

        switch (commands[0]){
            case "move":
                 int x = Integer.parseInt(commands[1]);
                 int y = Integer.parseInt(commands[2]);
                 result = redisTemplate.move(nickname, x, y);
                 break;
            case "attack":
                result = redisTemplate.attack(nickname);
                break;
            case "monsters":
                result = redisTemplate.showMonsters();
                break;
            case "users":
                result = redisTemplate.showUsers();
                break;
            case "chat":
                String to = commands[1];
                String content = commands[2];
                result = redisTemplate.chat(nickname, to, content);
                break;
            case "bot":
                result = redisTemplate.activateBot(nickname);
                break;
            case "exit":
                result = redisTemplate.deactivateBot(nickname);
                break;
        }
        return result;
    }
}


