package me.gamecms.org.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.gamecms.org.GameCMS;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileManager {
	
	private GameCMS plugin;

	private static File file;
	private static FileConfiguration config;

	public FileManager(GameCMS plugin) {
		this.plugin = plugin;
	}

	public void initialize() {

		file = new File(GameCMS.getInstance().getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(file);
		
		if (!file.exists()) {
			
			config.addDefault("server-key", "server_key");
			config.addDefault("payments-scheduler", 1200);
			config.addDefault("broadcast-payment", true);
			config.addDefault("api-key", "your_api_key");
			config.options().copyDefaults(true);
			
			save();

		}else{
			//set api-key if does not exist
			if (!config.contains("api-key")){
				config.set("api-key", "your_api_key");
				save();
			}
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
	
	public GameCMS getPlugin() {
		return plugin;
	}

}
