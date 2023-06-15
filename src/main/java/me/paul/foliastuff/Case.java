package me.paul.foliastuff;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.WeightedRandomizer;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.util.scheduler.TaskHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Case {

  private static final Map<Integer, Case> cases = Maps.newHashMap();

  private final WeightedRandomizer<CaseItem> items;

  @Getter
  private final int id;
  private Location location;
  private CaseRunnable runnable;

  private TextDisplay displayEnt;
  private ArmorStand interactEnt;
  @Getter @Setter(AccessLevel.PACKAGE)
  private boolean running = false;

  public Case(CaseItem... items) {
    this.id = cases.size();
    this.items = new WeightedRandomizer<>();

    for (CaseItem ci : items)
      this.items.add(ci, ci.getRarity().weight);

    cases.put(this.id, this);
  }

  public static Case[] getCases() {
    return cases.values().toArray(new Case[0]);
  }

  public Case add(CaseItem item) {
    items.add(item, item.getRarity().weight);
    return this;
  }

  public CaseItem[] getItems() {
    return items.getWeightMap().keySet().toArray(new CaseItem[0]);
  }

  /**
   * Center of where the "case opening" starts. Pls use a centered block location (0.5, 0, 0.5)
   *
   * @param loc Location
   * @return Case instance for chaining
   */
  public Case location(Location loc) {
    this.location = loc;
    Sync.get(location).delay(20).run(this::spawnDisplay);

    return this;
  }

  /**
   * Location of this case (cloned)
   *
   * @return Case location
   */
  public Location location() {
    if (location == null)
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
  public void spin(CompletableFuture<Pair<CaseItem, ItemStack>> future) {
    TaskHolder holder = new TaskHolder();
    this.runnable = new CaseRunnable(holder, this, future);
    this.running = true;
    Sync.get(this.location).holder(holder).interval(1).run(this.runnable);
  }

  public TextDisplay displayEntity() {
    return displayEnt;
  }

  public ArmorStand interactEntity() {
    return interactEnt;
  }

  private void spawnDisplay() {
    // delete previous one if the location has changed
    if (displayEnt != null) {
      interactEnt.remove();
      displayEnt.remove();

      interactEnt = null;
      displayEnt = null;
    }

    // define spawn loc
    Location spawnLoc = location.clone().add(-1, 1, -1.5);
    spawnLoc = spawnLoc.getBlock().getLocation().clone().add(0.5, 0, 0.5);

    // delete nearby text displays that may have not been deleted somehow
    spawnLoc.getWorld().getNearbyEntities(spawnLoc, 2, 2, 2, e -> e instanceof TextDisplay || e instanceof ArmorStand).forEach(Entity::remove);

    displayEnt = spawnLoc.getWorld().spawn(spawnLoc, TextDisplay.class);
    displayEnt.setBillboard(Display.Billboard.CENTER);
    displayEnt.text(Component.text("Right Click to Spin!").color(TextColor.color(0x965613)));

    interactEnt = spawnLoc.getWorld().spawn(spawnLoc.clone().subtract(0, 1.65, 0), ArmorStand.class);
    interactEnt.setInvisible(true);
    interactEnt.setInvulnerable(true);
    interactEnt.setGravity(false);
    interactEnt.setCanMove(false);
    interactEnt.setMetadata("caseId", new FixedMetadataValue(FoliaStuff.getInstance(), id));
  }

}
