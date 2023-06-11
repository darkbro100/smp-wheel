package me.paul.foliastuff.wheel.effects;

import com.github.johnnyjayjay.spigotmaps.MapBuilder;
import com.github.johnnyjayjay.spigotmaps.RenderedMap;
import com.github.johnnyjayjay.spigotmaps.rendering.ImageRenderer;
import com.github.johnnyjayjay.spigotmaps.util.ImageTools;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.wheel.GenerateEffect;
import me.paul.foliastuff.wheel.Wheel;
import me.paul.foliastuff.wheel.WheelEffect;
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

@GenerateEffect(description = "This is a test wheel effect", key = "effect_test", name = "Test", enabled = false)
public class TestEffect extends WheelEffect {

  private BufferedImage image;

  public TestEffect() {
    try {
      this.image = ImageIO.read(new File("C:\\Users\\pauli\\Desktop\\Pants.png"));
      this.image = ImageTools.resizeToMapSize(this.image);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void play(Player spinner, Wheel spun) {
    ImageRenderer renderer = ImageRenderer.create(image, spinner);
    RenderedMap map = MapBuilder.create().addRenderers(renderer).world(spinner.getWorld()).build();
    giveMap(map, spinner);
  }

  private void giveMap(RenderedMap map, Player p) {
    MapView view = Bukkit.createMap(map.getView().getWorld());
    map.getView().getRenderers().forEach(view::addRenderer);
    MapMeta mapMeta = (MapMeta) Bukkit.getItemFactory().getItemMeta(Material.FILLED_MAP);
    mapMeta.setMapView(view);

    ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
    itemStack.setItemMeta(mapMeta);

    final ItemStack before = p.getInventory().getItemInMainHand();
    final int beforeSlot = p.getInventory().getHeldItemSlot();

    p.getInventory().setItem(beforeSlot, itemStack);

    Sync.get(p).delay(20 * 5).run(() -> p.getInventory().setItem(beforeSlot, before));
  }

}
