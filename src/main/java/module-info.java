module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires com.google.gson;

    opens com.example.demo to javafx.fxml,com.google.gson;
    exports com.example.demo;
}