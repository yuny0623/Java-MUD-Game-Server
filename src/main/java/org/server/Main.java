package org.server;

import org.server.mainserver.MainServer;
import org.server.redistemplate.RedisTemplate;

public class Main {
    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
        mainServer.start();

        // 종료 직전에 서버 리셋
        RedisTemplate.serverReset();
    }
}