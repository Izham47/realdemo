package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EditTaskController {

    @FXML
    private TextField editTitleField;
    @FXML
    private TextField editDescField;
    @FXML
    private DatePicker editDueDateField;
    @FXML
    private ComboBox<String> editCategoryField;
    @FXML
    private ComboBox<Integer> editPriorityField;
    @FXML
    private Button buttonEdit;

    // Location of JSON data file
    private final String FILE_PATH = "tasks.json";

    // Stores which task index that are being edited
    private int taskIndex = -1;

    // Keep existing completion status so it won't be reset
    private boolean taskStatus = false;

    // Initializes ComboBOx options
    @FXML
    public void initialize() {
        if (editCategoryField != null)
            editCategoryField.getItems().addAll("Work", "Personal", "Health", "Finance", "Other");

        if (editPriorityField != null)
            editPriorityField.getItems().addAll(1, 2, 3, 4, 5);
    }

    // Receive the task and index
    public void getTaskData(ToDo task, int index){
        this.taskIndex = index;
        this.taskStatus = task.getCompleted(); // keep old completion status

        // Fill the form with original data
        editTitleField.setText(task.getTitle());
        editDescField.setText(task.getDescription());
        editCategoryField.setValue(task.getCategory());
        editPriorityField.setValue(task.getPriority());
    }

    @FXML
    public void buttonEdit() throws IOException{

        // Create an updated task and assigned new value based on user input
        ToDo updatedTask = new ToDo(
                editTitleField.getText(),
                editDescField.getText(),
                editDueDateField.getValue(),
                editCategoryField.getValue(),
                editPriorityField.getValue()
        );

        // Keep the previous completed state
        updatedTask.setCompleted(this.taskStatus);

        // Load all tasks from JSON to update new data
        List<ToDo> allTasks = loadTasks();

        // Replace the old task with updated version
        if(taskIndex >= 0 && taskIndex < allTasks.size()){
            allTasks.set(taskIndex, updatedTask);
            saveAllTasks(allTasks);

            // Notify user
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Task is edited!");
            alert.showAndWait();
        }

        // Go back to main screen
        closeAndReturn();
    }

    // Read JSON file and load all tasks
    private List<ToDo> loadTasks() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        Reader reader = new FileReader(file);
        List<ToDo> results = new Gson().fromJson(reader, new TypeToken<List<ToDo>>(){}.getType()); //Gson converts the JSON array into a List<ToDo> object
        reader.close();

        return (results == null) ? new ArrayList<>() : results;
    }

    // Save entire task list back to JSON
    private void saveAllTasks(List<ToDo> tasks) throws IOException {
        Writer writer = new FileWriter(FILE_PATH);
        new GsonBuilder().setPrettyPrinting().create().toJson(tasks, writer);
        writer.close();
    }


    // Close edit window and go back to task list page
    private void closeAndReturn() throws IOException {

        // Close current window
        Stage stage = (Stage) buttonEdit.getScene().getWindow();
        stage.close();

        // Load main view again
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewAllTask.fxml"));
        Parent root = loader.load();

        Stage mainStage = new Stage();
        mainStage.setTitle("TO DO LIST");
        mainStage.setScene(new Scene(root));
        mainStage.show();
    }
}
