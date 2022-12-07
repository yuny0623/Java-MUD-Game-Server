package org.server.mainserver;

import org.server.dto.CommandDto;
import org.server.dto.ResultDto;
import org.server.redistemplate.RedisTemplate;
import org.server.utils.JsonUtil;

import java.io.*;
import java.net.Socket;

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
        System.out.printf("%s : entered.\n", socket.getInetAddress());
        System.out.printf("Thread Name: %s\n", threadName);
    }

    public void sendMessage(String str){
        out.println(str);
    }

    public void send(ResultDto resultDto){
        String command = resultDto.getCommand();
        String json = "";
        switch(command){
            case "move":
                json = JsonUtil.generateJson(resultDto.getResult());
                server.broadCasting(json);
                break;
            case "attack":
                json = JsonUtil.generateJson(resultDto.getResult());
                server.broadCasting(json);
                break;
            case "monsters":
                json = JsonUtil.generateJson(resultDto.getResult());
                sendMessage(json);
                break;
            case "users":
                json = JsonUtil.generateJson(resultDto.getResult());
                sendMessage(json);
                break;
            case "chat":
                break;
            case "potion":
                json = JsonUtil.generateJson(resultDto.getResult());
                server.broadCasting(json);
                break;
        }
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
                System.out.printf("%s Login Success!\n", nickname);
            }else{
                nickname = threadName;
            }

            // Redis에 User 저장
            if(RedisTemplate.isValidUser(nickname)){ // 이미 접속 기록이 존재하는 유저인 경우
                RedisTemplate.renewalLogin(nickname); // 접속 유효시간 갱신
            }else { // 처음 접속하는 유저이거나 접속 정보가 만료된 유저인 경우
                RedisTemplate.createUser(nickname);
            }
            server.broadCasting(JsonUtil.generateJson(nickname + " has entered."));

            // 해당 유저에게 생성된 캐릭터 정보 전달
            String myInfo = RedisTemplate.myInfo(nickname);
            if(!(myInfo.isBlank() || myInfo.isEmpty())){
                sendMessage(JsonUtil.generateJson(myInfo));
            }

            // 게임 진행
            while(true){
                strIn = in.readLine();

                if(strIn.equals("ping")){
                    // health check
                    sendMessage("pong");
                }

                if(strIn == null){
                    continue;
                }

                // 사망했을 경우 게임 진행 불가
                if(RedisTemplate.isDead(nickname)){
                    sendMessage(JsonUtil.generateJson("You are Dead."));
                    continue;
                }

                command = JsonUtil.parseJson(strIn);

                if(command.equals("bot")){
                    System.out.printf("%s activate bot mode.\n", nickname);
                    server.broadCasting(JsonUtil.generateJson(nickname + " activate bot mode."));
                    continue;
                }
                if(command.equals("exit bot")){
                    System.out.printf("%s disabled bot mode.\n", nickname);
                    server.broadCasting(JsonUtil.generateJson(nickname + " disabled bot mode."));
                    continue;
                }
                CommandDto commandDto = new CommandDto(command, nickname);

                ResultDto resultDto = server.playGame(commandDto);
                System.out.println(resultDto.getResult());
                send(resultDto);
            }
        }catch(IOException e){
            System.out.printf("%s : removed.\n", nickname);
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
