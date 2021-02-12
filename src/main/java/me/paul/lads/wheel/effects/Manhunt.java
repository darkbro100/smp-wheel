package me.paul.lads.wheel.effects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import me.paul.lads.Main;
import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "Creates a Hunt on the player", key = "effect_manhunt", name = "Manhunt")
public class Manhunt extends WheelEffect implements Listener {
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
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (!p.isOp()) {
				e.setDamage(1);
			}
		}
	}
	
	@EventHandler
	public void onProjectileDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Projectile) {
		 Projectile p = (Projectile) e.getEntity();
		 if (p.getShooter() instanceof Player) {
			 Player a = (Player) p.getShooter();
			 if (!a.isOp()) {
				 e.setDamage(1);
			 }
		 }
		}
	}
	
	
	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(e.getEntity().getName().equalsIgnoreCase(spinner)){
			Player killer = e.getEntity().getKiller();
			killer.getInventory().addItem(new ItemStack(Material.DIRT, 10));
			killer.sendMessage(ChatColor.GREEN + "Psych! Enjoy the dirt. Idiot.");
			HandlerList.unregisterAll(this);	
		}
	}
}