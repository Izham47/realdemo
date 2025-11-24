package com.example.demo;

import javafx.scene.control.Alert.AlertType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    @FXML
    private Button goBackButton;

    private final String FILE_PATH = "tasks.json"; // JSON file to store tasks

    // Initializes ComboBOx options
    @FXML
    public void initialize() {
        // Add predefined categories to the ComboBox
        if (categoryy != null)
            categoryy.getItems().addAll("Work", "Personal", "Health", "Finance", "Other");

        // Add priority numbers 1-5 to the ComboBox
        if (priorityy != null)
            priorityy.getItems().addAll(1, 2, 3, 4, 5);
    }

    // Method called when the Save button is clicked
    @FXML
    public void buttonSave() throws IOException {
        String title = newtitlefield.getText();           // Get task title from input field
        String description = newdescriptionfield.getText(); // Get task description from input field

        // Check if title is empty and show alert
        // Only title is mandatory for a task
        if (title.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a Title for the task!");
            alert.showAndWait();
            return; // Stop execution, task won't be saved
        }

        // Create a new task object using user input
        ToDo newTask = new ToDo(title, description, duedatee.getValue(), categoryy.getValue(), priorityy.getValue());

        // Load existing tasks from the JSON file
        List<ToDo> allTasks = loadCurrentTasks();
        // Add the new task to the list
        allTasks.add(newTask);
        // Save updated list back to JSON file
        saveAllTasks(allTasks);

        // Show confirmation alert
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("New task is added!");
        alert.showAndWait();

        // Close the current "New Task" window
        Stage stage = (Stage) buttonSave.getScene().getWindow();
        stage.close();

        // Open list tasks  window to show updated task in the list
        Stage newTaskStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("ViewAllTask.fxml"));
        newTaskStage.setTitle("All Tasks");
        newTaskStage.setScene(new Scene(root, 1900, 1000));
        newTaskStage.show();
    }

    // Load tasks from JSON file
    private List<ToDo> loadCurrentTasks() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>(); // Return empty list if file does not exist

        // Read JSON into a List of ToDo objects
        Reader reader = new FileReader(file);
        List<ToDo> results = new Gson().fromJson(reader, new TypeToken<List<ToDo>>(){}.getType()); //Gson converts the JSON array into a List<ToDo> object
        reader.close();

        // If file was empty, Gson returns null; return empty list instead
        if (results == null) return new ArrayList<>();
        return results;
    }

    // Save tasks to JSON file
    private void saveAllTasks(List<ToDo> tasks) throws IOException {
        Writer writer = new FileWriter(FILE_PATH);
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Format JSON nicely
        gson.toJson(tasks, writer); // Write list to file
        writer.close();
    }

    public void goBackToMain() throws IOException {
        // 1. Get the current Stage (the Add New Task window)
        Stage currentStage = (Stage) goBackButton.getScene().getWindow();
        currentStage.close(); // Close the current window

        // 2. Load the main menu FXML file (myGUI.fxml)
        Stage mainStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Home.fxml")); // Load the main menu FXML

        // 3. Set up and display the main Stage
        mainStage.setTitle("Main Task Manager"); // You can change the title
        mainStage.setScene(new Scene(root, 1900, 1000)); // Use appropriate size
        mainStage.show();
    }

}
