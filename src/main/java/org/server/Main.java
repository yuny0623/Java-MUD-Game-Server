package org.server;

import org.server.http.RestServer;
import org.server.server.Server;
import org.server.utils.JedisUtil;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();

        RestServer restServer = new RestServer(server);
        restServer.start();

        // 종료 직전에 서버 리셋
        JedisUtil.clearRedis();
    }
}