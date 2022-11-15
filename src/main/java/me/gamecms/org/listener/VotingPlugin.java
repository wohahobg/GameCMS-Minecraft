package me.gamecms.org.listener;

import com.bencodez.votingplugin.events.PlayerPostVoteEvent;
import com.bencodez.votingplugin.user.VotingPluginUser;
import me.gamecms.org.GameCMS;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VotingPlugin implements Listener {

    private final GameCMS plugin;

    public VotingPlugin(GameCMS plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onVotingPluginVote(PlayerPostVoteEvent event) {
        if (!plugin.isEnabled()) {
            return;
        }
        VotingPluginUser user = event.getUser();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            user.getUserData().setString("gamecms_last_time_vote", "" + System.currentTimeMillis());
            user.getUserData().setString("gamecms_last_site_vote", "" + event.getVoteSite().getServiceSite());
        });
    }

}
