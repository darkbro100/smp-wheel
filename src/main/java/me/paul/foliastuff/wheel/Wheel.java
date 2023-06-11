package me.paul.foliastuff.wheel;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.Setter;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.Cooldown;
import me.paul.foliastuff.util.Duration;
import me.paul.foliastuff.util.LocUtil;
import me.paul.foliastuff.util.Util;
import me.paul.foliastuff.wheel.effects.KeepGoingEffect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;

@Getter
public class Wheel implements Runnable {

  /**
   * Ideal offset: 1-2
   * Ideal size: 10-12
   * Ideal rate/freq: as low as possible lol
   */

  private static final boolean DEBUG = false;
  private static final Material CENTER_BLOCK = Material.GLASS;

  @Getter
  private static final List<Wheel> wheels = new ArrayList<>();

  private final SplittableRandom random = new SplittableRandom();

  private final int id;

  private Cooldown lastSpin = null;

  private final Location center;
  private final Map<Vector, Material> cachedBlocks;
  private final List<Block> blocks;
  private final int radius;
  private final int pieces;
  private final Material[] parts;
  private final int offsetInc;
  private final int updateFrequency;
  @Setter
  private int lastAngle = 0;
  @Setter
  private Location buttonBlock;

  private boolean started = false;
  private ScheduledTask wheelTask;

  private UUID lastSpinner = null;

  /**
   * Wheel Runnable Related
   **/
  private final int MAX_REVERSE = 5;

  private int reverseCount = 0;
  private boolean reverse = false;
  private boolean shouldReverse = false;
  private boolean shouldPlay = true;

  private float currentPitch = 2F;
  private float pitchInc = 0F;

  private int offset = 0;
  private int tickDelay = 0;
  private int ticksRan = 0;
  private int executions = 0;

  private int executionProjection = 0;

  private int execWait = random.nextInt(50, 100);
  private int originalExecWait = execWait;
  private final int execWaitInc = 4;

  public Wheel(Location center, int radius, int pieces, int offsetInc, int updateFrequency, Material... parts) {
    this.id = wheels.size() + 1;
    this.center = center;
    this.radius = radius;
    this.pieces = pieces;
    this.offsetInc = offsetInc;
    this.updateFrequency = updateFrequency;
    this.parts = new Material[pieces];

    for (int i = 0; i < pieces; i++) {
      if (i >= parts.length) {
        this.parts[i] = Material.DIAMOND_BLOCK;
      }

      this.parts[i] = parts[i];
    }

    this.cachedBlocks = new HashMap<>();
    this.blocks = Util.makeCylinder(center, radius);

    wheels.add(this);
  }


  public boolean isLocked() {
    return lastSpin != null && !lastSpin.hasExpired();
  }

  public static Wheel get(int id) {
    return wheels.stream().filter(w -> w.id == id).findFirst().orElse(null);
  }

  public boolean spin(Player opener) {
    return spin(opener, false);
  }

  public boolean spin(Player opener, boolean force) {
    if (isLocked() && !force)
      return false;
    if (started)
      return false;

    offset = lastAngle;

    Bukkit.broadcast(opener.displayName().append(Component.text(" has spun the Wacky Wheel!").color(TextColor.color(0, 255, 0))));
    started = true;
    executionProjection = getTotalExecutions();
    /*
     * Pitch Range: 0.5-2
     * (0.5, 2^-11/12 - 2^11/12)
     */
    pitchInc = 1.5F / executionProjection;
    lastSpinner = opener.getUniqueId();
    Runnable onDone = () -> Bukkit.broadcast(Component.text("The Wacky Wheel is ready to spin.").color(TextColor.color(0, 255, 0)));
    lastSpin = DEBUG ? new Cooldown(Duration.minutes(0.1)).onDone(onDone) : new Cooldown(Duration.minutes(2)).onDone(onDone);

    this.wheelTask = Bukkit.getRegionScheduler().runAtFixedRate(FoliaStuff.getInstance(), center, task -> run(), 1, 1);
    return true;
  }

  /**
   * Draw this wheel with no offset
   */
  public void draw() {
    draw(0);
  }

  /**
   * Draw this {@link Wheel} with a given offset. This will offset each piece of
   * the wheel by however much you input
   *
   * @param offset Angle offset for each piece of the {@link Wheel}
   */
  public void draw(int offset) {
    for (int i = radius; i >= 0; i--) {
      draw(offset, i);
    }
  }

