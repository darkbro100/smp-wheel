package me.paul.foliastuff.other;

import com.github.johnnyjayjay.spigotmaps.InitializationListener;
import io.github.miniplaceholders.api.Expansion;
import lombok.Getter;
import me.paul.foliastuff.CaseItem;
import me.paul.foliastuff.CaseStats;
import me.paul.foliastuff.cmd.*;
import me.paul.foliastuff.listeners.CaseListener;
import me.paul.foliastuff.listeners.TreeFellerListener;
import me.paul.foliastuff.listeners.WheelListener;
import me.paul.foliastuff.util.NMS;
import me.paul.foliastuff.util.SettingsManager;
import me.paul.foliastuff.util.Util;
import me.paul.foliastuff.util.entity.CustomPanda;
import me.paul.foliastuff.util.gui.listener.GuiListener;
import me.paul.foliastuff.util.scheduler.TaskBuilder;
import me.paul.foliastuff.wheel.WheelEffectManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

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

    // Init nms stuff
    String ver = Util.getMinecraftRevision();
    try {
      NMS.init(ver);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      Bukkit.shutdown();
    }

    getCommand("wheel").setExecutor(new WheelCommand());
    getCommand("wheeleffect").setExecutor(new WheelEffectCommand());
    getCommand("displayname").setExecutor(new DisplayNameCommand());
    getCommand("gif").setExecutor(new DisplayImageCommand());
    getCommand("test").setExecutor(new TestCommand());

    CaseCommand cmd = new CaseCommand();
    getCommand("case").setExecutor(cmd);
    getCommand("case").setTabCompleter(cmd);

    registerListeners();

    WheelEffectManager.getInstance();

    getCommand("home").setExecutor(new HomeCommand());
    getCommand("sethome").setExecutor(new SetHomeCommand());

    instance = this;

    InitializationListener.register(SettingsManager.getInstance(), this);

    registerExpansion();

    // pls work
    NMS.registerEntityClass(CustomPanda.class, CustomPanda.createAttributes(), "custom_panda");

    // delay settings stuff to make sure all plugins are loaded
    SettingsManager.getInstance().setup();

    if (TaskBuilder.isFoliaSupported()) {
      Bukkit.getGlobalRegionScheduler().runDelayed(this, task -> {
        initStorageStuff();
      }, 40);
    } else {
      Bukkit.getScheduler().runTaskLater(this, this::initStorageStuff, 40);
    }
  }

  private void initStorageStuff() {
    SettingsManager.getInstance().loadCases();
    SettingsManager.getInstance().loadWheels();
    SettingsManager.getInstance().loadMapRenders();
    SettingsManager.getInstance().loadAllCaseStats();
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
    pm.registerEvents(new GuiListener(), this);
  }

  public void registerExpansion() {
    final Expansion.Builder expansionBuilder = Expansion.builder("case")
      .filter(Player.class)
      .audiencePlaceholder("name", (audience, ctx, queue) -> {
        final Player player = (Player) audience;
        return Tag.selfClosingInserting(player.name());
      })
      .audiencePlaceholder("player_total_opens", (audience, ctx, queue) -> {
        final Player player = (Player) audience;
        CaseStats stats = CaseStats.get(player.getUniqueId());
        return Tag.selfClosingInserting(Component.text(Util.format(stats.totalOpens())));
      })
      .audiencePlaceholder("total_emeralds_spent", (audience, ctx, queue) -> {
        final Player player = (Player) audience;
        CaseStats stats = CaseStats.get(player.getUniqueId());
        return Tag.selfClosingInserting(Component.text(Util.format(stats.totalOpens() * 3)));
      })
      .globalPlaceholder("total_opens", (ctx, queue) -> {
        int total = 0;
        for (CaseStats stats : CaseStats.getAll())
          total += stats.totalOpens();

        return Tag.selfClosingInserting(Component.text(Util.format(total)));
      })
      .globalPlaceholder("server_total_emeralds_spent", (ctx, queue) -> {
        int total = 0;
        for (CaseStats stats : CaseStats.getAll())
          total += (stats.totalOpens() * 3);

        return Tag.selfClosingInserting(Component.text(Util.format(total)));
      });

    for (CaseItem.CaseRarity rarity : CaseItem.CaseRarity.values()) {
      expansionBuilder.audiencePlaceholder(rarity.name().toLowerCase() + "_total_opens", (audience, ctx, queue) -> {
        final Player player = (Player) audience;
        CaseStats stats = CaseStats.get(player.getUniqueId());
        return Tag.selfClosingInserting(Component.text(Util.format(stats.getCaseOpens(rarity))));
      });
      expansionBuilder.globalPlaceholder("total_" + rarity.name().toLowerCase() + "_opens", (ctx, queue) -> {
        int total = 0;
        for (CaseStats stats : CaseStats.getAll())
          total += stats.getCaseOpens(rarity);

        return Tag.selfClosingInserting(Component.text(Util.format(total)));
      });
      expansionBuilder.audiencePlaceholder(rarity.name().toLowerCase() + "_total_percentage", (audience, ctx, queue) -> {
        final Player player = (Player) audience;
        CaseStats stats = CaseStats.get(player.getUniqueId());

        DecimalFormat df = new DecimalFormat("##.##");
        return Tag.selfClosingInserting(Component.text(df.format(stats.getChance(rarity) * 100)));
      });
      expansionBuilder.globalPlaceholder("total_" + rarity.name().toLowerCase() + "_percentage", (ctx, queue) -> {
        double totalRarityOpens = 0;
        double total = 0;
        for (CaseStats caseStats : CaseStats.getAll()) {
          total += caseStats.totalOpens();
          totalRarityOpens += caseStats.getCaseOpens(rarity);
        }
        double chance = totalRarityOpens / total;

        DecimalFormat df = new DecimalFormat("##.##");
        return Tag.selfClosingInserting(Component.text(df.format(chance * 100)));
      });
    }

    Expansion expansion = expansionBuilder.build();
    expansion.register();
  }

  public static FoliaStuff getInstance() {
    return instance;
  }
}
