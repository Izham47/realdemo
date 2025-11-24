package com.example.demo;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginWindow extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Use a BorderPane for the root node to help center the GridPane
        BorderPane root = new BorderPane();

        // Create the scene with the root, dimensions, and background color
        Scene scene = new Scene(root, 300, 150, Color.WHITE);

        // Create the GridPane layout
        GridPane gridpane = new GridPane();

        // Set alignment for the gridpane within the BorderPane
        gridpane.setAlignment(Pos.CENTER);
        // Optional: Add some padding/gap to the grid cells (not in the image, but helpful)
        gridpane.setHgap(10);
        gridpane.setVgap(10);

        // Initialize the components
        Label fNamelbl = new Label("First Name:");
        TextField fNameFld = new TextField();
        Label lNamelbl = new Label("Last Name:");
        TextField lNameFld = new TextField();
        Button signButt = new Button("Sign in"); // Note: "Save" in the image, "Sign in" in the code text

        // --- Positioning Components in the GridPane ---

        // First name label, set to (0, 0)
        GridPane.setHalignment(fNamelbl, HPos.RIGHT);
        gridpane.add(fNamelbl, 0, 0); // (Column, Row)

        // Last name label, set to (0, 1)
        GridPane.setHalignment(lNamelbl, HPos.RIGHT);
        gridpane.add(lNamelbl, 0, 1);

        // First name field, set to (1, 0)
        GridPane.setHalignment(fNameFld, HPos.LEFT);
        gridpane.add(fNameFld, 1, 0);

        // Last name field, set to (1, 1)
        GridPane.setHalignment(lNameFld, HPos.LEFT);
        gridpane.add(lNameFld, 1, 1);

        // Save button, set to (1, 2)
        // Note: The image shows "Save" but the code text says "Sign in"
        GridPane.setHalignment(signButt, HPos.RIGHT);
        gridpane.add(signButt, 1, 2);

        // Center the GridPane within the BorderPane root
        root.setCenter(gridpane);

        // Set the scene and show the stage
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}