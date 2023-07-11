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

public class PendingCommands implements Listener {

    private GameCMS plugin;

    private File file;
    private FileConfiguration fileConfiguration;

    public PendingCommands(GameCMS plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        file = new File(GameCMS.getInstance().getDataFolder(), "pending-commands.yml");
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void onPlayerJoin(Player player) {
        if (plugin.getPendingCommands().hasCommands(player.getName())) {
            plugin.getPendingCommands().executeCommands(player.getName());
        }
    }

    public void saveCommand(String username, CommandsHelper command) {

        String cleanUsername = username.trim();

        fileConfiguration.set(cleanUsername + "." + command.id + ".id", command.id);
        fileConfiguration.set(cleanUsername + "." + command.id + ".username", command.username);
        fileConfiguration.set(cleanUsername + "." + command.id + ".must_be_online", command.must_be_online);
        fileConfiguration.set(cleanUsername + "." + command.id + ".commands", command.getCommands());

        save(file, fileConfiguration);
    }

    public void deleteCommands(String username) {

        for (String order : fileConfiguration.getConfigurationSection(username).getKeys(false)) {
            fileConfiguration.set(username + "." + order + ".id", null);
            fileConfiguration.set(username + "." + order + ".username", null);
            fileConfiguration.set(username + "." + order + ".must_be_online", null);
            fileConfiguration.set(username + "." + order + ".commands", null);
            fileConfiguration.set(username + "." + order, null);
        }

        fileConfiguration.set(username, null);

        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean hasCommands(String username) {
        return fileConfiguration.isConfigurationSection(username);
    }

    public void executeCommands(String username) {

        Map<String, CommandsHelper> commandsMap = getUserCommands(username);

        commandsMap.values().forEach(command -> plugin.getWebStore().execute(command));

        deleteCommands(username);

    }

    @SuppressWarnings("unchecked")
    public Map<String, CommandsHelper> getUserCommands(String username) {

        Map<String, CommandsHelper> commands = new HashMap<String, CommandsHelper>();

        if (fileConfiguration.getString(username) == null) {
            return commands;
        }

        for (String command : fileConfiguration.getConfigurationSection(username).getKeys(false)) {

            boolean must_be_online = fileConfiguration.getBoolean(username + "." + command + ".must_be_online");
            List<String> userCommands = (List<String>) fileConfiguration.getList(username + "." + command + ".commands");

            commands.put(command, new CommandsHelper(command, username, must_be_online, userCommands));

        }

        return commands;

    }

    private void save(File file, FileConfiguration config) {

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
            writer.write(config.saveToString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
