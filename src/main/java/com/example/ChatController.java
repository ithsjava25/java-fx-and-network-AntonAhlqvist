package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ChatController {

    @FXML
    VBox messageContainer;

    @FXML
    TextField messageInput;

    @FXML
    StackPane messageStack;

    @FXML
    ImageView backgroundPattern;

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
        backgroundPattern.fitWidthProperty().bind(messageStack.widthProperty());
        backgroundPattern.fitHeightProperty().bind(messageStack.heightProperty());
        model.receiveMessage();
    }

    @FXML
    void handleLocalMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();
        Label label = new Label("Du skrev: " + message);
        label.setStyle("-fx-background-color: burlywood; -fx-padding: 6; -fx-background-radius: 4;");
        messageContainer.getChildren().add(0, label);
    }

    @FXML
    void handleSendMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();
        Label label = new Label("Du skrev: " + message);
        label.setStyle("-fx-background-color: peru; -fx-padding: 6; -fx-background-radius: 4;");
        messageContainer.getChildren().add(label);
        model.sendMessage(message);
    }
}