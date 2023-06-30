package me.paul.foliastuff.util.entity;

import com.mojang.serialization.Lifecycle;
import io.netty.util.DefaultAttributeMap;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CustomPanda extends Panda implements OwnableEntity, PlayerRideableJumping {

  @javax.annotation.Nullable
  private UUID owner;

  public CustomPanda(EntityType<? extends Panda> type, Level world) {
    super(type, world);
//    setPos(location.getX(), location.getY(), location.getZ());
    setMaxUpStep(1.0f);
  }

  @Nullable
  @Override
  public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
    return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
  }


  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(0, new FloatGoal(this));
//    this.goalSelector.addGoal(2, new Panda.PandaPanicGoal(this, 2.0D));
//    this.goalSelector.addGoal(2, new Panda.PandaBreedGoal(this, 1.0D));
//    this.goalSelector.addGoal(3, new Panda.PandaAttackGoal(this, 1.2000000476837158D, true));
    this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(Blocks.BAMBOO.asItem()), false));
//    this.goalSelector.addGoal(6, new Panda.PandaAvoidGoal<>(this, Player.class, 8.0F, 2.0D, 2.0D));
//    this.goalSelector.addGoal(6, new Panda.PandaAvoidGoal<>(this, Monster.class, 4.0F, 2.0D, 2.0D));
//    this.goalSelector.addGoal(7, new Panda.PandaSitGoal());
//    this.goalSelector.addGoal(8, new Panda.PandaLieOnBackGoal(this));
//    this.goalSelector.addGoal(8, new Panda.PandaSneezeGoal(this));
//    this.lookAtPlayerGoal = new Panda.PandaLookAtPlayerGoal(this, Player.class, 6.0F);
//    this.goalSelector.addGoal(9, this.lookAtPlayerGoal);
    this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
//    this.goalSelector.addGoal(12, new Panda.PandaRollGoal(this));
//    this.goalSelector.addGoal(13, new FollowParentGoal(this, 1.25D));
    this.goalSelector.addGoal(14, new WaterAvoidingRandomStrollGoal(this, 1.0D));
//    this.targetSelector.addGoal(1, (new Panda.PandaHurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
  }

  @Nullable
  @Override
  public UUID getOwnerUUID() {
    return owner;
  }

  public void setOwner(UUID uuid) {
    this.owner = uuid;
  }

  @Override
  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    if (!this.isVehicle() && !this.isBaby()) {
      this.doPlayerRide(player);
      return InteractionResult.sidedSuccess(this.level().isClientSide);
    } else {
      return super.mobInteract(player, hand);
    }
  }

  protected void doPlayerRide(Player player) {
//    this.setEating(false);
//    this.setStanding(false);
    if (!this.level().isClientSide) {
      player.setYRot(this.getYRot());
      player.setXRot(this.getXRot());
      player.startRiding(this);
    }
  }

  @Override
  public void tick() {
//    System.out.println("pre-deltamovement tick: " + getDeltaMovement());
    super.tick();
//    System.out.println("post-deltamovement tick: " + getDeltaMovement());
  }

  @Override
  public void travel(Vec3 movementInput) {
    super.travel(movementInput);
  }

  @Override
  public boolean isControlledByLocalInstance() {
    if (getPassengers().isEmpty())
      return super.isControlledByLocalInstance();

    return getPassengers().get(0) instanceof Player;
  }

  @Override
  protected void tickRidden(Player controllingPlayer, Vec3 movementInput) {
    super.tickRidden(controllingPlayer, movementInput);
    Vec2 vec2f = this.getRiddenRotation(controllingPlayer);

    this.setRot(vec2f.y, vec2f.x);
    this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

    if (!getPassengers().isEmpty()) {
      LivingEntity lent = getControllingPassenger();
      if (lent instanceof Player && lent.jumping && !isJumping) {
        isJumping = true;
      }
    }

    if (this.isControlledByLocalInstance()) {
      if (this.onGround() && isJumping) {
        this.executeRidersJump(movementInput);
      }
      isJumping = false;
    }

  }

  protected boolean isJumping;

  public double getCustomJump() {
    return 1.0d;
//    return this.getAttributeValue(Attributes.JUMP_STRENGTH);
  }

  protected void executeRidersJump(Vec3 movementInput) {
    double d0 = this.getCustomJump() * (double) (float) 1.0 * (double) this.getBlockJumpFactor();
    double d1 = d0 + (double) this.getJumpBoostPower();
    Vec3 vec3d1 = this.getDeltaMovement();

    this.setDeltaMovement(vec3d1.x, d1, vec3d1.z);
    isJumping = true;
    this.hasImpulse = true;
    if (movementInput.z > 0.0D) {
      float f1 = Mth.sin(this.getYRot() * 0.017453292F);
      float f2 = Mth.cos(this.getYRot() * 0.017453292F);

      this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f1 * (float) 1.0), 0.0D, (double) (0.4F * f2 * (float) 1.0)));
    }

  }

  protected Vec2 getRiddenRotation(LivingEntity controllingPassenger) {
    return new Vec2(controllingPassenger.getXRot() * 0.5F, controllingPassenger.getYRot());
  }

  @Override
  protected Vec3 getRiddenInput(Player controllingPlayer, Vec3 movementInput) {
    if (!this.onGround()) {
      return Vec3.ZERO;
    } else {
      float f = controllingPlayer.xxa * 0.5F;
      float f1 = controllingPlayer.zza;

      if (f1 <= 0.0F) {
        f1 *= 0.25F;
      }

      return new Vec3((double) f, 0.0D, (double) f1);
    }
  }

  @Override
  protected float getRiddenSpeed(Player controllingPlayer) {
    return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
  }

  @javax.annotation.Nullable
  @Override
  public LivingEntity getControllingPassenger() {
    Entity entity = this.getFirstPassenger();

    if (entity instanceof Mob) {
      Mob entityinsentient = (Mob) entity;

      return entityinsentient;
    } else {
      entity = this.getFirstPassenger();
      if (entity instanceof Player) {
        Player entityhuman = (Player) entity;

        return entityhuman;
      }

      return null;
    }
  }

  @Override
  public void onPlayerJump(int strength) {
    System.out.println("onPlayerJump: " + strength);
  }

  @Override
  public boolean canJump() {
    return true;
  }


  @Override
  public void handleStartJump(int height) {
    System.out.println("handleStartJump");

    // CraftBukkit start
    float power;
    if (height >= 90) {
      power = 1.0F;
    } else {
      power = 0.4F + 0.4F * (float) height / 90.0F;
    }

//    // CraftBukkit end
//    this.allowStandSliding = true;
//    this.standIfPossible();
//    this.playJumpSound();
  }

  @Override
  public void handleStopJump() {
    System.out.println("stop jump");
  }

  private static class BlankMoveControl extends MoveControl {

    private final CustomPanda panda;

    public BlankMoveControl(CustomPanda panda) {
      super(panda);
      this.panda = panda;
    }

    @Override
    public void tick() {
      if (panda.getPassengers().isEmpty())
        super.tick();
    }
  }

}
