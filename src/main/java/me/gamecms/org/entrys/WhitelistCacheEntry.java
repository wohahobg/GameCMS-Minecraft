package me.gamecms.org.entrys;

public class WhitelistCacheEntry {
    private final boolean isWhitelisted;
    private final long timestamp;

    public WhitelistCacheEntry(boolean isWhitelisted, long timestamp) {
        this.isWhitelisted = isWhitelisted;
        this.timestamp = timestamp;
    }

    public boolean isWhitelisted() {
        return isWhitelisted;
    }

    public long getTimestamp() {
        return timestamp;
    }
}