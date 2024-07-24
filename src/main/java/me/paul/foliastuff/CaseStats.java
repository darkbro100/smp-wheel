package me.paul.foliastuff;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CaseStats {

  private static final Map<UUID, CaseStats> stored = new HashMap<>();

  private final Map<CaseItem.CaseRarity, Integer> caseOpens;

  @Getter
  private final UUID uuid;

  @Getter @Setter
  private Timestamp lastWheelSpin = null;

  public CaseStats(UUID player, boolean store) {
    this.uuid = player;
    this.lastWheelSpin = Timestamp.from(Instant.now());
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

  public static CaseStats[] getAll() {
    return stored.values().toArray(new CaseStats[0]);
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
    if(totalOpens() == 0)
      return 0.0;
    if(getCaseOpens(rarity) == 0)
      return 0.0;

    return (double) getCaseOpens(rarity) / totalOpens();
  }

}
