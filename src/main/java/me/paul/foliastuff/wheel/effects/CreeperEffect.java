package me.paul.foliastuff.wheel.effects;

import com.google.common.collect.Lists;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.Duration;
import me.paul.foliastuff.util.Util;
import me.paul.foliastuff.wheel.GenerateEffect;
import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Consumer;

@GenerateEffect(key = "creeper_fucked", description = "CREEPER-PHOBIA", name = "Creeper Rain")
public class CreeperEffect extends WheelEffect implements Listener {

  private List<Entity> ents = Lists.newArrayList();

  @Override
  public void play(Player spinner, Wheel spun) {
    // registre listener
    Bukkit.getPluginManager().registerEvents(this, FoliaStuff.getInstance());

    // broadcast
    Bukkit.broadcast(Component.text("Creeper time has begun... :)").color(TextColor.color(0, 120, 0)));

    Bukkit.getGlobalRegionScheduler().runDelayed(FoliaStuff.getInstance(), task -> {
      // broadcast
      Bukkit.broadcast(Component.text("Creeper time has ended... :(").color(TextColor.color(120, 0, 0)));

      // unregister listener
      HandlerList.unregisterAll(this);

      // despawn entities
      ents.forEach(ent -> ent.getScheduler().run(FoliaStuff.getInstance(), t -> ent.remove(), null));
      ents.clear();
    }, Duration.minutes(2).ticks());

    Bukkit.getGlobalRegionScheduler().runAtFixedRate(FoliaStuff.getInstance(), new Consumer<ScheduledTask>() {
      private int running;

      @Override
      public void accept(ScheduledTask task) {
        if (running >= Duration.minutes(2).toTicks()) {
          task.cancel();
          return;
        }

        if (running % 200 == 0 && Util.random(1, 5) == 1) {
          Bukkit.getOnlinePlayers().forEach(p -> {
            Location loc = p.getLocation();
            Vector direction = loc.getDirection().multiply(-1).normalize();
            Location toPlay = loc.clone().add(direction.multiply(2));
            p.playSound(toPlay, Sound.ENTITY_CREEPER_PRIMED, 1f, 1f);
          });
        }

        running++;
      }
    }, 1, 1);
  }

  @EventHandler
  public void onSpawn(CreatureSpawnEvent event) {
    if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DEFAULT || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
      Location loc = event.getEntity().getLocation();
      Creeper c = loc.getWorld().spawn(loc, Creeper.class);
      if (Util.random(1, 4) == 1)
        c.setPowered(true);

      ents.add(c);
    }
  }
}
