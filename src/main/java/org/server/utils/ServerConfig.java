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
    public static final int[] TCP_CONNECTION_ANOTHER_PORT_LIST = {8081, 8082, 8083, 8084, 8085, 8086, 8087, 8088, 8089, 8090};
    public static final String JEDIS_DEFAULT_IP = "127.0.0.1";
}
