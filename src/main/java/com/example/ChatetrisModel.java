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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ChatetrisModel {

    private final String serverAddress;
    private final String topic = "mytopic";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final ObservableList<String> messages = FXCollections.observableArrayList();
    private final AtomicBoolean receiving = new AtomicBoolean(false);
    private final Consumer<Runnable> uiExecutor;

    /**
     * Provides access to the model's observable list of messages.
     *
     * @return the ObservableList of received messages; listeners are notified when the list changes
     */
    public ObservableList<String> getMessages() {
        return messages;
    }

    /**
     * Creates a ChatetrisModel for the given server address that schedules UI updates on the JavaFX application thread.
     *
     * @param serverAddress the base URL of the server used for sending and receiving messages
     */
    public ChatetrisModel(String serverAddress) {
        this(serverAddress, Platform::runLater);
    }

    /**
     * Create a ChatetrisModel for the given server and UI executor and begin receiving messages.
     *
     * @param serverAddress the base URL of the server used for sending and receiving messages
     * @param uiExecutor a Consumer that schedules Runnables on the UI thread (used to apply message updates)
     */
    public ChatetrisModel(String serverAddress, Consumer<Runnable> uiExecutor) {
        this.serverAddress = serverAddress;
        this.uiExecutor = uiExecutor;
        receiveMessage();
    }

    /**
     * Starts an asynchronous listener for incoming messages from the configured server topic and enqueues unique messages onto the UI thread.
     *
     * If a receive operation is already in progress this method returns immediately. Incoming newline-delimited JSON lines are parsed into NtfyMessageDto; when an event equals "message", the DTO's message text is added to the model's observable message list on the UI executor only if it is not already present. Parsing errors for individual lines are ignored. The internal receiving flag is cleared when the asynchronous operation completes.
     */
    public void receiveMessage() {
        if (receiving.get()) return;
        receiving.set(true);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(serverAddress + "/" + topic + "/json"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response -> {
                    response.body().forEach(line -> {
                        try {
                            NtfyMessageDto msg = mapper.readValue(line, NtfyMessageDto.class);
                            if ("message".equals(msg.event())) {

                                Runnable task = new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!messages.contains(msg.message())) {
                                            messages.add(msg.message());
                                        }
                                    }
                                };

                                runOnUi(task);
                                System.out.println(msg);
                            }
                        } catch (Exception ignored) {}
                    });
                })
                .whenComplete((res, ex) -> receiving.set(false));
    }

    /**
     * Schedules the given Runnable to execute on the UI thread, and runs it immediately if scheduling is not possible.
     *
     * @param task the action to execute on the UI thread
     */
    private void runOnUi(Runnable task) {
        try {
            uiExecutor.accept(task);
        } catch (IllegalStateException e) {
            task.run();
        }
    }

    /**
     * Sends the given message to the configured server as a JSON payload using the model's topic.
     *
     * @return `true` if the server request completed successfully, `false` otherwise.
     */
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