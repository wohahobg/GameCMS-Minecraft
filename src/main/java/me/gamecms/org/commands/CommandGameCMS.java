package me.gamecms.org.commands;

import com.google.gson.Gson;
import me.gamecms.org.GameCMS;
import me.gamecms.org.api.ApiRequestResponseMain;
import me.gamecms.org.utility.DurationHelper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;


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
                plugin.getConfigFile().initialize();
                plugin.getWebStore().load();
                sender.sendMessage(message("Конфигурационният файл е променен успешно."));
                return true;
            }

            if (commandKey.equalsIgnoreCase("force")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                plugin.getWebStore().execute(sender);
                return true;
            }
            if (commandKey.equalsIgnoreCase("setServerKey")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(message("Моля, въведете API ключа за този сървър."));
                    return false;
                }
                plugin.getConfigFile().setServerKey(args[1]);
                sender.sendMessage(message("Ключът на сървъра е променен успешно."));
                return true;
            }

            if (commandKey.equalsIgnoreCase("setScheduler")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(message("Моля, въведете време за проверка на нови команди. Например 1200"));
                    return false;
                }
                try {
                    long minSchedule = DurationHelper.getTickDurationFromFormat("m", 1);
                    long schedule = Math.max(Long.parseLong(args[1]), 0);

                    System.out.println(schedule);

                    if (schedule < minSchedule) {
                        sender.sendMessage(message("Времето за проверка не може да бъде по-малко от 1200."));
                        return false;
                    }

                    plugin.getConfigFile().setCommandsScheduler(schedule);
                    sender.sendMessage(message("Времето се промени успешно."));
                } catch (NumberFormatException e) {
                    sender.sendMessage(message("Това не е валидно число. Моля, опитайте номер като 1200"));
                }
                return true;
            }

            if (commandKey.equalsIgnoreCase("setApiKey")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(message("Моля, въведете API ключа на вашия уебсайт."));
                    return false;
                }
                plugin.getConfigFile().setApiKey(args[1]);
                sender.sendMessage(message("Ключът за API на уебсайта е променен успешно."));

                return true;
            }

            if (commandKey.equalsIgnoreCase("usePlaceholders")
                    || commandKey.equalsIgnoreCase("usePapi")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (plugin.getConfigFile().isPlaceholdersEnabled()){
                    plugin.getConfigFile().setUsePlaceholders(false);
                    sender.sendMessage(message("Изпозлването на Placeholders е деактивирано."));
                }else{
                    plugin.getConfigFile().setUsePlaceholders(true);
                    sender.sendMessage(message("Изпозлването на Placeholders е активирано."));
                }
                return true;
            }

            if (commandKey.equalsIgnoreCase("useBroadcastMessages")
                    || commandKey.equalsIgnoreCase("useBMC")) {
                if (!sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (plugin.getConfigFile().isBroadcastCommandsMessageEnabled()){
                    plugin.getConfigFile().setBroadcastCommandsMessage(false);
                    sender.sendMessage(message("Изпозлването на Broadcast съобшенията е деактивирано."));
                }else{
                    plugin.getConfigFile().setBroadcastCommandsMessage(true);
                    sender.sendMessage(message("Изпозлването на Broadcast съобшенията е активирано."));
                }
                return true;
            }

            if (commandKey.equalsIgnoreCase("getBalance") || commandKey.equalsIgnoreCase("checkBalance")) {
                if (!sender.hasPermission("gamecms.checkplayerbalance") && !sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(message("Моля въведете потребителско име."));
                    return false;
                }

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        String response = plugin.getApiBase().user().getBalance(args[1]);
                        Gson gson = new Gson();

                        ApiRequestResponseMain responseResult = gson.fromJson(response, ApiRequestResponseMain.class);

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
                    sender.sendMessage(message("Моля въведете потребителско име."));
                    return false;
                }
                if (args.length == 2) {
                    sender.sendMessage(message("Моля въведете сума която да се добави към баланса на " + args[1]));
                    return false;
                }
                try {
                    double balance = Double.parseDouble(args[2]);
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                        try {
                            String response = plugin.getApiBase().user().addBalance(args[1], balance);
                            Gson gson = new Gson();
                            ApiRequestResponseMain responseResult = gson.fromJson(response, ApiRequestResponseMain.class);

                            sender.sendMessage(message(responseResult.message));
                        } catch (Exception e) {
                            plugin.getLogger().info(e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    sender.sendMessage(message("Сумата трябва да бъде число. Пример; /gcms addbalance Wohaho 15.50"));
                    return false;
                }

                return true;
            }

            if (commandKey.equalsIgnoreCase("Verify")) {
                if (!(sender instanceof Player)){
                    sender.sendMessage(message("Тази команда може да се изпълнява единствено ако сте в сървъра."));
                    return false;
                }

                if (!sender.hasPermission("gamecms.verify") && !sender.hasPermission("gamecms.admin")) {
                    sender.sendMessage(noPermission());
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(message("Моля, въведете токена за потвърждение. Можете да генерирате такъв от нашия уебсайт в страницата на вашия профил."));
                    return false;
                }
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        Player player = (Player) sender;
                        String response = plugin.getApiBase().user().verifyProfile(args[1], player);
                        Gson gson = new Gson();
                        ApiRequestResponseMain responseResult = gson.fromJson(response, ApiRequestResponseMain.class);
                        sender.sendMessage(message(responseResult.message));
                    } catch (Exception e) {
                        plugin.getLogger().info(e.getMessage());
                    }
                });
                return true;
            }
        }

        sender.sendMessage(message("Find everything for this plugin here: &fhttps://docs.gamecms.org/24"));

        return true;

    }

    public String message(String message) {
        return ChatColor.translateAlternateColorCodes('&', "&f&l[&9GameCMS&f&l] &7&l" + message);
    }

    public String noPermission() {
        return ChatColor.translateAlternateColorCodes('&', "&f&l[&9GameCMS&f&l] &3&lНямате разрешение за тази команда.");
    }

}
