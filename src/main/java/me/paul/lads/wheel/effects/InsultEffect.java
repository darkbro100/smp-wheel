//package me.paul.lads.wheel.effects;
//
//import java.util.Random;
//
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//
//import me.paul.lads.wheel.GenerateEffect;
//import me.paul.lads.wheel.Wheel;
//import me.paul.lads.wheel.WheelEffect;
//import me.paul.lads.wheel.WheelEffectManager;
//import me.paul.lads.wheel.WheelImageEffect;
//import net.md_5.bungee.api.ChatColor;
//
//@GenerateEffect(description = "Insults whoever spun this wheel", key = "effect_insult", name = "Insult Player")
//public class InsultEffect extends WheelEffect {
//
//	private static final String[] messages = {"You are a fool!", 
//			"Your stream just sucks...",
////			"I wish gravity would stop working so I could float as far away from you as possible",
//			"You are the child of an oaf", 
////			"Every time you stream everyone just gets a little big sadder",
//			"The Wheel deems you a lowly being", 
//			"You might as well stop streaming", 
//			"Go jump off a building",
//			"Go outside loser", 
////			"Ever heard of going outside? You should try it", 
//			"Get a tan idiot" };
//
//	private Random random = new Random();
//	
//	@Override
//	public void play(Player spinner, Wheel spun) {
//		sendTitle(spinner, getMessage(), "", 10, 20 * 6, 10);
//		Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just got their feelings hurt.");
//	}
//
//	public String getMessage() {
//		return messages[random.nextInt(messages.length)];
//	}
//
//}
