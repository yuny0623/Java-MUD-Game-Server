package org.server.mainserver;

import org.server.dto.CommandDto;
import org.server.redistemplate.RedisTemplate;
import org.server.utils.JsonUtil;

import java.io.*;
import java.net.Socket;

public class ServerSocketThread extends Thread{
    Socket socket;
    MainServer server;
    BufferedReader in;
    PrintWriter out;
    RedisTemplate redisTemplate;
    String strIn;
    String threadName;
    String nickname;
    JsonUtil jsonUtil;
    String command;

    public ServerSocketThread(MainServer server, Socket socket){
        this.server = server ;
        this.socket = socket;
        redisTemplate = RedisTemplate.getInstance();
        threadName = super.getName();
        System.out.println(socket.getInetAddress() + ": entered.");
        System.out.println("Thread Name: " + threadName);
    }

    public void
    sendMessage(String str){
        out.println(str);
    }

    @Override
    public void run(){
        try{
            jsonUtil = JsonUtil.getInstance();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            strIn = in.readLine();
            System.out.println(strIn);
            command = jsonUtil.parseJson(strIn);
            nickname = command.split(" ")[1];
            /*
                logic
                1. 참여하고 있는 thread 중에 중복 nickname 있으면 이전 접속 deprecate
             */
            server.broadCasting(jsonUtil.generateJson(nickname + " has entered."));
            while(true){
                strIn = in.readLine();
                command = jsonUtil.parseJson(strIn);
                CommandDto commandDto = new CommandDto(command, nickname);
                server.playGame(commandDto);

                String json = jsonUtil.generateJson(nickname + ":" + command);
                server.broadCasting(json);
            }
        }catch(IOException e){
            System.out.println(nickname + ": removed.");
            server.removeClient(this);
        }finally{
            try{
                socket.close();
                out.close();
                in.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
