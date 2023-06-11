package me.paul.foliastuff.wheel.effects;//package me.paul.lads.wheel.effects;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.Sound;
//import org.bukkit.entity.Player;
//import org.bukkit.entity.TNTPrimed;
//
//import me.paul.lads.util.scheduler.Sync;
//import me.paul.lads.wheel.GenerateEffect;
//import me.paul.lads.wheel.Wheel;
//import me.paul.lads.wheel.WheelEffect;
//
//@GenerateEffect(description = "Explodes everyones beds", key = "effect_bed", name = "Explode Beds")
//public class BedExplodeEffect extends WheelEffect {
//	String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[The Wacky" + ChatColor.RED + ChatColor.ITALIC
//			+ " WHEEL" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me: ";
//
//	@Override
//	public void play(Player spinner, Wheel spun) {
//
//		Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(ChatColor.DARK_RED + "Wouldn't wanna sleep in your bed.",
//				"There's a suprise at home.", 10, 20 * 5, 10));
//		Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_DEATH, 1f, 1f));
//		Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " left you a present at home.");
//
//		List<Location> locations = new ArrayList<>();
//		for (Player player : Bukkit.getOnlinePlayers()) {
//			try {
//				Location bedLoc = player.getBedSpawnLocation();
//				if (bedLoc == null) {
//					player.teleport(player.getLocation().clone().add(0, 50, 0));
//					player.sendMessage(prefix + "Oh you think you're safe because you don't have a bed? Wrong. Up you go!");
//					continue;
//				} else {
//					locations.add(bedLoc);
//				}
//			} catch (Exception e) {
//			}
//		}
//		Sync.get().cycles(5).delay(40).run(() -> {
//			for (Location l : locations) {
//				Location bedLoc = l.clone().add(0, 5, 0);
//				bedLoc.getWorld().spawn(bedLoc, TNTPrimed.class);
//			}
//		});
//	}
//}
