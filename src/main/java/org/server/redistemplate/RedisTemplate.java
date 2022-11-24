package org.server.redistemplate;

import java.util.ArrayList;

public final class RedisTemplate {

    private static RedisTemplate instance;

    private RedisTemplate(){

    }

    public static RedisTemplate getInstance(){
        if(instance == null){
            instance = new RedisTemplate();
        }
        return instance;
    }

    public void createUser(){

    }

    public void move(){

    }

    public void attack(){

    }

    public void showMonsters(){

    }

    public ArrayList<String> showUsers(){
        return new ArrayList<>();
    }

    public void chat(){

    }

    public void activateBot(){

    }

    public void deactivateBot(){

    }
}
