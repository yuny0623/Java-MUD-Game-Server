package org.server.game.monster;

import org.server.game.Game;
import org.server.redistemplate.RedisTemplate;

public class MonsterAttacker extends Thread{

    public Game game;
    public RedisTemplate redisTemplate;

    int[] dx = {-1, 0, 1, 1, 1, 0, -1, -1};
    int[] dy = {-1, -1, -1, 0, 1, 1, 1, 0};

    public MonsterAttacker(){
        this.game = Game.getInstance();
        this.redisTemplate = RedisTemplate.getInstance();
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
            String users = redisTemplate.showUsers();
            String[] userLocations = users.split("\n");

            for(int i = 0; i < game.monsterList.size(); i++) {
                Monster monster = game.monsterList.get(i);
                int monsterX = monster.getX();
                int monsterY = monster.getY();
                int monsterStr = monster.getStr();

                for (int j = 0; j < userLocations.length; j++) {
                    String[] row = userLocations[j].split(" ");
                    String nickname = row[0];
                    int userX = Integer.parseInt(row[1]);
                    int userY = Integer.parseInt(row[2]);

                    for(int k = 0 ; k < 8; k ++){
                        int movedX = monsterX + dx[i];
                        int movedY = monsterY + dy[i];
                        if(0 <= movedX && movedY < 30 && 0 <= movedY && movedY < 30){
                            if(userX == movedX && userY == movedY){
                                redisTemplate.attacked(nickname, monsterStr);
                            }
                        }
                    }
                }
            }
        }
    }
}
