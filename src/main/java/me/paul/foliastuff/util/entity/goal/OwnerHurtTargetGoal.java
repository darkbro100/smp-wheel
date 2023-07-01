package me.paul.foliastuff.util.entity.goal;

import me.paul.foliastuff.util.entity.CustomEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class OwnerHurtTargetGoal extends TargetGoal {

  private final CustomEntity entity;
  private LivingEntity ownerLastHurt;
  private int timestamp;

  public OwnerHurtTargetGoal(CustomEntity entity) {
    super(entity.instance(), false);
    this.entity = entity;
    this.setFlags(EnumSet.of(Goal.Flag.TARGET));
  }

  @Override
  public boolean canUse() {
    if (entity.isBeingRidden())
      return false;

    LivingEntity owner = entity.getOwner();
    if (owner == null)
      return false;

    this.ownerLastHurt = owner.getLastHurtMob();
    int i = owner.getLastHurtMobTimestamp();

    return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT);
  }

  @Override
  public void start() {
    this.mob.setTarget(this.ownerLastHurt, org.bukkit.event.entity.EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true); // CraftBukkit - reason
    LivingEntity entityliving = this.entity.getOwner();

    if (entityliving != null) {
      this.timestamp = entityliving.getLastHurtMobTimestamp();
    }

    super.start();
  }
}
