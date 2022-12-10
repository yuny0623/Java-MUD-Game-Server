package org.server.http;

import org.server.config.ServerConfig;
import org.server.dto.CommandDto;
import org.server.dto.ResultDto;
import org.server.server.Server;
import org.server.utils.JedisUtil;
import org.server.utils.JsonUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RestServer extends Thread{

    ServerSocket serverSocket;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String strIn;
    Server server;

    public RestServer(Server server){
        this.server = server;
        System.out.println("[Rest Server] Start Rest Server.\n");
    }

    @Override
    public void run(){
        try{
            serverSocket = new ServerSocket(ServerConfig.HTTP_CONNECTION_DEFAULT_PORT);
            serverSocket.setReuseAddress(true);
            while(true){
                socket = serverSocket.accept();
                System.out.println("[Rest Server] Start Socket.");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                StringBuffer sb = new StringBuffer();
                while(true){
                    strIn = in.readLine();
                    if(strIn == null || strIn.isBlank() || strIn.isEmpty()){
                        break;
                    }
                    sb.append(strIn+"\n");
                }

                int contentLength = httpRequestParser(sb.toString());
                if(contentLength == -1){
                    System.out.println("[Rest Server] [Error] Cannot parse HTTP request.");
                    continue;
                }

                char[] contentBuffer = new char[contentLength];
                in.read(contentBuffer);
                System.out.println(contentBuffer);

                // play game
                play(String.valueOf(contentBuffer));

                System.out.println("[Rest Server] Close start.");
                socket.close();
            }
        }catch(IOException e)   {
            e.printStackTrace();
        }
    }

    public int httpRequestParser(String data){
        Map<String, String> httpRequestMap = new HashMap<>();
        String[] dataRow = data.split("\n");
        String requestType = dataRow[0].split(" ")[0];
        if(!(requestType.equals("POST") || requestType.equals("GET"))){
            return -1;
        }
        for(int i = 1; i < dataRow.length; i++){
            String[] row = dataRow[i].split(" ");
            httpRequestMap.put(row[0], row[1]);
        }
        return Integer.parseInt(httpRequestMap.get("Content-Length:"));
    }

    public void play(String json){
        Map<String, String> jsonMap = JsonUtil.parseHttpJson(json);
        String command = jsonMap.get("command");
        String nickname = jsonMap.get("nickname");
        CommandDto commandDto = new CommandDto(command, nickname);
        ResultDto resultDto = server.play(commandDto);
        send(resultDto);
    }

    public String buildHttpResponse(String data){
        String httpResponse = ServerConfig.HTTP_VERSION + " " + ServerConfig.HTTP_200_OK + ServerConfig.HTTP_NEW_LINE + data;
        return httpResponse;
    }

    public void sendHttpResponse(String str){
        out.println(buildHttpResponse(str));
    }

    public void send(ResultDto resultDto){
        String command = resultDto.getCommand();
        String result = resultDto.getResult();
        String json = "";
        switch(command){
            case "move":
                json = JsonUtil.generateJson(result);
                System.out.println("[REST Client] " + result);
                sendHttpResponse(json);
                break;
            case "attack":
                json = JsonUtil.generateJson(result);
                System.out.println("[REST Client] " + result);
                sendHttpResponse(json);
                break;
            case "monsters":
                json = JsonUtil.generateJsonByCommand(command, result);
                System.out.println("[REST Client] " + result);
                sendHttpResponse(json);
                break;
            case "users":
                json = JsonUtil.generateJsonByCommand(command, result);
                System.out.println("[REST Client] " + result);
                sendHttpResponse(json);
                break;
            case "chat":
                break;
            case "potion":
                json = JsonUtil.generateJson(result);
                System.out.println("[REST Client] " + result);
                sendHttpResponse(json);
                break;
        }
    }
}
