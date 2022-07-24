package me.gamecms.org.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholdersBase extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "Wohaho";
    }

    @Override
    public String getIdentifier() {
        return "gamecms";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

}
