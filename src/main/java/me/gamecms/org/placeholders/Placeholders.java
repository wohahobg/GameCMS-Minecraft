package me.gamecms.org.placeholders;


import me.gamecms.org.GameCMS;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholdersBase {

    private final GameCMS plugin;

    public Placeholders(GameCMS plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params)
    {
        if (plugin.getConfigFile().getUsePlaceholders()) {
            if (params.equals("user_balance")) {
                return plugin.getApiBase().user().getUserBalance(player.getUniqueId());
            }
        }
        return null;
    }


}
