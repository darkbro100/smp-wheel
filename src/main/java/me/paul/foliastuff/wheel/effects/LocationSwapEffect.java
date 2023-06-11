package me.paul.foliastuff.wheel.effects;

import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.wheel.GenerateEffect;
import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@GenerateEffect(description = "Swaps everyone's location", key = "wheel_swap", name = "Location Swap")
public class LocationSwapEffect extends WheelEffect {

  @Override
  public void play(Player spinner, Wheel spun) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      p.sendTitle(ChatColor.BLUE + ChatColor.BOLD.toString() + "Random Teleport",
        "Trading places with another player!", 10, 20 * 5, 10);
      p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
    }

    Sync.get(spinner).delay(40).run(this::Teleport);
  }

  public void Teleport() {
    List<Player> players = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
    Collections.shuffle(players);
    Map<Player, Location> playerMap = new LinkedHashMap<>();
    players.forEach(p -> playerMap.put(p, p.getLocation()));
    List<Map.Entry<Player, Location>> playerEntries = new ArrayList<>(playerMap.entrySet());

    for (int i = 0; i < playerEntries.size(); i++) {
      Map.Entry<Player, Location> entry = playerEntries.get(i);
      int nextIndex = (i == playerEntries.size() - 1) ? 0 : (i + 1);
      Map.Entry<Player, Location> swapTo = playerEntries.get(nextIndex);
      entry.getKey().teleportAsync(swapTo.getValue());
      entry.getKey().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
        ChatColor.GREEN + ChatColor.BOLD.toString() + "You swapped with: " + ChatColor.GOLD + swapTo.getKey().getName()));
    }

  }

}
