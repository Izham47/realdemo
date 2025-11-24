package com.example.demo;


import java.time.LocalDate;

public class ToDo {

    private String title;
    private String description;
    private String dueDate;
    private String category;
    private Integer priority;

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getDueDate() {
        return dueDate;
    }
    public String getCategory() { return category; }
    public Integer getPriority() { return priority; }



    public ToDo (String title, String description,LocalDate dueDate,String  category,Integer priority) {

        this.title = title;
        this.description = description;
        this.dueDate = (dueDate != null) ? dueDate.toString() : "";;
        this.category = category;
        this.priority = priority;

    }


}
