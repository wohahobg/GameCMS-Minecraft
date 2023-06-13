package me.gamecms.org.placeholders;


import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.gamecms.org.GameCMS;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {


    private final GameCMS plugin;

    public Placeholders(GameCMS plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "Wohaho";
    }

    @Override
    public String getIdentifier() {
        return "gamecms";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params)
    {
        if (plugin.getConfigFile().isPlaceholdersEnabled()) {
            if (params.equals("user_paid_balance")) {
                return String.valueOf(plugin.getApiBase().user().getPaidBalance(player.getUniqueId()));
            }
            if (params.equals("user_virtual_balance")) {
                return String.valueOf(plugin.getApiBase().user().getVirtualBalance(player.getUniqueId()));
            }
            if (params.equals("user_total_balance")) {
                return String.valueOf(plugin.getApiBase().user().getTotalBalance(player.getUniqueId()));
            }
        }
        return null;
    }


}
