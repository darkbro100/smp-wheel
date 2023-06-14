package me.paul.foliastuff.cmd;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DisplayNameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("no");
            return false;
        }

        Player p = ((Player) sender);

        if (args.length == 0) {
            if (p.displayName().toString().isEmpty()) {
                p.sendMessage(Component.text("Reset display name").color(TextColor.color(125, 125, 125)));
                p.displayName(null);
                p.playerListName(null);
                return false;
            }

            sender.sendMessage(Component.text("/display <name>").color(TextColor.color(125, 0, 0)));
            return false;
        }

        String name = ChatColor.translateAlternateColorCodes('&', args[0]);
        Component dn = Component.text(name);
        p.displayName(dn);
        p.playerListName(dn);
        p.sendMessage(Component.text("Display name changed to: ").color(TextColor.color(125, 125, 125)).append(dn));

        return true;
    }
}
