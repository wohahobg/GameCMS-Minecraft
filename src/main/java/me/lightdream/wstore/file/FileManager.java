package me.lightdream.wstore.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.lightdream.wstore.WStore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileManager {
	
	private WStore plugin;

	private static File file;
	private static FileConfiguration config;

	public FileManager(WStore plugin) {
		this.plugin = plugin;
	}

	public void initialize() {

		file = new File(WStore.getInstance().getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(file);
		
		if (!file.exists()) {
			
			config.addDefault("server-key", "api_key");
			config.addDefault("payments-scheduler", 1200);
			config.addDefault("broadcast-payment", true);
			config.options().copyDefaults(true);
			
			save();
			
		} 

	}
	
	public void save() {
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String getString(String object) {
		return config.getString(object);
	}

	public Integer getInt(String object) {
		return config.getInt(object);
	}

	public Long getLong(String object) {
		return config.getLong(object);
	}

	public Boolean getBoolean(String object) {
		return config.getBoolean(object);
	}

	public List<String> getList(String object) {
		return config.getStringList(object);
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public static File getFile() {
		return file;
	}
	
	public WStore getPlugin() {
		return plugin;
	}

}
