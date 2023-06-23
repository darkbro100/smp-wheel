package me.paul.foliastuff;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CaseStats {

  private static final Map<UUID, CaseStats> stored = new HashMap<>();

  private final Map<CaseItem.CaseRarity, Integer> caseOpens;

  @Getter
  private final UUID uuid;

  public CaseStats(UUID player, boolean store) {
    this.uuid = player;
    this.caseOpens = new HashMap<>();

    for (CaseItem.CaseRarity rarity : CaseItem.CaseRarity.values()) {
      caseOpens.put(rarity, 0);
    }

    if(store)
      store(uuid, this);
  }

  public CaseStats(UUID player) {
    this(player, false);
  }

  public static CaseStats get(UUID uuid) {
    return stored.get(uuid);
  }

  public static void store(UUID uuid, CaseStats stats) {
    stored.put(uuid, stats);
  }

  public static void clear(UUID uuid) {
    stored.remove(uuid);
  }

  public void addCaseOpen(CaseItem.CaseRarity rarity) {
    caseOpens.put(rarity, caseOpens.get(rarity) + 1);
  }

  public void setCaseOpens(CaseItem.CaseRarity rarity, int amount) {
    caseOpens.put(rarity, amount);
  }

  public int getCaseOpens(CaseItem.CaseRarity rarity) {
    return caseOpens.get(rarity);
  }

  public int totalOpens() {
    return caseOpens.values().stream().mapToInt(Integer::intValue).sum();
  }

  public double getChance(CaseItem.CaseRarity rarity) {
    return (double) getCaseOpens(rarity) / totalOpens();
  }

}
