package com.example.NewFiles;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ExperimentFX extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ExperimentFX.class.getResource("com/example/NewFiles/experiment-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("Experimentsappen");
        stage.setScene(scene);
        stage.show();
    }

    static void main() {
        launch();
    }
}