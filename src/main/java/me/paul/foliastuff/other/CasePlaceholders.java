package me.paul.foliastuff.other;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.paul.foliastuff.CaseItem;
import me.paul.foliastuff.CaseStats;
import me.paul.foliastuff.util.Util;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class CasePlaceholders extends PlaceholderExpansion {

  /**
   * This method should always return true unless we
   * have a dependency we need to make sure is on the server
   * for our placeholders to work!
   * This expansion does not require a dependency so we will always return true
   */
  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public @NotNull String getIdentifier() {
    return "wheel";
  }

  @Override
  public @NotNull String getAuthor() {
    return "paulyg";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0";
  }

  @Override
  public @Nullable String onRequest(final OfflinePlayer player, @NotNull String params) {
    //System.out.println("onREquest called with params: " + params);

    if (params.equalsIgnoreCase("name")) {
      return player.getName();
    } else if (params.equalsIgnoreCase("player_total_opens")) {
      CaseStats stats = CaseStats.get(player.getUniqueId());
      return Util.format(stats.totalOpens());
    } else if (params.equalsIgnoreCase("total_emeralds_spent")) {
      //System.out.println("HELLO");
      CaseStats stats = CaseStats.get(player.getUniqueId());
      return Util.format(stats.totalOpens() * 1);
    } else if (params.equalsIgnoreCase("total_opens")) {
      int total = 0;
      for (CaseStats stats : CaseStats.getAll())
        total += stats.totalOpens();
      return Util.format(total);
    } else if (params.equalsIgnoreCase("server_total_emeralds_spent")) {
      int total = 0;
      for (CaseStats stats : CaseStats.getAll())
        total += (stats.totalOpens() * 1);
      return Util.format(total);
    }

    for (CaseItem.CaseRarity rarity : CaseItem.CaseRarity.values()) {
      String name = "total_" + rarity.name().toLowerCase() + "_opens";
      String name2 = rarity.name().toLowerCase() + "_total_opens";
      //System.out.println(name2);
      String name3 = "total_" + rarity.name().toLowerCase() + "_percentage";
      String name4 = rarity.name().toLowerCase() + "_total_percentage";

      if(params.equalsIgnoreCase(name)) {
        CaseStats stats = CaseStats.get(player.getUniqueId());
        return Util.format(stats.getCaseOpens(rarity));
      } else if(params.equalsIgnoreCase(name2)) {
        int total = 0;
        for (CaseStats stats : CaseStats.getAll())
          total += stats.getCaseOpens(rarity);

        return Util.format(total);
      } else if(params.equalsIgnoreCase(name3)) {
        double totalRarityOpens = 0;
        double total = 0;
        for (CaseStats caseStats : CaseStats.getAll()) {
          total += caseStats.totalOpens();
          totalRarityOpens += caseStats.getCaseOpens(rarity);
        }
        double chance = totalRarityOpens / total;
        if(totalRarityOpens == 0 || total == 0)
          chance = 0;

        DecimalFormat df = new DecimalFormat("##.##");
        return df.format(chance * 100);
      } else if(params.equalsIgnoreCase(name4)) {
        CaseStats stats = CaseStats.get(player.getUniqueId());
        DecimalFormat df = new DecimalFormat("##.##");
        return df.format(stats.getChance(rarity) * 100);
      }
    }

    return null;
  }
}
