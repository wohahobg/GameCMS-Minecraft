package me.lightdream.wstore;

import me.lightdream.wstore.commands.CommandWStore;
import me.lightdream.wstore.file.FileManager;
import me.lightdream.wstore.pending.PendingOrder;
import me.lightdream.wstore.webstore.WebStore;
import org.bukkit.plugin.java.JavaPlugin;

public class WStore extends JavaPlugin {

	private static WStore instance;

	private FileManager fileManager;
	private PendingOrder pendingOrder;
	private WebStore webStore;

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

		webStore = new WebStore(this);
		webStore.load();
		webStore.start();
		
		getCommand("w-store").setExecutor(new CommandWStore());

	}

	@Override
	public void onDisable() {
		webStore.stop();
	}

	public static WStore getInstance() {
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

}
