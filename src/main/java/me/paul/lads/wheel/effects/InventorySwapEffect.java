//package me.paul.lads.wheel.effects;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.Sound;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//
//import me.paul.lads.util.scheduler.Sync;
//import me.paul.lads.wheel.GenerateEffect;
//import me.paul.lads.wheel.Wheel;
//import me.paul.lads.wheel.WheelEffect;
//import net.md_5.bungee.api.ChatMessageType;
//import net.md_5.bungee.api.chat.TextComponent;
//
//@GenerateEffect(description = "Swaps everyone's inventory around", key = "effect_invswap", name = "Inventory Swap")
//public class InventorySwapEffect extends WheelEffect {
//
//	@Override
//	public void play(Player spinner, Wheel spun) {
//		Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " swapped everyone's inventories!");
//		for (Player p : Bukkit.getOnlinePlayers()) {
//			p.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "Inventory Changing!",
//					"Trading inventories with another player!", 10, 20 * 3, 10);
//			p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1f, 1f);
//			swap();
//		}
//	}
//	
//	public void swap() {
//		@SuppressWarnings("unchecked")
//		List<Player> players = (List<Player>) Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
//		Collections.shuffle(players);
//		Map<Player, ItemStack[]> playerMap = new LinkedHashMap<>();
//		players.forEach(p -> playerMap.put(p, p.getInventory().getContents()));
//		List<Map.Entry<Player, ItemStack[]>> playerEntries = new ArrayList<>(playerMap.entrySet());
//		
//		for (int i = 0; i < playerEntries.size(); i++) {
//			Map.Entry<Player, ItemStack[]> entry = playerEntries.get(i);
//			int nextIndex = (i == playerEntries.size() - 1) ? 0 : (i + 1);
//			Map.Entry<Player, ItemStack[]> swapTo = playerEntries.get(nextIndex);
//			Sync.get().delay(40).run(() -> {
//				((Player) entry.getKey()).getInventory().setContents(swapTo.getValue());
//				((Player) entry.getKey()).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
//						ChatColor.GREEN + ChatColor.BOLD.toString() + "You swapped inventories with: " + ChatColor.GOLD + ((Player) swapTo.getKey()).getName()));
//			});
//		}
//	}
//}
