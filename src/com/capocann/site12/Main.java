package com.capocann.site12;

import javax.swing.*;

import com.capocann.site12.ui.combatPanel;
import com.capocann.site12.ui.menuPanel;
import com.capocann.site12.ui.resPanel;
import com.capocann.site12.ui.roamPanel;

import java.awt.*;

public class Main extends JFrame {
    //switches between panels(scenes)
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer  = new JPanel(cardLayout);

    public Main() {
        setTitle("Site 12");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Initialize panels
        menuPanel menu = new menuPanel(this);
        resPanel manage = new resPanel(this);
        combatPanel combat = new combatPanel(this);
        roamPanel roam = new roamPanel(this);
        

        // Add panels to the main container
        mainContainer.add(menu, "Menu");
        mainContainer.add(manage, "60secs");
        mainContainer.add(roam, "Tiles");
        mainContainer.add(combat, "OMORI");

        add(mainContainer);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    public void showScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
    }

    public static void main(String[] args) {
        new Main();
    }
}

