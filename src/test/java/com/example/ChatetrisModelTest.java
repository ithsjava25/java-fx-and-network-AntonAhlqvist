package com.example;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
public class ChatetrisModelTest {

    /**
     * Startar JavaFX-plattformen en gång före alla tester.
     * <p>
     * Krävs för att ObservableList och Platform.runLater ska fungera i tester.
     * <p>
     * OBS: Om testet körs i CI och JavaFX inte kan startas, hoppar denna metod över
     * uppstarten. Tester som är beroende av JavaFX måste då skippas eller
     * hanteras separat.
     */
    @BeforeAll
    static void initJavaFx() {
        try {
            if (System.getenv("CI") == null) {
                Platform.startup(() -> {
                });
            } else {
                System.out.println("Kör i CI (headless) — hoppar över JavaFX-start");
            }
        } catch (IllegalStateException | UnsupportedOperationException ignored) {
        }
    }

    /**
     * Minimal spy som fångar skickade meddelanden.
     */
    static class ChatConnectionSpy {
        List<String> sentMessages = new ArrayList<>();

        boolean send(String message) {
            sentMessages.add(message);
            return true;
        }
    }

    /**
     * Förenklad testversion av ChatetrisModel.
     * Testar bara logiken i sendMessage() utan riktiga HTTP-anrop.
     */
    static class ChatModelForTest {
        String messageToSend;
        ChatConnectionSpy connection;

        ChatModelForTest(ChatConnectionSpy connection) {
            this.connection = connection;
        }

        void sendMessage() {
            connection.send(messageToSend);
        }
    }

    /**
     * Testar den förenklade sendMessage-logiken i modellen.
     * Verifierar att ett meddelande som sätts på modellen faktiskt skickas
     * till den "spion"-kopplade ChatConnection.
     */
    @Test
    @DisplayName("Given a message when sendMessage is called then it should be sent via connection")
    void messagesSentToServer() {

        var spy = new ChatConnectionSpy();
        var model = new ChatModelForTest(spy);
        model.messageToSend = "Hejsan, servern!";

        model.sendMessage();

        assertThat(spy.sentMessages)
                .containsExactly("Hejsan, servern!");
    }

    /**
     * WireMock-test som simulerar servern.
     * <p>
     * Verifierar att POST skickas korrekt till root "/" med rätt meddelande.
     * <p>
     * JSON byggs nu med Jackson för korrekt hantering av specialtecken,
     * och `verify(...).withRequestBody(equalToJson(...))` används för att jämföra
     * innehållet utan att bry sig om ordning på fälten.
     */
    @Test
    @DisplayName("Given a message when sendMessage is called then POST should be sent to /")
    void sendMessagesToFakeServer(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        String fakeServerUrl = "http://localhost:" + wmRuntimeInfo.getHttpPort();
        ChatetrisModel model = new ChatetrisModel(fakeServerUrl) {
            @Override
            public void receiveMessage() {
            }
        };

        stubFor(post("/").willReturn(aResponse().withStatus(200)));

        String testMessage = "Hejsan \"svejsan\"!";
        model.sendMessage(testMessage);

        String expectedJson = new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(java.util.Map.of("topic", "mytopic", "message", testMessage));

        verify(postRequestedFor(urlEqualTo("/"))
                .withRequestBody(equalToJson(expectedJson)));
    }

    /**
     * Testar att ChatetrisModel korrekt tar emot och parsar meddelanden från en NDJSON-ström.
     * <p>
     * 1. WireMock används för att simulera servern med en ström av JSON-events.
     * <p>
     * 2. En CountDownLatch används för att vänta tills de förväntade meddelandena
     * har lagts till i modellens ObservableList.
     * <p>
     * 3. Testet verifierar att listan innehåller exakt de meddelanden som skickades,
     * i rätt ordning.
     * <p>
     * 4. UI-uppdateringar körs direkt i testtråden via Runnable::run för att kunna
     * köra testet synkront.
     */
    @Test
    @DisplayName("Given JSON stream from server when receiveMessage is called then messages are parsed")
    void receiveMessagesFromFakeServer(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        String fakeServerUrl = "http://localhost:" + wmRuntimeInfo.getHttpPort();

        ChatetrisModel model = new ChatetrisModel(fakeServerUrl, Runnable::run);

        CountDownLatch latch = new CountDownLatch(2);
        model.getMessages().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) latch.countDown();
            }
        });

        String jsonStream = """
                {"event":"keepalive"}
                {"event":"message","message":"Hej från servern!"}
                {"event":"message","message":"och en till gång! :-)"}
                """;

        stubFor(get("/mytopic/json")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonStream)
                        .withHeader("content-type", "application/x-ndjson")));

        model.receiveMessage();

        boolean receivedAll = latch.await(10, TimeUnit.SECONDS);
        assertThat(receivedAll).as("Alla meddelanden mottagna!").isTrue();

        assertThat(model.getMessages())
                .containsExactly("Hej från servern!", "och en till gång! :-)");
    }
}