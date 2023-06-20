package me.paul.foliastuff.wheel.effects;

import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.wheel.GenerateEffect;
import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.atomic.AtomicBoolean;

@GenerateEffect(description = "Link everyone's lives", key = "effect_lifelink", name = "LifeLink")
public class LifeLink extends WheelEffect implements Listener {
  private String spinner;

  public void play(Player spinner, Wheel spun) {
    this.spinner = spinner.getName();
    spinner.sendMessage(PREFIX + " Your life is now LINKED.");
    Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(ChatColor.RED + "Your life is now LINKED", ChatColor.DARK_RED + this.spinner + " dies, everyone dies.", 10, 20 * 5, 10));
    for (int i = 0; i < 7; i++) {
      Sync.get(spinner).delay(i * 5).run(() -> Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10f, 0f)));
    }
    Bukkit.getPluginManager().registerEvents(this, FoliaStuff.getInstance());
  }

  AtomicBoolean looping = new AtomicBoolean(false);

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    if (!looping.get() && e.getEntity().getName().equalsIgnoreCase(spinner)) {
      looping.set(true);
      e.deathMessage(Component.empty());

      Component dumbass = e.getEntity().displayName();

      // ensure this is wrapped in a sync block, so folia doesn't bork itself
      Bukkit.getOnlinePlayers().forEach(player -> Sync.get(player).run(() -> {
        player.playSound(player.getLocation(), Sound.MUSIC_DISC_WARD, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 1.0f, 1.0f);

        if (player.getHealth() > 0 && !player.isDead())
          player.setHealth(0);
      }));
      looping.set(false);

      Bukkit.broadcast(dumbass.append(Component.text(" died and ruined it for everyone.").color(TextColor.color(125, 0, 0))));

      HandlerList.unregisterAll(this);
    }
  }
}

