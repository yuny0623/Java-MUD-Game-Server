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

        // server reset when server down
        JedisUtil.clearRedis();
    }
}