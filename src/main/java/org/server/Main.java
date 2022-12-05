package org.server;

import org.server.mainserver.MainServer;
import org.server.redistemplate.RedisTemplate;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
        mainServer.start();
    }
}