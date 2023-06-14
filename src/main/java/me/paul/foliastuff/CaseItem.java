package me.paul.foliastuff;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.paul.foliastuff.util.Util;
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

  public enum CaseRarity {
    BLUE(79_920),
    PURPLE(15_980),
    PINK(3_200),
    RED(640),
    GOLD(260);

    final int weight;

    CaseRarity(int w) {
      weight = w;
    }
  }

}
