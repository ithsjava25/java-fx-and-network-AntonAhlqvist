package com.example;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatModel {

    private final String serverAddress;
    private final String topic = "mytopic";

    private final ObservableList<String> messages = FXCollections.observableArrayList();
    public ObservableList<String> getMessages() { return messages; }

    private final HttpClient client = HttpClient.newHttpClient();

    public ChatModel(String serverAdress) {
        this.serverAddress = serverAdress;
    }

    public void receiveMessage() {
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(serverAddress + "/" + topic + "/json"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response -> response.body()
                        .forEach(line -> {
                            try {
                                int start = line.indexOf("\"message\":\"") + 10;
                                int end = line.indexOf("\"", start);
                                if (start >= 10 && end > start) {
                                    String msg = line.substring(start, end);
                                    Platform.runLater(() -> messages.add(msg));
                                }
                            } catch (Exception ignored) {}
                        }));
    }

    public void sendMessage(String message) {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(serverAddress + "/" + topic))
                    .POST(HttpRequest.BodyPublishers.ofString(message))
                    .header("Cache", "no")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception ignored) {}
    }
}