package me.paul.lads.wheel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import com.github.johnnyjayjay.spigotmaps.MapBuilder;
import com.github.johnnyjayjay.spigotmaps.RenderedMap;
import com.github.johnnyjayjay.spigotmaps.rendering.ImageRenderer;
import com.github.johnnyjayjay.spigotmaps.util.ImageTools;

import me.paul.lads.Main;
import me.paul.lads.util.SettingsManager;
import net.md_5.bungee.api.ChatColor;

public class WheelImageEffect extends WheelEffect {

	private BufferedImage image;
	
	private WheelImageEffect(BufferedImage image) {
		this.image = image;
	}
	
	@Override
	public void play(Player spinner, Wheel spun) {
		ImageRenderer renderer = ImageRenderer.create(image);
		RenderedMap map = MapBuilder.create().addRenderers(renderer).store(SettingsManager.getInstance()).world(spinner.getWorld()).build();
		giveMap(map, spinner);
		Bukkit.broadcastMessage(ChatColor.GREEN + spinner.getName() + " received an image.");
	}
	
	private void giveMap(RenderedMap map, Player p) {
		MapView view = Bukkit.createMap(map.getWorld());
		view.getRenderers().forEach(view::removeRenderer);
		map.getRenderers().forEach(view::addRenderer);
        MapMeta mapMeta = (MapMeta) Bukkit.getItemFactory().getItemMeta(Material.FILLED_MAP);
        mapMeta.setMapView(view);
        
        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        itemStack.setItemMeta(mapMeta);
        
        final ItemStack before = p.getInventory().getItemInMainHand();
        final int beforeSlot = p.getInventory().getHeldItemSlot();
        p.getInventory().setItem(beforeSlot, itemStack);
        
        if(before != null && before.getType() != Material.AIR)
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
			
			WheelImageEffect effect = new WheelImageEffect(image);
			return effect;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
