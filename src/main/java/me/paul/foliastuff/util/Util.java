package me.paul.foliastuff.util;

import com.google.common.collect.Lists;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.util.scheduler.TaskHolder;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Entity;
import org.bukkit.material.Rails;
import org.bukkit.material.SimpleAttachableMaterialData;
import org.bukkit.material.Tree;
import org.bukkit.util.Vector;

import java.text.NumberFormat;
import java.util.*;

public class Util {

  private static String MINECRAFT_REVISION;
  public static String getMinecraftRevision() {
    if (MINECRAFT_REVISION == null) {
      MINECRAFT_REVISION = Bukkit.getServer().getClass().getPackage().getName();
    }
    return MINECRAFT_REVISION.substring(MINECRAFT_REVISION.lastIndexOf('.') + 2);
  }

  public static final SplittableRandom RANDOM = new SplittableRandom();

  /**
   * Formats the name of the given Enum to make it human readable.
   *
   * @param e The enum to format.
   * @return The formatted string.
   */
  public static String formatEnum(Enum<?> e) {
    return WordUtils.capitalizeFully(e.name().replaceAll("_", " "));
  }

  /**
   * @return A random entry in the list.
   */
  public static <T> T getRandomEntry(List<T> list) {
    return list.get(random(list.size()));
  }

  @SafeVarargs
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

  public static double random() {
    return RANDOM.nextDouble();
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
    TaskHolder holder = new TaskHolder();
    Sync.get(entity).interval(1).holder(holder).delay(1).run(() -> {
      if (entity.isDead() || !entity.isValid()) {
        holder.cancel();
        return;
      }

      if (entity.isInLava() || entity.isInWater() || isOnGround(entity)) {
        runnable.run();
        holder.cancel();
      }
    });
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
    if (pos.getWorld() == null)
      return Lists.newArrayList();

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

  public static final NumberFormat FORMAT = NumberFormat.getInstance(Locale.US);

  public static String format(Object o) {
    return FORMAT.format(o);
  }

  public static String format(int i) {
    return FORMAT.format(i);
  }

  public static String format(double i) {
    return FORMAT.format(i);
  }

  public static String format(long i) {
    return FORMAT.format(i);
  }

  public static String format(float i) {
    return FORMAT.format(i);
  }

  /**
   * Get direction that the sign is facing
   *
   * @param s Sign
   * @return Direction that sign is facing
   */
  public static Direction getDirFromSign(Sign s) {
    Optional<BlockFace> face = Util.getRotation(s.getBlock());
    return Direction.get(Util.getYaw(face.get()));
  }

  /**
   * Get direction that the sign is facing
   *
   * @param s Sign
   * @return Direction that sign is facing
   */
  public static Direction getDirFromSign(Block block) {
    Optional<BlockFace> face = Util.getRotation(block);
    return Direction.get(Util.getYaw(face.get()));
  }

  public static Optional<BlockFace> getRotation(Block block) {
    BlockData data = block.getBlockData();

    if (data instanceof Rotatable)
      return Optional.of(((Rotatable) data).getRotation());

    if (data instanceof Directional)
      return Optional.of(((Directional) data).getFacing());

    if (data instanceof SimpleAttachableMaterialData)
      return Optional.of(((SimpleAttachableMaterialData) data).getFacing());

    if (data instanceof Tree)
      return Optional.of(((Tree) data).getDirection());

    if (data instanceof Rails)
      return Optional.of(((Rails) data).getDirection());

    return Optional.empty();
  }

  public static float getYaw(BlockFace face) {
    return 1f * faceToYaw(face) + 90;
  }

  private static int faceToYaw(final BlockFace face) {
    return wrapAngle(45 * faceToNotch(face));
  }


  private static final EnumMap<BlockFace, Integer> notches = new EnumMap<>(BlockFace.class);
  public static final BlockFace[] RADIAL = {
    BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH,
    BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST
  };

  static {
    for (int i = 0; i < RADIAL.length; i++) {
      notches.put(RADIAL[i], i);
    }
  }

  private static int faceToNotch(BlockFace face) {
    Integer notch = notches.get(face);
    return notch == null ? 0 : notch.intValue();
  }

  private static int wrapAngle(int angle) {
    int wrappedAngle = angle;
    while (wrappedAngle <= -180) {
      wrappedAngle += 360;
    }
    while (wrappedAngle > 180) {
      wrappedAngle -= 360;
    }
    return wrappedAngle;
  }

  public enum Direction {
    NORTH("North", "N", 0),
    NORTH_EAST("North East", "NE", 45),
    EAST("East", "E", 90),
    SOUTH_EAST("South East",
      "SE", 135),
    SOUTH("South", "S", 180), SOUTH_WEST("South West", "SW",
      224),
    WEST("West", "W", 270),
    NORTH_WEST("North West", "NW", 315);

    Direction(String name, String acronym, int degrees) {
      Validate.notNull(name);
      Validate.notNull(acronym);
      Validate.isTrue(degrees >= 0 && degrees <= 360);

      this.name = name;
      this.acronym = acronym;
      this.degrees = degrees;
    }

    public Vector getVector() {
      return switch (this) {
        case NORTH -> BlockFace.NORTH.getDirection();
        case NORTH_EAST -> BlockFace.NORTH_EAST.getDirection();
        case EAST -> BlockFace.EAST.getDirection();
        case SOUTH_EAST -> BlockFace.SOUTH_EAST.getDirection();
        case SOUTH -> BlockFace.SOUTH.getDirection();
        case SOUTH_WEST -> BlockFace.SOUTH_WEST.getDirection();
        case WEST -> BlockFace.WEST.getDirection();
        case NORTH_WEST -> BlockFace.NORTH_WEST.getDirection();
      };
    }

    public static Direction get(float yaw) {
      return VALUES[(int) ((yaw >= 0.0F ? yaw + 22.4999F : yaw - 22.5F) / 45.0F) & 0x7];
    }

    public float getYaw() {
      return degrees;
    }

    public final String name;
    public final String acronym;
    public final int degrees;
    private static final Direction[] VALUES = {NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST,
      NORTH_WEST};
  }


}
