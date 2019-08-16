package me.paul.lads.wheel.effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "Gives the spinner of the wheel Speed 50", key = "effect_speed", name = "Speed L")
public class SpeedEffect extends WheelEffect {

	@Override
	public void play(Player spinner, Wheel spun) {
		spinner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 50));
		sendTitle(spinner, "Gotta go FAST.", "");
		Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " is fast as fuck BOYY");
	}

	
	
}
