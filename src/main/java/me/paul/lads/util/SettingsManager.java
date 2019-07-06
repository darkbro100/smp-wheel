package me.paul.lads.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapRenderer;

import com.github.johnnyjayjay.spigotmaps.MapStorage;
import com.github.johnnyjayjay.spigotmaps.rendering.ImageRenderer;
import com.google.common.collect.Lists;

import me.paul.lads.Main;
import me.paul.lads.streamlabs.LabUser;
import me.paul.lads.streamlabs.LabUtil;
import me.paul.lads.wheel.Wheel;

public class SettingsManager implements MapStorage {

	private static final SettingsManager instance = new SettingsManager();

	private SettingsManager() {
	}

	private File wheelFile;
	private YamlConfiguration wheelConfig;

	private File userFolder;

	public static SettingsManager getInstance() {
		return instance;
	}

	public void setup() {
		if (!Main.getInstance().getDataFolder().exists())
			Main.getInstance().getDataFolder().mkdir();

		wheelFile = new File(Main.getInstance().getDataFolder(), "wheel.yml");
		if (!wheelFile.exists()) {
			try {
				wheelFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Could not create wheel.yml!");
				e.printStackTrace();
			}
		}

		wheelConfig = YamlConfiguration.loadConfiguration(wheelFile);

		loadLabUsers();
	}

	private void loadLabUsers() {
		userFolder = new File(Main.getInstance().getDataFolder(), "users");
		if (!userFolder.exists())
			userFolder.mkdir();

		for (File f : userFolder.listFiles()) {
			try {
				String uuidStr = f.getName().replaceAll(".yml", "");
				UUID uuid = UUID.fromString(uuidStr);

				YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(f);
				String twitch = playerConfig.getString("twitch");
				String userName = playerConfig.getString("lastUserName");
				double wheelUnlock = playerConfig.getDouble("wheelUnlock");
				double moneyReceived = playerConfig.getDouble("moneyReceived");
				String accessToken = playerConfig.getString("accessToken");
				int spins = playerConfig.getInt("spins-available");

				LabUser user = new LabUser(userName, uuid, twitch, accessToken);
				user.setWheelGoal(wheelUnlock);
				user.setMoneyReceived(moneyReceived);
				user.setSpins(spins);

				LabUtil.getInstance().getStreamers().add(user);
				System.out.println("Loaded streamer: " + user.getTwitchUser() + " " + user.getMcUser());
			} catch (Exception e) {
				System.err.println("Error trying to read user file: " + f.getAbsolutePath());
				e.printStackTrace();
			}
		}
	}

	public void saveLabUsers() {
		for (LabUser user : LabUtil.getInstance().getStreamers()) {
			File userFile = new File(userFolder, user.getMcUuid().toString() + ".yml");
			if (!userFile.exists()) {
				try {
					userFile.createNewFile();
				} catch (Exception e) {
					System.err.println("Error creating file for streamer: " + user.getMcUser());
					e.printStackTrace();
				}
			}

			YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(userFile);
			userConfig.set("twitch", user.getTwitchUser());
			userConfig.set("lastUserName", user.getMcUser());
			userConfig.set("accessToken", user.getAccessToken());
			userConfig.set("wheelUnlock", user.getWheelGoal());
			userConfig.set("moneyReceived", user.getMoneyReceived());
			userConfig.set("spins-available", user.getSpins());

			try {
				userConfig.save(userFile);
			} catch (IOException e) {
				System.err.println("Error writing to user file: " + userFile.getAbsolutePath());
				e.printStackTrace();
			}
		}
	}

	public YamlConfiguration getWheelConfig() {
		return wheelConfig;
	}

	/**
	 * private Location center; private Map<Vector, Material> cachedBlocks; private
	 * int radius; private int pieces; private Material[] parts; private int
	 * offsetInc; private int updateFrequency; private int lastAngle = 0;
	 */

	public void loadWheels() {
		if (!wheelConfig.isConfigurationSection("wheels"))
			return;

		for (String key : wheelConfig.getConfigurationSection("wheels").getKeys(false)) {
			String path = "wheels." + key;

			Location center = LocUtil.locFromString(wheelConfig.getString(path + ".center"));
			String buttonStr = wheelConfig.getString(path + ".buttonLoc");
			int radius = wheelConfig.getInt(path + ".radius");
			int pieces = wheelConfig.getInt(path + ".pieces");
			List<Material> mats = wheelConfig.getStringList(path + ".parts").stream().map(Material::getMaterial)
					.collect(Collectors.toList());
			Material[] parts = mats.toArray(new Material[mats.size()]);
			int offsetInc = wheelConfig.getInt(path + ".offsetInc");
			int updateFreq = wheelConfig.getInt(path + ".updateFreq");
			int lastAngle = wheelConfig.getInt(path + ".lastAngle");

			Wheel w = new Wheel(center, radius, pieces, offsetInc, updateFreq, parts);
			w.setLastAngle(lastAngle);
			if (buttonStr != null) {
				w.setButtonBlock(LocUtil.locFromString(buttonStr));
			}

			System.out.println("Wheel Loaded");
		}
	}

