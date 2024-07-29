package me.gamecms.org.files;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;


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

    @Comment("HTTP port for accepting requests from GameCMS.ORG. This port is used for retrieving player information and executing server commands.")
    public static final Property<Integer> HTTP_PORT = newProperty("http-port", 0);

}


