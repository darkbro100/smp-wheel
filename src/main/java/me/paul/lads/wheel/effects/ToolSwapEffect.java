package me.paul.lads.wheel.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;

@GenerateEffect(description = "Swaps any tools the player has in their inventory with stone ones", key = "effect_toolswap", name = "Tool Swap")
public class ToolSwapEffect extends WheelEffect {

	private List<String> tools = Arrays.asList("pickaxe", "hoe", "axe", "shovel", "sword");
	
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

		sendTitle(spinner, "Your tools have been turned to stone!");
	}

}
