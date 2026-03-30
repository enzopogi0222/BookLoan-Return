package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Improved Main menu view with better visuals and icons. */
public class MainMenuView {
    private final Stage stage;
    private final Scene scene;
    private final Button addBookButton;
    private final Button viewBookButton;

    public MainMenuView(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Book Library Management System");
        this.stage.setMinWidth(ViewStyles.SCENE_WIDTH);
        this.stage.setMinHeight(ViewStyles.SCENE_HEIGHT);
        this.stage.setResizable(true);

        Font font = Font.font("Segoe UI", 16);
        
        Label titleLabel = new Label("📚 Library Manager");
        titleLabel.setStyle(ViewStyles.TITLE_STYLE);

        Label subtitleLabel = new Label("Everything you need for book management");
        subtitleLabel.setStyle(ViewStyles.SUBTITLE_STYLE);

        Label menuLabel = new Label("MAIN MENU");
        menuLabel.setStyle(ViewStyles.LABEL_STYLE + "-fx-letter-spacing: 2px; -fx-padding: 10 0 5 0;");

        addBookButton = new Button("➕ Add New Book");
        ViewStyles.styleGreenButton(addBookButton, font);
        addBookButton.setPrefWidth(280);

        viewBookButton = new Button("📂 View Catalog");
        ViewStyles.styleGreenButton(viewBookButton, font);
        viewBookButton.setPrefWidth(280);

        VBox centerPanel = new VBox(20);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(50, 60, 50, 60));
        centerPanel.setMaxWidth(500);
        ViewStyles.styleCard(centerPanel);
        
        VBox titleBox = new VBox(8, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);
        
        centerPanel.getChildren().addAll(titleBox, menuLabel, addBookButton, viewBookButton);

        StackPane root = new StackPane(centerPanel);
        root.setPadding(new Insets(30));
        root.setStyle(ViewStyles.BACKGROUND_STYLE);

        scene = new Scene(root, ViewStyles.SCENE_WIDTH, ViewStyles.SCENE_HEIGHT);
        this.stage.setScene(scene);
    }

    public void addAddBookListener(Runnable listener) {
        addBookButton.setOnAction(e -> listener.run());
    }

    public void addViewBookListener(Runnable listener) {
        viewBookButton.setOnAction(e -> listener.run());
    }

    public void show() {
        ViewStyles.showScenePreservingState(stage, scene);
    }

    public Stage getStage() { return stage; }
}
