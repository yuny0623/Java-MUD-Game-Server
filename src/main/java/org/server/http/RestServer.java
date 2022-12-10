package org.server.http;

import org.server.config.ServerConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RestServer extends Thread{

    ServerSocket serverSocket;
    Socket socket;
    BufferedReader in;
    String strIn;

    public RestServer(){
        System.out.println("[Rest Server] Start Rest Server.\n");
    }

    @Override
    public void run(){
        try{
            serverSocket = new ServerSocket(ServerConfig.HTTP_CONNECTION_DEFAULT_PORT);
            serverSocket.setReuseAddress(true);
            while(true){
                socket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuffer sb = new StringBuffer();
                while(true){
                    strIn = in.readLine();
                    if(strIn == null || strIn.isBlank() || strIn.isEmpty()){
                        break;
                    }
                    sb.append(strIn+"\n");
                }
                /*
                    소켓 처리
                 */
                int contentLength = httpRequestParser(sb.toString());
                if(contentLength == -1){
                    System.out.println("[Rest Server] [Error] Cannot parse HTTP request.");
                    continue;
                }
                char[] contentBuffer = new char[contentLength];
                in.read(contentBuffer);
                System.out.println(contentBuffer);
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
}
