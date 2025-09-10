package ru.common.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    List<Integer> subtaskIDs;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtaskIDs = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        subtaskIDs = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + "," + getDescription();
    }

    public void setSubtaskIDs(List<Integer> subtaskIDs) {
        this.subtaskIDs = new ArrayList<>(subtaskIDs);
    }

    public List<Integer> getSubtaskIDs() {
        return subtaskIDs;
    }

}
