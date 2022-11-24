package org.server.utils;

public final class ServerConfig {

    private static ServerConfig instance;
    private ServerConfig(){

    }
    public static ServerConfig getInstance(){
        if(instance == null){
            instance = new ServerConfig();
        }
        return instance;
    }

    public static final int TCP_CONNECTION_DEFAULT_PORT = 8080;
}
