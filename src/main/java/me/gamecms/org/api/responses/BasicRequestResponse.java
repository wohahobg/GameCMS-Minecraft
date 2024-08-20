package me.gamecms.org.api.responses;


import java.util.Map;

public class BasicRequestResponse {
    public static String bad_request_format = "{\"status\":500, \"message\":\"Something went wrong\"}";
    public int status;
    public String message;
    public int id = 0;
    public Map<String, String> data; // map
}
