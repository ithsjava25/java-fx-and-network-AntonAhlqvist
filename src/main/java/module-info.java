module chatclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires io.github.cdimascio.dotenv.java;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires javafx.base;

    opens com.example to javafx.fxml;
    exports com.example;
}