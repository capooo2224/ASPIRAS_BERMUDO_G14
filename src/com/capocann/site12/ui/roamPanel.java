package com.capocann.site12.ui;

import javax.swing.*;
import javax.imageio.ImageIO;

import com.capocann.site12.GameData;
import com.capocann.site12.Main;
import com.capocann.site12.io.InventoryCsvReader;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class roamPanel extends JPanel {
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final Color PANEL_BORDER = new Color(66, 66, 66);
    private static final Color PANEL_FILL = new Color(18, 18, 18);
    private static final Color CARD_FILL = new Color(35, 35, 35);
    private static final Color CARD_SELECTED = new Color(90, 120, 170);
    private static final Color TILE_UNEXPLORED = new Color(45, 45, 45);
    private static final Color TILE_EXPLORED = new Color(80, 95, 120);
    private static final Color TILE_BORDER = new Color(25, 25, 25);
    private static final Color PLAYER_MARKER = new Color(255, 214, 102);
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_SECONDARY = new Color(220, 220, 220);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    private static final Font CARD_TITLE_FONT = new Font("SansSerif", Font.BOLD, 15);
    private static final Font CARD_BODY_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 13);
    private static final String[] CHARACTER_ORDER = {"kriegs", "azrael", "gambit", "lazarus", "raphaela", "terry"};

    private final Image backgroundImage = new ImageIcon("assets/Backgrounds/background.png").getImage();
    private final GameData gameData = new GameData();
    private final Main main;

    private final JTextArea backpackSummaryArea = new JTextArea();
    private final JPanel portraitRosterPanel = new JPanel();
    private final JLabel infoPanelTitle = new JLabel("Backpack Summary", SwingConstants.LEFT);
    private final JPanel tileExplorerPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintTileExplorer((Graphics2D) g);
        }
    };
    private final Map<String, Integer> inventoryCounts = new HashMap<>();
    private final List<String> eventLogs = new ArrayList<>();
    private final List<LootEntry> lootTable = new ArrayList<>();
    private final Random random = new Random();

    private boolean[][] exploredTiles;
    private int mapRows = 6;
    private int mapCols = 8;
    private int playerRow = 0;
    private int playerCol = 0;
    private double combatChance = 0.35;
    private double lootChance = 0.10;

    private enum InfoMode { BACKPACK, LOGS }
    private InfoMode infoMode = InfoMode.BACKPACK;

    private static class LootEntry {
        private final String itemId;
        private final int minQty;
        private final int maxQty;
        private final int weight;

        private LootEntry(String itemId, int minQty, int maxQty, int weight) {
            this.itemId = itemId;
            this.minQty = minQty;
            this.maxQty = maxQty;
            this.weight = weight;
        }
    }

    public roamPanel(Main main) {
        this.main = main;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(new GridBagLayout());
        loadMapConfig();
        loadInventoryData();
        loadLootTable();
        initializeExplorationState();

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        GridBagConstraints leftConstraints = new GridBagConstraints();
        leftConstraints.gridx = 0;
        leftConstraints.gridy = 0;
        leftConstraints.weightx = 0.42;
        leftConstraints.weighty = 1.0;
        leftConstraints.fill = GridBagConstraints.BOTH;
        leftConstraints.insets = new Insets(16, 16, 16, 8);
        add(leftPanel, leftConstraints);

        GridBagConstraints rightConstraints = new GridBagConstraints();
        rightConstraints.gridx = 1;
        rightConstraints.gridy = 0;
        rightConstraints.weightx = 0.58;
        rightConstraints.weighty = 1.0;
        rightConstraints.fill = GridBagConstraints.BOTH;
        rightConstraints.insets = new Insets(16, 8, 16, 16);
        add(rightPanel, rightConstraints);

        refreshPartyList();
        refreshBackpackSummary();
        refreshInfoPanel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = createPanelContainer();
        leftPanel.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Party List", SwingConstants.LEFT);
        title.setForeground(TEXT_PRIMARY);
        title.setFont(TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(6, 10, 0, 10));
        leftPanel.add(title, BorderLayout.NORTH);

        portraitRosterPanel.setOpaque(false);
        portraitRosterPanel.setLayout(new BoxLayout(portraitRosterPanel, BoxLayout.Y_AXIS));
        portraitRosterPanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        refreshPartyList();

        JScrollPane rosterScroll = new JScrollPane(portraitRosterPanel);
        rosterScroll.setBorder(BorderFactory.createEmptyBorder());
        rosterScroll.getVerticalScrollBar().setUnitIncrement(16);
        rosterScroll.getViewport().setOpaque(false);
        rosterScroll.setOpaque(false);
        leftPanel.add(rosterScroll, BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton backpackButton = new JButton("Backpack");
        backpackButton.setFont(BUTTON_FONT);
        backpackButton.addActionListener(e -> {
            infoMode = InfoMode.BACKPACK;
            refreshInfoPanel();
        });
        buttonRow.add(backpackButton);

        JButton logsButton = new JButton("Logs");
        logsButton.setFont(BUTTON_FONT);
        logsButton.addActionListener(e -> {
            infoMode = InfoMode.LOGS;
            refreshInfoPanel();
        });
        buttonRow.add(logsButton);

        JButton returnButton = new JButton("Return to Res Panel");
        returnButton.setFont(BUTTON_FONT);
        returnButton.addActionListener(e -> main.showScreen("60secs"));
        buttonRow.add(returnButton);

        leftPanel.add(buttonRow, BorderLayout.SOUTH);
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        JPanel topRightPanel = createPanelContainer();
        topRightPanel.setLayout(new BorderLayout(12, 12));
        topRightPanel.add(createTopRightHeader(), BorderLayout.NORTH);
        topRightPanel.add(createTileExplorerPanel(), BorderLayout.CENTER);

        JPanel bottomRightPanel = createPanelContainer();
        bottomRightPanel.setLayout(new BorderLayout(10, 10));
        infoPanelTitle.setForeground(TEXT_PRIMARY);
        infoPanelTitle.setFont(TITLE_FONT);
        infoPanelTitle.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        bottomRightPanel.add(infoPanelTitle, BorderLayout.NORTH);

        backpackSummaryArea.setEditable(false);
        backpackSummaryArea.setOpaque(false);
        backpackSummaryArea.setForeground(TEXT_SECONDARY);
        backpackSummaryArea.setFont(CARD_BODY_FONT);
        backpackSummaryArea.setLineWrap(true);
        backpackSummaryArea.setWrapStyleWord(true);
        backpackSummaryArea.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottomRightPanel.add(backpackSummaryArea, BorderLayout.CENTER);

        GridBagConstraints topConstraints = new GridBagConstraints();
        topConstraints.gridx = 0;
        topConstraints.gridy = 0;
        topConstraints.weightx = 1.0;
        topConstraints.weighty = 0.74;
        topConstraints.fill = GridBagConstraints.BOTH;
        topConstraints.insets = new Insets(0, 0, 8, 0);
        rightPanel.add(topRightPanel, topConstraints);

        GridBagConstraints bottomConstraints = new GridBagConstraints();
        bottomConstraints.gridx = 0;
        bottomConstraints.gridy = 1;
        bottomConstraints.weightx = 1.0;
        bottomConstraints.weighty = 0.26;
        bottomConstraints.fill = GridBagConstraints.BOTH;
        rightPanel.add(bottomRightPanel, bottomConstraints);

        return rightPanel;
    }

    private JPanel createTopRightHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Exploration Tiles", SwingConstants.LEFT);
        title.setForeground(TEXT_PRIMARY);
        title.setFont(TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        header.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("Click adjacent tiles (1 step)", SwingConstants.RIGHT);
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setFont(CARD_BODY_FONT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        header.add(subtitle, BorderLayout.EAST);

        return header;
    }

    private JPanel createTileExplorerPanel() {
        tileExplorerPanel.setOpaque(false);
        tileExplorerPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        tileExplorerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTileClick(e.getX(), e.getY());
            }
        });
        return tileExplorerPanel;
    }

    private JPanel createPartyCard(String characterId) {
        GameData.CharacterStats stats = gameData.getCharacterStats(characterId);
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setOpaque(true);
        card.setBackground(CARD_FILL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PANEL_BORDER, 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        JLabel portrait = new JLabel(loadPortraitIcon(characterId, 64, 64));
        portrait.setPreferredSize(new Dimension(64, 64));
        portrait.setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 1));
        card.add(portrait, BorderLayout.WEST);

        JPanel statsPanel = new JPanel();
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(formatCharacterName(characterId));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setFont(CARD_TITLE_FONT);

        JLabel healthLabel = new JLabel(buildHealthText(stats));
        healthLabel.setForeground(TEXT_SECONDARY);
        healthLabel.setFont(CARD_BODY_FONT);

        JLabel stateLabel = new JLabel(buildStateText(stats));
        stateLabel.setForeground(TEXT_SECONDARY);
        stateLabel.setFont(CARD_BODY_FONT);

        statsPanel.add(nameLabel);
        statsPanel.add(Box.createVerticalStrut(6));
        statsPanel.add(healthLabel);
        statsPanel.add(stateLabel);

        card.add(statsPanel, BorderLayout.CENTER);

        return card;
    }

    private void refreshPartyList() {
        portraitRosterPanel.removeAll();

        for (String characterId : CHARACTER_ORDER) {
            portraitRosterPanel.add(createPartyCard(characterId));
            portraitRosterPanel.add(Box.createVerticalStrut(10));
        }

        portraitRosterPanel.revalidate();
        portraitRosterPanel.repaint();
    }

    private void loadInventoryData() {
        InventoryCsvReader csvReader = new InventoryCsvReader();
        List<GameData.InventoryEntry> loadedItems = csvReader.readInventoryItems("data/inventory.csv");

        if (loadedItems.isEmpty()) {
            loadedItems = List.of(new GameData.InventoryEntry("no_data", 0));
        }

        gameData.setInventoryItems(loadedItems);
        inventoryCounts.clear();
        for (GameData.InventoryEntry item : loadedItems) {
            if (item.getQuantity() > 0) {
                inventoryCounts.put(item.getItemId(), item.getQuantity());
            }
        }
    }

    private JPanel createInventoryListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout(8, 8));
        listPanel.setOpaque(false);

        JLabel inventoryTitle = new JLabel("Inventory Items", SwingConstants.LEFT);
        inventoryTitle.setForeground(TEXT_PRIMARY);
        inventoryTitle.setFont(CARD_TITLE_FONT);

        JPanel itemsContainer = new JPanel();
        itemsContainer.setOpaque(false);
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));

        for (GameData.InventoryEntry item : gameData.getInventoryItems()) {
            if (item.getQuantity() <= 0) {
                continue;
            }

            JPanel itemCard = new JPanel(new BorderLayout());
            itemCard.setBackground(CARD_FILL);
            itemCard.setBorder(BorderFactory.createLineBorder(PANEL_BORDER, 1));
            itemCard.setPreferredSize(new Dimension(430, 52));
            itemCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

            JLabel itemNameLabel = new JLabel(formatItemNameFromId(item.getItemId()), SwingConstants.LEFT);
            itemNameLabel.setForeground(TEXT_PRIMARY);
            itemNameLabel.setFont(CARD_TITLE_FONT);
            itemNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            itemCard.add(itemNameLabel, BorderLayout.CENTER);

            JLabel quantityLabel = new JLabel("x" + item.getQuantity(), SwingConstants.RIGHT);
            quantityLabel.setForeground(TEXT_SECONDARY);
            quantityLabel.setFont(CARD_TITLE_FONT);
            quantityLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            itemCard.add(quantityLabel, BorderLayout.EAST);

            itemsContainer.add(itemCard);
            itemsContainer.add(Box.createVerticalStrut(8));
        }

        if (itemsContainer.getComponentCount() == 0) {
            JLabel emptyLabel = new JLabel("No inventory data found", SwingConstants.CENTER);
            emptyLabel.setForeground(TEXT_PRIMARY);
            itemsContainer.setLayout(new BorderLayout());
            itemsContainer.add(emptyLabel, BorderLayout.CENTER);
        }

        JScrollPane listScrollPane = new JScrollPane(itemsContainer);
        listScrollPane.setBorder(BorderFactory.createLineBorder(PANEL_BORDER, 1));
        listScrollPane.setPreferredSize(new Dimension(470, 230));
        listScrollPane.getViewport().setOpaque(false);
        listScrollPane.setOpaque(false);

        listPanel.add(inventoryTitle, BorderLayout.NORTH);
        listPanel.add(listScrollPane, BorderLayout.CENTER);
        return listPanel;
    }

    private void loadMapConfig() {
        File config = new File("data/roam_tiles.csv");
        if (!config.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(config))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.equalsIgnoreCase("key,value")) {
                    continue;
                }

                String[] parts = trimmed.split(",", 2);
                if (parts.length < 2) {
                    continue;
                }
                String key = parts[0].trim().toLowerCase();
                String value = parts[1].trim();

                switch (key) {
                    case "rows" -> mapRows = Math.max(3, parseIntOrDefault(value, mapRows));
                    case "cols" -> mapCols = Math.max(3, parseIntOrDefault(value, mapCols));
                    case "start_row" -> playerRow = Math.max(0, parseIntOrDefault(value, playerRow));
                    case "start_col" -> playerCol = Math.max(0, parseIntOrDefault(value, playerCol));
                    case "combat_chance" -> combatChance = clamp01(parseDoubleOrDefault(value, combatChance));
                    case "loot_chance" -> lootChance = clamp01(parseDoubleOrDefault(value, lootChance));
                    default -> {
                        // ignore unknown keys
                    }
                }
            }
        } catch (IOException ignored) {
            // Keep defaults when config is unreadable.
        }

        playerRow = Math.min(playerRow, mapRows - 1);
        playerCol = Math.min(playerCol, mapCols - 1);
    }

    private void loadLootTable() {
        lootTable.clear();
        File table = new File("data/roam_loot.csv");
        if (!table.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(table))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.equalsIgnoreCase("item_id,min_qty,max_qty,weight")) {
                    continue;
                }

                String[] parts = trimmed.split(",");
                if (parts.length < 4) {
                    continue;
                }

                String itemId = parts[0].trim();
                int minQty = Math.max(1, parseIntOrDefault(parts[1].trim(), 1));
                int maxQty = Math.max(minQty, parseIntOrDefault(parts[2].trim(), minQty));
                int weight = Math.max(1, parseIntOrDefault(parts[3].trim(), 1));

                if (!itemId.isBlank()) {
                    lootTable.add(new LootEntry(itemId, minQty, maxQty, weight));
                }
            }
        } catch (IOException ignored) {
            // Keep empty loot table when file is unreadable.
        }
    }

    private void initializeExplorationState() {
        exploredTiles = new boolean[mapRows][mapCols];
        exploredTiles[playerRow][playerCol] = true;
        addLog("Exploration started at tile (" + (playerCol + 1) + ", " + (playerRow + 1) + ").");
    }

    private void paintTileExplorer(Graphics2D g2) {
        int width = tileExplorerPanel.getWidth();
        int height = tileExplorerPanel.getHeight();
        if (width <= 0 || height <= 0 || mapCols <= 0 || mapRows <= 0) {
            return;
        }

        int cellW = Math.max(1, width / mapCols);
        int cellH = Math.max(1, height / mapRows);

        for (int row = 0; row < mapRows; row++) {
            for (int col = 0; col < mapCols; col++) {
                int x = col * cellW;
                int y = row * cellH;
                g2.setColor(exploredTiles[row][col] ? TILE_EXPLORED : TILE_UNEXPLORED);
                g2.fillRect(x, y, cellW, cellH);
                g2.setColor(TILE_BORDER);
                g2.drawRect(x, y, cellW, cellH);
            }
        }

        int markerX = (playerCol * cellW) + (cellW / 2);
        int markerY = (playerRow * cellH) + (cellH / 2);
        int markerSize = Math.max(12, Math.min(cellW, cellH) / 2);
        g2.setColor(PLAYER_MARKER);
        g2.fillOval(markerX - markerSize / 2, markerY - markerSize / 2, markerSize, markerSize);
        g2.setColor(new Color(70, 55, 20));
        g2.drawOval(markerX - markerSize / 2, markerY - markerSize / 2, markerSize, markerSize);
    }

    private void handleTileClick(int mouseX, int mouseY) {
        int width = tileExplorerPanel.getWidth();
        int height = tileExplorerPanel.getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        int cellW = Math.max(1, width / mapCols);
        int cellH = Math.max(1, height / mapRows);
        int clickedCol = Math.max(0, Math.min(mapCols - 1, mouseX / cellW));
        int clickedRow = Math.max(0, Math.min(mapRows - 1, mouseY / cellH));

        int distance = Math.abs(clickedCol - playerCol) + Math.abs(clickedRow - playerRow);
        if (distance != 1) {
            addLog("Move blocked: you can only move one tile up/down/left/right.");
            refreshInfoPanel();
            return;
        }

        playerCol = clickedCol;
        playerRow = clickedRow;
        tileExplorerPanel.repaint();

        handleTileEvent(playerRow, playerCol);
        refreshInfoPanel();
    }

    private void handleTileEvent(int row, int col) {
        if (exploredTiles[row][col]) {
            addLog("Moved to explored tile (" + (col + 1) + ", " + (row + 1) + "). No new event.");
            return;
        }

        exploredTiles[row][col] = true;
        addLog("Entered unexplored tile (" + (col + 1) + ", " + (row + 1) + ").");

        double roll = random.nextDouble();
        if (roll < combatChance) {
            addLog("Combat encountered. Party moved to combat panel.");
            SwingUtilities.invokeLater(() -> main.showScreen("OMORI"));
            return;
        }

        if (roll < combatChance + lootChance) {
            grantRandomLoot();
            refreshBackpackSummary();
            return;
        }

        addLog("No loot found and no enemies encountered.");
    }

    private void grantRandomLoot() {
        if (lootTable.isEmpty()) {
            addLog("Loot event triggered, but loot table is empty.");
            return;
        }

        LootEntry picked = pickWeightedLoot();
        int qty = picked.minQty;
        if (picked.maxQty > picked.minQty) {
            qty += random.nextInt((picked.maxQty - picked.minQty) + 1);
        }

        inventoryCounts.merge(picked.itemId, qty, Integer::sum);
        syncInventoryToGameData();
        addLog("Loot found: " + formatItemNameFromId(picked.itemId) + " x" + qty + ".");
    }

    private LootEntry pickWeightedLoot() {
        int totalWeight = 0;
        for (LootEntry entry : lootTable) {
            totalWeight += entry.weight;
        }

        int roll = random.nextInt(Math.max(1, totalWeight));
        int cumulative = 0;
        for (LootEntry entry : lootTable) {
            cumulative += entry.weight;
            if (roll < cumulative) {
                return entry;
            }
        }
        return lootTable.get(lootTable.size() - 1);
    }

    private void syncInventoryToGameData() {
        List<GameData.InventoryEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : inventoryCounts.entrySet()) {
            if (entry.getValue() > 0) {
                entries.add(new GameData.InventoryEntry(entry.getKey(), entry.getValue()));
            }
        }
        gameData.setInventoryItems(entries);
    }

    private void refreshInfoPanel() {
        if (infoMode == InfoMode.LOGS) {
            infoPanelTitle.setText("Exploration Logs");
            StringBuilder sb = new StringBuilder();
            if (eventLogs.isEmpty()) {
                sb.append("No logs yet.");
            } else {
                for (String log : eventLogs) {
                    sb.append("- ").append(log).append('\n');
                }
            }
            backpackSummaryArea.setText(sb.toString());
        } else {
            infoPanelTitle.setText("Backpack Summary");
            refreshBackpackSummary();
        }
        backpackSummaryArea.setCaretPosition(0);
    }

    private void addLog(String message) {
        eventLogs.add(0, message);
        if (eventLogs.size() > 50) {
            eventLogs.remove(eventLogs.size() - 1);
        }
    }

    private int parseIntOrDefault(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private double parseDoubleOrDefault(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private String formatItemNameFromId(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return "Unknown Item";
        }

        String normalized = itemId;
        if (normalized.startsWith("itm_")) {
            normalized = normalized.substring(4);
        }

        String[] parts = normalized.split("_");
        StringBuilder nameBuilder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (nameBuilder.length() > 0) {
                nameBuilder.append(' ');
            }
            nameBuilder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                nameBuilder.append(part.substring(1));
            }
        }

        return nameBuilder.length() == 0 ? "Unknown Item" : nameBuilder.toString();
    }

    private String formatCharacterName(String characterId) {
        if (characterId == null || characterId.isBlank()) {
            return "Unknown";
        }

        return switch (characterId.toLowerCase()) {
            case "kriegs" -> "Kriegs";
            case "azrael" -> "Azrael";
            case "gambit" -> "Gambit";
            case "lazarus" -> "Lazarus";
            case "raphaela" -> "Raphaela";
            case "terry" -> "Terry";
            default -> Character.toUpperCase(characterId.charAt(0)) + characterId.substring(1);
        };
    }

    private String buildHealthText(GameData.CharacterStats stats) {
        if (stats == null) {
            return "HP: unknown";
        }

        return "HP: " + stats.getCurrentHealth() + " / " + stats.getMaxHealth() +
            " (" + stats.getHealthPercentage() + "%)";
    }

    private String buildStateText(GameData.CharacterStats stats) {
        if (stats == null) {
            return "State: unknown";
        }

        return stats.isAlmostDead() ? "State: almost dead" : "State: alive";
    }

    private void refreshBackpackSummary() {
        int totalItems = 0;
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<String, Integer> item : inventoryCounts.entrySet()) {
            if (item.getValue() <= 0) {
                continue;
            }

            totalItems += item.getValue();
            summary.append(formatItemNameFromId(item.getKey()))
                .append(" x")
                .append(item.getValue())
                .append('\n');
        }

        if (summary.length() == 0) {
            summary.append("No inventory data found.");
        }

        backpackSummaryArea.setText("Total items: " + totalItems + "\n\n" + summary);
        backpackSummaryArea.setCaretPosition(0);
    }

    private ImageIcon loadPortraitIcon(String characterId, int width, int height) {
        String path = getPortraitPath(characterId);
        BufferedImage image = loadImage(path);
        if (image == null) {
            return null;
        }

        Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private String getPortraitPath(String characterId) {
        return switch (characterId.toLowerCase()) {
            case "kriegs" -> "assets/res/Characters/Kriegs/PFPKriegs.png";
            case "azrael" -> "assets/res/Characters/Azrael/PFPAzrael.png";
            case "gambit" -> "assets/res/Characters/Gambit/PFPGambit.png";
            case "lazarus" -> "assets/res/Characters/Lazarus/PFPLazarus.png";
            case "raphaela" -> "assets/res/Characters/raphaela/PFPRaphaela.png";
            case "terry" -> "assets/res/Characters/terry/PFPTerry.png";
            default -> null;
        };
    }

    private BufferedImage loadImage(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }

        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            return null;
        }
    }

    private JPanel createPanelContainer() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(PANEL_FILL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PANEL_BORDER, 2),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        return panel;
    }
}