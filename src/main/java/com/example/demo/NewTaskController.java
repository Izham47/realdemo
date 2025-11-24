package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class NewTaskController {
    @FXML
    private TextField newtitlefield;
    @FXML
    private TextField newdescriptionfield;
    @FXML
    private DatePicker duedatee;
    @FXML
    private ComboBox<String> categoryy;
    @FXML
    private ComboBox<Integer> priorityy;
    @FXML
    private Button buttonSave;

    private final String FILE_PATH = "tasks.json";

    @FXML
    public void initialize() {
        if(categoryy != null) categoryy.getItems().addAll("Work", "Personal", "Health", "Finance", "Other");
        if(priorityy != null) priorityy.getItems().addAll(1,2,3,4,5);
    }
    @FXML
    public void buttonSave() throws IOException {
        String title = newtitlefield.getText();
        String description = newdescriptionfield.getText();

        if(title.isEmpty()){
            // FIX: Add an alert so the user knows why nothing happened
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a Title for the task!");
            alert.showAndWait();
            return;
        }

        ToDo newTask = new ToDo(title, description, duedatee.getValue(), categoryy.getValue(), priorityy.getValue() );

        List<ToDo>allTasks = loadCurrentTasks();
        allTasks.add(newTask);
        saveAllTasks(allTasks);

        Stage stage = (Stage) buttonSave.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("myGUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 900);
        stage.setTitle("To DO List!");
        stage.setScene(scene);
        stage.show();

    }

    private List<ToDo> loadCurrentTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>(); // Return empty list if file doesn't exist

        try (Reader reader = new FileReader(file)) {
            // Read JSON file into a List of ToDo objects
            List<ToDo> results = new Gson().fromJson(reader, new TypeToken<List<ToDo>>(){}.getType());

            // SAFETY CHECK: If the file was empty, Gson returns null.
            // We must return a new ArrayList instead of null.
            if (results == null) {
                return new ArrayList<>();
            }

            return results;

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveAllTasks(List<ToDo> tasks){
        try(Writer writer = new FileWriter(FILE_PATH)){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(tasks, writer);
        }catch(IOException e) { e.printStackTrace();}

}}
