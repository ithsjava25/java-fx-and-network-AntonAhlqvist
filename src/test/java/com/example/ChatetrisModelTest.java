package com.example;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
public class ChatetrisModelTest {

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
     * WireMock-test som simulerar servern.
     * Verifierar att POST skickas korrekt till /mytopic med rätt meddelande.
     */
    @Test
    @DisplayName("Given a message when sendMessage is called then POST should be sent to /mytopic")
    void sendMessagesToFakeServer(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        String fakeServerUrl = "http://localhost:" + wmRuntimeInfo.getHttpPort();
        ChatetrisModel model = new ChatetrisModel(fakeServerUrl);

        stubFor(post("/mytopic").willReturn(aResponse().withStatus(200)));

        String testMessage = "Hejsan svejsan!";
        model.sendMessage(testMessage);

        verify(postRequestedFor(urlEqualTo("/mytopic"))
                .withRequestBody(equalTo(testMessage)));
    }

    /**
     * WireMock-test som simulerar serverns JSON-ström.
     * Verifierar att mottagna "message"-event parsas, och visas i chatten.
     */
    @Test
    @DisplayName("Given JSON stream from server when receiveMessage is called then messages are parsed")
    void receiveMessagesFromFakeServer(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        String fakeServerUrl = "http://localhost:" + wmRuntimeInfo.getHttpPort();
        ChatetrisModel model = new ChatetrisModel(fakeServerUrl);

        String jsonStream = """
                {"event":"keepalive"}
                {"event":"message","message":"Hej från servern!"}
                {"event":"message","message":"Och en till!"}
                """;

        stubFor(get("/mytopic/json")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonStream)
                        .withHeader("content-type", "application/json")));

        model.receiveMessage();

        Thread.sleep(200);

        assertThat(model.getMessages())
                .contains("Hej från servern!", "Och en till!");
    }
}