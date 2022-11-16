package me.gamecms.org.api;


import me.gamecms.org.GameCMS;

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
}

