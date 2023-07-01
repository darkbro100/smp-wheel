package me.paul.foliastuff.cmd;

import me.paul.foliastuff.util.ItemBuilder;
import me.paul.foliastuff.util.scheduler.Sync;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player)) {
      return true;
    }

    Player player = (Player) commandSender;
    Sync.get(player).run(() -> {
      ItemStack it = ItemBuilder.of(Material.PANDA_SPAWN_EGG).name(Component.text("THE GOAT").color(TextColor.color(0xFFD700))).build();
      player.getInventory().addItem(it);
    });

    return true;
  }
}
