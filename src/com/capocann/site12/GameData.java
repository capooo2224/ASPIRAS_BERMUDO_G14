package com.capocann.site12;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameData {
    public static class InventoryEntry {
        private final String itemId;
        private final int quantity;

        public InventoryEntry(String itemId, int quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }

        public String getItemId() {
            return itemId;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    public static class CharacterStats {
        private final String characterName;
        private int maxHealth;
        private int currentHealth;
        private final String aliveImagePath;
        private final String almostdeadImagePath;
        private static final int HEALTH_THRESHOLD = 30; // Percentage threshold to show almostdead sprite

        public CharacterStats(String characterName, int maxHealth) {
            this.characterName = characterName;
            this.maxHealth = maxHealth;
            this.currentHealth = maxHealth;

            String normalizedName = characterName.toLowerCase();
            String characterFolder = getCharacterFolder(normalizedName);

            String alivePrimary = "assets/res/Characters/" + characterFolder + "/" + normalizedName + "-AliveDay.png";
            String almostdeadPrimary = "assets/res/Characters/" + characterFolder + "/" + normalizedName + "-AlmostdeadDay.png";

            String legacyAlive = "assets/res/Characters/" + characterFolder + "/Alive" + capitalizeFirst(normalizedName) + ".png";
            String legacyAlmostdead = "assets/res/Characters/" + characterFolder + "/Almostdead" + capitalizeFirst(normalizedName) + ".png";

            if ("terry".equals(normalizedName)) {
                this.aliveImagePath = resolveExistingPath(
                    alivePrimary,
                    "assets/res/Characters/terry/raphaela-AliveDay.png",
                    legacyAlive
                );
                this.almostdeadImagePath = resolveExistingPath(
                    almostdeadPrimary,
                    "assets/res/Characters/terry/raphaela-AlmostdeadDay.png",
                    legacyAlmostdead
                );
            } else {
                this.aliveImagePath = resolveExistingPath(alivePrimary, legacyAlive);
                this.almostdeadImagePath = resolveExistingPath(almostdeadPrimary, legacyAlmostdead);
            }
        }

        private String capitalizeFirst(String str) {
            if (str == null || str.isEmpty()) return str;
            return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        }

        private String getCharacterFolder(String normalizedName) {
            return switch (normalizedName) {
                case "kriegs" -> "Kriegs";
                case "azrael" -> "Azrael";
                case "gambit" -> "Gambit";
                case "lazarus" -> "Lazarus";
                case "raphaela" -> "raphaela";
                case "terry" -> "terry";
                default -> capitalizeFirst(normalizedName);
            };
        }

        private String resolveExistingPath(String primaryPath, String... fallbackPaths) {
            if (new File(primaryPath).exists()) {
                return primaryPath;
            }

            for (String fallback : fallbackPaths) {
                if (fallback != null && !fallback.isBlank() && new File(fallback).exists()) {
                    return fallback;
                }
            }

            return primaryPath;
        }

        public String getCharacterName() {
            return characterName;
        }

        public int getMaxHealth() {
            return maxHealth;
        }

        public int getCurrentHealth() {
            return currentHealth;
        }

        public void setCurrentHealth(int health) {
            this.currentHealth = Math.max(0, Math.min(health, maxHealth));
        }

        public void takeDamage(int damage) {
            setCurrentHealth(currentHealth - damage);
        }

        public void heal(int amount) {
            setCurrentHealth(currentHealth + amount);
        }

        public int getHealthPercentage() {
            return (int) ((currentHealth / (double) maxHealth) * 100);
        }

        public String getCurrentImagePath() {
            if (getHealthPercentage() <= HEALTH_THRESHOLD) {
                return almostdeadImagePath;
            }
            return aliveImagePath;
        }

        public String getAliveImagePath() {
            return aliveImagePath;
        }

        public String getAlmostdeadImagePath() {
            return almostdeadImagePath;
        }

        public boolean isAlive() {
            return currentHealth > 0;
        }

        public boolean isAlmostDead() {
            return getHealthPercentage() <= HEALTH_THRESHOLD;
        }
    }

    private final List<InventoryEntry> inventoryItems = new ArrayList<>();
    private final Map<String, CharacterStats> characterStats = new HashMap<>();

    public GameData() {
        // Initialize default characters
        initializeCharacters();
    }

    private void initializeCharacters() {
        characterStats.put("kriegs", new CharacterStats("kriegs", 100));
        characterStats.put("azrael", new CharacterStats("azrael", 120));
        characterStats.put("gambit", new CharacterStats("gambit", 110));
        characterStats.put("lazarus", new CharacterStats("lazarus", 95));
        characterStats.put("raphaela", new CharacterStats("raphaela", 115));
        characterStats.put("terry", new CharacterStats("terry", 105));
    }

    // Inventory methods
    public List<InventoryEntry> getInventoryItems() {
        return Collections.unmodifiableList(inventoryItems);
    }

    public void setInventoryItems(List<InventoryEntry> items) {
        inventoryItems.clear();
        if (items != null) {
            inventoryItems.addAll(items);
        }
    }

    // Character stats methods
    public CharacterStats getCharacterStats(String characterName) {
        return characterStats.get(characterName.toLowerCase());
    }

    public Map<String, CharacterStats> getAllCharacterStats() {
        return Collections.unmodifiableMap(characterStats);
    }

    public void addCharacterStats(String characterName, CharacterStats stats) {
        characterStats.put(characterName.toLowerCase(), stats);
    }
}
