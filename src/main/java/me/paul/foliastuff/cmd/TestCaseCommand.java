package me.paul.foliastuff.cmd;

import com.google.common.collect.Lists;
import me.paul.foliastuff.Case;
import me.paul.foliastuff.CaseItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TestCaseCommand implements CommandExecutor {

  private static final Case caseInst;

  static {
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
    pinks.add(new ItemStack(Material.GOLD_INGOT, 3));
    pinks.add(new ItemStack(Material.IRON_INGOT, 16));
    pinks.add(new ItemStack(Material.ENCHANTING_TABLE));

    List<ItemStack> reds = Lists.newArrayList();
    reds.add(new ItemStack(Material.DIAMOND, 5));
    reds.add(new ItemStack(Material.EMERALD, 5));
    reds.add(new ItemStack(Material.ANCIENT_DEBRIS, 1));

    List<ItemStack> gold = Lists.newArrayList();
    gold.add(new ItemStack(Material.NETHERITE_INGOT));
    gold.add(new ItemStack(Material.BEDROCK));
    gold.add(new ItemStack(Material.SPONGE));
    gold.add(new ItemStack(Material.DRAGON_HEAD));
    gold.add(new ItemStack(Material.SKELETON_SKULL));
    gold.add(new ItemStack(Material.ZOMBIE_HEAD));
    gold.add(new ItemStack(Material.CREEPER_HEAD));

    caseInst = new Case(new CaseItem(CaseItem.CaseRarity.BLUE).add(blues),
      new CaseItem(CaseItem.CaseRarity.PURPLE).add(purples),
      new CaseItem(CaseItem.CaseRarity.PINK).add(pinks),
      new CaseItem(CaseItem.CaseRarity.RED).add(reds),
      new CaseItem(CaseItem.CaseRarity.GOLD).add(gold));
  }


  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player player))
      return false;

    if (caseInst.location() == null)
      caseInst.location(player.getLocation().getBlock().getLocation().clone().add(0.1, 0, 0.5));

    CompletableFuture<ItemStack> future = new CompletableFuture<>();
    caseInst.spin(future);
    future.whenComplete((it, ex) -> {
      if (ex != null) {
        ex.printStackTrace();
        return;
      }

      player.sendMessage(Component.text(it.toString()));
    });

    player.sendMessage(Component.text("Spinning case..."));

    return false;
  }
}
