package me.paul.lads.wheel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.reflections.Reflections;

import lombok.Getter;
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
		Location ocean = new Location(world, -14975, 61, -4102); // irrelevant
		Location esports = new Location(world, -18, 95, 26); // comet
		Location gayboat = new Location(world, -27, 66, -144); // comet
		Location courthouse = new Location(world, 103, 87, 19); //tnt explode
		Location theatre = new Location(world, 30, 70, -111); //tnt explode
		Location dicknballs = new Location(world, -295, 186, 6); // comet
		Location car = new Location(world, 426, 83, -156); //tnt explode
		Location tower = new Location(world, 64, 98, -156); // comet
		Location ttt = new Location(world, -67, 91, 261); //tnt explode
		Location coin = new Location(world, -48, 77, -22); //tnt explode
		
		String prefix = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[The Wacky" + ChatColor.RED + ChatColor.ITALIC
				+ " WHEEL" + ChatColor.GRAY + ChatColor.ITALIC + "] -> me:";
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just blew up the Courthouse. What an asshole.");	
				spinner.getWorld().spawn(courthouse, TNTPrimed.class);
				spinner.getWorld().spawn(courthouse, TNTPrimed.class);
				}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just blew up Kara's Car in spawn, in cold blood. Dickhead.");	
				spinner.getWorld().spawn(car, TNTPrimed.class);
				spinner.getWorld().spawn(car, TNTPrimed.class);
				}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just blew up the theatre..I guess we can't do events anymore.. jerkoff..");	
				spinner.getWorld().spawn(theatre, TNTPrimed.class);
				spinner.getWorld().spawn(theatre, TNTPrimed.class);
				spinner.getWorld().spawn(theatre, TNTPrimed.class);
				}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just blew up the Schlatt Coin.. talk about jealousy.");	
				spinner.getWorld().spawn(coin, TNTPrimed.class);
				spinner.getWorld().spawn(coin, TNTPrimed.class);
				}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				Bukkit.broadcastMessage(ChatColor.GREEN + "Tic-Tac-Toe? I guess tic-tac-No*.. thanks.. " + spinner.getName());	
				spinner.getWorld().spawn(ttt, TNTPrimed.class);
				spinner.getWorld().spawn(ttt, TNTPrimed.class);
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
				spinner.sendMessage(prefix + " You've set the world ablaze. YOU did this. YOU.");
				Location strike = spinner.getLocation();
				spinner.getWorld().strikeLightning(strike);
				spinner.setFireTicks(400);
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just let the world catch on fire.");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doFireTick true");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Surprise fucker.");
				Location loc = spinner.getLocation();
				spinner.getWorld().spawn(loc, TNTPrimed.class);
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " got a surprise.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Surprise fucker.");
				Location loc = spinner.getLocation();
				for(int i = 0; i < 3; i++) {
					EnderDragon dragon = spinner.getWorld().spawn(loc, EnderDragon.class);
					dragon.setPhase(Phase.CHARGE_PLAYER);
				}
				spinner.getWorld().strikeLightning(loc); 
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " just summoned some fucking dragons");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " You think you can just fucking spin me whenever? EGO check.");
				Bukkit.getOnlinePlayers().forEach(player -> {
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1200, 4));
					player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 1200, 2));
				});
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " is just showing you all how high your EGO is.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.getInventory().clear();
				spinner.sendMessage(prefix + " Your inventory is now GONE. Reduced to ATOMS.");
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + "'s items went missing.");
			}
		});
		effects.add(new WheelEffect() {
			public void play(Player spinner, Wheel spun) {
				spinner.sendMessage(prefix + " Up you go!");
				Location teleport = spinner.getLocation();
				teleport.setY(teleport.getY() + 100);
				spinner.teleport(teleport);
				spinner.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1200, 4));
				Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " was sent into the sky");
			}
		});
		
		String[] theend = {
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/BetterChairs ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/bStats ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/PluginMetrics ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/CoreProtect ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/dynmap ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/PlugMan ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/ImageMaps ...",
			ChatColor.ITALIC + "Err" + ChatColor.MAGIC + "or re" + ChatColor.ITALIC + "moving" + ChatColor.MAGIC + "/home/car" + ChatColor.ITALIC + "son/smplive/plugins/SMPWheel - Access Denied ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/NoPhantoms ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/OldCombatMechanics ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/PlayerHeads ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/Updater ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/ViaVersion ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/web ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/wheelimage ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/WorldEdit ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/plugins/WorldGuard ...",
			ChatColor.ITALIC + "Successfully removed all JARs from /home/carson/smplive/plugins ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/cache ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/crash-reports ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/logs ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/timings ...",
			ChatColor.ITALIC + "Successfully removed contents of /home/carson/smplive/SMPLive2/advancements ...",
			ChatColor.ITALIC + "Successfully removed contents of /home/carson/smplive/SMPLive2/data ...",
			ChatColor.ITALIC + "Successfully removed contents of /home/carson/smplive/SMPLive2/datapacks ...",
			ChatColor.ITALIC + "Successfully removed contents of /home/carson/smplive/SMPLive2/DIM1 ...",
			ChatColor.ITALIC + "Successfully removed contents of /home/carson/smplive/SMPLive2/DIM-1 ...",
			ChatColor.ITALIC + "Successfully removed contents of /home/carson/smplive/SMPLive2/playerdata ...",
			ChatColor.ITALIC + "Successfully removed contents of /home/carson/smplive/SMPLive2/poi ...",
			ChatColor.ITALIC + "Successfully removed contents of /home/carson/smplive/SMPLive2/region ...",
			ChatColor.ITALIC + "Successfully removed contents of /home/carson/smplive/SMPLive2/stats ...",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/SMPLive2 ... Map Deleted.",
			ChatColor.ITALIC + "Successfully removed /home/carson/smplive/SMPLive2_nether ...",
			ChatColor.ITALIC + "Successfully removed contents of /home/carson/smplive/SMPLive2_the_end ...",
			ChatColor.ITALIC + "Successfully removed directory /home/carson/smplive ...",
			ChatColor.ITALIC + "Uploaded /home/carson/backup/SMPLive2.zip to https://smplive.net/media/SMPLive2.zip ...",
			ChatColor.ITALIC + "Uploaded /home/carson/backup/SMPLive2_nether.zip to https://smplive.net/media/SMPLive2_nether.zip ...",
			ChatColor.ITALIC + "Uploaded /home/carson/backup/SMPLive2_the_end.zip to https://smplive.net/media/SMPLive2_the_end.zip ...",
			ChatColor.ITALIC + "Don't be sad that it's over.. be happy it happened...",
		    ChatColor.ITALIC + "Server shutting down..."};
//		effects.add(new WheelPublicEffect(Duration.minutes(1), theend));


//		effects.add(WheelImageEffect.create("/home/carson/smplive/plugins/wheelimage/troll.png"));

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
