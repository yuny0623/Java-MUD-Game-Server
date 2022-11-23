package org.server.MainServer;

import java.io.*;
import java.net.Socket;

public class ServerSocketThread extends Thread{

    Socket socket;
    MainServer server;
    BufferedReader in;
    PrintWriter out;
    String strIn;
    String threadName;
    public ServerSocketThread(MainServer server, Socket socket){
        this.server = server ;
        this.socket = socket;
        threadName = super.getName();
        System.out.println(socket.getInetAddress() + ": entered.");
        System.out.println("Thread Name: " + threadName);
    }

    public void sendMessage(String str){
        out.println(str);
    }

    @Override
    public void run(){
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            server.broadCasting("[New Member]" + threadName + " has entered.\n");
            while(true){
                strIn = in.readLine();
                System.out.println(strIn);
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
