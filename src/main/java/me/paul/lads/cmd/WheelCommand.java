package me.paul.lads.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.paul.lads.streamlabs.LabUser;
import me.paul.lads.streamlabs.LabUtil;
import me.paul.lads.wheel.Wheel;

public class WheelCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(args.length > 0 && args[0].equalsIgnoreCase("progress")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("nu");
				return true;
			}
			
			LabUser user = LabUtil.getInstance().getUser(((Player)sender).getUniqueId());
			
			if(user == null)  {
				sender.sendMessage(ChatColor.RED + "You are not a registered streamer on this server!");
				return true;
			}
			
			double required = user.getWheelGoal();
			double received = user.getMoneyReceived();
			
			sender.sendMessage(ChatColor.GOLD + user.getTwitchUser() + " has currently received " + ChatColor.GREEN + "$" + received + ChatColor.GOLD + " out of the donation goal of " + ChatColor.GREEN + "$" + required);
			return true;
		} else if(args.length > 0 && args[0].equalsIgnoreCase("spins")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("nu");
				return true;
			}
			
			LabUser user = LabUtil.getInstance().getUser(((Player)sender).getUniqueId());
			
			int spins = user.getSpins();
			sender.sendMessage(ChatColor.GREEN + "Spins Available: " + ChatColor.RED + spins);
			return true;
		} else if(args.length >= 2 && args[0].equalsIgnoreCase("setbutton") && sender.isOp()) {
			int id = Integer.parseInt(args[1]);
			Wheel w = Wheel.get(id);
			
			Player p = (Player) sender;
			Block b = p.getTargetBlockExact(50);
			
			if(b == null) {
				p.sendMessage(ChatColor.RED + "Button not found!");
				return true;
			}
			
			if(!b.getType().name().contains("BUTTON")) {
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
			if(args.length > 2) {
				force = args[2].equalsIgnoreCase("-f");
			}
			
			if(!(sender instanceof Player)) {
				sender.sendMessage("nu");
				return true;
			}
			
			Player opener = (Player) sender;
			
//			LabUser opener = sender instanceof Player ? LabUtil.getInstance().getUser(((Player)sender).getUniqueId()) : LabUtil.getInstance().getStreamers().get(0);
//			
//			if(opener == null) {
//				sender.sendMessage(ChatColor.RED + "Could not find valid twitch user!");
//				return true;
//			}
			
			if (!w.spin(opener, force)) {
				sender.sendMessage(ChatColor.RED + "Wheel could not be spun!");
				return true;
			} else {
				sender.sendMessage(ChatColor.GREEN + "Spun the wheel!");
			}
		} else if(args.length >= 3  && sender.isOp()) {
			try {
				int radius = Math.abs(Integer.parseInt(args[0]));
				final int offsetInc = Integer.parseInt(args[1]);
				int frequency = Integer.parseInt(args[2]);

				if (!(sender instanceof Player)) {
					return true;
				}

				final Location center = ((Player) sender).getLocation();
				Wheel wheel = new Wheel(center, radius, 6, offsetInc, frequency, Material.DIAMOND_BLOCK,
						Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.COAL_BLOCK, Material.EMERALD_BLOCK,
						Material.REDSTONE_BLOCK);
				wheel.draw();
				sender.sendMessage("made wheel with id: " + wheel.getId());

			} catch (Exception e) {
				sender.sendMessage("Invalid number input for radius!");
				return true;
			}
		} else {
			if(sender.isOp()) {
				sender.sendMessage("/wheel <radius> <offset inc> <freq>");
				sender.sendMessage("/wheel start <id> [-f]");
				sender.sendMessage("/wheel setbutton <id>");
			}
			
			Wheel w = Wheel.get(1);
			if(w.isLocked()) {
				sender.sendMessage(ChatColor.RED + "Wheel is currently on cooldown. Time remaining: " + ChatColor.YELLOW + w.getLastSpin().getRemainingTime());
			} else {
				sender.sendMessage(ChatColor.GREEN + "Wheel is available for use!");
			}
//			sender.sendMessage(ChatColor.GOLD + "/" + lbl + " spins - " + ChatColor.GREEN + "View spins available for the wheel");
//			sender.sendMessage(ChatColor.GOLD + "/" + lbl + " progress" + ChatColor.GREEN + " - View your progress towards unlocking the wheel!");
			return true;
		}

		return true;
	}

}