package me.gamecms.org.webstore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.*;

import me.gamecms.org.GameCMS;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class PendingCommands implements Listener {

	private GameCMS plugin;

	private File file;
	private FileConfiguration config;

	private Map<UUID, BukkitTask> tasks;

	public PendingCommands(GameCMS plugin) {
		this.plugin = plugin;
	}

	public void initialize() {
		//TODO change pending_orders.yml to pending_commands.yml
		file = new File(GameCMS.getInstance().getDataFolder(), "pending_orders.yml");
		config = YamlConfiguration.loadConfiguration(file);
		tasks = new HashMap<>();
	}

	public void onPlayerJoin(Player player){
		if (plugin.getPendingCommands().hasCommands(player.getName())) {
			plugin.getPendingCommands().executeCommands(player.getName());
		}
	}

	public void saveCommand(String username, CommandsHelper command) {

		config.set(username + "." + command.id + ".id", command.id);
		config.set(username + "." + command.id + ".username", command.username);
		config.set(username + "." + command.id + ".must_be_online", command.must_be_online);
		config.set(username + "." + command.id + ".commands", command.getCommands());
		config.set(username + "." + command.id + ".order_message", command.order_message);

		save(file, config);
	}

	public void deleteOrder(String username) {

		for (String order : config.getConfigurationSection(username).getKeys(false)) {
			config.set(username + "." + order + ".id", null);
			config.set(username + "." + order + ".username", null);
			config.set(username + "." + order + ".must_be_online", null);
			config.set(username + "." + order + ".commands", null);
			config.set(username + "." + order + ".order_message", null);
			config.set(username + "." + order, null);
		}

		config.set(username, null);

		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean hasCommands(String username) {
		return config.isConfigurationSection(username);
	}

	public void executeCommands(String username) {

        Map<String, CommandsHelper> commandsMap = getUserCommands(username);

        commandsMap.values().forEach(command -> plugin.getWebStore().execute(command));

		deleteOrder(username);

	}

	@SuppressWarnings("unchecked")
	public Map<String, CommandsHelper> getUserCommands(String username) {

		Map<String, CommandsHelper> commands = new HashMap<String, CommandsHelper>();

		if (config.getString(username) == null) {
			return commands;
		}

		for (String command : config.getConfigurationSection(username).getKeys(false)) {

			boolean must_be_online = config.getBoolean(username + "." + command + ".must_be_online");
			List<String> userCommands = (List<String>) config.getList(username + "." + command + ".commands");
			String order_message = config.getString(username + "." + command + ".order_message");

			commands.put(command, new CommandsHelper(command, username, must_be_online, userCommands, order_message));

		}

		return commands;

	}

	private void save(File file, FileConfiguration config) {

		try {
			Writer writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
			writer.write(config.saveToString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
