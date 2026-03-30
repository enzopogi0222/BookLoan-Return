package com.mycompany.bookloanandreturn.View.common;

import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Shared colors and button styling for views. */
public final class ViewStyles {
    public static final double SCENE_WIDTH = 960;
    public static final double SCENE_HEIGHT = 600;
    
    public static final String BACKGROUND_STYLE = "-fx-background-color: linear-gradient(to bottom, #f2fbf2, #e2f2db);";
    public static final String CARD_STYLE = "-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #cde9cd; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(27,94,32,0.12), 16, 0.2, 0, 4);";
    
    public static final String LABEL_STYLE = "-fx-text-fill: #1b5e20; -fx-font-size: 13px; -fx-font-weight: 600;";
    public static final String TITLE_STYLE = "-fx-text-fill: #1b5e20; -fx-font-size: 32px; -fx-font-weight: 800;";
    public static final String SUBTITLE_STYLE = "-fx-text-fill: #4e6e50; -fx-font-size: 14px;";
    
    public static final String GREEN_BUTTON_STYLE = "-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-weight: 700; -fx-cursor: hand; -fx-transition: all 0.2s ease-in-out;";
    public static final String GREEN_BUTTON_HOVER_STYLE = "-fx-background-color: #256628; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-weight: 700; -fx-cursor: hand; -fx-scale-x: 1.02; -fx-scale-y: 1.02;";
    public static final String GREEN_BUTTON_PRESSED_STYLE = "-fx-background-color: #1b5e20; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-weight: 700; -fx-scale-x: 0.98; -fx-scale-y: 0.98;";

    public static final String SECONDARY_BUTTON_STYLE = "-fx-background-color: #e9f5e7; -fx-text-fill: #1b5e20; -fx-background-radius: 12; -fx-font-weight: 700; -fx-border-color: #cde9cd; -fx-border-radius: 12; -fx-cursor: hand;";
    public static final String SECONDARY_BUTTON_HOVER_STYLE = "-fx-background-color: #dff0dc; -fx-text-fill: #1b5e20; -fx-background-radius: 12; -fx-font-weight: 700; -fx-border-color: #bddfbe; -fx-border-radius: 12; -fx-cursor: hand; -fx-scale-x: 1.02; -fx-scale-y: 1.02;";
    
    public static final String INPUT_STYLE = "-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #d0e8d1; -fx-border-radius: 10; -fx-padding: 6 10 6 10; -fx-border-width: 1.5;";
    public static final String INPUT_FOCUS_STYLE = "-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #2e7d32; -fx-border-radius: 10; -fx-padding: 6 10 6 10; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(46,125,50,0.1), 8, 0.2, 0, 0);";
    public static final String INPUT_ERROR_STYLE = "-fx-background-color: #fff8f8; -fx-background-radius: 10; -fx-border-color: #d32f2f; -fx-border-radius: 10; -fx-padding: 6 10 6 10; -fx-border-width: 1.5;";

    public static final String TABLE_STYLE = "-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d0e8d1; -fx-border-radius: 12; -fx-selection-bar: #e8f5e9; -fx-selection-bar-non-focused: #f1f8f1;";

    private ViewStyles() {}

    public static void styleGreenButton(Button button) {
        button.setStyle(GREEN_BUTTON_STYLE);
        button.setPrefHeight(42);
        button.setOnMouseEntered(e -> button.setStyle(GREEN_BUTTON_HOVER_STYLE));
        button.setOnMouseExited(e -> button.setStyle(GREEN_BUTTON_STYLE));
        button.setOnMousePressed(e -> button.setStyle(GREEN_BUTTON_PRESSED_STYLE));
        button.setOnMouseReleased(e -> button.setStyle(button.isHover() ? GREEN_BUTTON_HOVER_STYLE : GREEN_BUTTON_STYLE));
    }

    public static void styleSecondaryButton(Button button) {
        button.setStyle(SECONDARY_BUTTON_STYLE);
        button.setPrefHeight(42);
        button.setOnMouseEntered(e -> button.setStyle(SECONDARY_BUTTON_HOVER_STYLE));
        button.setOnMouseExited(e -> button.setStyle(SECONDARY_BUTTON_STYLE));
        button.setOnMousePressed(e -> {
             button.setStyle(SECONDARY_BUTTON_HOVER_STYLE + "-fx-scale-x: 0.98; -fx-scale-y: 0.98;");
        });
        button.setOnMouseReleased(e -> button.setStyle(button.isHover() ? SECONDARY_BUTTON_HOVER_STYLE : SECONDARY_BUTTON_STYLE));
    }

    public static void styleGreenButton(Button button, Font font) {
        styleGreenButton(button);
        if (font != null) {
            button.setFont(font);
        }
    }

    public static void styleInput(TextField field) {
        field.setStyle(INPUT_STYLE);
        field.setPrefHeight(40);
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(INPUT_FOCUS_STYLE);
            } else {
                field.setStyle(INPUT_STYLE);
            }
        });
    }

    public static void styleDatePicker(DatePicker datePicker) {
        datePicker.setStyle(INPUT_STYLE);
        datePicker.setPrefHeight(40);
        datePicker.focusedProperty().addListener((obs, oldVal, newVal) -> {
            datePicker.setStyle(newVal ? INPUT_FOCUS_STYLE : INPUT_STYLE);
        });
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
