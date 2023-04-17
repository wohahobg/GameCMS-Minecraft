package me.gamecms.org.listener;

import com.bencodez.votingplugin.VotingPluginHooks;
import com.bencodez.votingplugin.advancedcore.api.user.userstorage.DataType;
import com.bencodez.votingplugin.advancedcore.api.user.userstorage.mysql.MySQL;
import com.bencodez.votingplugin.events.PlayerPostVoteEvent;
import com.bencodez.votingplugin.user.VotingPluginUser;
import me.gamecms.org.GameCMS;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class VotingPlugin implements Listener {

    private final GameCMS plugin;
    private String storageType;

    public VotingPlugin(GameCMS plugin) {
        this.plugin = plugin;
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            VotingPluginHooks votingPluginHooks = VotingPluginHooks.getInstance();
            storageType = String.valueOf(votingPluginHooks.getMainClass().getStorageType());
            if (storageType == "MYSQL") {
                MySQL mySql = votingPluginHooks.getMainClass().getMysql();
                mySql.alterColumnType("gamecms_last_time_vote", "BIGINT DEFAULT '0'");
                mySql.alterColumnType("gamecms_last_site_vote", "MEDIUMTEXT");
            }
        }, 50);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVotingPluginVote(PlayerPostVoteEvent event) {
        if (!plugin.isEnabled()) {
            return;
        }
        VotingPluginUser user = event.getUser();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (storageType == "MYSQL") {
                user.getUserData().setString("gamecms_last_time_vote", "" + System.currentTimeMillis() / 1000);
                user.getUserData().setString("gamecms_last_site_vote", "" + event.getVoteSite().getServiceSite());
            }
        });
    }

}
