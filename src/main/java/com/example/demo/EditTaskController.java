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
    @FXML private TextField editTitleField;
    @FXML private TextField editDescField;
    @FXML private DatePicker editDueDateField;
    @FXML private ComboBox<String> editCategoryField;
    @FXML private ComboBox<Integer> editPriorityField;
    @FXML private Button buttonEdit;

    private final String FILE_PATH = "tasks.json";
    private int taskIndex = -1;

    private boolean taskStatus = false;

    @FXML
    public void initialize() {
        if (editCategoryField != null)
            editCategoryField.getItems().addAll("Work", "Personal", "Health", "Finance", "Other");
        if (editPriorityField != null)
            editPriorityField.getItems().addAll(1, 2, 3, 4, 5);
    }

    public void getTaskData(ToDo task, int index){
        this.taskIndex = index;
        this.taskStatus = task.getCompleted();
        editTitleField.setText(task.getTitle());
        editDescField.setText(task.getDescription());
        editCategoryField.setValue(task.getCategory());
        editPriorityField.setValue(task.getPriority());

        if (task.getDueDate() != null && !task.getDueDate().isEmpty())
            editDueDateField.setValue(LocalDate.parse(task.getDueDate()));
    }

    @FXML
    public void buttonEdit(){
        ToDo updatedTask = new ToDo(editTitleField.getText(),
                editDescField.getText(),
                editDueDateField.getValue(),
                editCategoryField.getValue(),
                editPriorityField.getValue());

        updatedTask.setCompleted(this.taskStatus);
        List<ToDo> allTasks = loadTasks();

        if(taskIndex >= 0 && taskIndex < allTasks.size()){
            allTasks.set(taskIndex,updatedTask);
            saveAllTasks(allTasks);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Task is edited!");
            alert.showAndWait(); // Waits for user to click OK
        }
        closeAndReturn();
    }

    private List<ToDo> loadTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            List<ToDo> results = new Gson().fromJson(reader, new TypeToken<List<ToDo>>(){}.getType());
            return (results == null) ? new ArrayList<>() : results;
        } catch (IOException error) {
            return new ArrayList<>();
        }
    }

    private void saveAllTasks(List<ToDo> tasks) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(tasks, writer);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    private void closeAndReturn() {
        try {
            Stage stage = (Stage) buttonEdit.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewAllTask.fxml")); // Back to Main Menu
            Parent root = loader.load();
            Stage mainStage = new Stage();
            mainStage.setTitle("TO DO LIST");
            mainStage.setScene(new Scene(root));
            mainStage.show();
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
}

