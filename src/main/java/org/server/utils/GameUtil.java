package org.server.utils;

import org.server.game.Game;

public final class GameUtil {

    public GameUtil(){

    }

    public static synchronized int generateRandomNumber(int min, int max){
        return (int) (Math.random() * (max - min + 1)) + min;
    }
}
