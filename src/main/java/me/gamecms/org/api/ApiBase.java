package me.gamecms.org.api;


import me.gamecms.org.GameCMS;
import me.gamecms.org.utility.HTTPRequest;

import java.io.IOException;
import java.util.logging.Level;

public class ApiBase {

    public GameCMS plugin;
    private ApiUser apiUser;

    public ApiBase(GameCMS gameCMS) {
        plugin = gameCMS;
        apiUser = new ApiUser(this);
    }

    public ApiUser user() {
        return apiUser;
    }

    public String verifyServer(String token, String serverIp, int serverPort) {

        try {
            String PARAMS = "address=" + serverIp + "&port=" + serverPort;
            return this.sendRequest(PARAMS, this.plugin.API_URL + "/server-verify/minecraft", "POST", token);
        } catch (Exception e) {
            e.printStackTrace();
            this.plugin.getLogger().log(Level.INFO, "GameCMS seems to be offline right now. The data has been saved and will be executed soon.");
        }
        return token;
    }


    public String sendWebsiteApiRequest(String params, String uri, String method) throws IOException {
        return this.sendRequest(params, uri, method, plugin.getConfigFile().getWebsiteApiKey());
    }

    public String sendRequest(String params, String uri, String method, String token) throws IOException {
        String json;
        if (method.equals("POST")) {
            json = HTTPRequest.sendPost(uri, params, token);
        } else {
            uri = uri + "?" + params;
            json = HTTPRequest.sendGET(uri, token);
        }
        return json;
    }
}

