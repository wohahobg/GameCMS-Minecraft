package me.gamecms.org.webstore;


import me.gamecms.org.GameCMS;
import me.gamecms.org.entrys.WhitelistCacheEntry;
import me.gamecms.org.utility.DurationHelper;
import me.gamecms.org.utility.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.google.gson.Gson;

public class WebStore {

    private final GameCMS plugin;
    private BukkitTask task;
    private final Gson gson = new Gson();
    private final String API;

    public WebStore(GameCMS plugin) {
        this.plugin = plugin;
        API = plugin.API_URL + "/commands";
        this.load();
        this.start();
    }

    public void load() {
        Integer minSchedule = DurationHelper.getTickDurationFromFormat("m", 1);
        if (plugin.getConfigFile().getCommandsScheduler() < minSchedule) {
            plugin.getConfigFile().setCommandsScheduler(minSchedule);
        }
    }

    public void start() {
        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (plugin.getConfigFile().getLogFetchedCommands()) {
                plugin.getLogger().log(Level.INFO, "Fetching all due players...");
            }
            fetchAndExecuteCommands(null);
        }, plugin.getConfigFile().getCommandsScheduler(), plugin.getConfigFile().getCommandsScheduler());
    }

    public void fetchAndExecuteCommands(CommandSender sender) {
        try {
            List<String> executedCommandIds = new ArrayList<>();
            ArrayList<CommandsHelper> commands = plugin.getWebStore().getCommands();

            for (CommandsHelper command : commands) {
                if (command.must_be_online && Bukkit.getPlayer(command.username) == null) {
                    continue;
                }
                executeCommand(command);
                executedCommandIds.add(command.id);
            }

            if (!executedCommandIds.isEmpty()) {
                completeCommands(executedCommandIds);
            }

            if (sender == null) {
                if (plugin.getConfigFile().getLogFetchedCommands()) {
                    plugin.getLogger().log(Level.INFO, "Fetched due players (" + executedCommandIds.size() + " found).");
                }
            } else {
                sender.sendMessage("§aFetched due players §8(§e" + executedCommandIds.size() + " found§8)§7.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.WARNING, "GameCMS seems to be offline right now. The data has been saved and will be executed soon.");
        }
    }

    public void stop() {

        if (task != null) {
            task.cancel();
        }

    }

    public void executeCommand(CommandsHelper commandsHandlers) {

        Bukkit.getScheduler().runTask(plugin, () -> {
            commandsHandlers.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));

            plugin.getServer().getPluginManager().callEvent(new CommandsEvent(commandsHandlers));
        });

    }

    public ArrayList<CommandsHelper> getCommands() throws Exception {

        ArrayList<CommandsHelper> commands = new ArrayList<>();

        String response = HTTPRequest.sendGET(API + "/queue/minecraft", plugin.getConfigFile().getServerApiKey());

        ApiRequestResponse responseResult = gson.fromJson(response, ApiRequestResponse.class);
        if (responseResult.status == 200) {
            commands = (ArrayList<CommandsHelper>) responseResult.data;
        }

        return commands;
    }

    public void completeCommands(List<String> executedCommandIds) throws Exception {
        Gson gson = new Gson();
        System.out.println(executedCommandIds);
        String jsonPayload = gson.toJson(executedCommandIds);
        String params = "ids=" + jsonPayload;
        System.out.println(params);
        String apiKey = plugin.getConfigFile().getServerApiKey();
        HTTPRequest.sendPost(API + "/complete", params, apiKey);
    }

}
