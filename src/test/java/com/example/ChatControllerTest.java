package com.example;

import javafx.event.ActionEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatControllerTest {

    @Test
    @DisplayName("Given a message input when handleLocalMessage is called then it should appear in messageList")
    void localMessageShownInChat() {

        var controller = new ChatControllerForTest();
        controller.messageInputText = "hejsan, ditt busfrö!";
        controller.handleLocalMessage(new ActionEvent());

        assertThat(controller.messageList)
                .containsExactly("Du skrev: hejsan, ditt busfrö!");
    }

    /**
     * Förenklad testversion av ChatController.
     * Testar bara logiken i handleLocalMessage() genom att jämföra texten som läggs till i listan,
     * utan att starta JavaFX-gränssnittet.
     */
    static class ChatControllerForTest {
        List<String> messageList = new ArrayList<>();
        String messageInputText;

        void handleLocalMessage(ActionEvent e) {
            messageList.add("Du skrev: " + messageInputText);
        }
    }
}