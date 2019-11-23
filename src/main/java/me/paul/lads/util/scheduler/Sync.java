package me.paul.lads.util.scheduler;

import lombok.Getter;
import me.paul.lads.Main;

public class Sync {

	@Getter
	private TaskBuilder builder;

	public Sync() {
		builder = TaskBuilder.buildSync(Main.getInstance());
	}
	
	public static TaskBuilder get() {
		return new Sync().getBuilder();
	}
}
