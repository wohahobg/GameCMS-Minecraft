package me.gamecms.org.webstore;

import java.util.ArrayList;
import java.util.logging.Level;

import me.gamecms.org.GameCMS;
import me.gamecms.org.events.PaymentEvent;
import me.gamecms.org.payment.Commands;
import me.gamecms.org.payment.PaymentRequestResponse;
import me.gamecms.org.utility.DurationHelper;
import me.gamecms.org.utility.HTTPRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import com.google.gson.Gson;

public class WebStore {

    private GameCMS plugin;

    private String key;

    private long schedule;
    private boolean shouldBroadcast;

    private BukkitTask task;

    private final Gson gson = new Gson();
    private String API;

    public WebStore(GameCMS plugin) {
        this.plugin = plugin;
        API = plugin.API_URL + "/commands";
    }

    public void load() {

        long minSchedule = DurationHelper.getTickDurationFromFormat("m", 1);

        if (schedule < minSchedule) {
            schedule = minSchedule;
        }

        this.key = plugin.getFileManager().getString("server-key");
        this.schedule = plugin.getFileManager().getLong("payments-scheduler");
        this.shouldBroadcast = plugin.getFileManager().getBoolean("broadcast-payment");

    }

    public void start() {

        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {

                try {

                    plugin.getLogger().log(Level.INFO, "Fetching all due players...");

                    ArrayList<Commands> orders = plugin.getWebStore().getPayments();

                    for (Commands order : orders) {

                        if (order.must_be_online && Bukkit.getPlayer(order.username) == null) {
                            plugin.getPendingOrder().prepareOrder(order.username, order);
                            continue;
                        }

                        execute(order);

                    }

                    if (!orders.isEmpty()) {

                        try {
                            plugin.getWebStore().completePayments();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    plugin.getLogger().log(Level.INFO, "Fetched due players (" + orders.size() + " found).");

                } catch (Exception e) {
                    plugin.getLogger().log(Level.INFO, "GameCMS seems to be offline right now. The data has been saved and will be executed soon.");
                }

            }

        }, schedule, schedule);

    }

    public void stop() {

        if (task != null) {
            task.cancel();
        }

    }

    public void execute(CommandSender sender) {

        if (sender == null) {
            plugin.getLogger().log(Level.INFO, "Fetching all due players...");
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            try {
                ArrayList<Commands> orders = plugin.getWebStore().getPayments();

                for (Commands order : orders) {
                    //TODO: Remove the comment
                    if (order.must_be_online && Bukkit.getPlayer(order.username) == null) {
                        plugin.getPendingOrder().prepareOrder(order.username, order);
                        continue;
                    }
                    execute(order);
                }

                if (!orders.isEmpty()) {

                    try {
                        plugin.getWebStore().completePayments();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                if (sender == null) {
                    plugin.getLogger().log(Level.INFO, "Fetched due players (" + orders.size() + " found).");
                } else {
                    sender.sendMessage("§aFetched due players §8(§e" + orders.size() + " found§8)§7.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().log(Level.INFO, "GameCMS seems to be offline right now. The data has been saved and will be executed soon.");
            }

        });

    }

    public void execute(Commands order) {

        if (shouldBroadcast())
            if (order.order_message != null)
                if (!order.order_message.equals(""))
                    Bukkit.broadcastMessage(order.order_message.replace('&', '§'));

        Bukkit.getScheduler().runTask(plugin, () -> {
            order.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            plugin.getServer().getPluginManager().callEvent(new PaymentEvent(order));
        });

    }

    public ArrayList<Commands> getPayments() throws Exception {

        ArrayList<Commands> orders = new ArrayList<>();

        String json = HTTPRequest.sendGET(API + "/queue/minecraft", key);

        if (json.startsWith("{\"status\":\"200\"")) {
            orders = (ArrayList<Commands>) ((gson.fromJson(json, PaymentRequestResponse.class)).data);
        }
        return orders;

    }

    public void completePayments() throws Exception {
        HTTPRequest.sendGET(API + "/complete", key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSchedule(long schedule) {

        long minSchedule = DurationHelper.getTickDurationFromFormat("m", 1);

        if (schedule < minSchedule) {
            schedule = minSchedule;
        }

        this.schedule = schedule;
        this.plugin.getFileManager().getConfig().set("payments-scheduler", schedule);
        this.plugin.getFileManager().save();

    }

    public long getSchedule() {
        return schedule;
    }

    public boolean shouldBroadcast() {
        return shouldBroadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.shouldBroadcast = broadcast;
        this.plugin.getFileManager().getConfig().set("broadcast-payment", broadcast);
        this.plugin.getFileManager().save();
    }

}
