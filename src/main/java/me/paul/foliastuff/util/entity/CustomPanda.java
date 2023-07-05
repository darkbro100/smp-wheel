package me.paul.foliastuff.util.entity;

import io.papermc.paper.threadedregions.EntityScheduler;
import me.paul.foliastuff.util.entity.goal.FollowOwnerGoal;
import me.paul.foliastuff.util.entity.goal.OwnerHurtByTargetGoal;
import me.paul.foliastuff.util.entity.goal.OwnerHurtTargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CustomPanda extends Panda implements CustomEntity, PlayerRideableJumping {

  @javax.annotation.Nullable
  private UUID owner;

  static final Predicate<ItemEntity> PANDA_ITEMS = (entityitem) -> {
    ItemStack itemstack = entityitem.getItem();

    return (itemstack.is(Blocks.BAMBOO.asItem()) || itemstack.is(Blocks.CAKE.asItem())) && entityitem.isAlive() && !entityitem.hasPickUpDelay();
  };

  private PandaLookAtPlayerGoal lookAtPlayerGoal;

  public CustomPanda(EntityType<? extends Panda> type, Level world) {
    super(type, world);

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
    this.goalSelector.addGoal(3, new PandaAttackGoal(this, 1.2000000476837158D, true));
    this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(Blocks.BAMBOO.asItem()), false));
    this.goalSelector.addGoal(7, new CustomPanda.PandaSitGoal());
