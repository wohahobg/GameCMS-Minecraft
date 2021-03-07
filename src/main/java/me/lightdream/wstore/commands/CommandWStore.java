package me.lightdream.wstore.commands;

import me.lightdream.wstore.WStore;
import me.lightdream.wstore.webstore.WebStore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandWStore implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length == 1) {

			if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {

				WStore.getInstance().getFileManager().initialize();
				WStore.getInstance().getWebStore().load();
				
				sender.sendMessage("§aThe configuration file has been changed.");
				
			} else if (args[0].equalsIgnoreCase("force")) {
				WStore.getInstance().getWebStore().execute(sender);				
			} else {
				sender.sendMessage("§cThis command was not found!");
			}

			return true;
		}
		else if (args.length == 2) {
			if(args[0].equalsIgnoreCase("setKey")){
				WStore.getInstance().getWebStore().setKey(args[1]);
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[W-Store] &7Key changed successfully"));
				WStore.getInstance().getConfig().set("server-key", args[1]);
				WStore.getInstance().saveConfig();
				return true;
			}
			else if (args[0].equalsIgnoreCase("setTime")){
				try {
					WStore.getInstance().getWebStore().setSchedule(Math.max(Long.parseLong(args[1]), 1200));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[W-Store] &7Time changed successfully"));
					return true;
				} catch (NumberFormatException e) {
					sender.sendMessage("This is not a valid number");
				}
			}

		}

		sender.sendMessage("§cUse §e/wstore reload, force, setkey, settime");
		return true;

	}

}
