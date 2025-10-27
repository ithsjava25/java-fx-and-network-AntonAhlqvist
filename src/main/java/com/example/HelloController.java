package com.example;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    public final HelloModel model = new HelloModel();

    @FXML
    private Label messageLabel;

    @FXML
    private Label currentDateTime;

    public Button updateButton;

    private Timeline timeline;

    public HelloModel getModel() {
        return model;
    }

    @FXML
    ListView<String> listView;

    @FXML
    private void initialize() {
        if (currentDateTime != null)
            currentDateTime.textProperty().bind(model.dateTimeProperty());

        if (messageLabel != null) {
            messageLabel.setText(model.getGreeting());
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            model.setDateTime(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd- hh:mm:ss")));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        if (currentDateTime != null)
            currentDateTime.setText(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
    }

    public void updateButtonAction(ActionEvent actionEvent) {
            currentDateTime.setText(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
    }
}