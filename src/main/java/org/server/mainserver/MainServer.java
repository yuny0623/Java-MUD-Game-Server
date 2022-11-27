package org.server.mainserver;

import org.server.dto.CommandDto;
import org.server.game.Game;
import org.server.utils.ServerConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainServer {
    ServerSocket serverSocket;
    Socket socket;
     Game game;
    List<Thread> list;
    // List<Map<String, Map<String, Thread>>> userList;
    public MainServer(){
        list = new ArrayList<>();
        game = Game.getInstance();
        System.out.println("Main Server Created.");
    }

    public void giveAndTake(){
        try{
            serverSocket = new ServerSocket(ServerConfig.TCP_CONNECTION_DEFAULT_PORT);
            serverSocket.setReuseAddress(true);
            while(true){
                socket = serverSocket.accept();
                System.out.println("New Socket accepted.");
                ServerSocketThread thread = new ServerSocketThread(this, socket);
                addClient(thread);
                thread.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public synchronized void addClient(ServerSocketThread thread){
        list.add(thread);
        System.out.println("Client: 1 user added. Total: " + list.size());
    }

    public synchronized void removeClient(Thread thread){
        list.remove(thread);
        System.out.println("Client: 1 user removed. Total: " + list.size());
    }

    public synchronized void broadCasting(String str){
        for(int i = 0; i < list.size(); ++i){
            System.out.println("Broadcasting to client from Server: " + str);
            ServerSocketThread thread = (ServerSocketThread) list.get(i);
            thread.sendMessage(str);
        }
    }

    public synchronized void playGame(CommandDto commandDto){
        game.play(commandDto);
    }
}
