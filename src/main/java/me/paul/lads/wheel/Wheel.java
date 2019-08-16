package me.paul.lads.wheel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;
import me.paul.lads.Main;
import me.paul.lads.util.Cooldown;
import me.paul.lads.util.Duration;
import me.paul.lads.util.LocUtil;
import me.paul.lads.wheel.effects.KeepGoingEffect;

@Getter
public class Wheel implements Runnable {

	private static final boolean DEBUG = false;
	private static final Material CENTER_BLOCK = Material.GLASS;

	@Getter
	private static List<Wheel> wheels = new ArrayList<>();

	private SplittableRandom random = new SplittableRandom();

	private int id;

	private Cooldown lastSpin = null;
	
	private Location center;
	private Map<Vector, Material> cachedBlocks;
	private int radius;
	private int pieces;
	private Material[] parts;
	private int offsetInc;
	private int updateFrequency;
	@Setter
	private int lastAngle = 0;
	@Setter
	private Location buttonBlock;
	
	private boolean started = false;
	private BukkitTask wheelTask;

	private UUID lastSpinner = null;
	
	/** Wheel Runnable Related **/
	private final int MAX_REVERSE = 5;
	
	private int reverseCount = 0;
	private boolean reverse = false;
	private boolean shouldReverse = false;
	private boolean shouldPlay = true;
	
	private float currentPitch = 2F;
	private float pitchInc = 0F;
	
	private int offset = 0;
	private int tickDelay = 0;
	private int ticksRan = 0;
	private int executions = 0;

	private int executionProjection = 0;
	
	private int execWait = random.nextInt(50, 100);
	private int originalExecWait = execWait;
	private final int execWaitInc = 4;

	public Wheel(Location center, int radius, int pieces, int offsetInc, int updateFrequency, Material... parts) {
		this.id = wheels.size() + 1;
		this.center = center;
		this.radius = radius;
		this.pieces = pieces;
		this.offsetInc = offsetInc;
		this.updateFrequency = updateFrequency;
		this.parts = new Material[pieces];

		for (int i = 0; i < pieces; i++) {
			if (i >= parts.length) {
				this.parts[i] = Material.DIAMOND_BLOCK;
			}

			this.parts[i] = parts[i];
		}

		this.cachedBlocks = new HashMap<>();
		wheels.add(this);
	}
	
//	public boolean isLocked(LabUser opener) {
//		return opener.getSpins() <= 0;
//	}

	public boolean isLocked() {
		return lastSpin != null && !lastSpin.hasExpired();
	}
	
	public static Wheel get(int id) {
		return wheels.stream().filter(w -> w.id == id).findFirst().orElse(null);
	}

	public boolean spin(Player opener) {
		return spin(opener, false);
	}
	
