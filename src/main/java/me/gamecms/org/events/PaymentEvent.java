package me.gamecms.org.events;

import me.gamecms.org.payment.Commands;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PaymentEvent extends Event {
	
	private static final HandlerList handlerList = new HandlerList();
	
	private Commands order;
	
	public PaymentEvent(Commands order) {
		this.order = order;
	}
	
	public Commands getPayment() {
		return order;
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}
	
}
