package me.paul.foliastuff.other;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SetHomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            String homeStr = LocUtil.locToString(player.getLocation());
            player.getPersistentDataContainer().set(FoliaStuff.KEY, PersistentDataType.STRING, homeStr);
            TextComponent text = Component.text().content("Set your home!").color(TextColor.color(0, 255, 0)).build();
            player.sendMessage(text);
            return true;
        }

        return false;
    }
}
