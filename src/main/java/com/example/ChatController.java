package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class ChatController {

    @FXML
    private ListView<String> messageList;

    @FXML
    private TextField messageInput;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String serverAdress;
    private final String topic = "mytopic";

    public ChatController() {
        Dotenv dotenv = Dotenv.load();
        serverAdress = dotenv.get("HOST_NAME");
    }

    @FXML
    private void initialize() {

    }

    public void handleSendMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();
        messageList.getItems().add("Du skrev: " + message);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverAdress + "/" + topic))
                    .POST(HttpRequest.BodyPublishers.ofString(message))
            .header("Cache", "no")
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.discarding());

        } catch (IOException | InterruptedException e) {
            messageList.getItems().add("kunde inte skicka");
        }
    }
}