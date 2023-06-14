package me.paul.foliastuff;

import com.google.common.collect.Maps;
import me.paul.foliastuff.util.WeightedRandomizer;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.util.scheduler.TaskHolder;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Case {

  private static final Map<Integer, Case> cases = Maps.newHashMap();

  private final WeightedRandomizer<CaseItem> items;
  private final int id;
  private Location location;
  private CaseRunnable runnable;

  public Case(CaseItem... items) {
    this.id = cases.size();
    this.items = new WeightedRandomizer<>();

    for (CaseItem ci : items)
      this.items.add(ci, ci.getRarity().weight);

    cases.put(this.id, this);
  }

  public Case add(CaseItem item) {
    items.add(item, item.getRarity().weight);
    return this;
  }

  /**
   * Center of where the "case opening" starts. Pls use a centered block location (0.5, 0, 0.5)
   *
   * @param loc Location
   * @return Case instance for chaining
   */
  public Case location(Location loc) {
    this.location = loc;
    return this;
  }

  /**
   * Location of this case (cloned)
   *
   * @return Case location
   */
  public Location location() {
    if(location == null)
      return null;

    return location.clone();
  }

  protected CaseItem generateItem() {
    return items.select();
  }

  public static Case get(int id) {
    return cases.get(id);
  }

  /**
   * Spins this case. Takes in a future to complete when the spinning is done.
   *
   * @param future Future
   */
  public void spin(CompletableFuture<ItemStack> future) {
    TaskHolder holder = new TaskHolder();
    this.runnable = new CaseRunnable(holder, this, future);
    Sync.get(this.location).holder(holder).interval(1).run(this.runnable);
  }

}
