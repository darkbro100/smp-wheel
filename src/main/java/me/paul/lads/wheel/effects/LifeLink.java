package me.paul.lads.wheel.effects;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import me.paul.lads.Main;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

public class LifeLink extends WheelEffect implements Listener {
	String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[The Wacky" + ChatColor.RED + ChatColor.ITALIC+ " WHEEL" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";
	private String spinner;

	public void play(Player spinner, Wheel spun) {
		this.spinner = spinner.getName();
		spinner.sendMessage(String.valueOf(this.prefix) + " You life is now LINKED.");
		Bukkit.broadcastMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "LIFE LINK! If one person dies, EVERYONE dies.");
		Bukkit.getPluginManager().registerEvents(this, (Plugin) Main.getInstance());
	}
	boolean looping = false;
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(e.getEntity() instanceof Player && !looping){
			looping = true;
			e.setDeathMessage("");
			String dumbass = e.getEntity().getDisplayName();
			Bukkit.getOnlinePlayers().forEach(player -> {
			    if(player.getHealth() > 0 && !player.isDead()) {
			        player.setHealth(0);
			    }
			});
			looping = false;
			Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.MUSIC_DISC_WARD, 1.0f, 1.0f));
			Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_DEATH, 1.0f, 1.0f));
			Bukkit.broadcastMessage(ChatColor.YELLOW + dumbass + ChatColor.RED + " died and ruined it for everyone.");
				HandlerList.unregisterAll(this);
		}
	}
}

