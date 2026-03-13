package com.mycompany.bookloanandreturn.View.common;

import javafx.scene.control.Button;
import javafx.scene.text.Font;

/** Shared colors and button styling for views. */
public final class ViewStyles {
    public static final String BACKGROUND_STYLE = "-fx-background-color: #c8e6c9;";
    public static final String LABEL_STYLE = "-fx-text-fill: #1b5e20;";
    public static final String GREEN_BUTTON_STYLE = "-fx-background-color: #2e7d32; -fx-text-fill: white;";

    private ViewStyles() {}

    public static void styleGreenButton(Button button) {
        button.setStyle(GREEN_BUTTON_STYLE);
    }

    public static void styleGreenButton(Button button, Font font) {
        styleGreenButton(button);
        if (font != null) {
            button.setFont(font);
        }
    }
}
