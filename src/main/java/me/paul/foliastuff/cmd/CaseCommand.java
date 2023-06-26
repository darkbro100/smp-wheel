package me.paul.foliastuff.cmd;

import com.google.common.collect.Lists;
import me.paul.foliastuff.Case;
import me.paul.foliastuff.CaseItem;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.ItemBuilder;
import me.paul.foliastuff.util.Util;
import me.paul.foliastuff.util.gui.GuiButton;
import me.paul.foliastuff.util.gui.GuiPage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CaseCommand implements CommandExecutor, TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if (!(commandSender instanceof Player player))
      return false;

    if(!player.isOp()) {
      player.sendMessage(Component.text("fuck you"));
      return false;
    }

    if (args.length == 0 || !args[0].equalsIgnoreCase("edit")) {

      Block block = player.getTargetBlockExact(50);
      if(block == null || block.isEmpty()) {
        player.sendMessage(Component.text("look at sign pls"));
        return false;
      }

      Optional<BlockFace> optional = Util.getRotation(block);
      if(optional.isEmpty()) {
        player.sendMessage(Component.text("look at block with rotation pls"));
        return false;
      }

      createBasicCase().position(block);
      player.sendMessage(Component.text("Created new case"));
      return true;
    }

    if (args.length > 1 && args[0].equalsIgnoreCase("edit")) {
      try {
        int id = Integer.parseInt(args[1]);
        Case caseInst = Case.get(id);
        if (caseInst == null) {
          player.sendMessage(Component.text("Invalid case id"));
          return true;
        }

        GuiPage page = new GuiPage(FoliaStuff.getInstance(), 6, "Editing Case #" + id);
        for (CaseItem caseItem : caseInst.getItems()) {
          GuiButton button = new GuiButton(ItemBuilder.of(caseItem.getRarity().getBlockType()).name(Component.text("View " + caseItem.getRarity().name() + " Items")).build());
          button.setListener((e, p) -> onCaseItemButtonClick(e, p, caseItem));

          page.addButton(button);
        }

        page.show(player);
      } catch (Exception e) {
        player.sendMessage(Component.text("Invalid case id"));
        return true;
      }
    }

    return false;
  }

  private void onCaseItemButtonClick(InventoryClickEvent e_, Player player, CaseItem caseItem) {
    GuiPage page = new GuiPage(FoliaStuff.getInstance(), 6, "Editing - " + caseItem.getRarity().name());

    page.setMainClickListener((e) -> {
      e.setCancelled(true);

      if (e.getClickedInventory() != null && e.isLeftClick() && e.getView().getBottomInventory().equals(e.getClickedInventory()) && e.getCurrentItem() != null) {
        ItemStack clicked = e.getCurrentItem();
        caseItem.add(clicked.clone());

        // reopen the gui
        onCaseItemButtonClick(e, player, caseItem);
      }
    });

    for (ItemStack item : caseItem.drops()) {
      GuiButton button = new GuiButton(ItemBuilder.of(item.clone()).setLore(ChatColor.GRAY + "Right click to remove").build());
      button.setListener((e, p) -> {
        e.setCancelled(true);

        if (e.isRightClick())
          caseItem.removeDrop(item);

        // reopen the gui
        onCaseItemButtonClick(e, p, caseItem);
      });

      page.addButton(button);
    }

    page.show(player);
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
    reds.add(new ItemStack(Material.ANCIENT_DEBRIS, 3));
    reds.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 10));
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

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if(!commandSender.isOp())
      return List.of();
    if(args.length == 1)
      return List.of("edit");
    if(args.length == 2)
      return IntStream.range(0, Case.getCases().length).mapToObj(Integer::toString).collect(Collectors.toList());

    return List.of();
  }
}
