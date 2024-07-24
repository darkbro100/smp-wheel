package me.paul.foliastuff.wheel.effects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

import me.paul.foliastuff.wheel.GenerateEffect;
import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;

@GenerateEffect(description = "Explodes your bed", key = "effect_bed", name = "Explode Bed")
public class BedExplodeEffect extends WheelEffect {
	String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[The Wacky" + ChatColor.RED + ChatColor.ITALIC
			+ " WHEEL" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me: ";

	@Override
	public void play(Player spinner, Wheel spun) {

    spinner.playSound(spinner.getLocation(), Sound.ENTITY_BAT_DEATH, 1f, 1f);
		spinner.sendTitle(ChatColor.DARK_RED + "Wouldn't wanna sleep in your bed.",
				"There's a surprise at home.", 10, 20 * 5, 10);

    Location loc = spinner.getRespawnLocation();

    if (loc != null) {
      loc.getWorld().spawn(loc, TNTPrimed.class);
      Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just had their bed exploded.");
    } else {
      spinner.teleport(spinner.getLocation().clone().add(0, 50, 0));
      spinner.sendMessage(prefix + "Oh, you think you're safe because you don't have a bed? Wrong. Up you go!");
    }
	}
}
