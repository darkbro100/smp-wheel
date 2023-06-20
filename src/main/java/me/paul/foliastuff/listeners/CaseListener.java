package me.paul.foliastuff.listeners;

import com.mojang.datafixers.util.Pair;
import me.paul.foliastuff.Case;
import me.paul.foliastuff.CaseItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CaseListener implements Listener {

  @EventHandler
  public void onDie(PlayerDeathEvent event) {
    Player player = event.getPlayer();
    if (player.getKiller() != null) {
      ItemStack head = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta meta = (SkullMeta) head.getItemMeta();
      meta.setPlayerProfile(player.getPlayerProfile());
      head.setItemMeta(meta);

      event.getDrops().add(head);
    }
  }

//  @EventHandler
//  public void onJoin(PlayerJoinEvent event) {
//    Sync.get(event.getPlayer()).delay(20).run(() -> {
//      for (CaseItem caseItem : Case.getCases()[0].getItems()) {
//        if (caseItem.getRarity() == CaseItem.CaseRarity.BLUE)
//          continue;
//
//        if (caseItem.getRarity() == CaseItem.CaseRarity.PURPLE)
//          continue;
//
//        if(caseItem.getRarity() == CaseItem.CaseRarity.ANCIENT)
//          continue;
//
//        if(caseItem.getRarity() == CaseItem.CaseRarity.GOLD)
//          continue;
//
//        ItemStack drop = caseItem.generateItem();
//        while (drop.getType() != Material.ENCHANTED_BOOK && drop.getType() != Material.PLAYER_HEAD) {
//          drop = caseItem.generateItem();
//        }
//
//        event.getPlayer().getInventory().addItem(drop);
//      }
//    });
//  }

  @EventHandler
  public void onInteract(PlayerInteractAtEntityEvent event) {
    if (event.getRightClicked() instanceof ArmorStand stand && event.getRightClicked().hasMetadata("caseId")) {
      event.setCancelled(true);

      Player player = event.getPlayer();
      int id = stand.getMetadata("caseId").get(0).asInt();
      Case caseIns = Case.get(id);

      // can the player afford a spin?
      ItemStack hand = player.getInventory().getItemInMainHand();
      if (hand.getType() != Material.EMERALD || hand.getAmount() < 3) {
        player.sendMessage(Component.text("You need at least 3 emeralds to spin a case!", NamedTextColor.RED));
        return;
      }

      // don't let the case double spin
      if (caseIns.isRunning()) {
        if(caseIns.spinner().equals(player.getUniqueId())) {
          caseIns.quickOpen();
        } else {
          player.sendMessage(Component.text("Case is already spinning!", NamedTextColor.RED));
        }
        return;
      }

      // remove 3 emeralds from the player's inventory
      hand.subtract(3);

      CompletableFuture<Pair<CaseItem, ItemStack>> future = new CompletableFuture<>();
      caseIns.spin(player, future);
      future.whenComplete((pair, ex) -> {
        if (ex != null) {
          ex.printStackTrace();
          return;
        }

        ItemStack it = pair.getSecond();
        CaseItem caseItem = pair.getFirst();

        @NotNull HashMap<Integer, ItemStack> map = player.getInventory().addItem(it);
        if (!map.isEmpty())
          map.values().forEach(it2 -> player.getWorld().dropItemNaturally(player.getLocation(), it2));

        Bukkit.broadcast(player.displayName()
          .append(Component.text(" got a ")
            .color(TextColor.color(255, 255, 255)))
          .append(Component.text(caseItem.getRarity().name())
            .color(caseItem.getRarity().getColor()))
          .append(Component.text(" item!")
            .color(TextColor.color(255, 255, 255))));
      });

      Bukkit.broadcast(player.displayName()
        .append(Component.text(" is going for the gold!"))
        .append(Component.text(" GOLD GOLD GOLD!", NamedTextColor.GOLD)));
    }
  }
}
