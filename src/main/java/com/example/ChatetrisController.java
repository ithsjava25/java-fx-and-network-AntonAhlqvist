package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
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

    @FXML
    void tbButton() {
        String messageText = messageInput.getText();

        int TILE_SIZE = 64;
        int charWidth = 8;
        int textLength = messageText.length();

        int steps = (textLength * charWidth + TILE_SIZE - 1) / TILE_SIZE;
        if (steps < 1) steps = 1;
        if (steps > 8) steps = 8;

        int blockWidth = steps * TILE_SIZE;

        for (int i = 0; i < messageLayer.getChildren().size(); i++) {
            var block = messageLayer.getChildren().get(i);
            block.setLayoutY(block.getLayoutY() + TILE_SIZE);
        }

        Label tb = new Label(messageText);
        tb.setPrefSize(blockWidth, TILE_SIZE);
        tb.setLayoutX(64);
        tb.setLayoutY(64);
        tb.setAlignment(Pos.CENTER);
        tb.getStyleClass().add("tetrisblock");
        tb.setTextOverrun(OverrunStyle.CLIP);

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