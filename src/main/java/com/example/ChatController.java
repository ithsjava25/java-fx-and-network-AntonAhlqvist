package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatController {

    @FXML
    ListView<String> messageList;
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
        messageList.setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 16;");
        messageList.setItems(model.getMessages());
        model.receiveMessage();
    }

    @FXML
    void handleLocalMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();
        messageList.getItems().add("Du skrev: " + message);
    }

    @FXML
    void handleSendMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();
        messageList.getItems().add("Du skrev: " + message);
        model.sendMessage(message);
    }
}