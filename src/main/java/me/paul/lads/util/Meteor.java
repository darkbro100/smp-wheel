package me.paul.lads.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.paul.lads.util.scheduler.Sync;
import me.paul.lads.util.scheduler.TaskHolder;

public class Meteor {

	public static List<Material> materials = Arrays.asList(Material.STONE, Material.COBBLESTONE, Material.BEDROCK);

	private int radius;
	private Location spawn;
	private Vector dir;
	private List<ArmorStand> stands = new LinkedList<>();

	public Meteor(int radius) {
		this.radius = radius;
	}

	/**
	 * Spawns the Meteor at the given location. The Meteor will not move until
	 * {@link #strike(Location)} is called.
	 *
	 * @param l The location to spawn the Meteor.
	 */
	public void spawn(Location l) {
		this.spawn = l;
		MinecraftSphere s = new MinecraftSphere(l, radius);

		for (Location face : s.getFaces(24)) {
			ArmorStand as = face.getWorld().spawn(face, ArmorStand.class);
			as.setGravity(false);
			as.setHelmet(new ItemStack(Util.getRandomEntry(materials)));
			as.setVisible(false);
			stands.add(as);
		}
	}

	/**
	 * Starts moving the Meteor towards the given location. If the Meteor
	 * strikes the given location (hits it) then it will explode.
	 *
	 * @param l The location to strike.
	 */
	public void strike(Location l) {
		dir = l.clone().subtract(spawn).toVector().normalize();
		stands.forEach(s -> s.setGravity(true));
		stands.forEach(s -> s.setVelocity(dir));

		TaskHolder holder = new TaskHolder();
		Sync.get().interval(1).holder(holder).run(() -> {
			stands = stands.stream().filter(s -> !s.isOnGround() && !s.isDead()).collect(Collectors.toList());
			stands.forEach(s -> {
				s.setVelocity(dir);

				if (Util.RANDOM.nextBoolean())
					s.getLocation().getWorld().spawnParticle(Particle.FLAME, s.getLocation().clone().add(0, 2, 0), 1, radius, radius, radius);
			});

			if (stands.isEmpty())
				holder.cancel();
		});

		stands.forEach(stand -> Util.onTouchGround(stand, () -> {
			stand.remove();
			stand.getWorld().createExplosion(stand.getLocation(), 5F, true);
			Util.getEntitiesAround(stand.getLocation(), 1.5, ArmorStand.class).forEach(ArmorStand::remove);
		}));
	}
	
}
