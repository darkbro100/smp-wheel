package me.paul.foliastuff.cmd;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import me.paul.foliastuff.Case;
import me.paul.foliastuff.CaseItem;
import me.paul.foliastuff.util.ItemBuilder;
import me.paul.foliastuff.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
    caseInst.spin(player, future);
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
    blues.add(new ItemStack(Material.COBBLESTONE));
    blues.add(new ItemStack(Material.GRAVEL));
    blues.add(new ItemStack(Material.NETHERRACK));

    List<ItemStack> purples = Lists.newArrayList();
    purples.add(new ItemStack(Material.OBSIDIAN, 8));
    purples.add(new ItemStack(Material.STICKY_PISTON, 2));
    purples.add(new ItemStack(Material.BLUE_ICE, 32));
    purples.add(new ItemStack(Material.BOOKSHELF));
    purples.add(new ItemStack(Material.IRON_INGOT, 16));
    purples.add(new ItemStack(Material.GOLD_INGOT, 5));

    List<ItemStack> pinks = Lists.newArrayList();
    pinks.add(new ItemStack(Material.DIAMOND, 5));
    pinks.add(new ItemStack(Material.EMERALD, 16));
    pinks.add(new ItemStack(Material.ENCHANTED_BOOK));
    pinks.add(new ItemStack(Material.FIREWORK_ROCKET, 64));

    List<ItemStack> reds = Lists.newArrayList();
    reds.add(new ItemStack(Material.ANCIENT_DEBRIS, 1));
    reds.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
    reds.add(new ItemStack(Material.BEACON));

    ItemStack meatStack = ItemBuilder.of(Material.PORKCHOP)
      .addUnsafeEnchantment(Enchantment.KNOCKBACK, 5)
      .name(Component.text("My Schmeat").color(TextColor.color(125, 0, 125))).build();
    reds.add(meatStack);

    ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
    reds.add(playerHead);

    List<ItemStack> gold = Lists.newArrayList();
    ItemStack godTrident = ItemBuilder.of(Material.TRIDENT)
      .addUnsafeEnchantment(Enchantment.LOYALTY, 3)
      .addUnsafeEnchantment(Enchantment.RIPTIDE, 3)
      .addUnsafeEnchantment(Enchantment.CHANNELING, 1)
      .addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5)
      .addUnsafeEnchantment(Enchantment.IMPALING, 5)
      .addUnsafeEnchantment(Enchantment.DURABILITY, 5)
      .name(Component.text("God Trident")
        .color(TextColor.color(255, 255, 0)))
      .build();
    gold.add(godTrident);
    gold.add(ItemBuilder.of(Material.BEDROCK).setAmount(2).build());
    gold.add(ItemBuilder.of(Material.SPONGE).setAmount(64).build());

    ItemStack godBow = ItemBuilder.of(Material.BOW)
      .addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1)
      .addUnsafeEnchantment(Enchantment.MENDING, 1)
      .name(Component.text("God Bow").color(TextColor.color(255, 255, 0))).build();
    gold.add(godBow);

    EntityType[] types = {EntityType.ENDERMAN, EntityType.PIG, EntityType.COW, EntityType.ZOMBIE, EntityType.SPIDER, EntityType.SKELETON, EntityType.CREEPER, EntityType.IRON_GOLEM, EntityType.DROWNED, EntityType.MUSHROOM_COW, EntityType.CAMEL, EntityType.AXOLOTL, EntityType.EVOKER, EntityType.ZOMBIE_VILLAGER, EntityType.VILLAGER, EntityType.SKELETON_HORSE};
    for (EntityType type : types) {
      ItemStack spawner = new ItemStack(Material.SPAWNER);
      BlockStateMeta spawnerMeta = (BlockStateMeta) spawner.getItemMeta();
      CreatureSpawner state = (CreatureSpawner) spawnerMeta.getBlockState();
      state.setSpawnedType(type);
      spawnerMeta.setBlockState(state);
      spawner.setItemMeta(spawnerMeta);
      gold.add(spawner);
    }
    gold.add(new ItemStack(Material.WARDEN_SPAWN_EGG));

    List<ItemStack> ancient = Lists.newArrayList();

    ItemStack goatDirt = ItemBuilder.of(Material.DIRT)
      .addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 10)
      .name(Component.text("Goat Dirt")
        .color(TextColor.color(255, 80, 0)))
      .build();
    ancient.add(goatDirt);

    Material[] armor = {Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS};

    for (Material m : armor) {
      ItemStack goatArmorPiece = ItemBuilder.of(m)
        .addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
        .addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 5)
        .addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5)
        .addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 5)
        .addUnsafeEnchantment(Enchantment.DURABILITY, 5)
        .name(Component.text("Goat " + Util.formatEnum(m)).color(TextColor.color(50, 25, 0)))
        .build();

      ancient.add(goatArmorPiece);
    }

    ItemStack goatSword = ItemBuilder.of(Material.NETHERITE_SWORD)
      .addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5)
      .addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5)
      .addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 5)
      .addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2)
      .addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 5)
      .addUnsafeEnchantment(Enchantment.DURABILITY, 5)
      .name(Component.text("Goat Sword").color(TextColor.color(50, 25, 0)))
      .build();
    ItemStack goatAxe = ItemBuilder.of(Material.NETHERITE_AXE)
      .addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5)
      .addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5)
      .addUnsafeEnchantment(Enchantment.DIG_SPEED, 5)
      .addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2)
      .addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 5)
      .addUnsafeEnchantment(Enchantment.DURABILITY, 5)
      .name(Component.text("Goat Axe").color(TextColor.color(50, 25, 0))).build();
    ancient.addAll(Arrays.asList(goatSword, goatAxe));

    return new Case(new CaseItem(CaseItem.CaseRarity.BLUE).add(blues),
      new CaseItem(CaseItem.CaseRarity.PURPLE).add(purples),
      new CaseItem(CaseItem.CaseRarity.PINK).add(pinks),
      new CaseItem(CaseItem.CaseRarity.RED).add(reds),
      new CaseItem(CaseItem.CaseRarity.GOLD).add(gold),
      new CaseItem(CaseItem.CaseRarity.ANCIENT).add(ancient));
  }
}
