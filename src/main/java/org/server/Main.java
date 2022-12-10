package org.server;

import org.server.http.RESTServer;
import org.server.server.Server;
import org.server.utils.JedisUtil;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();

        RESTServer restServer = new RESTServer(server);
        restServer.start();

        // 종료 직전에 서버 리셋
        JedisUtil.clearRedis();
    }
}