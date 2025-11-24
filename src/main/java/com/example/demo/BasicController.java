package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class BasicController {

    @FXML
    private Label lb1;

    @FXML
    private Button viewButton;

    @FXML
    private Button addButton;

    @FXML
    private Button QuitButton;

    @FXML
    private Button GoBackButton;

    @FXML
    private Button clearButton;

    public void go_back() throws IOException {
        Stage stage = (Stage) GoBackButton.getScene().getWindow();
        stage.close();
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Add Contact");
        primaryStage.setScene(new Scene(root,1900, 1000));
        primaryStage.show();
    }

    public void Exit_App() {
        Stage stage = (Stage) QuitButton.getScene().getWindow();
        stage.close();
    }




    public void add_task() throws IOException {
        Stage stage=(Stage) addButton.getScene().getWindow();
        stage.close();
        Stage newTaskStage=new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("NewTask.fxml"));
        newTaskStage.setTitle("Add New Task");
        newTaskStage.setScene(new Scene(root, 1900, 1000));
        newTaskStage.show();

    }

    public void view_task() throws IOException {
        Stage stage=(Stage) viewButton.getScene().getWindow();
        stage.close();
        Stage newTaskStage=new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("ViewAllTask.fxml"));
        newTaskStage.setTitle(" All Task");
        newTaskStage.setScene(new Scene(root, 1900, 1000));
        newTaskStage.show();

    }

}

