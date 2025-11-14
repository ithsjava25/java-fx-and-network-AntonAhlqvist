package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
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
    private
    Pane messageLayer;

    /**
     * Creates and animates a tetris-style message block from the current input text and adds it to the message layer.
     *
     * The method measures the message width to compute a block width that spans 1–8 tile units, selects a matching
     * wood-background image, shifts existing blocks down by one tile with an ease animation, places the new block above
     * the stack, and animates it into position.
     *
     * - Tile size is 64 pixels and margin for width calculation is 32 pixels.
     * - Computed step count is clamped to the range 1 through 8 and determines the background image used.
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

    private final String serverAddress;
    private final ChatetrisModel model;

    /**
     * Creates a ChatetrisController and initializes its model using the `HOST_NAME` environment variable.
     *
     * The constructor loads environment variables, assigns `serverAddress` from `HOST_NAME`, and constructs a
     * ChatetrisModel with that address.
     */
    public ChatetrisController() {
        Dotenv dotenv = Dotenv.load();
        serverAddress = dotenv.get("HOST_NAME");
        model = new ChatetrisModel(serverAddress);
    }

    /**
     * Initializes UI resources for the controller by loading the handwritten font and setting
     * the background and frame images from bundled resources.
     *
     * Loads "/Fonts/LucidaHandwritingItalic.ttf" at 20pt and sets:
     * "/Images/Seamless_Brown_Wood_With_Light_Brown_Grid.png" as the backgroundImage and
     * "/Images/Dark_Brown_Wood_Frame_With_Text.png" as the frameImage.
     */
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

    @FXML
    Button sendButton;

    /**
     * Sends the current text from the message input to the model.
     */
    @FXML
    void handleSendMessage() {
        String message = messageInput.getText();
        model.sendMessage(message);
    }
}