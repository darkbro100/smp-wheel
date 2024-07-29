package me.paul.foliastuff.wheel;

import me.paul.foliastuff.util.scheduler.Sync;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class PassiveEffectGen {

  private static void launchFirework(Location loc) {
    Firework fw = loc.getWorld().spawn(loc, Firework.class);
    FireworkMeta fwm = fw.getFireworkMeta();
    FireworkEffect effect = FireworkEffect.builder().withColor(Color.BLUE).withFade(Color.AQUA).with(FireworkEffect.Type.STAR).withTrail().build();
    fwm.addEffect(effect);
    fwm.setPower(1);
    fw.setFireworkMeta(fwm);
  }

  public static void applyPassiveEffect(Player player) {
    List<Consumer<Player>> effects = new ArrayList<>();

    //add passive effects here, can be anything.

    effects.add(p -> {
      p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 3));
      p.sendMessage(Component.text("You feel faster!").color(TextColor.color(0, 255, 225)));
    });

    effects.add(p -> {
      p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 12000, 0));
      p.sendMessage(Component.text("You're in a negotiating mood!").color(TextColor.color(0, 233, 4)));
    });

    effects.add(p -> {
      p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6000, 4));
      p.sendMessage(Component.text("Feeling regenerative!").color(TextColor.color(8, 83, 255)));
    });

    effects.add(p -> {
      p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 12000, 3));
      p.sendMessage(Component.text("Break block and doesn't afraid of anything").color(TextColor.color(117, 8, 255)));
    });

    effects.add(p -> {
      p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6000, 3));
      p.sendMessage(Component.text("bros been lifting fr").color(TextColor.color(255, 154, 8)));
    });

    effects.add(p -> {
      // DIAMOND PARTY WOOOO
      Location loc = p.getLocation();
      loc.setY(loc.getY() + 1.5);
      ItemStack diamonds = new ItemStack(Material.DIAMOND);
      diamonds.setAmount(1);

      for (int i = 1; i < 11; i++) {
        Sync.get().delay(i * 20).run(() -> {
          launchFirework(loc);
          p.getWorld().dropItemNaturally(loc, diamonds);
        });
      }
    });

    effects.add(p -> {
      // EMERALD PARTY WOOOO
      Location loc = p.getLocation();
      loc.setY(loc.getY() + 1.5);
      ItemStack emerald = new ItemStack(Material.EMERALD);
      emerald.setAmount(2);

      for (int i = 1; i < 20; i++) {
        Sync.get().delay(i * 20).run(() -> {
          launchFirework(loc);
          p.getWorld().dropItemNaturally(loc, emerald);
        });
      }
    });

    Random random = new Random();
    Consumer<Player> selectedEffect = effects.get(random.nextInt(effects.size()));
    selectedEffect.accept(player);
  }
}
