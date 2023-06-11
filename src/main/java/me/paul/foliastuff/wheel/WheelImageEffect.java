package me.paul.foliastuff.wheel;

import com.github.johnnyjayjay.spigotmaps.MapBuilder;
import com.github.johnnyjayjay.spigotmaps.RenderedMap;
import com.github.johnnyjayjay.spigotmaps.rendering.ImageRenderer;
import com.github.johnnyjayjay.spigotmaps.util.ImageTools;
import com.google.common.collect.Lists;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.SettingsManager;
import me.paul.foliastuff.util.Util;
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
import java.util.List;
import java.util.Map;

public class WheelImageEffect extends WheelEffect {

  private final List<BufferedImage> images = Lists.newArrayList();

  private WheelImageEffect(BufferedImage image) {
    this.images.add(image);
  }

  private WheelImageEffect(List<BufferedImage> images) {
    this.images.addAll(images);
  }

  @Override
  public void play(Player spinner, Wheel spun) {
    giveMap(spinner);
    Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " received an image.");
  }

  public void giveMap(Player p) {
    BufferedImage image = Util.getRandomEntry(images);
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

    if (before.getType() != Material.AIR) {
      Map<Integer, ItemStack> drops = p.getInventory().addItem(before);
      if (!drops.isEmpty()) {
        for (ItemStack drop : drops.values()) {
          p.getWorld().dropItemNaturally(p.getLocation(), drop);
        }
      }
    }
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

  public static WheelImageEffect create(List<String> filePaths) {
    List<BufferedImage> images = Lists.newArrayList();
    for (String str : filePaths) {
      File file = new File(str);

      try {
        BufferedImage image = ImageIO.read(file);
        image = ImageTools.resizeToMapSize(image);
        images.add(image);
      } catch (IOException e) {
        FoliaStuff.getInstance().getLogger().warning("Failed to load image: " + str);
        e.printStackTrace();
      }
    }

    if (images.isEmpty())
      return null;

    return new WheelImageEffect(images);
  }

}
