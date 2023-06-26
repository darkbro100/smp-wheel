package me.paul.foliastuff.util.gui;

import com.google.common.collect.Lists;
import me.paul.foliastuff.other.FoliaStuff;
import me.paul.foliastuff.util.ItemBuilder;
import me.paul.foliastuff.util.scheduler.Sync;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

public abstract class FlippableGui extends GuiPage {

  protected final int MAX_ITEMS = 28;

  protected GuiPage mainMenu;
  protected int page;
  protected int startIndex;
  protected int endIndex;

  public FlippableGui(FoliaStuff plugin, String name, GuiPage mainMenu, int page) {
    super(plugin, 6, name + " - Page " + (page + 1));

    this.mainMenu = mainMenu;
    this.page = page;
    this.startIndex = page * MAX_ITEMS;
    this.endIndex = startIndex + MAX_ITEMS;

    GuiButton back = new GuiButton(ItemBuilder.of(Material.COMPARATOR).setName("Back").build());
    back.setListener((e, p) -> {
      e.setCancelled(true);

      if (page == 0)
        return;

      openPreviousPage(p);
    });
    this.setButton(0, back);

    GuiButton next = new GuiButton(ItemBuilder.of(Material.REPEATER).setName("Next").build());
    next.setListener((e, p) -> {
      e.setCancelled(true);

      if (endIndex >= getPageButtons().size())
        return;

      openNextPage(p);
    });
    this.setButton(8, next);

    GuiButton backToMenu = new GuiButton(ItemBuilder.of(Material.REDSTONE).setName("Main Menu").build());
    backToMenu.setListener((e, p) -> {
      e.setCancelled(true);
      mainMenu.show(p);
    });
    this.setButton(4, backToMenu);
  }

  public abstract List<GuiButton> getPageButtons();

  public abstract void openNextPage(Player player);

  public abstract void openPreviousPage(Player player);

  @Override
  public void show(Player player) {
    List<GuiButton> allButtons = getPageButtons();
    List<GuiButton> buttons = Lists.newArrayList();

    for (int i = startIndex; i < endIndex; i++) {
      if (i >= allButtons.size())
        continue;

      GuiButton button = allButtons.get(i);
      buttons.add(button);
    }

    int counter = 0;
    for (int row = 1; row <= 4; row++) {
      for (int col = 1; col <= 7; col++) {
        if (counter >= buttons.size())
          continue;

        GuiButton button = buttons.get(counter++);
        setButton(row, col, button);
      }
    }

    super.show(player);
  }

  public void playPurchaseEffect(Player player, int amount, Material type, int price) {
    player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1f, 1f);
    Sync.get(player).delay(3).run(() -> player.playSound(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1f, 1f));

    player.sendMessage(ChatColor.RED + "-" + getMoneyString(price) + " for x" + amount + " " + WordUtils.capitalize(type.name().replace("_", " ").toLowerCase()));
  }

  private String getMoneyString(int money) {
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    String moneyString = formatter.format(money);

    if (moneyString.endsWith(".00")) {
      int centsIndex = moneyString.lastIndexOf(".00");
      if (centsIndex != -1) {
        moneyString = moneyString.substring(1, centsIndex);
      }
    }

    return "$" + moneyString;
  }
}
