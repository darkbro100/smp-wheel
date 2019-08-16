package me.paul.lads.wheel.effects;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

import me.paul.lads.Main;
import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;

@GenerateEffect(description = "wheel fucks off", key = "effect_fly", name = "Flying Wheel")
public class FlyingWheelEffect extends WheelEffect {
	
	private Wheel wheel;
	private Player spinner;

	@Override
	public void play(Player spinner, Wheel spun) {
		this.spinner = spinner;
		this.wheel = spun;
		
		new FlyingWheel().startFlight();
		Bukkit.broadcastMessage(ChatColor.GREEN + "The wheel has had enough of " + spinner.getName() + "'s shit and flew away.");
	}
	
	private class FlyingWheel {
		
		private int taskId;
		private List<FallingBlock> blocks = Lists.newArrayList();
		private double xVel;
		private double zVel;
		
		public FlyingWheel() {
			for (Entry<Vector, Material> entry : wheel.getCachedBlocks().entrySet()) {
				Vector v = entry.getKey();
				Material material = entry.getValue();
				Location loc = new Location(spinner.getWorld(), v.getBlockX(), v.getBlockY(), v.getBlockZ());
				
				loc.getBlock().setType(Material.AIR);
				FallingBlock fb = spinner.getWorld().spawnFallingBlock(loc.clone().add(0.5, 0.5, 0.5), new MaterialData(material));
				
				blocks.add(fb);
			}
			
		}
		
		private void startFlight() {
			xVel = Math.random();
			zVel = Math.random();
			
			taskId = new BukkitRunnable() {
				
				private int counter;
				
			    @Override
			    public void run() {
			    	blocks.forEach(b -> {
			    		// change speed of the flying effect
			    		b.setVelocity(new Vector(xVel, 0.8,zVel));
			    	});
			    	
			    	if (counter++ > 20 * 10)
			    		stop();
			    }
			}.runTaskTimer(Main.getInstance(), 0, 1).getTaskId();
		}
		
		private void stop() {
			for (Entry<Vector, Material> entry : wheel.getCachedBlocks().entrySet()) {
				Vector v = entry.getKey();
				Material material = entry.getValue();
				Location loc = new Location(spinner.getWorld(), v.getBlockX(), v.getBlockY(), v.getBlockZ());
				
				loc.getBlock().setType(material);
			}
			
			blocks.forEach(b -> b.remove());
			Bukkit.getScheduler().cancelTask(taskId);
		}
	}

}
