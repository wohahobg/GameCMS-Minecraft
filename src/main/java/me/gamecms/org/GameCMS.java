package me.gamecms.org;

import me.gamecms.org.api.ApiBase;
import me.gamecms.org.commands.CommandGameCMS;
import me.gamecms.org.commands.CommandTabCompleter;
import me.gamecms.org.entrys.WhitelistCacheEntry;
import me.gamecms.org.files.ConfigFile;
import me.gamecms.org.listener.PlayerJoinQuit;
import me.gamecms.org.listener.VotingPlugin;
import me.gamecms.org.placeholders.Placeholders;
import me.gamecms.org.webstore.WebStore;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class GameCMS extends JavaPlugin{

    private static GameCMS instance;

    private ConfigFile configFile;
    private ApiBase apiBase;
    private WebStore webStore;
    private boolean placeholders;
    private PlayerJoinQuit playerListener;

    private final ConcurrentHashMap<String, WhitelistCacheEntry> whitelistCache = new ConcurrentHashMap<>();
    private final long CACHE_DURATION = TimeUnit.MINUTES.toMillis(2);

    public final String API_URL = "https://api.gamecms.org/v2";

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
        //load event listener
        getServer().getPluginManager().registerEvents(playerListener, this);
        getCommand("gcms").setExecutor(new CommandGameCMS(this));
        getCommand("gcms").setTabCompleter(new CommandTabCompleter());
    }

    @Override
    public void onDisable() {
        webStore.stop();
        playerListener.stopTasks();
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

    public ConcurrentHashMap<String, WhitelistCacheEntry> getWhitelistCache() {
        return whitelistCache;
    }

    public long getCACHE_DURATION() {
        return CACHE_DURATION;
    }
}
