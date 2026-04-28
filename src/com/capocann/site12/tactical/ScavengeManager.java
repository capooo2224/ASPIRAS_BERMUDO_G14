package com.capocann.site12.tactical;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ScavengeManager {
    private final Random rng = new Random();
    private int consecutiveNone = 0;
    private final Map<String,Integer> rarityWeights = new LinkedHashMap<>();

    public ScavengeManager() {
        // default weights if none loaded
        rarityWeights.put("Common", 60);
        rarityWeights.put("Rare", 20);
        rarityWeights.put("Epic", 10);
        rarityWeights.put("Legendary", 5);
    }

    public void loadRarityWeights(File csv) throws IOException {
        List<Map<String,String>> rows = TacticalCSVLoader.loadCsv(csv);
        if (rows.isEmpty()) return;
        rarityWeights.clear();
        for (Map<String,String> r: rows) {
            String name = r.getOrDefault("Rarity","Rarity");
            String w = r.getOrDefault("Weight","0");
            try { rarityWeights.put(name, Integer.parseInt(w)); } catch(Exception ex) {}
        }
    }

    public enum TileResult { LOOT, NONE, COMBAT }

    public TileResult rollTile() {
        // Pity timer: if 5 consecutive NONE, force LOOT on 5th
        if (consecutiveNone >= 4) {
            consecutiveNone = 0;
            return TileResult.LOOT;
        }
        int v = rng.nextInt(100);
        TileResult res;
        if (v < 20) res = TileResult.LOOT;
        else if (v < 80) res = TileResult.NONE;
        else res = TileResult.COMBAT;

        if (res == TileResult.NONE) consecutiveNone++; else consecutiveNone = 0;
        return res;
    }

    public String rollRarity() {
        int total = 0;
        for (int w: rarityWeights.values()) total += w;
        int pick = rng.nextInt(total);
        int acc = 0;
        for (Map.Entry<String,Integer> e: rarityWeights.entrySet()) {
            acc += e.getValue();
            if (pick < acc) return e.getKey();
        }
        return "Common";
    }
}
