package me.paul.lads.wheel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.reflections.Reflections;

import lombok.Getter;

public class WheelEffectManager {

	private List<WheelEffect> effects;
	private SplittableRandom random;

	private WheelEffectManager() {
		this.effects = new ArrayList<WheelEffect>();
		this.random = new SplittableRandom();

		setup();
	}

	/**
	 * This setups the {@link WheelEffectManager} instance. Loads all the wheel
	 * effects into the ArrayList
	 */
	private void setup() {
		Reflections ref = new Reflections("me.paul.lads.wheel.effects");
		Set<Class<?>> clazzes = ref.getTypesAnnotatedWith(GenerateEffect.class);

		System.out.println("Found " + clazzes.size() + " classes");

		clazzes.forEach(clazz -> {
			if (WheelEffect.class.isAssignableFrom(clazz)) {
				try {
					GenerateEffect ge = clazz.getAnnotation(GenerateEffect.class);
					if (ge.enabled()) {
						WheelEffect we = (WheelEffect) clazz.newInstance();
						System.out.println("Found new effect: " + ge.name());
						effects.add(we);
					} else {
						System.out.println("Skipped loading: " + ge.name());
					}
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		});

		// Manual Registrations. Easy Effects that don't really require creating a whole
		// new class/file for them\

		// world, x, y, z, yaw, pitch

//		String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[The Wacky" + ChatColor.RED + ChatColor.ITALIC
//				+ " WHEEL" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";

		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20*40, 7));
					p.sendTitle(ChatColor.BLUE + "Up, up and away!!", "It's not flying, it's falling, with style.", 10, 20*4, 10);
					p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
				}
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " bought everyone a hot balloon ride, except there's no balloon. And also no parachutes.");
			}
		});


//		effects.add(WheelImageEffect.create("/smplive/plugins/wheelimage/troll.png"));

		// REPLACE DIRECTORY WITH NEW IMAGES

		System.out.println("Loaded " + effects.size() + " effects...");
	}

	@Getter
	private static final WheelEffectManager instance = new WheelEffectManager();

	public WheelEffect getRandomEffect() {
		int index = random.nextInt(effects.size());
		return effects.get(index);
	}

	public WheelEffect getEffect(String key) {
		return effects.stream().filter(e -> {
			if (e.getClass().isAnnotationPresent(GenerateEffect.class)) {
				return e.getClass().getAnnotation(GenerateEffect.class).key().equals(key);
			}

			return false;
		}).findAny().orElse(null);
	}

	public WheelEffect getEffect(int index) {
		if (index < 0 || index >= effects.size())
			return null;

		return effects.get(index);
	}

}
