package com.capocann.site12.ui;

import javax.swing.*;

import com.capocann.site12.Main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class resPanel extends JPanel {
    private final Image backgroundImage = new ImageIcon("assets/res/Backgrounds/sublab.jpg").getImage();
    private double targetMouseX = 400;
    private double targetMouseY = 300;
    private double smoothMouseX = 400;
    private double smoothMouseY = 300;

    // EDIT HERE: Adjust x, y, width, height for each rectangle below.
    // These are screen-space overlays and are NOT affected by the camera pan.
    private final Rectangle[] overlayRects = {
            new Rectangle(70, 120, 625, 1080),
            new Rectangle(200, 120, 625, 1080),
            new Rectangle(330, 120, 625, 1080),
            new Rectangle(460, 120, 625, 1080),
            new Rectangle(590, 120, 625, 1080)
    };
        // EDIT HERE: Set image paths for each rectangle (must match the 5 rectangles above).
        private final String[] overlayImagePaths = {
            "assets/res/Characters/kriegs_helth.jpg",
            "assets/res/Characters/noFilter.webp",
            "assets/res/Characters/sublab.jpg",
            "assets/res/Characters/kriegs_helth.jpg",
            "assets/res/Characters/noFilter.webp"
        };
        private final Image[] overlayImages = {
            new ImageIcon(overlayImagePaths[0]).getImage(),
            new ImageIcon(overlayImagePaths[1]).getImage(),
            new ImageIcon(overlayImagePaths[2]).getImage(),
            new ImageIcon(overlayImagePaths[3]).getImage(),
            new ImageIcon(overlayImagePaths[4]).getImage()
        };

    public resPanel(Main main) {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        Timer cameraEaseTimer = new Timer(16, e -> {
            // Small lerp step gives a smoother camera follow.
            smoothMouseX += (targetMouseX - smoothMouseX) * 0.12;
            smoothMouseY += (targetMouseY - smoothMouseY) * 0.12;
            repaint();
        });
        cameraEaseTimer.start();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                targetMouseX = e.getX();
                targetMouseY = e.getY();
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
        int maxPanX = 24;
        int maxPanY = 14;

        double xRatio = panelW == 0 ? 0.0 : smoothMouseX / panelW;
        double yRatio = panelH == 0 ? 0.0 : smoothMouseY / panelH;

        int drawX = (int) (-maxPanX + (2 * maxPanX * xRatio));
        int drawY = (int) (-maxPanY + (2 * maxPanY * yRatio));
        int drawW = panelW + (2 * maxPanX);
        int drawH = panelH + (2 * maxPanY);

        g.drawImage(backgroundImage, drawX, drawY, drawW, drawH, this);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(3f));
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        for (int i = 0; i < overlayRects.length; i++) {
            Rectangle rect = overlayRects[i];
            Image img = overlayImages[i];
            if (img.getWidth(this) > 0) {
                g2.drawImage(img, rect.x, rect.y, rect.width, rect.height, this);
            } else {
                g2.setColor(new Color(255, 255, 255, 70));
                g2.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
            g2.setColor(Color.WHITE);
            g2.drawRect(rect.x, rect.y, rect.width, rect.height);
            g2.drawString("Rect " + (i + 1), rect.x + 8, rect.y + 18);
        }
        g2.dispose();
    }
}
