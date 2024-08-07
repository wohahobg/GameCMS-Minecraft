package me.gamecms.org.listener;

import com.google.gson.Gson;
import me.gamecms.org.GameCMS;
import me.gamecms.org.api.responses.BasicRequestResponse;
import me.gamecms.org.api.responses.UserBalanceResponse;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJoinQuit implements Listener {

    private final GameCMS plugin;

    private final Gson gson = new Gson();

    private final Map<UUID, BukkitTask> tasks;

    public PlayerJoinQuit(GameCMS plugin) {
        this.plugin = plugin;
        tasks = new HashMap<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        BukkitTask playerTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            String response = plugin.getApiBase().user().getBalance(player.getName());
            BasicRequestResponse responseResult = gson.fromJson(response, BasicRequestResponse.class);
            if (responseResult.status == 200) {
                UserBalanceResponse UserBalance = new UserBalanceResponse(responseResult.data.get("paid_balance"), responseResult.data.get("virtual_balance"), responseResult.data.get("total_balance"));
                plugin.getApiBase().user().userBalances.put(player.getUniqueId(), UserBalance);
            }
        }, 50, 2400);

        tasks.put(player.getUniqueId(), playerTask);

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        //remove the player from the balanceMap for placeholder
        plugin.getApiBase().user().userBalances.remove(player.getUniqueId());
        //cancel any task after player quit
        if (tasks.containsKey(player.getUniqueId())) {
            BukkitTask pendingTask = tasks.get(player.getUniqueId());
            if (pendingTask != null) {
                pendingTask.cancel();
            }
            tasks.remove(player.getUniqueId());
        }
    }


    public void stopTasks() {
        if (!tasks.isEmpty()) {
            for (BukkitTask task : tasks.values()) {
                task.cancel();
            }
            tasks.clear();
        }
    }

}
