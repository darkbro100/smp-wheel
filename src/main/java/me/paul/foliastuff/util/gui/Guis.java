package me.paul.foliastuff.util.gui;

import me.paul.foliastuff.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Guis {
  /**
   * A String that is replaced with the name of the parent GUI.
   */
  public static final String MENU_KEY = "{{exit}}";
  /**
   * A String that is replaced with the page number of the previous page.
   */
  public static final String NUM_PREV_KEY = "{{#prev}}";
  /**
   * A String that is replaced with the page number of the next page.
   */
  public static final String NUM_NEXT_KEY = "{{#next}}";
  /**
   * A String that is replaced with number of total pages.
   */
  public static final String NUM_TOTAL_KEY = "{{#total}}";
  private static final ItemStack MENU_BUTTON = new ItemBuilder(Material.REPEATER).setName(" > " + MENU_KEY).build();
  private static final ItemStack BACK_BUTTON = new ItemBuilder(Material.WHITE_CARPET).setName("◄ Back (" + NUM_PREV_KEY + "/" + NUM_TOTAL_KEY + ")").build();
  private static final ItemStack NEXT_BUTTON = new ItemBuilder(Material.WHITE_CARPET).setName("Next (" + NUM_NEXT_KEY + "/" + NUM_TOTAL_KEY + ") ►").build();

  public static ItemStack getExitButton() {
    return MENU_BUTTON.clone();
  }

  public static ItemStack getPrevButton() {
    return BACK_BUTTON.clone();
  }

  public static ItemStack getNextButton() {
    return NEXT_BUTTON.clone();
  }

}
