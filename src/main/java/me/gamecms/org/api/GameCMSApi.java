package me.gamecms.org.api;

import me.gamecms.org.GameCMS;
import me.gamecms.org.payment.Order;
import org.bukkit.Bukkit;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;


public class GameCMSApi {

	// Set the processing of payments as done.
	public static void completePayments() throws Exception {
		GameCMS.getInstance().getWebStore().completePayments();
	}

	// Displays all open payments.
	public static Collection<Order> getPayments() throws Exception {
		return GameCMS.getInstance().getWebStore().getPayments();
	}

	// Enables / disables broadcast messages on purchase. (order_message)
	public static void toggleBroadcast() {
		GameCMS.getInstance().getWebStore().setBroadcast((GameCMS.getInstance().getWebStore().shouldBroadcast() ? false : true));
	}

	// Sets the time when should be checked each time. (The number of ticks cannot
	// be set less than one minute.)
	public static void setSchedule(long scheduleTicks) {
		GameCMS.getInstance().getWebStore().setSchedule(scheduleTicks);
	}

	// Stores the player's payment in 'pending_orders' when not online.
	public static void prepareOrder(String username, Order order) {
		GameCMS.getInstance().getPendingOrder().prepareOrder(username, order);
	}

	// Clears the player's saved payments that need to be executed.
	public static void deleteOrder(String username) {
		GameCMS.getInstance().getPendingOrder().deleteOrder(username);
	}

	// Checks whether the player's orders are saved for execution.
	public static boolean hasOrder(String username) {
		return GameCMS.getInstance().getPendingOrder().hasOrder(username);
	}

	// Executes all saved payments.
	public static void executeOrders(String username) {
		GameCMS.getInstance().getPendingOrder().executeOrders(username);
	}

	// Shows all saved payments of the player.
	public static Map<String, Order> getOrders(String username) {
		return GameCMS.getInstance().getPendingOrder().getOrders(username);
	}


}
