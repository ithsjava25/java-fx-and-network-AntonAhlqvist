package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ChatetrisController {

    @FXML
    private ImageView backgroundImage;

    @FXML

    private ImageView frameImage;

    @FXML
    TextField messageInput;

    @FXML
    Button sendButton;

    @FXML
    private
    Pane messageLayer;

    private final String serverAddress;
    private final ChatetrisModel model;

    public ChatetrisController() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String configuredAddress = dotenv.get("HOST_NAME");

        if (configuredAddress == null || configuredAddress.isBlank()) {
            throw new IllegalStateException("HOST_NAME måste vara definierad i .env för att appen ska kunna starta.");
        }

        serverAddress = configuredAddress;
        model = new ChatetrisModel(serverAddress);
    }

    @FXML
    private void initialize() {

        Font.loadFont(
                getClass().getResource("/Fonts/LucidaHandwritingItalic.ttf").toExternalForm(),
                20
        );
        backgroundImage.setImage(new Image(
                getClass().getResource("/Images/Seamless_Brown_Wood_With_Light_Brown_Grid.png").toExternalForm()
        ));
        frameImage.setImage(new Image(
                getClass().getResource("/Images/Dark_Brown_Wood_Frame_With_Text.png").toExternalForm()
        ));
    }

    /**
     * Kombinerar sändning av meddelande till servern med skapandet av ett
     * tetrisblock i UI:t. Används av en knapp som ska utföra båda momenten
     * samtidigt.
     */
    @FXML
    private void handleSendAndTb(ActionEvent event) {
        handleSendMessage();
        tbButton();
    }

    /**
     * Hämtar texten från inmatningsfältet och skickar den till servern
     * via modellen. UI-relaterade effekter hanteras separat av tbButton().
     */
    @FXML
    void handleSendMessage() {
        String message = messageInput.getText();
        model.sendMessage(message);
    }

    /**
     * I korthet: Skapar ett nytt "tetrisblock" baserat på användarens text och
     * animerar befintliga block nedåt för att ge effekten av att nya
     * block staplas ovanpå tidigare.
     * <p>
     * 1. Hämtar texten och beräknar dess pixelbredd genom att rendera
     * den till ett temporärt Text-objekt.
     * <p>
     * 2. Omvandlar textbredden till ett antal blocksteg (1–8) baserat
     * på TILE_SIZE. Max 8 steg för att förhindra överlappning.
     * <p>
     * 3. Flyttar alla befintliga block nedåt med en TranslateTransition
     * för att ge en smidig tetris-liknande rörelse. När animationen
     * slutförts uppdateras blockets layoutY och translationen nollställs.
     * <p>
     * 4. Skapar ett nytt Label-block med korrekt bredd, centrering och
     * textöverströmningsbeteende.
     * <p>
     * 5. Väljer rätt bakgrundsbild baserat på antalet blocksteg och applicerar
     * den via inline-CSS så att varje block får en passande trätextur.
     * <p>
     * 6. Lägger in blocket i messageLayer och animerar det nedåt till sin
     * slutposition med samma typ av transition som de övriga blocken.
     */
    @FXML
    void tbButton() {
        String messageText = messageInput.getText();

        int TILE_SIZE = 64;

        Text temp = new Text(messageText);
        temp.setFont(Font.font("Lucida Handwriting", FontPosture.ITALIC, 18));
        double textPixelWidth = temp.getLayoutBounds().getWidth();

        int MARGIN = 32;

        int steps = (int) Math.ceil((textPixelWidth + MARGIN) / TILE_SIZE);
        if (steps < 1) steps = 1;
        if (steps > 8) steps = 8;
        int blockWidth = steps * TILE_SIZE;

        for (int i = 0; i < messageLayer.getChildren().size(); i++) {
            var block = messageLayer.getChildren().get(i);
            double targetY = block.getLayoutY() + TILE_SIZE;

            TranslateTransition tt = new TranslateTransition(Duration.millis(300), block);
            tt.setToY(targetY - block.getLayoutY());
            tt.setInterpolator(Interpolator.EASE_BOTH);
            int finalI = i;
            tt.setOnFinished(e -> {
                block.setLayoutY(targetY);
                block.setTranslateY(0);
            });
            tt.play();
        }

        Label tb = new Label(messageText);
        tb.setPrefSize(blockWidth, TILE_SIZE);
        tb.setLayoutX(64);
        tb.setLayoutY(64 - TILE_SIZE);
        tb.setAlignment(Pos.CENTER);
        tb.setTextOverrun(OverrunStyle.CLIP);

        tb.getStyleClass().add("tetrisblock");

        String imageName = switch (steps) {
            case 1 -> "Wood_Label_One_Unit.png";
            case 2 -> "Wood_Label_Two_Units.png";
            case 3 -> "Wood_Label_Three_Units.png";
            case 4 -> "Wood_Label_Four_Units.png";
            case 5 -> "Wood_Label_Five_Units.png";
            case 6 -> "Wood_Label_Six_Units.png";
            case 7 -> "Wood_Label_Seven_Units.png";
            case 8 -> "Wood_Label_Eight_Units.png";
            default -> "Wood_Label_One_Unit.png";
        };

        Image background = new Image(getClass().getResource("/Images/" + imageName).toExternalForm());
        tb.setStyle(
                "-fx-background-image: url('" + background.getUrl() + "'); " +
                        "-fx-background-size: cover; " +
                        "-fx-background-repeat: no-repeat; " +
                        "-fx-background-position: center;"
        );

        messageLayer.getChildren().add(tb);

        TranslateTransition ttNew = new TranslateTransition(Duration.millis(300), tb);
        ttNew.setToY(TILE_SIZE);
        ttNew.setInterpolator(Interpolator.EASE_BOTH);
        ttNew.setOnFinished(e -> {
            tb.setLayoutY(64);
            tb.setTranslateY(0);
        });
        ttNew.play();
    }
}