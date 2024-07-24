package me.paul.foliastuff.wheel;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.Duration;
import me.paul.foliastuff.util.scheduler.Sync;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

    // levitation effect
    effects.add(new WheelEffect() {
      public void play(Player spinner, Wheel spun) {
        for (Player p : Bukkit.getOnlinePlayers()) {
          Sync.get(p).run(() -> {
            p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 40, 7));
            p.sendTitle(ChatColor.BLUE + "Up, up and away!!", "It's not flying, it's falling, with style.", 10, 20 * 4, 10);
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
          });
        }
        Bukkit.broadcast(Component.text(spinner.getName() + " bought everyone a hot balloon ride, except there's no balloon. And also no parachutes.").color(TextColor.color(0, 255, 120)));
      }
    });

    //standed at sea
    effects.add(new WheelEffect() {
      public void play(Player spinner, Wheel spun) {
       Location getfucked = new Location(spinner.getWorld(), 2294, 129, 2073);
       spinner.teleport(getfucked);
       spinner.sendMessage(Component.text("Have a nice swim!").color(TextColor.color(0, 128, 255)));
       Bukkit.broadcast(Component.text(spinner.getName() + " has been lost at sea...").color(TextColor.color(0, 128, 255)));
      }
    });

    //block of doom
    effects.add(new WheelEffect() {
      public void play(Player spinner, Wheel spun) {
       Location getfucked = new Location(spinner.getWorld(), -25, 312, 380);
        for (int i = 0; i < 5; i++) {
          Sync.get(spinner).delay(i * 5).run(() -> spinner.playSound(spinner.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10f, 0f));
        }
       spinner.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DO NOT FUCKING MOVE", ChatColor.WHITE + "You are about to teleport...", 10, 20*3, 10);
        Sync.get(spinner).delay(Duration.seconds(5)).run(() -> spinner.teleport(getfucked));
      }
    });

    // slow mining effect
    effects.add(new WheelEffect() {
      @Override
      public void play(Player spinner, Wheel spun) {
        for (Player p : Bukkit.getOnlinePlayers()) {
          Sync.get(p).run(() -> {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Duration.minutes(2).ticks(), 3, false, false, false));
            p.sendMessage(Component.text("You feel slow af...").color(TextColor.color(120, 55, 2)));
          });
        }
      }

    });

    // image effects.
    File imagesDir = new File(FoliaStuff.getInstance().getDataFolder() + File.separator + "wheel_images");
    List<String> imagePaths = Lists.newArrayList();

    if (imagesDir.exists() && imagesDir.isDirectory()) {
      for (File f : Objects.requireNonNull(imagesDir.listFiles()))
        imagePaths.add(f.getAbsolutePath());
    }

    WheelImageEffect imageEffect = WheelImageEffect.create(imagePaths);
    if (imageEffect != null)
      effects.add(imageEffect);

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
