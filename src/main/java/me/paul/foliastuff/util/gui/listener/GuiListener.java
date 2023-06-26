package me.paul.foliastuff.util.gui.listener;

import me.paul.foliastuff.util.gui.GuiButton;
import me.paul.foliastuff.util.gui.GuiPage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class GuiListener implements Listener {

  @EventHandler
  public void onInvClick(InventoryClickEvent event) {
    InventoryView view = event.getView();
    Inventory top = view.getTopInventory();
    Inventory bottom = view.getBottomInventory();
    Inventory clicked = event.getClickedInventory();

    if ((top != null && GuiPage.get(top) != null) || (bottom != null && GuiPage.get(bottom) != null)) {
//			event.setCancelled(true);

      GuiPage page = GuiPage.get(event.getInventory());

      if (page == null)
        return;

      if(page.getMainClickListener() != null)
        page.getMainClickListener().accept(event);

      if(!top.equals(clicked))
        return;

      GuiButton button = page.getButton(event.getSlot());

      if (button == null)
        return;
      if (button.getListener() == null)
        return;


      //uncancel the event, let the guibutton handle it all
      event.setCancelled(false);
      button.getListener().onClick(event);
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    if (GuiPage.get(event.getInventory()) == null)
      return;
    GuiPage page = GuiPage.get(event.getInventory());
    Player player = (Player) event.getPlayer();

    if (page.hasPlayer(player))
      GuiPage.guipages.get(page).remove(player.getUniqueId());
  }

}
