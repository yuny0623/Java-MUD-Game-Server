package org.server;

import org.server.mainserver.ServerAction;

public class Main {
    public static void main(String[] args) {
        System.out.println("hi");
        Thread thread = new Thread(new ServerAction());
        thread.start();
    }
}