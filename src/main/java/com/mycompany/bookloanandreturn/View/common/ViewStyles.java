package com.mycompany.bookloanandreturn.View.common;

import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Shared colors and button styling for views. */
public final class ViewStyles {
    public static final double SCENE_WIDTH = 940;
    public static final double SCENE_HEIGHT = 560;
    public static final String BACKGROUND_STYLE = "-fx-background-color: linear-gradient(to bottom, #edf7ed, #dff0d8);";
    public static final String CARD_STYLE = "-fx-background-color: white; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #c8e6c9; -fx-border-width: 1;";
    public static final String LABEL_STYLE = "-fx-text-fill: #1b5e20; -fx-font-size: 13px; -fx-font-weight: 600;";
    public static final String TITLE_STYLE = "-fx-text-fill: #1b5e20; -fx-font-size: 26px; -fx-font-weight: 800;";
    public static final String SUBTITLE_STYLE = "-fx-text-fill: #4e6e50; -fx-font-size: 13px;";
    public static final String GREEN_BUTTON_STYLE = "-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: 700; -fx-cursor: hand;";
    public static final String GREEN_BUTTON_HOVER_STYLE = "-fx-background-color: #256628; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: 700; -fx-cursor: hand;";
    public static final String INPUT_STYLE = "-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #d0e8d1; -fx-border-radius: 10; -fx-padding: 6 10 6 10;";
    public static final String TABLE_STYLE = "-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d0e8d1; -fx-border-radius: 12;";

    private ViewStyles() {}

    public static void styleGreenButton(Button button) {
        button.setStyle(GREEN_BUTTON_STYLE);
        button.setPrefHeight(36);
        button.setOnMouseEntered(e -> button.setStyle(GREEN_BUTTON_HOVER_STYLE));
        button.setOnMouseExited(e -> button.setStyle(GREEN_BUTTON_STYLE));
    }

    public static void styleGreenButton(Button button, Font font) {
        styleGreenButton(button);
        if (font != null) {
            button.setFont(font);
        }
    }

    public static void styleInput(TextField field) {
        field.setStyle(INPUT_STYLE);
        field.setPrefHeight(36);
    }

    public static void styleCard(Region region) {
        region.setStyle(CARD_STYLE);
    }

    public static void showScenePreservingState(Stage stage, Scene scene) {
        boolean wasMaximized = stage.isMaximized();
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        stage.setScene(scene);
        if (wasMaximized) {
            stage.setMaximized(true);
        } else if (currentWidth > 0 && currentHeight > 0) {
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
        }
        stage.show();
    }

    public static void showInfoAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    public static void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
