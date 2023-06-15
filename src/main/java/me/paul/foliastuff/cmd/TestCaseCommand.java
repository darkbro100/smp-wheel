package me.paul.foliastuff.cmd;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import me.paul.foliastuff.Case;
import me.paul.foliastuff.CaseItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TestCaseCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player player))
      return false;

    Case caseInst = Case.get(0);
    if (caseInst == null)
      caseInst = createBasicCase();

    if (caseInst.location() == null)
      caseInst.location(player.getLocation().getBlock().getLocation().clone().add(0.1, 0, 0.5));

    CompletableFuture<Pair<CaseItem, ItemStack>> future = new CompletableFuture<>();
    caseInst.spin(future);
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

    player.sendMessage(Component.text("Spinning case..."));

    return false;
  }

  private Case createBasicCase() {
    List<ItemStack> blues = Lists.newArrayList();
    blues.add(new ItemStack(Material.DIRT));
    blues.add(new ItemStack(Material.SCAFFOLDING));
    blues.add(new ItemStack(Material.MUD));
    blues.add(new ItemStack(Material.STONE_BUTTON));

    List<ItemStack> purples = Lists.newArrayList();
    purples.add(new ItemStack(Material.COBBLESTONE));
    purples.add(new ItemStack(Material.GRAVEL));
    purples.add(new ItemStack(Material.NETHERRACK));

    List<ItemStack> pinks = Lists.newArrayList();
    pinks.add(new ItemStack(Material.GOLD_INGOT, 5));
    pinks.add(new ItemStack(Material.IRON_INGOT, 16));
    pinks.add(new ItemStack(Material.DIAMOND, 3));

    List<ItemStack> reds = Lists.newArrayList();
    reds.add(new ItemStack(Material.EMERALD, 15));
    reds.add(new ItemStack(Material.ANCIENT_DEBRIS, 1));

    List<ItemStack> gold = Lists.newArrayList();
    gold.add(new ItemStack(Material.NETHERITE_INGOT));
    gold.add(new ItemStack(Material.BEDROCK));
    gold.add(new ItemStack(Material.SPONGE));
    gold.add(new ItemStack(Material.DRAGON_HEAD));
    gold.add(new ItemStack(Material.SKELETON_SKULL));
    gold.add(new ItemStack(Material.ZOMBIE_HEAD));
    gold.add(new ItemStack(Material.CREEPER_HEAD));

    return new Case(new CaseItem(CaseItem.CaseRarity.BLUE).add(blues),
      new CaseItem(CaseItem.CaseRarity.PURPLE).add(purples),
      new CaseItem(CaseItem.CaseRarity.PINK).add(pinks),
      new CaseItem(CaseItem.CaseRarity.RED).add(reds),
      new CaseItem(CaseItem.CaseRarity.GOLD).add(gold));
  }
}
