package com.mycompany.bookloanandreturn.View.common;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;

/** Shared colors and button styling for views. */
public final class ViewStyles {
    public static final Color BACKGROUND = new Color(200, 230, 201);
    public static final Color LABEL = new Color(27, 94, 32);
    public static final Color BUTTON_GREEN = new Color(46, 125, 50);

    private ViewStyles() {}

    public static void styleGreenButton(JButton b) {
        b.setBackground(BUTTON_GREEN);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(true);
    }

    public static void styleGreenButton(JButton b, Font font) {
        styleGreenButton(b);
        if (font != null) b.setFont(font);
        b.setFocusPainted(false);
    }
}
