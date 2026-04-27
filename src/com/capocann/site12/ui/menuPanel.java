package com.capocann.site12.ui;

import javax.swing.*;

import com.capocann.site12.Main;

import java.awt.*;

public class menuPanel extends JPanel {
    private static final boolean SHOW_MENU_DEBUG_STATUS = true;

    private JPanel actions;
    private JLabel debugStatusLabel;

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

        actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        actions.setOpaque(false);
        actions.setVisible(true);

        JButton toRes = new JButton("Go to 60secs");
        JButton toTiles = new JButton("Go to Tiles");
        JButton toCombat = new JButton("Go to OMORI");
        JButton quit = new JButton("Quit");
        toRes.addActionListener(e -> main.showScreen("60secs"));
        toTiles.addActionListener(e -> main.showScreen("Tiles"));
        toCombat.addActionListener(e -> main.showScreen("OMORI"));
        quit.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(menuPanel.this);
            if (window != null) {
                window.dispose();
            }
            System.exit(0);
        });

        actions.add(toRes);
        actions.add(toTiles);
        actions.add(toCombat);
        actions.add(quit);
        content.add(actions, BorderLayout.SOUTH);
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
}
