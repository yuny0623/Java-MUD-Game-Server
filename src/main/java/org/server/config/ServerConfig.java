package org.server.config;

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
    public static final int HTTP_CONNECTION_DEFAULT_PORT = 8081;
    public static final String JEDIS_DEFAULT_IP = "127.0.0.1";
    public static final int[] DX = {-1, 0, 1, 1, 1, 0, -1, -1, 0};
    public static final int[] DY = {-1, -1, -1, 0, 1, 1, 1, 0, 0};
}
