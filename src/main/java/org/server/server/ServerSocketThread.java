package org.server.server;

import org.server.dto.CommandDto;
import org.server.dto.ResultDto;
import org.server.utils.JedisUtil;
import org.server.utils.JsonUtil;

import java.io.*;
import java.net.Socket;

public class ServerSocketThread extends Thread{
    Socket socket;
    Server server;
    BufferedReader in;
    PrintWriter out;
    String strIn;
    String threadName;
    String nickname;
    String command;

    public ServerSocketThread(Server server, Socket socket){
        this.server = server ;
        this.socket = socket;
        threadName = super.getName();
        System.out.printf("[Client] %s : entered.\n", socket.getInetAddress());
        System.out.printf("[Client] Thread Name: %s\n", threadName);
    }

    public void sendMessage(String str){
        out.println(str);
    }

    public void send(ResultDto resultDto){
        String command = resultDto.getCommand();
        String result = resultDto.getResult();
        String json = "";
        switch(command){
            case "move": case "attack": case "potion":
                json = JsonUtil.generateJson(result);
                System.out.println("[Client] " + result);
                server.broadCasting(json);
                break;
            case "monsters": case "users":
                json = JsonUtil.generateJsonByCommand(command, result);
                System.out.println("[Client] " + result);
                sendMessage(json);
                break;
            case "chat":
                break;
        }
    }

    public void processLogin(String strIn){
        command = JsonUtil.parseJson(strIn);
        nickname = command.split(" ")[1];
        boolean isLogin = server.login(nickname, this);
        if(isLogin){
            System.out.printf("[Client] %s Login Success!\n", nickname);
        }else{
            nickname = threadName;
        }
    }

    public void sendUserInfo(String nickname){
        String myInfo = JedisUtil.getMyInfo(nickname);
        if(!(myInfo.isBlank() || myInfo.isEmpty())){
            sendMessage(JsonUtil.generateJson(myInfo));
        }
    }

    public void saveUserInfo(String nickname){
        if(JedisUtil.isValidUser(nickname)){ // 이미 접속 기록이 존재하는 유저인 경우
            JedisUtil.renewalLogin(nickname); // 접속 유효시간 갱신
        }else { // 처음 접속하는 유저이거나 접속 정보가 만료된 유저인 경우
            String result = JedisUtil.createUser(nickname);
            System.out.println(result);
        }
        server.broadCasting(JsonUtil.generateJson(nickname + " has entered."));
    }

    public boolean isValidInput(String input){
        if(input == null || input.isEmpty() || input.isBlank()){
            return false;
        }
        return true;
    }

    @Override
    public void run(){
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            // 최초 input: nickname을 받음
            strIn = in.readLine();
            // 로그인 진행
            processLogin(strIn);

            // Redis 에 User 저장
            saveUserInfo(nickname);

            // 해당 유저에게 생성된 캐릭터 정보 전달
            sendUserInfo(nickname);

            // 게임 진행
            while(true){
                strIn = in.readLine();

                // 입력 유효성 검사
                if(!isValidInput(strIn)){
                    continue;
                }

                // 사망했을 경우 게임 진행 불가
                if(JedisUtil.isDead(nickname)){
                    sendMessage(JsonUtil.generateJson("You are Dead."));
                    continue;
                }

                command = JsonUtil.parseJson(strIn);

                if(command.equals("bot")){
                    System.out.printf("[Client] %s activate bot mode.\n", nickname);
                    server.broadCasting(JsonUtil.generateJson(nickname + " activate bot mode."));
                    continue;
                }
                if(command.equals("exit bot")){
                    System.out.printf("[Client] %s disabled bot mode.\n", nickname);
                    server.broadCasting(JsonUtil.generateJson(nickname + " disabled bot mode."));
                    continue;
                }
                CommandDto commandDto = new CommandDto(command, nickname);
                ResultDto resultDto = server.play(commandDto);
                send(resultDto);
            }
        }catch(IOException e){
            System.out.printf("[Client] %s : removed.\n", nickname);
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
