package com.example.demo;

import java.util.Optional;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
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
    @FXML
    private MenuItem exitbutton;
    @FXML private ComboBox<String> filterType;
    @FXML private ComboBox<String> filterValue;


    private final String FILE_PATH = "tasks.json";

    // Class-level variables so they can be accessed by all methods
    private ObservableList<ToDo> allTasks;
    private FilteredList<ToDo> filteredData;

    @FXML
    public void initialize() {
        // 1. Setup Columns
        titlecolumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptioncolumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        duedatecolumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        categorycolumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        prioritycolumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        completedcolumn.setCellValueFactory(new PropertyValueFactory<>("completed"));

        // 2. CheckBox Cell Factory
        completedcolumn.setCellFactory(column -> new TableCell<ToDo, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    if (getTableRow() != null && getTableView().getItems().size() > getIndex()) {
                        ToDo todo = getTableView().getItems().get(getIndex());
                        todo.setCompleted(checkBox.isSelected());
                        saveTasks();
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

        // 3. Load Data
        allTasks = FXCollections.observableArrayList(loadCurrentTasks());

        // 4. Initialize FilteredList (Class-level variable)
        filteredData = new FilteredList<>(allTasks, p -> true);

        // 5. Initialize Filter Logic
        if (filterType != null) {
            filterType.getItems().addAll("None", "Category", "Priority", "Status");
            filterType.valueProperty().addListener((obs, oldVal, newVal) -> {
                updateFilterOptions(newVal);
                updateTableFilter();
            });
        }

        if (filterValue != null) {
            filterValue.valueProperty().addListener((obs, oldVal, newVal) -> updateTableFilter());
        }

        // 6. Search Button Logic (Calls the master filter method)
        searchbutton.setOnAction(event -> updateTableFilter());

        // 7. Bind SortedList to Table
        SortedList<ToDo> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
    }

    private void updateFilterOptions(String type) {
        filterValue.getItems().clear();
        filterValue.setValue(null);

        if (type == null || "None".equals(type)) {
            filterValue.setDisable(true);
            return;
        }

        filterValue.setDisable(false);

        switch (type) {
            case "Category":
                filterValue.getItems().addAll("Work", "Personal", "Health", "Finance", "Other");
                break;
            case "Priority":
                filterValue.getItems().addAll("1", "2", "3", "4", "5");
                break;
            case "Status":
                filterValue.getItems().addAll("Completed", "Pending");
                break;
        }
    }

    private void updateTableFilter() {
        String keyword = searchfield.getText().toLowerCase();
        String type = (filterType != null) ? filterType.getValue() : "None";
        String value = (filterValue != null) ? filterValue.getValue() : null;

        filteredData.setPredicate(todo -> {
            // 1. Check Keyword
            boolean matchKeyword = true;
            if (keyword != null && !keyword.isEmpty()) {
                matchKeyword = todo.getTitle().toLowerCase().contains(keyword) ||
                        todo.getDescription().toLowerCase().contains(keyword);
            }

            // 2. Check Specific Filter
            boolean matchCriterion = true;
            if (type != null && value != null && !"None".equals(type)) {
                switch (type) {
                    case "Category":
                        matchCriterion = todo.getCategory().equalsIgnoreCase(value);
                        break;
                    case "Priority":
                        try {
                            int pVal = Integer.parseInt(value);
                            matchCriterion = (todo.getPriority() == pVal);
                        } catch (NumberFormatException e) { matchCriterion = false; }
                        break;
                    case "Status":
                        boolean isDone = todo.isCompleted();
                        if ("Completed".equals(value)) matchCriterion = isDone;
                        else if ("Pending".equals(value)) matchCriterion = !isDone;
                        break;
                }
            }

            return matchKeyword && matchCriterion;
        });
    }

    @FXML
    public void clickingEdit() throws IOException {
        ToDo selectTask = table.getSelectionModel().getSelectedItem();
        int selectIndex = table.getSelectionModel().getSelectedIndex();

        if (selectTask == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Select a task");
            alert.showAndWait();
            return;
        }


            // We must get the index from the MASTER list (allTasks), not the filtered table
            int masterIndex = allTasks.indexOf(selectTask);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditTask.fxml"));
            Parent root = loader.load();

            EditTaskController controller = loader.getController();
            controller.getTaskData(selectTask, masterIndex);

            Stage stage = new Stage();
            stage.setTitle("Edit Task");
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) table.getScene().getWindow()).close();

    }

    @FXML
    public void clickingDelete() {
        // 1. Get Selected Item
        ToDo selectTask = table.getSelectionModel().getSelectedItem();

        if (selectTask == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Select a task");
            alert.setContentText("Please select a task to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this task?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            allTasks.remove(selectTask);
            saveTasks();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Task is deleted!");
            alert.showAndWait();
}
}


    @FXML
    public void resetFilter() {
        searchfield.clear();
        if (filterType != null) filterType.setValue("None");
        if (filterValue != null) filterValue.setValue(null);

    }

    @FXML
    public void handleFAQ() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("FAQ");
        alert.setHeaderText("Frequently Asked Questions");
        alert.setContentText(
                "Q: How do I add a task?\n" +
                        "A: Click the 'Add New Task' button on the main menu.\n\n" +
                        "Q: How do I edit or delete?\n" +
                        "A: Go to 'View Tasks', select a row, and click the Edit or Delete buttons."
        );
        alert.showAndWait();
    }

    @FXML
    public void handleCredit() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Us");
        alert.setHeaderText("Smart ToDo List App");
        alert.setContentText(
                "Version: 1.0\n" +
                        "Created by: Muhammad Izham & Syed Nur Haikal\n" +
                        "Course: CAT201 - Integrated Software Development Workshop\n" +
                        "Universiti Sains Malaysia"
        );
        alert.showAndWait();}


        public void Exit_App() {
        Stage stage = (Stage) table.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void clearAllTasks() {
        // 1. Create a Confirmation Alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Clear");
        alert.setHeaderText("Clear All Data");
        alert.setContentText("Are you sure you want to delete ALL tasks? This cannot be undone.");

        // 2. Wait for user response
        if (alert.showAndWait().get() == ButtonType.OK) {
            // 3. Clear the list in memory
            allTasks.clear();

            // 4. Save the empty list to the file (overwriting the old data)
            saveTasks();

            // The TableView updates automatically because it is bound to allTasks
        }
    }

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
            List<ToDo> results = new Gson().fromJson(reader, new TypeToken<List<ToDo>>(){}.getType());
            return results == null ? new ArrayList<>() : results;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}