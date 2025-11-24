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

    @FXML
    public void go_back() throws IOException {
        Stage stage = (Stage) GoBackButton.getScene().getWindow();
        stage.close();
        Stage Primarystage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Primarystage.setTitle("Add Contact");
        Primarystage.setScene(new Scene(root,1900, 1000));
        Primarystage.show();
    }

    @FXML
    public void Exit_App() {
        Stage stage = (Stage) QuitButton.getScene().getWindow();
        stage.close();
    }




    public void add_task() throws IOException {
        Stage stage=(Stage) addButton.getScene().getWindow();
        stage.close();
        Stage addTaskStage =new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("NewTask.fxml"));
        addTaskStage.setTitle("Add New Task");
        addTaskStage.setScene(new Scene(root, 1900, 1000));
        addTaskStage.show();

    }

    public void view_task() throws IOException {
        Stage stage=(Stage) viewButton.getScene().getWindow();
        stage.close();
        Stage viewTaskStage =new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("ViewAllTask.fxml"));
        viewTaskStage.setTitle(" All Task");
        viewTaskStage.setScene(new Scene(root, 1900, 1000));
        viewTaskStage.show();

    }

}

