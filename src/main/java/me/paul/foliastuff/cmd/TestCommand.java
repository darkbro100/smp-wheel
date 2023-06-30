package me.paul.foliastuff.cmd;

import me.paul.foliastuff.util.NMS;
import me.paul.foliastuff.util.entity.CustomPanda;
import me.paul.foliastuff.util.scheduler.Sync;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class TestCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if(!(commandSender instanceof Player)) {
      return true;
    }

    Player player = (Player) commandSender;
    Sync.get(player).run(() -> {
      try {
        CustomPanda cp = (CustomPanda) NMS.createEntity(CustomPanda.class, player.getLocation());
        cp.setOwner(player.getUniqueId());

        player.sendMessage(Component.text("spawned panda"));
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e);
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    });

    return true;
  }
}
