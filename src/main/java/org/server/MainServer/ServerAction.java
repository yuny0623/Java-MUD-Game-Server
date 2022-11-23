package org.server.MainServer;

public class ServerAction implements Runnable{
    public ServerAction(){

    }

    @Override
    public void run(){
        MainServer server = new MainServer();
        server.giveAndTake();
    }
}
