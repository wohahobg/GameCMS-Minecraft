package me.gamecms.org.api;


import java.util.Map;

public class ApiRequestResponseMain {
    public static String bad_request_format = "{\"status\":500, \"message\":\"Something went wrong\"}";
    public int status;
    public String message;
    public Map<String, String> data; // map
}
