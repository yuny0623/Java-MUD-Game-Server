package org.server;

import org.server.mainserver.MainServer;

public class Main {
    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
        mainServer.start();
    }
}