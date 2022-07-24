package me.gamecms.org.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.gamecms.org.GameCMS;
import me.gamecms.org.utility.DurationHelper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigFile {

    private GameCMS plugin;

    private static File file;
    private static FileConfiguration config;


    public String serverKey;
    public String apiKey;
    public long commandsScheduler;
    public boolean broadcastCommandsMessage;
    public boolean usePlaceholders;


    public ConfigFile(GameCMS plugin) {
        this.plugin = plugin;
    }

    public void initialize() {

        file = new File(GameCMS.getInstance().getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            config.addDefault("server-key", "your_server_key");
            config.addDefault("commands-scheduler", 1200);
            config.addDefault("broadcast-commands-message", true);
            config.addDefault("api-key", "your_api_key");
            config.addDefault("use-placeholders", false);
            config.options().copyDefaults(true);
            save();
        } else {
            //set api-key if does not exist
            if (!config.contains("api-key")) {
                config.set("api-key", "your_api_key");
                save();
            }
            if (!config.contains("commands-scheduler")) {
                config.set("commands-scheduler", 1200);
                save();
            }
            if (!config.contains("broadcast-commands-message")) {
                config.set("broadcast-commands-message", true);
                save();
            }
            if (!config.contains("use-placeholders")) {
                config.set("use-placeholders", false);
                save();
            }
        }
        serverKey = config.getString("server-key");
        apiKey = config.getString("api-key");
        commandsScheduler = config.getLong("commands-scheduler");
        broadcastCommandsMessage = config.getBoolean("broadcast-commands-message");
        usePlaceholders = config.getBoolean("use-placeholders");
    }

    public void setServerKey(String key) {
        this.serverKey = key;
        config.set("server-key", key);
        save();
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setApiKey(String key) {
        this.apiKey = key;
        config.set("api-key", key);
        save();
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setCommandsScheduler(long schedule) {
        long minSchedule = DurationHelper.getTickDurationFromFormat("m", 1);

        if (schedule < minSchedule) {
            schedule = minSchedule;
        }

        this.commandsScheduler = schedule;
        config.set("commands-scheduler", schedule);
        save();
    }

    public long getCommandsScheduler() {
        return this.commandsScheduler;
    }

    public void setBroadcastCommandsMessage(boolean use) {
        this.broadcastCommandsMessage = use;
        config.set("broadcast-commands-message", use);
        save();
    }

    public boolean getBroadcastCommandsMessage() {
        return this.broadcastCommandsMessage;
    }

    public void setUsePlaceholders(boolean use) {
        this.usePlaceholders = use;
        config.set("use-placeholders", use);
        save();
    }

    public boolean getUsePlaceholders() {
        return this.usePlaceholders;
    }


    public void save() {

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static File getFile() {
        return file;
    }

    public GameCMS getPlugin() {
        return plugin;
    }

}
