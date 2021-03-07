package me.lightdream.wstore.pending;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.lightdream.wstore.WStore;
import me.lightdream.wstore.payment.Order;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class PendingOrder implements Listener {

	private WStore plugin;

	private File file;
	private FileConfiguration config;

	private Map<UUID, BukkitTask> tasks;

	public PendingOrder(WStore plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void initialize() {
		file = new File(WStore.getInstance().getDataFolder(), "pending_orders.yml");
		config = YamlConfiguration.loadConfiguration(file);
		tasks = new HashMap<>();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		System.out.println("[DEBUG]");
		Player player = event.getPlayer();

		BukkitTask pendingTask = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
			if (plugin.getPendingOrder().hasOrder(player.getName()))
				plugin.getPendingOrder().executeOrders(player.getName());
		}, 20 * 5);

		tasks.put(player.getUniqueId(), pendingTask);

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();

		if (tasks.containsKey(player.getUniqueId())) {

			BukkitTask pendingTask = tasks.get(player.getUniqueId());

			if (pendingTask != null) {
				pendingTask.cancel();
			}

			tasks.remove(player.getUniqueId());

		}

	}

	public void prepareOrder(String username, Order order) {

		config.set(username + "." + order.id + ".id", order.id);
		config.set(username + "." + order.id + ".username", order.username);
		config.set(username + "." + order.id + ".must_be_online", order.must_be_online);
		config.set(username + "." + order.id + ".commands", order.getCommands());
		config.set(username + "." + order.id + ".order_message", order.order_message);

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

	public boolean hasOrder(String username) {
		return config.isConfigurationSection(username);
	}
	
	public void executeOrders(String username) {
		
		Map<String, Order> orders = getOrders(username);
		
		orders.values().forEach(payment -> plugin.getWebStore().execute(payment));
		
		deleteOrder(username);
		
	}

	@SuppressWarnings("unchecked")
	public Map<String, Order> getOrders(String username) {

		Map<String, Order> orders = new HashMap<String, Order>();

		if (config.getString(username) == null) {
			return orders;
		}

		for (String order : config.getConfigurationSection(username).getKeys(false)) {

			boolean must_be_online = config.getBoolean(username + "." + order + ".must_be_online");
			List<String> commands = (List<String>) config.getList(username + "." + order + ".commands");
			String order_message = config.getString(username + "." + order + ".order_message");

			orders.put(order, new Order(order, username, must_be_online, commands, order_message));

		}

		return orders;

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
