package me.paul.lads.wheel.effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import me.paul.lads.util.scheduler.Sync;
import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "Zombie Apocalypse", key = "effect_zombie", name = "Zombie Apocalypse")
public class ZombieInvasion extends WheelEffect {
	String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[Wheel of" + ChatColor.RED + ChatColor.ITALIC
			+ " FEAR" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";

	@Override
	public void play(Player spinner, Wheel spun) {
		Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " has started the Zombie Apocalypse! Run!!");
		Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1f, 1f));
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			Location loc = p.getLocation().clone().add(0, 0, 5);
			Sync.get().cycles(25).delay(20).run(() -> {
				Zombie z = loc.getWorld().spawn(loc, Zombie.class);
				z.setBaby(false);
				z.setHealth(40);
				z.setTarget((LivingEntity) z.getNearbyEntities(50, 0, 50));
			});
		}
		
	}

}
