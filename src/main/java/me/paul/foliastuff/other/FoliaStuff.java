package me.paul.foliastuff.other;

import com.github.johnnyjayjay.spigotmaps.InitializationListener;
import lombok.Getter;
import me.paul.foliastuff.Case;
import me.paul.foliastuff.cmd.*;
import me.paul.foliastuff.listeners.CaseListener;
import me.paul.foliastuff.listeners.WheelListener;
import me.paul.foliastuff.util.SettingsManager;
import me.paul.foliastuff.wheel.WheelEffectManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoliaStuff extends JavaPlugin {

  @Getter
  private long startTime;

  public static final NamespacedKey KEY = NamespacedKey.fromString("folia-stuff");

  private static FoliaStuff instance;

  @Override
  public void onEnable() {
    // Plugin startup logic
    startTime = System.currentTimeMillis() / 1000L;
    instance = this;

    SettingsManager.getInstance().setup();
    SettingsManager.getInstance().loadCases();
    SettingsManager.getInstance().loadWheels();
    SettingsManager.getInstance().loadMapRenders();

    getCommand("wheel").setExecutor(new WheelCommand());
    getCommand("wheeleffect").setExecutor(new WheelEffectCommand());
    getCommand("displayname").setExecutor(new DisplayNameCommand());
    getCommand("gif").setExecutor(new DisplayImageCommand());
    getCommand("case").setExecutor(new TestCaseCommand());

    registerListeners();

    WheelEffectManager.getInstance();

    getCommand("home").setExecutor(new HomeCommand());
    getCommand("sethome").setExecutor(new SetHomeCommand());

    instance = this;

    InitializationListener.register(SettingsManager.getInstance(), this);
  }

  @Override
  public void onDisable() {
    SettingsManager.getInstance().saveWheels();
    SettingsManager.getInstance().saveMapRenders();
    SettingsManager.getInstance().saveCases();
  }

  private void registerListeners() {
    PluginManager pm = Bukkit.getPluginManager();
    pm.registerEvents(new WheelListener(), this);
    pm.registerEvents(new CaseListener(), this);
    pm.registerEvents(new TreeFellerListener(), this);
  }

  public static FoliaStuff getInstance() {
    return instance;
  }
}
