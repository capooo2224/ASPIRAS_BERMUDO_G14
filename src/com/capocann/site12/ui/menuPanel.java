package com.capocann.site12.ui;

import javax.swing.*;

import com.capocann.site12.Main;

import java.awt.*;

public class menuPanel extends JPanel {
    private final Image backgroundImage = new ImageIcon("assets/Backgrounds/background.png").getImage();

    public menuPanel(Main main) {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Menu Placeholder", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(title, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout());
        JButton toRes = new JButton("Go to 60secs");
        JButton toTiles = new JButton("Go to Tiles");
        JButton toCombat = new JButton("Go to OMORI");

        toRes.addActionListener(e -> main.showScreen("60secs"));
        toTiles.addActionListener(e -> main.showScreen("Tiles"));
        toCombat.addActionListener(e -> main.showScreen("OMORI"));

        actions.add(toRes);
        actions.add(toTiles);
        actions.add(toCombat);
        actions.setOpaque(false);
        add(actions, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