	/**
	 * Location center = LocUtil.locFromString(wheelConfig.getString(path +
	 * ".center")); int radius = wheelConfig.getInt(path + ".radius"); int pieces =
	 * wheelConfig.getInt(path + ".pieces"); List<Material> mats =
	 * wheelConfig.getStringList(path +
	 * ".parts").stream().map(Material::getMaterial).collect(Collectors.toList());
	 * Material[] parts = mats.toArray(new Material[mats.size()]); int offsetInc =
	 * wheelConfig.getInt(path + ".offsetInc"); int updateFreq =
	 * wheelConfig.getInt(path + ".updateFreq"); int lastAngle =
	 * wheelConfig.getInt(path + ".lastAngle");
	 */

	public void saveWheels() {
		wheelConfig.set("wheels", null);
		try {
			wheelConfig.save(wheelFile);

			for (Wheel w : Wheel.getWheels()) {
				String path = "wheels." + w.getId() + ".";

				List<String> parts = new ArrayList<>();
				for (Material m : w.getParts())
					parts.add(m.name());

				wheelConfig.set(path + "center", LocUtil.locToString(w.getCenter()));
				wheelConfig.set(path + "radius", w.getRadius());
				wheelConfig.set(path + "pieces", w.getPieces());
				wheelConfig.set(path + "parts", parts);
				wheelConfig.set(path + "offsetInc", w.getOffsetInc());
				wheelConfig.set(path + "updateFreq", w.getUpdateFrequency());
				wheelConfig.set(path + "lastAngle", w.getLastAngle());
				wheelConfig.set(path + "buttonLoc",
						w.getButtonBlock() == null ? null : LocUtil.locToString(w.getButtonBlock()));
			}

			wheelConfig.save(wheelFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadMapRenders() {
		File imageFolder = new File(Main.getInstance().getDataFolder(), "cache");
		if(!imageFolder.exists()) {
			imageFolder.mkdir();
			return;
		}
		
		for(File folderId : imageFolder.listFiles()) {
			if(!folderId.isDirectory())
				continue;
			
			int id = Integer.parseInt(folderId.getName());
			List<MapRenderer> renders = new ArrayList<>();
			
			for(File imageFile : folderId.listFiles()) {
				try {
					BufferedImage image = ImageIO.read(imageFile);
					ImageRenderer render = ImageRenderer.create(image);
					renders.add(render);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(renders.isEmpty()) {
				System.out.println("Found no renders for map id: " + id);
				continue;
			}
			
			System.out.println("Loaded map render: " + id);
			storedMaps.put(id, renders);
		}
	}
	
	public void saveMapRenders() {
		File imageFolder = new File(Main.getInstance().getDataFolder(), "cache");
		if(!imageFolder.exists())
			imageFolder.mkdir();
		
		for(Entry<Integer, List<MapRenderer>> entry : storedMaps.entrySet()) {
			int id = entry.getKey();
			List<MapRenderer> renders = entry.getValue();
			
			File mapFolder = new File(imageFolder, String.valueOf(id));
			if(!mapFolder.exists())
				mapFolder.mkdir();
			
			for(MapRenderer render : renders) {
				if(render instanceof ImageRenderer) {
					ImageRenderer imgRender = (ImageRenderer) render;
					BufferedImage rawImage = imgRender.getImage();
					
					File imageFile = new File(mapFolder, "image.png");
					try {
						ImageIO.write(rawImage, "png", imageFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private HashMap<Integer, List<MapRenderer>> storedMaps = new HashMap<>();

	@Override
	public List<MapRenderer> provide(int id) {
		return storedMaps.get(id);
	}

	@Override
	public void remove(int id, MapRenderer renderer) {
		id = id+1;
		List<MapRenderer> maps = storedMaps.getOrDefault(id, Lists.newArrayList());
		maps.remove(renderer);

		if (maps.isEmpty())
			storedMaps.remove(id);
		else
			storedMaps.put(id, maps);
	}

	@Override
	public void store(int id, MapRenderer renderer) {
		id = id+1;
		List<MapRenderer> maps = storedMaps.getOrDefault(id, Lists.newArrayList()); //TODO: have to tag the +1 because of how dog shit this API is
		maps.add(renderer);

		storedMaps.put(id, maps);
	}

}
