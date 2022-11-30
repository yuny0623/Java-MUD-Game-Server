package org.server.game;

import org.server.dto.CommandDto;
import org.server.game.monster.Monster;
import org.server.game.monster.MonsterAttacker;
import org.server.game.monster.MonsterGenerator;
import org.server.game.monster.MonsterManager;
import org.server.redistemplate.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

public final class Game{
    private static Game instance;
    public List<Monster> monsterList;
    public MonsterGenerator monsterGenerator;
    public MonsterManager monsterManager;
    public MonsterAttacker monsterAttacker;

    private Game(){
        monsterList = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            System.out.println("New Monsters Created.");
            monsterList.add(new Monster());
        }
        monsterGenerator = new MonsterGenerator(this);
        monsterManager = new MonsterManager(this);
        monsterAttacker = new MonsterAttacker(this);
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
        }
        return result;
    }
}


