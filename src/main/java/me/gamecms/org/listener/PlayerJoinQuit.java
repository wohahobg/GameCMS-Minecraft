package me.gamecms.org.listener;

import com.google.gson.Gson;
import me.gamecms.org.GameCMS;
import me.gamecms.org.MessageFormatter;
import me.gamecms.org.api.responses.BasicRequestResponse;
import me.gamecms.org.api.responses.UserBalanceResponse;
import me.gamecms.org.entrys.WhitelistCacheEntry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
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
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (plugin.getConfigFile().isWhitelistEnabled()) {
            String playerAddress = event.getAddress().toString();
            String playerName = event.getName();


            if (plugin.getConfigFile().getWhitelistedNamed().contains(playerName)) {
                return;
            }

            String whitelistMessage = MessageFormatter.formatMessage(plugin.getConfigFile().getWhitelistMessage());
            String whiteListMaxIpLimitExceededMessage = MessageFormatter.formatMessage(plugin.getConfigFile().getMaxIpLimitExceededMessage());

            if (playerAddress.startsWith("/")) {
                playerAddress = playerAddress.substring(1);
            }

            long joinByFromIp = plugin.getWhitelistCache().mappingCount();
            if (plugin.getConfigFile().getWhitelistMaxIPs() <= joinByFromIp) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, whiteListMaxIpLimitExceededMessage);
                return;
            }

            // Check cache
            Boolean isWhitelisted = getWhitelistStatusFromCache(playerAddress);
            if (isWhitelisted != null) {
                if (!isWhitelisted) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, whitelistMessage);
                }
                return;
            }

            try {
                String response = plugin.getApiBase().user().isWhitelisted(playerAddress);
                Gson gson = new Gson();
                BasicRequestResponse responseResult = gson.fromJson(response, BasicRequestResponse.class);
                if (responseResult.status != 200) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, whitelistMessage);
                }
            } catch (Exception e) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Error checking whitelist status: " + e.getMessage());
            }
        }
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

    private Boolean getWhitelistStatusFromCache(String playerAddress) {
        WhitelistCacheEntry cacheEntry = plugin.getWhitelistCache().get(playerAddress);

        if (cacheEntry != null) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - cacheEntry.getTimestamp()) < plugin.getCACHE_DURATION()) {
                return cacheEntry.isWhitelisted();
            } else {
                plugin.getWhitelistCache().remove(playerAddress);
            }
        }
        return null;
    }


}
