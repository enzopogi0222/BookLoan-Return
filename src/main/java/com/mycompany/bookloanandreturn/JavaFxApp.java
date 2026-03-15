package com.mycompany.bookloanandreturn;

import com.mycompany.bookloanandreturn.Controller.MainMenu;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFxApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        new MainMenu(primaryStage);
    }
}
