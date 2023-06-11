package me.paul.foliastuff.wheel;

import com.github.johnnyjayjay.spigotmaps.MapBuilder;
import com.github.johnnyjayjay.spigotmaps.RenderedMap;
import com.github.johnnyjayjay.spigotmaps.rendering.ImageRenderer;
import com.github.johnnyjayjay.spigotmaps.util.ImageTools;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.SettingsManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WheelImageEffect extends WheelEffect {

  private BufferedImage image;

  private WheelImageEffect(BufferedImage image) {
    this.image = image;
  }

  @Override
  public void play(Player spinner, Wheel spun) {
    giveMap(spinner);
    Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " received an image.");
  }

  public void giveMap(Player p) {
    ImageRenderer renderer = ImageRenderer.create(image);
    RenderedMap map = MapBuilder.create().addRenderers(renderer).store(SettingsManager.getInstance()).world(p.getWorld()).build();
    giveMap(map, p);
  }

  public void giveMap(RenderedMap map, Player p) {
    MapView view = Bukkit.createMap(map.getView().getWorld());
    view.getRenderers().forEach(view::removeRenderer);
    map.getView().getRenderers().forEach(view::addRenderer);
    MapMeta mapMeta = (MapMeta) Bukkit.getItemFactory().getItemMeta(Material.FILLED_MAP);
    mapMeta.setMapView(view);

    ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
    itemStack.setItemMeta(mapMeta);

    final ItemStack before = p.getInventory().getItemInMainHand();
    final int beforeSlot = p.getInventory().getHeldItemSlot();
    p.getInventory().setItem(beforeSlot, itemStack);

    if (before != null && before.getType() != Material.AIR)
      p.getInventory().addItem(before);

//        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
//        	p.getInventory().setItem(beforeSlot, before);
//        }, 20L * 5L);
  }

  public static WheelImageEffect create(String fileUrl) {
    File file = new File(fileUrl);
    try {
      BufferedImage image = ImageIO.read(file);
      image = ImageTools.resizeToMapSize(image);

      FoliaStuff.getInstance().getLogger().info("Created WheelEffect from image: " + fileUrl);
      return new WheelImageEffect(image);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

}
