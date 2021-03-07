package me.lightdream.wstore.webstore;

import java.util.ArrayList;
import java.util.logging.Level;

import me.lightdream.wstore.WStore;
import me.lightdream.wstore.events.PaymentEvent;
import me.lightdream.wstore.payment.Order;
import me.lightdream.wstore.payment.PaymentRequestResponse;
import me.lightdream.wstore.utility.DurationHelper;
import me.lightdream.wstore.utility.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import com.google.gson.Gson;

public class WebStore {
	
	private WStore plugin;
	
	private String key;
	
	private long schedule;	
	private boolean shouldBroadcast;

	private BukkitTask task;
	
	private final Gson gson = new Gson();
	private final String API = "https://api.w-store.org";
	
	public WebStore(WStore plugin) {
		this.plugin = plugin;
	}
	
	public void load() {
		
		long minSchedule = DurationHelper.getTickDurationFromFormat("m", 1);

		if (schedule < minSchedule) {
			schedule = minSchedule;
		}
		
		this.key = plugin.getFileManager().getString("server-key");
		this.schedule = plugin.getFileManager().getLong("payments-scheduler");
		this.shouldBroadcast = plugin.getFileManager().getBoolean("broadcast-payment");
		
	}
	
	public void start() {

		task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {

				try {
					
					plugin.getLogger().log(Level.INFO, "Fetching all due players...");

					ArrayList<Order> orders = plugin.getWebStore().getPayments();

					for (Order order : orders) {

						if (order.must_be_online && Bukkit.getPlayer(order.username) == null) {
							plugin.getPendingOrder().prepareOrder(order.username, order);
							continue;
						}

						execute(order);

					}

					if (!orders.isEmpty()) {

						try {
							plugin.getWebStore().completePayments();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					plugin.getLogger().log(Level.INFO, "Fetched due players (" + orders.size() + " found).");
					
				} catch (Exception e) {
					plugin.getLogger().log(Level.INFO, "W-Store seems to be offline right now. The data has been saved and will be executed soon.");
				}

			}

		}, schedule, schedule);

	}
	
	public void stop() {
		
		if(task != null) {
			task.cancel();
		}
		
	}
	
	public void execute(CommandSender sender) {

		if (sender == null) {
			plugin.getLogger().log(Level.INFO, "Fetching all due players...");
		}
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

			try {
				ArrayList<Order> orders = plugin.getWebStore().getPayments();

				for (Order order : orders) {
					//TODO: Remove the comment
					if (order.must_be_online && Bukkit.getPlayer(order.username) == null) {
						plugin.getPendingOrder().prepareOrder(order.username, order);
						continue;
					}
					execute(order);
				}

				if (!orders.isEmpty()) {

					try {
						plugin.getWebStore().completePayments();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				if (sender == null) {
					plugin.getLogger().log(Level.INFO, "Fetched due players (" + orders.size() + " found).");
				} else {
					sender.sendMessage("§aFetched due players §8(§e" + orders.size() + " found§8)§7.");
				}

			} catch (Exception e) {
				e.printStackTrace();
				plugin.getLogger().log(Level.INFO, "W-Store seems to be offline right now. The data has been saved and will be executed soon.");
			}

		});

	}
	
	public void execute(Order order) {
		
		if(shouldBroadcast())
			if(order.order_message != null)
				if(!order.order_message.equals(""))
					Bukkit.broadcastMessage(order.order_message.replace('&', '§'));

		Bukkit.getScheduler().runTask(plugin, () -> {
			order.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
			plugin.getServer().getPluginManager().callEvent(new PaymentEvent(order));
		});
		
	}
	
	public ArrayList<Order> getPayments() throws Exception {

		ArrayList<Order> orders = new ArrayList<>();

		String json = HTTPRequest.readUrl(API + "/orders/queue/commands/" + key);

		if (json.startsWith("{\"status\":\"100\"")){
			orders = (ArrayList<Order>) ((gson.fromJson(json, PaymentRequestResponse.class)).orders);
		}
		return orders;

	}

	public void completePayments() throws Exception {
		HTTPRequest.openUrl(API + "/orders/complete/commands/" + key);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key){
		this.key = key;
	}

	public void setSchedule(long schedule) {

		long minSchedule = DurationHelper.getTickDurationFromFormat("m", 1);

		if (schedule < minSchedule) {
			schedule = minSchedule;
		}

		this.schedule = schedule;
		this.plugin.getFileManager().getConfig().set("payments-scheduler", schedule);
		this.plugin.getFileManager().save();

	}

	public long getSchedule() {
		return schedule;
	}

	public boolean shouldBroadcast() {
		return shouldBroadcast;
	}

	public void setBroadcast(boolean broadcast) {
		this.shouldBroadcast = broadcast;
		this.plugin.getFileManager().getConfig().set("broadcast-payment", broadcast);
		this.plugin.getFileManager().save();
	}

}
