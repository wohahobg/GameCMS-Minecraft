package me.gamecms.org.commands;

import me.gamecms.org.GameCMS;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandGameCMS implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length == 1) {

			if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {

				GameCMS.getInstance().getFileManager().initialize();
				GameCMS.getInstance().getWebStore().load();
				
				sender.sendMessage("§aThe configuration file has been changed.");
				
			} else if (args[0].equalsIgnoreCase("force")) {
				GameCMS.getInstance().getWebStore().execute(sender);
			} else {
				sender.sendMessage("§cThis command was not found!");
			}

			return true;
		}
		else if (args.length == 2) {
			if(args[0].equalsIgnoreCase("setKey")){
				GameCMS.getInstance().getWebStore().setKey(args[1]);
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[GameCMS] &7Key changed successfully"));
				GameCMS.getInstance().getConfig().set("server-key", args[1]);
				GameCMS.getInstance().saveConfig();
				return true;
			}
			else if (args[0].equalsIgnoreCase("setTime")){
				try {
					GameCMS.getInstance().getWebStore().setSchedule(Math.max(Long.parseLong(args[1]), 1200));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[GameCMS] &7Time changed successfully"));
					return true;
				} catch (NumberFormatException e) {
					sender.sendMessage("This is not a valid number");
				}
			}

		}

		sender.sendMessage("§cUse §e/gcms reload, force, setkey, settime");
		return true;

	}

}
