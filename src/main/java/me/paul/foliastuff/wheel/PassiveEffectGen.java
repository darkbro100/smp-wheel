package me.paul.foliastuff.wheel;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class PassiveEffectGen {

  public static void applyPassiveEffect(Player player) {
    List<Consumer<Player>> effects = new ArrayList<>();

    //add passive effects here, can be anything.

    effects.add(p -> {
      //literally anything i fucking want functional interface ftw
    });

    Random random = new Random();
    Consumer<Player> selectedEffect = effects.get(random.nextInt(effects.size()));
    selectedEffect.accept(player);
  }
}
