package com.example.demo;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class FlowPlane extends Application {

    // Define the response Label globally or pass it to the handler
    // In this structure, we define it in start() but the ActionHandlers
    // need to be defined inside start() to access it easily.

    @Override
    public void start(Stage myStage) {
        // Give the stage a title.
        myStage.setTitle("Use JavaFX Buttons and Events.");

        // Use a FlowPane for the root node. In this case,
        // vertical and horizontal gaps of 10.
        FlowPane rootNode = new FlowPane(10, 10);

        // Center the controls in the scene.
        rootNode.setAlignment(Pos.CENTER);

        // Create a scene.
        Scene myScene = new Scene(rootNode, 300, 100);

        // Set the scene on the stage.
        myStage.setScene(myScene);

        // Create a label.
        Label response = new Label("Push a Button");

        // Create two push buttons.
        Button btnUp = new Button("Up");
        Button btnDown = new Button("Down");

        // Handle the action events for the Up button.
        btnUp.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                response.setText("You Pressed Up.");
            }
        });

        // Handle the action events for the Down button.
        btnDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                response.setText("You Pressed Down.");
            }
        });

        // Add the label and buttons to the scene graph.
        rootNode.getChildren().addAll(btnUp, btnDown, response);

        // Show the stage and its scene.
        myStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