  /**
   * Draw this wheel with a given offset and a radius
   *
   * @param r Radius for how big the wheel is
   */
  private void draw(int offset, int r) {
    int mIndex = 0;
    double angleDivisor = 360.0 / ((double) pieces);
    double pAngle = angleDivisor;
    Material m = parts[0];

    int x, z;
    int y = center.getBlockY() - 1; //cuz worldedit api is weird

    for (double i = 1; i <= 360.0; i++) {
      double d = Math.ceil(pAngle / i);
      if (d <= 1) {
        mIndex++;

        if (mIndex >= parts.length)
          mIndex = 0;

        m = parts[mIndex];
        pAngle += angleDivisor;
      }

      double angle = Math.toRadians(i + offset);

      x = (int) Math.floor((center.getX() + r * Math.cos(angle)));
      z = (int) Math.floor((center.getZ() + r * Math.sin(angle)));

      Vector v = new Vector(x, y, z);
      Material oldM = cachedBlocks.getOrDefault(v, Material.AIR);

      Block b = center.getWorld().getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ());
      if (blocks.contains(b)) {
        if (oldM != m)
          b.setType(m);
        cachedBlocks.put(v, m);
      }
    }
  }

  private void launchFirework() {
    Set<Vector> circle = cachedBlocks.keySet();
    int yOff = random.nextInt(2, 4);
    int index = random.nextInt(circle.size());
    Iterator<Vector> it = circle.iterator();

    for (int i = 0; i < index; i++) {
      it.next();
    }

    Location loc = it.next().toLocation(center.getWorld()).clone().add(0, yOff, 0);

    FireworkEffect effect = FireworkEffect.builder().trail(false).flicker(false).withColor(Color.RED).withFade(Color.ORANGE).with(FireworkEffect.Type.BALL).build();
    final Firework fw = center.getWorld().spawn(loc, Firework.class);
    FireworkMeta meta = fw.getFireworkMeta();
    meta.addEffect(effect);
    meta.setPower(0);
    fw.setFireworkMeta(meta);

    // delay ignite the firework
    fw.getScheduler().runDelayed(FoliaStuff.getInstance(), task -> fw.detonate(), null, 2);
  }

  @Override
  public void run() {
    if (execWait == 0) {
      WheelEffect effect = WheelEffectManager.getInstance().getRandomEffect();
      if (shouldReverse || effect instanceof KeepGoingEffect) {
        execWait++;
        reverse = true;
        shouldReverse = true;
        shouldPlay = false;
        return;
      }

      lastAngle = offset;
      resetRunnable();

      Player player = Bukkit.getPlayer(lastSpinner);
      if (shouldPlay && player != null && player.isOnline()) {
        effect.play(player, this);
      }

      // Play sound
      center.getWorld().playSound(center, Sound.ENTITY_ENDER_DRAGON_DEATH, 0.4F, 1.0F);

      // Play fireworks
      Bukkit.getRegionScheduler().runAtFixedRate(FoliaStuff.getInstance(), center, new Consumer<ScheduledTask>() {
        int fireworksExploded;
        final int fireworkDelay = 5;
        int ran = 0;

        @Override
        public void accept(ScheduledTask task) {
          if (fireworksExploded > 35) {
            task.cancel();
            return;
          }

          if (ran % fireworkDelay == 0) {
            launchFirework();
            fireworksExploded++;
          }

          ran++;
        }
      }, 1, 1);

      return;
    }

    if (executions != 0 && executions % execWait == 0) {
      if (reverse) {
        execWait *= 2;
        tickDelay -= execWaitInc;
      } else {
        execWait /= 2;
        tickDelay += execWaitInc;
      }
    }

    int ran = ticksRan;
    if (reverse) {
      ticksRan--;
    } else {
      ticksRan++;
    }

    if (tickDelay != 0 && ran % tickDelay != 0)
      return;

    draw(offset);
    offset += offsetInc;

    if (offset >= 360) {
      offset = 0;
    }

    center.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_HAT, 1.5F, currentPitch);
    if (reverse) {
      currentPitch += pitchInc;
      executions--;

      if (executions == 0) {
        reverse = false;
        execWait = originalExecWait;

        reverseCount++;
        if (reverseCount >= MAX_REVERSE) {
          shouldReverse = false;
        }
      }
    } else {
      currentPitch -= pitchInc;
      executions++;
    }
  }

  private int getTotalExecutions() {
    int temp = execWait;
    int fExec = 0;
    int fTickDelay = 0;
    int fRan = 0;

    while (temp != 0) {
      if (fExec != 0 && fExec % temp == 0) {
        temp /= 2;
        fTickDelay += execWaitInc;
      }

      int tempRan = fRan;
      fRan++;

      if (fTickDelay != 0 && tempRan % fTickDelay != 0)
        continue;

      fExec++;
    }

    return fExec;
  }

  private void resetRunnable() {
    started = false;

    executions = 0;
    ticksRan = 0;
    tickDelay = 0;
    offset = 0;
    currentPitch = 2F;
    pitchInc = 0F;

    reverse = false;
    reverseCount = 0;
    shouldReverse = false;
    shouldPlay = true;

    execWait = random.nextInt(50, 100);
    originalExecWait = execWait;

    // Cancel the task
    wheelTask.cancel();
    wheelTask = null;
  }

  public static Wheel get(Location location) {
    return wheels.stream().filter(w -> w.getButtonBlock() != null && LocUtil.matchesExact(w.getButtonBlock(), location)).findAny().orElse(null);
  }

}
