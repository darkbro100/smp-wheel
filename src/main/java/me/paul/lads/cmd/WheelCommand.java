package me.paul.lads.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.paul.lads.wheel.Wheel;

public class WheelCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {

		if (args.length >= 2 && args[0].equalsIgnoreCase("setbutton") && sender.isOp()) {
			int id = Integer.parseInt(args[1]);
			Wheel w = Wheel.get(id);

			Player p = (Player) sender;
			Block b = p.getTargetBlockExact(50);

			if (b == null) {
				p.sendMessage(ChatColor.RED + "Button not found!");
				return true;
			}

			if (!b.getType().name().contains("BUTTON")) {
				p.sendMessage(ChatColor.RED + "You are not looking at a button!");
				return true;
			}

			w.setButtonBlock(b.getLocation());
			p.sendMessage(ChatColor.GREEN + "Updated button for wheel: " + id);
			return true;
		} else if (args.length >= 2 && args[0].equalsIgnoreCase("start") && sender.isOp()) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "/wheel start <id>");
				return true;
			}

			int id = Integer.parseInt(args[1]);
			Wheel w = Wheel.get(id);

			if (w == null) {
				sender.sendMessage(ChatColor.RED + "Wheel not found");
				return true;
			}

			boolean force = false;
			if (args.length > 2) {
				force = args[2].equalsIgnoreCase("-f");
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage("nu");
				return true;
			}

			Player opener = (Player) sender;

			if (!w.spin(opener, force)) {
				sender.sendMessage(ChatColor.RED + "Wheel could not be spun!");
				return true;
			} else {
				sender.sendMessage(ChatColor.GREEN + "Spun the wheel!");
			}
		} else if (args.length >= 3 && sender.isOp()) {
			try {
				int radius = Math.abs(Integer.parseInt(args[0]));
				final int offsetInc = Integer.parseInt(args[1]);
				int frequency = Integer.parseInt(args[2]);

				if (!(sender instanceof Player)) {
					return true;
				}

				final Location center = ((Player) sender).getLocation();
				Wheel wheel = new Wheel(center, radius, 6, offsetInc, frequency, Material.RED_CONCRETE, Material.ORANGE_CONCRETE, Material.YELLOW_CONCRETE, Material.GREEN_CONCRETE, Material.BLUE_CONCRETE, Material.MAGENTA_CONCRETE, Material.PURPLE_CONCRETE, Material.PINK_CONCRETE);
				wheel.draw();
				sender.sendMessage("made wheel with id: " + wheel.getId());

			} catch (Exception e) {
				sender.sendMessage("Invalid number input for radius!");
				return true;
			}
		} else {
			if (sender.isOp()) {
				sender.sendMessage("/wheel <radius> <offset inc> <freq>");
				sender.sendMessage("/wheel start <id> [-f]");
				sender.sendMessage("/wheel setbutton <id>");
			}

			if(Wheel.getWheels().isEmpty()) {
				sender.sendMessage(ChatColor.RED + "There are currently no wheels setup");
				return true;
			}

			Wheel w = Wheel.get(1);
			if (w.isLocked()) {
				sender.sendMessage(ChatColor.RED + "Wheel is currently on cooldown. Time remaining: " + ChatColor.YELLOW
						+ w.getLastSpin().getRemainingTime());
			} else {
				sender.sendMessage(ChatColor.GREEN + "Wheel is available for use!");
			}
			return true;
		}

		return true;
	}

}
