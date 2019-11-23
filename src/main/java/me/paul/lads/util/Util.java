package me.paul.lads.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class Util {

	public static final SplittableRandom RANDOM = new SplittableRandom();
	
	/**
	 * @return A random entry in the list.
	 */
	public static <T> T getRandomEntry(List<T> list) {
		return list.get(random(list.size()));
	}

	public static <T> T getRandomEntry(T... args) {
		return args[random(args.length)];
	}
	
	/**
	 * Shortcut of {@link Random#nextInt(int)}.
	 * @param max The maximum number to return (non-inclusive).
	 * @return A number in the range of [0, max).
	 */
	public static int random(int max) {
		return RANDOM.nextInt(max);
	}
	
	/**
	 * Retrieve random numbers inbetween [min, max]
	 * @param x - Value 1
	 * @param y - Value 2
	 * @return Random number [min, max]
	 */
	public static int random(int x, int y) {
		int max,min;
		max = Math.max(x, y);
		min = Math.min(x, y);

		return RANDOM.nextInt((max - min) + 1) + min;
	}
	
	public static void onTouchGround(Entity entity, Runnable runnable) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (entity.isDead() || !entity.isValid()) {
					cancel();
					return;
				}
				if (Util.isOnGround(entity)) {
					runnable.run();
					cancel();
				}
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugins()[0], 0, 1);
	}

	public static boolean isOnGround(Entity entity) {
		return (entity.getVelocity().getY() == getGravity() || entity.getVelocity().getY() == -0.0) && entity.getLocation().add(0, -1, 0).getBlock().getType().isSolid();
	}
	
	public static double getGravity() {
		return -0.0784000015258789;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> ArrayList<T> getEntitiesAround(Location location, double distance, Class<T> entity) {
		ArrayList<T> entities = new ArrayList<>();
		for (Entity e : location.getWorld().getEntities())
			if (e.getLocation().distance(location) <= distance && entity.isAssignableFrom(e.getClass()))
				entities.add((T) e);
		return entities;
	}
	
}
