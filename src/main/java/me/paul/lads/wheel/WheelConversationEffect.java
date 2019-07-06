package me.paul.lads.wheel;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import me.paul.lads.Main;
import me.paul.lads.util.Duration;

public class WheelConversationEffect extends WheelEffect implements Runnable {

	/**
	 * ArrayList containing the contents of the conversation the wheel has with you
	 */
	private List<String> conversation;
	
	/**
	 * Player spinning the wheel (This variable will be null at first, will be updated when {@link play} is called
	 */
	private Player spinner;
	
	/**
	 * The actual bukkit task that handles the conversation
	 */
	private BukkitTask conversationTask;
	
	/* Tick related stuff */
	private int ticksRan;
	private int currentIndex;
	private int maxIndex;
	private int delay;
	
	public WheelConversationEffect(Duration length, String...conversation) {
		this.conversation = new ArrayList<>();
		for(String s : conversation)
			this.conversation.add(s);
		
		this.maxIndex = this.conversation.size() - 1;
		this.currentIndex = 0;
		this.ticksRan = 0;
		this.delay = length.toTicks() / this.maxIndex;
	}
	
	@Override
	public void run() {
		if(currentIndex > maxIndex) {
			cancelTask();
			return;
		}
		
		if(ticksRan % delay == 0) {
			String message = conversation.get(currentIndex);
			spinner.sendMessage(message);
			
			currentIndex++;
		}
		
		ticksRan++;
	}
	
	private void cancelTask() {
		conversationTask.cancel();
		
		spinner = null;
		conversationTask = null;
	}

	@Override
	public void play(Player spinner, Wheel spun) {
		this.spinner = spinner;
		this.conversationTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this, 1, 1);
	}

}
