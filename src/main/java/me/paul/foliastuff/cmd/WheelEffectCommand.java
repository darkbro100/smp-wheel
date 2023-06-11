package me.paul.foliastuff.cmd;

import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;
import me.paul.foliastuff.wheel.WheelEffectManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WheelEffectCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) return true;
    if (!sender.isOp()) return true;

    if (args.length == 0) {
      sender.sendMessage("/we <id>");
      return true;
    }

    Integer id = Integer.parseInt(args[0]);
    WheelEffect we = WheelEffectManager.getInstance().getEffect(id);

    if (we == null) {
      sender.sendMessage("Can't find wheel effect from id: " + id);
      return true;
    }

    we.play((Player) sender, Wheel.get(1));
    return true;
  }


}