	public boolean spin(Player opener, boolean force) {
		if(isLocked() && !force)
			return false;
		if (started)
			return false;

		offset = lastAngle;

		Bukkit.broadcastMessage(ChatColor.GREEN + opener.getName() + " has spun the Wacky Wheel!");
		started = true;
		executionProjection = getTotalExecutions();
		/*
		 * Pitch Range: 0.5-2
		 * (0.5, 2^-11/12 - 2^11/12)
		 */
		pitchInc = 1.5F / executionProjection;
		lastSpinner = opener.getUniqueId();
		Runnable onDone = () -> Bukkit.broadcastMessage(ChatColor.GREEN + "The Wacky Wheel has been granted another life...");
		lastSpin = DEBUG ? new Cooldown(Duration.minutes(1)).onDone(onDone) : new Cooldown(Duration.minutes(2)).onDone(onDone);
		
		this.wheelTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this, updateFrequency, updateFrequency);
		return true;
	}

	/**
	 * Draw this wheel with no offset
	 */
	public void draw() {
		draw(0);
	}

	/**
	 * Draw this {@link Wheel} with a given offset. This will offset each piece of
	 * the wheel by however much you input
	 * 
	 * @param offset
	 *            Angle offset for each piece of the {@link Wheel}
	 */
	public void draw(int offset) {
		for (int i = radius; i >= 0; i--) {
			draw(offset, i);
		}
	}

	/**
	 * Draw this wheel with a given offset and a radius
	 * 
	 * @param offset
	 *            Offset to rotate the wheel
	 * @param r
	 *            Radius for how big the wheel is
	 */
	private void draw(int offset, int r) {
		int mIndex = 0;
		double pAngle = 360.0 / ((double) pieces);
		Material m = parts[0];

		int x, z;
		int y = center.getBlockY();

		for (double i = 0.0; i < 360.0; i += 1.25) {
			if (i % pAngle == 0) {
				mIndex++;

				if (mIndex >= parts.length)
					mIndex = 0;

				m = parts[mIndex];
			}

			double angle = (i + offset) * Math.PI / 180;

			x = (int) (center.getX() + r * Math.cos(angle));
			z = (int) (center.getZ() + r * Math.sin(angle));

			Vector v = new Vector(x, y, z);
			Material oldM = cachedBlocks.getOrDefault(v, Material.AIR);

			if (oldM != m) {
				center.getWorld().getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ())
						.setType(r == 0 ? CENTER_BLOCK : m);
			}

			cachedBlocks.put(v, m);
		}
	}

	private void launchFirework() {
		Set<Vector> circle = cachedBlocks.keySet();
		int yOff = random.nextInt(2, 4);
		int index = random.nextInt(circle.size());
		Iterator<Vector> it = circle.iterator();
		
		for(int i = 0; i < index; i++) {
			it.next();
		}
		
		Location loc = it.next().toLocation(center.getWorld()).clone().add(0, yOff, 0);
		
		FireworkEffect effect = FireworkEffect.builder().trail(false).flicker(false).withColor(Color.RED).withFade(Color.ORANGE).with(FireworkEffect.Type.BALL).build();
		final Firework fw = center.getWorld().spawn(loc, Firework.class);
		FireworkMeta meta = fw.getFireworkMeta();
		meta.addEffect(effect);
		meta.setPower(0);
		fw.setFireworkMeta(meta);
		new BukkitRunnable() {
		    @Override
		    public void run() {
		      fw.detonate();
		    }
		}.runTaskLater(Main.getInstance(), 2L);
	}
	
	@Override
	public void run() {
		if (execWait == 0) {
			WheelEffect effect = WheelEffectManager.getInstance().getRandomEffect();
			if(shouldReverse || effect instanceof KeepGoingEffect) {
				execWait++;
				reverse = true;
				shouldReverse = true;
				shouldPlay = false;
				return;
			}

			Bukkit.broadcastMessage(ChatColor.RED + "The Wacky Wheel has decided its fate!");
			lastAngle = offset;
			resetRunnable();
			
			Player player = Bukkit.getPlayer(lastSpinner);
			if(shouldPlay && player != null && player.isOnline()) {
				effect.play(player, this);
			}
			
			center.getWorld().playSound(center, Sound.ENTITY_ENDER_DRAGON_DEATH, 0.4F, 1.0F);
			
			// Play Fireworks
			new BukkitRunnable() {
				int fireworksExploded;
				int fireworkDelay = 5;
				int ran = 0;
				
				@Override
				public void run() {
					if(fireworksExploded > 35) {
						cancel();
						return;
					}
					
					if(ran % fireworkDelay == 0) {
						launchFirework();
						fireworksExploded++;
					}
					
					ran++;
				}
			}.runTaskTimer(Main.getInstance(), 0L, 1L);
			
			return;
		}

		if (executions != 0 && execWait != 0 && executions % execWait == 0) {
			if(reverse) {
				execWait *= 2;
				tickDelay -= execWaitInc;
			} else {
				execWait /= 2;
				tickDelay += execWaitInc;
			}
		}

		int ran = ticksRan;
		if(reverse) {
			ticksRan--;
		} else {
			ticksRan++;
		}

		if (tickDelay != 0 && ran % tickDelay != 0)
			return;

		draw(offset);
		offset += offsetInc;

		if (offset >= 360) {
			offset = 0;
		}

		center.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_HAT, 1.5F, currentPitch);
		if(reverse) {
			currentPitch += pitchInc;
			executions--;
			
			if(executions == 0) {
				reverse = false;
				execWait = originalExecWait;
				
				reverseCount++;
				if(reverseCount >= MAX_REVERSE) {
					shouldReverse = false;
				}
			}
		} else {
			currentPitch -= pitchInc;
			executions++;
		}
	}

	private int getTotalExecutions() {
		int temp = execWait;
		int fExec = 0;
		int fTickDelay = 0;
		int fRan = 0;
		
		while(temp != 0) {
			if (fExec != 0 && temp != 0 && fExec % temp == 0) {
				temp /= 2;
				fTickDelay += execWaitInc;
			}
			
			int tempRan = fRan;
			fRan++;

			if (fTickDelay != 0 && tempRan % fTickDelay != 0)
				continue;
			
			fExec++;
		}
		
		return fExec;
	}
	
	private void resetRunnable() {
		started = false;

		executions = 0;
		ticksRan = 0;
		tickDelay = 0;
		offset = 0;
		currentPitch = 2F;
		pitchInc = 0F;
		
		reverse = false;
		reverseCount = 0;
		shouldReverse = false;
		shouldPlay = true;
		
		execWait = random.nextInt(50, 100);
		originalExecWait = execWait;
		
		// Cancel the task
		wheelTask.cancel();
		wheelTask = null;
	}

	public static Wheel get(Location location) {
		return wheels.stream().filter(w -> w.getButtonBlock() != null && LocUtil.matchesExact(w.getButtonBlock(), location)).findAny().orElse(null);
	}

}
