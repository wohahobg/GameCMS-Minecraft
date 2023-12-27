package me.gamecms.org;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class MessageFormatter {
    /**
     * Formats a list of message lines, translating color codes and joining them into a single string.
     *
     * @param lines The list of strings, each representing a line in the message.
     * @return A formatted string with color codes translated and lines joined.
     */
    public static String formatMessage(List<String> lines) {
        return lines.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.joining("\n"));
    }
}
