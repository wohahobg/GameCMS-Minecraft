package me.gamecms.org.placeholders;


import me.gamecms.org.GameCMS;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaceholdersRegister {

    private final GameCMS plugin;

    public Map<UUID, String> playerBalanceMap = new HashMap<>();

    public PlaceholdersRegister(GameCMS plugin) {
        this.plugin = plugin;

        new Placeholders(plugin).register();

    }

    public String getUserBalance(UUID playerUUID) {
        if (playerBalanceMap.containsKey(playerUUID)) {
            return playerBalanceMap.get(playerUUID);
        }
        return "0.00";
    }

}