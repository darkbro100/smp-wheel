package me.paul.foliastuff.wheel.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.paul.foliastuff.wheel.GenerateEffect;
import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "Swaps any tools the player has in their inventory with stone ones", key = "effect_toolswap", name = "Tool Swap")
public class ToolSwapEffect extends WheelEffect {

	private List<String> tools = Arrays.asList("pickaxe", "hoe", "axe", "shovel", "sword");
	String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[The Wacky" + ChatColor.RED + ChatColor.ITALIC + " WHEEL" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";

	@Override
	public void play(Player spinner, Wheel spun) {
		PlayerInventory pi = spinner.getInventory();
		ItemStack[] contents = pi.getContents();

		for (int i = 0; i < contents.length; i++) {
			ItemStack it = contents[i];

			if(it == null)
				continue;


			String name = it.getType().name();
			String[] parts = name.split("_");
			if(parts.length != 2)
				continue;

			String tool = parts[1].toLowerCase();
			if(tools.contains(tool)) {
				//Turn the tool to stone
				Material stoneTool = Material.getMaterial("STONE_" + tool.toUpperCase());
				it.setType(stoneTool);
			}
		}

		spinner.playSound(spinner.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
		sendTitle(spinner, ChatColor.DARK_RED + "Your tools are now", ChatColor.GRAY + " STONE.", 10, 20 * 5, 10);
		Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just had their tools ruined.");
	}

}
