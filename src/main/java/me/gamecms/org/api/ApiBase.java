package me.gamecms.org.api;


import me.gamecms.org.GameCMS;

public class ApiBase {

    public GameCMS plugin;

    public ApiBase(GameCMS gameCMS) {
        plugin = gameCMS;
    }

    public ApiUserBalance userBalance() {
        return new ApiUserBalance(this);
    }

}

