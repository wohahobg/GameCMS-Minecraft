package me.lightdream.wstore.payment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Order {

	public String id;
	public String username;
	public boolean must_be_online;
	public String commands;
	public String order_message;

	public Order(String id, String username, boolean must_be_online, List<String> commands, String order_message) {
		this.id = id;
		this.username = username;
		this.must_be_online = must_be_online;
		String var1 = "";
		for(String command : commands){
			var1+= command;
		}
		this.commands = var1;
		this.order_message = order_message;
	}


	public List<String> getCommands() {

		if (commands == null)
			return Collections.emptyList();

		commands = commands.replace("[", "");
		commands = commands.replace("]", "");
		commands = commands.replace("\"", "");

		return Arrays.asList(commands.split("\\s*,\\s*"));

	}

}
