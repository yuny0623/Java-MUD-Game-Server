package org.server.game;

import org.server.dto.CommandDto;

public final class Game {
    private static Game instance;

    private Game(){

    }

    public static Game getInstance(){
        if(instance == null){
            instance = new Game();
        }
        return instance;
    }

    public void play(CommandDto commandDto){
        /*
            actual play logic
         */
    }
}


