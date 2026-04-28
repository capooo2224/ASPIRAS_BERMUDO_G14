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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class resPanel extends JPanel {
    // UI Dimensions
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    // EDIT HERE: Change these values to resize/reposition Pause and Folder icons.
    private static final int PAUSE_ICON_X = 0;
    private static final int PAUSE_ICON_Y = 0;
    private static final int PAUSE_ICON_WIDTH = 850;
    private static final int PAUSE_ICON_HEIGHT = 550;
    private static final int FOLDER_ICON_X = 80;
    private static final int FOLDER_ICON_Y = 0;
    private static final int FOLDER_ICON_WIDTH = 850;
    private static final int FOLDER_ICON_HEIGHT = 550;

    // Camera
    private static final int MAX_PAN_X = 24;
    private static final int MAX_PAN_Y = 14;
    private static final double CAMERA_LERP = 0.12;

    // Colors
    private static final Color DARK_GRAY = new Color(30, 30, 30);
    private static final Color MEDIUM_GRAY = new Color(58, 58, 58);
    private static final Color BORDER_GRAY = new Color(120, 120, 120);
    private static final Color LIGHT_GRAY = new Color(230, 230, 230);
    private static final Color BORDER_DARK = new Color(70, 70, 70);
    private static final Color HIGHLIGHT_YELLOW = new Color(255, 230, 120);
    private static final Color SEMI_WHITE = new Color(255, 255, 255, 70);

    // Fonts
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private static final Font DIALOG_TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font SMALL_FONT = new Font("SansSerif", Font.BOLD, 12);

    // Timers
    private static final int TIMER_DELAY = 16;
    private static final int BORDER_WIDTH = 2;
    private static final int BORDER_WIDTH_THICK = 3;
    private static final int PAUSE_PANEL_WIDTH = 320;
    private static final int PAUSE_PANEL_PADDING = 24;
    private static final int PAUSE_BUTTON_HEIGHT = 52;
    private static final int PAUSE_BUTTON_GAP = 14;
    private static final float PAUSE_SLIDE_SPEED = 0.12f;
    // EDIT HERE: Change these values to resize the DayIcon.
    private static final int DAY_ICON_WIDTH = 850;
    private static final int DAY_ICON_HEIGHT = 550;
    private static final int DAY_ICON_PADDING = 0;

    // Dialog UI
    private static final int DIALOG_H_GAP = 0;
    private static final int DIALOG_V_GAP = 12;
    private static final int DIALOG_PADDING = 16;
    private static final int DIALOG_BUTTON_GAP = 8;
    private static final int GRID_ROWS = 2;
    private static final int GRID_COLS = 3;
    private static final int GRID_GAP = 10;
    private static final int GRID_ITEM_WIDTH = 150;
    private static final int GRID_ITEM_HEIGHT = 90;

    // Inventory
    private static final int INVENTORY_CARD_WIDTH = 430;
    private static final int INVENTORY_CARD_HEIGHT = 52;
    private static final int INVENTORY_SCROLL_WIDTH = 470;
    private static final int INVENTORY_SCROLL_HEIGHT = 230;
    private static final int INVENTORY_ITEM_STRUT = 8;
    private static final int INVENTORY_PADDING = 12;
    private static final int ALPHA_THRESHOLD = 10;
    private static final int DOOR_OVERLAY_INDEX = 6;

    // EDIT HERE: Per-overlay position offsets (x, y) for characters and door.
    // Index map: 0=Kriegs, 1=Azrael, 2=Gambit, 3=Lazarus, 4=Raphaela, 5=Terry, 6=Door
    private static final int KRIEGS_OFFSET_X = -30;
    private static final int KRIEGS_OFFSET_Y = 0;
    private static final int AZRAEL_OFFSET_X = -80;
    private static final int AZRAEL_OFFSET_Y = 0;
    private static final int GAMBIT_OFFSET_X = 100;
    private static final int GAMBIT_OFFSET_Y = 0;
    private static final int LAZARUS_OFFSET_X = 15;
    private static final int LAZARUS_OFFSET_Y = 0;
    private static final int RAPHAELA_OFFSET_X = 15;
    private static final int RAPHAELA_OFFSET_Y = 0;
    private static final int TERRY_OFFSET_X = -10;
    private static final int TERRY_OFFSET_Y = 0;
    private static final int DOOR_OFFSET_X = -35;
    private static final int DOOR_OFFSET_Y = 0;
    // EDIT HERE: Azrael hitbox trim values (in pixels) to refine click area.
    private static final int AZRAEL_HIT_TRIM_LEFT = 18;
    private static final int AZRAEL_HIT_TRIM_TOP = 12;
    private static final int AZRAEL_HIT_TRIM_RIGHT = 26;
    private static final int AZRAEL_HIT_TRIM_BOTTOM = 14;

    // EDIT HERE: Character info panel sizing and placement.
    private static final double CHARACTER_INFO_HEIGHT_RATIO = 0.50;
    private static final int CHARACTER_INFO_SIDE_MARGIN = 0;
    private static final int CHARACTER_INFO_BOTTOM_MARGIN = 0;
    private static final int CHARACTER_INFO_EXTRA_HEIGHT = 0;
    private static final int CHARACTER_INFO_PADDING = 18;
    private static final int CHARACTER_INFO_CORNER_RADIUS = 16;
    private static final int CHARACTER_INFO_TOGGLE_SIZE = 18;
    private static final float CHARACTER_INFO_SLIDE_SPEED = 0.12f;

    // EDIT HERE: Left-side placeholder image block sizing inside character panel.
    private static final int CHARACTER_PLACEHOLDER_WIDTH = 220;
    private static final int CHARACTER_PLACEHOLDER_HEIGHT = 220;
    private static final String CHARACTER_PLACEHOLDER_IMAGE_PATH = "assets/res/UI/character-placeholder.png";

    private static final String MAP_IMAGE_PATH = "assets/res/UI/Map.png";
    private static final String MAP_HOME_IMAGE_PATH = "assets/res/UI/home.png";
    private static final String MAP_DOWN_IMAGE_PATH = "assets/res/UI/down.png";

    // Background
    private final Image backgroundImage = new ImageIcon("assets/res/Backgrounds/SublabDay-nodoor.png").getImage();
    private final Image dayIcon = new ImageIcon("assets/res/Icons/DayIcon.png").getImage();
    private final BufferedImage pauseIcon = loadUiImage("assets/res/Icons/Pause.png");
    private final BufferedImage folderIcon = loadUiImage("assets/res/Icons/Folder.png");

    // Mouse tracking
    private double targetMouseX = PANEL_WIDTH / 2.0;
    private double targetMouseY = PANEL_HEIGHT / 2.0;
    private double smoothMouseX = PANEL_WIDTH / 2.0;
    private double smoothMouseY = PANEL_HEIGHT / 2.0;
    private int lastMouseX = (int) (PANEL_WIDTH / 2.0);
    private int lastMouseY = (int) (PANEL_HEIGHT / 2.0);
    private boolean isMouseInsidePanel = false;

    // EDIT HERE: Adjust x, y, width, height for each rectangle below.
    private final Rectangle[] overlayRects = {
        new Rectangle(158, 260, 1920, 450),
        new Rectangle(459, 259, 370, 470),
        new Rectangle(1000, 150, 770, 1280),
        new Rectangle(759, 259, 370, 600),
        new Rectangle(939, 292, 370, 600),
        new Rectangle(1000, 285, 370, 600),
        new Rectangle(1000, 285, 370, 600)
    };

    // EDIT HERE: Set image paths for each rectangle (must match the rectangles above).
    private final String[] overlayImagePaths = {
        "assets/res/Characters/Kriegs/kriegs-AliveDay.png",
        "assets/res/Characters/Azrael/azrael-AliveDay.png",
        "assets/res/Characters/Gambit/gambit-AliveDay.png",
        "assets/res/Characters/Lazarus/lazarus-AliveDay.png",
        "assets/res/Characters/raphaela/raphaela-AliveDay.png",
        "assets/res/Characters/terry/terry-AliveDay.png",
        "assets/res/Backgrounds/Door.png"
    };

    private final GameData gameData = GameData.getInstance();
    private final Main main;
    private final String[] characterIds = {"kriegs", "azrael", "gambit", "lazarus", "raphaela", "terry"};
    private final String[] roamSelectableCharacterIds = {"kriegs", "azrael", "gambit", "lazarus", "raphaela", "terry"};
    private final BufferedImage[] overlayImages = loadOverlayImages();
    private final BufferedImage characterPlaceholderImage = loadUiImage(CHARACTER_PLACEHOLDER_IMAGE_PATH);
    private int hoveredOverlayIndex = -1;
    private int selectedCharacterIndex = -1;

    private Timer pauseSlideTimer;
    private float pauseMenuProgress = 0f;
    private boolean pauseMenuTargetOpen = false;
    private Rectangle pauseIconBounds = new Rectangle();
    private Rectangle folderIconBounds = new Rectangle();
    private Rectangle resumeButtonBounds = new Rectangle();
    private Rectangle newRunButtonBounds = new Rectangle();
    private Rectangle mainMenuButtonBounds = new Rectangle();
    private Timer characterInfoSlideTimer;
    private float characterInfoSlideProgress = 0f;
    private boolean characterInfoTargetVisible = false;
    private Rectangle characterInfoToggleBounds = new Rectangle();
    private Rectangle characterInfoPanelBounds = new Rectangle();
    private String pendingTargetItemId = null;
    private String pendingTargetItemName = null;
    private boolean gameOverShown = false;
    private long gameOverScore = 0L;

    public resPanel(Main main) {
        this.main = main;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(new BorderLayout());
        loadInventoryData();

        pauseSlideTimer = new Timer(TIMER_DELAY, e -> {
            float target = pauseMenuTargetOpen ? 1f : 0f;
            if (pauseMenuProgress < target) {
                pauseMenuProgress = Math.min(target, pauseMenuProgress + PAUSE_SLIDE_SPEED);
                repaint();
            } else if (pauseMenuProgress > target) {
                pauseMenuProgress = Math.max(target, pauseMenuProgress - PAUSE_SLIDE_SPEED);
                repaint();
            } else {
                pauseSlideTimer.stop();
            }
        });

        characterInfoSlideTimer = new Timer(TIMER_DELAY, e -> {
            float target = characterInfoTargetVisible ? 1f : 0f;
            if (characterInfoSlideProgress < target) {
                characterInfoSlideProgress = Math.min(target, characterInfoSlideProgress + CHARACTER_INFO_SLIDE_SPEED);
                repaint();
            } else if (characterInfoSlideProgress > target) {
                characterInfoSlideProgress = Math.max(target, characterInfoSlideProgress - CHARACTER_INFO_SLIDE_SPEED);
                repaint();
            } else {
                characterInfoSlideTimer.stop();
                if (!characterInfoTargetVisible && characterInfoSlideProgress <= 0f) {
                    characterInfoToggleBounds = new Rectangle();
                }
            }
        });

        Timer cameraEaseTimer = new Timer(TIMER_DELAY, e -> {
            smoothMouseX += (targetMouseX - smoothMouseX) * CAMERA_LERP;
            smoothMouseY += (targetMouseY - smoothMouseY) * CAMERA_LERP;
            if (isMouseInsidePanel) {
                updateHoveredOverlay(lastMouseX, lastMouseY);
            }
            repaint();
        });
        cameraEaseTimer.start();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gameOverShown) {
                    setCursor(Cursor.getDefaultCursor());
                    return;
                }
                targetMouseX = e.getX();
                targetMouseY = e.getY();
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                isMouseInsidePanel = true;
                if (isPauseMenuOpen()) {
                    hoveredOverlayIndex = -1;
                    updatePauseMenuHoverCursor(e.getX(), e.getY());
                    repaint();
                } else {
                    if (characterInfoSlideProgress > 0f && characterInfoPanelBounds.contains(e.getPoint())) {
                        hoveredOverlayIndex = -1;
                        if (characterInfoToggleBounds.contains(e.getPoint())) {
                            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        } else {
                            setCursor(Cursor.getDefaultCursor());
                        }
                        repaint();
                        return;
                    }
                    updateHoveredOverlay(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameOverShown) {
                    return;
                }
                Point click = e.getPoint();

                if (pendingTargetItemId != null) {
                    int targetIndex = findTopCharacterHitIndex(click.x, click.y);
                    if (targetIndex >= 0) {
                        applyPendingItemToCharacter(targetIndex);
                    }
                    return;
                }

                if (characterInfoSlideProgress > 0f && characterInfoToggleBounds.contains(click)) {
                    characterInfoTargetVisible = false;
                    if (!characterInfoSlideTimer.isRunning()) {
                        characterInfoSlideTimer.start();
                    }
                    return;
                }

                if (characterInfoSlideProgress > 0f && characterInfoPanelBounds.contains(click)) {
                    return;
                }

                if (isPauseMenuOpen()) {
                    if (isPauseMenuFullyOpen()) {
                        if (resumeButtonBounds.contains(click)) {
                            closePauseMenu();
                            return;
                        }
                        if (newRunButtonBounds.contains(click)) {
                            closePauseMenu();
                            main.startNewRun();
                            return;
                        }
                        if (mainMenuButtonBounds.contains(click)) {
                            closePauseMenu();
                            main.showScreen("Menu");
                            return;
                        }
                    }

                    if (isPointOnVisibleIconPixel(pauseIcon, pauseIconBounds, click.x, click.y)) {
                        togglePauseMenu();
                        return;
                    }

                    closePauseMenu();
                    return;
                }

                if (isPointOnVisibleIconPixel(pauseIcon, pauseIconBounds, click.x, click.y)) {
                    togglePauseMenu();
                    return;
                }

                int clickedCharacter = findTopCharacterHitIndex(click.x, click.y);
                if (clickedCharacter >= 0) {
                    selectedCharacterIndex = clickedCharacter;
                    characterInfoTargetVisible = true;
                    if (!characterInfoSlideTimer.isRunning()) {
                        characterInfoSlideTimer.start();
                    }
                    repaint();
                    return;
                }

                if (isPointOnVisiblePixel(DOOR_OVERLAY_INDEX, click.x, click.y)) {
                    showDoorMapDialog();
                    return;
                }

                if (isPointOnVisibleIconPixel(folderIcon, folderIconBounds, click.x, click.y)) {
                    showPlaceholderUI();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                targetMouseX = getWidth() / 2.0;
                targetMouseY = getHeight() / 2.0;
                isMouseInsidePanel = false;
                hoveredOverlayIndex = -1;
                setCursor(Cursor.getDefaultCursor());
            }
        });

        JLabel label = new JLabel("60secs Placeholder Screen", SwingConstants.CENTER);
        label.setFont(TITLE_FONT);
        add(label, BorderLayout.CENTER);

        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> this.main.showScreen("Menu"));
        add(back, BorderLayout.SOUTH);
    }

    public void resetForNewRun() {
        loadInventoryData();
        refreshOverlayImagesFromStats();
        hoveredOverlayIndex = -1;
        selectedCharacterIndex = -1;
        pauseMenuProgress = 0f;
        pauseMenuTargetOpen = false;
        pauseSlideTimer.stop();
        characterInfoTargetVisible = false;
        characterInfoSlideProgress = 0f;
        characterInfoToggleBounds = new Rectangle();
        characterInfoPanelBounds = new Rectangle();
        pendingTargetItemId = null;
        pendingTargetItemName = null;
        gameOverShown = false;
        gameOverScore = 0L;
        characterInfoSlideTimer.stop();
        setCursor(Cursor.getDefaultCursor());
        repaint();
    }

    private boolean areAllCombatCharactersDead() {
        List<String> team = gameData.getRoamTeamCharacterIds();
        if (team == null || team.isEmpty()) {
            return false;
        }

        for (String id : team) {
            GameData.CharacterStats stats = gameData.getCharacterStats(id);
            if (stats != null && stats.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private void updateGameOverState() {
        if (gameOverShown) {
            return;
        }

        if (!areAllCombatCharactersDead()) {
            return;
        }

        gameOverShown = true;
        gameOverScore = ThreadLocalRandom.current().nextLong(100_000_000L, 10_000_000_000L);
        pendingTargetItemId = null;
        pendingTargetItemName = null;
        characterInfoTargetVisible = false;
        selectedCharacterIndex = -1;
        hoveredOverlayIndex = -1;
        closePauseMenu();
        setCursor(Cursor.getDefaultCursor());
    }

    private boolean isPauseMenuOpen() {
        return pauseMenuTargetOpen || pauseMenuProgress > 0f;
    }

    private boolean isPauseMenuFullyOpen() {
        return pauseMenuProgress >= 0.99f;
    }

    private void togglePauseMenu() {
        pauseMenuTargetOpen = !pauseMenuTargetOpen;
        if (!pauseSlideTimer.isRunning()) {
            pauseSlideTimer.start();
        }
        repaint();
    }

    private void closePauseMenu() {
        pauseMenuTargetOpen = false;
        if (!pauseSlideTimer.isRunning()) {
            pauseSlideTimer.start();
        }
    }

    private void updatePauseMenuHoverCursor(int mouseX, int mouseY) {
        boolean interactive = isPointOnVisibleIconPixel(pauseIcon, pauseIconBounds, mouseX, mouseY)
            || (isPauseMenuFullyOpen() &&
                (resumeButtonBounds.contains(mouseX, mouseY)
                    || newRunButtonBounds.contains(mouseX, mouseY)
                    || mainMenuButtonBounds.contains(mouseX, mouseY)));
        setCursor(interactive ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
    }

    private BufferedImage loadUiImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            return null;
        }
    }

    private void showPlaceholderUI() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog placeholderDialog = new JDialog(owner, Dialog.ModalityType.APPLICATION_MODAL);
        placeholderDialog.setUndecorated(true);
        placeholderDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel dialogContent = new JPanel(new BorderLayout(DIALOG_H_GAP, DIALOG_V_GAP));
        dialogContent.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_DARK, BORDER_WIDTH),
            BorderFactory.createEmptyBorder(DIALOG_PADDING, DIALOG_PADDING, DIALOG_PADDING, DIALOG_PADDING)
        ));
        dialogContent.setBackground(DARK_GRAY);

        JLabel title = new JLabel("Inventory", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(DIALOG_TITLE_FONT);
        dialogContent.add(title, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 10));
        bodyPanel.setOpaque(false);

        bodyPanel.add(createInventoryListPanel(placeholderDialog), BorderLayout.CENTER);
        dialogContent.add(bodyPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> placeholderDialog.dispose());
        dialogContent.add(closeButton, BorderLayout.SOUTH);

        placeholderDialog.setContentPane(dialogContent);
        placeholderDialog.pack();
        placeholderDialog.setLocationRelativeTo(this);
        placeholderDialog.setVisible(true);
    }

    private void showDoorMapDialog() {
        BufferedImage mapImage;
        BufferedImage homeImage;
        BufferedImage downImage;
        try {
            mapImage = ImageIO.read(new File(MAP_IMAGE_PATH));
            homeImage = ImageIO.read(new File(MAP_HOME_IMAGE_PATH));
            downImage = ImageIO.read(new File(MAP_DOWN_IMAGE_PATH));
        } catch (IOException ex) {
            return;
        }

        if (mapImage == null || homeImage == null || downImage == null) {
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog mapDialog = new JDialog(owner, Dialog.ModalityType.APPLICATION_MODAL);
        mapDialog.setUndecorated(true);
        mapDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        mapDialog.setBackground(new Color(0, 0, 0, 0));

        class MapOverlayPanel extends JPanel {
            private Rectangle mapBounds = new Rectangle();
            private Rectangle homeBounds = new Rectangle();
            private Rectangle downBounds = new Rectangle();

            MapOverlayPanel() {
                setOpaque(false);

                addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if (isPointOnVisibleImagePixel(homeImage, homeBounds, e.getX(), e.getY())
                                || isPointOnVisibleImagePixel(downImage, downBounds, e.getX(), e.getY())
                                || !isPointOnVisibleMapPixel(e.getX(), e.getY())) {
                            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        } else {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    }
                });

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (isPointOnVisibleImagePixel(homeImage, homeBounds, e.getX(), e.getY())) {
                            mapDialog.dispose();
                            return;
                        }

                        if (isPointOnVisibleImagePixel(downImage, downBounds, e.getX(), e.getY())) {
                            showRoamTeamSelectionDialog(mapDialog);
                            return;
                        }

                        if (!isPointOnVisibleMapPixel(e.getX(), e.getY())) {
                            mapDialog.dispose();
                        }
                    }
                });
            }

            private boolean isPointOnVisibleMapPixel(int x, int y) {
                if (!mapBounds.contains(x, y) || mapBounds.width <= 0 || mapBounds.height <= 0) {
                    return false;
                }

                double normX = (x - mapBounds.x) / (double) mapBounds.width;
                double normY = (y - mapBounds.y) / (double) mapBounds.height;
                int srcX = Math.min(mapImage.getWidth() - 1, Math.max(0, (int) (normX * mapImage.getWidth())));
                int srcY = Math.min(mapImage.getHeight() - 1, Math.max(0, (int) (normY * mapImage.getHeight())));
                int alpha = (mapImage.getRGB(srcX, srcY) >>> 24) & 0xFF;
                return alpha > ALPHA_THRESHOLD;
            }

            private boolean isPointOnVisibleImagePixel(BufferedImage image, Rectangle drawRect, int x, int y) {
                if (image == null || drawRect == null || drawRect.width <= 0 || drawRect.height <= 0) {
                    return false;
                }
                if (!drawRect.contains(x, y)) {
                    return false;
                }

                double normX = (x - drawRect.x) / (double) drawRect.width;
                double normY = (y - drawRect.y) / (double) drawRect.height;
                int srcX = Math.min(image.getWidth() - 1, Math.max(0, (int) (normX * image.getWidth())));
                int srcY = Math.min(image.getHeight() - 1, Math.max(0, (int) (normY * image.getHeight())));
                int alpha = (image.getRGB(srcX, srcY) >>> 24) & 0xFF;
                return alpha > ALPHA_THRESHOLD;
            }

            @Override
            protected void paintComponent(Graphics g) {
                int panelW = getWidth();
                int panelH = getHeight();

                double mapScale = Math.min((double) panelW / mapImage.getWidth(), (double) panelH / mapImage.getHeight());
                int mapW = (int) Math.round(mapImage.getWidth() * mapScale);
                int mapH = (int) Math.round(mapImage.getHeight() * mapScale);
                int mapX = (panelW - mapW) / 2;
                int mapY = (panelH - mapH) / 2;
                mapBounds = new Rectangle(mapX, mapY, mapW, mapH);
                g.drawImage(mapImage, mapX, mapY, mapW, mapH, null);

                int homeW = Math.max(1, (int) Math.round(homeImage.getWidth() * mapScale));
                int homeH = Math.max(1, (int) Math.round(homeImage.getHeight() * mapScale));
                int homeX = mapX + mapW - homeW - 24;
                int homeY = mapY + 24;
                homeBounds = new Rectangle(homeX, homeY, homeW, homeH);
                g.drawImage(homeImage, homeX, homeY, homeW, homeH, null);

                int downW = Math.max(1, (int) Math.round(downImage.getWidth() * mapScale));
                int downH = Math.max(1, (int) Math.round(downImage.getHeight() * mapScale));
                int downX = mapX + ((mapW - downW) / 2);
                int downY = mapY + mapH - downH - 24;
                downBounds = new Rectangle(downX, downY, downW, downH);
                g.drawImage(downImage, downX, downY, downW, downH, null);
            }
        }

        MapOverlayPanel content = new MapOverlayPanel();
        mapDialog.setContentPane(content);
        mapDialog.getContentPane().setBackground(new Color(0, 0, 0, 0));
        mapDialog.setSize(Math.max(1, getWidth()), Math.max(1, getHeight()));
        mapDialog.setLocationRelativeTo(this);
        mapDialog.setVisible(true);
    }

    private void showRoamTeamSelectionDialog(JDialog mapDialog) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog teamDialog = new JDialog(owner, "Select Team", Dialog.ModalityType.APPLICATION_MODAL);
        teamDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        root.setBackground(DARK_GRAY);

        JLabel title = new JLabel("Select Characters for Roam Team", SwingConstants.LEFT);
        title.setForeground(Color.WHITE);
        title.setFont(DIALOG_TITLE_FONT);
        root.add(title, BorderLayout.NORTH);

        List<String> selectedDefaults = gameData.getRoamTeamCharacterIds();
        Map<String, JCheckBox> checkboxes = new LinkedHashMap<>();

        JPanel options = new JPanel();
        options.setOpaque(false);
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        for (String id : roamSelectableCharacterIds) {
            JCheckBox box = new JCheckBox(formatCharacterName(id));
            box.setOpaque(false);
            box.setForeground(Color.WHITE);
            box.setSelected(selectedDefaults.contains(id));
            checkboxes.put(id, box);
            options.add(box);
            options.add(Box.createVerticalStrut(6));
        }
        root.add(options, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> teamDialog.dispose());

        JButton confirm = new JButton("Confirm");
        confirm.addActionListener(e -> {
            List<String> selected = new ArrayList<>();
            for (Map.Entry<String, JCheckBox> entry : checkboxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    selected.add(entry.getKey());
                }
            }

            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(
                    teamDialog,
                    "Select at least one character.",
                    "Team Required",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            gameData.setRoamTeamCharacterIds(selected);
            teamDialog.dispose();
            if (mapDialog != null) {
                mapDialog.dispose();
            }
            main.showScreen("Tiles");
        });

        actions.add(cancel);
        actions.add(confirm);
        root.add(actions, BorderLayout.SOUTH);

        teamDialog.setContentPane(root);
        teamDialog.pack();
        teamDialog.setLocationRelativeTo(this);
        teamDialog.setVisible(true);
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

    private void loadInventoryData() {
        InventoryCsvReader csvReader = new InventoryCsvReader();
        List<GameData.InventoryEntry> loadedItems = csvReader.readInventoryItems("data/inventory.csv");

        if (loadedItems.isEmpty()) {
            loadedItems = List.of(new GameData.InventoryEntry("no_data", 0));
        }
        gameData.setInventoryItems(loadedItems);
    }

    private void refreshOverlayImagesFromStats() {
        BufferedImage[] refreshed = loadOverlayImages();
        int copyLen = Math.min(overlayImages.length, refreshed.length);
        System.arraycopy(refreshed, 0, overlayImages, 0, copyLen);
    }

    public void refreshFromGameState() {
        refreshOverlayImagesFromStats();
        repaint();
    }

    private JPanel createPlaceholderGrid(String labelPrefix) {
        JPanel grid = new JPanel(new GridLayout(GRID_ROWS, GRID_COLS, GRID_GAP, GRID_GAP));
        grid.setOpaque(false);
        for (int i = 1; i <= 6; i++) {
            JPanel rect = new JPanel(new BorderLayout());
            rect.setPreferredSize(new Dimension(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            rect.setBackground(MEDIUM_GRAY);
            rect.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, BORDER_WIDTH));

            JLabel rectLabel = new JLabel(labelPrefix + i, SwingConstants.CENTER);
            rectLabel.setForeground(Color.WHITE);
            rect.add(rectLabel, BorderLayout.CENTER);

            grid.add(rect);
        }
        return grid;
    }

    private JPanel createInventoryListPanel(JDialog ownerDialog) {
        JPanel listPanel = new JPanel(new BorderLayout(8, 8));
        listPanel.setOpaque(false);

        JLabel inventoryTitle = new JLabel("Inventory Items", SwingConstants.LEFT);
        inventoryTitle.setForeground(Color.WHITE);
        inventoryTitle.setFont(LABEL_FONT);

        JPanel itemsContainer = new JPanel();
        itemsContainer.setOpaque(false);
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));

        for (GameData.InventoryEntry item : gameData.getInventoryItems()) {
            if (item.getQuantity() <= 0) {
                continue;
            }

            JPanel itemCard = new JPanel(new BorderLayout(8, 0));
            itemCard.setBackground(MEDIUM_GRAY);
            itemCard.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, BORDER_WIDTH));
            itemCard.setPreferredSize(new Dimension(INVENTORY_CARD_WIDTH, INVENTORY_CARD_HEIGHT));
            itemCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, INVENTORY_CARD_HEIGHT));

            JLabel itemNameLabel = new JLabel(formatItemNameFromId(item.getItemId()), SwingConstants.LEFT);
            itemNameLabel.setForeground(Color.WHITE);
            itemNameLabel.setFont(LABEL_FONT);
            itemNameLabel.setBorder(BorderFactory.createEmptyBorder(0, INVENTORY_PADDING, 0, INVENTORY_PADDING));
            itemCard.add(itemNameLabel, BorderLayout.CENTER);

            JLabel quantityLabel = new JLabel("x" + item.getQuantity(), SwingConstants.RIGHT);
            quantityLabel.setForeground(LIGHT_GRAY);
            quantityLabel.setFont(LABEL_FONT);
            quantityLabel.setBorder(BorderFactory.createEmptyBorder(0, INVENTORY_PADDING, 0, INVENTORY_PADDING));

            JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
            rightActions.setOpaque(false);
            rightActions.add(quantityLabel);
            JButton useButton = new JButton("Use");
            useButton.addActionListener(e -> {
                pendingTargetItemId = item.getItemId();
                pendingTargetItemName = formatItemNameFromId(item.getItemId());
                if (ownerDialog != null) {
                    ownerDialog.dispose();
                }
                repaint();
            });
            rightActions.add(useButton);
            itemCard.add(rightActions, BorderLayout.EAST);

            itemsContainer.add(itemCard);
            itemsContainer.add(Box.createVerticalStrut(INVENTORY_ITEM_STRUT));
        }

        if (itemsContainer.getComponentCount() == 0) {
            JLabel emptyLabel = new JLabel("No inventory data found", SwingConstants.CENTER);
            emptyLabel.setForeground(Color.WHITE);
            itemsContainer.setLayout(new BorderLayout());
            itemsContainer.add(emptyLabel, BorderLayout.CENTER);
        }

        JScrollPane listScrollPane = new JScrollPane(itemsContainer);
        listScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, BORDER_WIDTH));
        listScrollPane.setPreferredSize(new Dimension(INVENTORY_SCROLL_WIDTH, INVENTORY_SCROLL_HEIGHT));
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        updateGameOverState();

        int panelW = getWidth();
        int panelH = getHeight();

        int cameraOffsetX = getCameraOffsetX();
        int cameraOffsetY = getCameraOffsetY();
        int drawW = panelW + (2 * MAX_PAN_X);
        int drawH = panelH + (2 * MAX_PAN_Y);

        // Anchor the enlarged background so panning never exposes white edges.
        int backgroundDrawX = cameraOffsetX - MAX_PAN_X;
        int backgroundDrawY = cameraOffsetY - MAX_PAN_Y;

        g.drawImage(backgroundImage, backgroundDrawX, backgroundDrawY, drawW, drawH, this);
        g.drawImage(
            dayIcon,
            panelW - DAY_ICON_WIDTH - DAY_ICON_PADDING,
            DAY_ICON_PADDING,
            DAY_ICON_WIDTH,
            DAY_ICON_HEIGHT,
            this
        );

        if (pauseIcon != null) {
            g.drawImage(pauseIcon, PAUSE_ICON_X, PAUSE_ICON_Y, PAUSE_ICON_WIDTH, PAUSE_ICON_HEIGHT, this);
        }
        if (folderIcon != null) {
            g.drawImage(folderIcon, FOLDER_ICON_X, FOLDER_ICON_Y, FOLDER_ICON_WIDTH, FOLDER_ICON_HEIGHT, this);
        }
        pauseIconBounds = new Rectangle(PAUSE_ICON_X, PAUSE_ICON_Y, PAUSE_ICON_WIDTH, PAUSE_ICON_HEIGHT);
        folderIconBounds = new Rectangle(FOLDER_ICON_X, FOLDER_ICON_Y, FOLDER_ICON_WIDTH, FOLDER_ICON_HEIGHT);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(BORDER_WIDTH_THICK));
        g2.setFont(SMALL_FONT);
        // Draw door first so characters render in front of it.
        drawOverlay(g2, DOOR_OVERLAY_INDEX);
        for (int i = 0; i < overlayRects.length; i++) {
            if (i == DOOR_OVERLAY_INDEX) {
                continue;
            }
            drawOverlay(g2, i);
        }
        g2.dispose();

        drawCharacterInfoPanel((Graphics2D) g, panelW, panelH);
        drawPauseMenu((Graphics2D) g, panelW, panelH);
        drawPendingTargetPrompt((Graphics2D) g, panelW, panelH);
        drawGameOverOverlay((Graphics2D) g, panelW, panelH);
    }

    private void drawGameOverOverlay(Graphics2D g, int panelW, int panelH) {
        if (!gameOverShown) {
            return;
        }

        Graphics2D ui = (Graphics2D) g.create();
        ui.setColor(new Color(0, 0, 0, 205));
        ui.fillRect(0, 0, panelW, panelH);

        String title = "GAME OVER";
        String scoreText = "SCORE: " + gameOverScore;

        ui.setColor(new Color(220, 40, 40));
        ui.setFont(new Font("SansSerif", Font.BOLD, 78));
        FontMetrics titleFm = ui.getFontMetrics();
        int titleX = (panelW - titleFm.stringWidth(title)) / 2;
        int titleY = (panelH / 2) - 24;
        ui.drawString(title, titleX, titleY);

        ui.setColor(Color.WHITE);
        ui.setFont(new Font("SansSerif", Font.BOLD, 36));
        FontMetrics scoreFm = ui.getFontMetrics();
        int scoreX = (panelW - scoreFm.stringWidth(scoreText)) / 2;
        int scoreY = titleY + 58;
        ui.drawString(scoreText, scoreX, scoreY);

        ui.dispose();
    }

    private void drawPendingTargetPrompt(Graphics2D g, int panelW, int panelH) {
        if (pendingTargetItemId == null) {
            return;
        }

        Graphics2D ui = (Graphics2D) g.create();
        ui.setColor(new Color(0, 0, 0, 140));
        ui.fillRect(0, 0, panelW, panelH);

        String text = "Click on who to use";
        ui.setFont(new Font("SansSerif", Font.BOLD, 44));
        FontMetrics fm = ui.getFontMetrics();
        int textX = (panelW - fm.stringWidth(text)) / 2;
        int textY = (panelH / 2) - 12;

        ui.setColor(new Color(20, 20, 20, 220));
        ui.fillRoundRect(textX - 24, textY - fm.getAscent() - 18, fm.stringWidth(text) + 48, fm.getHeight() + 32, 18, 18);
        ui.setColor(Color.WHITE);
        ui.drawString(text, textX, textY);

        if (pendingTargetItemName != null) {
            String itemText = "Item: " + pendingTargetItemName;
            ui.setFont(new Font("SansSerif", Font.BOLD, 20));
            FontMetrics itemFm = ui.getFontMetrics();
            int ix = (panelW - itemFm.stringWidth(itemText)) / 2;
            ui.drawString(itemText, ix, textY + 36);
        }

        ui.dispose();
    }

    private void applyPendingItemToCharacter(int targetIndex) {
        if (pendingTargetItemId == null || targetIndex < 0 || targetIndex >= characterIds.length) {
            return;
        }

        String itemId = pendingTargetItemId;
        String targetId = characterIds[targetIndex];
        GameData.CharacterStats target = gameData.getCharacterStats(targetId);
        if (target == null) {
            pendingTargetItemId = null;
            pendingTargetItemName = null;
            repaint();
            return;
        }

        if (!consumeInventoryItem(itemId, 1)) {
            pendingTargetItemId = null;
            pendingTargetItemName = null;
            repaint();
            return;
        }

        applyItemEffectToCharacter(itemId, target);
        refreshOverlayImagesFromStats();
        pendingTargetItemId = null;
        pendingTargetItemName = null;
        repaint();
    }

    private boolean consumeInventoryItem(String itemId, int amount) {
        if (itemId == null || itemId.isBlank() || amount <= 0) {
            return false;
        }

        List<GameData.InventoryEntry> updated = new ArrayList<>();
        boolean consumed = false;

        for (GameData.InventoryEntry entry : gameData.getInventoryItems()) {
            if (entry.getItemId().equalsIgnoreCase(itemId) && !consumed) {
                int nextQty = entry.getQuantity() - amount;
                consumed = entry.getQuantity() >= amount;
                if (consumed && nextQty > 0) {
                    updated.add(new GameData.InventoryEntry(entry.getItemId(), nextQty));
                } else if (!consumed) {
                    updated.add(new GameData.InventoryEntry(entry.getItemId(), entry.getQuantity()));
                }
            } else {
                updated.add(new GameData.InventoryEntry(entry.getItemId(), entry.getQuantity()));
            }
        }

        if (consumed) {
            gameData.setInventoryItems(updated);
        }
        return consumed;
    }

    private void applyItemEffectToCharacter(String itemId, GameData.CharacterStats target) {
        String key = itemId == null ? "" : itemId.toLowerCase();

        if (key.contains("bandage")) {
            healByPercent(target, 20);
            return;
        }
        if (key.contains("medkit")) {
            healByPercent(target, 35);
            return;
        }
        if (key.contains("tourniquet")) {
            healByPercent(target, 12);
            target.clearStatus(GameData.StatusEffect.BLEED);
            return;
        }
        if (key.contains("who_knows_what") || key.contains("who_knows_what_s_inside")) {
            target.heal(target.getMaxHealth());
            return;
        }
        if (key.contains("unknown_contents")) {
            damageByPercent(target, 35);
            return;
        }
        if (key.contains("questionable_contents")) {
            target.setCurrentSanity(target.getCurrentSanity() - 3);
            return;
        }
        if (key.contains("apple")) {
            target.setCurrentHunger(target.getCurrentHunger() + 10);
            target.setCurrentThirst(target.getCurrentThirst() + 15);
            target.setCurrentSanity(target.getCurrentSanity() + 1);
            return;
        }
        if (key.contains("water_bottle") || key.contains("soda")) {
            target.setCurrentThirst(target.getCurrentThirst() + 30);
            return;
        }
        if (key.contains("sanity_pill")) {
            target.setCurrentSanity(target.getCurrentSanity() + 4);
            return;
        }
        if (key.contains("vodka")) {
            target.setCurrentThirst(target.getCurrentThirst() - 5);
            target.setCurrentSanity(target.getMaxSanity());
            target.clearStatus(GameData.StatusEffect.MORALE);
            return;
        }
        if (key.contains("trauma_kit")) {
            target.heal(target.getMaxHealth());
            target.clearStatus(GameData.StatusEffect.BLEED);
            target.clearStatus(GameData.StatusEffect.CRIPPLE);
            target.clearStatus(GameData.StatusEffect.FIRE);
            target.clearStatus(GameData.StatusEffect.MORALE);
        }
    }

    private void healByPercent(GameData.CharacterStats target, int percent) {
        int amount = Math.max(1, (int) Math.round(target.getMaxHealth() * (percent / 100.0)));
        target.heal(amount);
    }

    private void damageByPercent(GameData.CharacterStats target, int percent) {
        int amount = Math.max(1, (int) Math.round(target.getMaxHealth() * (percent / 100.0)));
        target.takeDamage(amount);
    }

    private void drawCharacterInfoPanel(Graphics2D g, int panelW, int panelH) {
        if ((selectedCharacterIndex < 0 || selectedCharacterIndex >= characterIds.length)
            && characterInfoSlideProgress <= 0f) {
            characterInfoToggleBounds = new Rectangle();
            characterInfoPanelBounds = new Rectangle();
            return;
        }

        Graphics2D ui = (Graphics2D) g.create();
        int panelHeight = (int) Math.round((panelH * CHARACTER_INFO_HEIGHT_RATIO) + CHARACTER_INFO_EXTRA_HEIGHT);
        panelHeight = Math.max(120, Math.min(panelH, panelHeight));

        int x = CHARACTER_INFO_SIDE_MARGIN;
        int visibleY = panelH - panelHeight - CHARACTER_INFO_BOTTOM_MARGIN;
        int hiddenY = panelH + CHARACTER_INFO_BOTTOM_MARGIN;
        int y = (int) Math.round(hiddenY - ((hiddenY - visibleY) * characterInfoSlideProgress));
        int width = Math.max(1, panelW - (CHARACTER_INFO_SIDE_MARGIN * 2));
        characterInfoPanelBounds = new Rectangle(x, y, width, panelHeight);

        ui.setColor(new Color(15, 15, 15, 218));
        ui.fillRoundRect(x, y, width, panelHeight, CHARACTER_INFO_CORNER_RADIUS, CHARACTER_INFO_CORNER_RADIUS);
        ui.setColor(new Color(200, 200, 200, 190));
        ui.setStroke(new BasicStroke(2f));
        ui.drawRoundRect(x, y, width, panelHeight, CHARACTER_INFO_CORNER_RADIUS, CHARACTER_INFO_CORNER_RADIUS);

        int triX = x + (width / 2);
        int triTopY = y + 8;
        Polygon triangle = new Polygon(
            new int[] {triX - CHARACTER_INFO_TOGGLE_SIZE / 2, triX + CHARACTER_INFO_TOGGLE_SIZE / 2, triX},
            new int[] {triTopY, triTopY, triTopY + CHARACTER_INFO_TOGGLE_SIZE},
            3
        );
        ui.setColor(new Color(245, 245, 245, 220));
        ui.fillPolygon(triangle);
        characterInfoToggleBounds = new Rectangle(
            triX - CHARACTER_INFO_TOGGLE_SIZE / 2,
            triTopY,
            CHARACTER_INFO_TOGGLE_SIZE,
            CHARACTER_INFO_TOGGLE_SIZE
        );

        int placeX = x + CHARACTER_INFO_PADDING;
        int placeY = y + CHARACTER_INFO_PADDING + 12;
        int placeW = Math.min(CHARACTER_PLACEHOLDER_WIDTH, Math.max(80, width / 3));
        int placeH = Math.min(CHARACTER_PLACEHOLDER_HEIGHT, panelHeight - (CHARACTER_INFO_PADDING * 2));

        if (characterPlaceholderImage != null) {
            ui.drawImage(characterPlaceholderImage, placeX, placeY, placeW, placeH, null);
        } else {
            ui.setColor(new Color(50, 50, 50, 220));
            ui.fillRect(placeX, placeY, placeW, placeH);
            ui.setColor(new Color(210, 210, 210, 210));
            ui.drawRect(placeX, placeY, placeW, placeH);
            ui.setFont(LABEL_FONT);
            ui.drawString("Image Placeholder", placeX + 16, placeY + 26);
        }

        int textX = placeX + placeW + 18;
        int textY = placeY + 38;
        String name = formatCharacterName(characterIds[selectedCharacterIndex]);
        GameData.CharacterStats stats = gameData.getCharacterStats(characterIds[selectedCharacterIndex]);

        ui.setColor(Color.WHITE);
        ui.setFont(new Font("SansSerif", Font.BOLD, 28));
        ui.drawString(name, textX, textY);

        ui.setFont(new Font("SansSerif", Font.PLAIN, 16));
        int lineY = textY + 34;
        if (stats != null) {
            ui.drawString("Health: " + stats.getCurrentHealth() + " / " + stats.getMaxHealth(), textX, lineY);
            lineY += 24;
            ui.drawString("Thirst: " + stats.getCurrentThirst() + " / " + stats.getMaxThirst(), textX, lineY);
            lineY += 24;
            ui.drawString("Hunger: " + stats.getCurrentHunger() + " / " + stats.getMaxHunger(), textX, lineY);
            lineY += 24;
            ui.drawString("Sanity: " + stats.getCurrentSanity() + " / " + stats.getMaxSanity(), textX, lineY);
        } else {
            ui.drawString("Character data unavailable.", textX, lineY);
        }

        ui.dispose();
    }

    private void drawPauseMenu(Graphics2D g, int panelW, int panelH) {
        if (pauseMenuProgress <= 0f) {
            resumeButtonBounds = new Rectangle();
            newRunButtonBounds = new Rectangle();
            mainMenuButtonBounds = new Rectangle();
            return;
        }

        Graphics2D ui = (Graphics2D) g.create();
        int overlayAlpha = (int) (140 * pauseMenuProgress);
        ui.setColor(new Color(0, 0, 0, overlayAlpha));
        ui.fillRect(0, 0, panelW, panelH);

        int drawerX = (int) (-PAUSE_PANEL_WIDTH + (PAUSE_PANEL_WIDTH * pauseMenuProgress));
        ui.setColor(new Color(20, 20, 20, 220));
        ui.fillRect(drawerX, 0, PAUSE_PANEL_WIDTH, panelH);

        ui.setColor(Color.WHITE);
        ui.setFont(TITLE_FONT);
        ui.drawString("Paused", drawerX + PAUSE_PANEL_PADDING, 56);

        int buttonW = PAUSE_PANEL_WIDTH - (PAUSE_PANEL_PADDING * 2);
        int y = 100;
        resumeButtonBounds = new Rectangle(drawerX + PAUSE_PANEL_PADDING, y, buttonW, PAUSE_BUTTON_HEIGHT);
        y += PAUSE_BUTTON_HEIGHT + PAUSE_BUTTON_GAP;
        newRunButtonBounds = new Rectangle(drawerX + PAUSE_PANEL_PADDING, y, buttonW, PAUSE_BUTTON_HEIGHT);
        y += PAUSE_BUTTON_HEIGHT + PAUSE_BUTTON_GAP;
        mainMenuButtonBounds = new Rectangle(drawerX + PAUSE_PANEL_PADDING, y, buttonW, PAUSE_BUTTON_HEIGHT);

        drawPauseButton(ui, resumeButtonBounds, "Resume");
        drawPauseButton(ui, newRunButtonBounds, "New Run");
        drawPauseButton(ui, mainMenuButtonBounds, "Main Menu");
        ui.dispose();
    }

    private void drawPauseButton(Graphics2D g, Rectangle rect, String label) {
        g.setColor(new Color(55, 55, 55, 235));
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);
        g.setColor(Color.WHITE);
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

        FontMetrics fm = g.getFontMetrics(LABEL_FONT);
        int textX = rect.x + ((rect.width - fm.stringWidth(label)) / 2);
        int textY = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();
        g.setFont(LABEL_FONT);
        g.drawString(label, textX, textY);
    }

    private void drawOverlay(Graphics2D g2, int index) {
        if (index < 0 || index >= overlayRects.length || index >= overlayImages.length) {
            return;
        }

        BufferedImage img = overlayImages[index];
        if (img == null) {
            return;
        }

        Rectangle drawRect = getAdjustedOverlayRect(index);
        g2.drawImage(img, drawRect.x, drawRect.y, drawRect.width, drawRect.height, this);
    }

    private Rectangle getAdjustedOverlayRect(int index) {
        Rectangle base = getFittedImageRect(index);
        return new Rectangle(
            base.x + getOverlayOffsetX(index),
            base.y + getOverlayOffsetY(index),
            base.width,
            base.height
        );
    }

    private int getOverlayOffsetX(int index) {
        return switch (index) {
            case 0 -> KRIEGS_OFFSET_X;
            case 1 -> AZRAEL_OFFSET_X;
            case 2 -> GAMBIT_OFFSET_X;
            case 3 -> LAZARUS_OFFSET_X;
            case 4 -> RAPHAELA_OFFSET_X;
            case 5 -> TERRY_OFFSET_X;
            case 6 -> DOOR_OFFSET_X;
            default -> 0;
        };
    }

    private int getOverlayOffsetY(int index) {
        return switch (index) {
            case 0 -> KRIEGS_OFFSET_Y;
            case 1 -> AZRAEL_OFFSET_Y;
            case 2 -> GAMBIT_OFFSET_Y;
            case 3 -> LAZARUS_OFFSET_Y;
            case 4 -> RAPHAELA_OFFSET_Y;
            case 5 -> TERRY_OFFSET_Y;
            case 6 -> DOOR_OFFSET_Y;
            default -> 0;
        };
    }

    private BufferedImage[] loadOverlayImages() {
        BufferedImage[] images = new BufferedImage[overlayImagePaths.length];
        for (int i = 0; i < overlayImagePaths.length; i++) {
            String imagePath = overlayImagePaths[i];
            if (i < characterIds.length) {
                GameData.CharacterStats stats = gameData.getCharacterStats(characterIds[i]);
                if (stats != null) {
                    imagePath = stats.getCurrentImagePath();
                }
            }

            try {
                images[i] = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                images[i] = null;
            }
        }
        return images;
    }

    private Rectangle getFittedImageRect(int index) {
        BufferedImage img = overlayImages[index];
        int panelW = getWidth();
        int panelH = getHeight();

        if (panelW <= 0 || panelH <= 0) {
            return new Rectangle(0, 0, 0, 0);
        }

        if (img == null || img.getWidth() <= 0 || img.getHeight() <= 0) {
            return new Rectangle(getCameraOffsetX(), getCameraOffsetY(), panelW, panelH);
        }

        double scale = Math.min((double) panelW / img.getWidth(), (double) panelH / img.getHeight());
        int scaledW = (int) Math.round(img.getWidth() * scale);
        int scaledH = (int) Math.round(img.getHeight() * scale);
        int imageX = ((panelW - scaledW) / 2) + getCameraOffsetX();
        int imageY = ((panelH - scaledH) / 2) + getCameraOffsetY();
        return new Rectangle(imageX, imageY, scaledW, scaledH);
    }

    private Rectangle getCameraShiftedRect(Rectangle baseRect) {
        return new Rectangle(baseRect.x + getCameraOffsetX(), baseRect.y + getCameraOffsetY(), baseRect.width, baseRect.height);
    }

    private int getCameraOffsetX() {
        int panelW = getWidth();
        if (panelW <= 0) {
            return 0;
        }
        double xRatio = smoothMouseX / panelW;
        return (int) (MAX_PAN_X - (2 * MAX_PAN_X * xRatio));
    }

    private int getCameraOffsetY() {
        int panelH = getHeight();
        if (panelH <= 0) {
            return 0;
        }
        double yRatio = smoothMouseY / panelH;
        return (int) (MAX_PAN_Y - (2 * MAX_PAN_Y * yRatio));
    }

    private void updateHoveredOverlay(int mouseX, int mouseY) {
        int hitIndex = findTopCharacterHitIndex(mouseX, mouseY);
        if (hitIndex < 0 && isPointOnVisiblePixel(DOOR_OVERLAY_INDEX, mouseX, mouseY)) {
            hitIndex = DOOR_OVERLAY_INDEX;
        }

        if (hitIndex != hoveredOverlayIndex) {
            hoveredOverlayIndex = hitIndex;
            setCursor(hitIndex >= 0 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            repaint();
        }
    }

    private int findTopCharacterHitIndex(int mouseX, int mouseY) {
        for (int i = characterIds.length - 1; i >= 0; i--) {
            if (isPointOnVisiblePixel(i, mouseX, mouseY)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isPointOnVisiblePixel(int index, int mouseX, int mouseY) {
        BufferedImage img = overlayImages[index];
        if (img == null || img.getWidth() <= 0 || img.getHeight() <= 0) {
            return false;
        }

        Rectangle drawRect = getAdjustedOverlayRect(index);
        Rectangle hitRect = drawRect;
        if (index == 1) {
            int trimW = AZRAEL_HIT_TRIM_LEFT + AZRAEL_HIT_TRIM_RIGHT;
            int trimH = AZRAEL_HIT_TRIM_TOP + AZRAEL_HIT_TRIM_BOTTOM;
            int newW = Math.max(1, drawRect.width - trimW);
            int newH = Math.max(1, drawRect.height - trimH);
            hitRect = new Rectangle(drawRect.x + AZRAEL_HIT_TRIM_LEFT, drawRect.y + AZRAEL_HIT_TRIM_TOP, newW, newH);
        }

        if (!hitRect.contains(mouseX, mouseY) || drawRect.width <= 0 || drawRect.height <= 0) {
            return false;
        }

        double normX = (mouseX - drawRect.x) / (double) drawRect.width;
        double normY = (mouseY - drawRect.y) / (double) drawRect.height;
        int srcX = Math.min(img.getWidth() - 1, Math.max(0, (int) (normX * img.getWidth())));
        int srcY = Math.min(img.getHeight() - 1, Math.max(0, (int) (normY * img.getHeight())));
        int alpha = (img.getRGB(srcX, srcY) >>> 24) & 0xFF;
        return alpha > ALPHA_THRESHOLD;
    }

    private boolean isPointOnVisibleIconPixel(BufferedImage icon, Rectangle drawRect, int mouseX, int mouseY) {
        if (icon == null || drawRect == null || drawRect.width <= 0 || drawRect.height <= 0) {
            return false;
        }
        if (!drawRect.contains(mouseX, mouseY)) {
            return false;
        }

        double normX = (mouseX - drawRect.x) / (double) drawRect.width;
        double normY = (mouseY - drawRect.y) / (double) drawRect.height;
        int srcX = Math.min(icon.getWidth() - 1, Math.max(0, (int) (normX * icon.getWidth())));
        int srcY = Math.min(icon.getHeight() - 1, Math.max(0, (int) (normY * icon.getHeight())));
        int alpha = (icon.getRGB(srcX, srcY) >>> 24) & 0xFF;
        return alpha > ALPHA_THRESHOLD;
    }
}
