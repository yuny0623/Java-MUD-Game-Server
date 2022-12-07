package org.server.dto;

public class ResultDto {
    String command;
    String result;

    public ResultDto(String command, String result){
        this.command = command;
        this.result = result;
    }

    public String getCommand(){
        return this.command;
    }

    public String getResult(){
        return this.result;
    }
}
