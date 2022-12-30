package me.gamecms.org.webstore;


import me.gamecms.org.GameCMS;
import me.gamecms.org.utility.DurationHelper;
import me.gamecms.org.utility.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
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
    }

    public void load() {
        long minSchedule = DurationHelper.getTickDurationFromFormat("m", 1);
        if (plugin.getConfigFile().getCommandsScheduler() < minSchedule) {
            plugin.getConfigFile().setCommandsScheduler(minSchedule);
        }
    }

    public void start() {

        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {

            try {

                plugin.getLogger().log(Level.INFO, "Fetching all due players...");

                ArrayList<CommandsHelper> commands = this.getCommands();

                for (CommandsHelper command : commands) {

                    if (command.must_be_online && Bukkit.getPlayer(command.username) == null) {
                        plugin.getPendingCommands().saveCommand(command.username, command);
                        continue;
                    }

                    execute(command);

                }

                if (!commands.isEmpty()) {

                    try {
                        this.completePayments();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                plugin.getLogger().log(Level.INFO, "Fetched due players (" + commands.size() + " found).");

            } catch (Exception e) {
                plugin.getLogger().log(Level.INFO, "GameCMS seems to be offline right now. The data has been saved and will be executed soon.");
            }

        }, plugin.getConfigFile().getCommandsScheduler(), plugin.getConfigFile().getCommandsScheduler());

    }

    public void stop() {

        if (task != null) {
            task.cancel();
        }

    }

    public void execute(CommandSender sender) {

        if (sender == null) {
            plugin.getLogger().log(Level.INFO, "Fetching all due players...");
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            try {
                ArrayList<CommandsHelper> commands = plugin.getWebStore().getCommands();

                for (CommandsHelper command : commands) {
                    if (command.must_be_online && Bukkit.getPlayer(command.username) == null) {
                        plugin.getPendingCommands().saveCommand(command.username, command);
                        continue;
                    }

                    execute(command);
                }

                if (!commands.isEmpty()) {

                    try {
                        plugin.getWebStore().completePayments();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                if (sender == null) {
                    plugin.getLogger().log(Level.INFO, "Fetched due players (" + commands.size() + " found).");
                } else {
                    sender.sendMessage("§aFetched due players §8(§e" + commands.size() + " found§8)§7.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().log(Level.INFO, "GameCMS seems to be offline right now. The data has been saved and will be executed soon.");
            }

        });

    }

    public void execute(CommandsHelper commandsHandlers) {

        if (plugin.getConfigFile().isBroadcastCommandsMessageEnabled()) {
            if (commandsHandlers.order_message != null) {
                if (!commandsHandlers.order_message.equals("")) {
                    Bukkit.broadcastMessage(commandsHandlers.order_message.replace('&', '§'));
                }
            }
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            commandsHandlers.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));

            plugin.getServer().getPluginManager().callEvent(new CommandsEvent(commandsHandlers));
        });

    }

    public ArrayList<CommandsHelper> getCommands() throws Exception {

        ArrayList<CommandsHelper> commands = new ArrayList<>();

        String response = HTTPRequest.sendGET(API + "/queue/minecraft", plugin.getConfigFile().getServerKey());

        ApiRequestResponse responseResult = gson.fromJson(response, ApiRequestResponse.class);
        if (responseResult.status == 200) {
            commands = (ArrayList<CommandsHelper>) responseResult.data;
        }

        return commands;

    }

    public void completePayments() throws Exception {
        HTTPRequest.sendGET(API + "/complete", plugin.getConfigFile().getServerKey());
    }

}
