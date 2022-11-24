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

    public synchronized void createUser(){

    }

    public synchronized void move(){

    }

    public synchronized void attack(){

    }

    public synchronized void showMonsters(){

    }

    public synchronized ArrayList<String> showUsers(){
        return new ArrayList<>();
    }

    public synchronized void chat(){

    }

    public synchronized void activateBot(){

    }

    public synchronized void deactivateBot(){

    }
}
