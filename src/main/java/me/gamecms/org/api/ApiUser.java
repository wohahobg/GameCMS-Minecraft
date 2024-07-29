package me.gamecms.org.api;

import me.gamecms.org.GameCMS;
import me.gamecms.org.api.responses.UserBalanceResponse;
import me.gamecms.org.utility.HTTPRequest;
import org.bukkit.entity.Player;

import java.io.*;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;


public class ApiUser {


    private final DecimalFormat df = new DecimalFormat("0.00");
    private final String API_URL;
    private final GameCMS plugin;
    public Map<UUID, UserBalanceResponse> userBalances = new HashMap<>();

    public ApiUser(ApiBase ApiBase) {
        plugin = ApiBase.plugin;
        API_URL = plugin.API_URL + "/websites/user";
    }


    public String addBalance(String username, double amount) {
        try {
            df.setRoundingMode(RoundingMode.UP);
            //setup REQUEST params. Without ? it is in sendRequest();
            String PARAMS = "balance=" + df.format(amount);
            return this.sendRequest(PARAMS, "balance/add?username=" + username, "POST");
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.INFO, "GameCMS seems to be offline right now. The data has been saved and will be executed soon.");
        }
        return null;
    }

    public String getBalance(String username) {
        try {
            String PARAMS = "username=" + username;
            return this.sendRequest(PARAMS, "balance/get", "GET");
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.INFO, "GameCMS seems to be offline right now. The data has been saved and will be executed soon.");
        }
        return null;
    }

    public String verifyProfile(String token, Player player) {
        try {
            //setup REQUEST params. Without ? it is in sendRequest();
            String PARAMS = "token=" + token + "&username=" + player.getName() + "&uuid=" + player.getUniqueId();
            return this.sendRequest(PARAMS, "verify/minecraft", "POST");
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.INFO, "GameCMS seems to be offline right now. The data has been saved and will be executed soon.");
        }
        return null;
    }

    public String getPaidBalance(UUID playerUUID) {
        if (this.userBalances.containsKey(playerUUID)) {
            UserBalanceResponse UserBalance = this.userBalances.get(playerUUID);
            return UserBalance.getPaid();
        }
        return "0.00";
    }

    public String getVirtualBalance(UUID playerUUID) {
        if (this.userBalances.containsKey(playerUUID)) {
            UserBalanceResponse UserBalance = this.userBalances.get(playerUUID);
            return UserBalance.getVirtual();
        }
        return "0.00";
    }

    public String getTotalBalance(UUID playerUUID) {
        if (this.userBalances.containsKey(playerUUID)) {
            UserBalanceResponse UserBalance = this.userBalances.get(playerUUID);
            return UserBalance.getTotal();
        }
        return "0.00";
    }

    public String sendRequest(String PARAMS, String URL, String METHOD) throws IOException {
        String json;
        if (METHOD.equals("POST")) {
            json = HTTPRequest.sendPost(API_URL + "/" + URL, PARAMS, plugin.getConfigFile().getWebsiteApiKey());
        } else {
            URL = API_URL + "/" + URL + "?" + PARAMS;
            json = HTTPRequest.sendGET(URL, plugin.getConfigFile().getWebsiteApiKey());
        }
        return json;
    }


}
