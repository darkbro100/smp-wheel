package me.paul.foliastuff.wheel.effects;

import me.paul.foliastuff.util.Duration;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.wheel.GenerateEffect;
import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

@GenerateEffect(key = "random_mobs", description = "Spawn mobs on people", name = "Mob Frenzy!")
public class RandomMobEffect extends WheelEffect implements Listener {

  @Override
  public void play(Player spinner, Wheel spun) {
    // weighted mobs
    Map<EntityType, Integer> mobsWeighted = new HashMap<>();
    mobsWeighted.put(EntityType.ZOMBIE, 20);
    mobsWeighted.put(EntityType.CREEPER, 10);
    mobsWeighted.put(EntityType.SKELETON, 20);
    mobsWeighted.put(EntityType.WITCH, 20);
    mobsWeighted.put(EntityType.HOGLIN, 10);
    mobsWeighted.put(EntityType.CAVE_SPIDER, 10);
    mobsWeighted.put(EntityType.SILVERFISH, 10);
    mobsWeighted.put(EntityType.ILLUSIONER, 8);
    mobsWeighted.put(EntityType.BLAZE, 10);
    mobsWeighted.put(EntityType.MAGMA_CUBE, 10);
    mobsWeighted.put(EntityType.WARDEN, 2);

    Random random = new Random();

    //dramatic effect
    Location strike = new Location(spinner.getWorld(), 0, 66, 0);
    Bukkit.getWorld("world").strikeLightning(strike);
    Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just started a " + ChatColor.BLUE + ChatColor.BOLD.toString() + "MOB FRENZY!!");
    Bukkit.getOnlinePlayers().forEach(p -> {
      p.playSound(p.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1f, 1f);
    });

    //spawn mobs on everyone for 2 minutes every 10 seconds (120 / 10 = 12)
    Sync.get(spinner).interval(Duration.seconds(10).toTicks()).cycles(12).onCycleEnd(() -> {
      Bukkit.broadcastMessage(ChatColor.BLUE + "Mob frenzy is over :(");
    }).run(() -> {
      Bukkit.getOnlinePlayers().forEach(p -> {
        Location loc = p.getLocation().clone().add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
        EntityType randomMob = getRandomMob(mobsWeighted, random);
        Bukkit.getWorld(p.getWorld().getName()).spawnEntity(loc, randomMob);
      });
    });
  }

  private EntityType getRandomMob(Map<EntityType, Integer> mobsWeighted, Random random) {
    int totalWeight = mobsWeighted.values().stream().mapToInt(Integer::intValue).sum();
    int randomWeight = random.nextInt(totalWeight);

    for (Map.Entry<EntityType, Integer> entry : mobsWeighted.entrySet()) {
      randomWeight -= entry.getValue();
      if (randomWeight <= 0) {
        return entry.getKey();
      }
    }
    //incase something fucks up just send a zombie i guess fuck it
    return EntityType.ZOMBIE;
  }
}

