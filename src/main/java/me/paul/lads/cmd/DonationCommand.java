package me.paul.lads.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.paul.lads.streamlabs.LabUser;
import me.paul.lads.streamlabs.LabUtil;

public class DonationCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(ChatColor.GREEN + "/" + lbl + " setgoal <goal>");
			sender.sendMessage(ChatColor.GREEN + "/" + lbl + " add <amount>");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("setgoal")) {
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "/" + lbl + " setgoal <goal>");
				return true;
			}
			
			try {
				Double goal = Double.parseDouble(args[1]);
				
				if(goal < 50D) {
					sender.sendMessage(ChatColor.RED + "Minimum donation goal must be $50!");
					return true;
				}
				
				synchronized (LabUtil.getInstance()) {
					LabUser user = LabUtil.getInstance().getUser(((Player)sender).getUniqueId());
					
					if(user == null) {
						sender.sendMessage(ChatColor.RED + "You are not a registered streamer on this server!");
						return true;
					}
					
					user.setWheelGoal(goal);
					sender.sendMessage(ChatColor.GREEN + "Set your donation goal to: $" + goal);
				}
			}catch(Exception e) {
				sender.sendMessage(ChatColor.RED + "Type a real number noob");
				return true;
			}
			
			return true;
		}
		
		if(args[0].equalsIgnoreCase("add")) {
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "/" + lbl + " add <amount>");
				return true;
			}
			
			try {
				double money = Double.parseDouble(args[1]);
				
				synchronized (LabUtil.getInstance()) {
					LabUser user = LabUtil.getInstance().getUser(((Player)sender).getUniqueId());
					if(user == null) {
						sender.sendMessage(ChatColor.RED + "You are not a registered streamer on this server!");
						return true;
					}
					
					user.setMoneyReceived(user.getMoneyReceived() + money);
					sender.sendMessage(ChatColor.GREEN + "Gave " + user.getTwitchUser() + " $" + money);
				}
			}catch(Exception e) {
				sender.sendMessage("Please input a valid decimal!");
				return true;
			}
			return true;
		}
		return true;
	}

}
