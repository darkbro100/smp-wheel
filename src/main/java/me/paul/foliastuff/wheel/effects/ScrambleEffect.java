package me.paul.foliastuff.wheel.effects;

import com.google.common.collect.Lists;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.Duration;
import me.paul.foliastuff.util.Util;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.util.scheduler.TaskBuilder;
import me.paul.foliastuff.wheel.GenerateEffect;
import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;


@GenerateEffect(key = "scramble_effect", name = "Word Scramble", description = "Answer correctly for a fat prize")
public class ScrambleEffect extends WheelEffect implements Listener {

  /**
   * Arbitrary words that will be scrambled
   */
  String[] words = {
    "Green", "Genie", "Apple", "Banana", "Gamer", "Dylan", "Cringe", "Tyler", "Squid", "Generic", "Genetic",
    "Love", "Golden", "Great", "Maple", "Pine", "Oak", "Birch", "Spruce", "Acacia", "Jungle", "Dark", "Light"
  };

  @Override
  public void play(Player spinner, Wheel spun) {
    String str = Util.getRandomEntry(words);
    String scrambled = scrambleWorld(str);

    correctAnswer = str;
    lastSpinner = spinner.getUniqueId();

    // Alert the spinner
    spinner.sendMessage(Component.text(PREFIX).append(Component.text(" What word is this? ").append(Component.text(scrambled)).append(Component.text(" You have 15 seconds."))));
    spinner.sendTitle(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Unscramble Word:", scrambled, 10, 20 * 5, 10);
    for (int i = 0; i < 7; i++) {
      Sync.get(spinner).delay(i * 5).run(() -> spinner.playSound(spinner.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10f, 0f));
    }

    // register stuff
    Bukkit.getPluginManager().registerEvents(this, FoliaStuff.getInstance());
    this.task = Sync.get(spinner).delay(Duration.seconds(15)).run(() -> {
      if (!correct) {
        spinner.sendMessage(Component.text(PREFIX).append(Component.text(" You suck. Good luck soldier... :>")));

        List<Block> blocks = getNearbySurfaceBlocks(spinner.getLocation(), 16, 4);
        for (int i = 0; i < 6; i++) {
          Block r = Util.getRandomEntry(blocks);
          r.getWorld().spawn(r.getLocation().clone().add(0.5, 0, 0.5), Zombie.class, z -> {
            z.setShouldBurnInDay(false);
            z.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10.0d);
            z.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0d);
            z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.45d);
            z.setTarget(spinner);
          });
        }
      }

      stopChallenge();
    });
  }

  private Object task;
  private boolean correct = false;
  private String answerInput = "";
  private String correctAnswer = "";
  private UUID lastSpinner;

  @EventHandler
  public void onChat(AsyncChatEvent event) {
    if (event.getPlayer().getUniqueId().equals(lastSpinner)) {

      answerInput = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());
      System.out.println("checking " + answerInput);
      correct = verifyAnswer();
      System.out.println("answer: " + correctAnswer);

      if (correct) {

        event.getPlayer().sendMessage(Component.text(PREFIX).append(Component.text(" You win! Enjoy this beautiful gift.")));

        ItemStack chosen = Util.getRandomEntry(MathEffect.TABLE.select().getRight());
        Map<Integer, ItemStack> failSafe = event.getPlayer().getInventory().addItem(chosen);
        if (!failSafe.isEmpty())
          failSafe.values().forEach(it -> event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), it));

        stopChallenge();
        event.setCancelled(true);
      }
    }
  }

  private void stopChallenge() {
    HandlerList.unregisterAll(this);

    lastSpinner = null;
    correctAnswer = "";
    answerInput = "";
    correct = false;

    if(TaskBuilder.isFoliaSupported()) {
      ((ScheduledTask) task).cancel();
    } else {
      ((BukkitTask) task).cancel();
    }
  }

  private boolean verifyAnswer() {
    return answerInput.equalsIgnoreCase(correctAnswer);
  }

  private static String scrambleWorld(String word) {
    ArrayList<Character> list = new ArrayList<>();
    for (char c : word.toCharArray()) {
      list.add(c);
    }
    Collections.shuffle(list);
    return new String(ArrayUtils.toPrimitive(list.toArray(new Character[list.size()])));
  }

  /**
   * Get a list of nearby surface locations with option to exclude radius
   *
   * @param center        Center of radius
   * @param radius        Maximum radius of blocks
   * @param excludeRadius Radius to exclude from returning list
   * @return List of surface blocks in the given radius
   */
  public static List<Block> getNearbySurfaceBlocks(Location center, int radius, int excludeRadius) {
    List<Block> locs = getSphereBlocks(center, radius, radius, false, true, 0).stream().filter(b -> b.getType() == Material.AIR && b.getRelative(BlockFace.DOWN).getType().isSolid() && b.getY() > center.getY() - 5).collect(Collectors.toList());
    locs.removeAll(getSphereBlocks(center, excludeRadius, excludeRadius, false, true, 0));

    return locs;
  }

  public static List<Block> getSphereBlocks(Location loc, int r, int h, boolean hollow, boolean sphere, int plus_y) {
    List<Block> circleblocks = Lists.newArrayList();
    int cx = loc.getBlockX();
    int cy = loc.getBlockY();
    int cz = loc.getBlockZ();
    for (int x = cx - r; x <= cx + r; x++) {
      for (int z = cz - r; z <= cz + r; z++) {
        for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
          double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
          if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
            circleblocks.add(loc.getWorld().getBlockAt(x, y + plus_y, z));
          }
        }
      }
    }
    return circleblocks;
  }

}
