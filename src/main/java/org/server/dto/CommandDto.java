package org.server.dto;

public class CommandDto {
    String command;
    String nickname;

    public CommandDto(String command, String nickname){
        this.command = command;
        this.nickname = nickname;
    }

    public String getCommand() {
        return command;
    }

    public String getNickname(){
        return nickname;
    }
}
