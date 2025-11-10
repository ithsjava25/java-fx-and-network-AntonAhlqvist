package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class ChatController {

    @FXML
    TextField messageInput;

    @FXML
    Button sendButton;

    @FXML
    Button localButton;

    private final String serverAddress;
    private final ChatModel model;

    public ChatController() {
        Dotenv dotenv = Dotenv.load();
        serverAddress = dotenv.get("HOST_NAME");
        model = new ChatModel(serverAddress);
    }

    @FXML
    private void initialize() {
    }

    @FXML
    void handleLocalMessage() {
    }

    @FXML
    void handleSendMessage() {
        String message = messageInput.getText();
        model.sendMessage(message);
    }
}