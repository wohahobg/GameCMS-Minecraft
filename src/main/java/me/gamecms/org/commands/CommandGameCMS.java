package me.gamecms.org.commands;

import com.google.gson.Gson;
import me.gamecms.org.GameCMS;
import me.gamecms.org.api.responses.BasicRequestResponse;
import me.gamecms.org.utility.DurationHelper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandGameCMS implements CommandExecutor {


    GameCMS plugin;

    public CommandGameCMS(GameCMS plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {

            String commandKey = args[0];

            if (commandKey.equalsIgnoreCase("reload") || commandKey.equalsIgnoreCase("rl")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                // Reload the configuration
                plugin.getWebStore().load();
                plugin.getConfigFile().getSettings().reload();
                sender.sendMessage(message("Configuration file reloaded successfully."));
                return true;
            }

            if (commandKey.equalsIgnoreCase("force")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                // Execute a forced action
                plugin.getWebStore().execute(sender);
                return true;
            }

            if (commandKey.equalsIgnoreCase("test")) {
                System.out.println(plugin.getConfigFile().getServerApiKey());
                return true;
            }

            if (commandKey.equalsIgnoreCase("setServerApiKey")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(message("Please enter the API key for this server."));
                    return false;
                }
                // Set the server API key
                plugin.getConfigFile().setServerApiKey(args[1]);
                sender.sendMessage(message("Server API key has been successfully set."));
                return true;
            }

            if (commandKey.equalsIgnoreCase("setScheduler")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(message("Please enter the time interval in minutes for checking new commands. For example: 1200"));
                    return false;
                }
                try {
                    int minSchedule = DurationHelper.getTickDurationFromFormat("m", 1);
                    int schedule = DurationHelper.getTickDurationFromFormat("m", Integer.parseInt(args[1]));

                    if (schedule < minSchedule) {
                        sender.sendMessage(message("The check interval cannot be less than 1 minute."));
                        return false;
                    }

                    // Set the commands scheduler
                    plugin.getConfigFile().setCommandsScheduler(schedule);
                    sender.sendMessage(message("The commands scheduler has been updated successfully."));
                } catch (NumberFormatException e) {
                    sender.sendMessage(message("The input is not a valid number. Please try again with a number like 1200."));
                }
                return true;
            }

            if (commandKey.equalsIgnoreCase("setWebsiteApiKey")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(message("Please enter your website API key."));
                    return false;
                }
                // Set the website API key
                plugin.getConfigFile().setWebsiteApiKey(args[1]);
                sender.sendMessage(message("Website API key has been updated."));
                return true;
            }

            if (commandKey.equalsIgnoreCase("usePlaceholders") || commandKey.equalsIgnoreCase("usePapi")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
                    sender.sendMessage(message("You don't have PlaceholderAPI installed!"));
                    return false;
                }
                // Toggle placeholders usage
                boolean usePlaceholders = plugin.getConfigFile().isPlaceholdersEnabled();
                plugin.getConfigFile().setUsePlaceholders(!usePlaceholders);
                String status = usePlaceholders ? "disabled" : "enabled";
                sender.sendMessage(message("Placeholders usage is now " + status + "!"));
                return true;
            }

            if (commandKey.equalsIgnoreCase("getBalance") || commandKey.equalsIgnoreCase("checkBalance")) {
                if (!sender.hasPermission("gamecms.checkplayerbalance") && !sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(message("Please enter a username!"));
                    return false;
                }

                // Get player balance asynchronously
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        String response = plugin.getApiBase().user().getBalance(args[1]);
                        Gson gson = new Gson();
                        BasicRequestResponse responseResult = gson.fromJson(response, BasicRequestResponse.class);
                        sender.sendMessage(message(responseResult.message));
                    } catch (Exception e) {
                        plugin.getLogger().info(e.getMessage());
                    }
                });
                return true;
            }

            if (commandKey.equalsIgnoreCase("addBalance")) {
                if (!sender.hasPermission("gamecms.addbalance") && !sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(message("Please enter a username!"));
                    return false;
                }
                if (args.length == 2) {
                    sender.sendMessage(message("Please enter an amount to be added to the balance of " + args[1]));
                    return false;
                }
                try {
                    double balance = Double.parseDouble(args[2]);
                    // Add balance to player asynchronously
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                        try {
                            String response = plugin.getApiBase().user().addBalance(args[1], balance);
                            Gson gson = new Gson();
                            BasicRequestResponse responseResult = gson.fromJson(response, BasicRequestResponse.class);
                            sender.sendMessage(message(responseResult.message));
                        } catch (Exception e) {
                            plugin.getLogger().info(e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    sender.sendMessage(message("The amount must be a number. Example: /addbalance PlayerName 15.50"));
                    return false;
                }
                return true;
            }

            if (commandKey.equalsIgnoreCase("Verify")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(message("This command can only be executed in-game."));
                    return false;
                }

                if (!sender.hasPermission("gamecms.verify") && !sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(message("Please enter the verification token. You can generate one from your profile page on our website."));
                    return false;
                }
                // Verify player profile asynchronously
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        Player player = (Player) sender;
                        String response = plugin.getApiBase().user().verifyProfile(args[1], player);
                        Gson gson = new Gson();
                        BasicRequestResponse responseResult = gson.fromJson(response, BasicRequestResponse.class);
                        sender.sendMessage(message(responseResult.message));
                    } catch (Exception e) {
                        plugin.getLogger().info(e.getMessage());
                    }
                });
                return true;
            }
        }

        sender.sendMessage(message("Find everything for this plugin here: " + ChatColor.WHITE + "https://docs.gamecms.org/24"));
        return true;
    }

    public String message(String message) {
        return ChatColor.translateAlternateColorCodes('&', "&f&l[&9GameCMS&f&l] &7&l" + message);
    }

    public String noPermission() {
        return ChatColor.translateAlternateColorCodes('&', "&f&l[&9GameCMS&f&l] &3&lYou don't have permission!");
    }

}
