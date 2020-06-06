package me.paul.lads.wheel.effects;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.paul.lads.util.scheduler.Sync;
import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "Troll users with sound", key = "effect_sound", name = "Sound Troll")
public class SoundMislead extends WheelEffect {

	@Override
	public void play(Player spinner, Wheel spun) {
		ArrayList<Sound> sounds = new ArrayList<>();
		sounds.add(Sound.ENTITY_CREEPER_PRIMED);
		sounds.add(Sound.ENTITY_GHAST_SCREAM);
		sounds.add(Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR);
		sounds.add(Sound.ENTITY_TNT_PRIMED);
		sounds.add(Sound.ENTITY_ELDER_GUARDIAN_HURT);
		
		Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(ChatColor.DARK_RED + "Don't trust everything you hear.", ChatColor.GRAY + "It may not even be real.", 10, 20*6, 10));
		
		Sync.get().cycles(5).interval(2400).delay(1000).run(() -> {
			int sound = new Random().nextInt(sounds.size());
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), sounds.get(sound), 1f, 1f);
			}
		});
	}

}
