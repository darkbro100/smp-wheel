package me.paul.lads.wheel.effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.paul.lads.util.Meteor;
import me.paul.lads.util.Util;
import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "Strikes a meteor on the player", key = "wheel_meteor", name = "Meteor")
public class MeteorEffect extends WheelEffect {
	String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[The Wacky" + ChatColor.RED + ChatColor.ITALIC + " WHEEL" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";
	private static final int RADIUS = 3;
	
	@Override
	public void play(Player spinner, Wheel spun) {
		int xOff = Util.random(-16, 16);
		int zOff = Util.random(-16, 16);
		int yOff = Util.random(24, 48);
		
		Location spawnLoc = null;
		Location playerLoc = null;
		for (Player player : Bukkit.getOnlinePlayers()) { 
			spawnLoc = player.getLocation().clone().add(xOff, yOff, zOff);
			playerLoc = player.getLocation();
			Meteor m = new Meteor(RADIUS);
			m.spawn(spawnLoc);
			m.strike(playerLoc);
		}
//		Location spawnLoc = Bukkit.getOnlinePlayers().forEach(p -> p.getLocation().clone().add(xOff, yOff, zOff));
//		Meteor m = new Meteor(RADIUS);
//		m.spawn(spawnLoc);
//		m.strike(spinner.getLocation());
		
	}

}
