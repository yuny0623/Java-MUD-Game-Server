package org.server.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonUtil {
    public static String generateJson(){
        return "";
    }

    public static void parseJson(String json){
        if(json.isEmpty() || json.isBlank()){
            System.out.println("Invalid json input!");
        }
        String command = null;
        JSONObject obj = null;
        JSONParser parser;
        try {
            parser = new JSONParser();
            obj = (JSONObject) parser.parse(json);
            command = (String) obj.get("command");
        }catch(ParseException e){
            e.printStackTrace();
        }

        switch(command){
            case "move":
                String x_val = (String) obj.get("x");
                String y_val = (String) obj.get("y");
                break;
            case "attack":
                /*
                    attack
                 */
                break;
            case "monsters":
                /*
                    show monsters
                 */
                break;
            case "users":
                /*
                    show user list
                 */
                break;
            case "chat":
                String opponent = (String) obj.get("opponent");
                String content = (String) obj.get("content");
                break;
            case "bot":
                /*
                    run bot mode
                 */
                break;
            default:
                System.out.println("Invalid json Input!");
        }
    }
}
