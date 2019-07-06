package me.paul.lads.wheel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

import org.bukkit.Material;
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
					if(ge.enabled()) {
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
		// new class/file for them
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				broadcastTitle(spinner.getName() + " is gey :3");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(ChatColor.GOLD
						+ "AYO! Time to go buy some Annoying Orange merch. Click the link if you need help: "); // TODO:
																												// insert
																												// link
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage("Congratulations!");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(
						"HEY! Me, the wheel, is FORCING you to go put on a Ninja vod for the rest of yo stream :3");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(
						"It's time for you to go on the Twitterz and tweet something super dumb. A hot take, unpopular opinion, any of that dumb shit will work :D.");
			}
		});
		String[] insuranceFraud = { "Ever heard of insurance fraud?",
				"You know it's actually pretty hard to get caught for insurance fraud?",
				"All you have to do is sign up for multiple insurance companies",
				"And then you claim to have been stolen from", "Listen, it's easy money. Easy profit",
				"You'll come around. I know you will",
				"Insurance fraud has existed since the beginning of insurance as a commercial enterprise",
				"So many people get away with it", "Insurance fraud",
				"Weigh the positives and negatives and get back to me.",
				"Have you come to a decision about... ahem... our business?" };
		effects.add(new WheelConversationEffect(Duration.hours(1), insuranceFraud));
		String[] stealing = { "Ever wanted to have something that reuiqres you to take it from someone else?",
				"You should try stealing some time", "You can take whatever you want and then it's yours!",
				"There's no strings attached", "Just take anything! You can have it! Nothing says you can't!",
				"You could take money...", "You could take material possessions...",
				"You could take whatever your heart desires!", "Try it! Just... steal something",
				"You know you want to", "Fuck you" };
		effects.add(new WheelConversationEffect(Duration.hours(1), stealing));
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(
						"HEY!!! Time to get off this game Minecraft and play something even SHITTIER on steam for a bit! >:D");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage("Go buy yourself a fortune cookie from Carson");
			}
		});

		String[] twitchPrime = { "Have you heard of Twitch Prime?",
				"Twitch Prime is a premium experience on Twitch that is included with Amazon Prime and Prime Video memberships",
				"Twitch Prime includes bonus games, and exclusive in-game content, a channel subscription every month at no additional cost to be used on any Partner or Affiliate channels, exclusive emotes, and chat badges.",
				"You can use your Amazon Prime account right now and subscribe to the channel you're watching!",
				"Support Streamers! Use Twitch Prime!" };
		effects.add(new WheelConversationEffect(Duration.minutes(2), twitchPrime));
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				sendTitle(spinner, "your mom");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.getWorld().dropItem(spinner.getLocation(), new ItemStack(Material.DIAMOND, 2));
				spinner.sendMessage("Go buy a burger or something");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				// TODO: teleport player to edge of one of the tallest buildings at spawn.
				spinner.sendMessage("Welcome to the tallest building at spawn :D");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage("Welcome to the Cuck Labyrinth");
				// TODO: teleport player to Cuck Labyrinth
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(
						"Let's have some fun... Go harass any streamer of your choosing for 30 minutes! No strings attached :3");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage("Hehehe... go trap another streamer right now, with sand preferably");
			}
		});

		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage("Go play Stal.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				sendTitle(spinner, "HEY CHAT...", "Can you guys count to 100? Prove it...", 10, 20 * 2, 10);
			}
		});
		
		effects.add(WheelImageEffect.create("C:\\Users\\Paul\\Desktop\\Pictures\\carson.jpg"));
		effects.add(WheelImageEffect.create("C:\\Users\\Paul\\Desktop\\Pictures\\ragek.jpg"));
		effects.add(WheelImageEffect.create("C:\\Users\\Paul\\Desktop\\Pictures\\mr-moseby.jpg"));
		effects.add(WheelImageEffect.create("C:\\Users\\Paul\\Desktop\\Pictures\\robertdowney.jpg"));
		effects.add(WheelImageEffect.create("C:\\Users\\Paul\\Desktop\\Pictures\\TrollFace.jpg"));
		effects.add(WheelImageEffect.create("C:\\Users\\Paul\\Desktop\\Pictures\\President_Barack_Obama.jpg"));
		effects.add(WheelImageEffect.create("C:\\Users\\Paul\\Desktop\\Pictures\\Dwayne_Johnson_2,_2013.jpg"));
		
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
			if(e.getClass().isAnnotationPresent(GenerateEffect.class)) {
				return e.getClass().getAnnotation(GenerateEffect.class).key().equals(key);
			}
			
			return false;
		}).findAny().orElse(null);
	}
	
	public WheelEffect getEffect(int index) {
		if(index < 0 || index >= effects.size())
			return null;
		
		return effects.get(index);
	}

}
