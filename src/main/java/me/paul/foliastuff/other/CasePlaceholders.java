package me.paul.foliastuff.other;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.paul.foliastuff.CaseStats;
import me.paul.foliastuff.util.Util;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CasePlaceholders extends PlaceholderExpansion {
  @Override
  public @NotNull String getIdentifier() {
    return "placeholder_cases";
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
  public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
    if (params.equalsIgnoreCase("name")) {
      return player.getName();
    } else if (params.equalsIgnoreCase("player_total_opens")) {
      CaseStats stats = CaseStats.get(player.getUniqueId());
      return Util.format(stats.totalOpens());
    } else if (params.equalsIgnoreCase("total_emeralds_spent")) {
      CaseStats stats = CaseStats.get(player.getUniqueId());
      Util.format(stats.totalOpens() * 3);
    } else if (params.equalsIgnoreCase("total_opens")) {
      int total = 0;
      for (CaseStats stats : CaseStats.getAll())
        total += stats.totalOpens();
      return Util.format(total);
    } else if (params.equalsIgnoreCase("server_total_emeralds_spent")) {
      int total = 0;
      for (CaseStats stats : CaseStats.getAll())
        total += (stats.totalOpens() * 3);
      return Util.format(total);
    }

    return null;
  }
}
