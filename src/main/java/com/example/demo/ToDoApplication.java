package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ToDoApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ToDoApplication.class.getResource("Home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1900, 1000);
        stage.setTitle("To DO List!");
        stage.setScene(scene);
        stage.show();
    }


}
