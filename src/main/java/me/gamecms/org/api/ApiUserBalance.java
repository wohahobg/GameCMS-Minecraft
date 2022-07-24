package me.gamecms.org.api;

import me.gamecms.org.GameCMS;
import me.gamecms.org.utility.HTTPRequest;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import java.util.logging.Level;


public class ApiUserBalance {


    private final DecimalFormat df = new DecimalFormat("0.00");
    private final String API_URL;
    private final ApiBase API;
    private final GameCMS plugin;

    public ApiUserBalance(ApiBase ApiBase) {
        API = ApiBase;
        plugin = ApiBase.plugin;
        API_URL = plugin.API_URL + "/websites/user/balance";
    }


    public String addBalance(String username, double amount) {
        try {
            df.setRoundingMode(RoundingMode.UP);
            //setup REQUEST params. Without ? it is in sendRequest();
            String PARAMS = "username=" + username + "&balance=" + df.format(amount);
            return this.sendRequest(PARAMS, "add", "POST");
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.INFO, "GameCMS seems to be offline right now. The data has been saved and will be executed soon.");
        }
        return null;
    }

    public String getBalance(String username) {
        try {
            String PARAMS = "username=" + username;
            return this.sendRequest(PARAMS, "check", "GET");
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.INFO, "GameCMS seems to be offline right now. The data has been saved and will be executed soon.");
        }
        return null;
    }


    public String sendRequest(String PARAMS, String URL, String METHOD) throws IOException {
        String json;
        if (METHOD.equals("POST")) {
            json = HTTPRequest.sendPost(API_URL + "/" + URL, PARAMS, plugin.getConfigFile().getApiKey());
        } else {
            URL = API_URL + "/" + URL + "?" + PARAMS;
            json = HTTPRequest.sendGET(URL, plugin.getConfigFile().getApiKey());
        }
        return json;
    }


}
