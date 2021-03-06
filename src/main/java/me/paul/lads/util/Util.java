package me.paul.lads.util;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

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
     *
     * @param max The maximum number to return (non-inclusive).
     * @return A number in the range of [0, max).
     */
    public static int random(int max) {
        return RANDOM.nextInt(max);
    }

    /**
     * Retrieve random numbers inbetween [min, max]
     *
     * @param x - Value 1
     * @param y - Value 2
     * @return Random number [min, max]
     */
    public static int random(int x, int y) {
        int max, min;
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

    public static List<Block> makeCylinder(Location pos, double radius) {
        return makeCylinder(pos, radius, radius, true);
    }

    /**
     * Makes a cylinder.
     *
     * @param pos     Center of the cylinder
     * @param radiusX The cylinder's largest north/south extent
     * @param radiusZ The cylinder's largest east/west extent
     * @param filled  If false, only a shell will be generated.
     * @return number of blocks changed
     */
    public static List<Block> makeCylinder(Location pos, double radiusX, double radiusZ, boolean filled) {
        List<Block> affected = Lists.newArrayList();

        radiusX += 0.5;
        radiusZ += 0.5;

        int height = 1;
        // for height?
        pos = pos.clone().subtract(0, height, 0);

        final double invRadiusX = 1 / radiusX;
        final double invRadiusZ = 1 / radiusZ;

        final int ceilRadiusX = (int) Math.ceil(radiusX);
        final int ceilRadiusZ = (int) Math.ceil(radiusZ);

        double nextXn = 0;
        forX:
        for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextZn = 0;
            forZ:
            for (int z = 0; z <= ceilRadiusZ; ++z) {
                final double zn = nextZn;
                nextZn = (z + 1) * invRadiusZ;

                double distanceSq = lengthSq(xn, zn);
                if (distanceSq > 1) {
                    if (z == 0) {
                        break forX;
                    }
                    break forZ;
                }

                if (!filled) {
                    if (lengthSq(nextXn, zn) <= 1 && lengthSq(xn, nextZn) <= 1) {
                        continue;
                    }
                }

                for (int y = 0; y < height; ++y) {
                    Block b1 = pos.clone().add(x, y, z).getBlock();
                    Block b2 = pos.clone().add(-x, y, z).getBlock();
                    Block b3 = pos.clone().add(x, y, -z).getBlock();
                    Block b4 = pos.clone().add(-x, y, -z).getBlock();
                    affected.addAll(Lists.newArrayList(b1, b2, b3, b4));
                }
            }
        }

        return affected;
    }

    private static double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    private static double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }

}
