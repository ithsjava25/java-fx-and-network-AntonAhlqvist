package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class ChatController {

    @FXML
    private ListView<String> messageList;

    @FXML
    private TextField messageInput;

    @FXML
    private void initialize() {

    }

    public void handleSendMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();
        messageList.getItems().add("Du skrev: " + message);
    }
}