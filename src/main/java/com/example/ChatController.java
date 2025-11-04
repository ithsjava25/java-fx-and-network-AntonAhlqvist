package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class ChatController {

    private final ChatModel model = new ChatModel();

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        if (messageLabel != null) {
            messageLabel.setText(model.getGreeting());
        }
    }
}
