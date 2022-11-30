package org.server.mainserver;

public class ServerAction implements Runnable{
    public ServerAction(){

    }

    @Override
    public void run(){
        System.out.println("hi2");
        MainServer server = new MainServer();
        server.giveAndTake();
    }
}
