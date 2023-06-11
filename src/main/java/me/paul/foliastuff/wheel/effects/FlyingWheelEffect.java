package me.paul.foliastuff.wheel.effects;

import com.google.common.collect.Lists;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.util.scheduler.TaskHolder;
import me.paul.foliastuff.wheel.GenerateEffect;
import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftFallingBlock;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map.Entry;

@GenerateEffect(description = "wheel fucks off", key = "effect_fly", name = "Flying Wheel")
public class FlyingWheelEffect extends WheelEffect {

  private Wheel wheel;
  private Player spinner;

  @Override
  public void play(Player spinner, Wheel spun) {
    this.spinner = spinner;
    this.wheel = spun;

    FlyingWheel fw = new FlyingWheel();
    fw.startFlight();

    Bukkit.broadcastMessage(ChatColor.GREEN + "The wheel has had enough of " + spinner.getName() + "'s shit and flew away.");
  }

  private class FlyingWheel {

    private TaskHolder holder;
    private List<FallingBlock> blocks = Lists.newArrayList();
    private double xVel;
    private double zVel;

    public FlyingWheel() {
      FoliaStuff.getInstance().getLogger().info("(FlyingWheel) Initializing blocks list.");

      for (Entry<Vector, Material> entry : wheel.getCachedBlocks().entrySet()) {
        Vector v = entry.getKey();
        Material material = entry.getValue();
        Location loc = new Location(spinner.getWorld(), v.getBlockX(), v.getBlockY(), v.getBlockZ());

        loc.getBlock().setType(Material.AIR);
        FallingBlock fb = spinner.getWorld().spawnFallingBlock(loc.clone().add(0.5, 0.5, 0.5), material.createBlockData());
        fb.setDropItem(false);

        FallingBlockEntity fbe = ((CraftFallingBlock)fb).getHandle();
        fbe.noPhysics = true;

        blocks.add(fb);
      }
      FoliaStuff.getInstance().getLogger().info("(FlyingWheel) Initialized " + blocks.size() + " blocks.");
    }

    private void startFlight() {
      xVel = Math.random();
      zVel = Math.random();

      this.holder = new TaskHolder();
      Sync.get(blocks.get(0)).interval(1).holder(holder).run(new Runnable() {
        private int counter;

        @Override
        public void run() {
          blocks.forEach(b -> {
            // change speed of the flying effect
            b.setTicksLived(1);
            b.setVelocity(new Vector(xVel, 0.6, zVel));
          });

          if (counter++ > 20 * 10) {
            stop();
            FoliaStuff.getInstance().getLogger().info("Stopping FlyingEffect");
          }
        }
      });
    }

    private void stop() {
      for (Entry<Vector, Material> entry : wheel.getCachedBlocks().entrySet()) {
        Vector v = entry.getKey();
        Material material = entry.getValue();
        Location loc = new Location(spinner.getWorld(), v.getBlockX(), v.getBlockY(), v.getBlockZ());

        loc.getBlock().setType(material);
      }

      blocks.forEach(b -> b.remove());
      holder.cancel();
    }
  }

}
