package me.paul.lads.util.scheduler;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Sets;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.paul.lads.util.Duration;

@Builder
@Getter
public class TaskBuilder implements Runnable {

	public static Set<TaskBuilder> hardRuns = Sets.newConcurrentHashSet();
	
	static final TimeUnit TICKS = null;
	@Setter
	private TaskType type;
	private Runnable runnable;
	private Runnable onCycleEnd;
	private long delay = 0L;
	private long interval = 0L;
	private long cycles = 0L;
	@Setter
	private Plugin plugin;
	private BukkitTask task;
	private boolean hardRun;
	private TaskHolder holder;
	
	public static TaskBuilder buildSync(Plugin plugin) {
		return TaskBuilder.builder().type(TaskType.SYNC).plugin(plugin).build();
	}
	
	public static TaskBuilder buildAsync(Plugin plugin) {
		return TaskBuilder.builder().type(TaskType.ASYNC).plugin(plugin).build();
	}
	
	/**
	 * Delays the task launch by the given duration. 
	 * @param dur The duration to delay.
	 */
	public TaskBuilder delay(Duration dur) {
		this.delay = dur.toTicks();
		return this;
	}
	
	/**
	 * Assign this {@link TaskBuilder} a {@link TaskHolder}
	 * @param holder {@link TaskHolder} to assign it to
	 * @return This instance for chaining
	 */
	public TaskBuilder holder(TaskHolder holder) {
		this.holder = holder;
		return this;
	}
	
	/**
	 * Delays the task launch by the given number of ticks.
	 * @param delay The time to delay in ticks.
	 */
	public TaskBuilder delay(long delay) {
		this.delay = delay;
		return this;
	}
	
	/**
	 * Launches the task at the given rate, regardless of how long
	 * the task takes to complete.
	 * @param interval The delay in ticks to wait between launches.
	 */
	public TaskBuilder interval(long interval) {
		this.interval = interval;
		return this;
	}

	public TaskBuilder interval(Duration interval) {
		this.interval = interval.toTicks();
		return this;
	}
	
	public TaskBuilder cycles(long cycles) {
		this.cycles = cycles;
		return this;
	}
	
	/**
	 * Call a runnable when the cycles are completed for this TaskBuilder
	 * @param onCycleEnd Runnable
	 * @return This instance for chaining
	 */
	public TaskBuilder onCycleEnd(Runnable onCycleEnd) {
		this.onCycleEnd = onCycleEnd;
		return this;
	}
	
	public BukkitTask run(Runnable runnable) {
        if(onCycleEnd != null) {
            this.runnable = () -> {
                runnable.run();
                onCycleEnd.run();
            };
        } else
            this.runnable = runnable;
		BukkitScheduler sch = Bukkit.getScheduler();

		if (interval == 0 && cycles == 0) { //Only to be ran once
			if (delay == 0) {
				if (type == TaskType.SYNC)
					return task = sch.runTask(plugin, this);
				else if (type == TaskType.ASYNC)
					return task = sch.runTaskAsynchronously(plugin, this);
			} else {
				if (type == TaskType.SYNC)
					return task = sch.runTaskLater(plugin, this, delay);
				else if (type == TaskType.ASYNC)
					return task = sch.runTaskLaterAsynchronously(plugin, this, delay);
			}
		} else {
			if (type == TaskType.SYNC)
				return task = sch.runTaskTimer(plugin, this, delay, interval);
			else if (type == TaskType.ASYNC)
				return task = sch.runTaskTimerAsynchronously(plugin, this, delay, interval);
		}
		
		//Should never happen
		return null;
	}

	public BukkitTask hardRun(Runnable runnable) {
        hardRun = true;
        hardRuns.add(this);
        return run(runnable);
    }
	
	protected int count = 0;
	@Override
	public void run() {
		if(holder != null && holder.isCancelled()) {
			task.cancel();
			return;
		}
		
		runnable.run();
		
		count++;
		
		if (cycles > 0 && count >= cycles) {
			task.cancel();
            if(hardRun)
                hardRuns.remove(this);
        }

        if(cycles == 0 && hardRun)
            hardRuns.remove(this);
	}

	public enum TaskType {
		SYNC,
		ASYNC;
	}
	
}
