package me.gamecms.org.placeholders;


import me.gamecms.org.GameCMS;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class PlaceholdersList extends PlaceholdersBase {

    private final GameCMS plugin;

    public PlaceholdersList(GameCMS plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params)
    {
        if (plugin.getConfigFile().getUsePlaceholders()) {
            if (params.equals("user_balance")) {
                return plugin.getPlaceholdersRegister().getUserBalance(player.getUniqueId());
            }
        }

        return null;
    }
}
