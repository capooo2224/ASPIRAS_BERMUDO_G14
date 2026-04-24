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
import java.io.File;
import java.io.IOException;
import java.util.List;

public class roamPanel extends JPanel {
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final Color PANEL_BORDER = new Color(66, 66, 66);
    private static final Color PANEL_FILL = new Color(18, 18, 18);
    private static final Color CARD_FILL = new Color(35, 35, 35);
    private static final Color CARD_SELECTED = new Color(90, 120, 170);
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

    private final JLabel selectedPortraitLabel = new JLabel();
    private final JLabel selectedNameLabel = new JLabel();
    private final JLabel selectedHealthLabel = new JLabel();
    private final JLabel selectedStateLabel = new JLabel();
    private final JLabel selectedPathLabel = new JLabel();
    private final JTextArea backpackSummaryArea = new JTextArea();
    private final JPanel portraitRosterPanel = new JPanel();

    private String selectedCharacterId = CHARACTER_ORDER[0];

    public roamPanel(Main main) {
        this.main = main;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(new GridBagLayout());
        loadInventoryData();

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

        refreshSelectedCharacterDetails();
        refreshBackpackSummary();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = createPanelContainer();
        leftPanel.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Portrait Roster", SwingConstants.LEFT);
        title.setForeground(TEXT_PRIMARY);
        title.setFont(TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(6, 10, 0, 10));
        leftPanel.add(title, BorderLayout.NORTH);

        portraitRosterPanel.setOpaque(false);
        portraitRosterPanel.setLayout(new BoxLayout(portraitRosterPanel, BoxLayout.Y_AXIS));
        portraitRosterPanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        refreshPortraitRoster();

        JScrollPane rosterScroll = new JScrollPane(portraitRosterPanel);
        rosterScroll.setBorder(BorderFactory.createEmptyBorder());
        rosterScroll.getVerticalScrollBar().setUnitIncrement(16);
        rosterScroll.getViewport().setOpaque(false);
        rosterScroll.setOpaque(false);
        leftPanel.add(rosterScroll, BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton backpackButton = new JButton("Backpack");
        backpackButton.setFont(BUTTON_FONT);
        backpackButton.addActionListener(e -> showBackpackDialog());
        buttonRow.add(backpackButton);

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
        topRightPanel.add(createSelectedCharacterPanel(), BorderLayout.CENTER);

        JPanel bottomRightPanel = createPanelContainer();
        bottomRightPanel.setLayout(new BorderLayout(10, 10));
        JLabel backpackTitle = new JLabel("Backpack Summary", SwingConstants.LEFT);
        backpackTitle.setForeground(TEXT_PRIMARY);
        backpackTitle.setFont(TITLE_FONT);
        backpackTitle.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        bottomRightPanel.add(backpackTitle, BorderLayout.NORTH);

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

        JLabel title = new JLabel("Character Sheet", SwingConstants.LEFT);
        title.setForeground(TEXT_PRIMARY);
        title.setFont(TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        header.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("Top right panel", SwingConstants.RIGHT);
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setFont(CARD_BODY_FONT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        header.add(subtitle, BorderLayout.EAST);

        return header;
    }

    private JPanel createSelectedCharacterPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        selectedPortraitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedPortraitLabel.setVerticalAlignment(SwingConstants.CENTER);
        selectedPortraitLabel.setBorder(BorderFactory.createLineBorder(PANEL_BORDER, 2));
        panel.add(selectedPortraitLabel, BorderLayout.WEST);

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        selectedNameLabel.setForeground(TEXT_PRIMARY);
        selectedNameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        selectedHealthLabel.setForeground(TEXT_SECONDARY);
        selectedHealthLabel.setFont(CARD_BODY_FONT);
        selectedStateLabel.setForeground(TEXT_SECONDARY);
        selectedStateLabel.setFont(CARD_BODY_FONT);
        selectedPathLabel.setForeground(new Color(180, 180, 180));
        selectedPathLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        textBlock.add(selectedNameLabel);
        textBlock.add(Box.createVerticalStrut(10));
        textBlock.add(selectedHealthLabel);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(selectedStateLabel);
        textBlock.add(Box.createVerticalStrut(8));
        textBlock.add(selectedPathLabel);

        panel.add(textBlock, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPortraitCard(String characterId) {
        GameData.CharacterStats stats = gameData.getCharacterStats(characterId);
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setOpaque(true);
        card.setBackground(selectedCharacterId.equals(characterId) ? CARD_SELECTED : CARD_FILL);
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

        MouseAdapter selectionHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedCharacterId = characterId;
                refreshSelectedCharacterDetails();
                repaint();
            }
        };
        card.addMouseListener(selectionHandler);
        portrait.addMouseListener(selectionHandler);
        statsPanel.addMouseListener(selectionHandler);
        for (Component component : statsPanel.getComponents()) {
            component.addMouseListener(selectionHandler);
        }

        return card;
    }

    private void refreshSelectedCharacterDetails() {
        GameData.CharacterStats stats = gameData.getCharacterStats(selectedCharacterId);
        selectedNameLabel.setText(formatCharacterName(selectedCharacterId));
        selectedHealthLabel.setText(buildHealthText(stats));
        selectedStateLabel.setText(buildStateText(stats));
        selectedPathLabel.setText("Image: " + (stats != null ? stats.getCurrentImagePath() : "unknown"));
        selectedPortraitLabel.setIcon(loadPortraitIcon(selectedCharacterId, 220, 220));

        refreshPortraitRoster();
    }

    private void refreshPortraitRoster() {
        portraitRosterPanel.removeAll();

        for (String characterId : CHARACTER_ORDER) {
            portraitRosterPanel.add(createPortraitCard(characterId));
            portraitRosterPanel.add(Box.createVerticalStrut(10));
        }

        portraitRosterPanel.revalidate();
        portraitRosterPanel.repaint();
    }

    private void showBackpackDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "Backpack", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PANEL_BORDER, 2),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        content.setBackground(new Color(25, 25, 25));

        JLabel title = new JLabel("Backpack", SwingConstants.CENTER);
        title.setForeground(TEXT_PRIMARY);
        title.setFont(TITLE_FONT);
        content.add(title, BorderLayout.NORTH);

        content.add(createInventoryListPanel(), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.setFont(BUTTON_FONT);
        closeButton.addActionListener(e -> dialog.dispose());
        content.add(closeButton, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void loadInventoryData() {
        InventoryCsvReader csvReader = new InventoryCsvReader();
        List<GameData.InventoryEntry> loadedItems = csvReader.readInventoryItems("data/inventory.csv");

        if (loadedItems.isEmpty()) {
            loadedItems = List.of(new GameData.InventoryEntry("no_data", 0));
        }

        gameData.setInventoryItems(loadedItems);
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
        for (GameData.InventoryEntry item : gameData.getInventoryItems()) {
            if (item.getQuantity() <= 0) {
                continue;
            }

            totalItems += item.getQuantity();
            summary.append(formatItemNameFromId(item.getItemId()))
                .append(" x")
                .append(item.getQuantity())
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