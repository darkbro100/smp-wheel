package me.paul.foliastuff.wheel;

import lombok.Getter;
import me.paul.foliastuff.other.FoliaStuff;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.reflections.Reflections;

import java.io.File;
import java.util.*;

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
    Reflections ref = new Reflections("me.paul.foliastuff.wheel.effects");
    Set<Class<?>> clazzes = ref.getTypesAnnotatedWith(GenerateEffect.class);

    FoliaStuff.getInstance().getLogger().info("Loaded " + clazzes.size() + " effect classes.");

    // Initialize all the classes that have the @GenerateEffect annotation. Will automatically initialize them and add them to our effects list.
    clazzes.forEach(clazz -> {
      if (WheelEffect.class.isAssignableFrom(clazz)) {
        try {
          GenerateEffect ge = clazz.getAnnotation(GenerateEffect.class);
          if (ge.enabled()) {
            WheelEffect we = (WheelEffect) clazz.newInstance();
            FoliaStuff.getInstance().getLogger().info("Found new effect: " + ge.name());
            effects.add(we);
          } else {
            FoliaStuff.getInstance().getLogger().info("Skipped loading: " + ge.name());
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
          p.getScheduler().run(FoliaStuff.getInstance(), task -> {
            p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 40, 7));
            p.sendTitle(ChatColor.BLUE + "Up, up and away!!", "It's not flying, it's falling, with style.", 10, 20 * 4, 10);
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
          }, null);
        }
        Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " bought everyone a hot balloon ride, except there's no balloon. And also no parachutes.");
      }
    });

    // REPLACE DIRECTORY WITH NEW IMAGES
    File imagesDir = new File(FoliaStuff.getInstance().getDataFolder() + File.separator + "wheel_images");
    if (imagesDir.exists() && imagesDir.isDirectory()) {
      for (File f : Objects.requireNonNull(imagesDir.listFiles()))
        effects.add(WheelImageEffect.create(f.getAbsolutePath()));
    }

    FoliaStuff.getInstance().getLogger().info("Loaded " + effects.size() + " effects...");
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
