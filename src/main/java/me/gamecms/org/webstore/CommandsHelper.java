package me.gamecms.org.webstore;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandsHelper {

    public String id;
    public String username;
    public boolean must_be_online;
    public List<String> commands;

    // Constructor
    public CommandsHelper(String id, String username, boolean must_be_online, List<String> commands) {
        this.id = id;
        this.username = username;
        this.must_be_online = must_be_online;
        this.commands = commands;
    }

    // Getter
    public List<String> getCommands() {
        if (commands == null) {
            return Collections.emptyList();
        } else {
            return commands;
        }
    }
}

