package me.paul.foliastuff.other;

import com.github.johnnyjayjay.spigotmaps.MapBuilder;
import com.github.johnnyjayjay.spigotmaps.RenderedMap;
import com.github.johnnyjayjay.spigotmaps.rendering.GifImage;
import com.github.johnnyjayjay.spigotmaps.rendering.GifRenderer;
import com.github.johnnyjayjay.spigotmaps.rendering.ImageRenderer;
import com.github.johnnyjayjay.spigotmaps.util.ImageTools;
import com.madgag.gif.fmsware.GifDecoder;
import me.paul.foliastuff.util.SettingsManager;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DisplayImageCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player))
      return false;

    if (args.length < 2) {
      sender.sendMessage("/" + label + " <gif/image> <path>");
      return false;
    }

    Player player = ((Player) sender);
    String type = args[0];
    String path = args[1];
    Entity target = player.getTargetEntity(50, true);

    if (target == null || !(target instanceof ItemFrame)) {
      sender.sendMessage("Must be looking at an Item Frame");
      return false;
    }

    switch (type.toLowerCase()) {
      case "gif": {
        File file = new File(FoliaStuff.getInstance().getDataFolder() + File.separator + path);
        GifDecoder decoder = new GifDecoder();
        decoder.read(file.getAbsolutePath());
        GifImage gifImage = GifImage.create(IntStream.range(0, decoder.getFrameCount()).mapToObj((i) -> GifImage.Frame.create(decoder.getFrame(i), 50)).collect(Collectors.toList()));

        // duplicate gif with proper timings maybe this fixes osme shit?
        List<ItemStack> items = ImageTools.divideIntoMapSizedParts(gifImage, true).stream().map(gif -> GifRenderer.builder().gif(gif).repeat(-1).build()).map(ir -> MapBuilder.create().addRenderers(ir).store(SettingsManager.getInstance()).world(player.getWorld()).build()).map(RenderedMap::createItemStack).collect(Collectors.toList());
        int sides = ((int) Math.sqrt(items.size()));
        drawItems(target, items, sides);

        break;
      }

      case "image": {
        File file = new File(FoliaStuff.getInstance().getDataFolder() + File.separator + path);
        BufferedImage image = null;
        try {
          image = cropToMapDividableSquare(ImageIO.read(file));
          List<ItemStack> items = ImageTools.divideIntoMapSizedParts(image, false).stream().map(ImageRenderer::create).map(ir -> MapBuilder.create().addRenderers(ir).store(SettingsManager.getInstance()).world(player.getWorld()).build()).map(RenderedMap::createItemStack).collect(Collectors.toList());
          int sides = ((int) Math.sqrt(items.size()));
          drawItems(target, items, sides);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        break;
      }
    }

    return false;
  }

  private void drawItems(Entity target, List<ItemStack> items, int sides) {
    BlockFace facing = getFacing(target.getLocation());
    BlockData data = target.getLocation().clone().add(-facing.getModX(), -facing.getModY(), -facing.getModZ()).getBlock().getBlockData().clone();
    BlockFace face = getBlockFace(target.getLocation());
    Location start = target.getLocation().clone();

    for (int i = 0; i < sides; i++) {
      for (int j = 0; j < sides; j++) {
        int index = i * sides + j;
        Location nextLoc = start.clone().add(face.getModX() * i, -j, face.getModZ() * i);
        nextLoc.clone().add(-facing.getModX(), -facing.getModY(), -facing.getModZ()).getBlock().setBlockData(data);

        ItemFrame frame = (ItemFrame) nextLoc.getWorld().spawnEntity(nextLoc, EntityType.ITEM_FRAME);
        frame.setItem(items.get(index), false);

        System.out.println("spawned: " + index + " at " + nextLoc);
      }
    }
  }

  private static BufferedImage cropToMapDividableSquare(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    Dimension size = ImageTools.MINECRAFT_MAP_SIZE;
    if (width < size.width && height < size.height) {
      return ImageTools.resizeToMapSize(image);
    } else if (width < size.width) {
      return ImageTools.resizeToMapSize(image.getSubimage(0, (height - size.height) / 2, size.width, size.height));
    } else if (height < size.height) {
      return ImageTools.resizeToMapSize(image.getSubimage((width - size.width) / 2, 0, size.width, size.height));
    } else {
      int measure = width < height
        ? size.width * (width / size.width)
        : size.height * (height / size.height);
      return ImageTools.copyOf(image).getSubimage((width - measure) / 2, (height - measure) / 2, measure, measure);
    }
  }

  private BlockFace getBlockFace(Location start) {
    Entity e = start.getWorld().getNearbyEntities(start, 1, 1, 1).stream().findFirst().orElse(null);
    if (e instanceof ItemFrame) {
      BlockFace face = e.getFacing();
      e.remove();

      if (face == BlockFace.SOUTH)
        return BlockFace.EAST;
      if (face == BlockFace.EAST)
        return BlockFace.NORTH;
      if (face == BlockFace.NORTH)
        return BlockFace.WEST;
      if (face == BlockFace.WEST)
        return BlockFace.SOUTH;
    }

    return BlockFace.EAST;
  }

  private BlockFace getFacing(Location start) {
    Entity e = start.getWorld().getNearbyEntities(start, 1, 1, 1).stream().findFirst().orElse(null);
    if (e instanceof ItemFrame) {
      return e.getFacing();
    }

    return BlockFace.EAST;
  }
}
