package me.paul.lads.wheel.effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.paul.lads.Main;
import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import me.paul.lads.wheel.WheelEffectManager;
import me.paul.lads.wheel.WheelImageEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "Creates a Hunt on the player", key = "effect_manhunt", name = "Manhunt")
public class HuntEffect extends WheelEffect implements Listener {
	String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[Wheel of" + ChatColor.RED + ChatColor.ITALIC
			+ " FEAR" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";
	private String spinner;
	@Override
	public void play(Player spinner, Wheel spun) {
		this.spinner = spinner.getName();
		spinner.sendMessage(prefix + " You've become a hunted man.");
		Bukkit.broadcastMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "FIRST PERSON TO KILL " + ChatColor.YELLOW
				+ spinner.getName() + ChatColor.GREEN + ChatColor.BOLD + " gets 10 DIAMONDS!!!");
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(e.getEntity().getName().equalsIgnoreCase(spinner)){
			Player killer = e.getEntity().getKiller();
			((WheelImageEffect)WheelEffectManager.getInstance().getTrollEffect()).giveMap(killer);
			HandlerList.unregisterAll(this);	
		}
	}
}
