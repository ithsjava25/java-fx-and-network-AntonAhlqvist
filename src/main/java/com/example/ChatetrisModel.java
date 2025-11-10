package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatetrisModel {

    private final String serverAddress;
    private final String topic = "mytopic";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final ObservableList<String> messages = FXCollections.observableArrayList();
    public ObservableList<String> getMessages() {
        return messages;
    }

    public ChatetrisModel(String serverAddress) {
        this.serverAddress = serverAddress;
        receiveMessage();
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
                                NtfyMessageDto msg = mapper.readValue(line, NtfyMessageDto.class);
                                if ("message".equals(msg.event())) {
                                    Platform.runLater(() -> messages.add(msg.message()));
                                    System.out.println(msg);
                                }
                            } catch (Exception ignored) {
                            }
                        })
                );
    }

    public boolean sendMessage(String message) {
        try {
            String jsonBody = String.format("{\"topic\": \"%s\", \"message\": \"%s\"}", topic, message);
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(serverAddress))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.discarding());
            return true;
        } catch (IOException e) {
            System.out.println("Det gick tyvärr inte att skicka meddelandet.");
        } catch (InterruptedException e) {
            System.out.println("Försöket att skicka meddelandet avbröts tyvärr.");
        }
        return false;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NtfyMessageDto(String id, long time, String event, String topic, String message) {
    }
}