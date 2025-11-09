package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ChatController {

    @FXML
    VBox messageContainer;

    @FXML
    TextField messageInput;

    private final String serverAddress;
    private final ChatModel model;

    public ChatController() {
        Dotenv dotenv = Dotenv.load();
        serverAddress = dotenv.get("HOST_NAME");
        model = new ChatModel(serverAddress);
    }

    @FXML
    private void initialize() {
        messageContainer.setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 16;");
        model.receiveMessage();
    }

    @FXML
    void handleLocalMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();
        Label label = new Label("Du skrev: " + message);
        messageContainer.getChildren().add(0, label);
    }

    @FXML
    void handleSendMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();
        Label label = new Label("Du skrev: " + message);
        messageContainer.getChildren().add(label);
        model.sendMessage(message);
    }
}