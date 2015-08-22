package com.caseyweed;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Preview panel for images.
 *
 * @author Casey Weed
 * @version 1.0
 */
public class PreviewPanel extends JFrame {

    /**
     * Create preview panel for a single image.
     *
     * @param image Image to be displayed
     * @param title Title for panel
     */
    public PreviewPanel(BufferedImage image, String title) {
        // set
        super(title);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(image.getWidth(), image.getHeight());

        // add content
        ImageIcon icon = new ImageIcon(image);
        JLabel label = new JLabel(icon);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.add(label);

        // add to frame and pack
        add(panel);
        pack();
    }
}
