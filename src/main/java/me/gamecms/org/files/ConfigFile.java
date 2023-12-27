package me.gamecms.org.files;

import ch.jalu.configme.SettingsManager;

import me.gamecms.org.GameCMS;

import ch.jalu.configme.SettingsManagerBuilder;

import java.io.File;
import java.util.List;


public class ConfigFile {

    private final SettingsManager settings;

    public ConfigFile(GameCMS plugin) {
        settings = SettingsManagerBuilder
                .withYamlFile(new File(plugin.getDataFolder(), "config.yml"))
                .configurationData(PluginConfigProperties.class)
                .useDefaultMigrationService()
                .create();
    }

    public SettingsManager getSettings(){
        return settings;
    }

    public void setWebsiteApiKey(String key) {
        settings.setProperty(PluginConfigProperties.WEBSITE_API_KEY, key);
        settings.save();
    }

    public String getWebsiteApiKey() {
        return settings.getProperty(PluginConfigProperties.WEBSITE_API_KEY);
    }


    public void setServerApiKey(String key) {
        settings.setProperty(PluginConfigProperties.SERVER_API_KEY, key);
        settings.save();
    }

    public String getServerApiKey() {
        return settings.getProperty(PluginConfigProperties.SERVER_API_KEY);
    }

    public void setCommandsScheduler(Integer schedule) {
        settings.setProperty(PluginConfigProperties.COMMANDS_SCHEDULER, schedule);
        settings.save();
    }

    public long getCommandsScheduler() {
        return settings.getProperty(PluginConfigProperties.COMMANDS_SCHEDULER);
    }

    public final boolean getLogFetchedCommands(){
        return settings.getProperty(PluginConfigProperties.LOG_FETCHED_COMMANDS);
    }

    public void setUsePlaceholders(boolean use) {
        settings.setProperty(PluginConfigProperties.USE_PLACEHOLDERS, use);
        settings.save();
    }

    public boolean isPlaceholdersEnabled() {
        return settings.getProperty(PluginConfigProperties.USE_PLACEHOLDERS);
    }

    public boolean isWhitelistEnabled() {
        return settings.getProperty(PluginConfigProperties.USE_WHITELIST);
    }

    public List<String> getWhitelistMessage() {
        return settings.getProperty(PluginConfigProperties.WHITELIST_MESSAGE);
    }

    public List<String> getWhitelistedNamed() {
        return settings.getProperty(PluginConfigProperties.WHITELISTED_NAMES);
    }

    public void saveWhitelist(List list){
        settings.setProperty(PluginConfigProperties.WHITELISTED_NAMES, list);
    }

}
