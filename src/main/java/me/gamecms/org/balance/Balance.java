package me.gamecms.org.balance;

import com.google.gson.Gson;
import jdk.nashorn.internal.ir.debug.ClassHistogramElement;
import me.gamecms.org.GameCMS;
import me.gamecms.org.utility.HTTPRequest;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.omg.CORBA.Request;


import java.io.*;
import java.math.RoundingMode;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;


public class Balance {


    private static final DecimalFormat df = new DecimalFormat("0.00");

    GameCMS plugin;

    private final Gson gson = new Gson();
    private final String API;

    public Balance(GameCMS plugin) {
        this.plugin = plugin;
        API = plugin.API_URL + "/websites/balance";
    }

    public String add(String username, double amount) {
        try {
            df.setRoundingMode(RoundingMode.UP);
            //setup REQUEST params. Without ? it is in sendRequest();
            String PARAMS = "username=" + username + "&balance=" + df.format(amount);
            return this.sendRequest(PARAMS, "add", "POST");
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getMessage());
        }
        return "Нещо се обърка, заявката не може да бъде изпълнена. (Balance::43)";
    }


    public String checkBalance(String username) {
        try {
            String GET = "username=" + username;
            return this.sendRequest(GET, "check", "GET");
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getMessage());
        }
        return "Нещо се обърка, заявката не може да бъде изпълнена. (Balance::54)";
    }


    public String sendRequest(String PARAMS, String URL, String METHOD) throws IOException {

        String apiKey = plugin.getFileManager().getString("api-key");

        String json;
        if (METHOD == "POST") {
            json = HTTPRequest.sendPost(API + "/" + URL, PARAMS, apiKey);
        } else {
            URL = API + "/" + URL + "?" + PARAMS;
            json = HTTPRequest.sendGET(URL, apiKey);
        }
        String message = "Нещо се обърка, заявката не може да бъде изпълнена (Balance::69)";
        if (json.startsWith("{\"status\":\"100\"") || json.startsWith("{\"status\":\"101\"")) {
            message = ((gson.fromJson(json, BalanceRequestResponse.class)).message);
        }
        return message;
    }


}
