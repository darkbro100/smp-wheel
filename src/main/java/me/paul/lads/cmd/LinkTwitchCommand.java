package me.paul.lads.cmd;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.twitch4j.streamlabs4j.api.domain.StreamlabsUser;

import me.paul.lads.streamlabs.LabUser;
import me.paul.lads.streamlabs.LabUtil;
import net.md_5.bungee.api.ChatColor;

public class LinkTwitchCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/linktwitch <twitch_user>");
			return true;
		}
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("nu");
			return true;
		}
		
		UUID pUuid = ((Player)sender).getUniqueId();
		
		String twitchUser = args[0];
		LabUser user = LabUtil.getInstance().getUser(pUuid);
		
		if(user != null) {
			sender.sendMessage(ChatColor.RED + "You are already linked with a twitch account! You cannot be linked to multiple twitch accounts!");
			return true;
		}
		
		user = LabUtil.getInstance().getUser(twitchUser, true);
		
		if(user != null) {
			sender.sendMessage(ChatColor.RED + user.getTwitchUser() + " is already associated with an in-game minecraft account! Contact an admin if this is incorrect!");
			return true;
		}
		
		Entry<String, StreamlabsUser> entry = LabUtil.getInstance().getCachedUsers().entrySet().stream().filter(e -> e.getValue().getTwitch().get().getDisplayName().equalsIgnoreCase(twitchUser)).findAny().orElse(null);
		
		if(entry == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " does not have an access token on this server! Contact an admin!");
			return true;
		}
		
		user = new LabUser(((Player)sender).getName(), ((Player)sender).getUniqueId(), entry.getValue().getTwitch().get().getDisplayName(), entry.getKey());
		LabUtil.getInstance().getStreamers().add(user);
		
		sender.sendMessage(ChatColor.GREEN + "Successfully linked your in-game account with your twitch account: " + user.getTwitchUser());
		return true;
	}
	
}
