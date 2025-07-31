package ru.common.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    List<Integer> subtaskIDs;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtaskIDs = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        subtaskIDs = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ru.common.model.Epic{" +
                "subtaskIDs=" + subtaskIDs +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public List<Integer> getSubtaskIDs() {
        return subtaskIDs;
    }

}
