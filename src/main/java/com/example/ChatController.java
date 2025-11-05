package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatController {

    @FXML
    private ListView<String> messageList;
    @FXML
    private TextField messageInput;

    private final String serverAdress;
    private final ChatModel model;

    public ChatController() {
        Dotenv dotenv = Dotenv.load();
        serverAdress = dotenv.get("HOST_NAME");
        model = new ChatModel(serverAdress);
    }

    @FXML
    private void initialize() {
        messageList.setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 16;");
        messageList.setItems(model.getMessages());
        model.receiveMessage();
    }

    @FXML
    private void handleLocalMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();
        messageList.getItems().add("Du skrev " + message);
    }

    @FXML
    private void handleSendMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();
        messageList.getItems().add("Du skrev: " + message);
        model.sendMessage(message);
    }
}