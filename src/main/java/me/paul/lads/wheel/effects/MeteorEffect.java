package me.paul.lads.wheel.effects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.paul.lads.util.Meteor;
import me.paul.lads.util.Util;
import me.paul.lads.util.scheduler.Sync;
import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "Strikes a meteor on the player", key = "wheel_meteor", name = "Meteor")
public class MeteorEffect extends WheelEffect {
	private static final int RADIUS = 1;

	@Override
	public void play(Player spinner, Wheel spun) {
		int xOff = Util.random(-16, 16);
		int zOff = Util.random(-16, 16);
		int yOff = Util.random(24, 48);

		// Announce meteors incoming
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "INCOMING METEORS",
					"GET TO COVER!!!!", 10, 20 * 5, 10);
			for (int i = 0; i < 7; i++) {
				Sync.get().delay(i * 5).run(() -> {
					Bukkit.getOnlinePlayers()
							.forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10f, 0f));
				});
			}

			// Grab locations and send meteors.
	        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
	        for (int i = 0; i < players.size(); i++) {
	            Player p = players.get(i);
	            final Location spawnLoc = p.getLocation().clone().add(xOff, yOff, zOff);
	            final Location playerLoc = p.getLocation();

	            Sync.get().delay(60*10 + (1500 * i)).run(() -> {
	                Meteor m = new Meteor(RADIUS);
	                m.spawn(spawnLoc);
	                m.strike(playerLoc);
	            });
	        }
		}
	}
}
