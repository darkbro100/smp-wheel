package me.paul.lads.wheel.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.paul.lads.util.scheduler.Sync;
import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

@GenerateEffect(description = "Swaps everyone's location", key = "wheel_swap", name = "Location Swap")
public class LocationSwapEffect extends WheelEffect {

	@Override
	public void play(Player spinner, Wheel spun) {
//		List<Location> playerLoc = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(ChatColor.BLUE + ChatColor.BOLD.toString() + "Random Telport",
					"Trading places with another player!", 10, 20 * 5, 10);
			p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
//			Location loc = p.getLocation();
//			playerLoc.add(loc);
		}

		Sync.get().delay(40).run(() -> {
			Teleport();
//			for (Player p : Bukkit.getOnlinePlayers()) {
//				int locNumber = new Random().nextInt(playerLoc.size());
//				Location newLoc = playerLoc.get(locNumber);
//				p.teleport(newLoc);
//			}
		});

	}

	public void Teleport() {
		@SuppressWarnings("unchecked")
		List<Player> players = (List<Player>) Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
		Collections.shuffle(players);
		Map<Player, Location> playerMap = new LinkedHashMap<>();
		players.forEach(p -> playerMap.put(p, p.getLocation()));
		List<Map.Entry<Player, Location>> playerEntries = new ArrayList<>(playerMap.entrySet());
		
		for (int i = 0; i < playerEntries.size(); i++) {
			Map.Entry<Player, Location> entry = playerEntries.get(i);
			int nextIndex = (i == playerEntries.size() - 1) ? 0 : (i + 1);
			Map.Entry<Player, Location> swapTo = playerEntries.get(nextIndex);
			((Player) entry.getKey()).teleport(swapTo.getValue());
			((Player) entry.getKey()).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
					ChatColor.GREEN + ChatColor.BOLD.toString() + "You swapped with: " + ChatColor.GOLD + ((Player) swapTo.getKey()).getName()));
		}

	}

}
