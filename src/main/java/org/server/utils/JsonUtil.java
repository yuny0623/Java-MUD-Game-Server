package org.server.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonUtil {
    private static JsonUtil instance;

    private JsonUtil(){

    }

    public static JsonUtil getInstance(){
        if(instance == null){
            instance = new JsonUtil();
        }
        return instance;
    }

    public synchronized String generateJson(String str){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Notice", str);
        return jsonObject.toJSONString();
    }

    public synchronized String parseJson(String json){
        if(json.isEmpty() || json.isBlank()){
            System.out.println("Invalid json input!");
        }
        String command = null;
        JSONObject obj = null;
        JSONParser parser;
        String result = "";
        try {
            parser = new JSONParser();
            obj = (JSONObject) parser.parse(json);
            command = (String) obj.get("command");
        }catch(ParseException e){
            e.printStackTrace();
        }
        switch(command){
            case "move":
                String x = (String) obj.get("x");
                String y = (String) obj.get("y");
                result = "move " + x + " " + y;
                break;
            case "attack":
                result = "attack";
                break;
            case "monsters":
                result = "monsters";
                break;
            case "users":
                result = "users";
                break;
            case "chat":
                String opponent = (String) obj.get("opponent");
                String content = (String) obj.get("content");
                result = "chat " + opponent + " " + content;
                break;
            case "bot":
                result = "bot";
                break;
            case "nickname":
                String nickname = (String) obj.get("nickname");
                result = "nickname " + nickname;
                break;
            default:
                return "";
        }
        return result;
    }
}
