package me.paul.foliastuff.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MinecraftSphere {

	public Location center;
	public int radius;

	public MinecraftSphere(Location center, int radius) {
		this.center = center;
		this.radius = radius;
	}

	public boolean contains(Location location) {
		return location.distance(center) <= radius;
	}

	public Location getRandomLocation() {
		Random r = new Random();
		int x = r.nextInt(radius) * (r.nextBoolean() ? 1 : -1);
		int y = r.nextInt(radius) * (r.nextBoolean() ? 1 : -1);
		int z = r.nextInt(radius) * (r.nextBoolean() ? 1 : -1);
		return new Location(center.getWorld(), center.getX() + x, center.getY() + y, center.getZ() + z);
	}

	public Set<Location> getFaces() {
		return getFaces(360);
	}

	public Set<Location> getFaces(int faces) {
		Set<Location> locations = new HashSet<>();
		double inc = 360d / faces;
		Location calcLoc = new Location(center.getWorld(), 0, 0, 0);
		for (float y = 0; y < 360; y += inc)
			for (float p = 0; p < 360; p += inc) {
				Location location = calcLoc;
				location.setYaw(y);
				location.setPitch(p);

				Vector dir = location.getDirection().clone();
				dir.setX(dir.getX() * radius);
				dir.setY(dir.getY() * radius);
				dir.setZ(dir.getZ() * radius);

				Vector loc = center.toVector().add(dir);
				locations.add(loc.toLocation(center.getWorld()));
			}
		return locations;
	}
	
}
