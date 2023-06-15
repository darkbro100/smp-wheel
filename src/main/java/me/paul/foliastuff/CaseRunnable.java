package me.paul.foliastuff;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.Duration;
import me.paul.foliastuff.util.Util;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.util.scheduler.TaskHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CaseRunnable implements Runnable {

  private static final Duration LIMIT = Duration.seconds(5.6);
  private static final int MAX_ITEMS = 5;

  private final TaskHolder holder;
  private final Case caseInst;

  private int ticks = 0;
  private int endTicks = 0;
  private int endDelay = 0;

  private final List<Item> itemCycle = Lists.newArrayListWithCapacity(MAX_ITEMS);

  private final Location maxLeft;
  private boolean shouldAdd = false;

  private static final Vector SPEED = new Vector(-0.5, 0, 0);

  private CaseItem winningItem;
  private final ItemStack winningItemStack;
  private static final int WINNING_TICKET = 109;
  private Item winningItemInstance;

  private CompletableFuture<Pair<CaseItem, ItemStack>> future;

  public CaseRunnable(TaskHolder holder, Case caseInst, CompletableFuture<Pair<CaseItem, ItemStack>> future) {
    this.future = future;
    this.holder = holder;
    this.caseInst = caseInst;
    this.winningItem = caseInst.generateItem();
    this.winningItemStack = winningItem.generateItem().clone();

    this.maxLeft = caseInst.location().clone().add(-0.1, 0, 0).add(-((double) MAX_ITEMS / 2), 0, 0);

    for (int i = -(MAX_ITEMS / 2); i <= MAX_ITEMS / 2; i++)
      itemCycle.add(drop(caseInst.generateItem(), i, false));
  }

  private static final double END_DEC = -0.02;

  @Override
  public void run() {
    //if time is up stop
    if (ticks >= LIMIT.ticks()) {

      // randomly make the ending wait 2-3 seconds
      if (endTicks == 0) {
        endDelay = Util.random(40, 60);
      }

      if (SPEED.getX() < END_DEC && endTicks != 0 && endTicks % 5 == 0)
        SPEED.add(new Vector(-END_DEC, 0, 0));

      // once the end state has ended
      if (endTicks >= endDelay) {
        holder.cancel();
        SPEED.setX(-0.5);

        // clear other items
        for (Item it : itemCycle) {
          if (it.equals(winningItemInstance))
            continue;

          it.remove();
        }
        itemCycle.clear();

        // exec cosmetic runnable
        CaseItem.CaseRarity rarity = winningItem.getRarity();
        if (rarity == CaseItem.CaseRarity.GOLD || rarity == CaseItem.CaseRarity.RED) {
          TaskHolder endHolder = new TaskHolder();
          Sync.get(winningItemInstance).interval(1).holder(endHolder).run(new CaseReceiveItemRunnable(winningItemInstance, winningItem, winningItemStack, endHolder, future));
        } else {
          Vector offset = new Vector(Util.random(), Util.random(), Util.random());

          if (rarity == CaseItem.CaseRarity.PURPLE || rarity == CaseItem.CaseRarity.BLUE) {
            winningItemInstance.getWorld().playSound(winningItemInstance.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            new ParticleBuilder(Particle.VILLAGER_ANGRY).location(winningItemInstance.getLocation()).offset(offset.getX(), offset.getY(), offset.getZ()).count(25).spawn();
          } else {
            winningItemInstance.getWorld().playSound(winningItemInstance.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1, 1);
            new ParticleBuilder(Particle.VILLAGER_HAPPY).location(winningItemInstance.getLocation()).offset(offset.getX(), offset.getY(), offset.getZ()).count(25).spawn();
          }

          winningItemInstance.remove();
          winningItemInstance = null;

          future.complete(Pair.of(winningItem, winningItemStack));
        }

        caseInst.setRunning(false);
        return;
      }

      // keep shifting items over (slowly)
      for (int i = itemCycle.size() - 1; i >= 0; i--) {
        Item item = itemCycle.get(i);
        item.setVelocity(SPEED);
        checkItem(item);
      }

      // set speed to 0 if the winning item is gonna leave
      if (winningItemInstance.getLocation().getX() < (caseInst.location().getX() - 0.75))
        SPEED.multiply(0);

      // add new item in case
      addNewItem();

      endTicks++;
      ticks++;
      return;
    }

    if (ticks != 0 && ticks % 40 == 0)
      SPEED.multiply(0.5);

    // Shift every item over to the left
    for (int i = itemCycle.size() - 1; i >= 0; i--) {
      Item item = itemCycle.get(i);
      item.setVelocity(SPEED);

      // mark for removal
      checkItem(item);
    }

    // add new item to the end of the cycle
    addNewItem();

    ticks++;
  }

  private void addNewItem() {
    if (shouldAdd) {
      // need to arbitarily insert the pre-chosen item into the list
      // so it appears in the middle
      boolean winner = ticks == WINNING_TICKET;
      Item it = drop(winner ? winningItem : caseInst.generateItem(), MAX_ITEMS / 2, winner);
      if (winner)
        winningItemInstance = it;

      itemCycle.add(it);

      caseInst.location().getWorld().playSound(caseInst.location(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);

      shouldAdd = false;
    }
  }

  private void checkItem(Item item) {
    // mark for removal
    if (item.getLocation().getX() < maxLeft.getX()) {
      item.remove();
      itemCycle.remove(item);
      shouldAdd = true;
    }
  }

  private Item drop(CaseItem item, int offset, boolean winner) {
    ItemStack it = winner ? winningItemStack.clone() : item.generateItem().clone();
    it.setAmount(1);

    ItemMeta meta = it.getItemMeta();
    meta.displayName(Component.text(UUID.randomUUID().toString()));
    it.setItemMeta(meta);

    Location spawnLoc = caseInst.location().clone().add(offset, 0.0, 0);

    return caseInst.location().getWorld().dropItem(spawnLoc, it, d -> {
      d.setVelocity(SPEED);
      d.setGravity(false);
      d.setCanPlayerPickup(false);
      d.setCanMobPickup(false);
    });
  }
}
