package me.paul.foliastuff.util.entity.goal;

import me.paul.foliastuff.util.entity.CustomEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.event.entity.EntityTeleportEvent;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {

  public static final int TELEPORT_WHEN_DISTANCE_IS = 12;
  private static final int MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 2;
  private static final int MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 3;
  private static final int MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 1;
  private final CustomEntity entity;
  private final Mob entityInstance;
  private LivingEntity owner;
  private final LevelReader level;
  private final double speedModifier;
  private final PathNavigation navigation;
  private int timeToRecalcPath;
  private final float stopDistance;
  private final float startDistance;
  private float oldWaterCost;
  private final boolean canFly;

  public FollowOwnerGoal(CustomEntity entity, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
    this.entity = entity;
    this.entityInstance = entity.instance();
    this.level = entity.instance().level();
    this.speedModifier = speed;
    this.navigation = entity.instance().getNavigation();
    this.startDistance = minDistance;
    this.stopDistance = maxDistance;
    this.canFly = leavesAllowed;
    this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    if (!(entityInstance.getNavigation() instanceof GroundPathNavigation) && !(entityInstance.getNavigation() instanceof FlyingPathNavigation)) {
      throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
    }
  }

  @Override
  public boolean canUse() {
    LivingEntity entityliving = this.entity.getOwner();

    if (entityliving == null) {
      return false;
    } else if (entityliving.isSpectator()) {
      return false;
    } else if (this.unableToMove()) {
      return false;
    } else if (this.entityInstance.distanceToSqr((Entity) entityliving) < (double) (this.startDistance * this.startDistance)) {
      return false;
    } else {
      this.owner = entityliving;
      return true;
    }
  }

  @Override
  public boolean canContinueToUse() {
    return this.navigation.isDone() ? false : (this.unableToMove() ? false : (this.owner.level() == this.level && this.entityInstance.distanceToSqr((Entity) this.owner) > (double) (this.stopDistance * this.stopDistance))); // Folia - region threading - check level
  }

  private boolean unableToMove() {
    return !entity.canFollow() || entity.isBeingRidden() || entityInstance.isPassenger() || entityInstance.isLeashed();
  }

  @Override
  public void start() {
    this.timeToRecalcPath = 0;
    this.oldWaterCost = this.entityInstance.getPathfindingMalus(BlockPathTypes.WATER);
  }

  @Override
  public void stop() {
    this.owner = null;
    this.navigation.stop();
  }

  @Override
  public void tick() {
    if (this.entityInstance.distanceToSqr(this.owner) <= 16 * 16) this.entityInstance.getLookControl().setLookAt(this.owner, 10.0F, (float) this.entityInstance.getMaxHeadXRot()); // Paper
    if (--this.timeToRecalcPath <= 0) {
      this.timeToRecalcPath = this.adjustedTickDelay(10);
      if (!io.papermc.paper.util.TickThread.isTickThreadFor(this.owner) || this.entityInstance.distanceToSqr((Entity) this.owner) >= 144.0D) { // Folia - region threading - required in case the player suddenly moves into another dimension
        this.teleportToOwner();
      } else {
        this.navigation.moveTo((Entity) this.owner, this.speedModifier);
      }

    }
  }

  private void teleportToOwner() {
    BlockPos blockposition = this.owner.blockPosition();
    // Folia start - region threading
    if (this.owner.isRemoved() || this.owner.level() != level) {
      return;
    }
    // Folia end - region threading

    for (int i = 0; i < 10; ++i) {
      int j = this.randomIntInclusive(-3, 3);
      int k = this.randomIntInclusive(-1, 1);
      int l = this.randomIntInclusive(-3, 3);
      boolean flag = this.maybeTeleportTo(blockposition.getX() + j, blockposition.getY() + k, blockposition.getZ() + l);

      if (flag) {
        return;
      }
    }

  }

  private boolean maybeTeleportTo(int x, int y, int z) {
    if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
      return false;
    } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
      return false;
    } else {
      // CraftBukkit start
      CraftEntity entity = this.entityInstance.getBukkitEntity();
      Location to = new Location(entity.getWorld(), (double) x + 0.5D, (double) y, (double) z + 0.5D, this.entityInstance.getYRot(), this.entityInstance.getXRot());
      EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
      this.entityInstance.level().getCraftServer().getPluginManager().callEvent(event);
      if (event.isCancelled()) {
        return false;
      }
      to = event.getTo();

      // Folia start - region threading - can't teleport here, we may be removed by teleport logic - delay until next tick
      // also, use teleportAsync so that crossing region boundaries will not blow up
      Location finalTo = to;
      this.entity.getScheduler().schedule(nmsEntity -> {
        if (nmsEntity.level() == FollowOwnerGoal.this.level) {
          this.entity.asyncTP(
            (net.minecraft.server.level.ServerLevel) nmsEntity.level(),
            new net.minecraft.world.phys.Vec3(finalTo.getX(), finalTo.getY(), finalTo.getZ()),
            Float.valueOf(finalTo.getYaw()), Float.valueOf(finalTo.getPitch()),
            net.minecraft.world.phys.Vec3.ZERO, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.UNKNOWN, Entity.TELEPORT_FLAG_LOAD_CHUNK,
            null
          );
        }
      }, null, 1L);
      // Folia start - region threading - can't teleport here, we may be removed by teleport logic - delay until next tick
      // CraftBukkit end
      this.navigation.stop();
      return true;
    }
  }

  private boolean canTeleportTo(BlockPos pos) {
    BlockPathTypes pathtype = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());

    if (pathtype != BlockPathTypes.WALKABLE) {
      return false;
    } else {
      BlockState iblockdata = this.level.getBlockState(pos.below());

      if (!this.canFly && iblockdata.getBlock() instanceof LeavesBlock) {
        return false;
      } else {
        BlockPos blockposition1 = pos.subtract(this.entityInstance.blockPosition());

        return this.level.noCollision(this.entityInstance, this.entityInstance.getBoundingBox().move(blockposition1));
      }
    }
  }

  private int randomIntInclusive(int min, int max) {
    return this.entityInstance.getRandom().nextInt(max - min + 1) + min;
  }
}
