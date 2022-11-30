package org.server.mainserver;

public class ServerAction implements Runnable{
    public ServerAction(){

    }

    @Override
    public void run(){
        MainServer server = new MainServer();
        server.giveAndTake();
    }
}
