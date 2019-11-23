package me.paul.lads.wheel.effects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.paul.lads.util.Meteor;
import me.paul.lads.util.Util;
import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;

@GenerateEffect(description = "Strikes a meteor on the player", key = "wheel_meteor", name = "Meteor")
public class MeteorEffect extends WheelEffect {

	private static final int RADIUS = 3;
	
	@Override
	public void play(Player spinner, Wheel spun) {
		int xOff = Util.random(-16, 16);
		int zOff = Util.random(-16, 16);
		int yOff = Util.random(24, 48);
		
		Location spawnLoc = spinner.getLocation().clone().add(xOff, yOff, zOff);
		Meteor m = new Meteor(RADIUS);
		m.spawn(spawnLoc);
		m.strike(spinner.getLocation());
	}

}
