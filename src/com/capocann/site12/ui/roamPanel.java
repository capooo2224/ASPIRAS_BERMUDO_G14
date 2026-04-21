package com.capocann.site12.ui;

import javax.swing.*;

import com.capocann.site12.Main;

import java.awt.*;

public class roamPanel extends JPanel {
    private final Image backgroundImage = new ImageIcon("assets/Backgrounds/background.png").getImage();

    public roamPanel(Main main) {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Tiles Placeholder Screen", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);

        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> main.showScreen("Menu"));
        add(back, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
