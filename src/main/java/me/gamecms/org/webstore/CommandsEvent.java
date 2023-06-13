package me.gamecms.org.webstore;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandsEvent extends Event {

	private static final HandlerList handlerList = new HandlerList();

	private CommandsHelper commands;

	public CommandsEvent(CommandsHelper commands) {
		this.commands = commands;
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
}
