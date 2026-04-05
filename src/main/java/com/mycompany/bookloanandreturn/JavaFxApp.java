package com.mycompany.bookloanandreturn;

import com.mycompany.bookloanandreturn.Controller.MainMenu;
import com.mycompany.bookloanandreturn.View.common.ViewStyles;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class JavaFxApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Image icon = ViewStyles.loadBrandLogo();
        if (icon != null && !icon.isError()) {
            primaryStage.getIcons().add(icon);
        }
        new MainMenu(primaryStage);
    }
}
