package me.paul.lads.wheel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import org.reflections.Reflections;

import lombok.Getter;
import me.paul.lads.util.Duration;
import net.md_5.bungee.api.ChatColor;

public class WheelEffectManager {

	private List<WheelEffect> effects;
	private SplittableRandom random;
	private int troll;

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
		Location sky = new Location(world, -109, 256, -35, -89, 50);
		Location cuck = new Location(world, -72, 68, -32, -179, 4);
		Location ocean = new Location(world, 9979, 61, 4508);
		Location corner = new Location(world, -19992, 70, 19993);
		String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[The Wacky" + ChatColor.RED + ChatColor.ITALIC
				+ " WHEEL" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				broadcastTitle(spinner.getName() + " is gay");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Congratulations!");
				Bukkit.broadcastMessage(ChatColor.GREEN + "Congratulations, " + spinner.getName());
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.teleport(ocean);
				spinner.sendMessage(prefix + " hope you're a good swimmer!!");
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " is lost at sea");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Listen to this really good song please. https://youtu.be/astISOttCQ0");
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " is listening to some music");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix
						+ " OPEN ENDED HIT. Kill anyone you desire on the server. Cannot be held against you in the court of law.");
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " is armed and dangerous.");
			}
		});
		String[] carsonsJoke = { prefix + " You want to hear a funny joke?",
				prefix + " So I'm sitting in the back of my car right?",
				prefix + " Then I look up at this sign, you know", prefix + " The sign says 'Wood Fired Pizza'",
				prefix + " So then I go, “Wood fired Pizza?, how’s Pizza gonna get a job now??”",
				prefix + " haha You get it right? It’s really funny",
				prefix + " Get it because it’s like someone named “Wood” fired somebody named “Pizza”",
				prefix + " But it actually just describes how the pizza was cooked", prefix + " Fuck you" };
		effects.add(new WheelConversationEffect(Duration.minutes(15), carsonsJoke));

		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.teleport(corner);
				spinner.sendMessage(prefix + " FIND A WAY BACK.");
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " has to find their way back.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " You must challenge a fellow streamer to either of the following:");
				spinner.sendMessage(ChatColor.AQUA + "Checkers");
				spinner.sendMessage(ChatColor.AQUA + "Connect Four");
				spinner.sendMessage(ChatColor.AQUA + "Battleship");
				spinner.sendMessage(ChatColor.AQUA + "Chess");
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " has a challenge to make.");
			}
		});

		String[] twitchPrime = { prefix + " Have you heard of Twitch Prime?", prefix
				+ " Twitch Prime is a premium experience on Twitch that is included with Amazon Prime and Prime Video memberships",
				prefix + " Twitch Prime includes bonus games, and exclusive in-game content, a channel subscription every month at no additional cost to be used on any Partner or Affiliate channels, exclusive emotes, and chat badges.",
				prefix + " You can use your Amazon Prime account right now and subscribe to the channel you're watching!",
				prefix + " Support Streamers! Use Twitch Prime!" };
		effects.add(new WheelConversationEffect(Duration.minutes(2), twitchPrime));

		String[] herobrine = { ChatColor.YELLOW + "herobrine joined the game",
				"<herobrine> sup fuckers",
				"<herobrine> how funny would it be if i destroyed spawn",
				"<herobrine> or how about this, your nether link? haha imagine",
				ChatColor.YELLOW + "herobrine left the game",
				ChatColor.YELLOW + "Detected Minecraft update -- (rev1.14.5, - Removed Herobrine)"
	};
		effects.add(new WheelPublicEffect(Duration.minutes(3), herobrine));
		
		String[] notch = { ChatColor.YELLOW + "Notch joined the game",
				"<Notch> wow this server fucking sucks lol",
				"<Notch> you guys actually stream this?",
				"<Notch> this is most disappointed i’ve ever been in a minecraft community",
				"<Notch> lemme just say I get so many bitches and hoes, it’s unreal",
				"<Notch> alright later losers",
				ChatColor.YELLOW + "Notch left the game",
	};
		effects.add(new WheelPublicEffect(Duration.minutes(5), notch));

		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				sendTitle(spinner, "Your Mom.", "");
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just got owned.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Welcome to the tallest building at spawn!");
				spinner.teleport(sky);
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " is getting some fresh air.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Welcome to the Cuck Labyrinth");
				spinner.teleport(cuck);
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " is dazed & confused.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Listen to this hot soundtrack for the rest of the Stream.");
				spinner.sendMessage(
						ChatColor.GREEN + "https://www.youtube.com/playlist?list=PLggwlnlDbhNxbrlmutHDn8v3CWu2bKB8J");
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " received a DEMAND from the Wheel.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " BURN. BURN. BURN. BURN");
				Location strike = spinner.getLocation();
				spinner.getWorld().strikeLightning(strike);
				spinner.setFireTicks(400);
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " was burned by the Wheel.");
			}
		});

		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.getWorld().dropItem(spinner.getLocation(), new ItemStack(Material.MUSIC_DISC_STAL, 1));
				spinner.sendMessage(prefix + " Go play Stal.");
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " was given Stal.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				sendTitle(spinner, "HEY CHAT...", "Can you guys count to 100? Prove it...", 10, 20 * 2, 10);
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just had their chat ruined.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + "Dude have you ever seen Charlie & the Chocolate Factory? You remember that one scene?");
				spinner.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1200, 7));
				spinner.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 1200, 15));
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + "drank some Fizzy Lifting Soda");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + "Dirty.");
				spinner.getInventory().addItem(new ItemStack(Material.DIRT, 2304));
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + "is dirty.");
			}
		});

		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/carson.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/ragecomic.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/moseby.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/rdj.png"));
		this.troll = effects.size();
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/troll.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/obama.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/troll.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/troll.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/troll.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/troll.png"));
		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/troll.png"));

		// REPLACE DIRECTORY WITH NEW IMAGES

		System.out.println("Loaded " + effects.size() + " effects...");
	}

	@Getter
	private static final WheelEffectManager instance = new WheelEffectManager();

	public WheelEffect getRandomEffect() {
		int index = random.nextInt(effects.size());
		return effects.get(index);
	}

	public WheelEffect getTrollEffect() {
		return getEffect(troll);
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
