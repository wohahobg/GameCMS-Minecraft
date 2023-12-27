package me.gamecms.org.files;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class PluginConfigProperties implements SettingsHolder {

    @Comment("API key for your website. You can obtain it from gamecms.app/profile/my-website")
    public static final Property<String> WEBSITE_API_KEY = newProperty("website-api-key", "your_api_key");

    @Comment("API key for your server (where commands run). You can obtain it from Integrations > Servers")
    public static final Property<String> SERVER_API_KEY = newProperty("server-api-key", "your_api_key");

    @Comment("The interval (in ticks) for checking new commands to execute. 20 ticks = 1 second")
    public static final Property<Integer> COMMANDS_SCHEDULER = newProperty("commands-scheduler", 1200);

    @Comment({
            "Specify whether to use placeholders provided by PlaceholderAPI.",
            "Note: PlaceholderAPI plugin must be installed for placeholders to work."
    })
    public static final Property<Boolean> USE_PLACEHOLDERS = newProperty("use-placeholders", false);

    @Comment("Log message for fetched commands")
    public static final Property<Boolean> LOG_FETCHED_COMMANDS = newProperty("log-fetched-commands", true);

    @Comment({
            "Enable or disable the use of the whitelist feature based on user groups from the website.",
            "If true, the plugin will check if players belong to a group on the website that has the whitelist option enabled.",
            "Players in groups without the whitelist option enabled, or players not in any group, will not be allowed to join.",
            "Set to false to allow all players to join the server, regardless of their group's whitelist status on the website."
    })
    public static final Property<Boolean> USE_WHITELIST = newProperty("use-whitelist", false);

    @Comment({
            "Customize the message sent to players who are not allowed to join.",
            "This message will be displayed to players who try to join the server but",
            "are not logged into the website with the same IP address, or their group does not have whitelist permissions.",
            "Provide clear instructions on how they can meet the whitelist criteria."
    })
    public static final Property<List<String>> WHITELIST_MESSAGE = newListProperty("whitelist-message", Arrays.asList(
            "&cYou are not whitelisted",
            "&eTo join, you must be in a whitelist-enabled group.",
            "&aLog in at yourwebsite.com with this IP."
    ));
    @Comment({
            "List of player names that are explicitly whitelisted.",
            "Players in this list will be allowed to join regardless of other whitelist settings."
    })
    public static final Property<List<String>> WHITELISTED_NAMES = newListProperty("whitelisted-names", Arrays.asList("Notch", "Wohaho"));


}


