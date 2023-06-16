package me.paul.foliastuff;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.paul.foliastuff.util.Util;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;

public class CaseItem {

  @Getter
  private final CaseRarity rarity;
  private final List<ItemStack> drops = Lists.newArrayList();

  public CaseItem(CaseRarity rarity) {
    this.rarity = rarity;
  }

  public CaseItem add(ItemStack item) {
    drops.add(item);
    return this;
  }

  public CaseItem add(ItemStack... items) {
    Collections.addAll(drops, items);
    return this;
  }

  public CaseItem add(List<ItemStack> items) {
    drops.addAll(items);
    return this;
  }

  public ItemStack[] drops() {
    return drops.toArray(new ItemStack[0]);
  }

  public ItemStack generateItem() {
    return Util.getRandomEntry(drops);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CaseItem caseItem = (CaseItem) o;
    return rarity == caseItem.rarity && Objects.equals(drops, caseItem.drops);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rarity, drops);
  }

  @Getter
  public enum CaseRarity {
    BLUE(79_920, TextColor.color(0, 0, 125), Material.BLUE_WOOL),
    PURPLE(15_980, TextColor.color(125, 0, 125), Material.PURPLE_WOOL),
    PINK(3_200, TextColor.color(255, 85, 255), Material.PINK_WOOL),
    RED(640, TextColor.color(125, 0, 0), Material.RED_WOOL),
    GOLD(260, TextColor.color(255, 170, 0), Material.YELLOW_WOOL);

    private static final CaseRarity[] VALUES = values();

    final int weight;
    final TextColor color;
    final Material blockType;

    CaseRarity(int w, TextColor c, Material b) {
      weight = w;
      color = c;
      blockType = b;
    }

    public static CaseRarity of(String key) {
      for(CaseRarity rarity : VALUES) {
        if(rarity.name().equalsIgnoreCase(key)) {
          return rarity;
        }
      }

      return null;
    }
  }

}
