package me.gamecms.org.commands;

import com.google.gson.Gson;
import me.gamecms.org.GameCMS;
import me.gamecms.org.api.ApiRequestResponseMain;
import me.gamecms.org.utility.DurationHelper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


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
                plugin.getConfigFile().initialize();
                plugin.getWebStore().load();
                sender.sendMessage(message("Конфигурационният файл е променена успешно."));
                return true;
            }

            if (commandKey.equalsIgnoreCase("force")) {
                plugin.getWebStore().execute(sender);
                return true;
            }
            if (commandKey.equalsIgnoreCase("setServerKey")) {
                if (args.length == 1) {
                    sender.sendMessage(message("Моля, въведете API ключа за този сървър."));
                    return false;
                }
                plugin.getConfigFile().setServerKey(args[1]);
                sender.sendMessage(message("Ключът на сървъра е променен успешно."));
                return true;
            }

            if (commandKey.equalsIgnoreCase("setScheduler")) {
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
                if (args.length == 1) {
                    sender.sendMessage(message("Моля, въведете API ключа на вашия уебсайт."));
                    return false;
                }
                plugin.getConfigFile().setApiKey(args[1]);
                sender.sendMessage(message("Ключът за API на уебсайта е променен успешно."));

                return true;
            }

            if (commandKey.equalsIgnoreCase("test")){
                plugin.getLogger().info(plugin.getConfigFile().getApiKey());
            }

            if (commandKey.equalsIgnoreCase("enablePlaceholders")
                    || commandKey.equalsIgnoreCase("enablePapi")) {
                plugin.getConfigFile().setUsePlaceholders(true);
                sender.sendMessage(message("Изпозлването на Placeholders е активирано."));
                return true;
            }
            if (commandKey.equalsIgnoreCase("disablePlaceholders")
                    || commandKey.equalsIgnoreCase("disablePapi")) {
                plugin.getConfigFile().setUsePlaceholders(false);
                sender.sendMessage(message("Изпозлването на Placeholders е деактивирано."));
                return true;
            }


            if (commandKey.equalsIgnoreCase("enableBroadcastCommandsMessage")
                    || commandKey.equalsIgnoreCase("enableBCM")) {
                plugin.getConfigFile().setBroadcastCommandsMessage(true);
                sender.sendMessage(message("Изпозлването на Broadcast съобшенията е активирано."));
                return true;
            }
            if (commandKey.equalsIgnoreCase("disableBroadcastCommandsMessage")
                    || commandKey.equalsIgnoreCase("disableBCM")) {
                plugin.getConfigFile().setBroadcastCommandsMessage(false);
                sender.sendMessage(message("Изпозлването на Broadcast съобшенията е деактивирано."));
                return true;
            }


            if (commandKey.equalsIgnoreCase("getBalance") || commandKey.equalsIgnoreCase("checkBalance")) {
                if (args.length == 1) {
                    sender.sendMessage(message("Моля въведете потребителско име."));
                    return false;
                }

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        String response = plugin.getApiBase().userBalance().getBalance(args[1]);
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
                            String response = plugin.getApiBase().userBalance().addBalance(args[1], balance);
                            Gson gson = new Gson();
                            ApiRequestResponseMain responseResult = gson.fromJson(response, ApiRequestResponseMain.class);
                            sender.sendMessage(message(responseResult.message));
                        } catch (Exception e) {
                            plugin.getLogger().info(e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    sender.sendMessage(message("Сумата трябва да бъде число. Пример; addbalance Wohaho 15.50"));
                    return false;
                }

                return true;
            }
        }

        sender.sendMessage("§cUse §e/gcms reload, force, setserverkey, setscheduler, setapikey, addbalance, checkbalance");
        return true;

    }

    public String message(String message) {
        return ChatColor.translateAlternateColorCodes('&', "&a[GameCMS] &7" + message);
    }

}
