package me.paul.lads.util.scheduler;

import lombok.Getter;
import me.paul.lads.Main;

public class Async {

	@Getter
	private TaskBuilder builder;

	public Async() {
		builder = TaskBuilder.buildAsync(Main.getInstance());
	}
	
	public static TaskBuilder get() {
		return new Async().getBuilder();
	}
}
