//package me.paul.lads.wheel.effects;
//
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.Sound;
//import org.bukkit.entity.Player;
//
//import me.paul.lads.util.Meteor;
//import me.paul.lads.util.Util;
//import me.paul.lads.util.scheduler.Sync;
//import me.paul.lads.wheel.GenerateEffect;
//import me.paul.lads.wheel.Wheel;
//import me.paul.lads.wheel.WheelEffect;
//import net.md_5.bungee.api.ChatColor;
//
//@GenerateEffect(description = "Strikes a meteor on the player", key = "wheel_meteor", name = "Meteor")
//public class MeteorEffect extends WheelEffect {
//	private static final int RADIUS = 2;
//
//	@Override
//	public void play(Player spinner, Wheel spun) {
//		int xOff = Util.random(-16, 16);
//		int zOff = Util.random(-16, 16);
//		int yOff = Util.random(24, 48);
//
//		// Announce meteors incoming
//		for (Player player : Bukkit.getOnlinePlayers()) {
//			player.sendTitle(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "INCOMING METEORS",
//					"10 SECONDS TO IMPACT", 10, 20 * 5, 10);
//			for (int i = 0; i < 7; i++) {
//				Sync.get().delay(i * 5).run(() -> {
//					Bukkit.getOnlinePlayers()
//							.forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10f, 0f));
//				});
//			}
//
//			// Grab locations and send meteors.
//			Sync.get().delay(20 * 10).run(() -> {
//				Location spawnLoc = null;
//				Location playerLoc = null;
//				for (Player p : Bukkit.getOnlinePlayers()) {
//					spawnLoc = p.getLocation().clone().add(xOff, yOff, zOff);
//					playerLoc = p.getLocation();
//					Meteor m = new Meteor(RADIUS);
//					m.spawn(spawnLoc);
//					m.strike(playerLoc);
//				}
//			});
//		}
//	}
//}
