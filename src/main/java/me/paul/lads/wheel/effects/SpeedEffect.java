package me.paul.lads.wheel.effects;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;

@GenerateEffect(description = "Gives the spinner of the wheel Speed 50", key = "effect_speed", name = "Speed L")
public class SpeedEffect extends WheelEffect {

	@Override
	public void play(Player spinner, Wheel spun) {
		spinner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 50));
		sendTitle(spinner, "You've been gifted Speed L");
	}

	
	
}
