package me.gamecms.org.placeholders;


import com.google.gson.Gson;
import me.gamecms.org.GameCMS;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaceholdersRegister {

    private final GameCMS plugin;

    public Map<UUID, String> balanceMap = new HashMap<>();

    public PlaceholdersRegister(GameCMS plugin) {
        this.plugin = plugin;

        new PlaceholdersList(plugin).register();

    }

    public String getUserBalance(UUID playerUUID) {
        if (balanceMap.containsKey(playerUUID)) {
            return balanceMap.get(playerUUID);
        }
        return "0.00";
    }

}
