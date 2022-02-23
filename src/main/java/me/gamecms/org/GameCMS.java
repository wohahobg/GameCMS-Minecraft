package me.gamecms.org;

import me.gamecms.org.balance.Balance;
import me.gamecms.org.commands.CommandGameCMS;
import me.gamecms.org.file.FileManager;
import me.gamecms.org.pending.PendingOrder;
import me.gamecms.org.webstore.WebStore;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class GameCMS extends JavaPlugin {

	private static GameCMS instance;

	private FileManager fileManager;
	private PendingOrder pendingOrder;
	private WebStore webStore;
	private Balance balance;
	public final String API_URL = "https://api.gamecms.org";

	@Override
	public void onLoad() {
		instance = this;
	}

	@Override
	public void onEnable() {
		
		fileManager = new FileManager(this);
		fileManager.initialize();

		pendingOrder = new PendingOrder(this);
		//getServer().getPluginManager().registerEvents(pendingOrder, this);
		pendingOrder.initialize();

		balance = new Balance(this);

		webStore = new WebStore(this);
		webStore.load();
		webStore.start();
		getCommand("gcms").setExecutor(new CommandGameCMS(this));

	}

	@Override
	public void onDisable() {
		webStore.stop();
	}

	public static GameCMS getInstance() {
		return instance;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public PendingOrder getPendingOrder() {
		return pendingOrder;
	}

	public WebStore getWebStore() {
		return webStore;
	}

	public String addBalance(String username, double amount) {
		 return balance.add(username, amount);
	}
	public String checkBalance(String username) {
		return balance.checkBalance(username);
	}
}
