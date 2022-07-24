package me.gamecms.org.listener;

import com.google.gson.Gson;
import me.gamecms.org.GameCMS;
import me.gamecms.org.api.ApiRequestResponseMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {


    private final GameCMS plugin;
    private final Gson gson = new Gson();
    private Map<UUID, BukkitTask> tasks;

    public PlayerListener(GameCMS gameCMS) {
        plugin = gameCMS;
        tasks = new HashMap<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        BukkitTask pendingTask = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            plugin.getPendingCommands().onPlayerJoin(player);

            //check if we should use placeholder
            //if so we make http api call and add player to the balanceMap in getPlaceholdersRegister();
            if (plugin.getConfigFile().getUsePlaceholders()) {
                String response = plugin.getApiBase().userBalance().getBalance(player.getName());
                ApiRequestResponseMain responseResult = gson.fromJson(response, ApiRequestResponseMain.class);
                if (responseResult.status == 200) {
                    plugin.getPlaceholdersRegister().balanceMap.put(player.getUniqueId(), responseResult.data.get("balance"));
                }


            }
        }, 20 * 5);

        tasks.put(player.getUniqueId(), pendingTask);

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        //remove the player from the balanceMap for placeholder
        //we run this only if use-placeholder is true;

        if (plugin.getPlaceholdersRegister().balanceMap.containsKey(player.getUniqueId())) {
            plugin.getPlaceholdersRegister().balanceMap.remove(player.getUniqueId());
        }

        //cancel any task after player quit
        if (tasks.containsKey(player.getUniqueId())) {
            BukkitTask pendingTask = tasks.get(player.getUniqueId());
            if (pendingTask != null) {
                pendingTask.cancel();
            }
            tasks.remove(player.getUniqueId());
        }


    }
}
