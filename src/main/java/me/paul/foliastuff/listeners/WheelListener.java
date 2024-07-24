package me.paul.foliastuff.listeners;

import com.google.common.collect.Maps;
import me.paul.foliastuff.CaseStats;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.Duration;
import me.paul.foliastuff.util.SettingsManager;
import me.paul.foliastuff.wheel.Wheel;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static me.paul.foliastuff.wheel.Wheel.WHEEL_COOLDOWN;

public class WheelListener implements Listener {

  private Map<UUID, EntityType> lastDamager = Maps.newHashMap();

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    PermissionAttachment attachment = player.addAttachment(FoliaStuff.getInstance());
    attachment.setPermission("bukkit.broadcast.user", true);
  }

  @EventHandler
  public void onDeath(PlayerDeathEvent event) {
    EntityType lastDamager = this.lastDamager.get(event.getPlayer().getUniqueId());
    if (lastDamager != null && lastDamager == EntityType.ARMOR_STAND) {
      event.deathMessage(event.getPlayer().displayName().append(Component.text(" lost all their items in a horrific accident...")));
      this.lastDamager.remove(event.getPlayer().getUniqueId());
    }
  }

  @EventHandler
  public void onDamage(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Item && e.getDamager() instanceof ArmorStand)
      e.setCancelled(true);

    if(e.getEntity() instanceof Player && e.getDamager() instanceof Item && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
      e.setCancelled(true);

    if (e.getEntity() instanceof Player) {
      lastDamager.put(e.getEntity().getUniqueId(), e.getDamager().getType());
    }
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
      Block b = event.getClickedBlock();
      if (b.getType().name().contains("BUTTON")) {
        Wheel w = Wheel.get(b.getLocation());
        Player p = event.getPlayer();
        CaseStats stats = CaseStats.get(p.getUniqueId());

        if (w == null) {
          return;
        }

        boolean res = w.spin(p);

        if (!res && w.isLocked()) {
          event.getPlayer().sendMessage(ChatColor.RED + "Wheel is currently on cooldown! Time remaining: " + w.getLastSpin().getRemainingTime());
        } else if (!res && w.isStarted()) {
          event.getPlayer().sendMessage(ChatColor.RED + "Wheel is already being spun!");
        } else if(!res && stats.getLastWheelSpin() != null) {
          Date last = stats.getLastWheelSpin();
          Date now = new Date();
          long diff = now.getTime() - last.getTime();
          if (diff < WHEEL_COOLDOWN.ms()) {
            // calculate time remaining
            long remaining = WHEEL_COOLDOWN.ms() - diff;
            event.getPlayer().sendMessage(org.bukkit.ChatColor.RED + "You must wait " + Duration.ms(remaining).formatText() + " between spins!");
          }
        }
      }
    }
  }

}
