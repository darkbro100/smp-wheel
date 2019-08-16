package me.paul.lads.wheel.effects;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "Tells whoever spun the wheel to buy some merch", key = "effect_merch", name = "Buy Merch")
public class MerchEffect extends WheelEffect {
	static String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[Wheel of" + ChatColor.RED + ChatColor.ITALIC
			+ " FEAR" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";

	private static final String[] messages = { 
			prefix + " How about some annoying orange? https://amzn.to/30pxb3D",
			prefix + " I'm feeling like you should support Smosh. https://smosh.store/",
			prefix + " You remember =3 dude? I remember. https://bit.ly/2Z7DeNl",
			prefix + " aha YES dude I'm such a fan of Markiplier. https://markiplier.com/collections/m-logo-tees",
			prefix + " jacksepticeye is like the irish pewdiepie so hes really cool. https://jacksepticeye.com/collections/frontpage",
			prefix + " REP the game that pays your rent. https://www.minecraftshop.com/"
			};

	private Random random = new Random();

	@Override
	public void play(Player spinner, Wheel spun) {
		spinner.sendMessage(getMessage());
		Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " is looking at some new Merch options.");

	}

	public String getMessage() {
		return messages[random.nextInt(messages.length)];

	}
}
