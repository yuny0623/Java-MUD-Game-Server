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
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            // 로그인 진행
            strIn = in.readLine();
            command = JsonUtil.parseJson(strIn);
            nickname = command.split(" ")[1];
            boolean isLogin = server.login(nickname, this);
            if(isLogin){
                System.out.println("Login Success!");
            }else{
                nickname = threadName;
            }

            // Redis에 User 저장
            RedisTemplate.createUser(nickname);
            server.broadCasting(JsonUtil.generateJson(nickname + " has entered."));

            // 게임 진행
            while(true){
                strIn = in.readLine();
                // 사망했을 경우 게임 진행 불가
                if(RedisTemplate.isDead(nickname)){
                    sendMessage(JsonUtil.generateJson("You are Dead."));
                    continue;
                }
                command = JsonUtil.parseJson(strIn);
                if(command.equals("bot")){
                    server.broadCasting(JsonUtil.generateJson(nickname + " activate bot mode."));
                    continue;
                }
                if(command.equals("exit bot")){
                    server.broadCasting(JsonUtil.generateJson(nickname + "disabled bot mode."));
                    continue;
                }
                CommandDto commandDto = new CommandDto(command, nickname);

                String result = server.playGame(commandDto);
                String resultJson = JsonUtil.generateJson(result);

                System.out.println(result);
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
