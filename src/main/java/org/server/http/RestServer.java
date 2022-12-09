package org.server.http;

import org.server.config.ServerConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

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
                strIn = in.readLine();
                System.out.println(strIn);
                /*
                    소켓 처리
                 */
            }
        }catch(IOException e)   {
            e.printStackTrace();
        }
    }

}
