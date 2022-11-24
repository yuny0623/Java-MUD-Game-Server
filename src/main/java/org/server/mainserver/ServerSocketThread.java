package org.server.mainserver;

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
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            // 닉네임 입력받기.
            String nickname = in.readLine();
            /*
                닉네임 중복 확인 logic
             */

            server.broadCasting("[New Member]" + threadName + " has entered.\n");

            while(true){
                strIn = in.readLine();
                JsonUtil.parseJson(strIn);
                server.broadCasting(strIn);
            }
        }catch(IOException e){
            System.out.println(threadName + ": removed.");
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
