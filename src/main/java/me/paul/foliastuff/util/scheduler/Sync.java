package me.paul.foliastuff.util.scheduler;

import lombok.Getter;
import me.paul.foliastuff.other.FoliaStuff;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class Sync {

  @Getter
  private final TaskBuilder builder;

  public Sync(TaskBuilder builder) {
    this.builder = builder;
  }

  public static TaskBuilder get(Entity ent) {
    return new Sync(TaskBuilder.build(FoliaStuff.getInstance(), ent)).getBuilder().delay(-1).interval(-1);
  }

  public static TaskBuilder get(Location loc) {
    return new Sync(TaskBuilder.build(FoliaStuff.getInstance(), loc)).getBuilder().delay(-1).interval(-1);
  }
}
