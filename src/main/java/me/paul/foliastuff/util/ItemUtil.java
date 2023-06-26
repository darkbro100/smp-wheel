package me.paul.foliastuff.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ItemUtil {

  public static ItemStack setName(ItemStack stack, String name) {
    if (name == null)
      return stack;
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(ChatColor.RESET + name);
    stack.setItemMeta(meta);
    return stack;
  }

  public static String getName(ItemStack stack) {
    if (stack == null)
      return "";
    if (stack.getItemMeta() == null)
      return "";
    String displayed = stack.getItemMeta().getDisplayName();
    if (displayed == null)
      return "";
    displayed = displayed.length() > 2 ? displayed.substring(2) : displayed;
    return displayed;
  }

  public static ItemStack setRawName(ItemStack stack, String name) {
    ItemMeta meta = stack.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(name);
      stack.setItemMeta(meta);
    }
    return stack;
  }

  public static String getRawItemName(ItemStack stack) {
    if (stack == null)
      return "";
    if (stack.getItemMeta() == null)
      return "";
    String displayed = stack.getItemMeta().getDisplayName();
    if (displayed == null)
      return "";
    return displayed;
  }

  public static List<String> getLore(ItemStack stack) {
    if (stack == null)
      return null;
    if (stack.getItemMeta() == null)
      return null;
    return stack.getItemMeta().getLore();
  }

  public static Integer getId(Item item) {
    if (item == null)
      return null;
    return getId(item.getItemStack());
  }

  public static Integer getId(ItemStack stack) {
    if (stack == null)
      return null;
    if (stack.getItemMeta() == null)
      return null;
    if (stack.getItemMeta().getLore() == null)
      return null;
    for (String line : stack.getItemMeta().getLore())
      if (line.startsWith("id:"))
        try {
          return Integer.parseInt(line.substring(3));
        } catch (NumberFormatException e) {
          return null;
        }
    return null;
  }

  public static ItemStack setId(ItemStack stack, int id) {
    ItemMeta meta = stack.getItemMeta();
    ArrayList<String> lore = new ArrayList<>();
    if (meta.getLore() != null)
      lore.addAll(meta.getLore());
    boolean set = false;
    for (int i = 0; i < lore.size() && !set; i++)
      if (lore.get(i).startsWith("id:")) {
        lore.set(i, "id:" + id);
        set = true;
      }
    if (!set)
      lore.add("id:" + id);
    meta.setLore(lore);
    stack.setItemMeta(meta);
    return stack;
  }

  public static ItemStack setDescription(ItemStack stack, List<String> description) {
    if (stack == null)
      return stack;
    if (description == null)
      return stack;
    ItemMeta meta = stack.getItemMeta();
    ArrayList<String> lore = new ArrayList<>();
    for (String line : description)
      if (line != null) // && line.length() != 0)
        lore.add(ChatColor.RESET + line);
    if (lore.size() != 0)
      meta.setLore(lore);
    stack.setItemMeta(meta);
    return stack;
  }

  public static ItemStack setDescription(ItemStack stack, String... description) {
    return ItemUtil.setDescription(stack, Arrays.asList(description));
  }

  public static ItemStack clearDescription(ItemStack stack) {
    if (stack == null)
      return stack;

    ItemMeta meta = stack.getItemMeta();

    if (!meta.hasLore())
      return stack;

    meta.getLore().clear();

    stack.setItemMeta(meta);

    return stack;
  }

  public static ItemStack create(ItemStack itemStack, String name, String... description) {
    itemStack = setName(itemStack, name);
    itemStack = setDescription(itemStack, description);
    return itemStack;
  }

  public static ItemStack create(Material type, String name, String... description) {
    return ItemUtil.create(type, 1, name, description);
  }

  public static ItemStack create(Material type, int amount, String name, String... description) {
    return ItemUtil.setDescription(ItemUtil.setName(new ItemStack(type, amount), name), description);
  }

  public static ItemStack create(Material type, int amount, short data, String name, String... description) {
    return ItemUtil.setDescription(ItemUtil.setName(new ItemStack(type, amount, data), name), description);
  }

  /**
   * @deprecated Unknown what the purpose of this is. It would be just as easy
   * to strip color codes and find the length (?)
   */
  @Deprecated
  public static int translateLength(String string, int length) {
    int nonColorCharCount = 0;
    boolean previousWasColorChar = false;
    for (int i = 0; i < string.length(); i++)
      if (previousWasColorChar)
        previousWasColorChar = false;
      else if (string.charAt(i) == ChatColor.COLOR_CHAR)
        previousWasColorChar = true;
      else {
        nonColorCharCount++;
        if (nonColorCharCount == length)
          return i + 1;
      }
    return string.length();
  }

  /**
   * Wraps the given String, but avoids cutting color characters off.
   *
   * @param string     The String to wrap.
   * @param lineLength The length of each line.
   * @return A list of wrapped text.
   */
  public static List<String> wrapWithColor(String string, int lineLength) {
    int length = translateLength(string, lineLength);
    List<String> lines;
    if (length == string.length()) {
      lines = new LinkedList<>();
      lines.add(string);
    } else {
      int lastSpace = string.lastIndexOf(' ', length);
      length = lastSpace == -1 ? length : lastSpace + 1;
      String line = string.substring(0, length).trim();
      lines = wrapWithColor(ChatColor.getLastColors(line) + string.substring(length).trim(), lineLength);
      lines.add(0, line);
    }
    return lines;
  }
}
