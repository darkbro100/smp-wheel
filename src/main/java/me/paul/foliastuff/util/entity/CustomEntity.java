//package me.paul.foliastuff.util.entity;
//
//import io.papermc.paper.threadedregions.EntityScheduler;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.Mob;
//import net.minecraft.world.entity.OwnableEntity;
//import net.minecraft.world.phys.Vec3;
//
//import java.util.List;
//
//public interface CustomEntity extends OwnableEntity {
//
//  List<Entity> passengers();
//
//  default boolean isBeingRidden() {
//    return !this.passengers().isEmpty();
//  }
//
//  Mob instance();
//
//  EntityScheduler getScheduler();
//
//  boolean asyncTP(ServerLevel destination, Vec3 pos, Float yaw, Float pitch, Vec3 speedDirectionUpdate,
//                  org.bukkit.event.player.PlayerTeleportEvent.TeleportCause cause, long teleportFlags,
//                  java.util.function.Consumer<Entity> teleportComplete);
//
//  default boolean isOwner(LivingEntity lent) {
//    return this.getOwnerUUID() != null && this.getOwnerUUID().equals(lent.getUUID());
//  }
//
//  boolean canFollow();
//
//}
