package me.paul.foliastuff.wheel.effects;

import me.paul.foliastuff.wheel.GenerateEffect;
import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;
import org.bukkit.entity.Player;

@GenerateEffect(description = "Makes the wheel constantly slow down and speed up the speeding. trolololo", key = "effect_keepspinning", name = "Keep Spinning")
public class KeepGoingEffect extends WheelEffect {

  /**
   * Technicall have to do nothing here as it's already implemented in the {@link Wheel} class
   */
  @Override
  public void play(Player spinner, Wheel spun) {
    spinner.sendMessage("This is unused content that the player should NEVER FUCKING SEE IN A MILLION YEARS HOLY SHIT WHY ARE YOU READING THIS");
  }

}
