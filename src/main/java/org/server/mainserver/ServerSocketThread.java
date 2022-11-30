package org.server.mainserver;

import org.server.dto.CommandDto;
import org.server.redistemplate.RedisTemplate;
import org.server.utils.JsonUtil;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ServerSocketThread extends Thread{
    Socket socket;
    MainServer server;
    BufferedReader in;
    PrintWriter out;
    String strIn;
    String threadName;
    String nickname;
    JsonUtil jsonUtil;
    String command;

    public ServerSocketThread(MainServer server, Socket socket){
        this.server = server ;
        this.socket = socket;
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

            // 로그인 진행
            strIn = in.readLine();
            command = jsonUtil.parseJson(strIn);
            nickname = command.split(" ")[1];
            boolean isLogin = server.login(nickname, this);
            if(isLogin){
                System.out.println("Login Success!");
            }else{
                nickname = threadName;
            }
            // Redis에 유저 저장
            RedisTemplate.createUser(nickname);
            server.broadCasting(jsonUtil.generateJson(nickname + " has entered."));

            // 게임 진행
            while(true){
                strIn = in.readLine();
                command = jsonUtil.parseJson(strIn);
                CommandDto commandDto = new CommandDto(command, nickname);

                String json = jsonUtil.generateJson(nickname + ":" + command);
                String result = server.playGame(commandDto);
                String resultJson = jsonUtil.generateJson(nickname + ":" + result);

                server.broadCasting(json);
                server.broadCasting(resultJson);
            }
        }catch(IOException e){
            System.out.println(nickname + ": removed.");
            server.removeClient(nickname, this);
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
