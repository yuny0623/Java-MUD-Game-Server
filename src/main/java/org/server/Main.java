package org.server;

import org.server.server.MainServer;
import org.server.utils.JedisUtil;

public class Main {
    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
        mainServer.start();

        // 종료 직전에 서버 리셋
        JedisUtil.clearRedis();
    }
}