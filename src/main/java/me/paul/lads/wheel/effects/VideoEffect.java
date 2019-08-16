package me.paul.lads.wheel.effects;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "Tells whoever spun the wheel to play a video", key = "effect_video", name = "Play Video")
public class VideoEffect extends WheelEffect {
	static String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[The Wacky" + ChatColor.RED + ChatColor.ITALIC
			+ " WHEEL" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";

	private static final String[] messages = { 
			prefix + " Watch this on stream: https://www.youtube.com/watch?v=MF7ql39_ufY",
			prefix + " Watch this on stream: https://youtu.be/hr7GyFM7pX4",
			prefix + " Watch this on stream: https://youtu.be/qtWVp2VOt1s",
			prefix + " Watch this on stream: https://youtu.be/bMEFj5C7oIc",
			prefix + " Watch this on stream: https://youtu.be/h2FiBAeTZls",
			prefix + " Watch this on stream: https://youtu.be/zpJAHBTA2k0",
			prefix + " Watch this on stream: https://youtu.be/rzLIUgnKY40",
			prefix + " Watch this on stream: https://youtu.be/eaIYmu4Xm9E",
			prefix + " Watch this on stream: https://youtu.be/0xENjGS9Sz4",
			prefix + " Watch this on stream: https://youtu.be/oahDLU64gPQ",
			prefix + " Watch this on stream: https://youtu.be/R8FVKVnYfY8",
			prefix + " Watch this on stream: https://youtu.be/vmIUvp0e1bw",
			prefix + " Watch this on stream: https://youtu.be/1yFGouySlxA",
			prefix + " Watch this on stream: https://youtu.be/qadN5lH14uw",
			prefix + " Watch this on stream: https://www.youtube.com/watch?v=e6mfgX9TdCg",
			prefix + " Watch this on stream: https://youtu.be/6np67Gosmq0",
			prefix + " Watch this on stream: https://youtu.be/exooWcoIAKQ"
			};

	private Random random = new Random();

	@Override
	public void play(Player spinner, Wheel spun) {
		spinner.sendMessage(getMessage());
		Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " is watching a video.");

	}

	public String getMessage() {
		return messages[random.nextInt(messages.length)];

	}
}
