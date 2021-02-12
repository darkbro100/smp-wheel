package me.paul.lads;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.paul.lads.cmd.WheelCommand;
import me.paul.lads.cmd.WheelEffectCommand;
import me.paul.lads.listeners.WheelInteract;
import me.paul.lads.util.SettingsManager;
import me.paul.lads.wheel.WheelEffectManager;

@Getter
public class Main extends JavaPlugin implements Listener {
	

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
		
		getCommand("wheel").setExecutor(new WheelCommand());
		getCommand("wheeleffect").setExecutor(new WheelEffectCommand());
		
		registerListeners();
		
		WheelEffectManager.getInstance();
		//InitializationListener.register(SettingsManager.getInstance(), this);
	}
	
	@Override
	public void onDisable() {
		SettingsManager.getInstance().saveWheels();
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
}
	

