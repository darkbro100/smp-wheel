package me.paul.foliastuff.util;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class LocUtil {

	public static Location locFromString(String string) {
		if (string == null)
			return null;
		String[] array = string.split(",");

		Location loc = new Location(Bukkit.getWorld(array[0]), Double.valueOf(array[1]), Double.valueOf(array[2]),
				Double.valueOf(array[3]));
		loc.setPitch(Float.parseFloat(array[4]));
		loc.setYaw(Float.parseFloat(array[5]));
		return loc;
	}

	public static String locToString(Location loc) {
		return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ","
				+ loc.getPitch() + "," + loc.getYaw();
	}

	public static String friendlyLocToString(Location loc) {
		return "X: " + loc.getBlockX() + ", Z: " + loc.getBlockZ();
	}

	public static String exactLocToString(Location loc) {
		return loc.getWorld().getName() + "," + round(loc.getX()) + "," + round(loc.getY()) + "," + round(loc.getZ())
				+ "," + loc.getPitch() + "," + loc.getYaw();
	}

	public static double round(double d) {
		return Math.round(d * 10.0) / 10.0;
	}

	public static boolean matches(Location loc1, Location loc2) {
		if (loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY()
				&& loc1.getBlockZ() == loc2.getBlockZ())
			return true;
		return false;
	}
	
	public static boolean matchesExact(Location l1, Location l2) {
		return l1.getWorld().equals(l2.getWorld()) && l1.getX() == l2.getX() && l1.getY() == l2.getY() && l1.getZ() == l2.getZ(); 
	}

	public static boolean chunkMatches(Chunk chunk1, Chunk chunk2) {
		if (chunk1.getX() == chunk2.getX() && chunk1.getZ() == chunk2.getZ())
			return true;
		return false;
	}

}
