package me.paul.foliastuff.other;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocUtil {

	public static Location locFromString(String str) {
		String[] array = str.split(",");
		return new Location(Bukkit.getWorld(array[0]), Double.parseDouble(array[1]), Double.parseDouble(array[2]),
				Double.parseDouble(array[3]));
	}

	public static String locToString(Location loc) {
		return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
	}

	public static boolean matches(Location loc, Location loc2) {
		return loc.getBlockX() == loc2.getBlockX() && loc.getBlockY() == loc2.getBlockY()
				&& loc.getBlockZ() == loc2.getBlockZ();
	}
	
	public static String locToFriendlyString(Location loc) {
		return "X: " + loc.getBlockX() + " Z: " + loc.getBlockZ();
	}

}
