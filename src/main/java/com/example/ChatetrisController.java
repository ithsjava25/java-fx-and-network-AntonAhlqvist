package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ChatetrisController {

    @FXML private ImageView backgroundImage;
    @FXML private ImageView frameImage;

    @FXML
    TextField messageInput;

    @FXML
    Button sendButton;

    @FXML
    Button localButton;

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
    void handleLocalMessage() {
    }

    @FXML
    void handleSendMessage() {
        String message = messageInput.getText();
        model.sendMessage(message);
    }
}