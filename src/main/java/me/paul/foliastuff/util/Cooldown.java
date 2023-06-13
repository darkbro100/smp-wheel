package me.paul.foliastuff.util;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.util.scheduler.TaskBuilder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class Cooldown {

  private long expiry;
  private Runnable onDone;
  private Object task;

  /**
   * Create a new cooldown
   *
   * @param expiresIn How long until this {@link Cooldown} expires
   */
  public Cooldown(Duration expiresIn) {
    this.expiry = System.currentTimeMillis() + expiresIn.toMilliseconds();
  }

  /**
   * Run something when this {@link Cooldown} finishes
   *
   * @param r Runnable to execute
   * @return This instance for chaining
   */
  public Cooldown onDone(Runnable r) {
    this.onDone = r;

    if (TaskBuilder.isFoliaSupported()) {
      this.task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(FoliaStuff.getInstance(), task -> {
        if (hasExpired()) {
          task.cancel();
          if (onDone != null)
            onDone.run();
        }
      }, 1, 5);
    } else {
      this.task = Sync.get().interval(5).run(() -> {
        if (hasExpired()) {
          cancelTask();
          if (onDone != null)
            onDone.run();
        }
      });
    }
    return this;
  }

  private void cancelTask() {
    if (TaskBuilder.isFoliaSupported()) {
      ((ScheduledTask) task).cancel();
    } else {
      ((BukkitTask) task).cancel();
    }

    onDone.run();

    task = null;
    onDone = null;
  }

  /**
   * @return True if this {@link Cooldown} has expired
   */
  public boolean hasExpired() {
    long now = System.currentTimeMillis();
    return now >= expiry;
  }

  public String getRemainingTime() {
    if (hasExpired())
      return "Expired";
    return Duration.milliseconds(expiry - System.currentTimeMillis()).formatText();
  }

}
