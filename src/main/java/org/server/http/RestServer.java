package org.server.http;

import org.server.config.ServerConfig;

import javax.net.ssl.SSLSession;
import java.awt.font.OpenType;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RestServer extends Thread{

    ServerSocket serverSocket;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
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
                /*
                    logic 수행.
                 */
                out.println(buildHttpResponse(String.valueOf(contentBuffer)));
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

    public String buildHttpResponse(String data){
        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + data;
        return httpResponse;
    }
}
