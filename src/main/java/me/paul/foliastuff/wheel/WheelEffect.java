package me.paul.foliastuff.wheel;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * This is the most abstract version of a wheel effect. This is a one time execution that happens when the wheel finishes spinning
 * @author Paul
 *
 */
public abstract class WheelEffect {

	protected final String PREFIX = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[The Wacky" + ChatColor.RED + ChatColor.ITALIC + " WHEEL" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";

	/**
	 * Play a WheelEffect for a Player
	 * @param spinner Player to play the effect to
	 */
	public abstract void play(Player spinner, Wheel spun);
	
	/**
	 * Send a title message to a player
	 * @param player Player receiving the message
	 * @param title Title message
	 */
	public void sendTitle(Player player, String title, String subtitle) {
		sendTitle(player, title, subtitle, 10, 20 * 10, 10);
	}
	
	/**
	 * Send a title message to a player
	 * @param player Player receiving the message
	 * @param title Title message
	 * @param subtitle Subtitle message (empty string if none)
	 * @param fadeIn How long (in ticks) the title fades in for
	 * @param stay How long (in ticks) the title stays on the screen
	 * @param fadeOut How long (in ticks) the title fades out for
	 */
	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		player.sendTitle(title, subtitle, 10, 20 * 10, 10);
	}
	
	/**
	 * Sends a message via the Action Bar to a Player
	 * @param player Player receiving the action bar message
	 * @param message Contents of the action bar message
	 */
	public void sendActionBar(Player player, String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
	}
	
	/**
	 * Broadcast a message to the server
	 * @param message Message being broadcasted to the server
	 */
	public void broadcast(String message) {
		for(Player p : Bukkit.getOnlinePlayers())
			p.sendMessage(message);
		Bukkit.getConsoleSender().sendMessage(message);
	}
	
	/**
	 * Broadcast a message to the server via the action bar
	 * @param message Message to send via the action bar
	 */
	public void broadcastActionBar(String message) {
		for(Player p : Bukkit.getOnlinePlayers())
			sendActionBar(p, message);
		Bukkit.getConsoleSender().sendMessage(message);
	}
	
	/**
	 * Broadcast a message to the server via the title bar
	 * @param title Title being sent
	 */
	public void broadcastTitle(String title) {
		broadcastTitle(title, "", 10, 20 * 5, 10);
	}
	
	/**
	 * Broadcast a message to the server via the title bar
	 * @param title Title being sent
	 * @param subtitle Subtitle being sent
	 * @param fadeIn Fade in time (in ticks)
	 * @param stay Stay time (in ticks)
	 * @param fadeOut Fade out time (in ticks)
	 */
	public void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		for(Player p : Bukkit.getOnlinePlayers())
			sendTitle(p, title, subtitle, 10, 20 * 10, 10);
		Bukkit.getConsoleSender().sendMessage(title);
		Bukkit.getConsoleSender().sendMessage(subtitle);
	}
	
}
