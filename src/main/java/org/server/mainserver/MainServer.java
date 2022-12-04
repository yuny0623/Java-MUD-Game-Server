package org.server.mainserver;

import org.server.dto.CommandDto;
import org.server.game.Game;
import org.server.redistemplate.RedisTemplate;
import org.server.utils.ServerConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class MainServer extends Thread{
    ServerSocket serverSocket;
    Socket socket;
    Game game;
    Map<String, Thread> userList;

    public MainServer(){
        System.out.println("Main Server Start.\n");

        userList = new HashMap<>();
        game = Game.getInstance();

        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setMainServer(this);
    }

    @Override
    public void run(){
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
        userList.put(thread.getName(), thread);
        System.out.println("Client: 1 user added. Total: " + userList.size());
    }

    public synchronized void removeClient(String nickname, Thread thread){
        ServerSocketThread foundThread = (ServerSocketThread) userList.remove(nickname);
        if(foundThread == null){
            userList.remove(thread.getName());
        }
        System.out.println("Client: 1 user removed. Total: " + userList.size());
    }

    public synchronized void broadCasting(String str){
        Set<String> keys = userList.keySet();
        for(String key: keys){
            ServerSocketThread thread = (ServerSocketThread) userList.get(key);
            thread.sendMessage(str);
        }
    }

    public synchronized void sendMessage(String sender, String receiver, String message){
        ServerSocketThread thread = (ServerSocketThread) userList.get(receiver);
        if(thread != null) {
            thread.sendMessage("["+sender+" > "+receiver+"] " + message);
        }else{
            System.out.println("[Chat] No User Exist.");
        }
    }

    public synchronized String playGame(CommandDto commandDto){
        return game.play(commandDto);
    }

    public synchronized boolean login(String nickname, Thread thread){
        ServerSocketThread foundThread = (ServerSocketThread) userList.get(nickname);
        if(foundThread != null){
            userList.remove(nickname);
            userList.put(nickname, thread);
            return true;
        }
        foundThread = (ServerSocketThread) userList.get(thread.getName());
        if(foundThread == null){
            return false;
        }
        userList.put(nickname, thread);
        userList.remove(thread.getName());
        Set<String> keys = userList.keySet();
        int i = 0;
        for(String key: keys){
            System.out.println("[" + i +".LoginUser] " + key +":" + userList.get(key));
            i++;
        }
        return true;
    }
}
