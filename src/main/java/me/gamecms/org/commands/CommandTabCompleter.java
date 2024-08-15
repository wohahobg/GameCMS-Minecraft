package me.gamecms.org.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandTabCompleter implements TabCompleter {

    private static final String[] COMMANDS = {
            "reload",
            "force",
            "setServerApiKey",
            "setScheduler",
            "setWebsiteApiKey",
            "placeholdersToggle",
            "getBalance",
            "checkBalance",
            "addBalance",
            "Verify"
    };

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> lowerCaseCommands = Arrays.stream(COMMANDS)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0].toLowerCase(), lowerCaseCommands, completions);
        Collections.sort(completions);
        return completions;
    }
}
