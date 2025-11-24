package com.example.demo;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.paint.Color;

public class SimpleWindow extends Application {


    @Override
    public void start(Stage stage) {

        Group root = new Group();
        Scene scene = new Scene(root);

        stage.setTitle("JavaFX Scene Graph Demo");
        stage.setScene(scene);
        stage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }
}
