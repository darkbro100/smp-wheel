package me.paul.lads.wheel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import lombok.Getter;
import me.paul.lads.util.Duration;
import net.md_5.bungee.api.ChatColor;

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
		World world = Bukkit.getWorld("SMPLive2");
		Location sky = new Location(world, -107, 256, -34, -89, 50);
		Location cuck = new Location(world, -72, 68, -32, -179, 4);
		String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[The Wacky" + ChatColor.RED + ChatColor.ITALIC
				+ " WHEEL" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				broadcastTitle(spinner.getName() + " is gay");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix
						+ " Don't you like the Annoying Orange? You should buy some merch dude. https://amzn.to/30pxb3D");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Congratulations!");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " You like Ninja dude? Go put on a VOD for the rest of the Stream. Now.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Go on Twitter and post something really dumb. Now.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix
						+ " OPEN ENDED HIT. Kill anyone you desire on the server. Cannot be held against you in the court of law.");
			}
		});
		String[] insuranceFraud = { prefix + " Ever heard of insurance fraud?",
				prefix + " You know it's actually pretty hard to get caught for insurance fraud?",
				prefix + " All you have to do is sign up for multiple insurance companies",
				prefix + " And then you claim to have been stolen from",
				prefix + " Listen, it's easy money. Easy profit", 
				prefix + " You'll come around. I know you will",
				prefix + " Insurance fraud has existed since the beginning of insurance as a commercial enterprise",
				prefix + " So many people get away with it", 
				prefix + " Insurance fraud",
				prefix + " Weigh the positives and negatives and get back to me.",
				prefix + " Have you come to a decision about... ahem... our business?", 
				prefix + " Fuck you" };
		effects.add(new WheelConversationEffect(Duration.hours(1), insuranceFraud));

		String[] stealing = { prefix + " Ever wanted to have something that requires you to take it from someone else?",
				prefix + " You should try stealing some time",
				prefix + " You can take whatever you want and then it's yours!",
				prefix + " There's no strings attached",
				prefix + " Just take anything! You can have it! Nothing says you can't!",
				prefix + " You could take money...", 
				prefix + " You could take material possessions...",
				prefix + " You could take whatever your heart desires!", 
				prefix + " Try it! Just... steal something",
				prefix + " You know you want to", 
				prefix + " Fuck you" };
		effects.add(new WheelConversationEffect(Duration.hours(1), stealing));

		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Go play a really shitty Steam game on Stream. Now.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Go buy yourself a fortune cookie from Carson");
			}
		});

		String[] twitchPrime = { prefix + " Have you heard of Twitch Prime?", 
				prefix + " Twitch Prime is a premium experience on Twitch that is included with Amazon Prime and Prime Video memberships",
				prefix + " Twitch Prime includes bonus games, and exclusive in-game content, a channel subscription every month at no additional cost to be used on any Partner or Affiliate channels, exclusive emotes, and chat badges.",
				prefix + " You can use your Amazon Prime account right now and subscribe to the channel you're watching!",
				prefix + " Support Streamers! Use Twitch Prime!" };
		effects.add(new WheelConversationEffect(Duration.minutes(2), twitchPrime));
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				sendTitle(spinner, "Your Mom.", "");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Go buy a burger or something");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Welcome to the tallest building at spawn!");
				spinner.teleport(sky);
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Welcome to the Cuck Labyrinth");
				spinner.teleport(cuck);
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Go harass somebody for 30 minutes. NOW.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Go trap another streamer right now. NOW.");
			}
		});

		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.getWorld().dropItem(spinner.getLocation(), new ItemStack(Material.MUSIC_DISC_STAL, 1));
				spinner.sendMessage(prefix + " Go play Stal.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				sendTitle(spinner, "HEY CHAT...", "Can you guys count to 100? Prove it...", 10, 20 * 2, 10);
			}
		});

		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/carson.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/ragecomic.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/moseby.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/rdj.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/troll.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/obama.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/therock.png"));

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
