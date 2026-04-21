package com.capocann.site12.ui;

import javax.swing.*;
import javax.imageio.ImageIO;

import com.capocann.site12.Main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class resPanel extends JPanel {
    private final Image backgroundImage = new ImageIcon("assets/res/Backgrounds/sublab.jpg").getImage();
    private static final int MAX_PAN_X = 24;
    private static final int MAX_PAN_Y = 14;
    private double targetMouseX = 400;
    private double targetMouseY = 300;
    private double smoothMouseX = 400;
    private double smoothMouseY = 300;
    private int lastMouseX = 400;
    private int lastMouseY = 300;
    private boolean isMouseInsidePanel = false;

    // EDIT HERE: Adjust x, y, width, height for each rectangle below.
    private final Rectangle[] overlayRects = {
            new Rectangle(158, 227, 200, 480),
            new Rectangle(459, 259, 200, 480),
            new Rectangle(659, 285, 200, 480),
            new Rectangle(759, 259, 200, 480),
            new Rectangle(939, 292, 200, 480),
            new Rectangle(1000, 285, 200, 480)
    };
        // EDIT HERE: Set image paths for each rectangle (must match the 5 rectangles above).
        private final String[] overlayImagePaths = {
            "assets/res/Characters/Kriegs/AliveKriegs.png",
            "assets/res/Characters/Azrael/AliveAzrael.png",
            "assets/res/Characters/Gambit/AliveGambit.png",
            "assets/res/Characters/Lazarus/AliveLazarus.png",
            "assets/res/Characters/Raphaela/AliveRaphaela.png",
            "assets/res/Characters/Terry/AliveTerry.png"
        };
        private final BufferedImage[] overlayImages = loadOverlayImages();
        private int hoveredOverlayIndex = -1;

    public resPanel(Main main) {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        Timer cameraEaseTimer = new Timer(16, e -> {
            // Small lerp step gives a smoother camera follow.
            smoothMouseX += (targetMouseX - smoothMouseX) * 0.12;
            smoothMouseY += (targetMouseY - smoothMouseY) * 0.12;
            if (isMouseInsidePanel) {
                updateHoveredOverlay(lastMouseX, lastMouseY);
            }
            repaint();
        });
        cameraEaseTimer.start();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                targetMouseX = e.getX();
                targetMouseY = e.getY();
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                isMouseInsidePanel = true;
                updateHoveredOverlay(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }
        });

        addMouseListener(new MouseAdapter() {
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
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);

        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> main.showScreen("Menu"));
        add(back, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelW = getWidth();
        int panelH = getHeight();

        double xRatio = panelW == 0 ? 0.0 : smoothMouseX / panelW;
        double yRatio = panelH == 0 ? 0.0 : smoothMouseY / panelH;

        int cameraOffsetX = (int) (-MAX_PAN_X + (2 * MAX_PAN_X * xRatio));
        int cameraOffsetY = (int) (-MAX_PAN_Y + (2 * MAX_PAN_Y * yRatio));
        int drawW = panelW + (2 * MAX_PAN_X);
        int drawH = panelH + (2 * MAX_PAN_Y);

        g.drawImage(backgroundImage, cameraOffsetX, cameraOffsetY, drawW, drawH, this);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(3f));
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        for (int i = 0; i < overlayRects.length; i++) {
            Rectangle rect = getCameraShiftedRect(overlayRects[i]);
            BufferedImage img = overlayImages[i];
            if (img != null) {
                Rectangle drawRect = getFittedImageRect(i);
                g2.drawImage(img, drawRect.x, drawRect.y, drawRect.width, drawRect.height, this);
            } else {
                g2.setColor(new Color(255, 255, 255, 70));
                g2.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
            g2.setColor(i == hoveredOverlayIndex ? new Color(255, 230, 120) : Color.WHITE);
            g2.drawRect(rect.x, rect.y, rect.width, rect.height);
            g2.drawString("Rect " + (i + 1), rect.x + 8, rect.y + 18);
        }
        g2.dispose();
    }

    private BufferedImage[] loadOverlayImages() {
        BufferedImage[] images = new BufferedImage[overlayImagePaths.length];
        for (int i = 0; i < overlayImagePaths.length; i++) {
            try {
                images[i] = ImageIO.read(new File(overlayImagePaths[i]));
            } catch (IOException e) {
                images[i] = null;
            }
        }
        return images;
    }

    private Rectangle getFittedImageRect(int index) {
        Rectangle rect = getCameraShiftedRect(overlayRects[index]);
        BufferedImage img = overlayImages[index];
        if (img == null || img.getWidth() <= 0 || img.getHeight() <= 0) {
            return new Rectangle(rect);
        }

        double scale = Math.min((double) rect.width / img.getWidth(), (double) rect.height / img.getHeight());
        int scaledW = (int) Math.round(img.getWidth() * scale);
        int scaledH = (int) Math.round(img.getHeight() * scale);
        int imageX = rect.x + (rect.width - scaledW) / 2;
        int imageY = rect.y + (rect.height - scaledH) / 2;
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
        return (int) (-MAX_PAN_X + (2 * MAX_PAN_X * xRatio));
    }

    private int getCameraOffsetY() {
        int panelH = getHeight();
        if (panelH <= 0) {
            return 0;
        }
        double yRatio = smoothMouseY / panelH;
        return (int) (-MAX_PAN_Y + (2 * MAX_PAN_Y * yRatio));
    }

    private void updateHoveredOverlay(int mouseX, int mouseY) {
        int hitIndex = -1;
        for (int i = 0; i < overlayImages.length; i++) {
            if (isPointOnVisiblePixel(i, mouseX, mouseY)) {
                hitIndex = i;
                break;
            }
        }

        if (hitIndex != hoveredOverlayIndex) {
            hoveredOverlayIndex = hitIndex;
            setCursor(hitIndex >= 0 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            repaint();
        }
    }

    private boolean isPointOnVisiblePixel(int index, int mouseX, int mouseY) {
        BufferedImage img = overlayImages[index];
        if (img == null || img.getWidth() <= 0 || img.getHeight() <= 0) {
            return false;
        }

        Rectangle drawRect = getFittedImageRect(index);
        if (!drawRect.contains(mouseX, mouseY) || drawRect.width <= 0 || drawRect.height <= 0) {
            return false;
        }

        double normX = (mouseX - drawRect.x) / (double) drawRect.width;
        double normY = (mouseY - drawRect.y) / (double) drawRect.height;
        int srcX = Math.min(img.getWidth() - 1, Math.max(0, (int) (normX * img.getWidth())));
        int srcY = Math.min(img.getHeight() - 1, Math.max(0, (int) (normY * img.getHeight())));
        int alpha = (img.getRGB(srcX, srcY) >>> 24) & 0xFF;
        return alpha > 10;
    }
}
