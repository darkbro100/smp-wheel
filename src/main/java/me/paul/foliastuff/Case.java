package me.paul.foliastuff;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.Util;
import me.paul.foliastuff.util.WeightedRandomizer;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.util.scheduler.TaskHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.entity.npc.Villager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftVillager;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.paul.foliastuff.CaseRunnable.MAX_ITEMS;

public class Case {

  private static final Map<Integer, Case> cases = Maps.newHashMap();

  private final WeightedRandomizer<CaseItem> items;

  @Getter
  private final int id;
  private Location location;
  private CaseRunnable runnable;

  protected UUID spinner;

  private Util.Direction direction;

  private TextDisplay displayEnt;
  private ArmorStand interactEnt;

  protected AtomicBoolean running = new AtomicBoolean(false);
  private TaskHolder holder;

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

  public boolean isRunning() {
    return running.get();
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
    return this;
  }

  public Case position(Block signBlock) {
    this.location = signBlock.getLocation().clone().add(0.5, 0, 0.5);
    this.direction = Util.getDirFromSign(signBlock);
    Sync.get(location).delay(20).run(this::spawnDisplay);
    Sync.get(signBlock.getLocation()).run(() -> signBlock.setType(Material.AIR));

    return this;
  }

  public Case position(Location location, Util.Direction direction) {
    this.location = location.clone();
    this.direction = direction;
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

  public Util.Direction getDirection() {
    return direction;
  }

  protected CaseItem generateItem() {
    return generateItem(false);
  }

  protected CaseItem generateItem(boolean skipGold) {
    CaseItem item = items.select();

    while (skipGold && item.getRarity() == CaseItem.CaseRarity.GOLD)
      item = items.select();

    return item;
  }

  public static Case get(int id) {
    return cases.get(id);
  }

  public UUID spinner() {
    return spinner;
  }

  /**
   * Spins this case. Takes in a future to complete when the spinning is done.
   *
   * @param spinner Player opening this case
   * @param future  Future
   */
  public void spin(Player spinner, CompletableFuture<Pair<CaseItem, ItemStack>> future) {
    this.spinner = spinner.getUniqueId();

    this.holder = new TaskHolder();
    this.runnable = new CaseRunnable(holder, this, future);
    this.running.set(true);
    Sync.get(this.location).holder(holder).interval(1).run(this.runnable);

    // Change the display text to "spinning"
    displayEnt.text(Component.text("Spinning...").color(TextColor.color(125, 125, 125)));
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
    Location spawnLoc = location().add(direction.getVector().multiply(-1.5)).add(0, 1.5, 0);

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

  public void quickOpen() {
    if (!holder.isCancelled()) {
      // cancel the task
      holder.cancel();

      // update floor
      for (int i = -(MAX_ITEMS / 2); i <= MAX_ITEMS / 2; i++) {
        Location loc = location().add(direction.getVector().clone().multiply(i - 0.2)).subtract(0, 1, 0);
        loc.getBlock().setType(Material.BLACK_WOOL);
      }

      // delete all the items
      runnable.itemCycle.forEach(item -> Sync.get(item).run(item::remove));

      // complete the future
      runnable.future.complete(Pair.of(runnable.winningItem, runnable.winningItemStack));

      // wait 2 seconds, then update the display
      Sync.get(location).delay(40).run(() -> {
        this.running.set(false);

        // reset display text
        displayEntity().text(Component.text("Right Click to Spin!").color(TextColor.color(0x965613)));
      });
    }
  }
}
