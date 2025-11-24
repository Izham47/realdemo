package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViewTaskController {

    @FXML private TableView<ToDo> table;
    @FXML private TableColumn<ToDo, String> titlecolumn;
    @FXML private TableColumn<ToDo, String> descriptioncolumn;
    @FXML private TableColumn<ToDo, LocalDate> duedatecolumn;
    @FXML private TableColumn<ToDo, String> categorycolumn;
    @FXML private TableColumn<ToDo, Integer> prioritycolumn;
    @FXML private TableColumn<ToDo, Boolean> completedcolumn;
    @FXML private TextField searchfield;
    @FXML private Button searchbutton;

    private final String FILE_PATH = "tasks.json";

    // ⭐ 1. FIXED: Promoted to class level so saveTasks() can access it
    private ObservableList<ToDo> allTasks;

    @FXML
    public void initialize() {
        // 1. Setup Columns
        titlecolumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptioncolumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        duedatecolumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        categorycolumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        prioritycolumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        completedcolumn.setCellValueFactory(new PropertyValueFactory<>("completed"));

        // ⭐ 2. FIXED: Completed the CheckBox Cell Factory logic
        // (Your previous code was missing the updateItem method and closing braces)
        completedcolumn.setCellFactory(column -> new TableCell<ToDo, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                // When user clicks the checkbox...
                checkBox.setOnAction(event -> {
                    if (getTableRow() != null && getTableView().getItems().size() > getIndex()) {
                        ToDo todo = getTableView().getItems().get(getIndex());
                        todo.setCompleted(checkBox.isSelected());
                        saveTasks(); // Update file immediately
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        });

        // 3. Load Data (Initialize the class field)
        allTasks = FXCollections.observableArrayList(loadCurrentTasks());

        // 4. Create FilteredList
        FilteredList<ToDo> searchedData = new FilteredList<>(allTasks, p -> true);

        // 5. BUTTON ACTION
        searchbutton.setOnAction(event -> {
            String newValue = searchfield.getText();
            searchedData.setPredicate(todo -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                if (todo.getTitle().toLowerCase().contains(lowerCaseFilter)) return true;
                if (todo.getDescription().toLowerCase().contains(lowerCaseFilter)) return true;
                if (todo.getCategory().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });

        // 6. Wrap in SortedList and Bind
        SortedList<ToDo> sortedData = new SortedList<>(searchedData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        // 7. Set Table Items
        table.setItems(sortedData);
    }

    @FXML
    public void clickingEdit(){
        ToDo selectTask = table.getSelectionModel().getSelectedItem();
        int  selectIndex = table.getSelectionModel().getSelectedIndex();

        if (selectTask == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Select a task");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditTask.fxml"));
            Parent root = loader.load();

            EditTaskController controller = loader.getController();
            controller.getTaskData(selectTask, selectIndex);

            Stage stage = new Stage();
            stage.setTitle("Edit Task");
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) table.getScene().getWindow()).close();
        }catch (IOException error){
            error.printStackTrace();
}
}


    // ⭐ 3. FIXED: Removed createGson() and used standard GsonBuilder
    private void saveTasks() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(allTasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<ToDo> loadCurrentTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            // Uses standard Gson
            List<ToDo> results = new Gson().fromJson(reader, new TypeToken<List<ToDo>>(){}.getType());
            return results == null ? new ArrayList<>() : results;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}