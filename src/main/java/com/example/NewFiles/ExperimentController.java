package com.example.NewFiles;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class ExperimentController {

    @FXML
    private AnchorPane root; // kopplas till fx:id="root" i FXML

    @FXML
    public void initialize() {
        String imageUrl = getClass().getResource("/com/example/images/LodjuretJosef.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + imageUrl + "');" +
                "-fx-background-size: cover;" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-position: center;");
    }
}