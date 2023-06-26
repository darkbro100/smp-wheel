package me.paul.foliastuff.util.gui;

import lombok.Getter;
import lombok.Setter;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class GuiPage {

  public static Map<GuiPage, Set<UUID>> guipages = new LinkedHashMap<>();

  protected FoliaStuff plugin;
  private Inventory inv;
  private Map<Integer, GuiButton> buttons = new LinkedHashMap<>();
  private String pageName;

  @Getter @Setter
  private Consumer<InventoryClickEvent> mainClickListener;

  /**
   * Constructor for GUI
   *
   * @param height Height of the GUI
   */
  public GuiPage(FoliaStuff plugin, int height) {
    this(plugin, height, "GUI");
  }

  /**
   * Constructor for GUI Page
   *
   * @param height Height of the GUI
   * @param name   Name of the GUI
   */
  public GuiPage(FoliaStuff plugin, int height, String name) {
    if ((9 * height) % 9 != 0) throw new IllegalArgumentException("Height must be a multiple of 9!");

    this.plugin = plugin;
    this.inv = Bukkit.createInventory(null, height * 9, name);
    this.pageName = name;

    guipages.put(this, new HashSet<>());
  }

  /**
   * @return Size of the inventory
   */
  public int getSize() {
    return inv.getSize();
  }

  /**
   * @return Next available slot in the inventory, will return -1 if there is no available slot
   */
  public int nextIndex() {
    for (int i = 0; i < getSize(); i++) {
      if (!buttons.containsKey(i)) return i;
    }
    return -1;
  }

  /**
   * Adds button to the GUI
   *
   * @param button Button to be added
   * @throws RuntimeException if Inventory is full
   */
  public void addButton(GuiButton button) {
    int index = nextIndex();

    if (index == -1) throw new RuntimeException("Inventory cannot be full!");
    setButton(index, button);
  }

  /**
   * @return The name of this {@link GuiPage}
   */
  public String getPageName() {
    return pageName;
  }

  /**
   * Sets a button in the GUI at a certain slot
   *
   * @param position Slot where the button will be placed
   * @param button   Button to be added
   * @throws IllegalArgumentException if the position is higher than the size of the inventory
   */
  public void setButton(int position, GuiButton button) {
    if (position > getSize()) throw new IllegalArgumentException("Position cannot be bigger than the size!");
    buttons.put(position, button);

    // update some local vars
    button.setGui(this);
    button.setSlot(position);

    update();
  }

  public void setButton(int row, int column, GuiButton button) {
    setButton(row * 9 + column, button);
  }

  /**
   * Sets all buttons to correct position
   */
  public void update() {
    inv.clear();

    buttons.entrySet().forEach(entry -> {
      inv.setItem(entry.getKey(), entry.getValue().getItem());
    });
  }

  /**
   * Remove a button at a specific slot
   *
   * @param i Slot
   */
  public void removeButton(int i) {
    GuiButton b = getButton(i);
    if (b != null) {
      b.setGui(null);
      b.setListener((GuiClickEvent) null);

      buttons.remove(i);
      update();

      inv.setItem(i, null);
    }
  }

  /**
   * @param slot Slot of where the button *should* be located
   * @return Button at the slot specified
   */
  public GuiButton getButton(int slot) {
    int exists = buttons.keySet().stream().filter(i -> i == slot).findFirst().orElse(-1);

    if (exists != -1)
      return buttons.get(exists);

    return null;
  }

  /**
   * Shows the player the GUI
   *
   * @param player Player who the GUI will be shown to
   */
  public void show(Player player) {
    guipages.get(this).add(player.getUniqueId());
    player.openInventory(inv);
  }

  /**
   * @param inv Inventory of GUI Page
   * @return GuiPage based on inventory
   */
  public static GuiPage get(Inventory inv) {
    return guipages.keySet().stream().filter(gui -> gui.getInv().equals(inv)).findAny().orElse(null);
  }

  /**
   * @param player Player that may or may not be in the GUI
   * @return true if player is currently in GUI
   */
  public boolean hasPlayer(Player player) {
    return guipages.get(this).stream().filter(uuid -> uuid.equals(player.getUniqueId())).findAny().isPresent();
  }

  /**
   * Adds a back button to this {@link GuiPage} to return to a previous {@link GuiPage}
   *
   * @param itemStack {@link ItemStack} to represent the back button
   * @param position  Position in  the inventory where the stack will be
   * @param mainPage  The {@link GuiPage} to be redirected to
   * @param listener  An optional listener to be called when the item is clicked
   */
  public void addBack(ItemStack itemStack, int position, GuiPage mainPage, GuiClickEvent listener) {
    if (mainPage != null && ItemUtil.getName(itemStack).contains(Guis.MENU_KEY))
      ItemUtil.setName(itemStack, ItemUtil.getName(itemStack).replaceAll(Pattern.quote(Guis.MENU_KEY), mainPage.pageName));

    GuiButton item = new GuiButton(itemStack);
    setButton(position, item);

    item.setListener((event, pl) -> {
      event.setCancelled(true);

      if (mainPage != null)
        mainPage.show(pl);
      /// pl.playSound(pl.getLocation(), Sound.DOOR_OPEN, 1.0F, 1.0F);
      if (listener != null)
        listener.onClick(event);
    });
    // player.playSound(this.player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
  }

  /**
   * Adds the default back button to the top left of the page.
   *
   * @param mainPage The page to return to when clicked.
   */
  public void addBack(GuiPage mainPage) {
    addBack(Guis.getExitButton(), 0, mainPage, null);
  }

  public void updateForOpened() {
    guipages.get(this).forEach(uuid -> {
      Player player = Bukkit.getPlayer(uuid);
      if (player == null)
        return;

      player.updateInventory();
    });
  }

  public Inventory getInv() {
    return inv;
  }

}
