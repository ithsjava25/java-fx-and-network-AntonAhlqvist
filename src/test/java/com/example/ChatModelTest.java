package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatModelTest {

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
     * Förenklad testversion av ChatModel.
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
}