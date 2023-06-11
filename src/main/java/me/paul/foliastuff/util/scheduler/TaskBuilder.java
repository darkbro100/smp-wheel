package me.paul.foliastuff.util.scheduler;

import com.google.common.collect.Sets;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.Duration;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Builder
@Getter
public class TaskBuilder implements Runnable {

  public static Set<TaskBuilder> hardRuns = Sets.newConcurrentHashSet();

  static final TimeUnit TICKS = null;
  @Setter
  private TaskType type;
  private Runnable runnable;
  private Runnable onCycleEnd;
  private long delay = -1L;
  private long interval = -1L;
  private long cycles = 0L;
  @Setter
  private Plugin plugin;
  @Nullable
  private Entity entity;
  @Nullable
  private Location location;
  private ScheduledTask task;
  private boolean hardRun;
  private TaskHolder holder;

  public static TaskBuilder build(Plugin plugin, Entity entity) {
    return TaskBuilder.builder().type(TaskType.ASYNC).entity(entity).plugin(plugin).build();
  }

  public static TaskBuilder build(Plugin plugin, Location loc) {
    return TaskBuilder.builder().type(TaskType.ASYNC).location(loc).plugin(plugin).build();
  }

  public static TaskBuilder buildSync(Plugin plugin) {
    return TaskBuilder.builder().type(TaskType.SYNC).plugin(plugin).build();
  }

  public static TaskBuilder buildAsync(Plugin plugin) {
    return TaskBuilder.builder().type(TaskType.ASYNC).plugin(plugin).build();
  }

  /**
   * Delays the task launch by the given duration.
   *
   * @param dur The duration to delay.
   */
  public TaskBuilder delay(Duration dur) {
    this.delay = dur.toTicks();
    if (this.delay == 0) this.delay = 1;

    return this;
  }

  /**
   * Assign this {@link TaskBuilder} a {@link TaskHolder}
   *
   * @param holder {@link TaskHolder} to assign it to
   * @return This instance for chaining
   */
  public TaskBuilder holder(TaskHolder holder) {
    this.holder = holder;
    return this;
  }

  /**
   * Delays the task launch by the given number of ticks.
   *
   * @param delay The time to delay in ticks.
   */
  public TaskBuilder delay(long delay) {
    if (delay == 0)
      this.delay = 1;
    else
      this.delay = delay;

    return this;
  }

  /**
   * Launches the task at the given rate, regardless of how long
   * the task takes to complete.
   *
   * @param interval The delay in ticks to wait between launches.
   */
  public TaskBuilder interval(long interval) {
    if (interval == 0)
      this.interval = 1;
    else
      this.interval = interval;

    return this;
  }

  public TaskBuilder interval(Duration interval) {
    this.interval = interval.toTicks();
    if (this.interval == 0) this.interval = 1;

    return this;
  }

  public TaskBuilder cycles(long cycles) {
    this.cycles = cycles;
    return this;
  }

  /**
   * Call a runnable when the cycles are completed for this TaskBuilder
   *
   * @param onCycleEnd Runnable
   * @return This instance for chaining
   */
  public TaskBuilder onCycleEnd(Runnable onCycleEnd) {
    this.onCycleEnd = onCycleEnd;
    return this;
  }

  public ScheduledTask run(Runnable runnable) {
    if (onCycleEnd != null) {
      this.runnable = () -> {
        runnable.run();
        onCycleEnd.run();
      };
    } else
      this.runnable = runnable;

//    FoliaStuff.getInstance().getLogger().info("Interval: " + interval);
//    FoliaStuff.getInstance().getLogger().info("Delay: " + delay);

    if (interval == -1L && cycles == 0) { //Only to be ran once
      if (delay == -1L) {
        if (location != null) {
          return this.task = plugin.getServer().getRegionScheduler().run(plugin, location, task -> run());
        } else if (entity != null) {
          return this.task = entity.getScheduler().run(plugin, task -> run(), null);
        }
      } else {
        if (location != null) {
          return this.task = plugin.getServer().getRegionScheduler().runDelayed(plugin, location, task -> run(), delay);
        } else if (entity != null) {
          return this.task = entity.getScheduler().runDelayed(plugin, task -> run(), null, delay);
        }
      }
    } else {

      if (location != null) {
        return this.task = plugin.getServer().getRegionScheduler().runAtFixedRate(plugin, location, task -> run(), delay == -1L ? 1 : delay, interval == -1L ? 1 : interval);
      } else if (entity != null) {
        return this.task = entity.getScheduler().runAtFixedRate(plugin, task -> run(), null, delay == -1L ? 1 : delay, interval == -1L ? 1 : interval);
      }
    }

    //Should never happen
    return null;
  }

  protected int count = 0;

  @Override
  public void run() {
    if (holder != null && holder.isCancelled()) {
      task.cancel();
      return;
    }

    runnable.run();

    count++;

    if (cycles > 0 && count >= cycles) {
      task.cancel();
      if (hardRun)
        hardRuns.remove(this);
    }

    if (cycles == 0 && hardRun)
      hardRuns.remove(this);
  }

  public enum TaskType {
    SYNC,
    ASYNC;
  }

}
