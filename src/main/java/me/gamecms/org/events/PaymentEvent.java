package me.gamecms.org.events;

import me.gamecms.org.payment.Order;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PaymentEvent extends Event {
	
	private static final HandlerList handlerList = new HandlerList();
	
	private Order order;
	
	public PaymentEvent(Order order) {
		this.order = order;
	}
	
	public Order getPayment() {
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
