package me.gamecms.org.balance;

import com.google.gson.Gson;
import me.gamecms.org.GameCMS;
import me.gamecms.org.utility.HTTPRequest;
import org.bukkit.Bukkit;

import java.math.RoundingMode;
import java.text.DecimalFormat;


public class Balance {


    private static final DecimalFormat df = new DecimalFormat("0.00");

    GameCMS plugin;

    private final Gson gson = new Gson();
    private final String API;
    private Object ArrayList;

    public Balance(GameCMS plugin) {
        this.plugin = plugin;
        API = plugin.API_URL + "/websites/balance";
    }

    public String add(String username, double amount) {
        try {
            df.setRoundingMode(RoundingMode.UP);
            //setup REQUEST params. Without ? it is in sendRequest();
            String GET = "username=" + username + "&balance=" + df.format(amount);
            return this.sendRequest(GET, "add");
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getMessage());
        }
        return "Нещо се обърка, заявката не може да бъде изпълнена. (37)";
    }


    public String checkBalance(String username) {
        try {
            String GET = "username=" + username;
            return this.sendRequest(GET, "check");
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getMessage());
        }
        return "Нещо се обърка, заявката не може да бъде изпълнена. (48)";
    }


    private String sendRequest(String get, String link) throws Exception {
        String apiKey = plugin.getFileManager().getString("api-key");

        String request = "Нещо се обърка, заявката не може да бъде изпълнена (55)";

        String json = HTTPRequest.readUrl(API + "/" + link + "/" + apiKey + "?" + get);
        if (json.startsWith("{\"status\":\"100\"") || json.startsWith("{\"status\":\"101\"")) {
            request = ((gson.fromJson(json, BalanceRequestResponse.class)).message);
        }
        return request;
    }


}
