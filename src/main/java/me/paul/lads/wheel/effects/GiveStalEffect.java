package me.paul.lads.wheel.effects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "Gives someone the Stal Music Disc", key = "effect_stal", name = "Give Stal")
public class GiveStalEffect extends WheelEffect {

	@Override
	public void play(Player spinner, Wheel spun) {
		spinner.sendMessage(ChatColor.GREEN + "Can I introduce you one of the HOTTEST tracks on the market today? Let me introduce to you " + ChatColor.BLUE + "Stal");
		spinner.getInventory().addItem(new ItemStack(Material.MUSIC_DISC_STAL));
	}

}
