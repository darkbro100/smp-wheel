package me.paul.foliastuff.other;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.paul.foliastuff.util.scheduler.TaskHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class HomeTimer {
    public static final int WAIT_TIME = 20 * 3;

    public HomeTimer(Player player, Location toTeleport) {
        this.player = player;
        this.toTeleport = toTeleport;
    }

    private final Location toTeleport;
    private final Player player;
    private int passed;
    private Location lastLoc;

    public void run(TaskHolder task) {
        Location loc = player.getLocation();
        if (lastLoc != null && !LocUtil.matches(loc, lastLoc)) {
            player.sendMessage(Component.text("You moved! Stopped teleport.").color(TextColor.color(175, 70, 0)));
            task.cancel();
            return;
        }

        if (passed >= WAIT_TIME) {
            player.teleportAsync(toTeleport, PlayerTeleportEvent.TeleportCause.PLUGIN).whenComplete((b, e) -> {
                player.sendMessage(Component.text("Teleported successfully!").color(TextColor.color(0, 255, 0)));
            });
            task.cancel();
            return;
        }

        lastLoc = loc;
        passed++;
    }
}
