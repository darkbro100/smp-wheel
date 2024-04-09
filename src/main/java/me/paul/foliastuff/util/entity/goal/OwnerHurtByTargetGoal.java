//package me.paul.foliastuff.util.entity.goal;
//
//import me.paul.foliastuff.util.entity.CustomEntity;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.ai.goal.Goal;
//import net.minecraft.world.entity.ai.goal.target.TargetGoal;
//import net.minecraft.world.entity.ai.targeting.TargetingConditions;
//
//import java.util.EnumSet;
//
//public class OwnerHurtByTargetGoal extends TargetGoal {
//  private final CustomEntity ent;
//  private LivingEntity ownerLastHurtBy;
//  private int timestamp;
//
//  public OwnerHurtByTargetGoal(CustomEntity ent) {
//    super(ent.instance(), false);
//    this.ent = ent;
//    this.setFlags(EnumSet.of(Goal.Flag.TARGET));
//  }
//
//  @Override
//  public boolean canUse() {
//    if (ent.isBeingRidden())
//      return false;
//
//    LivingEntity owner = ent.getOwner();
//    if (owner == null)
//      return false;
//
//    this.ownerLastHurtBy = owner.getLastHurtByMob();
//    int i = owner.getLastHurtByMobTimestamp();
//
//    return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
//  }
//
//  @Override
//  public void start() {
//    this.mob.setTarget(this.ownerLastHurtBy, org.bukkit.event.entity.EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true); // CraftBukkit - reason
//    LivingEntity entityliving = this.ent.getOwner();
//
//    if (entityliving != null) {
//      this.timestamp = entityliving.getLastHurtByMobTimestamp();
//    }
//
//    super.start();
//  }
//}
