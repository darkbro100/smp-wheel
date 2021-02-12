package me.paul.lads.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.paul.lads.wheel.Wheel;
import net.md_5.bungee.api.ChatColor;

public class WheelInteract implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = event.getClickedBlock();
			if(b.getType().name().contains("BUTTON")) {
				Wheel w = Wheel.get(b.getLocation());
				Player p = event.getPlayer();
				
				if(w == null) {
					return;
				}
				
				boolean res = w.spin(p);
				
				if(!res && w.isLocked()) {
					event.getPlayer().sendMessage(ChatColor.RED + "Wheel is currently on cooldown! Time remaining: " + w.getLastSpin().getRemainingTime());
					return;
				} else if(!res) {
					event.getPlayer().sendMessage(ChatColor.RED + "Wheel is already being spun!");
					return;
				} else {

				}
			}
		}
	}
	
}
