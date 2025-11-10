package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.awt.*;

public class ChatController {

    @FXML
    VBox messageContainer;

    @FXML
    TextField messageInput;

    @FXML
    StackPane messageStack;

    @FXML
    ImageView backgroundPattern;

    @FXML
    ImageView frameImage;

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
        backgroundPattern.setImage(
                new Image("file:C:/Users/anton/IdeaProjects/java-fx-and-network-AntonAhlqvist/src/main/resources/Images/tiles_background.png")
        );
        backgroundPattern.fitWidthProperty().bind(messageStack.widthProperty());
        backgroundPattern.fitHeightProperty().bind(messageStack.heightProperty());

        frameImage.setImage(
                new Image("file:C:/Users/anton/IdeaProjects/java-fx-and-network-AntonAhlqvist/src/main/resources/Images/frame_overlay.png")
        );
        frameImage.fitWidthProperty().bind(messageStack.widthProperty());
        frameImage.fitHeightProperty().bind(messageStack.heightProperty());
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