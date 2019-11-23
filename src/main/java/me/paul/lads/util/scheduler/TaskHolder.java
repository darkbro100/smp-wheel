package me.paul.lads.util.scheduler;

/**
 * Holds the state of a given {@link TaskBuilder}
 * @author paulguarnieri
 *
 */
public class TaskHolder {

	private boolean cancelled = false;
	
	public boolean cancel() {
		if(cancelled) {
			return false;
		}
		
		cancelled = true;
		return true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
}
