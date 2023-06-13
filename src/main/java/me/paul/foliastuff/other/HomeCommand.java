package me.paul.foliastuff.other;

import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.util.scheduler.TaskHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class HomeCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (sender instanceof final Player player) {
      assert FoliaStuff.KEY != null;
      String homeStr = player.getPersistentDataContainer().get(FoliaStuff.KEY, PersistentDataType.STRING);
      if (homeStr == null || homeStr.isEmpty()) {
        TextComponent text = Component.text().content("Your home is not set!").color(TextColor.color(255, 0, 0)).build();
        player.sendMessage(text);
        return true;
      } else {
        final Location loc = LocUtil.locFromString(homeStr);
        final HomeTimer timer = new HomeTimer(player, loc);
        TaskHolder homeHolder = new TaskHolder();
        Sync.get(player).interval(1).holder(homeHolder).run(() -> timer.run(homeHolder));
        player.sendMessage(Component.text("You will teleport in 3 seconds").color(TextColor.color(120, 120, 120)));
      }
    }
    return false;
  }
}
