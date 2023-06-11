package me.paul.foliastuff.util;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.paul.foliastuff.other.FoliaStuff;
import org.bukkit.Bukkit;

public class Cooldown {

  private long expiry;
  private Runnable onDone;
  private ScheduledTask task;

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
    this.task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(FoliaStuff.getInstance(), task -> {
      if (hasExpired()) {
        task.cancel();
        onDone.run();
      }
    }, 1, 5);
    return this;
  }

  private void cancelTask() {
    task.cancel();
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
