package com.capocann.site12.ui;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.capocann.site12.Main;

import java.awt.*;

public class menuPanel extends JPanel {
    private static final boolean SHOW_MENU_DEBUG_STATUS = false;

    private JPanel actions;
    private JLabel debugStatusLabel;
    // click coordinates debug
    private boolean clickCoordsEnabled = false;
    private JPanel clickGlass;

    public menuPanel(Main main) {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.BLACK);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        add(content, BorderLayout.CENTER);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        if (SHOW_MENU_DEBUG_STATUS) {
            JPanel statusHost = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
            statusHost.setOpaque(false);
            debugStatusLabel = new JLabel();
            debugStatusLabel.setForeground(Color.WHITE);
            debugStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            statusHost.add(debugStatusLabel);
            topBar.add(statusHost, BorderLayout.WEST);
            setDebugStatus("INIT", false);
        }

        content.add(topBar, BorderLayout.NORTH);

        // Center: three resizable placeholder rectangles with image links
        content.add(createThreePlaceholders(main), BorderLayout.CENTER);
    }

    private void setDebugStatus(String playbackState, boolean buttonsVisible) {
        if (!SHOW_MENU_DEBUG_STATUS || debugStatusLabel == null) {
            return;
        }

        SwingUtilities.invokeLater(() ->
            debugStatusLabel.setText("DEBUG | " + playbackState + " | buttonsVisible=" + buttonsVisible)
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    private JPanel createThreePlaceholders(Main main) {
        JPanel host = new JPanel(new GridBagLayout());
        host.setOpaque(false);

        JLayeredPane layered = new JLayeredPane();
        layered.setLayout(null);
        layered.setOpaque(false);
        layered.setPreferredSize(new Dimension(760, 430));

        String[] imageNames = new String[]{"Title.png", "Play.png", "Quit.png"};
        // Per-image values: { height, width, x, y }
        int[][] imageValues = new int[][]{
            {240, 420, 150, -10},
            {110, 280, 240, 190},
            {110, 280, 240, 320}
        };

        List<PixelRegion> regions = new ArrayList<>();

        for (int i = 0; i < imageNames.length; i++) {
            int maxH = imageValues[i][0];
            int maxW = imageValues[i][1];
            int x = imageValues[i][2];
            int y = imageValues[i][3];

            BufferedImage src = loadMenuImageOrPlaceholder(imageNames[i], i + 1);
            double scale = Math.min(1.0, Math.min((double) maxW / src.getWidth(), (double) maxH / src.getHeight()));
            int drawW = Math.max(8, (int) Math.round(src.getWidth() * scale));
            int drawH = Math.max(8, (int) Math.round(src.getHeight() * scale));

            Image scaled = src.getScaledInstance(drawW, drawH, Image.SCALE_SMOOTH);
            BufferedImage scaledBi = new BufferedImage(drawW, drawH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaledBi.createGraphics();
            g2.drawImage(scaled, 0, 0, null);
            g2.dispose();

            JLabel imageLabel = new JLabel(new ImageIcon(scaledBi));
            imageLabel.setBounds(x, y, drawW, drawH);
            imageLabel.setOpaque(false);

            // Higher layer = drawn on top.
            int layer = 100 + (imageNames.length - i);
            layered.add(imageLabel, Integer.valueOf(layer));

            // Title is intentionally NOT clickable.
            if (!"Title.png".equalsIgnoreCase(imageNames[i])) {
                Runnable onClick;
                if ("Play.png".equalsIgnoreCase(imageNames[i])) {
                    onClick = () -> main.showScreen("60secs");
                } else {
                    onClick = () -> {
                        Window window = SwingUtilities.getWindowAncestor(menuPanel.this);
                        if (window != null) {
                            window.dispose();
                        }
                        System.exit(0);
                    };
                }
                regions.add(new PixelRegion(new Rectangle(x, y, drawW, drawH), scaledBi, onClick));
            }
        }

        layered.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Point p = e.getPoint();
                // Check top-most clickable image first.
                for (int i = regions.size() - 1; i >= 0; i--) {
                    PixelRegion region = regions.get(i);
                    if (!region.bounds.contains(p)) {
                        continue;
                    }

                    int localX = p.x - region.bounds.x;
                    int localY = p.y - region.bounds.y;
                    int argb = region.alphaMask.getRGB(localX, localY);
                    int alpha = (argb >>> 24) & 0xFF;
                    if (alpha > 10) {
                        region.onClick.run();
                        return;
                    }
                }
            }
        });

        host.add(layered);

        return host;
    }

    private BufferedImage loadMenuImageOrPlaceholder(String imageName, int index) {
        try {
            File imageFile = new File("assets/menu/" + imageName);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                Image img = icon.getImage();
                int w = Math.max(8, img.getWidth(null));
                int h = Math.max(8, img.getHeight(null));
                BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = out.createGraphics();
                g2.drawImage(img, 0, 0, null);
                g2.dispose();
                return out;
            }
        } catch (Exception ignored) {
        }

        BufferedImage fallback = new BufferedImage(240, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = fallback.createGraphics();
        g2.setColor(new Color(60, 60, 60, 220));
        g2.fillRoundRect(0, 0, fallback.getWidth(), fallback.getHeight(), 16, 16);
        g2.setColor(new Color(180, 180, 180));
        g2.drawString("Placeholder " + index + " (" + imageName + ")", 12, 24);
        g2.dispose();
        return fallback;
    }

    private static class PixelRegion {
        final Rectangle bounds;
        final BufferedImage alphaMask;
        final Runnable onClick;

        PixelRegion(Rectangle bounds, BufferedImage alphaMask, Runnable onClick) {
            this.bounds = bounds;
            this.alphaMask = alphaMask;
            this.onClick = onClick;
        }
    }
}
