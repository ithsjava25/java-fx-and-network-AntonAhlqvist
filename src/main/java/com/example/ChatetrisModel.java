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
import java.util.Map;
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
     * Returnerar den observerbara listan med meddelanden. Eftersom listan
     * delas direkt (inte som en kopia) kan UI-komponenter binda sig till den
     * och uppdateras automatiskt när nya meddelanden tillkommer.
     */
    public ObservableList<String> getMessages() {
        return messages;
    }

    /**
     * Anropar den andra konstruktorn och skickar vidare samma
     * serveradress tillsammans med runLater som uiExecutor.
     * Detta gör att modellen direkt blir redo att användas i en
     * JavaFX-applikation utan ytterligare konfiguration.
     */
    public ChatetrisModel(String serverAddress) {
        this(serverAddress, Platform::runLater);
    }

    /**
     * Skapar modellen med angiven serveradress och en valfri mekanism för
     * UI-uppdateringar. Startar direkt lyssningen på inkommande meddelanden.
     */
    public ChatetrisModel(String serverAddress, Consumer<Runnable> uiExecutor) {
        this.serverAddress = serverAddress;
        this.uiExecutor = uiExecutor;
        receiveMessage();
    }

    /**
     * I korthet: denna metod kopplar upp sig mot Ntfy-servern, lyssnar på den
     * kontinuerliga JSON-strömmen av inkommande meddelanden och ser till att de
     * hamnar i JavaFX-tråden så att UI kan uppdateras korrekt.
     * <p>
     * 1. Säkerställer att endast en lyssning kan vara aktiv åt gången genom att
     * använda en flagga. Om en lyssning redan pågår avslutas metoden direkt.
     * <p>
     * 2. Skickar en asynkron HTTP-förfrågan till serverns "/json"-ända, där varje
     * rad i svaret representerar ett nytt meddelande i realtid. För varje rad
     * försöker metoden översätta JSON-datan till ett Ntfy-MessageDto-objekt och
     * plockar ut textinnehållet om händelsetypen är "message".
     * <p>
     * 3. Varje nytt meddelande skickas vidare till JavaFX-tråden via runOnUi().
     * Där läggs det till i listan om det inte redan finns, vilket förhindrar
     * dubbletter om servern råkar skicka samma rad flera gånger.
     * <p>
     * 4. När lyssningen avslutas – oavsett om det beror på ett fel eller att
     * anslutningen stängs – återställs flaggan så att en ny lyssning kan
     * startas igen.
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

                                Runnable task = () -> messages.add(msg.message());
                                runOnUi(task);
                                System.out.println(msg);
                            }
                        } catch (Exception e) {
                            System.err.println("Fel vid läsning av meddelande: " + e.getMessage());
                        }
                    });
                })
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        System.err.println("Lyssningen avslutades med fel: " + ex.getMessage());
                    }
                    receiving.set(false);
                });
    }

    /**
     * Försöker köra uppgiften via uiExecutor (vanligtvis JavaFX-tråden).
     * Om detta inte är möjligt körs uppgiften direkt i den aktuella tråden.
     */
    private void runOnUi(Runnable task) {
        try {
            uiExecutor.accept(task);
        } catch (IllegalStateException e) {
            task.run();
        }
    }

    /**
     * Skickar ett textmeddelande till Ntfy-servern på ett säkert sätt.
     * <p>
     * Metoden använder Jackson för att skapa JSON med topic och meddelande, skickar
     * det som en HTTP POST-förfrågan och kontrollerar serverns svar.
     * <p>
     * 1. Bygger ett JSON-objekt med topic och message via Jackson, vilket säkerställer
     * att specialtecken (citat, backslash, nya rader etc.) hanteras korrekt.
     * <p>
     * 2. Skickar förfrågan synkront med HttpClient. Svaret ignoreras, men HTTP-status
     * kontrolleras för att avgöra om sändningen lyckades.
     * <p>
     * 3. Returnerar true endast om servern svarar med en 2xx-statuskod.
     * <p>
     * 4. Vid nätverksfel eller andra IO-problem skrivs ett felmeddelande ut och
     * metoden returnerar false.
     * <p>
     * 5. Om tråden blir avbruten under väntan på servern fångas InterruptedException,
     * ett meddelande skrivs ut, interrupt-flaggan återställs med Thread.currentThread().interrupt(),
     * och metoden returnerar false.
     */
    public boolean sendMessage(String message) {
        try {
            String jsonBody = mapper.writeValueAsString(Map.of(
                    "topic", topic,
                    "message", message
            ));

            var request = HttpRequest.newBuilder()
                    .uri(URI.create(serverAddress))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return true;
            } else {
                System.out.printf("Server responded %d when sending message.%n", response.statusCode());
                return false;
            }

        } catch (IOException e) {
            System.out.println("Det gick tyvärr inte att skicka meddelandet: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Försöket att skicka meddelandet avbröts tyvärr.");
            Thread.currentThread().interrupt();
        }

        return false;
    }

    /**
     * Gör att JSON-fält som inte motsvaras av variabler i klassen ignoreras,
     * vilket förhindrar fel om servern skickar mer data än väntat.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NtfyMessageDto(String id, long time, String event, String topic, String message) {
    }
}