package com.example.demo;

import com.example.demo.ToDo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ViewTaskController  {

    @FXML
    private TableView<ToDo> table;

    @FXML
    private TableColumn<ToDo, String> titlecolumn;

    @FXML
    private TableColumn<ToDo, String> descriptioncolumn;

    @FXML
    private TableColumn<ToDo, String> duedatecolumn;

    @FXML
    private TableColumn<ToDo, String> categorycolumn;

    @FXML
    private TableColumn<ToDo, Integer> prioritycolumn;

    private final String FILE_PATH = "tasks.json";

    @FXML
    public void initialize() {

        titlecolumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptioncolumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        duedatecolumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        categorycolumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        prioritycolumn.setCellValueFactory(new PropertyValueFactory<>("priority"));

        List<ToDo> loadedTasks = loadCurrentTasks();

        ObservableList<ToDo> listForTable = FXCollections.observableArrayList(loadedTasks);
        table.setItems(listForTable);
    }

    private List<ToDo>  loadCurrentTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)){
            List<ToDo> results = new Gson().fromJson(reader, new TypeToken<List<ToDo>>(){}.getType());
            if (results == null) return new ArrayList<>();
            return results;
        }catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}