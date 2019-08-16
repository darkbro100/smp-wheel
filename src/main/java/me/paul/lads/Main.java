package me.paul.lads;

import java.util.List;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.johnnyjayjay.spigotmaps.InitializationListener;

import lombok.Getter;
import me.paul.lads.cmd.DonationCommand;
import me.paul.lads.cmd.LinkTwitchCommand;
import me.paul.lads.cmd.WheelCommand;
import me.paul.lads.cmd.WheelEffectCommand;
import me.paul.lads.listeners.WheelInteract;
import me.paul.lads.streamlabs.LabUtil;
import me.paul.lads.util.SettingsManager;
import me.paul.lads.wheel.WheelEffectManager;
import net.md_5.bungee.api.ChatColor;

@Getter
public class Main extends JavaPlugin implements Listener {
	

	public static void main(String[] args) {
	}
	
	@Getter
	private static Main instance = null;
	
	private Thread discordThread;
	
	@Getter
	private long startTime;
	
	public void onEnable() {
		startTime = System.currentTimeMillis() / 1000L;
		instance = this;
		
		SettingsManager.getInstance().setup();
		SettingsManager.getInstance().loadWheels();
		SettingsManager.getInstance().loadMapRenders();
		LabUtil.getInstance();
		
		getCommand("wheel").setExecutor(new WheelCommand());
		getCommand("donation").setExecutor(new DonationCommand());
		getCommand("linktwitch").setExecutor(new LinkTwitchCommand());
		getCommand("wheeleffect").setExecutor(new WheelEffectCommand());
		
		registerListeners();
		
		WheelEffectManager.getInstance();
		//InitializationListener.register(SettingsManager.getInstance(), this);
	}
	
	@Override
	public void onDisable() {
		SettingsManager.getInstance().saveWheels();
		SettingsManager.getInstance().saveLabUsers();
		SettingsManager.getInstance().saveMapRenders();
	}
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new WheelInteract(), this);
		pm.registerEvents(this, this);
		
	}
	
	@EventHandler
	public void onMapInitialize(MapInitializeEvent event) {
		MapView view = event.getMap();
		
		if(SettingsManager.getInstance().provide(view.getId()) != null) {
			List<MapRenderer> renders = SettingsManager.getInstance().provide(view.getId());
			view.getRenderers().clear();
			renders.forEach(view::addRenderer);
		}	
	}
		
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		EntityDamageEvent cause = e.getEntity().getLastDamageCause();
		if(cause != null && cause.getCause() == DamageCause.VOID) { 
			e.setDeathMessage(ChatColor.BLUE + e.getEntity().getName() + " has sacrificed their body to the V O I D");
			Location location = e.getEntity().getLocation();
			e.getEntity().getWorld().strikeLightning(location);
			e.getEntity().getWorld().strikeLightning(location);
			e.getEntity().getWorld().strikeLightning(location);
		}
	
	}
}
	

