package me.paul.lads.util;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.paul.lads.Main;

public class Cooldown {

	private long expiry;
	private Runnable onDone;
	private BukkitTask task;
	
	/**
	 * Create a new cooldown
	 * 
	 * @param expiresIn
	 *            How long until this {@link Cooldown} expires
	 */
	public Cooldown(Duration expiresIn) {
		this.expiry = System.currentTimeMillis() + expiresIn.toMilliseconds();
	}

	/**
	 * Run something when this {@link Cooldown} finishes
	 * 
	 * @param r
	 *            Runnable to execute
	 * @return This instance for chaining
	 */
	public Cooldown onDone(Runnable r) {
		this.onDone = r;
		this.task = new BukkitRunnable() {
			public void run() {
				if (hasExpired()) {
					cancelTask();
					return;
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 5L);
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
		if(hasExpired())
			return "Expired";
		return Duration.milliseconds(expiry - System.currentTimeMillis()).formatText();
	}

}
