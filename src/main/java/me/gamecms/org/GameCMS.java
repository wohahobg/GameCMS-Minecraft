package me.gamecms.org;

import fi.iki.elonen.NanoHTTPD;
import me.gamecms.org.api.ApiBase;
import me.gamecms.org.commands.CommandGameCMS;
import me.gamecms.org.commands.CommandTabCompleter;
import me.gamecms.org.files.ConfigFile;
import me.gamecms.org.listener.PlayerJoinQuit;
import me.gamecms.org.listener.VotingPlugin;
import me.gamecms.org.placeholders.Placeholders;
import me.gamecms.org.webstore.WebStore;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class GameCMS extends JavaPlugin{

    private static GameCMS instance;

    private ConfigFile configFile;
    private ApiBase apiBase;
    private WebStore webStore;
    private boolean placeholders;
    private PlayerJoinQuit playerListener;

    private final long CACHE_DURATION = TimeUnit.MINUTES.toMillis(2);

    public final String API_URL = "https://api.gamecms.org/v2";
    private GameCMSHttpServer httpSever;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        configFile = new ConfigFile(this);

        apiBase = new ApiBase(this);

        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
           placeholders = new Placeholders(this).register();
        }

        if (this.getServer().getPluginManager().getPlugin("VotingPlugin") != null){
            VotingPlugin votingPlugin = new VotingPlugin(this);
            getServer().getPluginManager().registerEvents(votingPlugin, this);
        }

        webStore = new WebStore(this);
        playerListener = new PlayerJoinQuit(this);

//        // Start the HTTP server with the port from the config file
//        int httpPort = getConfigFile().getHttpPort();
//        try {
//          httpSever = new GameCMSHttpServer(this, httpPort);
//        } catch (IOException e) {
//            getLogger().severe("Failed to start the HTTP server: " + e.getMessage());
//        }

        //load event listener
        getServer().getPluginManager().registerEvents(playerListener, this);
        getCommand("gcms").setExecutor(new CommandGameCMS(this));
        getCommand("gcms").setTabCompleter(new CommandTabCompleter());
    }

    @Override
    public void onDisable() {
        webStore.stop();
        playerListener.stopTasks();
//        try {
//            httpSever.stop();
//        } catch (Exception e) {
//            getLogger().severe("Error while stopping the HTTP server: " + e.getMessage());
//        }
    }

    public static GameCMS getInstance() {
        return instance;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }

    public WebStore getWebStore() {
        return webStore;
    }

    public ApiBase getApiBase() {
        return apiBase;
    }
    public long getCACHE_DURATION() {
        return CACHE_DURATION;
    }
}
