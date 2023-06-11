package me.paul.foliastuff.util.scheduler;

import lombok.Getter;
import me.paul.foliastuff.other.FoliaStuff;

public class Async {

  @Getter
  private TaskBuilder builder;

  public Async() {
    builder = TaskBuilder.buildAsync(FoliaStuff.getInstance());
  }

  public static TaskBuilder get() {
    return new Async().getBuilder();
  }
}
