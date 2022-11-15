package me.gamecms.org.api;


import me.gamecms.org.GameCMS;

public class ApiBase {

    public GameCMS plugin;

    public ApiBase(GameCMS gameCMS) {
        plugin = gameCMS;
    }

    public ApiUser user() {
        return new ApiUser(this);
    }
}

