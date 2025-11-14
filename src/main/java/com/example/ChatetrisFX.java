package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatetrisFX extends Application {

    /**
     * Initializes and shows the primary application window using the FXML layout.
     *
     * Sets the scene to 640×640, sets the window title to "Chatetris", shows the stage, and disables resizing.
     *
     * @param stage the primary Stage supplied by the JavaFX runtime
     * @throws Exception if the FXML resource cannot be loaded or the scene cannot be created
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatetrisFX.class.getResource("chatetris-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 640, 640);
        stage.setTitle("Chatetris");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    /**
     * Application entry point that launches the JavaFX application.
     */
    static void main() {
        launch();
    }
}