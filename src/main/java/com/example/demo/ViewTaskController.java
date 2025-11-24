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

    // JavaFX UI Components from FXML
    @FXML private TableView<ToDo> table;                // Table to display tasks
    @FXML private TableColumn<ToDo, String> titlecolumn;      // Column for task title
    @FXML private TableColumn<ToDo, String> descriptioncolumn; // Column for task description
    @FXML private TableColumn<ToDo, LocalDate> duedatecolumn;  // Column for due date
    @FXML private TableColumn<ToDo, String> categorycolumn;    // Column for category
    @FXML private TableColumn<ToDo, Integer> prioritycolumn;   // Column for priority
    @FXML private TableColumn<ToDo, Boolean> completedcolumn;  // Column with checkbox for completion
    @FXML private TextField searchfield;                    // Field to enter search keyword
    @FXML private Button searchbutton;                      // Button to trigger search
    @FXML private ComboBox<String> filterType;             // Dropdown to select type of filter
    @FXML private ComboBox<String> filterValue;            // Dropdown to select filter value

    // Path to JSON file storing tasks
    private final String FILE_PATH = "tasks.json";

    // Observable list for the whole list of tasks
    private ObservableList<ToDo> allTasks;

    // Filtered list for applying search and filter
    private FilteredList<ToDo> filteredData;

    // Automatically called to initialize after FXML is loaded
    @FXML
    public void initialize() throws IOException {

        // Setup Table Columns
        titlecolumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptioncolumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        duedatecolumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        categorycolumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        prioritycolumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        completedcolumn.setCellValueFactory(new PropertyValueFactory<>("completed"));


        //  Add CheckBox in Completed Column

        completedcolumn.setCellFactory(column -> new TableCell<ToDo, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                // When the checkbox is clicked, update the task's 'completed' status and save immediately
                checkBox.setOnAction(event -> {
                    if (getTableRow() != null && getTableView().getItems().size() > getIndex()) {
                        ToDo todo = getTableView().getItems().get(getIndex());
                        todo.setCompleted(checkBox.isSelected()); // Update object
                        try {
                            saveTasks(); // Save changes to JSON file
                        } catch (IOException e) {
                            e.printStackTrace(); // Print error to console for debugging
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty); // Call superclass method to maintain default behavior

                // If this cell is empty or has no data (the row is deleted or uninitialized), remove the checkbox
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item); // Reflect the task's completed status
                    setGraphic(checkBox);       // Display checkbox in the cell
                }
            }
        });


        // Load tasks from file into memory

        allTasks = FXCollections.observableArrayList(loadCurrentTasks());


        // Initialize FilteredList for search/filter functionality

        filteredData = new FilteredList<>(allTasks, p -> true);


        // Setup ComboBox options

        if (filterType != null) {
            filterType.getItems().addAll("None", "Category", "Priority", "Status"); // Types of filter
            filterType.valueProperty().addListener((obs, oldVal, newVal) -> {
                updateFilterOptions(newVal); // Update available options when type changes
                updateTableFilter();         // Refresh table immediately
            });
        }




        //Whenever the user changes the selection in the second filter ComboBox (filterValue), automatically update the task list in the table to reflect the new filter
        if (filterValue != null) {
            filterValue.valueProperty().addListener((obs, oldVal, newVal) -> updateTableFilter());
        }


        // Setup search button to update filtered table

        searchbutton.setOnAction(event -> updateTableFilter());

        // Enable column sorting

        SortedList<ToDo> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty()); // Bind comparator to table
        table.setItems(sortedData); // Display sorted and filtered tasks
    }

    // Update available filter values based on selected filter type
    private void updateFilterOptions(String type) {
        filterValue.getItems().clear();
        filterValue.setValue(null);

        if (type == null || "None".equals(type)) {
            filterValue.setDisable(true); // Disable if no filtering
            return;
        }

        filterValue.setDisable(false);

        // Give Options dropdown based on filter type
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

    // Apply search keyword and filters to TableView
    private void updateTableFilter() {
        String keyword = searchfield.getText().toLowerCase();
        String type = (filterType != null) ? filterType.getValue() : "None";
        String value = (filterValue != null) ? filterValue.getValue() : null;

        filteredData.setPredicate(todo -> {
            // Check keyword match in title or description
            boolean matchKeyword = keyword == null || keyword.isEmpty() ||
                    todo.getTitle().toLowerCase().contains(keyword) ||
                    todo.getDescription().toLowerCase().contains(keyword);

            // Check specific filter criteria
            boolean matchCriterion = true;
            if (type != null && value != null && !"None".equals(type)) {
                switch (type) {
                    case "Category":
                        matchCriterion = todo.getCategory().equalsIgnoreCase(value);
                        break;
                    case "Priority":
                        matchCriterion = todo.getPriority() == Integer.parseInt(value);
                        break;
                    case "Status":
                        matchCriterion = "Completed".equals(value) ? todo.isCompleted() : !todo.isCompleted();
                        break;
                }
            }

            // Return true if task matches both keyword and filter criteria
            return matchKeyword && matchCriterion;
        });
    }


    // Edit task

    @FXML
    public void clickingEdit() throws IOException {
        ToDo selectTask = table.getSelectionModel().getSelectedItem();

        if (selectTask == null) { // If no task selected, show warning
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Select a task");
            alert.showAndWait();
            return;
        }

        int masterIndex = allTasks.indexOf(selectTask); // Get index in master list (allTasks)

        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditTask.fxml"));
        Parent root = loader.load();

        EditTaskController controller = loader.getController();
        controller.getTaskData(selectTask, masterIndex);

        Stage stage = new Stage();
        stage.setTitle("Edit Task");
        stage.setScene(new Scene(root));
        stage.show();

        ((Stage) table.getScene().getWindow()).close(); // Close current window
    }


    // Delete task

    @FXML
    public void clickingDelete() throws IOException {
        ToDo selectTask = table.getSelectionModel().getSelectedItem();

        if (selectTask == null) { // If no task selected, show warning
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Select a task");
            alert.setContentText("Please select a task to delete.");
            alert.showAndWait();
            return;
        }

        // Ask for confirmation before deleting
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this task?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            allTasks.remove(selectTask); // Remove from memory
            saveTasks();                 // Save updated list to JSON

            // Inform the user
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Task is deleted!");
            alert.showAndWait();
        }
    }

    // Reset search and filter
    @FXML
    public void resetFilter() {
        searchfield.clear();
        if (filterType != null) filterType.setValue("None");
        if (filterValue != null) filterValue.setValue(null);
    }

    // Show FAQ dialog
    @FXML
    public void handleFAQ() {
        Alert alert = new Alert(AlertType.INFORMATION);
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

    // Show credits dialog
    @FXML
    public void handleCredit() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About Us");
        alert.setHeaderText("Smart ToDo List App");
        alert.setContentText(
                "Version: 1.0\n" +
                        "Created by: Muhammad Izham & Syed Nur Haikal\n" +
                        "Course: CAT201 - Integrated Software Development Workshop\n" +
                        "Universiti Sains Malaysia"
        );
        alert.showAndWait();
    }

    // Return to Main Menu
    public void MainMenu() throws IOException {

        Stage currentStage = (Stage) table.getScene().getWindow();
        currentStage.close(); // Close the current 'View All Tasks' window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));  // Load the Main Menu FXML
        Parent root = loader.load();
        Stage mainStage = new Stage();
        mainStage.setTitle("TO DO LIST");
        mainStage.setScene(new Scene(root));
        mainStage.show(); //Show the Main Menu window
    }

    // Clear all tasks after confirmation
    @FXML
    public void clearAllTasks() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Clear");
        alert.setHeaderText("Clear All Data");
        alert.setContentText("Are you sure you want to delete ALL tasks? This cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            allTasks.clear(); // Clear in-memory list
            saveTasks();      // Save empty list to file
        }
    }


    // Save tasks to JSON

    private void saveTasks() throws IOException {
        Writer writer = new FileWriter(FILE_PATH);
        new GsonBuilder().setPrettyPrinting().create().toJson(allTasks, writer); // Convert list to JSON
        writer.close();
    }


    // Load tasks from JSON

    private List<ToDo> loadCurrentTasks() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>(); // Return empty list if file not found

        Reader reader = new FileReader(file);
        List<ToDo> results = new Gson().fromJson(reader, new TypeToken<List<ToDo>>(){}.getType());
        reader.close();

        return results == null ? new ArrayList<>() : results;
    }
}
