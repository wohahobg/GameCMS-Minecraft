package me.gamecms.org;

import me.gamecms.org.api.ApiBase;
import me.gamecms.org.commands.CommandGameCMS;
import me.gamecms.org.commands.CommandTabCompleter;
import me.gamecms.org.file.ConfigFile;
import me.gamecms.org.listener.PlayerListener;
import me.gamecms.org.webstore.PendingCommands;
import me.gamecms.org.placeholders.PlaceholdersRegister;
import me.gamecms.org.webstore.WebStore;
import org.bukkit.plugin.java.JavaPlugin;


public class GameCMS extends JavaPlugin{

    private static GameCMS instance;

    private ConfigFile configFile;
    private ApiBase apiBase;
    private PendingCommands pendingCommands;
    private WebStore webStore;
    private PlaceholdersRegister placeholdersRegister;

    public final String API_URL = "https://api.gamecms.org/v2";

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        configFile = new ConfigFile(this);
        configFile.initialize();

        //load the api
        apiBase = new ApiBase(this);

        //load pending commands
        pendingCommands = new PendingCommands(this);
        pendingCommands.initialize();

        //register placeholder if PlaceholderAPI exist
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholdersRegister = new PlaceholdersRegister(this);
        }


        //load webstore tasks
        //se we can check simple every x times for new commands
        webStore = new WebStore(this);
        webStore.load();
        webStore.start();


        //load event listener
        getServer().getPluginManager().registerEvents(new PlayerListener (this), this);
        getCommand("gcms").setExecutor(new CommandGameCMS(this));
        getCommand("gcms").setTabCompleter(new CommandTabCompleter());
    }



    @Override
    public void onDisable() {
        webStore.stop();
    }


    public static GameCMS getInstance() {
        return instance;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }

    public PendingCommands getPendingCommands() {
        return pendingCommands;
    }

    public WebStore getWebStore() {
        return webStore;
    }

    public ApiBase getApiBase() {
        return apiBase;
    }

    public PlaceholdersRegister getPlaceholdersRegister(){
        return placeholdersRegister;
    }

}
