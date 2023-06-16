package me.paul.foliastuff.wheel.effects;

import com.google.common.collect.Lists;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.Duration;
import me.paul.foliastuff.util.Util;
import me.paul.foliastuff.util.WeightedRandomizer;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.util.scheduler.TaskBuilder;
import me.paul.foliastuff.wheel.GenerateEffect;
import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@GenerateEffect(key = "math_question", name = "Math Questions", description = "Answer correctly for a fat prize")
public class MathEffect extends WheelEffect implements Listener {

  public static final WeightedRandomizer<Pair<Integer, List<ItemStack>>> TABLE;

  static {
    TABLE = new WeightedRandomizer<>();

    List<ItemStack> blues = Lists.newArrayList();
    blues.add(new ItemStack(Material.DIRT));
    blues.add(new ItemStack(Material.SCAFFOLDING));
    blues.add(new ItemStack(Material.MUD));
    blues.add(new ItemStack(Material.STONE_BUTTON));

    List<ItemStack> purples = Lists.newArrayList();
    purples.add(new ItemStack(Material.COBBLESTONE));
    purples.add(new ItemStack(Material.GRAVEL));
    purples.add(new ItemStack(Material.NETHERRACK));

    List<ItemStack> pinks = Lists.newArrayList();
    pinks.add(new ItemStack(Material.GOLD_INGOT, 3));
    pinks.add(new ItemStack(Material.IRON_INGOT, 16));
    pinks.add(new ItemStack(Material.ENCHANTING_TABLE));

    List<ItemStack> reds = Lists.newArrayList();
    reds.add(new ItemStack(Material.DIAMOND, 5));
    reds.add(new ItemStack(Material.EMERALD, 5));
    reds.add(new ItemStack(Material.ANCIENT_DEBRIS, 1));

    List<ItemStack> gold = Lists.newArrayList();
    gold.add(new ItemStack(Material.NETHERITE_INGOT));
    gold.add(new ItemStack(Material.BEDROCK));
    gold.add(new ItemStack(Material.SPONGE));
    gold.add(new ItemStack(Material.DRAGON_HEAD));
    gold.add(new ItemStack(Material.SKELETON_SKULL));
    gold.add(new ItemStack(Material.ZOMBIE_HEAD));
    gold.add(new ItemStack(Material.CREEPER_HEAD));

    TABLE.add(Pair.of(0, blues), 79_920);
    TABLE.add(Pair.of(1, purples), 15_980);
    TABLE.add(Pair.of(2, pinks), 3_200);
    TABLE.add(Pair.of(3, reds), 640);
    TABLE.add(Pair.of(4, gold), 260);

//        int b = 0, p = 0, pi = 0, r = 0, g = 0;
//        for (int i = 0; i < 10000; i++) {
//            Pair<Integer, List<ItemStack>> chose = TABLE.select();
//            switch (chose.getLeft()) {
//                case 0:
//                    b++;
//                    break;
//                case 1:
//                    p++;
//                    break;
//                case 2:
//                    pi++;
//                    break;
//                case 3:
//                    r++;
//                    break;
//                case 4:
//                    g++;
//                    break;
//            }
//        }
//
//        System.out.println(String.format("%d out of 10k is: %f", b, ((double)b / 10_000)));
//        System.out.println(String.format("%d out of 10k is: %f", p, ((double)p / 10_000)));
//        System.out.println(String.format("%d out of 10k is: %f", pi, ((double)pi / 10_000)));
//        System.out.println(String.format("%d out of 10k is: %f", r, ((double)r / 10_000)));
//        System.out.println(String.format("%d out of 10k is: %f", g, ((double)g / 10_000)));
  }

  Character[] opCodes = {'+', '-', '/', '*'};

  @Override
  public void play(Player spinner, Wheel spun) {
    int x = Util.random(3, 12), y = Util.random(3, 12);
    double answer = 0.0d;

    char randomCode = Util.getRandomEntry(opCodes);

    switch (randomCode) {
      case '+': {
        answer = x + y;
        break;
      }
      case '-': {
        answer = x - y;
        break;
      }
      case '/': {
        x = x * y;
        answer = ((double) x / y);
        break;
      }
      case '*': {
        answer = x * y;
        break;
      }
    }

    correctAnswer = answer;
    lastSpinner = spinner.getUniqueId();

    spinner.sendMessage(Component.text(PREFIX).append(Component.text(" What is " + x + " " + randomCode + " " + y + "? You have 2 seconds to answer.")));
    spinner.sendTitle(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "What is: ", (x + " " + randomCode + " " + y + " = ?"), 10, 20 * 5, 10);
    for (int i = 0; i < 7; i++) {
      Sync.get(spinner).delay(i * 5).run(() -> spinner.playSound(spinner.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10f, 0f));
    }

    Bukkit.getPluginManager().registerEvents(this, FoliaStuff.getInstance());
    this.task = Sync.get(spinner).delay(Duration.seconds(5)).run(() -> {
      if (!correct) {
        spinner.sendMessage(Component.text(PREFIX).append(Component.text(" You suck. Enjoy the ride! :>")));
        spinner.teleportAsync(spinner.getLocation().clone().add(0, 255, 0));
      }

      stopChallenge();
    });
  }

  private Object task;
  private boolean correct = false;
  private double answerInput = 0.0d;
  private double correctAnswer = 0.0d;
  private UUID lastSpinner;

  @EventHandler
  public void onChat(AsyncChatEvent event) {
    if (event.getPlayer().getUniqueId().equals(lastSpinner)) {
      String str = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());

      try {
        answerInput = Double.parseDouble(str);
      } catch (NumberFormatException ignored) {
      }

      correct = verifyAnswer();
      if (correct) {
        event.getPlayer().sendMessage(Component.text(PREFIX).append(Component.text(" You win! Enjoy this beautiful gift.")));

        ItemStack chosen = Util.getRandomEntry(TABLE.select().getRight());
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
    correctAnswer = 0.0d;
    answerInput = 0.0d;
    correct = false;

    if(TaskBuilder.isFoliaSupported()) {
      ((ScheduledTask) task).cancel();
    } else {
      ((BukkitTask) task).cancel();
    }
  }

  private boolean verifyAnswer() {
    return answerInput == correctAnswer;
  }

}
