package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ChatetrisController {

    @FXML
    private ImageView backgroundImage;

    @FXML

    private ImageView frameImage;

    @FXML
    TextField messageInput;

    @FXML
    private
    Pane messageLayer;

    private int messageCount = 0;

    @FXML
    void tbButton() {
        messageCount++;

        int TILE_SIZE = 64;

        for (int i = 0; i < messageLayer.getChildren().size(); i++) {
            var block = messageLayer.getChildren().get(i);
            double oldY = block.getLayoutY();
            double newY = oldY + TILE_SIZE;
            block.setLayoutY(newY);
        }

        Label tb = new Label(String.valueOf(messageCount));

        tb.setPrefSize(TILE_SIZE, TILE_SIZE);
        tb.getStyleClass().add("tetrisblock");
        tb.setLayoutX(64);
        tb.setLayoutY(64);

        messageLayer.getChildren().add(tb);
    }

    private final String serverAddress;
    private final ChatetrisModel model;

    public ChatetrisController() {
        Dotenv dotenv = Dotenv.load();
        serverAddress = dotenv.get("HOST_NAME");
        model = new ChatetrisModel(serverAddress);
    }

    @FXML
    private void initialize() {
        backgroundImage.setImage(new Image(
                getClass().getResource("/Images/Seamless_Brown_Wood_With_Light_Brown_Grid.png").toExternalForm()
        ));
        frameImage.setImage(new Image(
                getClass().getResource("/Images/Dark_Brown_Wood_Frame.png").toExternalForm()
        ));
    }

    @FXML
    Button sendButton;

    @FXML
    Button localButton;

    @FXML
    void handleLocalMessage() {
    }

    @FXML
    void handleSendMessage() {
        String message = messageInput.getText();
        model.sendMessage(message);
    }
}