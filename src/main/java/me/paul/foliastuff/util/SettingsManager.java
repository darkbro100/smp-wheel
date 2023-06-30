package me.paul.foliastuff.util;

import com.github.johnnyjayjay.spigotmaps.MapStorage;
import com.github.johnnyjayjay.spigotmaps.rendering.ImageRenderer;
import com.google.common.collect.Lists;
import me.paul.foliastuff.Case;
import me.paul.foliastuff.CaseItem;
import me.paul.foliastuff.CaseStats;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.scheduler.Sync;
import me.paul.foliastuff.wheel.Wheel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

public class SettingsManager implements MapStorage {

  private static final SettingsManager instance = new SettingsManager();

  private SettingsManager() {
  }

  private File wheelFile, dataFile;
  private YamlConfiguration wheelConfig, dataConfig;

  public static SettingsManager getInstance() {
    return instance;
  }

  public void setup() {
    if (!FoliaStuff.getInstance().getDataFolder().exists())
      FoliaStuff.getInstance().getDataFolder().mkdir();

    wheelFile = new File(FoliaStuff.getInstance().getDataFolder(), "wheel.yml");
    if (!wheelFile.exists()) {
      try {
        wheelFile.createNewFile();
      } catch (IOException e) {
        System.err.println("Could not create wheel.yml!");
        e.printStackTrace();
      }
    }

    wheelConfig = YamlConfiguration.loadConfiguration(wheelFile);

    dataFile = new File(FoliaStuff.getInstance().getDataFolder(), "data.yml");
    if (!dataFile.exists()) {
      try {
        dataFile.createNewFile();
      } catch (IOException e) {
        System.err.println("Could not create data.yml!");
        e.printStackTrace();
      }
    }

    dataConfig = YamlConfiguration.loadConfiguration(dataFile);
  }

  public void loadAllCaseStats() {
    for (String key : dataConfig.getKeys(false)) {
      UUID uuid = UUID.fromString(key);
      CaseStats stats = load(uuid);
      CaseStats.store(uuid, stats);
    }
  }

  public CaseStats load(UUID player) {
    if (!dataConfig.contains(player.toString()))
      return new CaseStats(player);

    CaseStats stats = new CaseStats(player);
    for(CaseItem.CaseRarity rarity : CaseItem.CaseRarity.values())
      stats.setCaseOpens(rarity, dataConfig.getInt(player + "." + rarity.name().toLowerCase()));

    return stats;
  }

  public void save(CaseStats stats) {
    for(CaseItem.CaseRarity rarity : CaseItem.CaseRarity.values())
      dataConfig.set(stats.getUuid() + "." + rarity.name().toLowerCase(), stats.getCaseOpens(rarity));

    try {
      dataConfig.save(dataFile);
    } catch (IOException e) {
      System.err.println("Could not save data.yml!");
      e.printStackTrace();
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
  public void loadCases() {
    File dir = new File(FoliaStuff.getInstance().getDataFolder(), "cases");
    if (!dir.exists()) {
      dir.mkdir();
      return;
    }

    for (File f : dir.listFiles()) {
      if (!f.getName().endsWith(".yml"))
        continue;

      YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

      Location center = LocUtil.locFromString(config.getString("location"));
      Util.Direction direction = Util.Direction.valueOf(config.getString("direction"));
      Case caseInstance = new Case();

      for (String key : config.getConfigurationSection("items").getKeys(false)) {
        String path = "items." + key;
        CaseItem.CaseRarity rarity = CaseItem.CaseRarity.of(key);
        CaseItem caseItem = new CaseItem(rarity);

        for (String itemKey : config.getConfigurationSection(path + ".drops").getKeys(false)) {
          ItemStack stack = config.getItemStack(path + ".drops." + itemKey);
//          FoliaStuff.getInstance().getLogger().info("Loaded " + stack + " for rarity " + rarity.name());
          caseItem.add(stack);
        }

        caseInstance.add(caseItem);
//        FoliaStuff.getInstance().getLogger().info("Loaded caseitem " + rarity.name());
      }

      caseInstance.position(center, direction);
      FoliaStuff.getInstance().getLogger().info("Loaded case " + caseInstance.getId() + " @ " + center + " with " + caseInstance.getItems().length + " items");
    }
  }

  public void saveCases() {
    File dir = new File(FoliaStuff.getInstance().getDataFolder(), "cases");
    if (!dir.exists()) {
      dir.mkdir();
      return;
    }

    for(File f : dir.listFiles())
      f.delete();

    for (Case c : Case.getCases()) {
      File f = new File(dir, c.getId() + ".yml");
      YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

      config.set("location", LocUtil.locToString(c.location()));
      config.set("direction", c.getDirection().name());

      for (CaseItem item : c.getItems()) {
        String path = "items." + item.getRarity().name();
        ItemStack[] items = item.drops();

        for (int i = 0; i < items.length; i++) {
          config.set(path + ".drops." + i, items[i]);
        }
      }

      try {
        config.save(f);
      } catch (IOException e) {
        e.printStackTrace();
      }

      // attempt to delete previous ents
      if(c.interactEntity() != null)
        c.interactEntity().remove();
      if(c.displayEntity() != null)
        c.displayEntity().remove();
    }
  }

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

      FoliaStuff.getInstance().getLogger().info("Wheel Loaded @ " + center);
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
    File imageFolder = new File(FoliaStuff.getInstance().getDataFolder(), "cache");
    if (!imageFolder.exists()) {
      imageFolder.mkdir();
      return;
    }

    for (File folderId : imageFolder.listFiles()) {
      if (!folderId.isDirectory())
        continue;

      int id = Integer.parseInt(folderId.getName());
      List<MapRenderer> renders = new ArrayList<>();

      for (File imageFile : folderId.listFiles()) {
        try {
          BufferedImage image = ImageIO.read(imageFile);
          ImageRenderer render = ImageRenderer.create(image);
          renders.add(render);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      if (renders.isEmpty()) {
//        FoliaStuff.getInstance().getLogger().info("Found no renders for map id: " + id);
        continue;
      }

//      FoliaStuff.getInstance().getLogger().info("Loaded map render: " + id);
      storedMaps.put(id, renders);
    }
  }

  public void saveMapRenders() {
    File imageFolder = new File(FoliaStuff.getInstance().getDataFolder(), "cache");
    if (!imageFolder.exists())
      imageFolder.mkdir();

    for (Entry<Integer, List<MapRenderer>> entry : storedMaps.entrySet()) {
      int id = entry.getKey();
      List<MapRenderer> renders = entry.getValue();

      File mapFolder = new File(imageFolder, String.valueOf(id));
      if (!mapFolder.exists())
        mapFolder.mkdir();

      for (MapRenderer render : renders) {
        if (render instanceof ImageRenderer) {
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

  private final HashMap<Integer, List<MapRenderer>> storedMaps = new HashMap<>();

  @Override
  public List<MapRenderer> provide(int id) {
    return storedMaps.get(id);
  }

  @Override
  public void remove(int id, MapRenderer renderer) {
//        id = id + 1;
    List<MapRenderer> maps = storedMaps.getOrDefault(id, Lists.newArrayList());
    maps.remove(renderer);

    if (maps.isEmpty())
      storedMaps.remove(id);
    else
      storedMaps.put(id, maps);
  }

  @Override
  public void store(int id, MapRenderer renderer) {
//        id = id + 1;
    List<MapRenderer> maps = storedMaps.getOrDefault(id, Lists.newArrayList()); //TODO: have to tag the +1 because of how dog shit this API is
    maps.add(renderer);

    storedMaps.put(id, maps);
  }

}
