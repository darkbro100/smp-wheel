package me.paul.foliastuff;

import com.destroystokyo.paper.ParticleBuilder;
import com.mojang.datafixers.util.Pair;
import me.paul.foliastuff.util.Duration;
import me.paul.foliastuff.util.Util;
import me.paul.foliastuff.util.scheduler.TaskHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.CompletableFuture;

import static me.paul.foliastuff.CaseRunnable.MAX_ITEMS;

public class CaseReceiveItemRunnable implements Runnable {

  private final Item item;
  private final ItemStack stack;
  private final CaseItem caseItem;
  private final TaskHolder holder;
  private final CompletableFuture<Pair<CaseItem, ItemStack>> future;

  private final Case caseInst;

  public CaseReceiveItemRunnable(Case caseInst, Item item, CaseItem caseItem, ItemStack stack, TaskHolder holder, CompletableFuture<Pair<CaseItem, ItemStack>> future) {
    this.caseItem = caseItem;
    this.item = item;
    this.stack = stack;
    this.holder = holder;
    this.future = future;
    this.caseInst = caseInst;
  }

  private int ticks;

  private static final Duration LIMIT = Duration.seconds(3);

  private static final double radius = 0.5d;
  private static final double Y_INC = 0.03d;
  private static final double Y_HELIX = 0.015;

  private float pitch = 0.5f;
  private boolean pitchUp = true;

  @Override
  public void run() {
    if (ticks >= LIMIT.ticks()) {
      item.getWorld().playSound(item.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.5f);
      hollowSphereParticles();

      item.remove();
      holder.cancel();
      caseInst.spinner = null;

      caseInst.displayEntity().text(Component.text("Right Click to Spin!").color(TextColor.color(0x965613)));
      future.complete(new Pair<>(caseItem, stack));

      // update floor
      caseInst.resetFloor(Util.Direction.get(caseInst.getDirection().getYaw() + 90).getVector().multiply(-1));
    }

    item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, pitch);
    item.setVelocity(new Vector(0, Y_INC, 0));
    helix();

    pitch += pitchUp ? 0.05f : -0.05f;
    if (pitch >= 2F)
      pitchUp = true;
    else if (pitch <= 0.5f)
      pitchUp = false;

    ticks++;
  }

  private void hollowSphereParticles() {
    int numParticles = 100; // Number of particles in the sphere
    double radius = 1.0; // Radius of the sphere
    double thickness = 0.1; // Thickness of the shell
    Location center = item.getLocation().clone().add(0, 0.5, 0); // Center point of the sphere

    for (int i = 0; i < numParticles; i++) {
      double theta = 2 * Math.PI * Math.random(); // Random angle in the range [0, 2π]
      double phi = Math.acos(2 * Math.random() - 1); // Random angle in the range [0, π]

      // Convert spherical coordinates to Cartesian coordinates
      double x = (radius + thickness) * Math.sin(phi) * Math.cos(theta);
      double y = (radius + thickness) * Math.sin(phi) * Math.sin(theta);
      double z = (radius + thickness) * Math.cos(phi);

      // Spawn the particle at the calculated position
      new ParticleBuilder(Particle.SOUL_FIRE_FLAME)
        .location(center)
        .extra(0.1)
        .count(0)
        .offset(x, y, z)
        .spawn();
    }
  }

  private double y = 0;

  private void helix() {
    double x = radius * Math.cos(y * 10);
    double z = radius * Math.sin(y * 10);
    new ParticleBuilder(Particle.FLAME).location(item.getLocation().clone().add(x, y, z)).extra(0).count(1).spawn();

    x = radius * Math.sin(-(y * 10));
    z = radius * Math.cos(-(y * 10));
    new ParticleBuilder(Particle.FLAME).location(item.getLocation().clone().add(x, y, z)).extra(0).count(1).spawn();

    x = radius * Math.cos(-(y * 10));
    z = radius * Math.sin(-(y * 10));
    new ParticleBuilder(Particle.FLAME).location(item.getLocation().clone().add(x, y, z)).extra(0).count(1).spawn();

    x = radius * Math.sin((y * 10));
    z = radius * Math.cos((y * 10));
    new ParticleBuilder(Particle.FLAME).location(item.getLocation().clone().add(x, y, z)).extra(0).count(1).spawn();

    y += Y_HELIX;
  }

}
