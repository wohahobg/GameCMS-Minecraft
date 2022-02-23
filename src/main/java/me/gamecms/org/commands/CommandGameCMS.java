package me.gamecms.org.commands;

import me.gamecms.org.GameCMS;
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

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {

                GameCMS.getInstance().getFileManager().initialize();
                GameCMS.getInstance().getWebStore().load();

                sender.sendMessage(message("The configuration file has been changed"));

            } else if (args[0].equalsIgnoreCase("force")) {
                GameCMS.getInstance().getWebStore().execute(sender);
            } else {
                sender.sendMessage(message("This command was not found!"));
            }

            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setKey")) {
                GameCMS.getInstance().getWebStore().setKey(args[1]);
                sender.sendMessage(message("Server Key changed successfully"));
                GameCMS.getInstance().getConfig().set("server-key", args[1]);
                GameCMS.getInstance().saveConfig();
                return true;
            } else if (args[0].equalsIgnoreCase("setTime")) {
                try {
                    GameCMS.getInstance().getWebStore().setSchedule(Math.max(Long.parseLong(args[1]), 1200));
                    sender.sendMessage(message("Time changed successfully"));
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(message("This is not a valid number"));
                }
            } else if (args[0].equalsIgnoreCase("setApiKey")) {
                GameCMS.getInstance().getWebStore().setKey(args[1]);
                sender.sendMessage(message("Website API Key changed successfully"));
                GameCMS.getInstance().getConfig().set("api-key", args[1]);
                GameCMS.getInstance().saveConfig();
                return true;
            }else if (args[0].equalsIgnoreCase("checkBalance")){
                String response = plugin.checkBalance(args[1]);
                sender.sendMessage(message(response));
                return true;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("addBalance")) {
                double balance = 0;
                try {
                    balance = Double.parseDouble(args[2]);
                } catch (Exception e) {
                    sender.sendMessage(message("Сумата трябва да бъде число. Пример; addbalance Wohaho 15.50"));
                }
                if (balance != 0) {
                    String response = plugin.addBalance(args[1], balance);
                    sender.sendMessage(message(response));
                }else{
                    sender.sendMessage(message("Сумата трябва да бъде по-голяма от 0"));
                }

                return true;

            }
        }

        sender.sendMessage("§cUse §e/gcms reload, force, setkey, settime");
        return true;

    }

    public String message(String message) {
        return ChatColor.translateAlternateColorCodes('&',"&a[GameCMS] &7" + message);
    }

}