//    this.goalSelector.addGoal(8, new CustomPanda.PandaLieOnBackGoal(this));
    this.goalSelector.addGoal(8, new CustomPanda.PandaSneezeGoal(this));
    this.lookAtPlayerGoal = new CustomPanda.PandaLookAtPlayerGoal(this, Player.class, 6.0F);
    this.goalSelector.addGoal(9, this.lookAtPlayerGoal);
    this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    this.goalSelector.addGoal(12, new CustomPanda.PandaRollGoal(this));
    this.goalSelector.addGoal(13, new FollowOwnerGoal(this, 1.25D, 4, 32, false));
    this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
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
  public List<Entity> passengers() {
    return this.getPassengers();
  }

  @Override
  public boolean canPerformAction() {
    if (passengers().isEmpty())
      return super.canPerformAction();

    return false;
  }

  @Override
  public EntityScheduler getScheduler() {
    return getBukkitEntity().taskScheduler;
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, 0.27000000596046448D).add(Attributes.ATTACK_DAMAGE, 12.0D);
  }

  @Override
  public boolean asyncTP(ServerLevel destination, Vec3 pos, Float yaw, Float pitch, Vec3 speedDirectionUpdate, PlayerTeleportEvent.TeleportCause cause, long teleportFlags, Consumer<Entity> teleportComplete) {
    // nosuchmethoderror for god knows what reason, so we use the api method which ultimately just calls the nms method anyways?!?!?!?
    //    return ((Entity)this).teleportAsync(((ServerLevel) destination), pos, yaw, pitch, speedDirectionUpdate, cause, teleportFlags, teleportComplete);
    getBukkitEntity().teleportAsync(new Location(destination.getWorld(), pos.x, pos.y, pos.z, yaw, pitch), cause);
    return true;
  }

  @Override
  public void readAdditionalSaveData(CompoundTag nbt) {
    super.readAdditionalSaveData(nbt);

    UUID uuid;

    if (nbt.hasUUID("Owner")) {
      uuid = nbt.getUUID("Owner");
    } else {
      String s = nbt.getString("Owner");

      uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
    }

    if (uuid != null) {
      this.setOwner(uuid);
    }
  }

  @Override
  public void addAdditionalSaveData(CompoundTag nbt) {
    super.addAdditionalSaveData(nbt);

    if (this.getOwnerUUID() != null) {
      nbt.putUUID("Owner", this.getOwnerUUID());
    }
  }

  @Override
  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    if(isOwner(player) && player.isShiftKeyDown()) {
      this.setOnBack(true);
      return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    if (isOwner(player) && !this.isVehicle() && !this.isBaby()) {
      this.doPlayerRide(player);
      return InteractionResult.sidedSuccess(this.level().isClientSide);
    } else if(!isOwner(player)) {
      setTarget(player, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true);
      return InteractionResult.FAIL;
    } else {
      return super.mobInteract(player, hand);
    }
  }

  @Override
  public boolean canFollow() {
    if(!getPassengers().isEmpty())
      return false;

    return !isEating() && !isOnBack() && !isSitting();
  }

  protected void doPlayerRide(Player player) {
    if (!this.level().isClientSide) {
      this.sit(false);
      this.eat(false);
      this.setOnBack(false);
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
    if (passengers().isEmpty())
      return super.isControlledByLocalInstance();

    return passengers().get(0) instanceof Player;
  }

  @Override
  protected void tickRidden(Player controllingPlayer, Vec3 movementInput) {
    super.tickRidden(controllingPlayer, movementInput);
    Vec2 vec2f = this.getRiddenRotation(controllingPlayer);

    this.setRot(vec2f.y, vec2f.x);
    this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

    if (!passengers().isEmpty()) {
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

  @Override
  protected void checkFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) { }

  private void trySit() {
    if (!this.isInWater()) {
      this.setZza(0.0F);
      this.getNavigation().stop();
      this.sit(true);
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

  @Override
  public Mob instance() {
    return this;
  }

  private static class BlankMoveControl extends MoveControl {

    private final CustomPanda panda;

    public BlankMoveControl(CustomPanda panda) {
      super(panda);
      this.panda = panda;
    }

    @Override
    public void tick() {
      if (panda.passengers().isEmpty())
        super.tick();
    }
  }

  private static class PandaAttackGoal extends MeleeAttackGoal {

    private final CustomPanda panda;

    public PandaAttackGoal(CustomPanda panda, double speed, boolean pauseWhenMobIdle) {
      super(panda, speed, pauseWhenMobIdle);
      this.panda = panda;
    }

    @Override
    public boolean canUse() {
      return this.panda.canPerformAction() && super.canUse();
    }
  }

  private class PandaSitGoal extends Goal {

    private int cooldown;

    public PandaSitGoal() {
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
      if (this.cooldown <= CustomPanda.this.tickCount && !CustomPanda.this.isBaby() && !CustomPanda.this.isInWater() && CustomPanda.this.canPerformAction() && CustomPanda.this.getUnhappyCounter() <= 0) {
        List<ItemEntity> list = CustomPanda.this.level().getEntitiesOfClass(ItemEntity.class, CustomPanda.this.getBoundingBox().inflate(6.0D, 6.0D, 6.0D), CustomPanda.PANDA_ITEMS);

        return !list.isEmpty() || !CustomPanda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
      } else {
        return false;
      }
    }

    @Override
    public boolean canContinueToUse() {
      return !CustomPanda.this.isInWater() && (CustomPanda.this.isLazy() || CustomPanda.this.random.nextInt(reducedTickDelay(600)) != 1) ? CustomPanda.this.random.nextInt(reducedTickDelay(2000)) != 1 : false;
    }

    @Override
    public void tick() {
      if (!CustomPanda.this.isSitting() && !CustomPanda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
        CustomPanda.this.trySit();
      }

    }

    @Override
    public void start() {
      List<ItemEntity> list = CustomPanda.this.level().getEntitiesOfClass(ItemEntity.class, CustomPanda.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), CustomPanda.PANDA_ITEMS);

      if (!list.isEmpty() && CustomPanda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
        CustomPanda.this.getNavigation().moveTo((Entity) list.get(0), 1.2000000476837158D);
      } else if (!CustomPanda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
        CustomPanda.this.trySit();
      }

      this.cooldown = 0;
    }

    @Override
    public void stop() {
      ItemStack itemstack = CustomPanda.this.getItemBySlot(EquipmentSlot.MAINHAND);

      if (!itemstack.isEmpty()) {
        CustomPanda.this.forceDrops = true; // Paper
        CustomPanda.this.spawnAtLocation(itemstack);
        CustomPanda.this.forceDrops = false; // Paper
        CustomPanda.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        int i = CustomPanda.this.isLazy() ? CustomPanda.this.random.nextInt(50) + 10 : CustomPanda.this.random.nextInt(150) + 10;

        this.cooldown = CustomPanda.this.tickCount + i * 20;
      }

      CustomPanda.this.sit(false);
    }
  }

  private static class PandaLieOnBackGoal extends Goal {

    private final CustomPanda panda;
    private int cooldown;

    public PandaLieOnBackGoal(CustomPanda panda) {
      this.panda = panda;
    }

    @Override
    public boolean canUse() {
      return this.cooldown < this.panda.tickCount && this.panda.isLazy() && this.panda.canPerformAction() && this.panda.random.nextInt(reducedTickDelay(400)) == 1;
    }

    @Override
    public boolean canContinueToUse() {
      return !this.panda.isInWater() && (this.panda.isLazy() || this.panda.random.nextInt(reducedTickDelay(600)) != 1) ? this.panda.random.nextInt(reducedTickDelay(2000)) != 1 : false;
    }

    @Override
    public void start() {
      this.panda.setOnBack(true);
      this.cooldown = 0;
    }

    @Override
    public void stop() {
      this.panda.setOnBack(false);
      this.cooldown = this.panda.tickCount + 200;
    }
  }

  private static class PandaSneezeGoal extends Goal {

    private final CustomPanda panda;

    public PandaSneezeGoal(CustomPanda panda) {
      this.panda = panda;
    }

    @Override
    public boolean canUse() {
      return this.panda.isBaby() && this.panda.canPerformAction() ? (this.panda.isWeak() && this.panda.random.nextInt(reducedTickDelay(500)) == 1 ? true : this.panda.random.nextInt(reducedTickDelay(6000)) == 1) : false;
    }

    @Override
    public boolean canContinueToUse() {
      return false;
    }

    @Override
    public void start() {
      this.panda.sneeze(true);
    }
  }

  private static class PandaLookAtPlayerGoal extends LookAtPlayerGoal {

    private final Panda panda;

    public PandaLookAtPlayerGoal(Panda panda, Class<? extends LivingEntity> targetType, float range) {
      super(panda, targetType, range);
      this.panda = panda;
    }

    public void setTarget(LivingEntity target) {
      this.lookAt = target;
    }

    @Override
    public boolean canContinueToUse() {
      return this.lookAt != null && super.canContinueToUse();
    }

    @Override
    public boolean canUse() {
      if (this.mob.getRandom().nextFloat() >= this.probability) {
        return false;
      } else {
        if (this.lookAt == null) {
          if (this.lookAtType == Player.class) {
            this.lookAt = this.mob.level().getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
          } else {
            this.lookAt = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.lookAtType, this.mob.getBoundingBox().inflate((double) this.lookDistance, 3.0D, (double) this.lookDistance), (entityliving) -> {
              return true;
            }), this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
          }
        }

        return this.panda.canPerformAction() && this.lookAt != null;
      }
    }

    @Override
    public void tick() {
      if (this.lookAt != null) {
        super.tick();
      }

    }
  }

  private static class PandaRollGoal extends Goal {

    private final CustomPanda panda;

    public PandaRollGoal(CustomPanda panda) {
      this.panda = panda;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
      if ((this.panda.isBaby() || this.panda.isPlayful()) && this.panda.onGround()) {
        if (!this.panda.canPerformAction()) {
          return false;
        } else {
          float f = this.panda.getYRot() * 0.017453292F;
          float f1 = -Mth.sin(f);
          float f2 = Mth.cos(f);
          int i = (double) Math.abs(f1) > 0.5D ? Mth.sign((double) f1) : 0;
          int j = (double) Math.abs(f2) > 0.5D ? Mth.sign((double) f2) : 0;

          return this.panda.level().getBlockState(this.panda.blockPosition().offset(i, -1, j)).isAir() ? true : (this.panda.isPlayful() && this.panda.random.nextInt(reducedTickDelay(60)) == 1 ? true : this.panda.random.nextInt(reducedTickDelay(500)) == 1);
        }
      } else {
        return false;
      }
    }

    @Override
    public boolean canContinueToUse() {
      return false;
    }

    @Override
    public void start() {
      this.panda.roll(true);
    }

    @Override
    public boolean isInterruptable() {
      return false;
    }
  }

}
