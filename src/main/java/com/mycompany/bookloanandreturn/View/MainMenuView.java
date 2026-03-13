package com.mycompany.bookloanandreturn.View;

import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/** Main menu view with options for Add Book and View Book. */
public class MainMenuView {
    private final Stage stage;
    private final Button addBookButton;
    private final Button viewBookButton;

    public MainMenuView(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Book Loan and Return - Main Menu");

        Font font = Font.font("Segoe UI", 14);
        Label titleLabel = new Label("Main Menu");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setStyle(ViewStyles.LABEL_STYLE);

        addBookButton = new Button("Add Book");
        ViewStyles.styleGreenButton(addBookButton, font);
        addBookButton.setPrefWidth(160);

        viewBookButton = new Button("View Book");
        ViewStyles.styleGreenButton(viewBookButton, font);
        viewBookButton.setPrefWidth(160);

        VBox centerPanel = new VBox(12, titleLabel, addBookButton, viewBookButton);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(30, 40, 30, 40));
        centerPanel.setStyle(ViewStyles.BACKGROUND_STYLE);

        Scene scene = new Scene(centerPanel, 520, 360);
        this.stage.setScene(scene);
    }

    public void addAddBookListener(Runnable listener) {
        addBookButton.setOnAction(e -> listener.run());
    }

    public void addViewBookListener(Runnable listener) {
        viewBookButton.setOnAction(e -> listener.run());
    }

    public void show() { stage.show(); }
    public void hide() { stage.hide(); }
    public Stage getStage() { return stage; }
}
